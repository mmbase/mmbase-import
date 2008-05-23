<%-- Check whether project categorie is a selection and if so return the string 'selection'--%>
<%! public String selection(String categorie, String language) { 
	String selectionStr = "";
	if(!categorie.equals("Opleiding")) {
		selectionStr = "(" + lan(language, "selectie") + ")";
	}
	return selectionStr;
} %>