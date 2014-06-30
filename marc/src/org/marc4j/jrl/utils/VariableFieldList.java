package org.marc4j.jrl.utils;

import java.util.ArrayList;
import org.marc4j.marc.VariableField;

/**
 * VariableFieldList.java created by levenick on May 17, 2014 at 12:18:49 PM
 */
public class VariableFieldList extends ArrayList<VariableField>{

    public String toString() {
        String returnMe = "VariableFieldList:";
        
        for (VariableField nextField: this) {
            returnMe += "\n\t" + nextField.toString();
        }

        return returnMe;
    }
}
