package org.marc4j.jrl;


/**
 * TestFrame.java created by levenick on Dec 18, 2013 at 12:07:27 PM
 */
public class TestFrame extends javax.swing.JFrame {

    public TestFrame() {  // this constructor creates the form and displays it
        initComponents();
        setTitle("testing!!");
        setSize(1500,1500);
        setVisible(true);
    }

    /** You can't modify the following code; it is regenerated by the Form Editor.
     */
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
        jScrollPane1.setBounds(20, 30, 1130, 1120);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea theTA;
    // End of variables declaration//GEN-END:variables

    void display(String sound) {
        theTA.append(sound + "\n");
    }

}