package jrl2016;

import java.io.File;
import static org.marc4j.rules.Globals.getRootPath;
import org.marc4j.rules.MyReader;

/**
 * The various constants
 *
 * @author levenick May 25, 2016 9:11:23 AM
 */
public class ProcessorGlobals {

    public final static String PAREN_REGEX = "\\(.*?\\)";
    public final static String PART_REGEX = "(pt. |part )(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|i |ii|iii|iv|v|vi|vii|viii|ix|x)(\\.|,)";
    public final static String TITLE_MAIN_REGEX = "---|--|- -";

    public final static String TITLE_MAIN = "TitleMain";
    public final static String TITLE_PART = "TitlePartNameOrNumber";
    public final static String TITLE_SUBTITLE = "TitleSubtitle";
    public final static String TITLE_VARIANT_ALTERNATIVE = "TitleVariantAlternative";
    public final static String TITLE_VARIANT_PARALLEL = "TitleVariantParallel";
    public final static String TITLE_ADDITIONAL = "TitleAdditional";
    public final static String DOLLAR1_PART = "TitleUnmarked";

    public final static String SUBTITLE_DELIMITER = ":";
    public final static String VARIANT_ALTERNATIVE_DELIMITER = "or,";
    public final static String PARALLEL_DELIMITER = "=";
    public final static String ADDITIONAL_DELIMITER = ";";
    public final static String RESPONSIBLE_DELIMITER = "/";

    public final static int CREATED_BY_SPLITMAIN = 1;
    public final static int CREATED_BY_RESPONSIBLE = 2;
    public final static int CREATED_BY_PARALLEL = 3;
    public final static int CREATED_BY_ADDITIONAL = 4;
    public final static int CREATED_BY_UNMARKED = 5;
    public final static int CREATED_BY_BEGINNING_PART = 6;
    public final static int CREATED_BY_SUBTITLE = 7;
    public final static int CREATED_BY_PARENS = 8;
    public final static int CREATED_BY_VARIANT = 9;
    public final static int NUM_TRANSFORMS = CREATED_BY_VARIANT;

    private static int[] transformCounts;
    private static String trailingPunctuation = ".";

    public static String PROCESSING_TYPE_BASIC = "a";
    public static String PROCESSING_TYPE_ENHANCED_AS_BASIC = "t";

    public static void incTransformCount(int n) {
        transformCounts[n]++;
    }

    public static String transformKey() {
        String returnMe = "Here's what the $7 fields mean:\n";

        returnMe += "\tCREATED_BY_SPLITMAIN = 1\n";
        returnMe += "\tCREATED_BY_RESPONSIBLE = 2\n";
        returnMe += "\tCREATED_BY_PARALLEL = 3\n";
        returnMe += "\tCREATED_BY_ADDITIONAL = 4\n";
        returnMe += "\tCREATED_BY_UNMARKED = 5\n";
        returnMe += "\tCREATED_BY_BEGINNING_PART = 6\n";
        returnMe += "\tCREATED_BY_SUBTITLE = 7\n";
        returnMe += "\tCREATED_BY_PARENS = 8\n";
        returnMe += "\tCREATED_BY_VARIANT = 9\n";
        return returnMe;
    }

    public static void main(String[] args) {
        System.out.println(transformKey());
    }

    public static void init() {
        initCounts();
        initTrailingPunctuation();
    }

    public static void initCounts() {
        transformCounts = new int[NUM_TRANSFORMS + 1];
    }

    public static String toStringTransformCounts() {
        String returnMe = "Counts of transforms:";
        for (int i = 1; i <= NUM_TRANSFORMS; i++) {
            returnMe += "\n\t" + transformCounts[i];
        }
        return returnMe + "\n";
    }

    public static void initTrailingPunctuation() {
        MyReader mr = new MyReader(getRootPath() + File.separatorChar + "patterns" + File.separatorChar + "trailingPunctuation.txt");
        String s = mr.giveMeTheNextLine();
        System.out.println("trailing punctuation = " + s);
        trailingPunctuation = s;
    }

    public static String getTrailingPunctuation() {
        return trailingPunctuation;
    }
}
