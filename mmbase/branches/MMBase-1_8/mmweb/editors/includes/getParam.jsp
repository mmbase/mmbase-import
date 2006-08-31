<%-- Author: H.Hangyi / 26.06.2002 / www.mmatch.nl --%>

<%! public String getParam(String paramString, String param, String seperator) {
		String value = "";
		int from = paramString.indexOf(param); 
		if(from!=-1){
			from += param.length();
			int to = paramString.indexOf(seperator,from);
			if(to==-1){ to = paramString.length(); }
			value = paramString.substring(from,to); 
		}
		return value;
}
%>
