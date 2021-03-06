package org.marc4j.rules;

import javax.swing.JFrame;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * OneRecordFrame.java created by levenick on Dec 29, 2013 at 11:06:02 AM
 */
public class OneRecordFrame extends javax.swing.JFrame {

    Record theRecord;
    FieldList displayList;
    static int x = 30;
    static int y = 600;
    static int count = 0;
    private boolean showAll;
    private boolean showDefault = true;
    private boolean compressed;

    public OneRecordFrame(Record theRecord, MainFrame theRealFrame, boolean foo) {
        this(theRecord, theRealFrame.getInputFileName() + " " + theRecord.getControlNumberField());
        theRealFrame.ping();
    }

    public OneRecordFrame(Record theRecord, JFrame theFrame) {
        this(theRecord);
    }

    public OneRecordFrame(Record theRecord) {
        initComponents();
        this.theRecord = theRecord;
        Integer[] foo = {6, 7, 8, 300, 960, 961, 962, 963, 964};
        displayList = new FieldList(foo);
        setTitle("Displaying: " + theRecord.getControlNumberField());
        setBounds(x, y, 500, 500);
        adjustPosition();
        display();
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setVisible(true);
    }

    private void adjustPosition() {
        x += 10;
        y += 10;
        count++;

        if (count > 6) {
            count = 0;
            x = 30;
            y = 600;
        }
    }

    public OneRecordFrame(Record record, String s) {
        this(record);
        setTitle(s);
    }

    /**
     * You can't modify the following code; it is regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        theTA = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        showAllMenu = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        toggleCompressedMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        theTA.setColumns(20);
        theTA.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        theTA.setRows(5);
        jScrollPane1.setViewportView(theTA);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");

        showAllMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.META_MASK));
        showAllMenu.setText("All");
        showAllMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllMenuActionPerformed(evt);
            }
        });
        jMenu2.add(showAllMenu);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.META_MASK));
        jMenuItem2.setText("Default");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        toggleCompressedMenu.setText("Toggle compressed");
        toggleCompressedMenu.setToolTipText("<html>\nDisplays subfields on a single line\n<BR>\nYou must select All or Default after to redisplay!");
        toggleCompressedMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCompressedMenuActionPerformed(evt);
            }
        });
        jMenu2.add(toggleCompressedMenu);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showAllMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllMenuActionPerformed
        showAll = true;
        showDefault = false;
        display();
    }//GEN-LAST:event_showAllMenuActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        showAll = false;
        showDefault = true;
        display();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void toggleCompressedMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleCompressedMenuActionPerformed
        compressed = ! compressed;
    }//GEN-LAST:event_toggleCompressedMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem showAllMenu;
    private javax.swing.JTextArea theTA;
    private javax.swing.JMenuItem toggleCompressedMenu;
    // End of variables declaration//GEN-END:variables

    private void display() {
        theTA.setText("");
        theTA.append(theRecord.getLeader().toString() + "\n");
        for (VariableField nextField : theRecord.getVariableFields()) {
            String tag = nextField.getTag();
            int tagInt = Integer.parseInt(tag);

            if (showAll || displayList.contains(tagInt)) {
                if (!compressed) {
                theTA.append("=" + nextField.toString() + "\n");
                } else {
                if (Integer.parseInt(nextField.getTag()) <= 10) {
                    theTA.append("=" + nextField.toString() + "\n");
                } else {
                    DataField df = (DataField) nextField;
                    theTA.append(myToString(df) + "\n");
                }
//                if (tagInt == 8) {
//                    theTA.append("                            |||\n");
//                }
            }
            }
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
