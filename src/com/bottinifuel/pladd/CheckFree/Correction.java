/*
 * Created on Jan 25, 2006 by pladd
 *
 */
package com.bottinifuel.pladd.CheckFree;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Correction
{
    public final int    LineNumber;
    public final int    AccountNumber;

    public final String   OrigAccountNumber;
    public final Calendar ChangeDate;
    public final String   CustName;
    public final String   CustAddr1;
    public final String   CustAddr2;
    public final String   CustCity;
    public final String   CustState;
    public final String   CustZip;
    
    public static final String [] CorrectionHeader = { "CorrectedAcct#",
                                                       "Acct#",
                                                       "CorrectedDate",
                                                       "Name",
                                                       "Address1",
                                                       "Address2",
                                                       "City",
                                                       "State",
                                                       "Zip" };
    
    public static final String DateFmt = "yyyy/MM/dd HH:mm";

    /** Constrct a Correction from it's String[] CSV file format
     * 
     * @param csvLine
     * @param lineNum
     * @throws Exception
     */
    public Correction(String [] csvLine, int lineNum) throws Exception
    {
        LineNumber = lineNum;

        if (csvLine.length != 9)
            throw new Exception("Correction import error: line #" + LineNumber
                    + " incorrect number of items on line: " + csvLine.length);

        try { AccountNumber = Integer.parseInt(csvLine[0]); }
        catch (NumberFormatException e) {
            throw new Exception("Correction import error: line #" + LineNumber
                    + " account number unreadable \"" + csvLine[0] + "\"");
        }

        int i = 1;
        OrigAccountNumber = csvLine[i++];

        ChangeDate = Calendar.getInstance();
        ChangeDate.setLenient(false);
        SimpleDateFormat df = new SimpleDateFormat(DateFmt);
        Date d = df.parse(csvLine[i++]); 
        if (d == null)
            throw new Exception("Correction file: date format incorrect, line #" + LineNumber);
        ChangeDate.setTime(d);

        CustName  = csvLine[i++];
        CustAddr1 = csvLine[i++];
        CustAddr2 = csvLine[i++];
        CustCity  = csvLine[i++];
        CustState = csvLine[i++];
        CustZip   = csvLine[i++];
    }

    
    /** Construct a Correction from a line item original and the new acct number
     * 
     * @param orig
     * @param correctAccountNum
     */
    public Correction(LineItem orig, int correctAccountNum)
    {
        LineNumber = 0;
        AccountNumber = correctAccountNum;
        OrigAccountNumber = orig.CustAcctNumText;
        ChangeDate = Calendar.getInstance();
        CustName  = orig.CustName;
        CustAddr1 = orig.CustAddr1;
        CustAddr2 = orig.CustAddr2;
        CustCity  = orig.CustCity;
        CustState = orig.CustState;
        CustZip   = orig.CustZip;
    }

    /** Check that the first line of a CheckFree import file has the items we're expecting
     * 
     * @param line Array containing the column headings to be checked
     * @return true if the file has the proper format
     */
    static public boolean VerifyImportFormat(String [] line)
    {
        return CheckFreeImporter.VerifyHeader(CorrectionHeader, line);
    }

    
    /** Return a string array representation for saving to a CSV file
     * 
     * @return String[]
     */
    public String [] export()
    {
        String line[] = new String[CorrectionHeader.length];
        NumberFormat nf = new DecimalFormat("0000000");
        
        DateFormat df = new SimpleDateFormat(Correction.DateFmt);
        int i = 0;
        line[i++] = nf.format(AccountNumber);
        line[i++] = OrigAccountNumber;
        line[i++] = df.format(ChangeDate.getTime());
        line[i++] = CustName;
        line[i++] = CustAddr1;
        line[i++] = CustAddr2;
        line[i++] = CustCity;
        line[i++] = CustState;
        line[i++] = CustZip;
        
        return line;
    }

    
    /** String representation of the item -- intended for debugging
     * @return String 
     */
    public String toString()
    {
        String rcode = "Original: " + OrigAccountNumber + " Corrected: " + AccountNumber + "\n";
        rcode += CustName + "\n" + CustAddr1 + "\n";
        if (CustAddr2.compareTo("") != 0)
            rcode += CustAddr2 + "\n";
        rcode += CustCity + ", " + CustState + " " + CustZip;
        
        return rcode;
    }
}
