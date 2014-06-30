package org.marc4j.rules;

/**
 * GenericDisplayFrame.java created by levenick on May 30, 2014 at 12:16:33 PM
 */
public class GenericDisplayFrame extends javax.swing.JFrame {

    static int left = 400;
    static int up = 300;

    public GenericDisplayFrame() {  // this constructor creates the form and displays it
        initComponents();
        setTitle("GenericDisplayFrame");
        setBounds(left, up, 500, 500);
        left += 30;
        up += 30;
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setVisible(true);
    }

    public GenericDisplayFrame(String title, StringList list) {
        this();
        setTitle(title);
        spew(list.toString());
    }

    public GenericDisplayFrame(String title, String s, int xx, int yy) {
        this(title, s);
        setBounds(xx, yy, 500,500);
    }
    public GenericDisplayFrame(String title, String s) {
        this();
        setTitle(title);
        spew(s);
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

    private void spew(String s) {
        theTA.setText(s);
    }

    void showMe(String s) {
        theTA.append(s);
    }

}
