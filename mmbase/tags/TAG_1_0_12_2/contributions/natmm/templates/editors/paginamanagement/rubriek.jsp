<%@page import="com.finalist.tree.*,
   nl.leocms.authorization.forms.*,
   nl.leocms.util.*,
	 nl.leocms.applications.*,
	 java.util.*,
   org.mmbase.bridge.*,
   nl.leocms.servlets.UrlConverter" %>
<%@include file="/taglibs.jsp" %>
<cache:flush scope="application"/>
<% UrlConverter.getCache().flushAll(); %>
<mm:cloud jspvar="cloud" rank="basic user">
<%

   RubriekHelper rubriekHelper = new RubriekHelper(cloud);
	 int DEFAULT_STYLE = -1;
	 String [] style1 = null;
	 String [] layout = null;
   String cssPath = null;
	
	ApplicationHelper ap = new ApplicationHelper(cloud);
	// todo: create a more generic version for this piece of code
	if(ap.isInstalled("NatMM")) {
		
	  DEFAULT_STYLE = NatMMConfig.DEFAULT_STYLE;
		layout = NatMMConfig.layout;
   	style1 = NatMMConfig.style1;
		cssPath = NatMMConfig.cssPath;
   }
	if(ap.isInstalled("NatNH")) {
	
	  DEFAULT_STYLE = NatNHConfig.DEFAULT_STYLE;
	  style1 = NatNHConfig.style1;
		cssPath = NatNHConfig.cssPath;
   }
	if(ap.isInstalled("NMIntra")) {
	
	  DEFAULT_STYLE = NMIntraConfig.DEFAULT_STYLE;
	  style1 = NMIntraConfig.style1;
    layout = NMIntraConfig.layout;
		cssPath = NMIntraConfig.cssPath;
   }
	
	String sWarning = "";
	if(style1==null) {
		sWarning += "WARNING: style1 is not defined by the available applications<br/>";
		style1 = new String [1];
	}
   if(cssPath==null) {
		sWarning += "WARNING: cssPath is not defined by the available applications<br/>";
		cssPath = "";
	}
  HashMap leocmsStyles = new HashMap();
	for(int i=0; i< style1.length; i++) {
		leocmsStyles.put(cssPath + style1[i] + ".css", style1[i]);
	}
  String rubriekSubsiteNodeNumber = "";
%>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Rubrieken</title>
</head>
<body>
<% if(!sWarning.equals("")) { %><div style="color:red"><%= sWarning %></div><% } %>
<logic:equal name="RubriekForm" property="node" value="">
<h1>Rubriek toevoegen</h1>
De nieuwe rubriek wordt een subrubriek van:
<bean:define id="parentNode" property="parent" name="RubriekForm" scope="request" type="java.lang.String"/>
<b>
    <%
       rubriekSubsiteNodeNumber = rubriekHelper.getSubsiteRubriek(parentNode);
       out.println(rubriekHelper.getPathToRootString(parentNode));
    %>
</b>
</logic:equal>
<logic:notEqual name="RubriekForm" property="node" value="">
<h1>Rubriek wijzigen</h1>
Rubriek:<b>
<bean:define id="nodenr" property="node" name="RubriekForm" scope="request" type="java.lang.String"/>
   <mm:import id="nodenummber"><%= nodenr %></mm:import>
   <%
       rubriekSubsiteNodeNumber = rubriekHelper.getSubsiteRubriek(nodenr);
       out.println(rubriekHelper.getPathToRootString(nodenr));
    %>
</b>
</logic:notEqual>
<mm:import id="level">2</mm:import>
<mm:present referid="nodenummber">
   <mm:node referid="nodenummber">
      <mm:remove referid="level"/>
      <mm:import id="level"><mm:field name="level"/></mm:import>
   </mm:node>
</mm:present>

<html:form action="/editors/paginamanagement/RubriekAction">
<html:hidden property="node"/>
<html:hidden property="parent"/>
<html:hidden property="level"/>
<table class="formcontent">
    <tr><td class="fieldname" width='120'>Naam</td><td><html:text property="naam" size='40' maxlength='40' />
    <span class="notvalid"><html:errors bundle="LEOCMS" property="naam" /></span></td></tr>
<%-- hh
    <tr><td class="fieldname">Naam - frans</td><td><html:text property="naam_fra" size='40' maxlength='40' /></td></tr>
    <tr><td class="fieldname">Naam - engels</td><td><html:text property="naam_eng" size='40' maxlength='40' /></td></tr>
    <tr><td class="fieldname">Naam - duits</td><td><html:text property="naam_de" size='40' maxlength='40' /></td></tr>
<logic:equal name="RubriekForm" property="level" value="1">
</logic:equal>
    <tr><td class="fieldname">Beschikbare talen</td><td><html:checkbox property="fra_active" styleClass="check"/>Frans *</br><html:checkbox property="eng_active" styleClass="check"/>Engels *</br><html:checkbox property="de_active" styleClass="check"/>Duits *</br></td></tr>
<logic:equal name="RubriekForm" property="level" value="1">
    <tr><td class="fieldname">&nbsp;</td><td><html:checkbox property="wholesubsite" styleClass="check"/>Talen gelden voor de hele subsite</td></tr>
</logic:equal>
   <tr><td></td><td><b>* Wanneer bij een subsite een taal wordt uitgeschakeld, zal dit voor de hele subsite gelden. Hoewel het nog steeds mogelijk is binnen een subrubriek dezelfde taal aan te zetten zal dit geen effect hebben, zolang bij de subsite de taal uit staat.</b></td></tr>
--%>
	<% 
	if(layout!=null && layout.length!=0) { 
		%>
		<tr><td class="fieldname">Layout</td><td>
				<html:select property="naam_fra">
					 <html:option value="-1">Layout van parent rubriek</html:option>
					 <%
					 for(int i=0; i<layout.length; i++) {
						%>
						<html:option value="<%= "" + i %>"><%= layout[i] %></html:option>
						<%
					} %>
				</html:select>
			</td>
		</tr>
		<%
	} else {
		%>
		<html:hidden property="naam_fra" value="-1" />
		<%
	} %>
   <tr><td class="fieldname">Style</td>
      <td>
         <html:select property="style">
            <html:option value="parentstyle">Style van parent rubriek</html:option>
            <%
            Iterator stylesIt = leocmsStyles.keySet().iterator();
            while (stylesIt.hasNext()) {
               String key = (String) stylesIt.next();
               String value = (String) leocmsStyles.get(key);
               %>
               <html:option value="<%= key %>"><%= value %></html:option>
               <%
            }
            %>
         </html:select>
      </td>
   </tr>
   <tr><td class="fieldname">Rubriek is zichtbaar</td>
       <td>
        <html:select property="is_visible">
          <html:option value="1">ja</html:option>
          <html:option value="0">nee</html:option>
        </html:select>
       </td>
    </tr>
   <logic:equal name="RubriekForm" property="level" value="2">
        <tr><td class="fieldname">Rubriek is doorzoekbaar</td>
           <td>
           <html:select property="issearchable">
             <html:option value="1">ja</html:option>
             <html:option value="0">nee</html:option>
           </html:select>
           </td>
        </tr>
   </logic:equal>
   <logic:notEqual name="RubriekForm" property="level" value="2">
       <html:hidden property="issearchable" value='' />
   </logic:notEqual>
   <logic:equal name="RubriekForm" property="level" value="1">
        <tr><td class="fieldname">Url (for Google-sitemap)</td><td><html:text property="url" maxlength='100' />
            <span class="notvalid"><html:errors bundle="LEOCMS" property="url" /></span></td></tr>
   </logic:equal>
   <logic:notEqual name="RubriekForm" property="level" value="1">
       <html:hidden property="url" value='' />
   </logic:notEqual>
   <tr><td class="fieldname">Template subdir (only for developers)</td><td><html:text property="url_live" maxlength='100' /></td></tr>
</table>
<table class="formcontent">

   <tr>
      <td>
         <mm:compare referid="level" value="0" inverse="true">
            <html:submit value='Opslaan' style="width:90"/>
         </mm:compare>
         &nbsp;<html:cancel value='Annuleren' style="width:90"/>
      </td>
   </tr>
</table>
</html:form>

</body>
</html>
</mm:cloud>