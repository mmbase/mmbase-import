package org.mmbase.util;

public class Escape {

	static public String singlequote(String str) {
		 String line=null,obj;
		 int idx;
		if (str!=null) {
		 /* Single ' protection */
		 line=new String("");
		 obj=new String(str);
		 while((idx=obj.indexOf('\''))!=-1) {
			 line+=obj.substring(0,idx)+"''";
			 obj=obj.substring(idx+1);
		 }
		 line=line+obj;
		}
		 return(line);
	}	
}
