package com.bottinifuel.pladd.CheckFree;


/**************************************************************************
*
*   File name: FileType
*   Defines all of the Files types CheckFreeImporter supports
* 	 
* @author carlonc
* 
*************************************************************************/
/* Change Log:
* 
*   Date         Description                                        Pgmr
*  ------------  ------------------------------------------------   -----
*  Apr 09,2013    Intial Dev...                                     carlonc 
*************************************************************************/
public class FileTypes {
	public static final String CHECKFREE     = "CheckFree";   
	public static final String METAVANTE     = "Metavante";   
	
	public static Object[] getFileTypes() {
		Object[] possibilities = {CHECKFREE, METAVANTE};
		return possibilities;
	}
}
