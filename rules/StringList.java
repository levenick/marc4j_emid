package org.marc4j.rules;

import java.util.ArrayList;

/**
 * StringList.java created by levenick on May 26, 2014 at 3:20:17 PM
 */
public class StringList extends ArrayList<String> {

    public StringList() {
    }

    /**
     * Reads each line of the path into the StringList
     * Adds a ^ to the beginning of each so they only match things at the front
     *
     * @param filename
     */
    public StringList(String path, boolean addHat) {
        MyReader mr = new MyReader(path);
        while (mr.hasMoreData()) {
            String s = mr.giveMeTheNextLine();
            if (!s.trim().isEmpty()) {

                if (addHat) {
                    if (s.charAt(0)=='^') {
                        System.out.println("Oops, already a ^ in front!");
                        add(s);
                } else {
                        add("^" + s);
                    }
                } else {
                    add(s);
                }
            }
        }
    }

}
