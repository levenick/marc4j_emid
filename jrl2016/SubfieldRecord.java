package jrl2016;

/**
 * @author levenick May 4, 2016 1:37:35 PM
 */

public class SubfieldRecord {

    protected char ch;
    protected String contents;

    public SubfieldRecord(){}   //empty default constructor

    public SubfieldRecord(char ch, String contents) {   //initializing constructor
        this();   // invoke the default constructor
        this.ch = ch;
        this.contents = contents;
    }

    public char getCh() {return ch;}
    public String getContents() {return contents;}

    public void setCh(char ch) { this.ch = ch;}
    public void setContents(String contents) { this.contents = contents;}

    public String toString() {
        String returnMe = "";
        returnMe += " $" + getCh();
        returnMe += ": " + getContents();
        return returnMe;
    } // toString()

    /**
     * Append to the end of the $7 record to keep track of what transformations have been made
     * @param s 
     */
    void append(int n) {
        setContents(getContents() + "" + n);
        ProcessorGlobals.incTransformCount(n);
    }
}  // DollarThing

