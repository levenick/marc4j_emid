package org.marc4j.rules;

import java.util.ArrayList;

/**
 * RuleSetList.java created by levenick on Jun 23, 2014 at 5:06:05 PM
 */
class RuleSetList extends ArrayList<RuleSet> {

    RuleSetList() {
        
    }
    
    public String toString() {
        String returnMe = "RuleSetList\n";

        for (RuleSet nextRS: this)
            returnMe += "\n\t" + nextRS.getPath();
        
        return returnMe;
    }
    
    public String toStringResults() {
        String returnMe = "Rule Applications\n";

        for (RuleSet nextRS: this)
            returnMe += "\n  " + nextRS.getCounts();
        
        return returnMe;
    }
}
