package org.marc4j.rules;

import java.util.ArrayList;

/**
 * FieldList.java created by levenick on May 6, 2014 at 9:52:02 AM
 */
public class FieldList extends ArrayList<Integer> {

    public FieldList (Integer[] array) {
        for (int i=0; i<array.length; i++) {
            add(array[i]);
        }
    }
    public String toString() {
        String returnMe = "I am a FieldList:";
        
        for (Integer nextField: this) {
            returnMe += "\n\t" + nextField;
        }
        
        return returnMe;
    }
    
    public static void main(String[] args) {
        Integer[] foo = {1,2,123};
        FieldList fl = new FieldList(foo);
        System.out.println("fl = " + fl);
    }
}
