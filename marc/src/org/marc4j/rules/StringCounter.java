package org.marc4j.rules;

/**
 * StringCounter.java created by levenick on May 12, 2014 at 12:01:08 PM
 */
public class StringCounter implements Comparable<StringCounter>{

    protected int count;
    protected String s;

    public int compareTo(StringCounter that) {
        return this.s.compareTo(that.s);
    }
    
//    public int compareTo(StringCounter that) {
//        if (this.count<that.count) {
//            return 1;
//        }
//        if (this.count>that.count) {
//            return -1;
//        }
//        return 0;
//    }
    public StringCounter(String s){
        this(1, s);
    }  

    public StringCounter(int count, String s) {   //initializing constructor
        this.count = count;
        this.s = s;
    }

    public int getCount() {return count;}
    public String getS() {return s;}

    public void setCount(int count) { this.count = count;}
    public void setS(String s) { this.s = s;}

    public String toString() {
        String returnMe = "StringCounter: ";
        returnMe += "\tcount=" + getCount();
        returnMe += "\ts=" + getS();
        return returnMe;
    } // toString()

    void inc() {
        count++;
    }

    public void decrement() {
        count--;
    }
}  // StringCounter
