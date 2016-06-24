package org.marc4j.rules;

import java.util.ArrayList;

/**
 * ConditionList.java created by levenick on May 14, 2014 at 9:50:48 AM
 */
class ConditionList extends ArrayList<Condition> {

    public String toString() {
        String returnMe = "\n\tConditionList:";
        
        for (Condition nextCondition: this) {
            returnMe += "\n\t\t" + nextCondition.toString();
        }

        return returnMe;
    }
    
    public String toStringBrief() {
        String returnMe = " ";
        
        for (Condition nextCondition: this) {
            returnMe += "\n\t\t" + nextCondition.toStringBrief();
        }

        return returnMe;
    }
}
