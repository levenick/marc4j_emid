package org.marc4j.rules;

import java.util.StringTokenizer;

/**
 * Header.java created by levenick on May 14, 2014 at 10:29:48 AM The
 * translation of the first line of a list of rule specs
 */
class Header {

    private String inputFileName;
    private int numberConditions;
    private int numberValues;
    private ActionList listOfOutputs;
    private int targetField;
    private boolean onlyOneMatch = false;
    /**
     * the field we will be writing to
     */
    private String errorString;

    Header(MyReader mr) {
        inputFileName = Globals.getRuleFileName();
        if (!mr.hasMoreData()) {
            System.out.println("Empty file?? " + mr.toString());
            Globals.fatalError("Header: no data(!!) in " + inputFileName);
        }

        String s = mr.giveMeTheNextLine();
        if (!validHeader(s)) {
            Globals.panic("invalid header!!" + s + errorString);
        }
        setNumberValues(s.split(",").length);
        listOfOutputs = generateOutputList(s);
    }

    Header(MyReader mr, String path) {
        this(mr);
        inputFileName = path;
    }

    private int figureNumConditions(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        String pos = st.nextToken();
        int count = 0;

        if (!pos.contains("position")) {
            Globals.fatalError("first line of >" + inputFileName + "< does *not* start with \"position\"");
        }

        while (pos.contains("position")) {
            count++;
            st.nextToken(); // skip the "condition"
            pos = st.nextToken();
        }

        return count;
    }

    private ActionList generateOutputList(String s) {
        ActionList returnMe = new ActionList();

        StringTokenizer st = new StringTokenizer(s, ",");
        skipConditionHeads(st);

        while (st.hasMoreTokens()) {  // grab the action fields
            returnMe.add(new Action(st));
        }

        return returnMe;
    }

    private void skipConditionHeads(StringTokenizer st) {
        for (int i = 0; i < this.numberConditions * 2; i++) {
            st.nextToken();  // skip the conditions
        }
    }

    /**
     * first figure the number of conditions then see if all the 96x fields are
     * the same if so, set the target field to that
     *
     * @param s
     * @return
     */
    private boolean validHeader(String s) {
        numberConditions = figureNumConditions(s);
        StringTokenizer st = new StringTokenizer(s, ",");

        skipConditionHeads(st);
        if (!st.hasMoreTokens()) {
            errorString = "Nothing after condition/positions!!";
            return false;
        }

        targetField = Utils.extractFieldFromParens(st.nextToken());
        while (st.hasMoreTokens()) {
            int nextField = Utils.extractFieldFromParens(st.nextToken());
            if (nextField == 999) {  // kludge for only one
                onlyOneMatch = true;
            } else if (nextField != targetField) {
                errorString = "Different field numbers in header! targetField=" + targetField + " found " + nextField + " as well.";
                return false;
            }
        } // while

        return true; // yay!
    }

    public String toString() {
        String returnMe = "Header from: " + this.inputFileName + " num=" + numberConditions + "\n";

        returnMe += listOfOutputs.toString();

        return returnMe;
    }

//    public static void main(String[] args) {
//        String s = "position1,condition1,position2,condition2,position3,condition3,position4,condition4,Duration (961$f),Duration type ($961g),Duration qualifier (961$i),Duration validity ($961j)";
//        Header aHeader = new Header(s);
//        System.out.println("aHeader = " + aHeader);
//    }
    public int getNumberConditions() {
        return numberConditions;
    }

    public ActionList getListOfOutputs() {
        return listOfOutputs;
    }

    public int getTargetField() {
        return targetField;
    }

    String getInputFileName() {
        return inputFileName;
    }

    private void setNumberValues(int n) {
        numberValues = n;
    }

    public int getNumberValues() {
        return numberValues;
    }

    String toStringBrief() {
        String returnMe = "Header: ";

        returnMe += listOfOutputs.toStringHeader();

        return returnMe;
    }

    int getLastHeaderField() {
        throw new UnsupportedOperationException("Header:getLastHeaderField not written(!!) (yet).");
    }

    boolean getOnlyOneMatch() {
return onlyOneMatch;    }
}
