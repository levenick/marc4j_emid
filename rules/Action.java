package org.marc4j.rules;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

/**
 * Action.java created by levenick on May 14, 2014 at 9:53:06 AM
 * A single action, from the list of actions associated with a rule
 * Each Action has a field and subfield, plus the value it outputs (the outputValue!)
 * The outputValue is either literal or programmatic, and createActualOutputValue transforms the latter.
 */
public class Action {

    protected int field = -1;
    protected char subfield = '?';
    protected String outputValue = "empty in header!";

    public Action() {
    }   //empty default constructor

    /**
     * Only called from Header Gets the next token from st, assumes it looks
     * like (960$a), or similar, stores the three digit part in field and the
     * letter in subfield
     *
     * @param st the StringTokenizer with the header line in it
     */
    public Action(StringTokenizer st) {
        String s = st.nextToken();

        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(s);

        Globals.initRuleDebug("In Action(StringTokenizer st) { " + s);
        field = Utils.extractFieldFromParens(s);
        if (field < 0) { // which should never happen!
            Globals.fatalError("No field found.%nFormat error?? Header::valid failed??");
        }
        subfield = Utils.extractSingleCharFromParens(s);
        if (subfield == '?') {  // also will not happen!
            Globals.fatalError("No char found.%nFormat error?? Header::valid failed??");
        }

        Globals.initRuleDebug("this = " + this);

    }

    public Action(int field, char subField, String outputValue) {   //initializing constructor
        this();   // invoke the default constructor
        this.field = field;
        this.subfield = subField;
        this.outputValue = outputValue;
    }

    public int getField() {
        return field;
    }

    public char getSubField() {
        return subfield;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setField(int field) {
        this.field = field;
    }

    public void setSubfield(char subField) {
        this.subfield = subField;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    /**
     * Transforms the record by adding a field with the value created here
     * 
     * If the outputValue is literal it is just returned
     * else it is programmatic... in which case...
     *
     * A bunch of special cases that could not be handled automagically Mostly
     * it's when things like #.#:1, or # inches, where the value that matched
     * the number needs to go in the new field and must be recovered from the
     * record that matched This is accomplished (blush) by grabbing the last
     * dataField matched from Globals, i.e.
     *
     * Globals.getMatchedDataFromSubfield();
     *
     * I'm embarrassed to have done this, but, for some reason, a better way did
     * not occur to me...
     *
     * Sorry.
     *
     * @param theRecord
     */
    String createActualOutputValue(Condition matchedCondition) {
        String actualOutputValue = Utils.stripDoubleFlag(outputValue);
        if (getOutputValue().equals("matchee")) {
            return Globals.getMatchedDataFromSubfield();
            //return matchedCondition.getTheConditionDataRequired().getDataValueRequired();
        } else if (outputValue.equals("ratio")) {
            actualOutputValue = calcRatio(matchedCondition);
        } else if (aUnitThang(outputValue)) {
            return handleUnits(matchedCondition);
        } else if (outputValue.equals("fullwide")) {
            actualOutputValue = calcFullWide(matchedCondition);
        } else if (outputValue.equals("PxxHxxMxxS")) {
            actualOutputValue = calcTime(matchedCondition);
        } else if (outputValue.equals("000")) {
            actualOutputValue = "P16H39M";
        } else if (outputValue.equals("minutes")) {
            actualOutputValue = minutesAsHM(matchedCondition);
        } else if (outputValue.equals("seconds")) {
            actualOutputValue = secondsAsMS(matchedCondition);
        } else if (outputValue.equals("minutesSeconds")) {
            actualOutputValue = minutesSecondsAsMS(matchedCondition);
        } else if (outputValue.equals("hoursMinutes")) {
            actualOutputValue = hoursMinutesAsHM(matchedCondition);
        } else if (outputValue.equals("hms") || outputValue.equals("partHMS")) {
            actualOutputValue = hoursMinutesSeconds(matchedCondition);
        } else if (outputValue.equals("partMinutes")) {
            actualOutputValue = partMinutesAsHM(matchedCondition);
        } else if (outputValue.equals("2partMinutes")) {
            actualOutputValue = pairPartMinutesAsHM(matchedCondition);
        } else if (outputValue.equals("3partMinutes")) {
            actualOutputValue = triplePartMinutesAsHM(matchedCondition);
        } else if (outputValue.equals("totalMinutes")) {
            actualOutputValue = totalMinutes(matchedCondition);
        } else if (outputValue.equals("totalHMS")) {
            actualOutputValue = totalHMS(matchedCondition);
        } else if (outputValue.equals("totalMinutesPair")) {
            actualOutputValue = totalMinutesPair(matchedCondition);
        } else if (outputValue.equals("totalMinutesTriple")) {
            actualOutputValue = totalMinutesTriple(matchedCondition);
        } else if (outputValue.equals("totalHours")) {
            actualOutputValue = totalHours(matchedCondition);
        } else if (outputValue.equals("hours")) {
            actualOutputValue = hoursAsHM(matchedCondition);
        } else if (outputValue.equals("partHours")) {
            actualOutputValue = partHoursAsHM(matchedCondition);
        } else if (outputValue.equals("partNumber")) {
            actualOutputValue = partNumber();  // the part number is in Globals, so no need for the condition...
        }

        if (actualOutputValue.length() <= 0) {
            Globals.panic("Action:: getActualOutputValue -- failed to generate actualOutputValue. outputValue=" + outputValue);
        }

        return actualOutputValue;
    }

    private boolean aUnitThang(String s) {
        return s.equals("unit") | s.equals("unitFraction") | s.equals("fraction");
    }

    private String handleUnits(Condition matchedCondition) {
        String patternRequired = matchedCondition.getTheConditionDataRequired().getDataValueRequired();
//        System.out.println("patternRequired = " + patternRequired);
        Pattern pattern = Pattern.compile(patternRequired);
        Matcher matcher = pattern.matcher(Globals.getMatchedDataFromSubfield());
//        System.out.println("Globals.getMatchedDataFromSubfield() = " + Globals.getMatchedDataFromSubfield());
        if (!matcher.find()) {
            Globals.panic("handleUnits: impossible!! It already matched the units!");
        }
        String groupFromPattern = matcher.group();

        String unitS = calcUnit(groupFromPattern);  // figure out the units

        if (outputValue.equals("unit")) { // need the value without the unit...
            int wholeValue = Utils.extractSingleInt(groupFromPattern);
            if (unitS.equals("cm")) {
                return "" + (10 * wholeValue); // + " " + unitS;
            } else {
                return "" + wholeValue; // + " " + unitS;
            }
        } else if (outputValue.equals("unitFraction")) {
            return Utils.extractWholeAndFractionAsString(groupFromPattern); // + " " + unitS;
        } else if (outputValue.equals("fraction")) {
            return Utils.extractFractionAsString(groupFromPattern); // + " " + unitS;
        }

        return "huh... this is impossible (I hope!)";
    }

    private String calcUnit(String groupFromPattern) {
        //System.out.println("groupFromPattern = " + groupFromPattern);

        String units = Utils.extractUnits(groupFromPattern);
        if (units.equals("in")) {
            return "in.";
        }

        return units;
    }

    private String calcRatio(Condition matchedCondition) {
        //System.out.println("calcRatio:: matchedCondition = " + matchedCondition);
        String whatMatched = Globals.getMatchedDataFromSubfield();
        //System.out.println("whatMatched = " + whatMatched);

        double d = Utils.extractDouble(whatMatched);

        return format(d) + ":1";
    }

    private String format(double d) {
        String returnMe = "" + (int) d;

        int fraction = (int) ((d - (int) d) * 100);
        String fracS = "" + fraction;

        returnMe += "." + fracS.charAt(0);
        if (fracS.length()>1) {
            returnMe += fracS.charAt(1);
        }

        return returnMe;
    }

    public static void main(String[] args) {
        Action a = new Action();
        System.out.println(a.format(1.33));
    }

    private String calcFullWide(Condition matchedCondition) {
        //System.out.println("calcFullWide:: matchedCondition = " + matchedCondition);
        String whatMatched = Globals.getMatchedDataFromSubfield();
        //System.out.println("whatMatched = " + whatMatched);

        double d = Utils.extractDouble(whatMatched);
        if (d >= 1.5) {
            return "widescreen";
        } else {
            return "fullscreen";
        }
    }

    public String toString() {
        String returnMe = "Action: ";
        returnMe += " field=" + getField();
        returnMe += "$" + getSubField();
        returnMe += "\toutputValue=" + getOutputValue();
        return returnMe;
    } // toString()

    String toStringBrief() {
        return "" + getField() + getSubField() + ": \"" + getOutputValue() + "\"";
    }

    String toStringHeader() {
        return "" + getField() + "$" + getSubField();
    }

    boolean saysNeedToSplit() {
        return outputValue.charAt(0) == Globals.SPLIT_CHAR;
    }

    /**
     * remove the #, we don't need it anymore
     */
    void stripDoubleFlag() {
        outputValue = Utils.stripDoubleFlag(outputValue);
    }

    private String minutesAsHM(Condition matchedCondition) {
        int minutes = Utils.extractFirstIntFromParens(matchedCondition);

        return Utils.toHM(minutes);
    }

    /**
     * This is a tricky one. "totalMinutes" comes from "7videocassettes (12 min. each)"
     * which needs 8 (count 'em, eight) fields -- one for the total (this one) and 
     * 7 for the seven parts (see partMinutes).
     * So... need to multiply 7*12... the 7 was stored in Globals (back in Rule:transform(...), whereas, 
     * the matchedCondition has the (12 min each) at the end, so extractFirstIntFromParens can grab it
     * @param matchedCondition
     * @return 
     */
    private String totalMinutes(Condition matchedCondition) {
        int totalMins = Globals.getRepetitions() * Utils.extractFirstIntFromParens(matchedCondition);
        return Utils.toHM(totalMins);
    }

    private String totalHMS(Condition matchedCondition) {
        int totalSeconds = Globals.getRepetitions() * extractHMSAsInt(matchedCondition);
        return Utils.toHMS(totalSeconds);
    }

    private String partMinutesAsHM(Condition matchedCondition) {
        return minutesAsHM(matchedCondition);
    }

    private String partNumber() {
        return "" + Globals.getPartNumber();
    }

    private String hoursAsHM(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int hours = Utils.extractFirstIntFromParens(whatMatched);

        return Utils.toHM(hours * 60);
    }

    private String partHoursAsHM(Condition matchedCondition) {
        return hoursAsHM(matchedCondition);
    }

    private String totalHours(Condition matchedCondition) {
        int totalHrs = Globals.getRepetitions() * Utils.extractFirstIntFromParens(matchedCondition);
        return Utils.toHM(totalHrs * 60);
    }

    private String totalMinutesPair(Condition matchedCondition) {
        int firstMin = Utils.extractFirstIntFromParens(matchedCondition);
        int secondMin = Utils.extractSecondIntFromParens(matchedCondition);
        return Utils.toHM(firstMin + secondMin);
    }

    private String totalMinutesTriple(Condition matchedCondition) {
        int firstMin = Utils.extractFirstIntFromParens(matchedCondition);
        int secondMin = Utils.extractSecondIntFromParens(matchedCondition);
        int thirdMin = Utils.extractThirdIntFromParens(matchedCondition);
        return Utils.toHM(firstMin + secondMin + thirdMin);
    }

    private String pairPartMinutesAsHM(Condition matchedCondition) {
        if (Globals.getPartNumber() == 1) {
            return minutesAsHM(matchedCondition);
        } else {
            if (Globals.getPartNumber() != 2) {
                Globals.panic("Gah! Must be 2!!" + " mc=" + matchedCondition);
            }
            int minutes = Utils.extractSecondIntFromParens(matchedCondition);

            return Utils.toHM(minutes);
        }
    }

    private String triplePartMinutesAsHM(Condition matchedCondition) {
        if (Globals.getPartNumber() == 1) {
            return minutesAsHM(matchedCondition);
        } else if (Globals.getPartNumber() == 2) {
            int minutes = Utils.extractSecondIntFromParens(matchedCondition);
            return Utils.toHM(minutes);
        } else {
            if (Globals.getPartNumber() != 3) {
                Globals.panic("Gah! Must be 3!!" + " mc=" + matchedCondition);
            }
            int minutes = Utils.extractThirdIntFromParens(matchedCondition);

            return Utils.toHM(minutes);
        }
    }

    private String minutesSecondsAsMS(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int minutes = Utils.extractFirstIntFromParens(whatMatched);
        int seconds = Utils.extractSecondIntFromParens(matchedCondition);

        return Utils.toMS(minutes * 60 + seconds);
    }

    private String hoursMinutesAsHM(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int hours = Utils.extractFirstIntFromParens(whatMatched);
        int minutes = Utils.extractSecondIntFromParens(matchedCondition);

        return Utils.toHM(hours * 60 + minutes);
    }

    private String secondsAsMS(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int seconds = Utils.extractFirstIntFromParens(whatMatched);

        return Utils.toMS(seconds);
    }

    private String hoursMinutesSeconds(Condition matchedCondition) {
        return Utils.toHMS(extractHMSAsInt(matchedCondition));
    }

    /**
     * converts "(1 hr, 1 min, 10 sec)" to 3670 (right, seconds) 
     * @param matchedCondition
     * @return 
     */
    private int extractHMSAsInt(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int hours = Utils.extractFirstIntFromParens(whatMatched);
        int minutes = Utils.extractSecondIntFromParens(matchedCondition);
        int seconds = Utils.extractThirdIntFromParens(matchedCondition);
        return hours * 60 * 60 + minutes * 60 + seconds;
    }

    private String calcTime(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();

        String returnMe = Utils.secondsToHoursMinutesSeconds(Globals.getTime());
        return returnMe;
    }

}  // Action
