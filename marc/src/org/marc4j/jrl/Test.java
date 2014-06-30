package org.marc4j.jrl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Test.java created by levenick on Dec 18, 2013 at 10:50:48 AM
 */
public class Test {

    static int soundCount, silentCount;
    static TestFrame tf;

    private static boolean messWith(Record record) {
        boolean found = false;
        boolean foundSound = false;
        boolean foundSilent = false;
        boolean foundBSD = false;

        for (VariableField field : record.getVariableFields()) {
            String s = field.toString();
            DataField dataField = (DataField) record.getVariableField("245");
            foundBSD = foundBSD || checkForBSD((DataField) record.getVariableField("300"));

            Subfield subfield = dataField.getSubfield('a');
            String title = "no title?!?!";
            if (subfield != null) {
                title = subfield.getData();
            } else {
                System.out.println(title + dataField + "\n*****\n");
            }

            if (s.toLowerCase().contains("sound") || s.toLowerCase().contains("silent")) {
                found = true;;
            }
            if (s.contains("sound")) {
                foundSound = true;
                soundCount++;
                // get the first field occurence for a given tag
                //spew(dataField.toString() + '\n');

                // retrieve the first occurrence of subfield with code 'a'
                spew("================================ Sound -- Title proper: " + title + "  =============================================");
                spew("\t" + s);
                spew("*********************************");
            }

            if (s.toLowerCase().contains("silent")) {
                foundSilent = true;
                silentCount++;
                spew("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Silent -- Title proper: " + title + "  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                spew("\t" + s);
                spew("*********************************");
            }
        }

        if (foundSilent && !foundBSD) {
            spew("OOP!");
            spew("jrl: record=" + record.toString() + "/jrl");


        }
        return found;
    }

    static void spew(String s) {
        //tf.display(s);
    }

    private static boolean checkForBSD(DataField dataField) {
        //System.out.println("dataField=" + dataField);
        if (dataField == null) {
            return false;
        }
        String s = dataField.toString();
//        if (!s.contains("bsd")) {
//            System.out.println("dataField = " + dataField);
//        }
        return s.contains("bsd");
    }

    private static void tryStuff(Record record) {
        VariableField vf = record.getVariableField("245");
        System.out.println("vf = " + vf);    
    
        DataField df = (DataField) vf;
        Subfield sf = df.getSubfield('a');
        System.out.println("sf = " + sf);
       Subfield sfx = df.getSubfield('x');
        System.out.println("sfx = " + sfx);
    }

    public String toString() {
        String returnMe = "I am a Test, please fill in my variables so I can be debugged.";

        return returnMe;
    }

    public static void main(String args[]) throws Exception {
        int count = 0;
        //tf = new TestFrame();

//        InputStream input = Test.class.getResourceAsStream("../samples/resources/VideosProvisionalSample.mrc");

   //     InputStream input = Test.class.getResourceAsStream("../samples/resources/NewVideosAllFinal.mrc");
        InputStream input = Test.class.getResourceAsStream("../samples/resources/NewVideosSample6.mrc");
        System.out.println("input = " + input);

        int recordCount=0;
        MarcReader reader = new MarcStreamReader(input);
        while (reader.hasNext() && recordCount++ < 10) {
            Record record = reader.next();
            count++;
            //System.out.println("\n\njrl: record=" + record.toString() + "/jrl");
            tryStuff(record);

//            if (messWith(record)) {
////                tf.display("...that from this.." + record.toString() + "\n\n");
//            }
        }

        System.out.println("count=" + count);
        System.out.println("soundCount = " + soundCount);
        System.out.println("silentCount = " + silentCount);
    }
}
