package org.marc4j.rules;

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
import jrl2016.Processor;
import jrl2016.ProcessorGlobals;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * MainFrame.java created by levenick on May 25, 2014 at 4:16:54 PM
 */
public class MainFrame extends javax.swing.JFrame {

    private MarcStreamReader theMarcReader;

    String inputFileName;
    private String rulesFileName;
    private String regexesFileName;
    private String outputFileName;

    Record theRecord;
    private RuleSet theRuleSet;
    private MyWriter mw;
    private MarcStreamWriter writer;
    GenericDisplayFrame showMe;   // for debugging the path...
    private boolean started = false;

    private boolean may27 = true;

    public MainFrame() {  // this constructor creates the form and displays it
        initComponents();
        setSize(1000, 700);
        setTitle("EMID");
        GenericDisplayFrame first = tryToFixPath();
        String fileTry = setPath();

        try {
            showMe = new GenericDisplayFrame("Where am I?", "Trying to start...");
            if (Globals.initDebug()) {
                showMe.showMe("\n\nPath=" + Globals.getRootPath() + "\n\tGoing to try to open the input file... from:" + fileTry);
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
            initRegexesList();
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
     *
     * @return
     */
    GenericDisplayFrame tryToFixPath() {
        GenericDisplayFrame returnMe = new GenericDisplayFrame("Welcome", "The program has, at least, started...\n\n"
                + "user.dir=" + System.getProperty("user.dir")
                + "\n\nif this is all you see, likely the path is not set right..."
                + "\n\nTo get the path set: create a file named fileInfo.txt \nin the user directory named above, i.e. where the .jar file is"
                + "\nIt should contain the complete pathname to the directory containing three directories:"
                + "\n\tinput (containing the input files... i.e. whatever.mrc"
                + "\n\toutput (where output will go)"
                + "\n\regexes (where regular expression list live)"
                + "\n\tpatterns (where the pattern lists go)"
                + "\n\trules (containing the rule sets)"
                + "\n\n\nThen run it again and (with any luck) it should work..."
        );
        return returnMe;
    }

    /**
     * Set the rootPath by reading fileInfo.txt in the current directory
     *
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
        String rootPath = mr.giveMeTheNextLine();
        Globals.setRootPath(rootPath);
        System.out.println("rootPath1 = " + Globals.getRootPath());
        mr.close();

        return masterFileString;
    }

    /**
     * initialize a JList (for input, output, and rules)
     *
     * @param theList
     * @param values
     */
    private void initJList(JList theList, String[] values) {
        DefaultListModel listModel = ((DefaultListModel) theList.getModel());
        listModel.clear();
        for (String nextS : values) {
            listModel.addElement(nextS);
        }

        theList.setModel(listModel);
        theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        theList.setSelectedIndex(0);
    }

    private void initInputList() {
        initJList(inputList, getFilenames(Globals.getRootPath() + "/input"));
        if (Globals.initDebug()) {
            showMe.showMe("\n\nMade it back from initJList");
        }
        ((DefaultListModel) inputList.getModel()).addElement("lastOutput");
    }

    private void initOutputList() {
        initJList(outputList, getFilenames(Globals.getRootPath() + "/output"));
    }

    private void initRulesList() {
        initJList(rulesList, getFilenames(Globals.getRootPath() + "/rules"));
    }

    private void initRegexesList() {
        initJList(regexesList, getFilenames(Globals.getRootPath() + "/regexes"));
        System.out.println("updated regexes list!!");
    }

    /**
     * grab all the filename from a particular path on a Mac isHidden works for
     * .DSSTORE, but on a PC, oh no!
     *
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
            } else if (Globals.initDebug()) {
                showMe.showMe("\n\nfound a hidden file: " + nextF);
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
        jLabel8 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        regexesList = new javax.swing.JList<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newFileMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        nextMenu = new javax.swing.JMenuItem();
        matchEmMenu = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        processMenu = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        ruleMenu = new javax.swing.JMenu();
        displayRulesMenu = new javax.swing.JMenuItem();
        testRuleSetMenuItem = new javax.swing.JMenuItem();
        testRuleMenuItem = new javax.swing.JMenuItem();
        ruleOneMenuItem = new javax.swing.JMenuItem();
        rulesetOneMenuItem = new javax.swing.JMenuItem();
        allRuleSetsMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        designRegexMenu = new javax.swing.JMenuItem();
        createRegexMenu = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        showFailedMenu = new javax.swing.JMenuItem();
        initDebugMenu = new javax.swing.JMenuItem();
        debugRulesMenu = new javax.swing.JMenuItem();
        debugMatchMenu = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        countMissingMenu = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        view505Menu = new javax.swing.JMenuItem();
        count505MenuItem = new javax.swing.JMenuItem();
        createTinyFileMenu = new javax.swing.JMenuItem();
        learnToWriteMenu = new javax.swing.JMenuItem();

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
        regexTF.setBounds(30, 30, 970, 32);

        fieldTF.setText("505a");
        fieldTF.setToolTipText("Type a field (like 345a) here to apply the Regex to it and display. If you choose Record/Match all, it will apply the Regex to every specified field in the input file!");
        fieldTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldTFActionPerformed(evt);
            }
        });
        getContentPane().add(fieldTF);
        fieldTF.setBounds(30, 100, 60, 26);

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
        resultTF.setBounds(150, 90, 430, 26);

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
        jScrollPane2.setBounds(710, 160, 210, 330);

        jLabel6.setText("Click regex file");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(370, 240, 110, 20);

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

        jLabel8.setText("Click ruleSet file");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(750, 140, 110, 20);

        regexesList.setModel(new DefaultListModel());
        regexesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                regexesListValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(regexesList);

        getContentPane().add(jScrollPane5);
        jScrollPane5.setBounds(320, 280, 240, 180);

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

        nextMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.META_MASK));
        nextMenu.setText("Next");
        nextMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextMenuActionPerformed(evt);
            }
        });
        jMenu2.add(nextMenu);

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

        jMenu7.setText("Process");

        processMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        processMenu.setText("Process");
        processMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processMenuActionPerformed(evt);
            }
        });
        jMenu7.add(processMenu);

        jMenuItem1.setText("Read UTF-8");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem1);

        jMenuBar1.add(jMenu7);

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

        createRegexMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        createRegexMenu.setText("Create regex file");
        createRegexMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRegexMenuActionPerformed(evt);
            }
        });
        jMenu3.add(createRegexMenu);

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

        jMenu6.setText("Test");

        view505Menu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.META_MASK));
        view505Menu.setText("View 505");
        view505Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view505MenuActionPerformed(evt);
            }
        });
        jMenu6.add(view505Menu);

        count505MenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.META_MASK));
        count505MenuItem.setText("Count 505");
        count505MenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                count505MenuItemActionPerformed(evt);
            }
        });
        jMenu6.add(count505MenuItem);

        createTinyFileMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.META_MASK));
        createTinyFileMenu.setText("CreateTinyFile");
        createTinyFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createTinyFileMenuActionPerformed(evt);
            }
        });
        jMenu6.add(createTinyFileMenu);

        learnToWriteMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.META_MASK));
        learnToWriteMenu.setText("Learn to write!");
        learnToWriteMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnToWriteMenuActionPerformed(evt);
            }
        });
        jMenu6.add(learnToWriteMenu);

        jMenuBar1.add(jMenu6);

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

    private void nextMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMenuActionPerformed
        started = true;
        nextRecord();
    }//GEN-LAST:event_nextMenuActionPerformed

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
        new RegexChainDesignFrame(fullInputPath(inputFileName));
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

    private void view505MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view505MenuActionPerformed
        //spew505();
        spewJust505();
    }//GEN-LAST:event_view505MenuActionPerformed

    private void count505MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_count505MenuItemActionPerformed
        count505();
    }//GEN-LAST:event_count505MenuItemActionPerformed

    private void processMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processMenuActionPerformed
        System.out.println("process!");
        processTitles();
//        System.out.println("processing!");
//        setInputFileNameAndOpen();
//        String inputPathName = rootPath + "input" + File.separatorChar + inputFileName;
//        String outputFileName = rootPath + "output" + File.separatorChar + "testOutput";   // fix this!!
//
////        Processor.init(inputPathName, outputFileName);
////        Processor.partitionItAll();
//        testPartparts(inputPathName, outputFileName);
//        System.out.println("\nDone processing!");
//        System.out.println(ProcessorGlobals.toStringTransformCounts());
    }//GEN-LAST:event_processMenuActionPerformed

    private void processTitles() {
        setInputFileNameAndOpen();
        openOutputFile();
        setRegexesFileNameAndOpen();
        Processor.init();
        Processor.processItAll(theMarcReader, writer);

        /*while (theMarcReader.hasNext()) {
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
         */
        writer.close();
        System.out.println(ProcessorGlobals.toStringTransformCounts());

    }
    private void regexesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_regexesListValueChanged
        if (!regexesList.getValueIsAdjusting()) {
            setRegexesFileNameAndOpen();
        }
    }//GEN-LAST:event_regexesListValueChanged

    private void createTinyFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createTinyFileMenuActionPerformed
        setInputFileNameAndOpen();
        openOutputFile();
        int loopCount = 0;

        while (theMarcReader.hasNext()) {
            loopCount++;
            Globals.writeDebug("top of loop loopCount=" + loopCount);

            Record aRecord = theMarcReader.next();

            if (loopCount == 3 || loopCount == 4) {
                writer.write(aRecord);
            }

        }
        writer.close();
        Globals.writeDebug("closed the writer");
    }//GEN-LAST:event_createTinyFileMenuActionPerformed

    private void learnToWriteMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_learnToWriteMenuActionPerformed
        setInputFileNameAndOpen();
        openOutputFile();

        if (theMarcReader.hasNext()) {
            Record aRecord = theMarcReader.next();
            displayWholeRecord(aRecord, "Original Record");
            modifyRecord(aRecord);
            displayWholeRecord(aRecord, "Modified Record");

            //writer.write(aRecord);
        }
        writer.close();
    }//GEN-LAST:event_learnToWriteMenuActionPerformed

    private void createRegexMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRegexMenuActionPerformed
        BuildRegexesDialog crf = new BuildRegexesDialog();
        initRegexesList();
    }//GEN-LAST:event_createRegexMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        UTF8 = true;
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    void displayWholeRecord(Record theRecord, String s) {
        boolean full = true;
        new OneRecordFrame(theRecord, s, full);
    }

    void modifyRecord(Record theRecord) {
        Processor.processItAll(theMarcReader, writer);
    }

    void doTheWork(Record theRecord) {
        MarcFactory factory = MarcFactory.newInstance();
        DataField theField = factory.newDataField(Utils.asTag(935), ' ', ' ');

        Utils.addSubField(theRecord, theField, 'a', " I am a new subfield!");
        Utils.addSubField(theRecord, theField, 'b', " I am another new subfield!");

        theRecord.addVariableField(theField);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem allRuleSetsMenu;
    private javax.swing.JMenuItem count505MenuItem;
    private javax.swing.JMenuItem countMissingMenu;
    private javax.swing.JMenuItem createRegexMenu;
    private javax.swing.JMenuItem createTinyFileMenu;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JMenuItem learnToWriteMenu;
    private javax.swing.JMenuItem matchEmMenu;
    private javax.swing.JMenuItem newFileMenu;
    private javax.swing.JMenuItem nextMenu;
    private javax.swing.JList outputList;
    private javax.swing.JMenuItem processMenu;
    private javax.swing.JTextField rawValueTF;
    private javax.swing.JTextField regexTF;
    private javax.swing.JList<String> regexesList;
    private javax.swing.JTextField resultTF;
    private javax.swing.JMenu ruleMenu;
    private javax.swing.JMenuItem ruleOneMenuItem;
    private javax.swing.JList rulesList;
    private javax.swing.JMenuItem rulesetOneMenuItem;
    private javax.swing.JMenuItem showFailedMenu;
    private javax.swing.JMenuItem testRuleMenuItem;
    private javax.swing.JMenuItem testRuleSetMenuItem;
    private javax.swing.JMenuItem view505Menu;
    // End of variables declaration//GEN-END:variables

    /**
     * Open a Dialog to let the user select a rule from a ruleSet
     *
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
     * The heart of the processing. Applies every rule in a ruleset to a record.
     * If the rule matches, then transform the record (and count the match) If
     * the ruleset has a 999 field, break after the first match
     *
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
        String[] values = getFilenames(Globals.getRootPath() + "/rules");
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
     * Applies whatever regex is in regexTF to the Field in the fieldTF Display
     * the result in the
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

        int count = 0;
        while (matcher.find()) {
            System.out.println("matcher.start() = " + matcher.start());
            System.out.println("matcher.end() = " + matcher.end());
            System.out.println("input.subString[matcher.start(), matcher.end() = >" + input.substring(matcher.start(), matcher.end()) + "<=");
            count++;
            System.out.println("count = " + count);
            System.out.println("matcher.groupCount() = " + matcher.groupCount());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("matcher.group(" + i + ") = " + matcher.group(i));
            }
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
        ProcessorGlobals.initTrailingPunctuation();
    }

    private void openInputFile() {
        String openMe = Globals.getRootPath() + "input" + File.separatorChar + inputFileName;
        if (Globals.initDebug()) {
            showMe.showMe("gonna try to open the input... openMe = >" + openMe + "<\n");
        }

        try {
            if (UTF8) {
                theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)), "UTF-8");
            } else {
                theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)));
            }
        } catch (Exception e) {
            Globals.panic("failed to open " + openMe);
        }
    }

    private void openOutputFileAsInputFile() {
        String openMe = Globals.getRootPath() + "output" + File.separatorChar + outputFileName;
        if (Globals.initDebug()) {
            showMe.showMe("gonna try to open the input... openMe = >" + openMe + "<\n");
        }
        try {
            theMarcReader = new MarcStreamReader(new BufferedInputStream(new FileInputStream(openMe)));

        } catch (Exception e) {
            Globals.panic("failed to open " + openMe);
        }
    }

    private static boolean UTF8 = true;

    private void openOutputFile() {
        String openMe = Globals.getRootPath() + "output" + File.separatorChar + outputFileName;
        System.out.println("output openMe = " + openMe);
        try {
            if (UTF8) {
                writer = new MarcStreamWriter(new FileOutputStream(new File(openMe)), "UTF8", true);  // output in UTF8...
//                AnselToUnicode converter = new AnselToUnicode();                                // needed for UTF8
//                writer.setConverter(converter);                                                 // this too
            } else {
                writer = new MarcStreamWriter(new FileOutputStream(new File(openMe)), true);
            }
        } catch (Exception e) {
            Globals.fatalError("openOutputFile:: could not open " + openMe + " e=" + e);
        }
    }

    /*MarcWriter writer = new MarcStreamWriter(System.out, "UTF8");

        AnselToUnicode converter = new AnselToUnicode();
        writer.setConverter(converter);*/
    private void nextRecord() {
        if (started) {
            if (theMarcReader.hasNext()) {
                theRecord = theMarcReader.next();
                doStuff(theRecord);
            } else {
                resultTF.setText("end of file!");
            }
        }
    }

    void doStuff(Record theRecord) {
        apply();
        new OneRecordFrame(theRecord, this, true);
    }

    public void ping() {
        this.setVisible(false);
        this.setVisible(true);
    }

    private void setInputFileNameAndOpen() {
        inputFileName = (String) inputList.getSelectedValue();
        System.out.println("inputFileName = " + inputFileName);
        if (inputFileName.equals("lastOutput")) {
            openOutputFileAsInputFile();
        } else {
            openInputFile();
        }
        setTitle();
    }

    void foo() {
        String openMe = Globals.getRootPath() + "output" + File.separatorChar + outputFileName;
        File aFile = new File(openMe);
    }

    private void setOutputFileNameAndOpen() {
        outputFileName = (String) outputList.getSelectedValue();
        openOutputFile();
    }

    private void setRulesFileNameAndOpen() {
        rulesFileName = (String) rulesList.getSelectedValue();
        setTitle();
        theRuleSet = readRuleSet(rulesFileName);
    }

    private void setRegexesFileNameAndOpen() {
        regexesFileName = (String) regexesList.getSelectedValue();
        setTitle();
        openRegexesFile(regexesFileName);
    }

    void openRegexesFile(String fn) {
        if (regexesFileName == null) {
            return;
        }
        String openMe = Globals.getRootPath() + "regexes" + File.separatorChar + regexesFileName;
        Processor.setRegexList(new StringList(openMe, true));
        may27("opened regexes file: " + openMe + " it had " + Processor.getRegexList().size() + " lines.");
    }

    void may27(String s) {
        if (may27) {
            System.out.println(s);
        }
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

    private void spew505() {
        setInputFileNameAndOpen();

        int fieldNumber = 505;

        int count = 0, outputCount = 0;
        while (theMarcReader.hasNext()) {
            theRecord = theMarcReader.next();
            System.out.println("theRecord.toString() = " + theRecord.toString());
            count++;
            if (count % 100 == 0) {
                boolean countedItAlready = false;
                for (char ch = 'a'; ch <= 'z'; ch++) {
                    StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, ch);
                    if (!sList.isEmpty()) {
                        if (!countedItAlready) {
                            outputCount++;
                            countedItAlready = true;
                        }
                        boolean first = true;
                        for (String nextS : sList) {
                            if (first) {
                                System.out.println("(" + outputCount + " " + ch + ")" + nextS);
                                first = false;
                            } else {
                                System.out.println("\t" + nextS);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("count = " + count);
        System.out.println("outputCount = " + outputCount);
        System.out.println("All done!!");

    }

    private void spewJust505() {
        setInputFileNameAndOpen();

        int fieldNumber = 505;

        int countRecords = 0, count505a = 0, count505Other = 0, countSerii = 0;
        while (theMarcReader.hasNext()) {
            Record theRecord = theMarcReader.next();
            countRecords++;
            if (has505x(theRecord, 'a')) {
                count505a++;
                StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, 'a');
                boolean first = true;
                for (String s : sList) {
                    if (s.toLowerCase().contains(" pt. ")) {
                        if (first) {
                            System.out.println("\n$a");
                            first = false;
                        }
                        countSerii++;
                        System.out.println("s = " + s);
                    }
                }

            }

            for (char ch = 'b'; ch <= 'z'; ch++) {
                if (has505x(theRecord, ch)) {
                    count505Other++;
                    StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, 'a');
                    boolean first = true;
                    for (String s : sList) {
                        if (s.toLowerCase().contains(" pt. ")) {
                            if (first) {
                                System.out.println("\n$" + ch + "\n");
                                first = false;
                            }
                            countSerii++;
                            System.out.println("s = " + s);
                        }
                    }
                }
            }

        }

        System.out.println("count = " + countRecords);
        System.out.println("count505a = " + count505a);
        System.out.println("count505Other = " + count505Other);
        System.out.println("countSerii = " + countSerii);
        System.out.println("All done!!");
    }

    boolean has505x(Record theRecord, char ch) {
        StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, 505, ch);

        return !sList.isEmpty();
    }

    private RuleSet readRuleSet(String fileNameToRead) {
        String openMe = Globals.getRootPath() + "rules" + File.separatorChar + fileNameToRead;
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
        MyWriter mw = new MyWriter(Globals.getRootPath() + "output" + File.separatorChar + s);
        mw.close();
        initOutputList();
    }

    private void setRuleSetFileNameAndOpen() {
        throw new UnsupportedOperationException("RegexFrame:setRuleSetFileNameAndOpen not written(!!) (yet).");
    }

    public String getInputFileName() {
        return inputFileName;
    }

    private String fullInputPath(String inputFileName) {
        return Globals.getRootPath() + "input" + File.separatorChar + inputFileName;
    }

    private String fullOutputPath(String outputFileName) {
        return Globals.getRootPath() + "output" + File.separatorChar + outputFileName;
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

        String s = list.toString(loopCount) + "\nout of " + loopCount + " records.";
        new GenericDisplayFrame("What's missing?", s);
    }

    private void count505() {
        setInputFileNameAndOpen();

        int fieldNumber = 505;

        int count = 0, failCount = 0, count505a = 0, count505Other = 0;
        while (theMarcReader.hasNext()) {
            theRecord = theMarcReader.next();
            count++;
            VariableField resultField = theRecord.getVariableField("" + fieldNumber);
            if (resultField == null) {
                failCount++;
            } else {
                StringList sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, 'a');
                if (!sList.isEmpty()) {
                    count505a++;
                    if (sList.size() > 1) {
                        System.out.println("sList.size() = " + sList.size());
                    }
                    for (char ch = 'b'; ch <= 'z'; ch++) {
                        sList = Utils.getDataFromFieldAndSubfield(theRecord, fieldNumber, ch);
                        if (!sList.isEmpty()) {
                            count505Other++;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("count = " + count);
        System.out.println("failCount = " + failCount);
        System.out.println("so... " + (count - failCount) + " with 505s");
        System.out.println("records with 505a = " + count505a);
        System.out.println("records with 505a + 505 Other = " + count505Other);
    }

}
