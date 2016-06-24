package jrl2016;

import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static jrl2016.ProcessorGlobals.CREATED_BY_BEGINNING_PART;
import static jrl2016.ProcessorGlobals.CREATED_BY_RESPONSIBLE;
import static jrl2016.ProcessorGlobals.CREATED_BY_SUBTITLE;
import static jrl2016.ProcessorGlobals.CREATED_BY_VARIANT;
import static jrl2016.ProcessorGlobals.PAREN_REGEX;
import static jrl2016.ProcessorGlobals.RESPONSIBLE_DELIMITER;
import static jrl2016.ProcessorGlobals.SUBTITLE_DELIMITER;
import static jrl2016.ProcessorGlobals.TITLE_MAIN;
import static jrl2016.ProcessorGlobals.TITLE_PART;
import static jrl2016.ProcessorGlobals.TITLE_SUBTITLE;
import static jrl2016.ProcessorGlobals.TITLE_VARIANT_ALTERNATIVE;
import static jrl2016.ProcessorGlobals.VARIANT_ALTERNATIVE_DELIMITER;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.rules.Globals;
import org.marc4j.rules.StringList;
import org.marc4j.rules.Utils;

/**
 * The internal form of a 935 field while it is being processed
 *
 * @author levenick May 4, 2016 1:36:37 PM
 */
public class Title935Record {

    private String currentText;
    private String titleType;
    private SubfieldList subfieldList;
    private ExtraTitleList extraTitleList;

    private boolean isAMainTitle = false;
    private int currentE;
    private char nextSubfield;   // for when we generate all the subfields... or will that just be done in a loop? Seems like the part part will use it??
//    private static final boolean MAY18 = false;
//    private static final boolean BUSTED = false;
//    private static final boolean MAY26 = false;
//    private static final boolean JUNE15=false;
    private static final boolean JUNE23 = false;

    public Title935Record() {
        subfieldList = new SubfieldList();
        extraTitleList = new ExtraTitleList();
        nextSubfield = 'g';  // 
        if (currentE == 0) {
            currentE = 1;
        }
    }   //empty default constructor

    public Title935Record(String rawText, String titleType) {   //initializing constructor
        this();   // invoke the default constructor
        this.currentText = rawText;
        this.titleType = titleType;
    }

    Title935Record(String trim, String title, int countMain) { // only called in chain
        this(trim, title);
        isAMainTitle = title.equals(TITLE_MAIN);
//        currentE = 1;
//        subfieldList.add(new SubfieldRecord('d', "" + countMain));
//        subfieldList.add(new SubfieldRecord('e', "" + currentE));  // start with 1, but $e may be incremented if we split
    }

    Title935Record(String trim, String title, int d, int e) { // only called in chain
        this(trim, title, d);
        subfieldList.add(new SubfieldRecord('d', "" + d));
        subfieldList.add(new SubfieldRecord('e', "" + e));  // d (previous line) is what we started from, currentD is increasing
    }

    Title935Record(String trim, String TITLE_MAIN, int d, int e, int whereWasItMade) {
        this(trim, TITLE_MAIN, d, e);
        appendDollar7(whereWasItMade);
    }

    public String getCurrentText() {
        return currentText;
    }

    public SubfieldList getSubFieldList() {
        return subfieldList;
    }

    public void setCurrentText(String rawText) {
        this.currentText = rawText;
    }

    public String toString() {
        String returnMe = "\nI am a Title935Record: ";
        returnMe += "    titleType=" + titleType;
        returnMe += "  main=" + this.isAMainTitle;
        returnMe += "   currentText=" + getCurrentText();
        returnMe += "\n  " + getSubFieldList().toString();
        for (ExtraTitle et : extraTitleList) {
            returnMe += "\n\t" + et.toString();
        }
        return returnMe;
    } // toString()

    void chopResponsible() {
        String[] responsible = currentText.split(RESPONSIBLE_DELIMITER);
        currentText = responsible[0];
        for (int i = 1; i < responsible.length; i++) {
            subfieldList.add(new SubfieldRecord('1', responsible[i]));
            appendDollar7(CREATED_BY_RESPONSIBLE);
        }
    }

    public static void main(String[] args) {
        String returnMe = "abcde....";
        if (returnMe.charAt(returnMe.length() - 1) == PERIOD) {
            returnMe = returnMe.substring(0, returnMe.length() - 1);
        }
        System.out.println("returnMe = " + returnMe);
//        Title935Record tm = new Title935Record("part 3. Third title, no. 1, Beginning = Parallel title / directed by Somebody ; produced by Somebody else. part 4. A final title.", "testTitle");
//        System.out.println("before tm = " + tm);
//        tm.chopResponsible();
//        System.out.println("after tm = " + tm);
    }

//    void splitTitlesAdditional() {
//        for ()
//                String[] additional = rawText.split(";");
//        rawText = additional[0];
//        for (int i=1; i<additional.length; i++) {
//            dollarList.add(new DollarThing('0', additional[i]));
//        }
//
//        
//    }
    /**
     * returns the value of the $d field as an int
     *
     * @return
     */
    int getD() {
        SubfieldRecord dDollar = findDollarD();
        return Integer.parseInt(dDollar.getContents());
    }

    private SubfieldRecord findDollarD() {
        SubfieldRecord returnMe = findDollar('d');
        if (returnMe == null) {
            Globals.fatalError("uh-oh... no $d, and there's gotta be!!");
        }
        return returnMe;
    }

    private SubfieldRecord findDollar1() {
        return findDollar('1');
    }

    /**
     * returns the subfield record of the passed char
     *
     * @param findMe
     * @return
     */
    private SubfieldRecord findDollar(char findMe) {
        for (SubfieldRecord nextDT : subfieldList) {
            if (nextDT.getCh() == findMe) {
                return nextDT;
            }
        }

        return null;
    }

    /**
     * pastes the number of the current processing operation onto the $7 field
     * (so you can tell what's happened!)
     *
     * @param n
     */
    private void appendDollar7(int n) {
        SubfieldRecord the7 = findDollar('7');
        if (the7 == null) {
            subfieldList.add(new SubfieldRecord('7', "" + n));
            ProcessorGlobals.incTransformCount(n);
        } else {
            the7.append(n);
        }
//        for (SubfieldRecord nextDT : subfieldList) {
//            if (nextDT.getCh() == '7') {
//                nextDT.setContents(nextDT.getContents() + "" + n);
//                may18("Appended $7=" + nextDT.getContents());
//                return;
//            }
//        }
//
//        may18("Created $7 =" + n);
    }

//    void may18(String s) {
//        if (MAY18) {
//            System.out.println("May18: " + s);
//        }
//    }
    boolean getIsAMainTitle() {
        return isAMainTitle;
    }

    int incAndReturnNextE() {
        currentE++;
        return currentE;
    }

    /**
     * Like it says. Used to write subfields sequentially.
     *
     * @return
     */
    char incAndReturnNextCh() {
        return nextSubfield++;
    }

    /**
     * Trims off prefixes (using that big, horrible list of regexes Splits
     * subtitles (into the extraTitleList) Splits variant titles (also intot
     * that extraTitleList)
     *
     * @param myParent
     */
    public void splitParts(Whole505a myParent) {
        String buffer = this.getCurrentText();
        buffer = splitPartsInFront(buffer);

        buffer = splitSubtitlesAndTitleVariantAlternative(buffer, myParent);
        //may18("\n******************^^^^^^^^^^^^^^**************\nbuffer = " + buffer);
        this.setCurrentText(buffer);
        //splitPartsAtEnd(buffer);
    }

    /**
     * Grabs "pt. 1," or "Part one," or "part II." from the front of the current
     * text of this title and moves each into the next $subfield, followed by
     * TITLE_PART in the next subfield
     *
     * @return remaining String after the part parts are removed
     */
    private String splitPartsInFront(String buffer) {
        /*
        while (more parts in front) {
            write $next as pt. 1 (or whatever)
            partString = what's between there and the next part
            delete to end of partString from  mainTitle string  
            write $next as partString
         */

        //may18("splitPartsInFront(): currentText to start=buffer = " + buffer);
        return stripParts(buffer, TITLE_PART, Processor.getRegexlist());
    }

    private String stripParts(String buffer, String nameOfTitle, StringList theRegexList) {
        String returnMe = matchUpTheFronts(theRegexList, buffer, nameOfTitle);

        return returnMe;
    }

    /**
     * As long as we find a prefix to trim off initialize a MatchRecord for
     * MyMatcher to use if it finds a prefix output it to the next subfield pair
     *
     * @param theRegexList
     * @param s
     * @param nameOfTitle
     * @return
     */
    boolean fooleryWithPeriods = true;
    public static final char PERIOD = '.';

    String matchUpTheFronts(StringList theRegexList, String s, String nameOfTitle) {
        String returnMe = s;
        boolean pastedAPeriod = false;
        if (fooleryWithPeriods) {
            if (!s.isEmpty() && s.charAt(s.length() - 1) != '.') {
                s += PERIOD;  // paste on a trailing period
                pastedAPeriod = true;
            }
        }
        MatchRecord theMatchRecord = MyMatcher.matchAny(theRegexList, s);
        while (theMatchRecord != null) {
            //june15("Matched: before choppage, theMatchRecord = " + theMatchRecord);
            theMatchRecord.splitUsingRegex();
            theMatchRecord.moveTrailingAlpha();
            //june15("split, theMatchRecord = " + theMatchRecord);
            theMatchRecord.trimTrailingPunct();
            //june15("trimmedTrailing, theMatchRecord = " + theMatchRecord + "\n");

            //System.out.println("after choppage, theMatchRecord = " + theMatchRecord);
            addPair(theMatchRecord.getWhatMatched(), nameOfTitle);
            this.appendDollar7(CREATED_BY_BEGINNING_PART);
            returnMe = theMatchRecord.getOriginal();
            //may26("adding =>" + theMatchRecord.getWhatMatched() + "<=" + "returning-->" + returnMe + "<--");
            Processor.incCountPart();
            theMatchRecord = MyMatcher.matchAny(theRegexList, theMatchRecord.getOriginal());
        }

        if (fooleryWithPeriods && pastedAPeriod && !returnMe.isEmpty()) {
            if (returnMe.charAt(returnMe.length() - 1) == PERIOD) {
                returnMe = returnMe.substring(0, returnMe.length() - 1);
            }
        }

        return returnMe;
    }

    /*private String oldstripParts(String buffer, String nameOfTitle, String theRegex) {
        String returnMe = "";
        Pattern p = Pattern.compile(theRegex);
        // get a matcher object
        Matcher matcher = p.matcher(buffer.toLowerCase());

        ArrayList<Point> list = new ArrayList<Point>();
        boolean found = false;
        while (matcher.find()) {  // make a list of the start and end of each part part
            list.add(new Point(matcher.start(), matcher.end()));
        }

        if (list.isEmpty()) {
            return buffer;
        }

        for (int i = 0; i < list.size(); i++) {
            Point nextP = list.get(i);
            addPair(buffer.substring(nextP.x, nextP.y), nameOfTitle);
            appendDollar7(CREATED_BY_BEGINNING_PART);
            int endS = 0;
            if (i + 1 < list.size()) {
                endS = (int) list.get(i + 1).getX();
            } else {
                endS = buffer.length();
            }
            returnMe = buffer.substring(nextP.y);
            this.setCurrentText(returnMe);
            //System.out.println("Bottom of the loop: returnMe = " + returnMe);
            busted("Now the subfieldList is: " + subfieldList.toString());
        }

        return returnMe;
    }*/
    /**
     * Adds a pair of subfields, the first the value, the second the name (like
     * TitleAdditional)
     *
     * @param valueOfField -- the value
     * @param type -- the type
     */
    void addPair(String valueOfField, String type) {
        subfieldList.add(new SubfieldRecord(this.incAndReturnNextCh(), valueOfField)); // the partpart
        subfieldList.add(new SubfieldRecord(this.incAndReturnNextCh(), type));                        // the kind of title
    }

    void addLiteralSubfield(char ch, String value) {
        subfieldList.add(new SubfieldRecord(ch, value));                // the partpart
    }

//    void busted(String s) {
//        if (BUSTED) {
//            System.out.println("first " + s);
//        }
//    }
    private String splitSubtitlesAndTitleVariantAlternative(String buffer, Whole505a myParent) {
        //busted("top of splitPartsInMiddle: buffer=" + buffer);
        buffer = subtitleSplit(buffer, SUBTITLE_DELIMITER, TITLE_SUBTITLE, myParent);
        //busted("after splitting off the subtitle: buffer=" + buffer);
        buffer = variantSplit(buffer, myParent);
//        buffer = splitMiddlePartParts(buffer);

        return buffer;
    }

    private String variantSplit(String buffer, Whole505a myParent) {  // add myParent!!
        //busted("top of splitPartsAtEnd: buffer=" + buffer);

        String[] pieces = buffer.split(VARIANT_ALTERNATIVE_DELIMITER);
        buffer = pieces[0];
        for (int i = 1; i < pieces.length; i++) {
            String s = pieces[i];
            addAnotherTitleField(VARIANT_ALTERNATIVE_DELIMITER + " " + s, TITLE_VARIANT_ALTERNATIVE, CREATED_BY_VARIANT);

//            this.addPair(VARIANT_ALTERNATIVE_DELIMITER + " " + s, TITLE_VARIANT_ALTERNATIVE);
//            appendDollar7(CREATED_BY_VARIANT);
        }
        return buffer;
    }

    private String subtitleSplit(String buffer, String splitter, String titleType, Whole505a myParent) {
        String[] subtitles = buffer.split(splitter);
        this.setCurrentText(subtitles[0]);  // if we find nothing, this will be the whole thing... but, if there are splitters it will just be what's before the first one
        for (int innerI = 1; innerI < subtitles.length; innerI++) {
            String nextTitle = subtitles[innerI].trim();
            addAnotherTitleField(nextTitle, titleType, CREATED_BY_SUBTITLE);
//            this.addPair(nextTitle, titleType);
//            appendDollar7(CREATED_BY_SUBTITLE);
        }
        if (subtitles.length == 1) {
            return buffer;
        }
        if (buffer.indexOf(splitter.charAt(0)) == -1) {
            System.out.println("This *should* be impossible... buffer = " + buffer);
            System.out.println("splitter = " + splitter);
        }

        buffer = buffer.substring(0, buffer.indexOf(splitter.charAt(0)));  // this could be a bug... it assumes the splitter is just :
//        if (subtitles.length > 2) {
//            System.out.println("Um... multiple subtitles... ... is that okay??" + "getCurrentText()===>" + getCurrentText());
//        }

        return buffer;
    }

    /**
     * Move anything inside ()'s to $0 fields and delete
     */
    void moveFromParens() {
        String buffer = this.getCurrentText();
        if (JUNE23) {
            System.out.println("MOVE FROM PARENS currentText to start=buffer = " + buffer);
        }
        Pattern p = Pattern.compile(PAREN_REGEX);
        // get a matcher object
        Matcher matcher = p.matcher(buffer.toLowerCase());

        ArrayList<Point> list = new ArrayList<Point>();
        while (matcher.find()) {  // make a list of the start and end of each part part
            list.add(new Point(matcher.start(), matcher.end()));
        }

        for (int i = 0; i < list.size(); i++) {
            Point nextP = list.get(i);
            subfieldList.add(new SubfieldRecord('0', buffer.substring(nextP.x, nextP.y)));
            this.appendDollar7(ProcessorGlobals.CREATED_BY_PARENS);
        }

        this.setCurrentText(buffer.replaceAll(PAREN_REGEX, ""));
        //busted("Having removed stuff in parens the subfieldList is: " + subfieldList.toString());
        nowMoveFromParensInSubtitlesAndThings();
    }

    void nowMoveFromParensInSubtitlesAndThings() {
        for (ExtraTitle nextExtraTitle : extraTitleList) {
            /* this is copied from above, and it should be combined, but I'm tired!! */
            String buffer = nextExtraTitle.getTitle();

            Pattern p = Pattern.compile(PAREN_REGEX);
            // get a matcher object
            Matcher matcher = p.matcher(buffer.toLowerCase());

            ArrayList<Point> list = new ArrayList<Point>();
            while (matcher.find()) {  // make a list of the start and end of each part part
                list.add(new Point(matcher.start(), matcher.end()));
            }

            for (int i = 0; i < list.size(); i++) {
                Point nextP = list.get(i);
                subfieldList.add(new SubfieldRecord('0', buffer.substring(nextP.x, nextP.y)));
                this.appendDollar7(ProcessorGlobals.CREATED_BY_PARENS);
            }

            nextExtraTitle.setTitle(buffer.replaceAll(PAREN_REGEX, ""));
        }
    }

    /**
     * move anything in the $1 field in parens to $0 this is just like
     * moveFromParens, except we are looking in $0 instead of the whole thing
     * they obviously should be combined, but my head hurts!!
     *
     * @param dollar1Text
     */
    void moveFromParensInsideDollar1(String dollar1Text) {
        String buffer = dollar1Text;
        //busted("MOVE FROM $1 PARENS currentText to start=buffer = " + buffer);
        Pattern p = Pattern.compile(PAREN_REGEX);
        // get a matcher object
        Matcher matcher = p.matcher(buffer.toLowerCase());

        ArrayList<Point> list = new ArrayList<Point>();
        while (matcher.find()) {  // make a list of the start and end of each part part
            list.add(new Point(matcher.start(), matcher.end()));
        }

        for (int i = 0; i < list.size(); i++) {
            Point nextP = list.get(i);
            subfieldList.add(new SubfieldRecord('0', buffer.substring(nextP.x, nextP.y)));
            this.appendDollar7(ProcessorGlobals.CREATED_BY_PARENS);
        }

        //busted("Before $1Text=" + this.getDollar1Text());
        this.setDollar1Text(buffer.replaceAll(PAREN_REGEX, ""));
        //busted("After $1Text=" + this.getDollar1Text());

        //busted("...InsideDollar1: Having removed stuff in parens the subfieldList is: " + subfieldList.toString());
    }

    public String getDollar1Text() {
        return this.subfieldList.getSubfieldValue('1');
    }

    public void setDollar1Text(String buffer) {
        this.subfieldList.setSubfieldValue('1', buffer);
    }

    boolean hasDollar1() {
        return findDollar1() != null;
    }

    void transferDollar1To(Title935Record nu935) {
        SubfieldRecord it = findDollar1();
        subfieldList.remove(it);
        nu935.getSubFieldList().add(it);

        // now fix up the $7s...
        nu935.appendDollar7(CREATED_BY_RESPONSIBLE);
        removeDollar7(CREATED_BY_RESPONSIBLE);
    }

    private void removeDollar7(int removeMe) {
        SubfieldRecord dollar7 = this.findDollar('7');
        dollar7.setContents(dollar7.getContents().replaceFirst("" + removeMe, ""));
    }

//    private void may26(String s) {
//        if (MAY26) {
//            System.out.println(s);
//        }
//    }
    /**
     * Now that all the processing is done move whatever remains of the original
     * text along with the type of this title, into the next two subfields
     * ...and then all the other titles that are hanging around NB: they are
     * output in the order they were in originally (i.e. in the original text)
     */
    void convertTitlesToSubfields() {
        this.addPair(currentText, titleType);
        for (ExtraTitle nextTitle : extraTitleList) {
            String title = nextTitle.getTitle();
            String titleType = nextTitle.getTitleType();
            this.addPair(title, titleType);
            int createdBy = nextTitle.getCreatedBy();
            this.appendDollar7(createdBy);
        }

    }

    /**
     * convert the internal format (i.e. the Title935Record record) to mrc
     * format, paste onto the original mrc record, and output
     *
     * @param theMarcWriter
     * @param theRecord
     */
    void convertToMRC_Format(Record theRecord) {
        MarcFactory factory = MarcFactory.newInstance();
        DataField theField = factory.newDataField(Utils.asTag(935), ' ', ' ');

        for (SubfieldRecord nextSubField : subfieldList) {
            Utils.addSubField(theRecord, theField, nextSubField.getCh(), nextSubField.getContents());
        }
        theRecord.addVariableField(theField);
    }

    void addConstantFields(int numOccurances, String aOrT) {
        addA();
        addB(numOccurances);
        addC(aOrT);
    }

    private void addA() {
        addLiteralSubfield('a', "505");
    }

    private void addB(int numOccurances) {
        addLiteralSubfield('b', "" + numOccurances);
    }

    private void addC(String aOrT) {
        addLiteralSubfield('c', aOrT);
    }

    private void addAnotherTitleField(String title, String titleType, int createdBy) {
        this.extraTitleList.add(new ExtraTitle(title, titleType, createdBy));

    }

//    private void june15(String s) {
//        if (JUNE15)
//            System.out.println("June 15: " +s);
//    }
}  // TileMain

