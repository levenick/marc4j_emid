package jrl2016;

import java.util.ArrayList;
import org.marc4j.rules.Globals;

/**
 * @author levenick May 4, 2016 1:37:19 PM
 */
class SubfieldList extends ArrayList<SubfieldRecord> {

    public boolean add(SubfieldRecord sr) {
        for (int i = 0; i < this.size(); i++) {
            if (sr.getCh() < get(i).getCh()) {
                this.add(i, sr);
                return true;
            }
        }

        return super.add(sr);  // biggest so add at the end...
    }

    public String toString() {
        String returnMe = "";

        for (SubfieldRecord nextDT : this) {
            if (!Character.isDigit(nextDT.getCh())) {
                returnMe += " " + nextDT.toString();
            }
        }
        for (SubfieldRecord nextDT : this) {  // $digits second... kludge!
            if (Character.isDigit(nextDT.getCh())) {
                returnMe += " " + nextDT.toString();
            }
        }

        return returnMe;
    }

    String getSubfieldValue(char c) {
        SubfieldRecord sr = findSubfield(c);
        if (sr == null) {
            return null;
        }
        return sr.getContents();
    }

    void setSubfieldValue(char c, String buffer) {
        SubfieldRecord sr = findSubfield(c);
        if (sr == null) {
            Globals.panic("There had to have been a subfield " + c + "!!!");
        }
        sr.setContents(buffer);
    }

    SubfieldRecord findSubfield(char ch) {
        SubfieldRecord returnMe = null;

        for (SubfieldRecord nextSR : this) {
            if (nextSR.getCh() == ch) {
                return nextSR;
            }
        }

        return returnMe;
    }

}
