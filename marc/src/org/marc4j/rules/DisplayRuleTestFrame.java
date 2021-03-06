package org.marc4j.rules;

import org.marc4j.marc.Record;

/**
 * DisplayRuleTestFrame.java created by levenick on May 27, 2014 at 2:39:32 PM
 */
public class DisplayRuleTestFrame extends javax.swing.JFrame {

    public DisplayRuleTestFrame() {
         initComponents();
        setBounds(600, 600, 500, 500);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

   }
    public DisplayRuleTestFrame(Rule theRule, int count) {  // this constructor creates the form and displays it
        this();
        setTitle("Rule test results");
        theTA.setText(theRule.toString() + "\n\ntotal record count=" + count
                + "\n#matches=" + theRule.getUsages());
        setVisible(true);
    }

    DisplayRuleTestFrame(Rule aRule, Record theRecord) {
        this();
        setTitle("Rule matching");
        theTA.setText(aRule.toString());
        setVisible(true);
        tryMatching(aRule, theRecord);
    }

    void tryMatching(Rule aRule, Record theRecord) {
        if (aRule.matches(theRecord)) {
            theTA.append("\n\n\tmatched! \tmatched! \tmatched! ");
            aRule.transform(theRecord);
            new OneRecordFrame(theRecord, "transformed!");
        } else {
            theTA.append("\n\nNo match.");
        }
    }

    /**
     * You can't modify the following code; it is regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        theTA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        theTA.setColumns(20);
        theTA.setRows(5);
        jScrollPane1.setViewportView(theTA);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea theTA;
    // End of variables declaration//GEN-END:variables

}
