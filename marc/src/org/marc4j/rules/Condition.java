package org.marc4j.rules;

import org.marc4j.marc.Record;

/**
 * Condition.java created by levenick on May 14, 2014 at 9:52:15 AM The
 * superclass of the three kinds of Fields we might be looking at: 1) leader 2)
 * Control Field 3) Other, i.e. 11-999
 */
public abstract class Condition {
//    private static char[] backSlashMe = {']', '[', '(', ')'};
//    private static String fixOne(String what, char ch) {
//        return what.replace(""+ch, "\\" + ch);
//    }
//    
//    private static String backSlash(String what) {
//        for (char ch: backSlashMe)
//            what = fixOne(what, ch);
//        
//        return what;
//    }

    public static void main(String[] args) {
        String s = "[d*]";

        //System.out.println("...  " + backSlash(s));
    }

        /**
     * the Condition matches the theRecord if *any* field with this number
     * matches
     *
     * @param theRecord
     * @return
     */
public abstract boolean matches(Record theRecord);

    public abstract int getFieldNumber();

    public abstract int getRepeatField();

    public abstract char getMostRecentSubFieldChar();

    public abstract int getRepeatSubfield();

    public abstract boolean hasSubfield();

    private ConditionDataRequired theConditionDataRequired;    // value it must match

    /**
     * Constructs a Condition If the first char is an 'L' it is leader,
     * otherwise will be a 3-digit fieldNumber designator Creates one of three:
     * RequiredLeader, RequiredControlField, or RequiredDataFields depending on
     * what is required. The data required is one of two: ConditionCharRequired,
     * ConditionStringRequired
     *
     * @param where - fieldNumber and column index(es)
     * @param what - what value is required
     */
    public static Condition instantiate(String where, String what) {
        Condition returnMe;

        String front = where.substring(0, 3);
        String back = where.substring(4);

        if (front.charAt(0) == 'L') {
            returnMe = new RequiredLeader(back, what);
        } else {
            int fieldNumber = Integer.parseInt(front);
            if (fieldNumber < 10) {
                returnMe = new RequiredControlField(fieldNumber, back, what);
            } else {
                returnMe = new RequiredDataFields(fieldNumber, back, what);

            }
        }
        Globals.initRuleDebug("*^*^*&^* I constructed a condition... " + returnMe.toString());

        return returnMe;
    }

    protected boolean matchesSingleColumn(String s, int inputColumn, char inputCharValue) {
        if (s.charAt(inputColumn) == inputCharValue) {
            Globals.initMatchDebug("\n============\nmatched column " + inputColumn + " char=" + inputCharValue);
            return true;
        } else {
            return false;
        }
    }

    public abstract String toStringBrief();

//    boolean matches(String s) {
//        if (inputColumnEnd == 0) {
//            if (s.charAt(inputColumn) == inputCharValue) {
//                Globals.initMatchDebug("\n============\nmatched column " + inputColumn + " char=" + inputCharValue);
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return matchesRange(s);  // it's a range! 
//        }
//    }
//
//        private boolean matchesDataField(Record theRecord) {
//        i
//
    /**
     * Tries to match the data from the control field
     *
     * @param theRecord
     * @return
     */
//    private boolean matchesControlField(Record theRecord) {
//        ControlField theControlField = (ControlField) theRecord.getVariableField(asTag(fieldNumber));
//        if (theControlField == null) {
//            return false;
//        }
//        return matches(theControlField.getData());
//    }
//
//
//
    /**
     * Fixed length columns in MARC, so if it's 18 or 71, the length=2, 
     * otherwise there is a range, like 18-20
     * @param back
     * @return 
     */
    protected boolean onlyOneColumn(String back) {
        return back.length() == 2;
    }
//
//    public String toString() {
//        String returnMe = "I am a Condition: ";
//        returnMe += "\tfield=" + getFieldNumber();
//        returnMe += "\tsubField=" + getMostRecentSubFieldChar();
//        returnMe += "\tleader=" + getLeader();
//        returnMe += "\tinputCharValue=" + getMostRecentSubFieldChar();
//        returnMe += "\tinputColumn=" + getInputColumn();
//        returnMe += "\tinputColumnEnd=" + getInputColumnEnd();
//        returnMe += "\tdataValueRequired=" + getDataValueRequired();
//        return returnMe;
//    } // toString()
//
//    public String toStringBrief() {
//        String returnMe = "Cond: ";
//        returnMe += " field=" + getFieldNumber();
//        returnMe += " subField=" + getMostRecentSubFieldChar();
//        returnMe += " leader=" + getLeader();
//        returnMe += " inputCharValue=" + getMostRecentSubFieldChar();
//        returnMe += " inputColumn=" + getInputColumn();
//        returnMe += " inputColumnEnd=" + getInputColumnEnd();
//        returnMe += " dataValueRequired=" + getDataValueRequired();
//        return returnMe;
//    } // toString()

    public ConditionDataRequired getTheConditionDataRequired() {
        return theConditionDataRequired;
    }

    public void setTheConditionDataRequired(ConditionDataRequired cdr) {
        theConditionDataRequired = cdr;
    }

}  // Condition

