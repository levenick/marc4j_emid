package org.marc4j.jrl;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Foo.java created by levenick on May 13, 2014 at 2:20:13 PM
 */
public class Foo {

    public static void main(String[] args) {
        String input = "(961$f)";

        Pattern pattern = Pattern.compile("\\d\\d\\d");
        Matcher matcher = pattern.matcher(input);

        boolean found = false;
        while (matcher.find()) {
            System.out.println("I found the text "
                    + matcher.group() + " starting at "
                    + matcher.start() + " and ending at index " + matcher.end());
            found = true;
        }
        if (!found) {
            System.out.println("No match found.%n");
        }
    }
}

//    public static void main(String[] args){
//        Console console = System.console();
//        if (console == null) {
//            System.err.println("No console.");
//            System.exit(1);
//        }
//        while (true) {
//
//            Pattern pattern = 
//            Pattern.compile(console.readLine("%nEnter your regex: "));
//
//            Matcher matcher = 
//            pattern.matcher(console.readLine("Enter input string to search: "));
//
//            boolean found = false;
//            while (matcher.find()) {
//                console.format("I found the text" +
//                    " \"%s\" starting at " +
//                    "index %d and ending at index %d.%n",
//                    matcher.group(),
//                    matcher.start(),
//                    matcher.end());
//                found = true;
//            }
//            if(!found){
//                console.format("No match found.%n");
//            }
//        }
//    }

