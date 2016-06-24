package org.marc4j.rules;

import org.marc4j.marc.Record;

/**
 * Repeatable.java created by levenick on May 23, 2014 at 9:14:47 AM
 * The superclass of RequiredControlField and RequiredDataFields
 */
public abstract class Repeatable extends Condition {

    protected int fieldNumber;

    private int repeatSubfield;
    private int repeatField;
    
    public abstract int getInputColumn();
    public abstract int getInputColumnEnd();

    /**
     * the Condition matches the theRecord if *any* field with this number
     * matches
     *
     * @param theRecord
     * @return
     */
    public boolean matches(Record theRecord) {
        return matchesAny(theRecord);
    }

    public abstract boolean matchesAny(Record theRecord);

    protected void itMatched(int fieldCount) {
        Globals.initMatchDebug("itMatched: fieldCount=" + fieldCount);
        repeatField = fieldCount;
    }

    protected void itMatched(int fieldCount, int subfieldCount) {
        Globals.initMatchDebug("itMatched: fieldCount=" + fieldCount + " subfieldCount=" + subfieldCount);
        repeatField = fieldCount;
        repeatSubfield = subfieldCount;
    }

 
    @Override
    public int getRepeatField() {
        return repeatField;
    }

    @Override
    public int getRepeatSubfield() {
        return repeatSubfield;
    }

    @Override
    public int getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    @Override
    public abstract char getMostRecentSubFieldChar();
    @Override
    public abstract boolean hasSubfield();

}
