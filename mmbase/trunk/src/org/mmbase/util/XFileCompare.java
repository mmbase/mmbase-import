package org.mmbase.util;


/**
 * Class to compare XFile object depending on their comparefield.
 * @see vpro.james.util.SortedVector
 * @see vpro.james.util.CompareInterface
 * @see vpro.james.util.XFile
 * 
 * @author David V van Zeventer
 * @version 16 November 1998
 */
public class XFileCompare implements CompareInterface {
	String compareField;

	public XFileCompare(String compareField) {
		this.compareField = compareField;
	}

	public int compare(Object thisOne, Object other) {
		int result = 0;
		long longresult=0;
		XFile xfileobj1 = (XFile)thisOne;
		XFile xfileobj2 = (XFile)other;
		
		if (compareField.equals("filepath")){		//Compare objects using their filepathname.
			result = (xfileobj1.getFilePath()).compareTo(xfileobj2.getFilePath());
			//System.out.println("XFileCompare: Filepath compare result= "+result);

		}else {
			if (compareField.equals("modtime")){	//Compare objects using their modification time.
				longresult =  xfileobj1.getModTime() - xfileobj2.getModTime();
				result = (int)(longresult/1000);
				//System.out.println("XFileCompare: Modificationtime compare result= "+result);
			}
		}
		return (result);
	}
} 
