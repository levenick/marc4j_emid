package jrl2016;

import java.util.regex.Pattern;
import static jrl2016.ProcessorGlobals.PART_REGEX;
import org.marc4j.rules.StringList;

/**
 * @author levenick May 16, 2016 12:42:15 PM
 */
public class MyMatcher {

    static final boolean DEBUG_MY_MATCHER = false;

    public static boolean match(MatchRecord mr) {
        if (mr.getOriginal() == null) {
            return false;
        }

        Pattern p = Pattern.compile(mr.getRegex());
        java.util.regex.Matcher m = p.matcher(mr.getOriginal());
//WAS        java.util.regex.Matcher m = p.matcher(mr.getOriginal().toLowerCase());

        if (m.find()) {
            mr.setEnd(m.end());
            mr.setStart(m.start());
            return true;
        }
        return false;  // didn't match
    }

    public static void main(String[] args) {
        String theRegex = "(disc(\\. |: |s | ))(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|i |ii|iii|iv|v|vi|vii|viii|ix|x)(\\. |, |: )";
        String theString = "Disc one: 1. The bumpy road to love -- 2. Only you -- 3. Oy, wilderness  -- disc two: 10. Get real -- 11. Seoul mates  -- disc three: 21. It happened in Juneau -- 22. Our wedding -- 23. Cicely.";
        System.out.println("theString = " + theString);
        System.out.println("theRegex = " + theRegex);
        System.out.println("");
        StringList list = new StringList();
        list.add(theRegex); 
        
        MatchRecord mr = matchAny(list, theString);
        if (mr == null) {
            System.out.println("Fail!!");
            System.out.println("list = " + list);
            System.exit(888888);
        }
        mr.splitUsingRegex();
        mr.trimTrailingPunct();

        System.out.println("mr = " + mr);
        /*String s = "pt. 1 Part 2. Chapter 66, and some stuff at the end that should remain";
        String fileName = "partparts.txt";
        StringList list = new StringList(CreateHorribleRegex.PATH + fileName, false);
        System.out.println("nope...");*/
//        while (MyMatcher.matchAny(list, s)) {
//
//        }
    }

    public static MatchRecord matchAny(StringList regexList, String searchMe) {
        MatchRecord returnMe = null;

        for (String partPart : regexList) {
            MatchRecord theMatchRecord = new MatchRecord(searchMe, partPart);
            if (match(theMatchRecord)) {
                return theMatchRecord;
            }
        }
        return returnMe;
    }

    static void doStuffWithParts() {
        String input = "pt. 1, part 2, Part III, some other stuff which should be leftover!";
        MatchRecord theMatcherRecord = new MatchRecord(input, PART_REGEX);
//        debugMyMatcher("theMatcherRecord = " + theMatcherRecord);
        while (MatchRecord.chewOneOffTheFront(theMatcherRecord)) {
//            debugMyMatcher("chewed... and now the record is: " + theMatcherRecord);
        }
    }

    public static boolean chewOneOffTheFront(MatchRecord theMatcherRecord) {
//        debugMyMatcher("Top of chewOneOffTheFront... \n\t>theMatcherRecord = " + theMatcherRecord);
        String regex = theMatcherRecord.getRegex();
        if (regex.charAt(0) != '^') {
            regex = "^" + regex;
        }
        theMatcherRecord.setRegex(regex);  // only find it at the front

        boolean returnMe = false;
//        debugMyMatcher("\n\ngoing to match... \n\t>theMatcherRecord = " + theMatcherRecord);

        if (MyMatcher.match(theMatcherRecord)) {
            returnMe = true;

            System.out.println("Emit $x whatever and $x++ PART... ==>" + theMatcherRecord.getOriginal().substring(0, theMatcherRecord.getEnd()));
            theMatcherRecord.setOriginal(theMatcherRecord.getOriginal().substring(theMatcherRecord.getEnd()));
        }

        return returnMe;
    }

//    private static void debugMyMatcher(String s) {
//        if (DEBUG_MY_MATCHER) {
//            System.out.println("debugMyMatcher: ==>" + s + "<==");
//        }
//    }

}
