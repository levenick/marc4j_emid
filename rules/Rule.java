package org.marc4j.rules;

import java.net.URL;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Rule.java created by levenick on May 14, 2014 at 9:46:37 AM A rule has a
 * condition and an action. Each is a list. If all the conditions match a
 * particular record, then all the actions generate subfields in a new field
 * (e.g. 962)
 */
public class Rule {

    private ConditionList conditionList;
    private ActionList actionList, actionListAdditional;

    static int serial = 0;
    /**
     * The rule set this Rule is in
     */
    private static RuleSet theRuleSet;

    int serialNumber;
    int usages;
    private Condition matchedCondition;  // the condition that matched (or null if not)

    /**
     * Constructs a Rule from a String
     *
     * @param header -- the format for the rule
     * @param s -- input String of the form LDR/06,g,008/33,v,008/18-20,!=
     * 000-999,,,unspecified,total,,correct
     *
     * All the actions are in the ActionList initially, but if there is a # at
     * the start of any, it means from there on is a second field, so those are
     * moved into actionListAdditional...
     */
    Rule(Header header, String s) {
        serialNumber = serial;
        serial++;
        Globals.initRuleDebug("Making rule " + serialNumber + " from s=" + s + " using " + header);
        String[] values = s.split("\t", header.getNumberValues());
        for (int i = 0; i < values.length; i++) {
            Globals.initRuleDebug("\ti=" + i + " >" + values[i] + "<");
        }

        conditionList = buildConditionList(values, header);
        actionList = buildActionList(values, header);
        Globals.debugDouble("the original action list: " + actionList.toString());
        actionListAdditional = buildActionListAdditional(actionList);
        Globals.debugDouble("the trimmed action list: " + actionList.toString());
        Globals.debugDouble("the ADDITIONAL action list: " + actionListAdditional.toString());

    }

    /**
     * Constructs a list of conditions, all of which must be true for this rule
     * to fire Each condition has a where (which field/subfield) and a what (the
     * pattern it must match)
     *
     * @param values -- s.split(",") of the input
     * @param header -- format of the rule
     * @return -- the list!
     */
    ConditionList buildConditionList(String[] values, Header header) {
        ConditionList returnMe = new ConditionList();
        for (int i = 0; i < header.getNumberConditions(); i++) {
            if (values[i * 2].length() > 0) {
                returnMe.add(Condition.instantiate(values[i * 2], values[i * 2 + 1]));
            }
        }

        return returnMe;
    }

    /**
     * Starting after the where/whats at the beginning, use the remaining header
     * fields along with any non-blank "what to output" fields, to build
     * conditions to put on the list
     *
     * @param values
     * @param header
     * @return the actions list!
     */
    ActionList buildActionList(String[] values, Header header) {
        ActionList returnMe = new ActionList();

        int count = 0;
        int startIndex = header.getNumberConditions() * 2;

        for (Action nextAction : header.getListOfOutputs()) {
            String outputIndicator = values[startIndex + count].trim();
            if (outputIndicator.length() > 0) { // i.e. if there is anything there!
                returnMe.add(new Action(nextAction.getField(), nextAction.getSubField(), outputIndicator));
            }
            count++;
        }

        return returnMe;
    }

    /**
     * Build the action list for when there is a second 96x record to write
     * Especially for col. tinted with col. sequences... First copy all the
     * Actions past the # to returnMe Second delete them from the original
     *
     * @param actionList
     * @return
     */
    private ActionList buildActionListAdditional(ActionList actionList) {
        ActionList returnMe = new ActionList();

        if (needToSplitIt(actionList)) {
            int count = 0;
            boolean splitting = false;
            for (Action nextAction : actionList) {    // make the other list starting with #          
                if (nextAction.saysNeedToSplit()) {
                    splitting = true;
                    nextAction.stripDoubleFlag();
                }
                if (splitting) {
                    returnMe.add(nextAction);
                    count++;
                }
            }

            for (int i = 0; i < count; i++) {  // trim that many
                actionList.remove(actionList.size() - 1);
            }
        }

        return returnMe;
    }

    /**
     * if there's a # starting an action, the rest of the actions go in a second
     * (or subsequent) Field
     *
     * @param list
     * @return
     */
    private boolean needToSplitIt(ActionList list) {
        for (Action nextAction : list) {
            if (nextAction.saysNeedToSplit()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to match every condition of the rule to the record
     *
     * @param theRecord
     * @return true if every condition matches
     */
//    public static int count = 0;
//    public static int spacesCount = 0;
//    public static int dashesCount = 0;
//    public static int otherCount = 0;
    public boolean matches(Record theRecord) {
        Globals.setCurrentRule(this);
        matchedCondition = null;
        Condition lastCondition = null;
        for (Condition nextCondition : conditionList) {
            lastCondition = nextCondition; // for use after the loop
            //Globals.initMatchDebug("trying " + nextCondition.toString());
            if (specialCase650SoSkip(theRecord, nextCondition)) {
                continue;
            }
            if (!nextCondition.matches(theRecord)) {
                return false;
            }
        }

        /* At this point all the Conditions in the list matched... 
         ...so...
         the last one was the data and what it found is in Globals.matchedDataFromSubField!
         */
        matchedCondition = lastCondition;
        Globals.incCounter();
        Globals.initMatchDebug("matchedCondition=" + matchedCondition + " and... the data it matched is:"
                + Globals.getMatchedDataFromSubfield());
        return true;
    }

    /**
     * Transforms the record by adding a field Tag 96x from the rule $a-$e where
     * it was found $? - from the various actions Mostly there is just one field
     * per action, but if there are more that is indicated by a # (see Globals:
     * public static char SPLIT_CHAR = '#';) as the first char of the action
     * output, in that case the remainder of outputs are stored in
     * actionListAdditional If that's not empty, this creates a second record
     * with values from those Plus... If there are multiple parts (like 4
     * cartridges (13 minutes ea.) we need to generate 5 (count 'em, five)
     * fields blah blah total blah blah part 1 blah blah part 2 blah blah part 3
     * blah blah part 4 which makes kind of a mess of the code (since it was
     * designed to have a rule have a single condition/action pair... details!
     *
     * @param theRecord - the record to transform
     */
    public void transform(Record theRecord) {
        int targetField = getTheRuleSet().getTargetField();
        MarcFactory factory = MarcFactory.newInstance();
        calcRepetitionsAndStoreInGlobals(theRecord);
//        if (itsNothing()) {
//            return;
//        }
        createAndAddNewFields(actionList, theRecord, targetField, factory);
        if (twoOrMoreFieldsToOutput()) {
            Globals.setPartNumber(-17);
            if (!needPartsFields()) {
                createAndAddNewFields(actionListAdditional, theRecord, targetField, factory); // the second field
            } else {
                dealWithMultipleParts(theRecord, targetField, factory); // the parts fields!
            }
        }
    }

//    /**
//     * Um... in Color3 there's a condition; only apply this rule if there is no
//     * 650$v| 650$x| 650$y| 650$z field So, uh... there's a rule that matches
//     * *anything* in those fields which prevents it (the previous) from firing
//     * ...but... it should do nothing... this implements that
//     *
//     * @return
//     */
//    private boolean itsNothing() {
//        String outValue = actionList.get(0).getOutputValue();
//        if (outValue.equals("nothing")) {
//            return true;
//        }
//        return false;
//    }
    private boolean twoOrMoreFieldsToOutput() {
        return !actionListAdditional.isEmpty();
    }

    /**
     * If there are not multiple parts (like 4 cartridges (13 minutes ea.) only
     * one field is needed, the action output words are "partMinutes", etc NB:
     * this depends on those appear FIRST, and why are they hard-coded here?
     * Um... cuz I'm outta time to do it nicely!
     *
     * @return
     */
    private boolean needPartsFields() {
        String outValue = actionListAdditional.get(0).getOutputValue();
        if (outValue.equals("partMinutes") || outValue.equals("2partMinutes") || outValue.equals("3partMinutes") || outValue.equals("partHours") || outValue.equals("partHMS")) {
            return true;
        }
        return false;
    }

    /**
     * Need multiple fields for the whole and each part, gah! The whole was done
     * before, now need the parts fields. First, figure out how many, then emit
     * on field for each The tricky part is "What part number are we on?"!
     *
     * @param theRecord
     * @param targetField
     * @param factory
     */
    private void dealWithMultipleParts(Record theRecord, int targetField, MarcFactory factory) {
        for (int i = 1; i <= Globals.getRepetitions(); i++) {
            Globals.setPartNumber(i);
            createAndAddNewFields(actionListAdditional, theRecord, targetField, factory);
        }
    }

    /**
     * Calculates how many parts. There are two cases, 1) if it's from 7 disks
     * (13 min. ea.) then there are 7, if it's from (12, 12 min.), there are
     * 2... but here, we know it's the first case
     *
     * @param theRecord
     * @return how many parts!
     */
    private void calcRepetitionsAndStoreInGlobals(Record theRecord) {
        int reps = -1;
        if (twoOrMoreFieldsToOutput() && needPartsFields()) {
            String outValue = actionListAdditional.get(0).getOutputValue(); // 
            if (outValue.equals("2partMinutes")) {
                reps = 2;
            } else if (outValue.equals("3partMinutes")) {
                reps = 3;
            } else {
                reps = calcMysteryReps(theRecord);
            }
        }

        Globals.setRepetitions(reps);
    }

    private int calcMysteryReps(Record theRecord) {
        String whatMatched = Globals.getMatchedDataFromSubfield();
        int returnMe = Utils.extractFirstInt(whatMatched);  // assumes it something like 7 videocassettes (12 min. each)
        return returnMe;
    }

    private void createAndAddNewFields(ActionList actionList, Record theRecord, int targetField, MarcFactory factory) {
        DataField theField = factory.newDataField(Utils.asTag(targetField), ' ', ' ');

        addSubfieldA(theRecord, theField);
        addSubfieldB(theRecord, theField);
        if (matchedCondition.hasSubfield()) {
            addSubfieldC(theRecord, theField);
            addSubfieldD(theRecord, theField);
            addSubfieldE(theRecord, theField);
        } else if (matchedCondition.getFieldNumber() < 10) {
            //addControlFieldC(theRecord, theField);
            addColumnC(theRecord, theField);
        }
        for (Action nextAction : actionList) {
            if (nextAction.getOutputValue().length() > 0) {
                theField.addSubfield(factory.newSubfield(nextAction.getSubField(), nextAction.createActualOutputValue(matchedCondition)));
            }
        }
        addSubfieldR(theRecord, theField); // rule indicator
        theRecord.addVariableField(theField);

    }

    public void incUsages() {
        usages++;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public static void main(String[] args) {
        URL u = Rule.class.getResource("resources/data");
        System.out.println("u = " + u);
        String f = u.getFile();
        String p = u.getPath();
        System.out.println("f = " + f);
        System.out.println("p = " + p);
    }

    private String dataSourceFieldNumber() {
        return Utils.asTag(matchedCondition.getFieldNumber());
    }

    private String dataSourceOccuranceNumber() {
        return "" + matchedCondition.getRepeatField();
    }

    private char dataSourceSubfieldChar() {
        return matchedCondition.getMostRecentSubFieldChar();
    }

    private int dataSourceControlFieldCol() {
        return Globals.getMostRecentControlFieldCol();
    }

    private String dataSourceSubfieldOccuranceNumber() {
        return "" + matchedCondition.getRepeatSubfield();
    }

    private void addSubfieldA(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'a', dataSourceFieldNumber());
    }

    private void addSubfieldB(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'b', dataSourceOccuranceNumber());
    }

    private void addSubfieldC(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'c', "" + dataSourceSubfieldChar());
    }

    private void addSubfieldD(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'd', dataSourceSubfieldOccuranceNumber());
    }

    private void addSubfieldE(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'e', Globals.getCounter());
    }

    private void addColumnC(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'c', "" + dataSourceControlFieldCol());
    }

    private void addSubfieldR(Record theRecord, DataField theField) {
        Utils.addSubField(theRecord, theField, 'r', theRuleSet.getShortInputFileName() + " R" + this.serialNumber);
    }

    public String toString() {
        String returnMe = "\nRule " + serialNumber + ":\t";

        returnMe += conditionList.toString() + actionList.toString();
        if (!actionListAdditional.isEmpty()) {
            returnMe += " additional!" + actionListAdditional.toString();
        }

        return returnMe;
    }

    public String toStringBrief() {
        String returnMe = "\nRule " + serialNumber + ":\t";

        returnMe += "Conditions: " + conditionList.toStringBrief() + "\n\tActions: " + actionList.toStringBrief();
        if (!actionListAdditional.isEmpty()) {
            returnMe += " \n\tadditional!" + actionListAdditional.toStringBrief();
        }
        return returnMe;
    }

    void resetUsages() {
        usages = 0;
    }

    private boolean specialCase650SoSkip(Record theRecord, Condition nextCondition) {
        String whatWeAreLookingFor = nextCondition.getTheConditionDataRequired().getDataValueRequired();
        if (whatWeAreLookingFor.equals("special650")) {
            if (theRecord.getVariableField("650") != null && anyOfThoseFieldsExist(theRecord, nextCondition)) {
                return true;
            }
            nextCondition.getTheConditionDataRequired().setDataValueRequired("Silent films");
            return false;
        }
        return false;
    }

    private boolean anyOfThoseFieldsExist(Record theRecord, Condition nextCondition) {
        VariableField vf = theRecord.getVariableField("650");
        if (vf == null) {
            System.out.println("ieeee! theRecord = " + theRecord);
            System.out.println("nextCondition = " + nextCondition);
            System.out.println("what matched=" + Globals.getMatchedDataFromSubfield());
        }
        DataField df = (DataField) vf;
        Subfield sf;
        sf = df.getSubfield('v');
        if (sf != null) {
            return true;
        }
        sf = df.getSubfield('x');
        if (sf != null) {
            return true;
        }
        sf = df.getSubfield('y');
        if (sf != null) {
            return true;
        }
        sf = df.getSubfield('z');
        if (sf != null) {
            return true;
        }

        return false;  // didn't find any
    }

    public static RuleSet getTheRuleSet() {
        return theRuleSet;
    }

    public static void setRuleSet(RuleSet rs) {
        theRuleSet = rs;
    }

    /**
     * resets the serial number for a new ruleset
     */
    public static void resetSerial() {
        serial = 0;
    }

    public int getUsages() {
        return usages;
    }
}
