package org.marc4j.rules;

import java.util.StringTokenizer;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * RequiredDataFields.java created by levenick on May 23, 2014 at 9:21:14 AM
 * Subclass of repeatable.  Fields and subfield may repeat.  There may be more than one 245 field in a record
 * and more than one $a subfields in a particular field...
 * DataFields occur in Conditions
 */
public class RequiredDataFields extends Repeatable {

    protected FieldSpecList fieldList;
    protected char mostRecentSubFieldChar;    // there might be multiple fields in the list, this is the one that matched (since the loop terminates when it matches)

    @Override
    public boolean hasSubfield() {
        return true;
    }

    
    RequiredDataFields(int fieldNumber, String back, String what) {
        this.fieldNumber = fieldNumber;
        fieldList = new FieldSpecList();
        fieldList.add(new FieldSpecifier(fieldNumber, back.charAt(0)));
        setTheConditionDataRequired(new ConditionDataRequired(what));
        addAdditionalFields(back.substring(1));
    }

    private void addAdditionalFields(String s) {
        s = s.replace(",", "|");
        s = chewOffFront(s);
        StringTokenizer st = new StringTokenizer(s, "|");
        while (st.hasMoreTokens()) {
            fieldList.add(new FieldSpecifier(st.nextToken().trim()));
        }

    }

    private String chewOffFront(String s) {
        while (s.length() > 0 && !Character.isDigit(s.charAt(0))) {
            s = s.substring(1);
        }
        return s;
    }

    public static void main(String[] args) {
        RequiredDataFields r = new RequiredDataFields(234, "a", "foo");
        System.out.println("r = " + r);

        RequiredDataFields q = new RequiredDataFields(234, "a| 333$z | 999$a", "bar");
        System.out.println("q = " + q);

        RequiredDataFields s = new RequiredDataFields(234, "a, 123$a, 234$b, 678$c", "all kinds of stuff!");
        System.out.println("s = " + s.toString());

    }

    /**
     * Matches any of the field$subfields in fieldList (there may be more than
     * one!)
     *
     * @param theRecord
     * @return
     */
    public boolean matchesAny(Record theRecord) {
        for (FieldSpecifier nextFieldSpec : fieldList) {
            if (matchesAnyOccuranceInTheRecord(theRecord, nextFieldSpec)) {
                return true;
            }
        }

        return false; // failure!
    }

    /**
     * Tries to match the data from any of the specified subfield in any of the
     * specified field (which is not as simple as it sounds, since either may have duplicates. 
     * There can be duplicates of either!)
     *
     * @param theRecord
     * @param fieldSpec the particular field$subfield we are looking for
     * @return
     */
    private boolean matchesAnyOccuranceInTheRecord(Record theRecord, FieldSpecifier fieldSpec) {
        fieldNumber = fieldSpec.getFieldNumber();
        mostRecentSubFieldChar = fieldSpec.getSubfieldChar();

        int countFieldOccurances = 0;
        for (VariableField nextField : theRecord.getVariableFields(Utils.asTag(fieldNumber))) {
            int countSubfieldOccurances = 0;
            countFieldOccurances++;
            DataField nextDataField = (DataField) nextField;
            for (Subfield nextSubField : nextDataField.getSubfields(mostRecentSubFieldChar)) {
                countSubfieldOccurances++;
                Globals.initRuleDebug("RequiredDataField:: matchesAny -- trying to find "
                        + getTheConditionDataRequired() + " in " + nextDataField.toString());
                String patternRequired = getTheConditionDataRequired().getDataValueRequired();
                if (nextSubField.find(patternRequired)) { // use the built-in find
//                    if (itsACursed2000WithCommasTypeNumber(nextSubField.getData()))
//                        continue;
                    itMatched(countFieldOccurances, countSubfieldOccurances);
                    setDataFromSubfield(nextSubField.getData(), patternRequired);
                    return true;
                }
            }
        }

        return false;  // none of the subfields in any of the field instances matched
    }

    @Override
    public String toStringBrief() {
        String returnMe = fieldList.briefToString() + ": " + getTheConditionDataRequired().toString();

        return returnMe;
    }

    public String toString() {
        String returnMe = "RequiredDataField: fieldNumber(s)=(" + fieldList.briefToString() + ") " + getTheConditionDataRequired().toString();

        return returnMe;
    }

    @Override
    public char getMostRecentSubFieldChar() {
        return mostRecentSubFieldChar;
    }

    @Override
    public int getInputColumn() {
        throw new UnsupportedOperationException("This should never happen... no columns");
    }

    @Override
    public int getInputColumnEnd() {
        throw new UnsupportedOperationException("This should never happen... no columns");
    }

//    private void setDataFromSubfield(String data) {
//        Globals.setMatchedDataFromSubfield(data);
//    }

    /**
     * Sets the most recent condition match in Globals so we can recover it in the Action part (immediately from transform!)
     * @param data
     * @param patternRequired 
     */
    private void setDataFromSubfield(String data, String patternRequired) {
        Globals.setMatchedDataFromSubfield(Utils.applyPattern(data, patternRequired));
    }

//    private boolean itsACursed2000WithCommasTypeNumber(String s) {
//        return Utils.itsACursed2000WithCommasTypeNumber(s);
//    }

}
