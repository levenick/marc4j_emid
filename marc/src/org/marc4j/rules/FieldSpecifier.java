package org.marc4j.rules;

public class FieldSpecifier {

    protected int fieldNumber;
    protected char subfieldChar;

    public FieldSpecifier() {
    }   //empty default constructor

    public FieldSpecifier(int fieldNumber, char subfieldChar) {   //initializing constructor
        this();   // invoke the default constructor
        this.fieldNumber = fieldNumber;
        this.subfieldChar = subfieldChar;
    }

    public FieldSpecifier(String s) {
        s = s.replace("$", "");
        if (s.length() != 4) {
            Globals.fatalError("FieldSpecifier(String s) got passed: " + s);
        }
        String front = s.substring(0, 3);
        int num = Integer.parseInt(front);
        char ch = s.charAt(3);
        setFieldNumber(num);
        setSubfieldChar(ch);
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public char getSubfieldChar() {
        return subfieldChar;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    public void setSubfieldChar(char subfieldChar) {
        this.subfieldChar = subfieldChar;
    }

    public String toString() {
        String returnMe = "FieldSpec: " + getFieldNumber() + getSubfieldChar();
        return returnMe;
    } // toString()

    String briefToString() {
        return "" + getFieldNumber() + getSubfieldChar();
    }
}  // FieldSpecifier
