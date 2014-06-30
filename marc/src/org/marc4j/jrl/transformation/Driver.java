package org.marc4j.jrl.transformation;

import java.io.InputStream;
import java.util.Collections;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.rules.OneRecordFrame;
import org.marc4j.rules.StringCounterList;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.marc4j.rules.Globals;
import org.marc4j.rules.Rule;
import org.marc4j.rules.RuleSet;

/**
 * Driver.java created by levenick on May 16, 2014 at 8:50:47 AM
 */
public class Driver {

    public static void main(String[] args) {
        //InputStream input = Driver.class.getResourceAsStream("../../samples/resources/NewVideosAllFinal.mrc");
        InputStream input = Driver.class.getResourceAsStream("resources/testOut2.mrc");
        MarcReader reader = new MarcStreamReader(input);

        //Globals.setRuleFileName("resources/Duration.csv");
        Globals.setRuleFileName("resources/MovingImageFormats.csv");
        
        RuleSet list = new RuleSet(Rule.class.getResourceAsStream(Globals.getRuleFileName()));
        System.out.println("rules!\n" + list);
        //System.exit(1);
        
        int count = 0;
        int errorCount = 0;
        StringCounterList errorList = new StringCounterList();

        while (reader.hasNext()) {
            count++;
            Record aRecord = reader.next();

            if (Globals.initMatchDebug()) {
                new OneRecordFrame(aRecord, "Before: Record " + count);
            }
            //try {
                transform(aRecord, list);
//            } catch (Exception e) {
//                errorCount++;
//                new OneRecordFrame(aRecord, "Busted: Record " + count);
//
//                System.out.println("count=" + count + "the record is: " + aRecord + " and the exception is" + e);
//                VariableField gack = aRecord.getVariableField("300");
//                if (gack != null) {
//                    DataField foo = (DataField) gack;
//                    errorList.add(foo.getSubfield('a').toString());
//                }
//                errorList.add(Globals.getRecordDataValue());
//
//            }
            if (Globals.initMatchDebug()) {
                new OneRecordFrame(aRecord, "After: Record " + count);
            }

        }
        reportUsages(list);

        System.out.println("That's all folk! Count=" + count + " errorCount=" + errorCount);
        Collections.sort(errorList);
        System.out.println("errorList = " + errorList);
//        System.out.println("Rule.count = " + Rule.count);
//        System.out.println("Rule.spacesCount = " + Rule.spacesCount);
//        System.out.println("Rule.dashesCount = " + Rule.dashesCount);
//        System.out.println("Rule.otherCount = " + Rule.otherCount);
    }

    private static void transform(Record theRecord, RuleSet ruleList) {
        //TransformationFrame theFrame = new TransformationFrame(theRecord, ruleList);
        for (Rule nextRule : ruleList) {
            if (nextRule.matches(theRecord)) {
                Globals.initMatchDebug("\n\n\tmatched! \tmatched! \tmatched! " + nextRule.toString());
                nextRule.transform(theRecord);
                nextRule.incUsages();
            }
        }
    }

    public  static void reportUsages(RuleSet list) {
        for (Rule nextRule : list) {
            int usages = nextRule.getUsages();
            if (usages > 0) {
                System.out.println("R" + nextRule.getSerialNumber() + " used " + usages + " times");
            }
        }
    }
}
