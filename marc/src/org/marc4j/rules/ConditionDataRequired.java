package org.marc4j.rules;

/**
 * ConditionDataRequired.java created by levenick on May 23, 2014 at 9:27:51 AM
 */
public class ConditionDataRequired {

    private String dataValueRequired;

    public ConditionDataRequired(String s) {
        dataValueRequired = s;
    }

    public String getDataValueRequired() {
        return dataValueRequired;
    }

    public void setDataValueRequired(String dataValueRequired) {
        this.dataValueRequired = dataValueRequired;
    }

    public String toString() {
        String returnMe = "\"" + dataValueRequired + "\"";

        return returnMe;
    }
}
