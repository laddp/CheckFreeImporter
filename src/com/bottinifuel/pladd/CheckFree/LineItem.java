/*
 * Created on Jan 24, 2006 by pladd
 *
 */
package com.bottinifuel.pladd.CheckFree;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LineItem
{
    public final int      LineNum;

    public final String   MapID;
    public final String   MerchantName;

    private int           CustAcctNum;
    public final String   CustAcctNumText;
    public final String   CustName;
    public final String   CustAddr1;
    public final String   CustAddr2;
    public final String   CustCity;
    public final String   CustState;
    public final String   CustZip;

    public final String   PaymentDescription;
    public final double   PaymentAmount;
    public final Calendar PaymentDate;

    public final String   ReturnReasonCode;
    public final Calendar ReturnDate;

    public final double   AdjustmentAmount;
    public final Calendar OriginalPaymentDate;

    public final String   CSV_Filename;

    public final boolean  Corrected;
    public final boolean  AutoCorrected;
    
    private final static String [] ImportHeader = {"MapID",
                                                 "Merchant Name",
                                                 "Customer Account Number",
                                                 "Customer Name",
                                                 "Customer Address",
                                                 "Customer Address2",
                                                 "Customer City",
                                                 "Customer State",
                                                 "Customer Zip Code",
                                                 "Payment Description",
                                                 "Payment Amount",
                                                 "Payment Date",
                                                 "Return Reason Code",
                                                 "Return Date",
                                                 "Adjustment Amount",
                                                 "Original Payment Date",
                                                 "CSV Filename" };

    
    public LineItem(String [] csvLine, int lineNum, CheckFreeImporter cf) throws Exception, DropException
    {
        LineNum = lineNum;

        if (csvLine.length != 17)
            throw new Exception("Line #" + LineNum
                                + ": Incorrect number of data fields: " + csvLine.length
                                + " expecting 17");
        MapID = csvLine[0];
        MerchantName = csvLine[1];

        CustAcctNumText = csvLine[2];

        CustName = csvLine[3];
        CustAddr1 = csvLine[4];
        CustAddr2 = csvLine[5];
        CustCity = csvLine[6];
        CustState = csvLine[7];
        CustZip = csvLine[8];
               
        PaymentDescription = csvLine[9];
        try { PaymentAmount = Double.parseDouble(csvLine[10]); }
        catch (NumberFormatException e) {
            throw new Exception("Line #" + LineNum
                    + ": Error reading payment amount: \""
                    + csvLine[9] + "\"");
        }
        PaymentDate = GetDate(csvLine[11], LineNum);
               
        ReturnReasonCode = csvLine[12];
        ReturnDate = GetDate(csvLine[13], LineNum);
        try { AdjustmentAmount = Double.parseDouble(csvLine[14]); }
        catch (NumberFormatException e) {
            throw new Exception("Line #" + LineNum
                    + ": Error reading adjustment amount: "
                    + csvLine[14] + "\"");
        }
        OriginalPaymentDate = GetDate(csvLine[15], LineNum);

        CSV_Filename = csvLine[16];
       
        boolean isCorrected = false;
        // NOTE: attempting to parse the account number must be last
        //       because the CorrectionDialog calls toString() on this object
        //       and that requires the other fields be initialized first
        if (cf.Corrections.containsKey(csvLine[2]))
        {
            CustAcctNum = ((Correction)cf.Corrections.get(csvLine[2])).AccountNumber;
            isCorrected = true;
            AutoCorrected = true;
        }
        else
        {
        	AutoCorrected = false;
            int tryIt;
            try { 
            	tryIt = Integer.parseInt(csvLine[2]);  
            }
            catch (NumberFormatException e) {
                CorrectionDialog cd = new CorrectionDialog(cf, this, false);
                cd.setVisible(true);
                
                if (cd.isStop())
                {
                    throw new Exception("Batch stopped. Please start again.");
                }
                else if (cd.isCorrected())
                {
                    tryIt = cd.getCorrectedAcctNum();
                    Correction c = new Correction(this, tryIt);
                    cf.Corrections.put(csvLine[2], c);
                    cf.CorrectionsUpdated = true;
                    isCorrected = true;
                }
                else
                {
                    throw new DropException();
                }
            }
            CustAcctNum = tryIt; 
        }
        Corrected = isCorrected;
    }

    
    private Calendar GetDate(String s, int l) throws Exception, NumberFormatException
    {
        if (s.compareTo("0") == 0)
            return null;
        
        Calendar rc = Calendar.getInstance();
        rc.clear();
        rc.setLenient(false);
        String year, month, day;
        year  = s.substring(0,4);
        month = s.substring(4,6);
        day   = s.substring(6,8);
        DateFormat df = new SimpleDateFormat("yyyyMMdd"); 
        try {
            rc.set(Integer.parseInt(year),
                   Integer.parseInt(month)-1,
                   Integer.parseInt(day));
            String d = df.format(rc.getTime());
            if (d.compareTo(s) != 0)
                throw new Exception("Line #" + l
                                    + ": Invalid date: "
                                    + s + "\"");
        }
        catch (NumberFormatException e) {
            throw new Exception("Line #" + l
                    + ": Error reading date: "
                    + s + "\"");
        }
        
        return rc;
    }

    
    /** Check that the first line of a CheckFree import file has the items we're expecting
     * 
     * @param line Array containing the column headings to be checked
     * @return true if the file has the proper format
     */
    static public boolean VerifyImportFormat(String [] line)
    {
        return CheckFreeImporter.VerifyHeader(ImportHeader, line);
    }

    
    private static final String DB_NUMBER   = "0001";
    private static final String DIV_NUMBER  = "01";
    private static final String INVOICE_NUM = "000000";
    private static final String FILLER16    = "                ";
    private static final String CHECK_NUM   = "000000";
    private static final String BATCH_NUM   = "0000";
    private static final String SEQ_NUM     = "00000";
    private static final String FLAG1       = "M";
    private static final String POST_CODE   = "0224";
    public String ExportItem()
    {
        String rc = "1" + DB_NUMBER + DIV_NUMBER;
        DecimalFormat df = new DecimalFormat("0000000");
        rc += df.format(CustAcctNum);
        rc += INVOICE_NUM;
        rc += FILLER16;
        df.applyPattern("00000000");
        rc += df.format(PaymentAmount * 100);
        rc += CHECK_NUM;
        rc += BATCH_NUM;
        rc += SEQ_NUM;
        rc += FLAG1;
        rc += POST_CODE;
        rc += FILLER16;
        rc += "\n";
        return rc;
    }


    public String toString()
    {
        String rcode = CustName + "\n" + CustAddr1 + "\n";
        if (CustAddr2.compareTo("") != 0)
            rcode += CustAddr2 + "\n";
        rcode += CustCity + ", " + CustState + " " + CustZip;
        
        return rcode;
    }

    public class DropException extends Exception
    {
        private static final long serialVersionUID = 1L;
        private String msg; 
                
        public DropException()
        {
            msg = "Transaction dropped: please process manually:\n";
            int i = 0;
            msg += ImportHeader[i++] + ": " + MapID              + "\n";
            msg += ImportHeader[i++] + ": " + MerchantName       + "\n";
            msg += ImportHeader[i++] + ": " + CustAcctNumText    + "\n";
            msg += ImportHeader[i++] + ": " + CustName           + "\n";
            msg += ImportHeader[i++] + ": " + CustAddr1          + "\n";
            msg += ImportHeader[i++] + ": " + CustAddr2          + "\n";
            msg += ImportHeader[i++] + ": " + CustCity           + "\n";
            msg += ImportHeader[i++] + ": " + CustState          + "\n";
            msg += ImportHeader[i++] + ": " + CustZip            + "\n";
            msg += ImportHeader[i++] + ": " + PaymentDescription + "\n";
            msg += ImportHeader[i++] + ": " + PaymentAmount      + "\n";
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String date;
            if (PaymentDate != null)
                date = df.format(PaymentDate.getTime());
            else
                date = "0";
            msg += ImportHeader[i++] + ": " + date               + "\n";
            msg += ImportHeader[i++] + ": " + ReturnReasonCode   + "\n";
            if (ReturnDate != null)
                date = df.format(ReturnDate.getTime()); 
            else
                date = "0";
            msg += ImportHeader[i++] + ": " + date               + "\n";
            msg += ImportHeader[i++] + ": " + AdjustmentAmount   + "\n";
            if (OriginalPaymentDate != null)
                date = df.format(OriginalPaymentDate.getTime()); 
            else
                date = "0";
            msg += ImportHeader[i++] + ": " + date               + "\n";
            msg += ImportHeader[i++] + ": " + CSV_Filename       + "\n";
        }
        
        public String toString()
        {
            return msg;
        }
    }

    /**
     * @param custAcctNum The custAcctNum to set.
     */
    public void setCustAcctNum(int custAcctNum)
    {
        CustAcctNum = custAcctNum;
    }

    /**
     * @return Returns the custAcctNum.
     */
    public int getCustAcctNum()
    {
        return CustAcctNum;
    }
    
    /**
     * returns the expected length of a line item 
     * @return
     */
    public static int getExpectedLength() {
    	return ImportHeader.length; 
    }
}
