<%@ page errorPage="exception.jsp"
%><%@page import="org.mmbase.bridge.*,
	javax.servlet.jsp.JspException,
	java.util.Locale,
	java.util.List,
	java.util.ArrayList,
	nl.hcs.mmbase.TranslationModule"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="loginmethod" from="parameters">loginpage</mm:import
><mm:locale language="en"><mm:cloud method="$loginmethod" loginpage="login.jsp" jspvar="cloud"><mm:log jspvar="log">

<mm:import id="portal"><%= session.getServletContext().getInitParameter("portal.name") %></mm:import>
<mm:import externid="objectnumber" required="true"  jspvar="objectnumber" vartype="Integer"/>
<mm:import externid="fields" required="true" jspvar="fields"/>
<mm:import externid="input" jspvar="input">text</mm:import>
<mm:import externid="editid"/>
<mm:import externid="clientwidth" jspvar="clientw" vartype="Integer">400</mm:import>
<mm:import externid="show">vertical</mm:import>

<% int clientwidth =  (clientw.intValue() / 2) - 30; %>

<%	Locale locale = org.mmbase.bridge.LocalContext.getCloudContext().getDefaultLocale();
	String localLanguage = locale.getLanguage();
	String localCountry = locale.getCountry();
	if ("".equals(localCountry.trim())) {
		if ("nl".equals(localLanguage)) {
			localCountry = "NL";
		}
		if ("en".equals(localLanguage)) {
			localCountry = "UK";
		}
	}
%><mm:import id="locallanguage"><%= localLanguage %></mm:import
><mm:import id="localcountry"><%= localCountry %></mm:import
><mm:import id="locallocale"><%= localLanguage %>_<%= localCountry %></mm:import>

<%	List fieldList = new ArrayList();
	List guifieldList = new ArrayList();
	List inputList = new ArrayList();

	int i = 0;
	while((i = fields.indexOf(",")) > -1) {
		fieldList.add(fields.substring(0, i));
		fields = fields.substring(i+1);
	}
	fieldList.add(fields);

	input = input.toLowerCase();
	int j = 0;
	while((j = input.indexOf(",")) > -1) {
		inputList.add(input.substring(0, j));
		input = input.substring(j+1);
	}
	inputList.add(input);
	
	while ( fieldList.size() >= inputList.size() ) {
		inputList.add("text");
	}
	
	TranslationModule translationModule = 
		(TranslationModule) org.mmbase.module.Module.getModule("TRANSLATIONS");
%>

<html>
<head>
<title></title>
<link href="../style/wizard.css" type="text/css" rel="stylesheet" />
<link href="../style.css" type="text/css" rel="stylesheet" />
<script src="../javascript/tools.js" language="javascript"><!--help IE--></script>
<script src="../javascript/wysiwyg.js" language="javascript"><!--help IE--></script>
<script src="../javascript/translate.js" language="javascript"><!--help IE--></script>
<script language="javascript"><!--	window.history.forward(1);	--></script>
<script language="javascript">
<!-- 
	if (browserutils.ie5560win) { 
		window.attachEvent("onload",start_wysiwyg); 
	}
-->
</script>
</head>

<body onunload="doOnUnLoad_ew();" onload="doOnLoad_ew();">
<form id="edit_translate" action="process.jsp" method="post" name="form">
	<input name="curform" value="edit_translate" type="hidden"/>
	<input name="cmd" value=""  type="hidden" id="hiddencmdfield"/>
	<input name="language" value="en" type="hidden" />
	<input name="objectnumber" value="<mm:write referid="objectnumber"/>" type="hidden" />
	<input name="fields" value="<mm:write referid="fields"/>" type="hidden" />
	<input name="input" value="<mm:write referid="input"/>" type="hidden" />
	<input name="editid" value="<mm:write referid="editid"/>" type="hidden" />

<mm:compare referid="show" value="vertical">
	<%@include file="includes/vertical.jsp" %>
</mm:compare>
<mm:compare referid="show" value="horizontal">
	<%@include file="includes/horizontal.jsp" %>
</mm:compare>

</form>
</body>
</html>

</mm:log></mm:cloud></mm:locale>