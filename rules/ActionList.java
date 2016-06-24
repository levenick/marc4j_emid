package org.marc4j.rules;

import java.util.ArrayList;

/**
 * ActionList.java created by levenick on May 14, 2014 at 9:50:55 AM
 */
class ActionList extends ArrayList<Action> {

    public String toString() {
        String returnMe = "\n\tActionList:";

        for (Action nextAction : this) {
            returnMe += "\n\t\t" + nextAction.toString();
        }

        return returnMe;
    }

    public String toStringBrief() {
        String returnMe = "";

        for (Action nextAction : this) {

            returnMe += "\n\t\t" + nextAction.toStringBrief();
        }

        return returnMe;
    }
    public String toStringHeader() {
        String returnMe = "";

        int count = 0;
        for (Action nextAction : this) {
            if (count > 0) {
                returnMe += ", ";
            }
            count++;
            returnMe += nextAction.toStringHeader();
        }

        return returnMe;
    }
    
    
}
