package jrl2016;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.rules.FieldList;

/**
 *
 * @author levenick
 */
public class Display505Frame extends javax.swing.JFrame {

    Record theRecord;
    FieldList displayList;
    static int x = 100;
    static int y = 100;
    private boolean compressed;
    
    private Whole505a the505;

    Display505Frame(String s, Whole505a the505) {
        initComponents();
        this.the505 = the505;
        setTitle(s);
        setBounds(x, y, 1700, 500);
        display();
        adjustPosition();
        setVisible(true);
    }

    private void adjustPosition() {
        x += 10;
        y += 200;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        theTA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        theTA.setColumns(20);
        theTA.setRows(5);
        jScrollPane1.setViewportView(theTA);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 0, 1940, 460);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea theTA;
    // End of variables declaration//GEN-END:variables

 
    private void display() {
        theTA.setText(the505.getRawString());
        theTA.append("\n\n");
        for (Title935Record nextTitle: the505.getTitleList()) {
            theTA.append("    " + nextTitle.toString());
        }
    }

    private String myToString(DataField df) {
        StringBuilder sb = new StringBuilder();
        sb.append("="+df.getTag());
        sb.append(' ');
        sb.append(df.getIndicator1());
        sb.append(df.getIndicator2());
        for (Subfield sf : df.getSubfields()) {
            sb.append(sf.toString());
        }
        return sb.toString();
    }

}
