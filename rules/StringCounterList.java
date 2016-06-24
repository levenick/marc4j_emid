package org.marc4j.rules;

import java.util.ArrayList;

/**
 * StringCounterList.java created by levenick on May 12, 2014 at 12:00:20 PM
 */
public class StringCounterList extends ArrayList<StringCounter> {

    public void add(String s) {
        if (s == null) {
            return;
        }
        int index = whereInTheList(s);
        if (index >= 0) {
            this.get(index).inc();
        } else {
            add(new StringCounter(s));
        }
    }

    public String toString() {
        String returnMe = "StringList:";

        for (StringCounter nextCtr : this) {
            returnMe += "\n\t" + nextCtr.getS() + " (" + nextCtr.getCount() + ")";
        }

        return returnMe;
    }

    public String toString(int n) {
        String returnMe = "StringList:";

        for (StringCounter nextCtr : this) {
            returnMe += "\n\t" + nextCtr.getS() + " (" + (n - nextCtr.getCount()) + ")";
        }

        return returnMe;
    }

    public static void main(String[] args) {
        StringCounterList list = new StringCounterList();
        list.add("foo");
        list.add("bar");
        list.add("bar");
        list.add("foo");
        list.add("foo");
        list.add("foo");
        list.add("foo");
        System.out.println("list = " + list);
    }

    private int whereInTheList(String s) {
        int i = 0;
        for (StringCounter nextCtr : this) {
            if (nextCtr.getS().equals(s)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int getTotal() {
        int sum = 0;

        for (StringCounter sc : this) {
            sum += sc.getCount();
        }

        return sum;
    }
}
