<meta http-equiv="Pragma" contect="no-cache">
<%@ page import="com.finalist.cmsc.module.glossary.Glossary" %>
<%@ page import="com.finalist.cmsc.module.glossary.GlossaryFactory" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>

var aDicArray = new Array();
var aDesctiptionArray = new Array();


<%
    Glossary glossary = GlossaryFactory.getGlossary();
    Map<String, String> terms = glossary.getTerms();
    Iterator<String> keySet = terms.keySet().iterator();
    while (keySet.hasNext()) {
        String key = keySet.next();

%>
aDicArray[i] = <%=key%>;
aDesctiptionArray[i] = "<%=terms.get(key)%>";

<%
    }
%>
