package jrl2016;

import org.marc4j.rules.StringList;

/**
 *
 * @author levenick
 */
public class PartPartWindow extends javax.swing.JFrame {

    private String fileName = "BeginningRegexes.txt";

    public PartPartWindow() {
        initComponents();
        setTitle("partpart tester");
        setSize(500, 800);
        setVisible(true);
        test();
    }

    void test() {
        load();
        split();
    }
    StringList list;

    void split() {
        StringList theRegexList = buildRegexList();
        String s = inputTF.getText();

        System.out.println("\n\nGwanna split up =>" + s + "<=");

        matchUpTheFronts(theRegexList, s);
        System.out.println("all done!");
    }

    void matchUpTheFronts(StringList theRegexList, String s) {
        MatchRecord theMatchRecord = MyMatcher.matchAny(theRegexList, s);
        while (theMatchRecord != null) {
            //System.out.println("Matched: before choppage, theMatchRecord = " + theMatchRecord);
            theMatchRecord.splitUsingRegex();
            //System.out.println("after choppage, theMatchRecord = " + theMatchRecord);
            System.out.println("here is where need to call addPair for the 935 record we are working on... for =>" + theMatchRecord.getWhatMatched() + "<=" + "-->" + theMatchRecord.getOriginal() + "<--");
            theMatchRecord = MyMatcher.matchAny(theRegexList, theMatchRecord.getOriginal());
        }
    }

    StringList buildRegexList() {
        String numberRegex = numberThingTF.getText();
        String prefixRegex = pToN_TF.getText();
        StringList theRegexList = new StringList();

        for (String nextS : list) {
            String regex = "^(" + nextS + prefixRegex + numberRegex + CreateHorribleRegex.AFTER_NUMBER;
            //theTA.append(regex + "\n");
            theRegexList.add(regex);
        }

        return theRegexList;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        theTA = new javax.swing.JTextArea();
        inputTF = new javax.swing.JTextField();
        pToN_TF = new javax.swing.JTextField();
        numberThingTF = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        theTA.setColumns(20);
        theTA.setRows(5);
        jScrollPane1.setViewportView(theTA);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 180, 370, 380);

        inputTF.setText("Disc one.  amendment 7, Pt. 1. chapter iii. cassette three, page 6, amendment 1234, left over!");
        inputTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputTFActionPerformed(evt);
            }
        });
        getContentPane().add(inputTF);
        inputTF.setBounds(40, 50, 570, 26);

        pToN_TF.setText("(\\. |s | ))");
        getContentPane().add(pToN_TF);
        pToN_TF.setBounds(250, 90, 62, 26);

        numberThingTF.setText("(\\d+|one|two|three|four|five|six|seven|eight|nine|ten|i |ii|iii|iv|v|vi|vii|viii|ix|x)");
        getContentPane().add(numberThingTF);
        numberThingTF.setBounds(10, 120, 590, 26);

        jMenu1.setText("File");

        loadMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.META_MASK));
        loadMenu.setText("Load");
        loadMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuActionPerformed(evt);
            }
        });
        jMenu1.add(loadMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTFActionPerformed
        test();
    }//GEN-LAST:event_inputTFActionPerformed

    private void loadMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMenuActionPerformed
        load();
    }//GEN-LAST:event_loadMenuActionPerformed

    void load() {
        list = new StringList(CreateHorribleRegex.PATH + fileName, false);
        System.out.println("loaded... list.size=" + list.size());
//        for (String s : list) {
//            theTA.append(s + "\n");
//        }
    }

    public static void main(String[] args) {
        new PartPartWindow();
        System.out.println("...");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField inputTF;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem loadMenu;
    private javax.swing.JTextField numberThingTF;
    private javax.swing.JTextField pToN_TF;
    private javax.swing.JTextArea theTA;
    // End of variables declaration//GEN-END:variables

}
