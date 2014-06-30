package org.marc4j.rules;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * RuleList.java created by levenick on May 14, 2014 at 9:46:20 AM A set of
 * rules, including: a Header with the format (derived from the first line of
 * the input a list of Rules in the form Condition:Action to tranform records if
 * the Condition matches, apply the action to transform
 *
 * Here's the Duration.csv file as an example: *
 * position1,condition1,position2,condition2,position3,condition3,position4,condition4,Duration
 * (961$f),Duration type ($961g),Duration qualifier (961$i),Duration validity
 * ($961j) LDR/06,g,008/33,m,008/18-20,001-999,,,PxxHxxMxxS,total,,correct
 * LDR/06,g,008/33,v,008/18-20,001-999,,,PxxHxxMxxS,total,,correct
 * LDR/06,g,008/33,m,008/18-20,000,,,P16H39M,total,over,correct
 * LDR/06,g,008/33,v,008/18-20,000,,,P16H39M,total,over,correct
 * LDR/06,g,008/33,m,008/18-20,!= 000-999,,,unspecified,total,,correct
 * LDR/06,g,008/33,v,008/18-20,!= 000-999,,,unspecified,total,,correct
 * LDR/06,m,006/00,g,006/16,m,006/01-03,001-999,PxxHxxMxxS,total,,correct
 * LDR/06,m,006/00,g,006/16,n,006/01-03,001-999,PxxHxxMxxS,total,,correct
 * LDR/06,m,006/00,g,006/16,m,006/01-03,000,P16H39M,total,over,correct
 * LDR/06,m,006/00,g,006/16,v,006/01-03,000,P16H39M,total,over,correct
 * LDR/06,m,006/00,g,006/16,m,006/01-03,!= 000-999,unspecified,total,,correct
 * LDR/06,m,006/00,g,006/16,v,006/01-03,!= 000-999,unspecified,total,,correct
 */
public class RuleSet extends ArrayList<Rule> {
    
    private boolean onlyOneMatch = false;
    Header header;
    private String path;
    
    public RuleSet(String path) {
        Rule.resetSerial();
        this.path = path;
        try {
            doEverything(new BufferedInputStream(new FileInputStream(path)));
        } catch (Exception e) {
            Globals.fatalError("RuleSet:: constructor -- oops! " + path + "e=" + e);
        }
    }
    
    public RuleSet(InputStream input) {
        doEverything(input);
    }
    
    private void doEverything(InputStream input) {
        MyReader mr = new MyReader(input);
        header = new Header(mr, path);
        Globals.initRuleDebug("the header is: " + header);
        Rule.setRuleSet(this);
        setOnlyOneMatch(header);
        
        while (mr.hasMoreData()) {
            String s = mr.giveMeTheNextLine();
            s = Utils.tabsForCommas(s);
            add(new Rule(header, s));
        }
    }
    
    public String toString() {
        String returnMe = "RuleList:" + header.toString();
        
        for (Rule nextRule : this) {
            returnMe += "\n\t" + nextRule.toString();
        }
        
        return returnMe;
    }
    
    public String toStringBrief() {
        String returnMe = header.toStringBrief();
        
        for (Rule nextRule : this) {
            returnMe += "\n\t" + nextRule.toStringBrief();
        }
        
        return returnMe;
    }
    
    public int getTargetField() {
        return header.getTargetField();
    }
    
    public String getInputFileName() {
        return header.getInputFileName();
    }
    
    public void resetUsageCounts() {
        for (Rule nextRule : this) {
            nextRule.resetUsages();
        }
    }
    
    String usages() {
        String returnMe = "";
        int sum = 0;
        
        for (Rule nextRule : this) {
            int n = nextRule.getUsages();
            sum += n;
            returnMe += "\n\tR" + nextRule.getSerialNumber() + " " + n;
        }
        
        returnMe += "\ntotal=" + sum;
        
        return returnMe;
    }

    /**
     * Sets the one only switch -- if it is true, then only the first rule that
     * matches is applied if false, all matching rules fire.
     *
     * @param header - if the last field of the header is 999o, then it sets it
     * to true... kludge!!
     */
    private void setOnlyOneMatch(Header header) {
        onlyOneMatch = header.getOnlyOneMatch();
    }
    
    boolean getOnlyOneMatch() {
        return onlyOneMatch;
    }
    
    String getPath() {
        return path;
    }
    
    String getCounts() {
        String returnMe = "Ruleset:" + this.getInputFileName().substring(getInputFileName().lastIndexOf(File.separatorChar));
        
        for (Rule nextRule : this) {
            int n = nextRule.getUsages();
            if (n > 0) {
                returnMe += ", R" + nextRule.getSerialNumber() + " " + n;
                Globals.addToApplicationCount(n);
            }
        }
        
        return returnMe;
    }
    
}
