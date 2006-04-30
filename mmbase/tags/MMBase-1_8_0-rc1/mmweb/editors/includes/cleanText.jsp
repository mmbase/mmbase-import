<%! public String replace(String text, String oldStr, String newStr) {
	// replaces the tag by a space (important for <br> and <p>
	int oldStrPos =  text.indexOf(oldStr); 
	while(oldStrPos>-1){
		text = text.substring(0,oldStrPos) + newStr + text.substring(oldStrPos+oldStr.length());
		oldStrPos =  text.indexOf(oldStr, oldStrPos + newStr.length()); 
	}		
	return text;
}
%>

<%! public String cleanText(String text, String tagOpenStr, String tagCloseStr) {
	// replaces the tag by a space (important for <br> and <p>
	int tagOpen =  text.indexOf(tagOpenStr); 
	int tagClose =  text.indexOf(tagCloseStr,tagOpen);
	while(tagOpen>-1){
		text = text.substring(0,tagOpen) + " " + text.substring(tagClose+1);
		tagOpen =  text.indexOf(tagOpenStr); 
		tagClose =  text.indexOf(tagCloseStr,tagOpen);
	}		
	return text;
}
%>

<%! public String cleanEmptyTag(String text, String startTag, String closeTag) {
	// replaces occurences of <b> </b>; <p> </p>
	int startTagOpen =  text.indexOf(startTag); 
	while(startTagOpen>-1){
		int startTagClose =  text.indexOf(">",startTagOpen);
		int closeTagOpen =  text.indexOf(closeTag,startTagClose); 
		int closeTagClose =  text.indexOf(">",closeTagOpen);		
		if(closeTagOpen>-1){
			String tagedString = text.substring(startTagClose+1,closeTagOpen);
			int nbspPos = tagedString.indexOf("&nbsp;");
			while(nbspPos>-1){
				tagedString = tagedString.substring(0,nbspPos) + tagedString.substring(nbspPos+6);
				nbspPos = tagedString.indexOf("&nbsp;");
			}
			if(tagedString.trim().equals("")){
				text = text.substring(0,startTagOpen) + "&nbsp;" + text.substring(closeTagClose+1);
				startTagOpen =  text.indexOf(startTag,startTagOpen+1);
			} else {
				startTagOpen =  text.indexOf(startTag,closeTagClose+1);			
			}
		} else {
			startTagOpen = -1;
		}
	}
	return text;
}
%>

<%! public String cleanParam(String text, String paramStr) {
	// deletes occurences of param in text
	int paramOpen =  text.indexOf(paramStr); 
	while(paramOpen>-1){
		// find the max of the quotation mark and the space before the closing greater than
		int paramClose =  text.indexOf(">",paramOpen)-1; // -1 to leave the tag undamaged
		if(paramClose>-1){ // there is a close tag
			int paramCloseQMark =  text.indexOf("\"",text.indexOf("\"",paramOpen)+1);
				if((paramCloseQMark==-1)||(paramCloseQMark>paramClose)) paramCloseQMark = paramClose;
			int paramCloseSpace =  text.indexOf(" ",paramOpen);
				if((paramCloseSpace==-1)||(paramCloseSpace>paramClose)) paramCloseSpace = paramClose;
			if(paramCloseQMark<paramCloseSpace){
				paramClose = paramCloseSpace;
			} else {
				paramClose = paramCloseQMark;
			}
		} else { // no close tag, leave unchanged
			paramClose = paramOpen;
		}
		text = text.substring(0,paramOpen) 
			//	+ "<!-- qm=" + paramCloseQMark + " s=" + paramCloseSpace + " c=" + paramCloseQMark + " -->"
				+ text.substring(paramClose+1);
		paramOpen =  text.indexOf(paramStr); 
	}		
	return text;
}
%>

<%! public String stripText(String text) {
	// cleans text from & and spaces
	int charPos =  text.indexOf("&"); 
	while(charPos>-1){
		text = text.substring(0,charPos) + "en" + text.substring(charPos+1);
		charPos =  text.indexOf("&"); 
	}
	charPos =  text.indexOf(" "); 
	while(charPos>-1){
		text = text.substring(0,charPos) + "_" + text.substring(charPos+1);
		charPos =  text.indexOf(" "); 
	}		
	return text;
}
%>

<%! public String stripParam(String paramString, String param, String seperator) {
		int from = paramString.indexOf(param); 
		if(from!=-1){
			int to = paramString.indexOf(seperator,from);
			if(to==-1){ to = paramString.length(); }
			paramString = paramString.substring(0,from-1)+paramString.substring(to); ; 
		}
		return paramString;
}
%>
