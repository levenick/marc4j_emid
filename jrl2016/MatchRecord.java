package jrl2016;

import static jrl2016.ProcessorGlobals.PART_REGEX;

/*Gah!!  Read this! Jeez!!
 * Two different actions
 *  for parts
 *      the matching part is the regex which should be deleted from the original
 *  for titles and stuff
 *      the matching part precedes or follows the pattern and both should be deleted
 * 
 * 
 * @author levenick May 11, 2016 1:01:51 PM
 */
public class MatchRecord {

    static final boolean FIRST_RUN = false;
    static final boolean FIRST_MAIN_RUN = false;
    static final boolean FIRST_PARTS_RUN = false;
    static final boolean DEBUG_TRIM_TRAILING = false;
    static final boolean DEBUG_TRIM_TRAILING_ALPHA = false;

//    private static void firstRun(String s) {
//        if (FIRST_RUN) {
//            System.out.println("First run: " + s);
//        }
//    }

    protected String original;
    protected String regex;
    protected String whatMatched;
    protected String leftOfMatch;
    protected String rightOfMatch;
    private int end;
    private int start;

    public MatchRecord() {
    }   //empty default constructor

    public MatchRecord(String original, String regex) {   //initializing constructor
        this();   // invoke the default constructor
        this.original = original;
        this.regex = regex;
    }

    public String getOriginal() {
        return original;
    }

    public String getRegex() {
        return regex;
    }

    public String getWhatMatched() {
        return whatMatched;
    }

    public String getLeftOfMatch() {
        return leftOfMatch;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setWhatMatched(String whatMatched) {
        this.whatMatched = whatMatched;
    }

    public void setLeftOfMatch(String trimmed) {
        this.leftOfMatch = trimmed;
    }

    public String toString() {
        String returnMe = "I am a MatcherRecord: ";
        returnMe += "\n\toriginal=" + getOriginal();
        returnMe += "\n\tregex=" + getRegex();
        returnMe += "\n\twhatMatched=" + getWhatMatched();
        returnMe += "\n\tleftOf=" + getLeftOfMatch();
        returnMe += "\n\trightOf=" + rightOfMatch;
        return returnMe;
    } // toString()

    public static void main(String[] args) {
        splitMain();
        doStuffWithParts();
        System.out.println("done");
    }

    static void splitMain() {
        String input = "pt. 1. Social skills interaction -- pt. 2. Reading strategies -- pt. 3. Cooperative learning -- pt. 4. Self management.";
        MatchRecord theMatcherRecord = new MatchRecord(input, "--|---|- -");
        firstMainRun("theMatcherRecord = " + theMatcherRecord);

        while (MyMatcher.match(theMatcherRecord)) {
            theMatcherRecord.splitUsingRegex();
            create935Main(theMatcherRecord.getLeftOfMatch());
        }
        create935Main(theMatcherRecord.getOriginal());
        firstMainRun("...and... finished, what's leftover?  ==>" + theMatcherRecord.getOriginal());
    }

    static void create935Main(String mt) {
        System.out.println("CREATE 935 WITH THE STRING==>" + mt + "<==");
    }

    void splitUsingRegex() {
        leftOfMatch = original.substring(0, start);
        whatMatched = original.substring(start, end);
        rightOfMatch = original.substring(end);
        original = rightOfMatch.trim();
    }

    /**
     * Given what matched some big horrible partpart regex for parts at the
     * beginning trim the trailing punctuation (if there is any)
     *
     * @return
     */
    void trimTrailingPunct() {
        String trailingRegex = ProcessorGlobals.getTrailingPunctuation();

//        debugTrimTrailing("trimming the trailing punctuation: whatMatched=" + this.getWhatMatched() + " trailing regex=" + trailingRegex);

        String s = getWhatMatched();
        if (s.substring(s.length() - 1).matches(trailingRegex)) {
            setWhatMatched(s.substring(0, s.length() - 1));  // was s.length()-1!! and now it is again... but why did I change it??
//            debugTrimTrailing("matched! returning ==>" + getWhatMatched() + "<==");
        } 
//        else {
//            debugTrimTrailing("no match");
//        }
    }

    /**
     * Er... if there is a capital letter at the end of the prefix (due to something I don't understand about the regexes,
     * move it back to the right of the prefix where it belongs.
     */
    void moveTrailingAlpha() {
        String s = getWhatMatched();
        if (DEBUG_TRIM_TRAILING_ALPHA) {
            System.out.println("alphatrim! starting ==>" + s + "<==" + " and rightof=" + getRightOfMatch());
        }

        if (s.substring(s.length() - 1).matches("[A-Z]")) {
            this.setOriginal(s.substring(s.length() - 1) + this.getOriginal());
            setWhatMatched(s.substring(0, s.length() - 1).trim());
            if (DEBUG_TRIM_TRAILING_ALPHA) {
                System.out.println("alphatrim! returning ==>" + getWhatMatched() + "<==" + " and rightof=" + getRightOfMatch());
            }
        }
    }

    private void debugTrimTrailing(String s) {
        if (DEBUG_TRIM_TRAILING) {
            System.out.println("Debugging trim trailing: " + s);
        }
    }

    static void doStuffWithParts() {
        String input = "pt. 1, part 2, Part III, some other stuff which should be leftover!";
        MatchRecord theMatcherRecord = new MatchRecord(input, PART_REGEX);
        firstPartsRun("theMatcherRecord = " + theMatcherRecord);
        while (MatchRecord.chewOneOffTheFront(theMatcherRecord)) {
            firstPartsRun("chewed... and now the record is: " + theMatcherRecord);
        }
    }

    public static boolean chewOneOffTheFront(MatchRecord theMatcherRecord) {
        firstPartsRun("Top of chewOneOffTheFront... \n\t>theMatcherRecord = " + theMatcherRecord);
        String regex = theMatcherRecord.getRegex();
        if (regex.charAt(0) != '^') {
            regex = "^" + regex;
        }
        theMatcherRecord.setRegex(regex);  // only find it at the front

        boolean returnMe = false;
        firstPartsRun("\n\ngoing to match... \n\t>theMatcherRecord = " + theMatcherRecord);

        if (MyMatcher.match(theMatcherRecord)) {
            returnMe = true;

            System.out.println("Emit $x whatever and $x++ PART... ==>" + theMatcherRecord.getOriginal().substring(0, theMatcherRecord.getEnd()));
            theMatcherRecord.setOriginal(theMatcherRecord.getOriginal().substring(theMatcherRecord.getEnd()));
        }

        return returnMe;
    }

    /*
                mr.setWhatMatched(mr.getOriginal().substring(m.start(), m.end()).trim());
            mr.setRightOMatch(mr.getOriginal().substring(0,m.start()).trim());
            mr.setLeftOfMatch(mr.getOriginal().replace(mr.getOriginal().substring(m.end()), "").trim());
     */
    public void setRightOMatch(String s) {
        rightOfMatch = s;
    }

    private static void firstMainRun(String s) {
        if (FIRST_MAIN_RUN) {
            System.out.println("First run main: " + s);
        }
    }

    private static void firstPartsRun(String s) {
        if (FIRST_PARTS_RUN) {
            System.out.println("First parts main: " + s);
        }
    }

    public String getRightOfMatch() {
        return rightOfMatch;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

}  // MatcherRecord
