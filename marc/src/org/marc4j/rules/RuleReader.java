package org.marc4j.rules;

import java.io.InputStream;

/**
 * RuleReader.java created by levenick on May 14, 2014 at 9:35:50 AM
 */
public class RuleReader {
    RuleSet list;
    
    public RuleReader (InputStream input) {
        list = new RuleSet(input);
        Globals.initDebug("and the rule list is " + list);
    }

    public String toString() {
        String returnMe = "I am a RuleReader, please fill in my variables so I can be debugged.";

        return returnMe;
    }
}
