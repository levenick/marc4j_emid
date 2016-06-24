package org.marc4j.rules;

import org.marc4j.marc.Record;

/**
 * RequiredLeader.java created by levenick on May 23, 2014 at 9:14:24 AM
 */
public class RequiredLeader extends Condition {
    
    protected int inputColumn;
    
    RequiredLeader(String back, String what) {
        if (onlyOneColumn(back)) {
            inputColumn = Integer.parseInt(back);
            setTheConditionDataRequired(new ConditionDataRequired(""+what.charAt(0)));
        } else {
            Globals.panic("RequiredLeader:: found more than one column; I thought LDR only used single columns!!");
        }
        
    }
    
    public int getInputColumn() {
        return inputColumn;
    }
    
    public void setInputColumn(int inputColumn) {
        this.inputColumn = inputColumn;
    }
    
    @Override
    public String toStringBrief() {
        String returnMe = " LDR: " + getTheConditionDataRequired().toString();

        return returnMe;    
    }


    public String toString() {
        String returnMe = "RequiredLeader:";
        
        returnMe += " column=" + inputColumn + " ";
        returnMe += getTheConditionDataRequired().toString();
        
        return returnMe;
    }
    
    public static void main(String[] args) {
        RequiredLeader foo = new RequiredLeader("06-08", "k");
        System.out.println("foo = " + foo);
    }

    @Override
    public boolean matches(Record theRecord) {
        char charValue = getTheConditionDataRequired().getDataValueRequired().charAt(0);
        return matchesSingleColumn(theRecord.getLeader().toString(), getInputColumn(), charValue);
    }

    @Override
    public int getFieldNumber() {
        return 0;
    }

    @Override
    public int getRepeatField() {
        return 1;
    }

    @Override
    public char getMostRecentSubFieldChar() {
        return '?';
    }

    @Override
    public int getRepeatSubfield() {
        return 0;
    }

    @Override
    public boolean hasSubfield() {
        return false;
    }

}
