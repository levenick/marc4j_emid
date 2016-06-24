package org.marc4j.rules;

import java.util.Collections;
import java.util.Comparator;

/**
 * DisplayRegexMatchesFrame.java created by levenick on May 27, 2014 at 9:04:20
 * AM
 */
public class DisplayRegexMatchesFrame extends javax.swing.JFrame {

    private StringCounterList resultList;
    private boolean byFrequency;
    static int left = 500;
    static int up = 100;

    public DisplayRegexMatchesFrame() {  // this constructor creates the form and displays it
        initComponents();
        setBounds(left, up, 400, 900);
        left += 200;
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("by alphabet");
        setVisible(true);
    }

    public DisplayRegexMatchesFrame(String title, StringCounterList resultList) {
        this();
        this.resultList = resultList;
        setTitle(title);

        display();
    }

    private void display() {
        alphaOrFreq();
        theTA.setText(resultList.toString());
    }

    private String alphaOrFreq() {
        if (byFrequency) {
            Collections.sort(resultList, FREQ);
        } else {
            Collections.sort(resultList);
        }

        return resultList.toString();
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
        alphaMenu = new javax.swing.JMenuItem();
        freqMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        theTA.setColumns(20);
        theTA.setRows(5);
        jScrollPane1.setViewportView(theTA);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Sort");

        alphaMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        alphaMenu.setText("Alphabetically");
        alphaMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alphaMenuActionPerformed(evt);
            }
        });
        jMenu2.add(alphaMenu);

        freqMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        freqMenu.setText("By Frequency");
        freqMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freqMenuActionPerformed(evt);
            }
        });
        jMenu2.add(freqMenu);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void alphaMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alphaMenuActionPerformed
        byFrequency = false;
        setTitle("By alphabet");
        display();
    }//GEN-LAST:event_alphaMenuActionPerformed

    private void freqMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freqMenuActionPerformed
        byFrequency = true;
        setTitle("By frequency");
        display();
    }//GEN-LAST:event_freqMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem alphaMenu;
    private javax.swing.JMenuItem freqMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea theTA;
    // End of variables declaration//GEN-END:variables

    static final Comparator<StringCounter> FREQ
            = new Comparator<StringCounter>() {
                public int compare(StringCounter one, StringCounter two) {
                    if (one.getCount() > two.getCount()) {
                        return -1;
                    }
                    if (one.getCount() < two.getCount()) {
                        return 1;
                    }
                    return 0;
                }
            };

}
