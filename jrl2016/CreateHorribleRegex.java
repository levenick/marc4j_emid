package jrl2016;

import org.marc4j.rules.*;

/**
 * @author levenick May 12, 2016 3:25:05 PM
 */
public class CreateHorribleRegex {

    public final static String NUMBER_THING = "(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|i |ii|iii|iv|v|vi|vii|viii|ix|x)";
    public final static String AFTER_NUMBER = "(\\. |, )";
    public final static String PART_TO_NUMBER = "(\\. |s | ))";
    
    public final static String PATH = "src/jrl2016/";

    public static void main(String[] args) {
        MyReader mr = new MyReader(PATH + "BeginningPatterns.txt");
        MyWriter mw = new MyWriter(PATH + "BeginningRegexes.txt");
        
        String[] splits = null;
        while (mr.hasMoreData()) {
            String s = mr.giveMeTheNextLine();
            splits = s.split("\t");
            //System.out.println("s = " + s);
            //s = s.replaceFirst("\\d*\\s", "").trim(); // just deleting those pesky numbers... hey! fix this!!
            //s = "(" + s + PART_TO_NUMBER + NUMBER_THING + AFTER_NUMBER;
            if (splits.length<2) break;
            
            s = splits[1];
            mw.println(s);
        }

        mr.close();
        mw.close();
        System.out.println("bye");
    }

}
