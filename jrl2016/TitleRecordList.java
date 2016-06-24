package jrl2016;

import java.util.ArrayList;

/**
 * @author levenick May 4, 2016 1:36:10 PM
 */

class TitleRecordList extends ArrayList<Title935Record> {

    public String toString() {
        String returnMe ="";
        
        for (Title935Record nextTR: this) {
            returnMe += "\n\t" + nextTR.toString();
        }
        
        return returnMe;
    }
}
