/**************************************************************************
*
*   File name: MetavanteConversion 
*   Converts a Metavante csv file into the same format as a Checkfree csv. 
* @author carlonc
* 
*************************************************************************
* Change Log:
* 
*   Date         Description                                        Pgmr
*  ------------  ------------------------------------------------   -----
*  Mar 05,2013   New class for version 2.0.                         carlonc 
*************************************************************************/
package com.bottinifuel.pladd.CheckFree;

import java.io.File;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MetavanteConversion {
	
	private final static String [] ImportHeader = {"Account Number",
                                                   "Type",
                                                   "Reversal Amount",
                                                   "Rev Eff Date",
                                                   "Payment Amount",
                                                   "Pay Eff Date",
                                                   "First Name",
                                                   "Last Name",
                                                   "Street Address 1",
                                                   "Street Address 2",
                                                   "City",
                                                   "State",
                                                   "Zip",
                                                   "Reference ID",
                                                   "Description" };
	
	public final int      LineNum;
	String[] lineItem;
	
	public MetavanteConversion(String [] csvLine, int lineNum, String fileName) throws Exception, ReversalException
	    {
		    LineNum = lineNum;
		    if (csvLine.length != 15)
	            throw new Exception("Line #" + lineNum
	                                + ": Incorrect number of data fields: " + csvLine.length
	                                + " expecting 15");

		    lineItem     = new String[LineItem.getExpectedLength()];
	        lineItem[0]  = csvLine[13];               // MapID 
	        lineItem[1]  = "";                        // does not exist in metavante 
	        
	        if (csvLine[0].startsWith("'")) {         // accounts in metvante may start with a ', strip it off
	        	lineItem[2] = csvLine[0].substring(1);// acct number 
	        }
	        else 
                lineItem[2]  = csvLine[0];            // acct Number  
            
	        lineItem[3]  = csvLine[6].trim()+" "+csvLine[7].trim(); // CustName 
            lineItem[4]  = csvLine[8];                // CustAddr1 
            lineItem[5]  = csvLine[9];                // CustAddr2 
            lineItem[6]  = csvLine[10];               // CustCity
            lineItem[7]  = csvLine[11];               // CustState 
            lineItem[8]  = csvLine[12];               // CustZip
            if (csvLine[1].trim().equals("P"))        
            	lineItem[9] = "PAYMENT";              // Payment description
            else     
            	lineItem[9]  = csvLine[1];            // PaymentDescription   
            
            if (csvLine[4].trim().equals(""))
            	lineItem[10] = "0";                   // set to 0 
            else 
                lineItem[10] = csvLine[4].trim();     // PaymentAmount 
            
            if (csvLine[5].trim().equals(""))         // set to 0
            	lineItem[11] = "0";
            else 
            	lineItem[11] = convertDate(csvLine[5]);// PaymentDate 
            
            lineItem[12] = " ";                       // Reason code, not in Metavante 
            
            if (csvLine[2].trim().equals("")) 
            	lineItem[14] = "0";                   // AdjustmentAmount 
            else 
                lineItem[14] = csvLine[2].trim();     // AdjustmentAmount 
            
            lineItem[15] = "0";                       // Original Payment Date, not in Metavante 
            
            int pos = fileName.lastIndexOf(File.separator);
            try {
               lineItem[16] = fileName.substring(pos+1);  // fileName
            }
            catch (IndexOutOfBoundsException e) {
            	lineItem[16] = fileName;	
            }
            // do return date last
            if (csvLine[3].trim().equals(""))
                lineItem[13] = "0";                   // ReturnDate  
            else {
            	lineItem[13] = csvLine[3];  
            	throw new ReversalException(csvLine); // flag a reversal to user	
            } 	
    }
	
	/** Check that the first line of a Metavante import file has the items we're expecting
     * 
     * @param line Array containing the column headings to be checked
     * @return true if the file has the proper format
     */
    static public boolean VerifyImportFormat(String [] line)
    {
    	if (line[0].startsWith("'")) {
        	line[0] = line[0].substring(1); 
        }
        return CheckFreeImporter.VerifyHeader(ImportHeader, line);
    }
	
	public String[] getLineItem() {
		/*for (String s: lineItem) {
	           System.out.println("converted line item="+s);
        }*/
		return lineItem;
	}
	
	private String convertDate(String csvDate) throws Exception {
		SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy");
        Date date;
        try {
			date = dt.parse(csvDate);
			Format formatter = new SimpleDateFormat("yyyyMMdd");
			return formatter.format(date);                                  
		} catch (ParseException e) {
			return "0";
		}
    }
	
	public class ReversalException extends Exception
    {
        private static final long serialVersionUID = 1L;
        private String msg; 
                
        public ReversalException(String[] csvLine)
        {
            msg = "Transaction is a revseral: please process manually:\n";
            int i = 0;
            msg += ImportHeader[i++] + ": " + lineItem[2]  + "\n";  
            msg += ImportHeader[i++] + ": " + lineItem[9]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[14] + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[13] + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[10] + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[11] + "\n";
            msg += ImportHeader[i++] + ": " + csvLine[6]   + "\n";
            msg += ImportHeader[i++] + ": " + csvLine[7]   + "\n"; 
            msg += ImportHeader[i++] + ": " + lineItem[4]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[5]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[6]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[7]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[8]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[0]  + "\n";
            msg += ImportHeader[i++] + ": " + lineItem[9]  + "\n";
        }
        
        public String toString()
        {
            return msg;
        }
    }
 
}
