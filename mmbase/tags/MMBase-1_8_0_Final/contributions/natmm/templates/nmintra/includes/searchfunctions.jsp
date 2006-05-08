<%! public String searchResults(TreeSet searchResultList) {
	String searchResults = searchResultList.toString();
	searchResults = searchResults.substring(1,searchResults.length()-1);
	return HtmlCleaner.replace(searchResults," ","");
}
%><%! public String superSearchString(String searchText) {
	// special characters are two characters in the database
	// therefore searching on for instance 'a' should both include _ (for a) and __ (for special versions of a)
	char wildcard = '%';
	
	String searchString = searchText.toUpperCase();
	searchString = HtmlCleaner.replace(searchString,"OE","" + wildcard);
	
	char translatedChar [] = HtmlCleaner.translatedChar();
	searchString = searchString.replace('A',wildcard);
	searchString = searchString.replace('E',wildcard);
	searchString = searchString.replace('I',wildcard);
	searchString = searchString.replace('O',wildcard);
	searchString = searchString.replace('U',wildcard);
	for(int c= 0; c<48; c++){
		searchString = searchString.replace(translatedChar[c],wildcard);
	}
	if(searchString.replace(wildcard,' ').trim().equals("")) {
		searchString = searchText.toUpperCase();
	}
	return searchString;
}
%><%! public String subSearchString(String searchText) {
	// map special characters to their standard counterparts
	
	String searchString = searchText.toUpperCase();
	String rawString [] = HtmlCleaner.rawString(); 
	char translatedChar [] = HtmlCleaner.translatedChar(); 
	char standardChar [] = HtmlCleaner.standardChar();
	
	searchString = HtmlCleaner.replace(searchString,"OE","O");
	for(int c= 0; c<24; c++){ //*** replace all &charentities; ***
		searchString = HtmlCleaner.replace(searchString,rawString[c].toUpperCase(),"" + standardChar[c]);
	}
	for(int c= 0; c<24; c++){ //*** replace all \uFFFF chars ***
		searchString = searchString.replace(translatedChar[c],standardChar[c]);
	}
	return searchString;
}
%><%! public Vector createSearchTerms(String searchString) {
	TreeSet searchTermSet = new TreeSet(); // *** use TreeSet to delete duplicates ***
	String searchTerm = "";
	searchString = searchString.toUpperCase() + " ";
	while(!searchString.equals("")) { 
		if(searchString.substring(0,1).equals("\"")) { // string whithin "  " ?
			searchString = searchString.substring(1);
			try{
				searchTerm = searchString.substring(0,searchString.indexOf("\"")); 
				searchString = searchString.substring(searchString.indexOf("\"")+1);
			} catch (Exception e) { // no closing "
				searchTerm = "";
			}
		} else { // take next word
			searchTerm = searchString.substring(0,searchString.indexOf(" ")); 
			searchString = searchString.substring(searchString.indexOf(" ")+1); 
		}
		searchTerm = searchTerm.replace('-',' '); // for 'search-fast' search on the string 'search fast'
		if(!searchTerm.equals("")){
			searchTermSet.add(searchTerm);
		}
	}
	TreeMap searchTermMap = new TreeMap(); // *** use SortedMap to sort on length ***
	Iterator searchTermList = searchTermSet.iterator();
	while(searchTermList.hasNext()) {
		searchTerm = (String) searchTermList.next();
		//Integer key = new Integer(searchTerm.length()*10);
		int key = searchTerm.length()*10;
		while (searchTermMap.containsKey(new Integer(key))){
			key++;
		}
		searchTermMap.put(new Integer(key), searchTerm);
	}
	Vector searchTerms = new Vector(); // *** create resulting Vector from sortedMap ***
	while(!searchTermMap.isEmpty()) {
		Integer lastKey = (Integer) searchTermMap.lastKey();
		searchTerm = (String) searchTermMap.get(lastKey);
		searchTermMap.remove(lastKey);
		searchTerms.add(searchTerm);
	}
	return searchTerms;
}
%><%! public Vector findSearchTerm(String textStr, String searchTerm, int fromIndex, boolean debug) {
	// *** find the first and smallest occurence searchTerm (including %) after fromIndex ***
	// *** precondition textStr does not contain any &charentities; ***
	debug = false;
	if(debug) System.out.println( "Debug info for findSearchTerm");
	
	textStr = textStr.toUpperCase();
	searchTerm = searchTerm.toUpperCase();
	char wildcard = '%';
	Vector fromToIndex = new Vector();

	if(debug) System.out.println("searchterm: " + searchTerm + "\ntext: " + textStr + "\nfrom: " + fromIndex);  

	if(searchTerm.indexOf(wildcard)==-1){
		
		int sPos = textStr.indexOf(searchTerm,fromIndex);
		if(sPos>-1) {
			int ePos = sPos + searchTerm.length();
			fromToIndex.add(new Integer(sPos));
			fromToIndex.add(new Integer(ePos));
		}
	} else {

		Vector subString = new Vector();
		Vector subStrPos = new Vector();
		int leadingWildcards = 0;
		int followingWildcards = 0;
		while(!searchTerm.equals("")) {
			int wPos = searchTerm.indexOf(wildcard);
			if(wPos>0) {
				subString.add(searchTerm.substring(0,wPos));
				subStrPos.add(new Integer(-1));
				followingWildcards = searchTerm.length()-wPos;
			} else if (wPos==0) {
				if(subString.isEmpty()) { leadingWildcards++; }
			} else {
				subString.add(searchTerm);
				subStrPos.add(new Integer(-1));
				followingWildcards = 0;
				searchTerm = "";
			}
			if((wPos+1)<searchTerm.length()) {
				searchTerm = searchTerm.substring(wPos+1);
			} else {
				searchTerm = "";
			}
		}

		if(debug) System.out.println("leading wildcards: " + leadingWildcards  + " following wildcards: " + followingWildcards
					+ "\nsubstrings: " + subString  + " positions: " + subStrPos + "\n*** walk forwards ***" );

		if(!subString.isEmpty()) {

			// *** step 1: walk forwards ***
			int cPos = 0;
			if(fromIndex==0) fromIndex = leadingWildcards; // *** make sure "F" is not found when search on "%F" ***
			int sPos = textStr.indexOf((String) subString.elementAt(cPos),fromIndex);
			while(sPos>-1) {
				subStrPos.set(cPos,new Integer(sPos));
				cPos++;
				if(cPos<subString.size()&&(sPos+1)<textStr.length()) {
					sPos = textStr.indexOf((String) subString.elementAt(cPos),sPos+1);
				} else {
					sPos = -1;
				}
				if(debug) System.out.println( "substrings: " + subString  + " positions: " + subStrPos );
			}

			if(debug) System.out.println( "*** walk backwards ***" );
			// *** step 2: walk backwards ***
			if(cPos==subString.size()) {
				cPos--;
				sPos = ((Integer) subStrPos.elementAt(cPos)).intValue();		
				while(cPos>0) {
					cPos--;
					sPos = textStr.substring(0,sPos).lastIndexOf((String) subString.elementAt(cPos));
					subStrPos.set(cPos,new Integer(sPos));	
					if(debug) System.out.println("substrings: " + subString  + " positions: " + subStrPos);
				}
			}

			if(debug) System.out.println("\nsubstrings: " + subString  + " positions: " + subStrPos);

			sPos = ((Integer) subStrPos.lastElement()).intValue();
			if(sPos>-1) {
				sPos = ((Integer) subStrPos.firstElement()).intValue()-leadingWildcards;
				fromToIndex.add(new Integer(sPos));
				sPos = ((Integer) subStrPos.lastElement()).intValue()+((String) subString.lastElement()).length()+followingWildcards;
				if(sPos>textStr.length()) sPos = textStr.length(); // *** to do: under which conditions does this occur ? ***
				fromToIndex.add(new Integer(sPos));				
			}
		} 
	}
	if(debug&&!fromToIndex.isEmpty()) {
		int sPos = ((Integer) fromToIndex.elementAt(0)).intValue();
		int ePos = ((Integer) fromToIndex.elementAt(1)).intValue();
		System.out.println( "\nfromToIndex: " + fromToIndex
					+ "\ntext: " + textStr.substring(0,sPos) + "|" + textStr.substring(sPos,ePos) + "|" + textStr.substring(ePos) );
	}
	
	return fromToIndex;
}
%><%! public Vector findSearchTerm(String textStr, String searchTerm, boolean debug) {
	return findSearchTerm(textStr,searchTerm,0,debug);
}
%><%! public int minPos(String textStr, Vector searchTerms, boolean useSub,boolean debug) {
	// *** find the position of the first searchTerm in the textStr ***	
	int minPos = textStr.length();
	Iterator searchTermList = searchTerms.iterator();
	while(searchTermList.hasNext()) {
		String searchTerm = (String) searchTermList.next();
		if(useSub) searchTerm = subSearchString(searchTerm);
		Vector fromToIndex = findSearchTerm(textStr,searchTerm,debug);
		if(!fromToIndex.isEmpty()) {
			int sPos =  ((Integer) fromToIndex.firstElement()).intValue();
			if(minPos>sPos) minPos = sPos;
		}
	}
	if(debug) System.out.println( "minPos on Search Terms: " + minPos + " (useSub= " + useSub + ")");
	return minPos;
}
%><%! public int startPos(String textStr, Vector searchTerms, boolean useSub,boolean debug) {
	// *** find the first position of a searchTerm in the textStr ***	
	int maxPos = textStr.length();
	int startPos = maxPos;
	Iterator searchTermList = searchTerms.iterator();
	while(startPos==maxPos&&searchTermList.hasNext()) {
		String searchTerm = (String) searchTermList.next();
		if(useSub) searchTerm = subSearchString(searchTerm);
		Vector fromToIndex = findSearchTerm(textStr,searchTerm,debug);
		if(!fromToIndex.isEmpty()) {
			startPos =  ((Integer) fromToIndex.firstElement()).intValue();
		}
	}
	if(debug) System.out.println( "startPos on Search Terms: " + startPos + " (useSub= " + useSub + ")");
	return startPos;
}
%><%! public String highLightSearchTerm(String textStr, String searchTerm, String highlight, boolean useSuper, boolean debug) {
	int sPos = 0;
	int ePos = 0;
	int i = 0;
	Vector fromToIndex = findSearchTerm(textStr,searchTerm,debug);
	while(!fromToIndex.isEmpty()&&(i<5)) // *** not more than five marks for each searchTerm ***
	{	sPos = ((Integer) fromToIndex.elementAt(0)).intValue();
		ePos = ((Integer) fromToIndex.elementAt(1)).intValue();
		// the highlight should not fall inside another highlight
		// the highlight should not be part of tag e.g. u
		if(HtmlCleaner.insideTag(textStr,sPos,ePos,"<" + highlight + ">","</" + highlight + ">")
			||HtmlCleaner.insideTag(textStr,sPos,ePos,"<",">")
			||textStr.substring(sPos,ePos).indexOf("<")>-1
			||textStr.substring(sPos,ePos).indexOf(">")>-1
			) {
			if(debug) System.out.println("Not going to highlight: " + textStr.substring(sPos,ePos) + " (useSuper= " + useSuper + ")" );
			fromToIndex = findSearchTerm(textStr,searchTerm,ePos,debug);
		} else {
			if(debug) System.out.println("Going to highlight: " + textStr.substring(sPos,ePos) + " (useSuper= " + useSuper + ")" );
			textStr = textStr.substring(0,sPos) 
					+ "<" + highlight + ">" + textStr.substring(sPos,ePos) + "</" + highlight + ">"
					+ textStr.substring(ePos);
			fromToIndex = findSearchTerm(textStr,searchTerm,ePos+4,true);
			i++;
		}
	}
	return textStr;
}
%><%! public String highlightSearchTerms(String textStr, Vector searchTerms, String highlight) {

	boolean debug = false;
	if(debug) System.out.println( "Debug info for highlightSearchTerms");

	// *** strip textStr from html taggings *** 
	textStr = HtmlCleaner.cleanText(textStr,"<",">");
	
	textStr = HtmlCleaner.replace(textStr,"oe","o");
	textStr = HtmlCleaner.replace(textStr,"OE","O");
	
	// *** map the &charentities; to their \uFFFF counterparts ***
	String rawString [] = HtmlCleaner.rawString();
	char translatedChar [] = HtmlCleaner.translatedChar();
	for(int c= 0; c<rawString.length; c++){ 
		textStr = HtmlCleaner.replace(textStr,rawString[c],"" + translatedChar[c]);
	}
		
	int startPos = startPos(textStr, searchTerms, false, debug);
	if(startPos==textStr.length()) { // *** try the subSearchTerm ***
		startPos = startPos(subSearchString(textStr), searchTerms, true, debug);
	}
		
	if(startPos==textStr.length()) {
		startPos = 0;
	}
	// *** try to find beginning of sentence ***
	int dotPos = textStr.lastIndexOf(". ",startPos);
	if(dotPos>-1) { // found beginning of sentence
		startPos = dotPos+2;
	} else { //  *** probably first sentence of paragraph or title ***
		startPos = 0;
	}
	textStr = textStr.substring(startPos); 
	int spacePos = textStr.indexOf(" ",180); 
	if(spacePos>-1) { 
		textStr = textStr.substring(0,spacePos);
	}
	Iterator searchTermList = searchTerms.iterator();
	while(searchTermList.hasNext()) {
		String searchTerm = (String) searchTermList.next();
		int length = textStr.length();
		textStr = highLightSearchTerm(textStr, searchTerm, highlight, false, debug);
		
		if(length==textStr.length()) { // *** try the superSearchTerm ***
			searchTerm = superSearchString(searchTerm); 
			textStr = highLightSearchTerm(textStr, searchTerm, highlight, true, debug);
		}
	}
	
	// *** map the \uFFFF to their &charentities; counterparts ***
	for(int c= 0; c<rawString.length; c++){ 
		textStr = HtmlCleaner.replace(textStr,"" + translatedChar[c],rawString[c]);
	}
	return textStr;
}
%><%! public boolean containsSearchTerms(String textStr, Vector searchTerms) {
	// *** check whether textStr contains the searchTerms ***
	boolean containsSearchTerms = true;
	Iterator searchTermList = searchTerms.iterator();
	while(searchTermList.hasNext()) {
		String searchTerm = (String) searchTermList.next();
		if(textStr.indexOf(searchTerm)==-1) {
			containsSearchTerms = false;
		}
	}
	return containsSearchTerms;
}
%>