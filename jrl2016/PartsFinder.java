package jrl2016;

import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.marc4j.rules.StringList;

public class PartsFinder {

    private static String REGEX = ":|or,";
    //private static String REGEX = "\\(.*?\\)";
    //private static String REGEX = "(pt\\. |part )(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|i |ii|iii|iv|v|vi|vii|viii|ix|x)(\\.|,)";
    //private static String INPUT = "Pt. 1. one -- part 234 two -- pt. three. three -- pt. 4, four-- PART TEN 10!,";
    //private static String INPUT = "Stuff (parenthesized stuff!) and other stuff (more stuff from parens) and more other stuff";
    private static String INPUT = "Another title : with subtitle, chapter 5 (1965) or, One thing or, some OTHER thing!";

    public static void main(String[] args) {

        Pattern p = Pattern.compile(REGEX);
        // get a matcher object
        Matcher matcher = p.matcher(INPUT);
        
        ArrayList<Point> list = new ArrayList<Point>();
        boolean found = false;
        while (matcher.find()) {
            System.out.println("Found the text >" + matcher.group()
                    + "< starting at index " + matcher.start()
                    + " and ending at index " + matcher.end()
            + "... which originally was: " + INPUT.substring(matcher.start(), matcher.end()));
            found = true;
            list.add(new Point(matcher.start(), matcher.end()));
        }
        if (!found) {
            System.out.println("nothing!!");
        } else {
            for (int i=0; i< list.size(); i++) {
                Point nextP = list.get(i);
                System.out.println("nextP = " + nextP);
                System.out.println(INPUT.substring(nextP.x, nextP.y));
                int endS=0;
                if (i+1 < list.size()) {
                    endS = (int) list.get(i+1).getX();
                } else endS = INPUT.length();
                System.out.println("==>" + INPUT.substring(nextP.y, endS));
            }
        }
                
        String[] pieces = INPUT.split(REGEX);
        for (String s: pieces) {
            System.out.println("s = " + s);
        }
        
         System.out.println("before INPUT = " + INPUT);
       INPUT = INPUT.replaceAll(REGEX, "");
        System.out.println("after INPUT = " + INPUT);
    }
}


