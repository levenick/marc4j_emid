package jrl2016;

import static jrl2016.ProcessorGlobals.PROCESSING_TYPE_BASIC;
import static jrl2016.ProcessorGlobals.PROCESSING_TYPE_ENHANCED_AS_BASIC;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.rules.StringList;
import org.marc4j.rules.Utils;

/**
 * @author levenick May 3, 2016 10:57:38 AM
 */
public class Processor {

    static final boolean INIT_DEBUG = false;
    private final static boolean LATER_DEBUG = false;
    private final static boolean DEBUGGING = false;
    private static MarcStreamReader theMarcReader;
//    private static MyWriter processedMW, failedMW;
    static StringList regexList;
    private static int countPart;  // count the number of parts found
    private final static boolean JUNE16 = false;
    private static String aOrT = "?";
    private static final boolean JUNE22 = false;

    /**
     * Prepare to process -- the regexList was set from Main already
     *
     * @param input
     * @param output
     */
    public static void init() {
//        debugJune1("inputFileName=" + input);
//        debugJune1("outputFileName=" + output);

        ProcessorGlobals.initCounts();
        System.out.println("is there something else I need to initialize??");
    }

    /**
     * Process the title records via this pseudo-code for each record { for each
     * 505a { count505s++ (and save as numOccurances for output with $b) split
     * on — -> new 935 TitleMain (1: CREATED_BY_SPLITMAIN) for each 935
     * TitleMain move after / -> $1 (2: CREATED_BY_RESPONSIBLE) split on = ->
     * new 935 TitleVariantParallel (3:CREATED_BY_PARALLEL) split on ;|;and, ->
     * new 935 TitleAdditional (4:CREATED_BY_ADDITIONAL)
     *
     * for each 935 move (this) -> $0 this (8: CREATED_BY_PARENS)
     *
     * for each $1 split on part parts -> new 935 TitleUnmarked (with part 4. A
     * final title as the value) (5: unmarked)
     *
     * for each 935 { match BeginningRegexes.txt at beginning and move -> $x++
     * whatMatched $x++ TitlePartNameOrNumber (6: beginning part) NB:
     * whatMatched has trailing punctuation trimmed (see
     * data/regexes/trailingPunctuation.txt) split off subtitles (using : as the
     * splitter) for each : add ExtraTitle to ExtraTitleList with (the text,
     * "TitleSubtitle", CREATED_BY_SUBTITLE) for later output split on or, ->
     * $x++ TitleVariantAlternative (9: CREATED_BY_VARIANT) add ExtraTitle to
     * ExtraTitleList with (the text, "TitleVariantAlternative",
     * CREATED_BY_VARIANT) for later output }
     *
     * for each 935 { write the title and its type to the next two subfields
     * (this assumes they are in the title list in the correct order!) for each
     * ExtraTitle in extraTitleList { add a pair of subfields with the next $
     * values with the title, and its type add who created it to $7 } }
     *
     * for each 935 output { $a505 $bnumOccurances $ca } } // for each 505a
     * write record to output file
     *
     * @param the505
     */
    public static void process(Whole505a the505) {
        the505.crushMultipleSpaces();
        step2_splitMainTitles(the505);
        initDebug("after step2", the505);
        step3_chopResponsible(the505);
        initDebug("after step3, responsible", the505);
        step3Point6_splitParallelAndAdditionalTitles(the505);
        initDebug("after step3Point6_splitParallelAndAdditionalTitles, split others", the505);
        step4_splitParts(the505);
        initDebug("after step4_splitParts", the505);
        step4point5_moveFromParens(the505);
        initDebug("after step4point5_moveFromParens -- moveParens", the505);

        step6WriteTitles(the505);
//        initDebug("after step6WriteTitles -- write titles?? FIX THIS!!", the505);
        step7AddConstantFields(the505);
        laterDebug("after step7", the505);
    }

    private static void step2_splitMainTitles(Whole505a the505) {
//        spit("\nStarting step2");
        the505.splitMainTitles();
//        spit("\nEnd of step2 the505 = " + the505);
    }

    private static void step3_chopResponsible(Whole505a the505) {
//        spit("\nStarting step3");
        the505.chopResponsible();
//        spit("\nEnd of step3 the505 = " + the505);
    }

    private static void spit(String s) {
        if (DEBUGGING) {
            System.out.println(s);
        }
    }

    private static void step3Point6_splitParallelAndAdditionalTitles(Whole505a the505) {
//        spit("\nStarting step4.6");
        the505.splitParallelAndAdditionalTitles();
//        spit("\nEnd of step4.6 the505 = " + the505);
    }

    private static void step4_splitParts(Whole505a the505) {
//        spit("\nStarting step4");
        the505.splitParts();
        the505.splitDollar1Parts();
//        spit("\nEnd of step4 the505 = " + the505);
    }

    private static void step4point5_moveFromParens(Whole505a the505) {
//        spit("\nStarting step4point5_moveFromParens");
        the505.moveFromParens();
    }

    private static void step6WriteTitles(Whole505a the505) {
        the505.convertTitlesToSubfields();
    }

    private static void step7AddConstantFields(Whole505a the505) {
        the505.addConstantFields(aOrT);
    }

    static void buildRegexList(String fn) {
        regexList = new StringList(fn, true);  // read in all the regexes and add a hat in front!
    }

    /*    private static MyWriter openTextOutput(String output) {
        return new MyWriter(output);
    }

    private static void openInput(String openMe) {
        try {
            theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)));

        } catch (Exception e) {
            Globals.panic("failed to open " + openMe);
        }
    }
     */
    public static int recordCount;
    public static long startTime;
    private static MarcStreamWriter theMarcWriter;

    public static void processItAll(MarcStreamReader theReader, MarcStreamWriter writeTohere) {

        theMarcReader = theReader;
        theMarcWriter = writeTohere;
        /*
        for each record {
            for each 505a {
                process it!
            }
        }*/

        int countNo505a = 0;
        recordCount = 0;
        startTime = System.currentTimeMillis();
        while (theMarcReader.hasNext()) {
            recordCount++;
            if (recordCount % 1000 == 0) {
                long time = System.currentTimeMillis();
                time = (time - startTime) / 1000;
                System.out.print("\nrecordCount = " + recordCount);
                System.out.println(" time = " + time + " so... that's " + 1.0 * recordCount / time + " records/sec...");
            }
//            spit("\nNew record #" + recordCount);
            Record theRecord = theMarcReader.next();
            if (JUNE22 && recordCount == 25) {

                System.out.println("\ncount=" + recordCount + "\ntheRecord = " + theRecord);
            }
            char ch = 'a';                                    // only 505$a right now
            StringList listOf505as = Utils.getDataFromFieldAndSubfield(theRecord, 505, ch);
            if (listOf505as.isEmpty()) {  // if no 505$a
                aOrT = PROCESSING_TYPE_ENHANCED_AS_BASIC;
                countNo505a++;
                process505NotA(theRecord);
            } else {
                aOrT = PROCESSING_TYPE_BASIC;
                // otherwise... send each out for processing
                int count505s = 0; // count which 505 we are on
                for (String next505string : listOf505as) {
                    count505s++;
                    Whole505a the505 = new Whole505a(next505string, count505s);
                    process(the505);

                    // having processed it, write the processed fields into the original record
                    the505.convertToMRC_Format(theRecord);
                }
            }
            // and then spit it out to the file
            theMarcWriter.write(theRecord);
        }

//        processedMW.close();
        System.out.print("recordCount = " + recordCount);
        System.out.println("  countNo505a = " + countNo505a);
    }

    /**
     * If there is no 505$a, concatenate $b-$z (with spaces in between!) and
     * treat that as $a for ...er... a poor substitute for Enhanced Workflow
     *
     * @param theRecord -- the original record
     */
    private static void process505NotA(Record theRecord) {
        int count505s=0;
        for (VariableField nextField : theRecord.getVariableFields()) {
            String s = "";
            String tag = nextField.getTag();
            int tagInt = Integer.parseInt(tag);

            if (tagInt == 505) {
                count505s++;
                if (JUNE22 && recordCount == 25) {
                    System.out.println("JUNE22 && recordCount==25 nextField = " + nextField.toString());
                }
                for (Subfield nextSubField : nextField.getSubfields()) {

                    char ch = nextSubField.getCode();
                    if (ch > 'a' && ch <= 'z') {
                        s += " " + nextSubField.getData();
                    }
                }
                Whole505a the505 = new Whole505a(s, count505s);
                process(the505);
                the505.convertToMRC_Format(theRecord);
                if (JUNE16) {
                    System.out.println("converted it...");
                    System.out.println("the505 = " + the505);
                    System.out.println("");
                }

            }
        }

    }

    /*          for (VariableField nextField : theRecord.getVariableFields()) {
            String tag = nextField.getTag();
            int tagInt = Integer.parseInt(tag);

            if (showAll || displayList.contains(tagInt)) {
                if (!compressed) {
                    theTA.append("=" + nextField.toString() + "\n");
                } else if (Integer.parseInt(nextField.getTag()) <= 10) {
                    theTA.append("=" + nextField.toString() + " ");
                } else {
                    DataField df = (DataField) nextField;
                    theTA.append("\n" + myToString(df));
                } //                if (tagInt == 8) {
                //                    theTA.append("                            |||\n");
                //                }
            }
        }*/
//    void add505a(Record theRecord, String s) {
//        theRecord.g
//        MarcFactory factory = MarcFactory.newInstance();
//        DataField theField = factory.newDataField(Utils.asTag(935), ' ', ' ');
//
//        for (SubfieldRecord nextSubField : subfieldList) {
//            Utils.addSubField(theRecord, theField, nextSubField.getCh(), nextSubField.getContents());
//        }
//        theRecord.addVariableField(theField);
//    }
/*private static void toFailFile(String s) {
        failedMW.println(s);
    }

    private static void toSucceedFile(String s) {
        processedMW.println(s);
    }

    private static boolean goodPartFormat(String nextS) {
        return false;
    }
     */
    static StringList getRegexlist() {
        return regexList;
    }

    public static void setRegexList(StringList list) {
        regexList = list;
    }

    static void incCountPart() {
        countPart++;
    }

    /**
     * @return the countPart
     */
    public static int getCountPart() {
        return countPart;
    }

    /**
     * @param aCountPart the countPart to set
     */
    public static void setCountPart(int aCountPart) {
        countPart = aCountPart;
    }

    public static StringList getRegexList() {
        return regexList;
    }

    private static void debugJune1(String s) {
        System.out.println(s);
    }

    private static void initDebug(String s, Whole505a the505) {
        if (INIT_DEBUG) {
            new Display505Frame(s, the505);
        }
    }

    private static void laterDebug(String s, Whole505a the505) {
        if (LATER_DEBUG) {
            new Display505Frame(s, the505);
        }
    }

}
