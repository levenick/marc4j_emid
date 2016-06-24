package jrl2016;

import static jrl2016.ProcessorGlobals.ADDITIONAL_DELIMITER;
import static jrl2016.ProcessorGlobals.CREATED_BY_ADDITIONAL;
import static jrl2016.ProcessorGlobals.CREATED_BY_PARALLEL;
import static jrl2016.ProcessorGlobals.CREATED_BY_SPLITMAIN;
import static jrl2016.ProcessorGlobals.CREATED_BY_UNMARKED;
import static jrl2016.ProcessorGlobals.DOLLAR1_PART;
import static jrl2016.ProcessorGlobals.PARALLEL_DELIMITER;
import static jrl2016.ProcessorGlobals.PART_REGEX;
import static jrl2016.ProcessorGlobals.RESPONSIBLE_DELIMITER;
import static jrl2016.ProcessorGlobals.TITLE_ADDITIONAL;
import static jrl2016.ProcessorGlobals.TITLE_MAIN;
import static jrl2016.ProcessorGlobals.TITLE_MAIN_REGEX;
import static jrl2016.ProcessorGlobals.TITLE_PART;
import static jrl2016.ProcessorGlobals.TITLE_VARIANT_PARALLEL;
import org.marc4j.marc.Record;
import org.marc4j.rules.Globals;

/**
 * The internal form of the 505 field, for processing
 * 
 * @author levenick May 4, 2016 1:35:13 PM
 */
public class Whole505a {

    private TitleRecordList titleList; // the list of titles, only titlemain, alternative, and additional?
    protected String rawString;
    private static final boolean BUSTED = false;
    private int numOccurances;
    private static final boolean JUNE10 = false;

    public Whole505a() {
        titleList = new TitleRecordList();
    }

    public Whole505a(String rawString) {   //initializing constructor
        this();   // invoke the default constructor
        this.rawString = rawString;
    }

    Whole505a(String rawString, int serial) {
        this(rawString);
        numOccurances = serial;
    }

    public TitleRecordList getTitleList() {
        return titleList;
    }

    public String getRawString() {
        return rawString;
    }

    public void setRawString(String rawString) {
        this.rawString = rawString;
    }

    public String toString() {
        String returnMe = "I am a Whole505a: ";
        returnMe += "\trawString=" + getRawString();
        returnMe += "\n\tmainList=" + getTitleList();
        return returnMe;
    } // toString()

    /**
     * Splits the raw string into main titles on --, - -, ---
     * Adds each to the titleList for later processing
     */
    void splitMainTitles() {
        int countMain = 0;
        for (String s : rawString.split(TITLE_MAIN_REGEX)) {
            countMain++;
            titleList.add(new Title935Record(s.trim(), TITLE_MAIN, countMain, 1, CREATED_BY_SPLITMAIN));
        }
    }

    /**
     * for each 935
     *     moves anything after a / into $1
     */
    void chopResponsible() {
        for (Title935Record nextTM : titleList) {
            nextTM.chopResponsible();
        }
    }

    void splitParallelAndAdditionalTitles() {
        splitTitlesVariantParallel();
        splitTitlesAdditional();
    }

    /**
     * splits off parallel titles, breaking on "="
     */
    private void splitTitlesVariantParallel() {
        betterSplit(PARALLEL_DELIMITER, TITLE_VARIANT_PARALLEL, null);
    }

    /**
     * splits off additional titles, breaking on ";" or "and," and keeping the
     * and, in the new title (while deleting the ;)
     */
    private void splitTitlesAdditional() {
        betterSplit(ADDITIONAL_DELIMITER, TITLE_ADDITIONAL, "and,");
    }

    /**
     * for each 935
     *      splitPartsInFront -- using a list of regexes in data/regexes
     *      splits Subtitles
     *      splits TitleVariantAlternative(buffer, myParent);
     */
    public void splitParts() {
        for (Title935Record nextTM : titleList) {
            nextTM.splitParts(this);
        }
    }

    public void splitDollar1Parts() {
        TitleRecordList originalList = titleList;
        titleList = new TitleRecordList(); // create a fresh list
        Title935Record mostRecentMain = null;

        for (int i = 0; i < originalList.size(); i++) {
            Title935Record nextTitleRecord = originalList.get(i);
            titleList.add(nextTitleRecord);   // leave it on the list
            if (nextTitleRecord.getIsAMainTitle()) {
                mostRecentMain = nextTitleRecord;  // so we can get the next serial for $e
            }

            String dollar1Text = nextTitleRecord.getDollar1Text();
            if (dollar1Text == null) {
                continue;
            }
            // first grab stuff outta parens!!
            nextTitleRecord.moveFromParensInsideDollar1(dollar1Text);

            // now look for a trailing part thang
            String[] partSplit = nextTitleRecord.getDollar1Text().split(PART_REGEX);
//            busted("looking for $1 parts... nextTitleRecord.getDollar1Text()=" + nextTitleRecord.getDollar1Text());
            nextTitleRecord.setDollar1Text(partSplit[0]);  // if we find nothing, this will be the whole thing... but, if there are ='s it will just be what's before the first one
//            busted("new $1 text... nextTitleRecord.getDollar1Text()=" + nextTitleRecord.getDollar1Text());

            // now try to find a trailing part thang
            for (int innerI = 1; innerI < partSplit.length; innerI++) {
                String nextTitle = partSplit[innerI].trim();  // pick up the title following the partpart
                if (mostRecentMain == null) {
                    Globals.fatalError("oops! mostRecentMain==null!!");
                }
                // found a part part in the $1, so make a new Record for it
                Title935Record nuTitle935Record = new Title935Record(nextTitle, DOLLAR1_PART, nextTitleRecord.getD(), mostRecentMain.incAndReturnNextE(), CREATED_BY_UNMARKED);
                String partPartForAddPair = dollar1Text.substring(partSplit[0].length(), dollar1Text.indexOf(nextTitle));
                nuTitle935Record.addPair(partPartForAddPair, TITLE_PART);
                titleList.add(nuTitle935Record); // add the new one
            }
        }

    }

    /**
     * for each title in titleList splits on splitter (a regex) leaves the first
     * split as the title (so if there's no split it doesn't change!) for each
     * additional split if there's an "and" after the ;, paste it on add a new
     * Title935Record with the next title and the same $d and next $e for the
     * mainTitle this came from... sigh...
     *
     * @param splitter
     * @param titleType
     * @param specialCaseAnd
     */
    private void betterSplit(String splitter, String titleType, String specialCaseAnd) {
        TitleRecordList originalList = titleList;
        titleList = new TitleRecordList(); // create a fresh list
        Title935Record mostRecentMain = null;
//        boolean manyParallel = false;
        for (int i = 0; i < originalList.size(); i++) {
            Title935Record nextTitleRecord = originalList.get(i);
            titleList.add(nextTitleRecord);   // leave it on the list
            if (nextTitleRecord.getIsAMainTitle()) {
                mostRecentMain = nextTitleRecord;  // so we can get the next serial for $e
            }

            String[] parallel = nextTitleRecord.getCurrentText().split(splitter);
            nextTitleRecord.setCurrentText(parallel[0]);  // if we find nothing, this will be the whole thing... but, if there are ='s it will just be what's before the first one
            for (int innerI = 1; innerI < parallel.length; innerI++) {
                String nextTitle = parallel[innerI].trim();
                if (mostRecentMain == null) {
                    Globals.fatalError("oops! mostRecentMain==null!!");
                }
                if (specialCaseAnd != null && nextTitle.startsWith(specialCaseAnd)) {
                    nextTitle = ";" + nextTitle;   // paste the ; back on
                }
                int howCreated = CREATED_BY_ADDITIONAL;
                if (splitter.equals(PARALLEL_DELIMITER)) {
                    howCreated = CREATED_BY_PARALLEL;
                }
                Title935Record nuTitle935 = new Title935Record(nextTitle, titleType, nextTitleRecord.getD(), mostRecentMain.incAndReturnNextE(), howCreated);
                titleList.add(nuTitle935); // add the new one
                maybeTransferDollar1(nextTitleRecord, nuTitle935);
            }

//            if (parallel.length > 2) {
//                manyParallel = true;
//            }

        }
//        if (manyParallel) {  // maybe dump this record to special file??
//            System.out.println("count=" + Processor.recordCount + "  Um... more than one parallel (or additional!) title added... is that okay??");
//            System.out.println("this.getRawString() = " + this.getRawString());
//        }
    }

    /**
     * If the old935 had a $1 (with things following a / in the original if the
     * / was closer to the nu than the old, transfer it there...
     *
     * @param old935
     * @param nu935
     */
    private void maybeTransferDollar1(Title935Record old935, Title935Record nu935) {
        if (old935.hasDollar1()) {
            int indexOfOldTitle = this.getRawString().indexOf(old935.getCurrentText());
            int indexOfNewTitle = this.getRawString().indexOf(nu935.getCurrentText());
            int indexOfSlash = this.getRawString().indexOf(RESPONSIBLE_DELIMITER);
//            System.out.println("indexOfSlash = " + indexOfSlash);
//            System.out.println("indexOfNewTitle = " + indexOfNewTitle);
//            System.out.println("indexOfOldTitle = " + indexOfOldTitle);
            if (indexOfOldTitle < indexOfNewTitle && indexOfNewTitle < indexOfSlash) {
                old935.transferDollar1To(nu935);

            }
        }
    }

    /**
     * for each 935
     *      Move anything in ()'s into $0
     */
    void moveFromParens() {
        for (Title935Record nextTM : titleList) {
            nextTM.moveFromParens();
        }
    }

    void busted(String s) {
        if (BUSTED) {
            System.out.println("Whole505a busted: " + s);
        }
    }

    /**
     * Now that all the processing is done... 
     */ 
    void convertTitlesToSubfields() {
        for (Title935Record nextTM : titleList) {
            nextTM.convertTitlesToSubfields();
        }
    }

    /**
     * convert the internal format (i.e. the Whole505a record) to mrc format,
     * paste onto the original mrc record, and output ...which... I guess... is
     * done by the Title935Records...
     *
     * @param theMarcWriter
     * @param theRecord
     */
    void convertToMRC_Format(Record theRecord) {
        for (Title935Record nextTM : titleList) {
            nextTM.convertToMRC_Format(theRecord);
        }

    }

    /**
     * Processing done:
     * for each 935
     *      paste in $a-c
     */
    void addConstantFields(String aOrT) {
        for (Title935Record nextTM : titleList) {
            nextTM.addConstantFields(numOccurances, aOrT);
            if (JUNE10) {
                System.out.println("rawString = " + rawString);
                System.out.println("numOccurances = " + numOccurances);
            }
        }
    }

    /**
     * Make any multiple spaces into just one...
     */
    void crushMultipleSpaces() {
        String s = this.getRawString();
        //System.out.println("before: s = " + s);
        while (s.contains("  ")) {
            s=s.replaceAll("  ", " ");
            //System.out.println("after: s = " + s);
        }
        this.setRawString(s);
    }

    public static void main(String[] args) {
        Whole505a whole = new Whole505a("di yi ji-di er ji. Meng liang gu -- di san ji-di si ji. Zhong qiu duo cheng ye -- di wu ji-di qi ji. Feng juan hei tu di -- di ba  ji-di shi ji. Zhong yuan hu xiao tian");
        whole.crushMultipleSpaces();
        System.out.println("whole = " + whole);
    }
}  // Whole505a

