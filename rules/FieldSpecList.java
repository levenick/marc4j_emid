package org.marc4j.rules;

import java.util.ArrayList;

/**
 * FieldSpecList.java created by levenick on May 31, 2014 at 12:50:31 PM
 */
public class FieldSpecList extends ArrayList<FieldSpecifier> {

    public String toString() {
        String returnMe = "FieldSpecList:";

        for (FieldSpecifier nextFs : this) {
            returnMe += "\n\t" + nextFs.toString();
        }

        return returnMe;
    }

    String briefToString() {
        String returnMe = "";
        int count = 0;
        for (FieldSpecifier nextFs : this) {
            if (count > 0) {
                returnMe += ", ";
            }
            count++;
            returnMe += nextFs.briefToString();
        }

        return returnMe;
    }
}
