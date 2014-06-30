package org.marc4j.rules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Utils.java created by levenick on May 16, 2014 at 12:59:29 PM
 */
public class Utils {

    /**
     * converts an int to a three digit String allows the programmer to work
     * with ints for field numbers and convert them to the String tags marc4j
     * uses
     *
     * @param field
     * @return
     */
    public static String asTag(int field) {
        if (field > 99) {
            return "" + field;
        }
        if (field > 9) {
            return "0" + field;
        }
        if (field > 0) {
            return "00" + field;
        }
        return "000";
    }

    public static boolean in001_999(String dataValue) {
        return between(dataValue, 001, 999);
    }

    /**
     * returns whether the dataValue is between, uh, hi, and lo (inclusive
     *
     * @param dataValue
     * @param lo
     * @param hi
     * @return
     */
    private static boolean between(String dataValue, int lo, int hi) {
        int value = Integer.parseInt(dataValue);
        return value >= lo && value <= hi;
    }

    public static boolean in000_999(String dataValue) {
        return between(dataValue, 000, 999);
    }

    /**
     *
     * @param s
     * @param inputColumn
     * @param inputColumnEnd
     * @return the chars in the columns between the other two params as a String
     */
    public static String columnRangeString(String s, int inputColumn, int inputColumnEnd) {
        String match3 = s.substring(inputColumn, inputColumnEnd + 1);

        if (match3.length() != 3) {
            Globals.fatalError("huh, column range != 3 (time to write more code!!) match3=" + match3 + " s=" + s + " from=" + inputColumn + " to=" + inputColumnEnd);
        }
        return s.substring(inputColumn, inputColumnEnd + 1);
    }

    /**
     * adaptor to simplify adding a field/subField to a record
     *
     * @param record
     * @param field
     * @param subField
     * @param outputValue
     */
    public static void addField(Record record, int field, char subField, String outputValue) {
        MarcFactory factory = MarcFactory.newInstance();

        DataField dataField = factory.newDataField(Utils.asTag(field), ' ', ' ');
        dataField.addSubfield(factory.newSubfield(subField, outputValue));
        record.addVariableField(dataField);
    }

    /**
     *
     * @param time - the time in seconds
     * @return the time as PxxHxxMxxS, 00S? means time==0!
     */
    public static String secondsToHoursMinutesSeconds(int time) {
        int seconds = time % 60;
        time = time / 60;
        int minutes = time % 60;
        int hours = time / 60;

        return format(hours, minutes, seconds);
    }

    /**
     * formats time as PxxHxxMxxS, 00S? means time==0
     *
     * @param hrs
     * @param minutes
     * @param seconds
     * @return
     */
    private static String format(int hrs, int minutes, int seconds) {
        String returnMe = "P";
        returnMe += hmsPart(hrs, "H");
        returnMe += hmsPart(minutes, "M");
        returnMe += hmsPart(seconds, "S");

        if (returnMe.length() == 0) {
            returnMe = "00S?";
        }

        return returnMe;
    }

    /**
     *
     * @param value
     * @return value as a 2 digit number
     */
    private static String twoDigits(int value) {
        if (value > 9) {
            return "" + value;
        }
        if (value > 0) {
            return "0" + value;
        }
        return "00";
    }

    /**
     *
     * @param s
     * @return is s all digits!
     */
    public static boolean allDigits(String s) {
        if (s.length() == 0) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Strips the ()s from a String
     *
     * @param s
     * @return
     */
    public static String extractFromParens(String s) {
        String returnMe = "failed extraction";
        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            returnMe = matcher.group();
        } else {
            Globals.fatalError("Utils:: extractFromParens --No match found.%nFormat error?? No parens!!");
        }

        return returnMe;
    }

    /**
     * extracts a three digit number from ()s
     *
     * @param s
     * @return
     */
    public static int extractFieldFromParens(String s) {
        int returnMe = -2;
        String inside = extractFromParens(s);
        returnMe = extract3(inside);

        return returnMe;
    }

    private static int extract3(String inside) {
        Pattern pattern = Pattern.compile("\\d\\d\\d");
        Matcher matcher = pattern.matcher(inside);
        if (matcher.find()) {
            String found = matcher.group();
            return Integer.parseInt(found);
        } else {
            Globals.fatalError("extractFieldFromParens:: Extract3 -- Busted!!" + inside);
            return -1;
        }
    }

    public static char extractSingleCharFromParens(String s) {
        char returnMe = '?';
        String inside = extractFromParens(s);
        returnMe = extractChar(inside);

        return returnMe;
    }

    public static char extractChar(String s) {
        Pattern pattern = Pattern.compile("[a-z]");
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.fatalError("no alphabetic subfield!");
        }
        return matcher.group().charAt(0);
    }

    public static boolean isAControlField(int fieldNumber) {
        return fieldNumber < 10;
    }

    public static void addSubField(Record theRecord, DataField theField, char c, String data) {
        MarcFactory factory = MarcFactory.newInstance();
        theField.addSubfield(factory.newSubfield(c, data));
    }

    /**
     * Gets all the subfield data from all the fields with that number (right,
     * can be multiple fields with a particular number and multiple subfields
     * within each...
     *
     * @param theRecord
     * @param fieldNumber
     * @param subFieldChar
     * @return
     */
    public static StringList getDataFromFieldAndSubfield(Record theRecord, int fieldNumber, char subFieldChar) {
        StringList returnMe = new StringList();
        String tag = Utils.asTag(fieldNumber);

        List<VariableField> list = theRecord.getVariableFields(tag);
        for (VariableField vField : list) {
            DataField dField = (DataField) vField;
            List<Subfield> subFieldList = dField.getSubfields(subFieldChar);
            for (Subfield nextSubField : subFieldList) {
                returnMe.add(nextSubField.getData());
            }
        }

        return returnMe;
    }

    public static String getFirstDataFromFieldAndSubfield(Record aRecord, FieldSpecifier theFieldSpec) {
        int fieldNumber = theFieldSpec.getFieldNumber();
        char subFieldChar = theFieldSpec.getSubfieldChar();

        StringList list = getDataFromFieldAndSubfield(aRecord, fieldNumber, subFieldChar);
        if (list.size() > 0) {
            return list.get(0);
        }

        return "foo";
    }

    /**
     * csv input data from Excel is, uh, comma-separated, but there are commas
     * in the patterns sometimes so, to fix that, this replaces unquoted commas
     * with tabs, and throws away the quotes while it's at it!
     *
     * @param s
     * @return s with unquoted commas replaced by tabs and quotes stripped
     */
    public static String tabsForCommas(String s) {
        String returnMe = "";
        boolean quoted = false;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '"') {
                quoted = !quoted;
            } else {
                if (!quoted && ch == ',') {
                    returnMe += "\t";
                } else {
                    returnMe += ch;
                }
            }
        }

        if (quoted) {
            Globals.panic("unclosed double quotes!!");
        }

        return returnMe;
    }

    /**
     * Extracts a double from a string like (2.35:1)
     *
     * @param s
     * @return the double value
     */
    public static double extractDouble(String s) {
        Pattern pattern = Pattern.compile("\\(\\d+\\.\\d+:1\\)");
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            pattern = Pattern.compile("\\(\\d+:1\\)");
            matcher = pattern.matcher(s);

            if (!matcher.find()) {
                Globals.panic("impossible!! s=" + s);
            }
        }
        String result = matcher.group();
        //System.out.println("result = " + result);
        String numberString = result.substring(1, result.indexOf(':'));
        //System.out.println("numberString = " + numberString);

        return Double.parseDouble(numberString);
    }

    static String unitPattern = "(in|cm|mm|inches|inch)";

    /**
     *
     * @param s
     * @return in|cm|mm|inches|inch
     */

    public static String extractUnits(String s) {
        Pattern pattern = Pattern.compile(unitPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched the units! s=" + s);
        }
        String result = matcher.group();

        return result;
    }

    static String fractionPattern = "\\d/\\d";

    public static String extractFractionAsString(String s) {
        Pattern pattern = Pattern.compile(fractionPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched upstream!");
        }
        String fraction = matcher.group();

        return fraction;
    }

    static String wholeAndFractionPattern = "\\d* \\d/\\d";

    public static String extractWholeAndFractionAsString(String s) {
        Pattern pattern = Pattern.compile(wholeAndFractionPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched the units!");
        }
        String result = matcher.group();

        return result;
    }

    static String intPattern = "\\d+";

    /**
     *
     * @param s
     * @return the first d+
     */
    public static int extractSingleInt(String s) {
        Pattern pattern = Pattern.compile(intPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched the units!");
        }
        String result = matcher.group();
        int n = -1;
        try {
            n = Integer.parseInt(result);
        } catch (Exception e) {
            Globals.advice("extractInt failed on the input: >" + s + "< so, you have a -1 as the width... result=" + result + "=");
        }
        return n;
    }

    static String firstIntPattern = "\\d+";

    public static int extractFirstInt(String s) {
        Pattern pattern = Pattern.compile(firstIntPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched! s=" + s + "=");
        }
        String result = matcher.group();
        int returnMe = -1;
        try {
            returnMe = Integer.parseInt(result);
        } catch (Exception e) {
            Globals.panic("Impossible!! oops... should have been an int! The String passed was >" + s + "<  " + e.toString());
        }
        return returnMe;
    }

    static String firstIntInParensPattern = "\\(.*\\d+";

    public static int extractFirstIntFromParens(String s) {
        Pattern pattern = Pattern.compile(firstIntInParensPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched the units! s=" + s + "=");
        }
        String result = matcher.group();

        if (result.charAt(0) == '(') { // and it should be!
            pattern = Pattern.compile("\\d+");
            matcher = pattern.matcher(result);
            if (!matcher.find()) {
                Globals.panic("inside! impossible!! It already matched the units! s=" + s + "=");
            }
            result = matcher.group();
        }

        int n = -1;
        try {
            n = Integer.parseInt(result);
        } catch (Exception e) {
            Globals.advice("extractFirstInt failed on the input: >" + s + "< so, you have a -1 as the width... result=" + result + "=");
        }
        return n;
    }

    static String secondIntInParensPattern = "\\(.*\\d+.*\\d+";

    public static int extractSecondIntFromParens(String s) {
        Pattern pattern = Pattern.compile(secondIntInParensPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("second impossible!! It already matched! s=" + s + "=");
        }
        String result = matcher.group();

        if (result.charAt(0) == '(') { // and it should be!
            pattern = Pattern.compile("\\d+");
            matcher = pattern.matcher(result);
            if (!matcher.find()) {
                Globals.panic("inside second! impossible!! It already matched! s=" + s + "=");
            }
            if (!matcher.find()) {
                Globals.panic("inside second second! impossible!! It already matched! s=" + s + "=");
            }
            result = matcher.group();
        }

        int n = -1;
        try {
            n = Integer.parseInt(result);
        } catch (Exception e) {
            Globals.advice("extractSecondInt failed on the input: >" + s + "< so, you have a -1 as the result=" + result + "=");
        }
        return n;
    }

    static String thirdIntInParensPattern = "\\(.*\\d+.*\\d+.*\\d+";

    private static int extractThirdIntFromParens(String s) {
        Pattern pattern = Pattern.compile(thirdIntInParensPattern);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            Globals.panic("second impossible!! It already matched! s=" + s + "=");
        }
        String result = matcher.group();

        if (result.charAt(0) == '(') { // and it should be!
            pattern = Pattern.compile("\\d+");
            matcher = pattern.matcher(result);
            if (!matcher.find()) {
                Globals.panic("inside second! impossible!! It already matched! s=" + s + "=");
            }
            if (!matcher.find()) {
                Globals.panic("inside second second! impossible!! It already matched! s=" + s + "=");
            }
            if (!matcher.find()) {
                Globals.panic("inside third second! impossible!! It already matched! s=" + s + "=");
            }
            result = matcher.group();
        }

        int n = -1;
        try {
            n = Integer.parseInt(result);
        } catch (Exception e) {
            Globals.advice("extractSecondInt failed on the input: >" + s + "< so, you have a -1 as the result=" + result + "=");
        }
        return n;
    }

    public static String applyPattern(String data, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            Globals.panic("impossible!! It already matched previously!");
        }
        String result = matcher.group();

        return result;
    }

    /**
     * remove the #
     * @param outputValue
     * @return 
     */
    public static String stripDoubleFlag(String outputValue) {
        if (outputValue.charAt(0) == Globals.SPLIT_CHAR) {
            return outputValue.replace("" + Globals.SPLIT_CHAR, "");
        }
        return outputValue;
    }

    /**
     * converts int minutes to String PxHxM
     *
     * @param minutes
     * @return
     */
    public static String toHM(int minutes) {
        int hr = minutes / 60;
        minutes = minutes % 60;

        return format(hr, minutes, 0);
    }

    public static String hmsPart(int value, String what) {
        if (value == 0) {
            return "";
        }
        return "" + twoDigits(value) + what;
    }

    public static int extractFirstIntFromParens(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int minutes = Utils.extractFirstIntFromParens(whatMatched);

        return minutes;
    }

    public static int extractSecondIntFromParens(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int minutes = Utils.extractSecondIntFromParens(whatMatched);

        return minutes;
    }

    public static int extractThirdIntFromParens(Condition matchedCondition) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int seconds = Utils.extractThirdIntFromParens(whatMatched);

        return seconds;
    }

    /**
     * would be better to utilize toMS once hours are settled; but I'm tired
     *
     * @param seconds
     * @return
     */
    public static String toHMS(int seconds) {
        int hr = seconds / (60 * 60);
        seconds = seconds % (60 * 60);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return format(hr, minutes, seconds);
    }

//    static String cursedCommaPattern = "\\(.*\\d+,\\d\\d\\d";

//    public static boolean itsACursed2000WithCommasTypeNumber(String s) {
//        Pattern pattern = Pattern.compile(cursedCommaPattern);
//        Matcher matcher = pattern.matcher(s);
//        if (!matcher.find()) {
//            return false;
//        }
//
//        return true;
//    }

    public static String toMS(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return format(0, minutes, seconds);
    }

//    public static boolean find(Subfield nextSubField, String pattern) {
//        Pattern p = Pattern.compile(pattern);
//        Matcher m = p.matcher(removeCursed2000withCommaTypeComma(nextSubField.getData()));
//        return m.find();
//    }
//
//    private static String removeCursed2000withCommaTypeComma(String data) {
//        if (itsACursed2000WithCommasTypeNumber(data)) {
//            Pattern pattern = Pattern.compile(cursedCommaPattern);
//            Matcher matcher = pattern.matcher(data);
//            matcher.find();
//            String matchedS = matcher.group();
//            int startIndex = data.indexOf(matchedS);
//            String front = data.substring(0, startIndex);
//            System.out.println("front =" + front + "=");
//            String back = data.substring(startIndex);
//            System.out.println("back =" + back + "=");
//            back = back.replace(",", "");
//            System.out.println("after... back = " + back);
//            return front + back;
//        }
//
//        return data;
//    }

//    public static void main(String[] args) {
//        String s = "2 bar (1,234 mins.)";
//        String after = removeCursed2000withCommaTypeComma(s);
//        System.out.println("after = " + after);
//
//        //String input = "1reel (ca. 12 min.) :";
//        //int n = extractSingleInt(input);
//        //String num = extractFractionAsString(input);
////        String units = extractUnits(input);
////        String result = applyPattern(input, "reel");
////        System.out.println("input=" + input + "\n\tresult=" + result);
//////        String input = "blah, blah, \"foo, foo\", blah";
////        System.out.println("input = " + input);
////        String output = tabsForCommas(input);
////        System.out.println("output = " + output);
////        System.out.println(toHM(601));
//    }

}
