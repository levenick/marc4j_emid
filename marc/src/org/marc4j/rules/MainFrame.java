package org.marc4j.rules;

import filecopystuff.MyWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * MainFrame.java created by levenick on May 25, 2014 at 4:16:54 PM
 */
public class MainFrame extends javax.swing.JFrame {

    private MarcStreamReader theMarcReader;
    String rootPath;
    String inputFileName;
    private String rulesFileName;

    Record theRecord;
    private RuleSet theRuleSet;
    private String outputFileName;
    private MyWriter mw;
    private MarcStreamWriter writer;
    GenericDisplayFrame showMe;   // for debugging the path...

    public MainFrame() {  // this constructor creates the form and displays it
        initComponents();
        setSize(600, 700);
        setTitle("EMID");
        GenericDisplayFrame first = tryToFixPath();
        String fileTry = setPath();

        try {
            showMe = new GenericDisplayFrame("Where am I?", "Trying to start...");
            if (Globals.initDebug()) {
                showMe.showMe("\n\nPath=" + rootPath + "\n\tGoing to try to open the input file... from:" + fileTry);
            }
            initInputList();
            if (Globals.initDebug()) {
                showMe.showMe("\n\nDid it!!");
            }
            initOutputList();
            if (Globals.initDebug()) {
                showMe.showMe("\n\nand the outputlist too!");
            }
            initRulesList();
        } catch (Exception e) {
            Globals.panic("..." + e.toString());
        }
        Globals.setInitDebug(false);
        first.setVisible(false);
        showMe.setVisible(false);
        setVisible(true);
    }

    /**
     * Try to help the user figure out how to get the path set right
     * @return 
     */
    GenericDisplayFrame tryToFixPath() {
        GenericDisplayFrame returnMe = new GenericDisplayFrame("Welcome", "The program has, at least, started...\n\n"
                + "user.dir=" + System.getProperty("user.dir")
                + "\n\nif this is all you see, likely the path is not set right..."
                + "\n\nTo get the path set: create a file named fileInfo.txt \nin the user directory named above"
                + "\nIt should contain the complete pathname to the directory containing three directories:"
                + "\n\tinput (containing the input files... i.e. whatever.mrc"
                + "\n\toutput (where output will go)"
                + "\n\trules (containing the rule sets)"
                + "\n\n\nThen run it again and (with any luck) it should work..."
        );
        return returnMe;
    }

    /**
     * Set the rootPath by reading fileInfo.txt in the current directory
     * @return 
     */
    private String setPath() {

        String masterFileString = System.getProperty("user.dir") + "/fileInfo.txt";
        //String masterFileString = "./resources/fileInfo.txt";
//        InputStream input = Rule.class.getResourceAsStream(masterFileString);
//        URL foo = Rule.class.getResource(".");
//        if (input == null) {
//            Globals.panic("setPath failed to open the master file\n " + masterFileString + " \n.=" + foo.toString());
//        }

        MyReader mr = new MyReader(masterFileString);
        if (mr == null) {
            Globals.panic("setPath failed to open the master file\n " + masterFileString);
        }
        rootPath = mr.giveMeTheNextLine();
        System.out.println("rootPath1 = " + rootPath);
        mr.close();
//        rootPath = Rule.class.getResource("resources/data").getPath() + "/";
//        System.out.println("rootPath2 = " + rootPath + " .=" + foo.toString());
        return masterFileString;
    }

    /**
     * initialize a JList (for input, output, and rules)
     * @param theList
     * @param values 
     */
    private void initJList(JList theList, String[] values) {
        DefaultListModel listModel = ((DefaultListModel) theList.getModel());
        for (String nextS : values) {
            listModel.addElement(nextS);
        }

        theList.setModel(listModel);
        theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        theList.setSelectedIndex(0);
    }

    private void initInputList() {
        initJList(inputList, getFilenames(rootPath + "/input"));
        if (Globals.initDebug()) {
            showMe.showMe("\n\nMade it back from initJList");
        }
        ((DefaultListModel) inputList.getModel()).addElement("lastOutput");
    }

    private void initOutputList() {
        initJList(outputList, getFilenames(rootPath + "/output"));
    }

    private void initRulesList() {
        initJList(rulesList, getFilenames(rootPath + "/rules"));
    }

    /**
     * grab all the filename from a particular path
     * on a Mac isHidden works for .DSSTORE, but on a PC, oh no!
     * @param dirPath
     * @return and array of them!
     */
    private String[] getFilenames(String dirPath) {
        if (Globals.initDebug()) {
            showMe.showMe("\nin getFilenames");
        }
        File pathDir = new File(dirPath);
        if (Globals.initDebug()) {
            showMe.showMe("\n\tpathDir=" + pathDir);
        }

        ArrayList<String> list = new ArrayList<String>();
        for (File nextF : pathDir.listFiles()) {
            String fixWindowS = nextF.toString();
            fixWindowS = fixWindowS.substring(fixWindowS.lastIndexOf(File.separatorChar) + 1);
            boolean windowsGlitch = fixWindowS.charAt(0) == '.';
            if (!nextF.isHidden() && !windowsGlitch) {
                if (Globals.initDebug()) {
                    showMe.showMe("\n\tnextFile=" + nextF);
                }

                //System.out.println("nextF = " + nextF);
                String s = nextF.toString();

                int last = s.lastIndexOf(File.separatorChar);
                list.add(s.substring(last + 1));
            } else {
                if (Globals.initDebug()) {
                    showMe.showMe("\n\nfound a hidden file: " + nextF);
                }
            }
        }
        if (Globals.initDebug()) {
            showMe.showMe("\n\ndone making the list");
        }
        String[] returnMe = new String[list.size()];
        int i = 0;
        for (String nextS : list) {
            returnMe[i] = nextS;
            i++;
        }

        return returnMe;
    }

    /**
     * You can't modify the following code; it is regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        regexTF = new javax.swing.JTextField();
        fieldTF = new javax.swing.JTextField();
        rawValueTF = new javax.swing.JTextField();
        resultTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        inputList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rulesList = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        outputList = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newFileMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        matchEmMenu = new javax.swing.JMenuItem();
        ruleMenu = new javax.swing.JMenu();
        displayRulesMenu = new javax.swing.JMenuItem();
        testRuleSetMenuItem = new javax.swing.JMenuItem();
        testRuleMenuItem = new javax.swing.JMenuItem();
        ruleOneMenuItem = new javax.swing.JMenuItem();
        rulesetOneMenuItem = new javax.swing.JMenuItem();
        allRuleSetsMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        designRegexMenu = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        showFailedMenu = new javax.swing.JMenuItem();
        initDebugMenu = new javax.swing.JMenuItem();
        debugRulesMenu = new javax.swing.JMenuItem();
        debugMatchMenu = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        countMissingMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        regexTF.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        regexTF.setText("\\(\\d*\\)");
        regexTF.setToolTipText("<html>\n\nEnter a regular expression here to experiment.  Hit enter to apply it to the Raw Value below. \n<br>\nE.g. \n<OL>\n<LI>.* matches any number of any character (so you will see the value of that field), \n<LI>col matches any instance of col in that field, \n<LI>[a-z] matches every alphabetic (one at a time), \n<LI>[a-z]* matches any series of alphabetics, \n<LI>and \\(\\d*\\) matches any series of digits (and nothing else) surrounded by ()'s");
        regexTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regexTFActionPerformed(evt);
            }
        });
        getContentPane().add(regexTF);
        regexTF.setBounds(30, 30, 970, 34);

        fieldTF.setText("300a");
        fieldTF.setToolTipText("Type a field (like 345a) here to apply the Regex to it and display. If you choose Record/Match all, it will apply the Regex to every specified field in the input file!");
        fieldTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldTFActionPerformed(evt);
            }
        });
        getContentPane().add(fieldTF);
        fieldTF.setBounds(30, 100, 60, 28);

        rawValueTF.setText("blah blah blah (12345)");
        rawValueTF.setToolTipText("Enter anything you like there and hit enter to see what the regex extracts from it.");
        rawValueTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rawValueTFActionPerformed(evt);
            }
        });
        getContentPane().add(rawValueTF);
        rawValueTF.setBounds(90, 140, 490, 30);

        resultTF.setText(" ");
        getContentPane().add(resultTF);
        resultTF.setBounds(150, 90, 430, 28);

        jLabel1.setText("Field");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(40, 90, 30, 10);

        jLabel2.setText("Raw value");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(220, 110, 62, 40);

        jLabel3.setText("That regex gives");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(200, 70, 180, 16);

        jLabel4.setText("Regex");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(210, 10, 100, 16);

        inputList.setModel(new DefaultListModel());
        inputList.setToolTipText("Click on a file to make it the current input file.");
        inputList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                inputListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(inputList);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 200, 220, 180);

        jLabel5.setText("Click input filename to open");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(30, 180, 190, 20);

        rulesList.setModel(new DefaultListModel());
        rulesList.setToolTipText("Click a ruleSet file to make it the current ruleset\n");
        rulesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                rulesListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(rulesList);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(310, 200, 210, 330);

        jLabel6.setText("Click ruleSet file");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(350, 180, 110, 20);

        outputList.setModel(new DefaultListModel());
        outputList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                outputListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(outputList);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(30, 440, 200, 100);

        jLabel7.setText("outputFile, File/New to add");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 420, 180, 16);

        jMenu1.setText("File");

        newFileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newFileMenu.setText("New OutputFile");
        newFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileMenuActionPerformed(evt);
            }
        });
        jMenu1.add(newFileMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Record");
        jMenu2.setToolTipText("<html>\nMatches the Regex to all the records in the input file\n<br>\nLists the matches in a new window\n<br>\nTo see the ones that don't match, use View/Debug Show Failure");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.META_MASK));
        jMenuItem1.setText("Next");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        matchEmMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        matchEmMenu.setText("Match em all");
        matchEmMenu.setToolTipText("<html>\nMatches the regular expression in Regex to all the records in the input file.\n<br>\nLists the matches in a new window.\n<br>\nTo see the ones that don't match, use View/Debug/Show failure on\n</html>");
        matchEmMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchEmMenuActionPerformed(evt);
            }
        });
        jMenu2.add(matchEmMenu);

        jMenuBar1.add(jMenu2);

        ruleMenu.setText("Rules");

        displayRulesMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        displayRulesMenu.setText("Display rules");
        displayRulesMenu.setToolTipText("Displays the rules in the current ruleSet.");
        displayRulesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayRulesMenuActionPerformed(evt);
            }
        });
        ruleMenu.add(displayRulesMenu);

        testRuleSetMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        testRuleSetMenuItem.setText("Apply ruleset to input file");
        testRuleSetMenuItem.setToolTipText("<html>\nApply the current rule set on the current input file.\n<br>Writes all the records (transformed or not) to the current output file\n<br>You can read them back in by choosing lastOutput in the input window\n<br>But the output is whatever is selected in the outputFile window \n<br>I.e. whereever/data/output/currentfile");
        testRuleSetMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testRuleSetMenuItemActionPerformed(evt);
            }
        });
        ruleMenu.add(testRuleSetMenuItem);

        testRuleMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        testRuleMenuItem.setText("Test a rule on input file");
        testRuleMenuItem.setToolTipText("<html>Test a single rule against the current input file\n<br> Will display the first 5 it matches\n<br> Displays the total number of matches");
        testRuleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testRuleMenuItemActionPerformed(evt);
            }
        });
        ruleMenu.add(testRuleMenuItem);

        ruleOneMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        ruleOneMenuItem.setText("Test a rule against one record");
        ruleOneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruleOneMenuItemActionPerformed(evt);
            }
        });
        ruleMenu.add(ruleOneMenuItem);

        rulesetOneMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        rulesetOneMenuItem.setText("Test ruleset on current record");
        rulesetOneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rulesetOneMenuItemActionPerformed(evt);
            }
        });
        ruleMenu.add(rulesetOneMenuItem);

        allRuleSetsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        allRuleSetsMenu.setText("Apply *all* rulesets!");
        allRuleSetsMenu.setToolTipText("<html>\nApplies all the ruleSets to each record in the input file.\n<br>\nWrites each, with all the added fields, to the output file\n<br>\nSelect lastOutput as the input file to see what it did.");
        allRuleSetsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRuleSetsMenuActionPerformed(evt);
            }
        });
        ruleMenu.add(allRuleSetsMenu);

        jMenuBar1.add(ruleMenu);

        jMenu3.setText("Regex");

        designRegexMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        designRegexMenu.setText("Design regex chain");
        designRegexMenu.setToolTipText("<html>\nBrings up a window (associated with the current input file) to experiment with regexes\n<br>\nThe pane on the left is editable, each line is a regex that will be applie to the field at the bottom\n<br>\nPush the button to apply all the regexes to that field\n");
        designRegexMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                designRegexMenuActionPerformed(evt);
            }
        });
        jMenu3.add(designRegexMenu);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("View/Debug");

        showFailedMenu.setText("Show failure on");
        showFailedMenu.setToolTipText("Used with Matchem All - brings up the mismatches as well as the matches\n");
        showFailedMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFailedMenuActionPerformed(evt);
            }
        });
        jMenu4.add(showFailedMenu);

        initDebugMenu.setText("Init debug on");
        initDebugMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initDebugMenuActionPerformed(evt);
            }
        });
        jMenu4.add(initDebugMenu);

        debugRulesMenu.setText("Debug rules on");
        debugRulesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugRulesMenuActionPerformed(evt);
            }
        });
        jMenu4.add(debugRulesMenu);

        debugMatchMenu.setText("Debug match on");
        debugMatchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugMatchMenuActionPerformed(evt);
            }
        });
        jMenu4.add(debugMatchMenu);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Missed");
        jMenu5.setToolTipText("<html>\nHaving applied all the rulesets to a file, perhaps you want to know how many records have 96x fields added.\n<br>\nSo... select lastOutput and count which are missing...");

        countMissingMenu.setText("Count missing 96x");
        countMissingMenu.setToolTipText("<html>\nHaving applied all the rulesets to a file, perhaps you want to know how many records have 96x fields added.\n<br>\nSo... select lastOutput and count which are missing...");
        countMissingMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countMissingMenuActionPerformed(evt);
            }
        });
        jMenu5.add(countMissingMenu);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regexTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regexTFActionPerformed
        apply();
    }//GEN-LAST:event_regexTFActionPerformed

    private void fieldTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldTFActionPerformed
        apply();
    }//GEN-LAST:event_fieldTFActionPerformed

    private void rawValueTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawValueTFActionPerformed
        // testing only
        applyAndOutput();
    }//GEN-LAST:event_rawValueTFActionPerformed

    private void newFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileMenuActionPerformed
        createNewOutputFile();
    }//GEN-LAST:event_newFileMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        nextRecord();

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void inputListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_inputListValueChanged
        if (!inputList.getValueIsAdjusting()) {
            setInputFileNameAndOpen();
            nextRecord();
        }
    }//GEN-LAST:event_inputListValueChanged

    private void matchEmMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchEmMenuActionPerformed
        matchEmAll();
    }//GEN-LAST:event_matchEmMenuActionPerformed

    private void rulesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_rulesListValueChanged
        if (!rulesList.getValueIsAdjusting()) {
            setRulesFileNameAndOpen();
        }
    }//GEN-LAST:event_rulesListValueChanged

    private void testRuleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testRuleMenuItemActionPerformed
        testARule(getMeARule());
    }//GEN-LAST:event_testRuleMenuItemActionPerformed

    private void testRuleSetMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testRuleSetMenuItemActionPerformed
        testSingleRuleSetOnWholeFile();
        String s = theRuleSet.usages();
        String fn = theRuleSet.getInputFileName().substring(theRuleSet.getInputFileName().lastIndexOf(File.separatorChar) + 1);
        new GenericDisplayFrame("Matches/rule " + inputFileName + " ruleset=" + fn, s);

    }//GEN-LAST:event_testRuleSetMenuItemActionPerformed

    private void rulesetOneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rulesetOneMenuItemActionPerformed
        testEntireRuleSet(theRecord);
    }//GEN-LAST:event_rulesetOneMenuItemActionPerformed

    private void ruleOneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruleOneMenuItemActionPerformed
        Rule aRule = getMeARule();
        new DisplayRuleTestFrame(aRule, theRecord);

    }//GEN-LAST:event_ruleOneMenuItemActionPerformed

    private void outputListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_outputListValueChanged
        if (!outputList.getValueIsAdjusting()) {
            setOutputFileNameAndOpen();
        }    }//GEN-LAST:event_outputListValueChanged

    private void designRegexMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_designRegexMenuActionPerformed
        new RegexChainDesignFrame(fullPath(inputFileName));
    }//GEN-LAST:event_designRegexMenuActionPerformed

    private void displayRulesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayRulesMenuActionPerformed
        new GenericDisplayFrame("Ruleset:" + theRuleSet.getInputFileName(), theRuleSet.toStringBrief(), 600, 200);
    }//GEN-LAST:event_displayRulesMenuActionPerformed

    private void debugMatchMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugMatchMenuActionPerformed
        Globals.toggleMatchDebug();
        setMenuText(debugMatchMenu, Globals.getMatchDebug());
    }//GEN-LAST:event_debugMatchMenuActionPerformed

    private void debugRulesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugRulesMenuActionPerformed
        Globals.toggleRuleDebug();
        setMenuText(debugRulesMenu, Globals.getRuleDebug());
    }//GEN-LAST:event_debugRulesMenuActionPerformed

    private void showFailedMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showFailedMenuActionPerformed
        Globals.toggleShowFailed();
        setMenuText(showFailedMenu, Globals.getShowingFailure());
    }//GEN-LAST:event_showFailedMenuActionPerformed

    private void initDebugMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initDebugMenuActionPerformed
        Globals.toggleInitDebug();
        setMenuText(initDebugMenu, Globals.getInitDebug());
    }//GEN-LAST:event_initDebugMenuActionPerformed

    private void allRuleSetsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRuleSetsMenuActionPerformed
        doItAll();
    }//GEN-LAST:event_allRuleSetsMenuActionPerformed

    private void countMissingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countMissingMenuActionPerformed
        countMissing();
    }//GEN-LAST:event_countMissingMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem allRuleSetsMenu;
    private javax.swing.JMenuItem countMissingMenu;
    private javax.swing.JMenuItem debugMatchMenu;
    private javax.swing.JMenuItem debugRulesMenu;
    private javax.swing.JMenuItem designRegexMenu;
    private javax.swing.JMenuItem displayRulesMenu;
    private javax.swing.JTextField fieldTF;
    private javax.swing.JMenuItem initDebugMenu;
    private javax.swing.JList inputList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenuItem matchEmMenu;
    private javax.swing.JMenuItem newFileMenu;
    private javax.swing.JList outputList;
    private javax.swing.JTextField rawValueTF;
    private javax.swing.JTextField regexTF;
    private javax.swing.JTextField resultTF;
    private javax.swing.JMenu ruleMenu;
    private javax.swing.JMenuItem ruleOneMenuItem;
    private javax.swing.JList rulesList;
    private javax.swing.JMenuItem rulesetOneMenuItem;
    private javax.swing.JMenuItem showFailedMenu;
    private javax.swing.JMenuItem testRuleMenuItem;
    private javax.swing.JMenuItem testRuleSetMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * Open a Dialog to let the user select a rule from a ruleSet
     * @return 
     */
    private Rule getMeARule() {
        RuleChoiceDialog rcDialog = new RuleChoiceDialog(theRuleSet);
        Rule currentRule = rcDialog.getChosenRule();

        return currentRule;
    }

    private void testSingleRuleSetOnWholeFile() {
        RuleSetList ruleSetList = new RuleSetList();
        ruleSetList.add(theRuleSet);
        applyRuleSetsToFile(ruleSetList);
    }

    private void applyRuleSetsToFile(RuleSetList ruleSetList) {
        resetUsageCounts(ruleSetList);
        setInputFileNameAndOpen();
        openOutputFile();
        int count = 0;
        int loopCount = 0;

        Globals.writeDebug("about to start the loop...");

        while (theMarcReader.hasNext()) {
            loopCount++;
            Globals.writeDebug("top of loop loopCount=" + loopCount);
            count++;
            if (count % 10000 == 0) {
                System.out.println("count = " + count);
            }
            Record aRecord = theMarcReader.next();
            for (RuleSet nextRS : ruleSetList) {
                applyAllRulesInASet(nextRS, aRecord);
            }
            writer.write(aRecord);
            Globals.writeDebug("\twrote one");

            //System.out.print(" " + count);
            //new OneRecordFrame(aRecord, "transformed??");
        }
        writer.close();
        Globals.writeDebug("closed the writer");
    }

    private void resetUsageCounts(RuleSetList ruleSetList) {
        for (RuleSet nextRS : ruleSetList) {
            nextRS.resetUsageCounts();
        }
    }

    /**
     * The heart of the processing.
     * Applies every rule in a ruleset to a record.
     * If the rule matches, then transform the record (and count the match)
     * If the ruleset has a 999 field, break after the first match
     * @param theCurrentRuleSet
     * @param theCurrentRecord 
     */
    private void applyAllRulesInASet(RuleSet theCurrentRuleSet, Record theCurrentRecord) {
        Rule.setRuleSet(theCurrentRuleSet);
        Globals.resetCounter();
        for (Rule nextRule : theCurrentRuleSet) {
            if (nextRule.matches(theCurrentRecord)) {
                Globals.initMatchDebug("\n\n\tmatched! \tmatched! \tmatched! " + nextRule.toString());
                nextRule.transform(theCurrentRecord);
                nextRule.incUsages();
                if (theCurrentRuleSet.getOnlyOneMatch()) { // i.e. if there's a 999 field in the Header of the RuleSet
                    break;   // only apply the first rule that matches
                }
            }
        }
    }

    /**
     * Reads in all the RuleSets in the rules directory, puts them all in a
     * RuleSetList, then iterates over all of them for each Record and writes
     * the results... whee!!
     */
    private void doItAll() {
        RuleSetList ruleSetList = new RuleSetList();
        String[] values = getFilenames(rootPath + "/rules");
        for (String nextS : values) {
            ruleSetList.add(readRuleSet(nextS));
        }
        System.out.println("ruleSetList = " + ruleSetList.toString());
        applyRuleSetsToFile(ruleSetList);
        System.out.println("done!");
        String results = ruleSetList.toStringResults() + "\ntotal rule application=" + Globals.getApplicationCount();
        new GenericDisplayFrame("Done!", results);
    }

    private void testEntireRuleSet(Record theRecord) {
        for (Rule nextRule : theRuleSet) {
            if (nextRule.matches(theRecord)) {
                Globals.initMatchDebug("\n\n\tmatched! \tmatched! \tmatched! " + nextRule.getSerialNumber());
                nextRule.transform(theRecord);
                new OneRecordFrame(theRecord);
            }
        }
    }

    private void testARule(Rule currentRule) {
        int count = 0;
        int matchCount = 0;
        setInputFileNameAndOpen();
        while (theMarcReader.hasNext()) {
            count++;
            Record aRecord = theMarcReader.next();
            if (currentRule.matches(aRecord)) {
                if (matchCount < 5) {
                    matchCount++;
                    Globals.matchAdvice("matched! " + currentRule.toString(), aRecord.toString());
                }
                currentRule.transform(aRecord);
                currentRule.incUsages();
            }
        }
        new DisplayRuleTestFrame(currentRule, count);
    }

    /**
     * Applies whatever regex is in  regexTF to the Field in the fieldTF
     * Display the result in the 
     */
    private void apply() {
        String fieldString = fieldTF.getText();
        String front = fieldString.substring(0, 3);
        int fieldNumber = 0;
        try {
            fieldNumber = Integer.parseInt(front);
        } catch (Exception e) {
            fieldTF.setText("245a");
            fieldTFActionPerformed(null);
        }
        String back = fieldString.substring(3).trim();
        char subfield = back.charAt(0);

        applyToField(fieldNumber, subfield);
    }

//    private void findMultipleFieldsAndOrSubfields() {
//        StringList list = Utils.getDataFromFieldAndSubfield(theRecord, 700, 'a');
//        System.out.println("list = " + list);
//    }

    void applyToField(int fieldNumber, char subFieldChar) {
        StringList list = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, subFieldChar);

        String tag = Utils.asTag(fieldNumber);

        if (list.size() == 0) {
            rawValueTF.setText("no " + tag + "$" + subFieldChar + " field!!");

        } else if (list.size() == 1) {
            String foo = list.get(0);
            rawValueTF.setText(foo);
            applyAndOutput();
        } else {
            handleMultipleValues(list, Utils.asTag(fieldNumber) + subFieldChar);
        }
    }

    private void handleMultipleValues(StringList list, String title) {
        new GenericDisplayFrame(title, list);
    }

    private void applyAndOutput() {
        resultTF.setText(applyRegexToLiteral(rawValueTF.getText()));
    }

    private String applyRegexToLiteral(String input) {
        String returnMe = "";
        String re = regexTF.getText();

        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (returnMe.length() > 0) {
                returnMe += ", ";
            }
            returnMe += matcher.group();
        }

        if (returnMe.length() == 0) {
            returnMe = "failed extraction";
        }
        return returnMe;
    }

    public static void main(String[] args) {
        MainFrame rf = new MainFrame();
        //rf.findMultipleFieldsAndOrSubfields();
    }

    private void openInputFile() {
        String openMe = rootPath + "input" + File.separatorChar + inputFileName;
        if (Globals.initDebug()) {
            showMe.showMe("gonna try to open the input... openMe = >" + openMe + "<\n");
        }
        //String openMe = "/Users/levenick/kelley/data/input/ExampleFileBefore.mrc";
        try {
            theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)));

        } catch (Exception e) {
            Globals.panic("failed to open " + openMe);
        }
    }

    private void openOutputFileAsInputFile() {
        String openMe = rootPath + "output" + File.separatorChar + outputFileName;
        if (Globals.initDebug()) {
            showMe.showMe("gonna try to open the input... openMe = >" + openMe + "<\n");
        }        //String openMe = "/Users/levenick/kelley/data/input/ExampleFileBefore.mrc";
        try {
            theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)));

        } catch (Exception e) {
            Globals.panic("failed to open " + openMe);
        }
    }

    private void openOutputFile() {
        String openMe = rootPath + "output" + File.separatorChar + outputFileName;
        System.out.println("output openMe = " + openMe);
        try {
            writer = new MarcStreamWriter(new FileOutputStream(new File(openMe)));
        } catch (Exception e) {
            Globals.fatalError("openOutputFile:: could not open " + openMe + " e=" + e);
        }
    }

    private void nextRecord() {
        if (theMarcReader.hasNext()) {
            theRecord = theMarcReader.next();
            doStuff(theRecord);
        } else {
            resultTF.setText("end of file!");
        }
    }

    void doStuff(Record theRecord) {
        apply();
        new OneRecordFrame(theRecord, this, true);
    }

    public void ping() {
        resultTF.setText(resultTF.getText());
        regexTF.requestFocus();
        this.requestFocus();
        regexTF.selectAll();
    }

    private void setInputFileNameAndOpen() {
        inputFileName = (String) inputList.getSelectedValue();
        if (inputFileName.equals("lastOutput")) {
            openOutputFileAsInputFile();
        } else {
            openInputFile();
        }
        setTitle();
    }

    void foo() {
        String openMe = rootPath + "output" + File.separatorChar + outputFileName;
        File aFile = new File(openMe);
        //Files.d
    }

    private void setOutputFileNameAndOpen() {
        outputFileName = (String) outputList.getSelectedValue();
        openOutputFile();
    }

    private void setRulesFileNameAndOpen() {
        rulesFileName = (String) rulesList.getSelectedValue();
        setTitle();
        theRuleSet = readRuleSet(rulesFileName);
        //displayRuleSet();
    }

    private void setTitle() {
        setTitle("input=" + inputFileName + " pattern=" + rulesFileName);

    }

    private void matchEmAll() {
        StringCounterList resultList = new StringCounterList();
        StringCounterList failureList = new StringCounterList();
        setInputFileNameAndOpen();

        String fieldString = fieldTF.getText();
        String front = fieldString.substring(0, 3);
        int fieldNumber = 245;
        try {
            fieldNumber = Integer.parseInt(front);
        } catch (Exception e) {
            fieldTF.setText("245a");
            fieldTFActionPerformed(null);
        }
        String back = fieldString.substring(3).trim();
        char subFieldChar = back.charAt(0);

        String re = regexTF.getText();
        System.out.println("re = >" + re + "<");
        Pattern pattern = Pattern.compile(re);

        while (theMarcReader.hasNext()) {
            boolean found = false;
            theRecord = theMarcReader.next();
            StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, subFieldChar);
            for (String nextS : sList) {
                Matcher matcher = pattern.matcher(nextS);

                while (matcher.find()) {
                    found = true;
                    String g = matcher.group();
                    if (g.length() > 0) {
                        resultList.add(g);
                    }
                }
            }

            if (!found && sList.size() > 0) {
                failureList.add(sList.get(0));
            }
        }

        new DisplayRegexMatchesFrame("matched: (" + Utils.asTag(fieldNumber) + subFieldChar + ")" + re, resultList);
        if (Globals.getShowingFailure()) {
            new DisplayRegexMatchesFrame("failed: (" + Utils.asTag(fieldNumber) + subFieldChar + ")" + re, failureList);
        }
    }

    private RuleSet readRuleSet(String fileNameToRead) {
        String openMe = rootPath + "rules" + File.separatorChar + fileNameToRead;
        System.out.println("openMe = " + openMe);
        RuleSet returnMe = new RuleSet(openMe);

        return returnMe;
    }

    private void displayRuleSet() {
        new RuleChoiceDialog(theRuleSet, false);
    }

    private void createNewOutputFile() {
        String s = (String) JOptionPane.showInputDialog(
                new JFrame(),
                "Enter new output file name:",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "someFile.mrc");
        System.out.println("s = " + s);
        MyWriter mw = new MyWriter(rootPath + "output/" + s);
        mw.close();
        initOutputList();
    }

    private void setRuleSetFileNameAndOpen() {
        throw new UnsupportedOperationException("RegexFrame:setRuleSetFileNameAndOpen not written(!!) (yet).");
    }

    public String getInputFileName() {
        return inputFileName;
    }

    private String fullPath(String inputFileName) {
        return rootPath + "/input/" + inputFileName;
    }

    private void setMenuText(JMenuItem aMenu, boolean on) {
        String currentS = aMenu.getText();
        String front = currentS.substring(0, currentS.lastIndexOf(' '));
        String newS;
        if (on) {
            newS = front + " off";
        } else {
            newS = front + " on";
        }
        aMenu.setText(newS);
    }

    private void countMissing() {
        StringCounterList list = new StringCounterList();

        setInputFileNameAndOpen();
        int loopCount = 0;

        Globals.writeDebug("about to start the loop...");

        while (theMarcReader.hasNext()) {
            loopCount++;
            Globals.writeDebug("top of loop loopCount=" + loopCount);

            Record aRecord = theMarcReader.next();
            for (int fn = 960; fn <= 964; fn++) {
                VariableField vf = aRecord.getVariableField("" + fn);
                if (vf != null) {
                    list.add("" + fn);
                }
            }
        }
        
        String s=list.toString(loopCount) + "\nout of " + loopCount + " records.";
        new GenericDisplayFrame("What's missing?", s);
    }

}
