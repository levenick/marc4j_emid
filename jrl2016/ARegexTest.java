package jrl2016;

import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author levenick May 11, 2016 8:59:54 AM
 */
public class ARegexTest {

    public static void main(String[] asdf) {
        String str = "123456";
        Pattern p = Pattern.compile("^(\\d\\d)");
        Matcher m = p.matcher(str);
        System.out.println(m.groupCount());
        while (m.find()) {
            String word = m.group();
            System.out.println(word + " " + m.start() + " " + m.end());
        }
//        String input = "pt1. pt2. stuff... pt3";
//        String regex = "(pt\\d\\1)";
//        Pattern p = Pattern.compile(regex);
//        // get a matcher object
//        Matcher matcher = p.matcher(input);
//
//        ArrayList<Point> list = new ArrayList<Point>();
//        boolean found = false;
//        while (matcher.find()) {
//                    System.out.println("matcher.group() = " + matcher.group());
//
//            System.out.println("Found the text >" + matcher.group()
//                    + "< starting at index " + matcher.start()
//                    + " and ending at index " + matcher.end()
//                    + "... which originally was: " + input.substring(matcher.start(), matcher.end()));
//            found = true;
//            list.add(new Point(matcher.start(), matcher.end()));
//        }
        System.out.println("bye");
    }

}
