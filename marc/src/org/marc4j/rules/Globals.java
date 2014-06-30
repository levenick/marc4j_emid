package org.marc4j.rules;

/**
 * Globals.java created by levenick on May 14, 2014 at 12:15:05 PM Things that
 * need to be accessed from anywhere!
 */
public class Globals {

    private static String ruleFileName = "nothing";
    private static int time;
    private static String recordDataValue;
    private static boolean rangeWasDigits;
    private static Rule currentRule;
    private static String matchedDataFromSubField;
    private static boolean showingFailure = false;
    private static boolean ruleDebug = false;
    private static boolean matchDebug = false;
    private static boolean initDebug = true;
    public static char SPLIT_CHAR = '#';
    private static boolean debugDouble = false;
    //private static int mostRecentControlFieldNum;
    private static int mostRecentControlFieldCol;
    private static int partNumber;
    private static int repetitions;
    private static int usageCounter;
    private static int applicationCount;

    public static void setTime(int t) {
        time = t*60;
    }

    public static void setRuleFileName(String s) {
        ruleFileName = s;
    }

    public static String getRuleFileName() {
        return ruleFileName;
    }

    public static boolean initRuleDebug() {
        return ruleDebug;
    }

    public static boolean initMatchDebug() {
        return matchDebug;
    }

    public static boolean initDebug() {
        return initDebug;
    }

    public static void initRuleDebug(String s) {
        if (initRuleDebug()) {
            System.out.println("...init Rule debugging...\n " + s);
        }
    }

    public static void panic(String s) {
        dumpStack("Panicking! Complaint = " + s + "\ncurrentRule = " + currentRule);
        throw new UnsupportedOperationException("Panic!! " + s);
    }

    public static void dumpStack(String s) {
        String note = s;
        for (Object o : Thread.currentThread().getStackTrace()) {
            note += "\n\t" + o.toString();
        }
        new GenericDisplayFrame("oops!", note);
    }

    public static void main(String[] args) {
        dumpStack("just checking...");
    }

    public static void fatalError(String s) {
        dumpStack("fatalError" + s);
        throw new UnsupportedOperationException("Fatal error! " + s);
    }

    public static void initDebug(String s) {
        if (initDebug()) {
            System.out.println("...init  debugging... " + s);
        }
    }

    public static void initMatchDebug(String s) {
        if (initMatchDebug()) {
            System.out.println("...init  debugging... " + s);
        }
    }

    public static int getTime() {
        return time;
    }

    static void setRecordDataValue(String r) {
        recordDataValue = r;
    }

    public static String getRecordDataValue() {
        return recordDataValue;
    }

    public static void setRangeWasDigits(boolean digits) {
        rangeWasDigits = digits;
    }

    public static void setCurrentRule(Rule r) {
        currentRule = r;
        //System.out.println("this is never used??"); yes it is!!
    }

    public static void setMatchedDataFromSubfield(String data) {
        matchedDataFromSubField = data;
    }

    public static String getMatchedDataFromSubfield() {
        return matchedDataFromSubField;
    }

    static void toggleShowFailed() {
        showingFailure = !showingFailure;
    }

    static boolean getShowingFailure() {
        return showingFailure;
    }

    static boolean getRuleDebug() {
        return ruleDebug;
    }

    static void toggleMatchDebug() {
        matchDebug = !matchDebug;
    }

    static boolean getMatchDebug() {
        return matchDebug;
    }

    static void toggleRuleDebug() {
        ruleDebug = !ruleDebug;
    }

    static void toggleInitDebug() {
        initDebug = !initDebug;
    }

    static boolean getInitDebug() {
        return initDebug;
    }

    static void setInitDebug(boolean b) {
        initDebug = b;
    }

    public static void advice(String s) {
        matchAdvice("advice... ", s);
    }

    public static void matchAdvice(String title, String s) {
        new GenericDisplayFrame(title, s);
    }

    static boolean writeDebug = false;

    static void writeDebug(String s) {
        if (writeDebug) {
            System.out.println("debugging write: " + s);
        }
    }

    static void debugDouble(String s) {
        if (debugDouble) {
            System.out.println(s);
        }
    }

    /**
     * To be able to write the $c field
     *
     * @return
     */
    static int getMostRecentControlFieldCol() {
        return mostRecentControlFieldCol;
    }

    static void setMostRecentControlFieldCol(int inputColumn) {
        mostRecentControlFieldCol = inputColumn;
    }

    /**
     * To hold the part number between the iteration in Rule and the output in
     * Action
     *
     * @return
     */
    static int getPartNumber() {
        return partNumber;
    }

    static void setPartNumber(int i) {
        partNumber = i;
    }

    static void setRepetitions(int reps) {
        repetitions = reps;
    }

    static int getRepetitions() {
        return repetitions;
    }

    static void incCounter() {
        usageCounter++;
    }

    static void resetCounter() {
        usageCounter=0;
    }

    static String getCounter() {
        return ""+usageCounter;
    }

    static void addToApplicationCount(int n) {
        applicationCount += n;
    }

    static int getApplicationCount() {
        return applicationCount;
    }

}
