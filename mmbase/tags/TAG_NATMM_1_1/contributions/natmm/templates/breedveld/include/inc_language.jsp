<%@include file="../include/inc_english.jsp" %>
<%@include file="../include/inc_french.jsp" %>

<%! public String lan(String lanId, String wordToTranslate) {
	if(lanId.equals("english")) {
		return lan_english(wordToTranslate);
	} else if(lanId.equals("french")) {
		return lan_french(wordToTranslate);
	} else {
		return wordToTranslate;
	}
}
%>

<% String language = (String) session.getAttribute("language");
   if(language == null) language = "english";
%>  
