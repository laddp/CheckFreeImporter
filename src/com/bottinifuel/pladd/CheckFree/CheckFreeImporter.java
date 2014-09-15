/*
 * Created on Jan 24, 2006 by pladd
 *
 */
/************************************************************************
* Change Log:
* 
*   Date         Description                                        Pgmr
*  ------------  ------------------------------------------------   -----
*  Apr 12,2013   Version 2 Added Metavante csv file.                carlonc 
*************************************************************************/
package com.bottinifuel.pladd.CheckFree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.bottinifuel.pladd.CheckFree.LineItem.DropException;
import com.bottinifuel.pladd.CheckFree.MetavanteConversion.ReversalException;

/**
 * Program to import a CheckFree CSV file and export ADD Energy BANKPOST file
 * @author pladd
 *
 */
public class CheckFreeImporter extends JFrame implements ActionListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -4319857793594895304L;
    
    protected File KnownAccounts;
    protected File CorrectionFile;
    protected File CSVimport;
    protected File BankPost;
    
    protected Hashtable<Integer, String>    Accounts;
    protected Vector<LineItem>              Items;
    protected Hashtable<String, Correction> Corrections;
    protected boolean CorrectionsUpdated = false;
    protected boolean KnownAccountsChanged = false;

    protected JTextField AcctFileName;
    protected JTextField CorrectFileName;
    protected JTextField InFileName;
    protected JTextField OutFileName;
    
    private JButton RunButton;

    private static String CheckFreeDirectory;
    private        String fileType = "";
    private static String version  = "v2.0"; 
    		
    private JFileChooser FileChooser;

    private boolean InputChosen  = false;
    private boolean OutputChosen = false;

	private int correctedCount = 0;
	private int autoCorrectedCount = 0;
	private int newItemCount = 0;

	private static final String [] KnownHeader = { "Acct#", "Name" };

    
    public CheckFreeImporter() throws Exception
    {
        super ("CheckFreee Importer "+version);
        setDefaultCloseOperation(EXIT_ON_CLOSE);        

        if (System.getProperty("os.name").compareTo("Linux") == 0)
            CheckFreeDirectory = "/share/Accounts Receivable/CheckFree";
        else
            CheckFreeDirectory = "S:/Accounts Receivable/CheckFree";
       
        FileChooser = new JFileChooser(CheckFreeDirectory);

        JPanel contents = new JPanel();

        contents.setLayout(new GridBagLayout());
        getContentPane().add(contents, BorderLayout.CENTER);

        JLabel AcctFileLabel = new JLabel("Accounts File: ");
        AcctFileLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        JLabel CorrectLabel  = new JLabel("Corrections File: ");
        CorrectLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        JLabel InFileLabel   = new JLabel("Input File: ");
        InFileLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        JLabel OutFileLabel  = new JLabel("Output File: ");
        OutFileLabel.setHorizontalAlignment(SwingConstants.TRAILING);

        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        contents.add(AcctFileLabel, gridBagConstraints);
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.anchor = GridBagConstraints.EAST;
        gridBagConstraints_1.ipadx = 10;
        gridBagConstraints_1.gridy = 1;
        gridBagConstraints_1.gridx = 0;
        contents.add(CorrectLabel, gridBagConstraints_1);
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.anchor = GridBagConstraints.EAST;
        gridBagConstraints_2.ipadx = 10;
        gridBagConstraints_2.gridy = 2;
        gridBagConstraints_2.gridx = 0;
        contents.add(InFileLabel, gridBagConstraints_2);
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.anchor = GridBagConstraints.EAST;
        gridBagConstraints_3.ipadx = 10;
        gridBagConstraints_3.gridy = 3;
        gridBagConstraints_3.gridx = 0;
        contents.add(OutFileLabel, gridBagConstraints_3);        

        AcctFileName    = new JTextField();
        CorrectFileName = new JTextField();
        InFileName      = new JTextField();
        OutFileName     = new JTextField();

        AcctFileName   .setColumns(50);
        CorrectFileName.setColumns(50);
        InFileName     .setColumns(50);
        OutFileName    .setColumns(50);

        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.weighty = 1;
        gridBagConstraints_4.weightx = 1;
        gridBagConstraints_4.gridy = 0;
        gridBagConstraints_4.gridx = 1;
        contents.add(AcctFileName, gridBagConstraints_4);
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.weighty = 1;
        gridBagConstraints_5.weightx = 1;
        gridBagConstraints_5.gridy = 1;
        gridBagConstraints_5.gridx = 1;
        contents.add(CorrectFileName, gridBagConstraints_5);
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.weighty = 1;
        gridBagConstraints_6.weightx = 1;
        gridBagConstraints_6.gridy = 2;
        gridBagConstraints_6.gridx = 1;
        contents.add(InFileName, gridBagConstraints_6);
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.weighty = 1;
        gridBagConstraints_7.weightx = 1;
        gridBagConstraints_7.gridy = 3;
        gridBagConstraints_7.gridx = 1;
        contents.add(OutFileName, gridBagConstraints_7);

        JButton AcctFileButton = new JButton("Browse...");
        JButton CorrectButton  = new JButton("Browse...");
        JButton InFileButton   = new JButton("Browse...");
        JButton OutFileButton  = new JButton("Browse...");
        AcctFileButton.setActionCommand("acctf");
        CorrectButton .setActionCommand("correctf");
        InFileButton  .setActionCommand("inputf");
        OutFileButton .setActionCommand("outputf");
        AcctFileButton.addActionListener(this);
        CorrectButton .addActionListener(this);
        InFileButton  .addActionListener(this);
        OutFileButton .addActionListener(this);
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.gridy = 0;
        gridBagConstraints_8.gridx = 2;
        contents.add(AcctFileButton, gridBagConstraints_8);
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.gridx = 2;
        contents.add(CorrectButton, gridBagConstraints_9);
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.gridy = 2;
        gridBagConstraints_10.gridx = 2;
        contents.add(InFileButton, gridBagConstraints_10);
        final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
        gridBagConstraints_11.gridy = 3;
        gridBagConstraints_11.gridx = 2;
        contents.add(OutFileButton, gridBagConstraints_11);

        KnownAccounts = new File(CheckFreeDirectory + "/data/accounts.csv");
        AcctFileName.setText(KnownAccounts.getAbsolutePath());

        CorrectionFile = new File(CheckFreeDirectory + "/data/corrections.csv");
        CorrectFileName.setText(CorrectionFile.getAbsolutePath());

        Box controlbox = new Box(BoxLayout.X_AXIS);
        getContentPane().add(controlbox, BorderLayout.SOUTH);

        RunButton = new JButton("Run");
        JButton CloseButton = new JButton("Close");

        RunButton.setMnemonic(KeyEvent.VK_R);
        RunButton.setDisplayedMnemonicIndex(0);
        RunButton.setActionCommand("run");
        RunButton.addActionListener(this);
        RunButton.setEnabled(false);

        CloseButton.setMnemonic(KeyEvent.VK_C);
        CloseButton.setDisplayedMnemonicIndex(0);
        CloseButton.setActionCommand("close");
        CloseButton.addActionListener(this);
        
        controlbox.add(RunButton);
        controlbox.add(CloseButton);

        this.pack();
        //this.show();
        this.setVisible(true);
    }

    
    protected static boolean VerifyHeader(String [] expected, String [] header)
    {
    	if (header.length != expected.length)
            return false;
        for (int i = 0; i < expected.length; i++)
        {
            if (expected[i].compareTo(header[i]) != 0) {
            	return false;
            }    
        }
        return true;
    }
    
 
    /**
     * Read known accounts into Accounts hash
     * 
     * @throws Exception
     * @param Implicit: KnownAccounts
     */
    @SuppressWarnings("resource")
	private void ReadAccounts() throws Exception
    {
    	Accounts = new Hashtable<Integer, String>();
        CSVReader reader;
        String [] nextLine;
        int lineNumber;
        try { reader = new CSVReader(new FileReader(KnownAccounts)); }
        catch (FileNotFoundException e) { throw new Exception("Accounts file not found"); }
        lineNumber = 0;
        while ((nextLine = reader.readNext()) != null) {
            lineNumber++;
            if (lineNumber == 1)
            {
                if (!VerifyHeader(KnownHeader, nextLine))
                    throw new Exception("Account file format mismatch!");
            }
            else
            {
                Accounts.put(new Integer(nextLine[0]), nextLine[1]);
            }
        }
        reader.close();
    }

    
    /**
     * Read account corrections into Corrections hash
     * 
     * @throws Exception
     * @param Implicit: CorrectionsFile
     */
    @SuppressWarnings("resource")
	private void ReadCorrections() throws Exception
    {
    	Corrections = new Hashtable<String, Correction>();
        CSVReader reader;
        String [] nextLine;
        int lineNumber;
        try { reader = new CSVReader(new FileReader(CorrectionFile)); }
        catch (FileNotFoundException e) { throw new Exception("Corrections file not found"); }
        lineNumber = 0;
        while ((nextLine = reader.readNext()) != null) {
            lineNumber++;
            if (lineNumber == 1)
            {
                if (!Correction.VerifyImportFormat(nextLine))
                    throw new Exception("Account file format mismatch!");
            }
            else
            {
                Corrections.put(nextLine[1], new Correction(nextLine, lineNumber));
            }
        }
        reader.close();
    }


    /**
     * Read import items from CSVimport file
     * @throws Exception
     */
    @SuppressWarnings("resource")
	private void ReadImports() throws Exception
    {
    	Items = new Vector<LineItem>();
        CSVReader reader;
        String [] nextLine;
        int lineNumber;

        try { reader = new CSVReader(new FileReader(CSVimport)); }
        catch (FileNotFoundException e) { throw new Exception("Import file not found"); }
        
        lineNumber = 0;
        while ((nextLine = reader.readNext()) != null) {
        	lineNumber++;
            if (lineNumber == 1)
            {
            	if (fileType.equals(FileTypes.CHECKFREE)) {
                   if (!LineItem.VerifyImportFormat(nextLine))
                       throw new Exception(FileTypes.CHECKFREE+" import format mismatch!");
            	}
            	else if (fileType.equals(FileTypes.METAVANTE)) {
            	   if (!MetavanteConversion.VerifyImportFormat(nextLine))
                       throw new Exception(FileTypes.METAVANTE+" import format mismatch!");
            	}
            }
            else
            {
            	try {
                	LineItem i = null;
                	if (fileType.equals(FileTypes.CHECKFREE)) {
                       i = new LineItem(nextLine, lineNumber, this);
                       Items.add(i);
                       if (i.Corrected) correctedCount++;
                   	   if (i.AutoCorrected) autoCorrectedCount++;
                	}
                    else if ((fileType.equals(FileTypes.METAVANTE) && (nextLine[0].toUpperCase().indexOf("TOTAL")==-1))) {
                       MetavanteConversion mc = new MetavanteConversion(nextLine, lineNumber, CSVimport.getAbsolutePath());	
                       i = new LineItem(mc.getLineItem(), lineNumber, this);
                       Items.add(i);
                       if (i.Corrected) correctedCount++;
                   	   if (i.AutoCorrected) autoCorrectedCount++;
                   	}
                	
                }
                catch (DropException d)
                {
                	JOptionPane.showMessageDialog(this, d.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            	catch (ReversalException r)
                {
                	JOptionPane.showMessageDialog(this, r.toString(), "Payment Reversal Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        reader.close();
    }


    
    /**
     * Locate new account numbers
     */
    private void LocateNewAccounts() throws Exception
    {
    	Iterator<LineItem> iter = Items.iterator();
        for (LineItem i = iter.next();
             i != null;
             i = iter.hasNext() ? iter.next() : null)
        {
        	if (!Accounts.containsKey(i.getCustAcctNum()))
            {
            	CorrectionDialog cd = new CorrectionDialog(this, i, true);
                cd.setVisible(true);

                if (cd.isStop())
                {
                    throw new Exception("Batch stopped. Please start again.");
                }
                else if (cd.isCorrected())
                {
                    int acctNum = cd.getCorrectedAcctNum();
                    Correction c = new Correction(i, acctNum);
                    Corrections.put(i.CustAcctNumText, c);
                    CorrectionsUpdated = true;
                    i.setCustAcctNum(cd.getCorrectedAcctNum());
                    correctedCount++;
                }
                else if (cd.isDrop())
                {
                    iter.remove();
                    DropException d = i.new DropException();
                    JOptionPane.showMessageDialog(this, d.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                if (!cd.isDrop())
                {
                    Accounts.put(i.getCustAcctNum(), i.CustName);
                    KnownAccountsChanged = true;
                    newItemCount++;
                }
            }
        }
    }

    
    /** Locate transactions that are not supported for export and report & remove them
     * 
     * @throws Exception
     */
    private void FilterOutUnsupported() throws Exception
    {
    	Iterator<LineItem> iter = Items.iterator();
        for (LineItem i = iter.next();
             i != null;
             i = iter.hasNext() ? iter.next() : null)
        {
            if (i.PaymentDescription.compareTo("PAYMENT") != 0)
            {
                iter.remove();
                DropException d = i.new DropException();
                JOptionPane.showMessageDialog(this, d.toString(), "Unsupported transaction type: Process manually", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    @SuppressWarnings("unused")
	private static final String TRAILER_REC = "2";
    private static final String FILLER      = "                                                           ";

    /**
     * Export items to BANKPOST.ASC format
     */
    private void ExportItems() throws IOException, Exception
    {
    	FileWriter out = new FileWriter(OutFileName.getText());
        int count = 0;
        double total = 0;

        for (LineItem i : Items)
        {
            count++;
            total += i.PaymentAmount;
            out.write(i.ExportItem());
        }
        
        String trailer = "2";
        DecimalFormat f = new DecimalFormat("00000");
        trailer += f.format(count);
        f.applyPattern("0000000000");
        trailer += f.format(total * 100);
        DateFormat df = new SimpleDateFormat("MMddyy");
        trailer += df.format(new Date());
        trailer += FILLER;
        trailer += "\n";
        
        out.write(trailer);
        out.close();
        
        String msg = "Export completed:\n"
        	+ "    Corrected items:      " + correctedCount + "\n"
        	+ "    Auto-corrected items: " + autoCorrectedCount + "\n"
        	+ "    New items:            " + newItemCount + "\n"
            + "    Total Items:          " + count + "\n"
            + "    Amount:               " + NumberFormat.getCurrencyInstance().format(total);
        JOptionPane.showMessageDialog(this, msg, "Export Completed", JOptionPane.INFORMATION_MESSAGE);

        // upload the file via sftp
        SSH_upload su = new SSH_upload(this, Items);
        su.setVisible(true);
        if (su.getDoConnect())
        {
            su.DoConnect();
            JOptionPane.showMessageDialog(this, "Upload completed", "Upload Completed", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /** There were corrections made this session, save them back to the corrections file
     * 
     *
     */
    private void ExportCorrections() throws IOException
    {
    	FileWriter w = new FileWriter(CorrectFileName.getText());
        CSVWriter cf = new CSVWriter(w);

        cf.writeNext(Correction.CorrectionHeader);
        for (Correction k : Corrections.values())
        {
            cf.writeNext(k.export());
        }

        cf.close();
    }

    
    /** There were new acounts corrected or verified this session, save them back to the known accounts file
     * 
     *
     */
    private void ExportKnownAccounts() throws IOException
    {
    	FileWriter w = new FileWriter(AcctFileName.getText());
        CSVWriter af = new CSVWriter(w);

        String line[] = new String[2];
        af.writeNext(KnownHeader);
        for (int i : Accounts.keySet())
        {
            line[0] = Integer.toString(i);
            line[1] = Accounts.get(i);
            af.writeNext(line);
        }
        
        af.close();
    }
    

    /** Process Swing API events
     * @param ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {
        String event = e.getActionCommand();
        if (event.equals("close"))
            System.exit(0);        
        else if (event.equals("run"))
        {
            try {
                ReadAccounts(); 
                ReadCorrections();
                ReadImports();
                LocateNewAccounts();
                FilterOutUnsupported();
                if (CorrectionsUpdated)
                    ExportCorrections();
                if (KnownAccountsChanged)
                    ExportKnownAccounts();
                ExportItems();
            }
            catch (Exception error) {
                String msg = error.getMessage(); 
                if (msg.compareTo("") == 0)
                    msg = "Unknown error";
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (event.equals("acctf"))
        {
            if (FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                KnownAccounts = FileChooser.getSelectedFile();
                AcctFileName.setText(KnownAccounts.getAbsolutePath());
            }
        }
        else if (event.equals("correctf"))
        {
            if (FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                CorrectionFile = FileChooser.getSelectedFile();
                CorrectFileName.setText(CorrectionFile.getAbsolutePath());
            }
        }
        else if (event.equals("inputf"))
        { 
        	OutputChosen = false; // reset
            if (FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                CSVimport = FileChooser.getSelectedFile();
                InFileName.setText(CSVimport.getAbsolutePath());
                
                fileType = (String)JOptionPane.showInputDialog(
                                    this,
                                    "Select the import file type",
                                    "Customized Dialog",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    FileTypes.getFileTypes(),
                                    FileTypes.CHECKFREE);

                if (!OutputChosen)
                {
                    String shorterName = CSVimport.getAbsolutePath();
                    shorterName = shorterName.substring(0,shorterName.lastIndexOf('.'));
                    shorterName += ".asc";

                    BankPost = new File(shorterName);
                    OutFileName.setText(shorterName);

                    OutputChosen = true;
                }

                InputChosen = true;
            }
        }
        else if (event.equals("outputf"))
        {
            if (FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                BankPost = FileChooser.getSelectedFile();
                OutFileName.setText(BankPost.getAbsolutePath());
                OutputChosen = true;
            }
        }

        if (InputChosen && OutputChosen)
            RunButton.setEnabled(true);
        else
            RunButton.setEnabled(false);
    }


    /** Main method for CheckFreeImporter
     * @param args
     */
    public static void main(String[] args)
    {
    	try {
        	@SuppressWarnings("unused")
        	CheckFreeImporter cfi = new CheckFreeImporter(); 
        }
        catch (Exception e)
        {
            System.out.print(e);
        }
    }


    /**
     * @return Returns the checkFreeDirectory.
     */
    public String getDirectory()
    {
        return CheckFreeDirectory;
    }
    
    /**
     * returns the filetype being processed, Checkfree or Metavante
     * @return
     */
    public String getFileType() {
    	return fileType.trim();
    }
}
