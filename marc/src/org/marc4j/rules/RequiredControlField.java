package org.marc4j.rules;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * RequiredControlField.java created by levenick on May 23, 2014 at 9:20:24 AM
 * Subclass of Repeatable because there may be multiple instances of control fields.
 */
public class RequiredControlField extends Repeatable {

    protected int inputColumn;
    protected int inputColumnEnd;  // if it's a range, the last input column

    public RequiredControlField(int fieldNumber, String back, String what) {
        this.fieldNumber = fieldNumber;
        if (onlyOneColumn(back)) {
            inputColumn = Integer.parseInt(back);
            char whatChar = what.charAt(0);
            if (whatChar == '\\') {
                whatChar = ' ';
            }
            setTheConditionDataRequired(new ConditionDataRequired("" + whatChar));
        } else {
            grabTheColumns(back);
            setTheConditionDataRequired(new ConditionDataRequired(what));
        }
    }

    public boolean matchesAny(Record theRecord) {
        int countFieldOccurances = 0;
        for (VariableField nextField : theRecord.getVariableFields(Utils.asTag(fieldNumber))) {
            countFieldOccurances++;
            ControlField nextControlField = (ControlField) nextField;

            Globals.initRuleDebug("RequiredControlField:: matchesAny -- trying to find "
                    + getTheConditionDataRequired() + " in " + nextControlField.toString());
            if (matchesThisOne(nextControlField)) {
                if (countFieldOccurances == 2) {
                    if (itsNotTheSameFiretruckingControlField(countFieldOccurances)) {
                        continue;
                    }
                }
                itMatched(countFieldOccurances);
                Globals.setMostRecentControlFieldCol(inputColumn);
                return true;
            }
        }

        return false;  // none of the subfields in any of the field instances matched
    }

    /**
     * This fixes the problem where there were two copies of 007 
     * Field 007 crcna|||||||| 
     * Field 007 vz mzazu| 
     * and the v from the second combined with
     * the r from the first... to get video reel!!
     *
     * @param countFieldOccurances
     * @return
     */
    private boolean itsNotTheSameFiretruckingControlField(int countFieldOccurances) {
        return getRepeatField() != countFieldOccurances;
    }

    private void grabTheColumns(String back) {
        inputColumn = gimmeeANumber(back, 0, 2);
        inputColumnEnd = gimmeeANumber(back, 3, 2);
    }

    private int gimmeeANumber(String s, int start, int length) {
        String fragment = s.substring(start, start + length);
        return Integer.parseInt(fragment);
    }

    public int getInputColumnEnd() {
        return inputColumnEnd;
    }

    public void setInputColumnEnd(int inputColumnEnd) {
        this.inputColumnEnd = inputColumnEnd;
    }

    @Override
    public boolean hasSubfield() {
        return false;
    }

    public static void main(String[] args) {
        RequiredControlField foo = new RequiredControlField(8, "06", "m");
        System.out.println("foo = " + foo);
        foo = new RequiredControlField(8, "18-20", "001-999");
        System.out.println("foo = " + foo);

        RequiredDataFields bar = new RequiredDataFields(345, "a", "videoThang");
        System.out.println("bar = " + bar);
    }

    @Override
    public char getMostRecentSubFieldChar() {
        Globals.panic("RequiredControlField:: getInputCharValue -- should never happen!!");
        return '?';
    }

    @Override
    public int getInputColumn() {
        return inputColumn;
    }

    private boolean matchesThisOne(ControlField nextControlField) {
        if (getInputColumnEnd() == 0) {
            char needThisChar = getTheConditionDataRequired().getDataValueRequired().charAt(0);
            String nextData = nextControlField.getData();
            if (getInputColumn() >= nextData.length()) {
                return false;
            }
            char foundThisChar = nextData.charAt(getInputColumn());
            return needThisChar == foundThisChar;
        } else {
            return matchesRange(nextControlField.getData());
        }
    }

    boolean matchesRange(String s) {
        String recordDataValue = Utils.columnRangeString(s, getInputColumn(), getInputColumnEnd());
        recordDataValue = recordDataValue.trim();  // trim trailing spaces!
        Globals.setRecordDataValue(recordDataValue);    // record it in Globals for the Action

        if (getTheConditionDataRequired().getDataValueRequired().equals("000")) {
            return recordDataValue.equals("000");
        } else {
            if (getTheConditionDataRequired().getDataValueRequired().equals("001-999")) {
                if (recordDataValue.length() > 0 && Utils.allDigits(recordDataValue)) {
                    Globals.setTime(Integer.parseInt(recordDataValue));
                    return Utils.in001_999(recordDataValue);
                } else {
                    return false;
                }
            } else if (getTheConditionDataRequired().getDataValueRequired().equals("!= 000-999")) {
                return !Utils.allDigits(recordDataValue);
            } else {
                Globals.panic("Condition::matchesRange -- not 000, or 001-999, or != 000-999!! Time to write code!!");
                return false;
            }
        }
    }

    @Override
    public String toStringBrief() {
        String returnMe = " " + Utils.asTag(fieldNumber) + ": " + getTheConditionDataRequired().toString();;

        return returnMe;
    }

    public String toString() {
        String returnMe = "RequiredControlField: " + Utils.asTag(fieldNumber);

        returnMe += " column=" + inputColumn;
        if (inputColumnEnd != 0) {
            returnMe += "-" + inputColumnEnd;
        }

        returnMe += " value required=" + getTheConditionDataRequired().toString();

        return returnMe;
    }

}
