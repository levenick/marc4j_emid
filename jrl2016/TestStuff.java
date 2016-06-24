package jrl2016;

import org.marc4j.rules.StringList;

/**
 * @author levenick May 4, 2016 9:41:31 AM
 */
public class TestStuff {
    static boolean initDebug = false;

 
    
    static String inputParen = "pt. 1. A far (stuff in parens so there will be two in this record) country (85 min.) , or, foo! foo! foo! -- pt. 2. End of the road (55 min.) -- pt. 3. Real water (54 min.) -- pt. 4. Standing tall (54 min.) -- pt. 5. Death by myth (85 min.).";
//    static String input = "Second title or, 2nd title";
    static String input = "pt. 1. A (wonderful) title (50 min.) ; Second title -- Part 2, Another title : with subtitle, chapter 5 (1965)-- "
            + "part 3. Third title, no. 1, Beginning = Parallel title / directed by Somebody ; produced by Somebody else. part 4. A final title.";
    static String easyIn = "Main1; Additional1 -- Main2: subtitleIn2 -- Main3 = Parallel3 / Responsibility3 ; continued in $1. part 4, Final title.";

    static String june14 = "606.1. Techniques of therapeutic communication";
    static String nowWhat = "[Disc 1] Volume I: Greece ; Greece: Age of Alexander ; The Aztecs -- [Disc 2] Volume II: Carthage ; China ; Russia -- [Disc 3] Volume III: Great Britain ; The Persians ; The Maya -- [Disc 4] Volume IV: Napoleon and beyond ; The Byzantines ; Age of architects. ";
    static String june15 = "Cassette 1. Chapters 1-4 -- Cassette 2. Chapters 5-8.";
    static String june16 ="di yi ji-di er ji. Meng liang gu -- di san ji-di si ji. Zhong qiu duo cheng ye -- di wu ji-di qi ji. Feng juan hei tu di -- di ba  ji-di shi ji. Zhong yuan hu xiao tian";
    static String june20 ="Module A. Chapters 3 & 5.--Module B. Chapters 8 & 9";
    static String june22 = "[1]. Ppoppi = Popee -- [2]. Chinsil ŭi mun = The gate of truth -- [6]. Aegukcha geim = Patriot game. tape 1. lecture 1. J.B., we hardly know you ; lecture 2. Brothels of Hamburg ; lecture 3. Schumanns -- tape 2. lecture 4. Vagabond years ; lecture 5. Maturity.";
    static String june23 = "[1] Konkombe: Nigerian music (1980) -- [10-11] Romany Trail, pts. 1-2 (1982) -- [12] Number 17 cotton mill Shanghai blues: music in China (1984) ";
    public static void main(String[] args) {
        StringList list = new StringList("/Users/levenick/kelley/kelley2016/data/regexes/aaRegexes6_21.txt", true);
        Processor.setRegexList(list);
        Processor.init();
        //System.out.println("list = " + list);
        ProcessorGlobals.init();
        Processor.process(new Whole505a(june23));
        System.out.println("ProcessorGlobals.toStringTransformCounts() = " + ProcessorGlobals.toStringTransformCounts());
    }
    


}
