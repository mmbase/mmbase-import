<%@page import="java.util.Enumeration,nl.leocms.applications.NatMMConfig"%>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:locale language="nl">
<jsp:useBean id="form" scope="session" class="nl.leocms.forms.MembershipForm"/>
<%
String actionId = request.getParameter("action");
if(actionId==null) { actionId =""; }
if (actionId.equals("generatenew")){
   form.generateAsciiFile(cloud,cloud.getUser().getIdentifier());
}
%>
<html>
   <head>
   <title></title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   </head>
   <SCRIPT LANGUAGE="JavaScript">
   <!--
   var cancelClick = false;
   function doDelete(prompt) {
   	var conf;
   	if (prompt && prompt!="") {
   		conf = confirm(prompt);
   	} else conf=true;
   	cancelClick=true;
   	return conf;
   }
   var newwin;
   function downloadPopup() {
      newwin = window.open('memberdownload.jsp?action=showlast','memberidfinder','width=400,height=200,scrollbars=no,toolbar=no,location=no');
      return false;
   }
   //-->
   </SCRIPT>
   <body style="overflow:auto;" <% if(actionId.equals("generatenew")) { %>onload="javascript:downloadPopup();setTimeout('newwin.focus();',250);"<% } %> >
   <%

   if(actionId.equals("showlast")) {
      %>
      <div align="right"><a href="#" onClick="window.close()"><img src='/editors/img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
      <h2>Download bestand met nieuwe leden</h2>
      <mm:listnodes type="events_attachments" constraints="titel LIKE '%_leden%'" orderby="titel" directions="DOWN" max="1">
			<a href="<mm:attachment />">klik hier om het bestand te downloaden (rechter muisklik, opslaan als)</a>	
      </mm:listnodes>
   	<%
   } else {
   %>
      <h2>Download nieuwe leden</h2>
      <table class="formcontent" style="width:500px;margin-bottom:20px;">
         <tr bgcolor="6B98BD">
         	<td style="width:33%;"><strong>Aantal records</strong></td>
         	<td style="width:33%;"><strong>Datum uitgelezen</strong></td>
         	<td style="width:33%;"><strong>Uitgelezen door</strong></td>		
         </tr>
         <% int iSizeNotExported = form.notDownloadedMembersList(cloud).size();
         int rowCount = 1; 
      	if (iSizeNotExported!=0){
            %>
         	<tr>
         		<td style="width:33%;">
         			<%= iSizeNotExported %>
         		</td>
         		<td style="width:33%;"><a onclick="return doDelete('Weet u zeker dat u dit bestand wilt opslaan?');"
                        title="Klik hier om dit bestand te downloaden."
         					onmousedown="cancelClick=true;"
                        href="memberdownload.jsp?action=generatenew">nog niet uitgelezen</a></td>
         		<td style="width:33%;"></td>
         	</tr>
            <% rowCount++;
      	} 
         %>	
      	<mm:listnodes type="events_attachments" constraints="titel LIKE '%_leden%'" orderby="titel" directions="DOWN">
      	<tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
      		<td style="width:33%;">
      			<mm:field name="bron"/>
      		</td>
      		<td style="width:33%;">
      			<a onclick="return doDelete('Weet u zeker dat u dit bestand wilt opslaan?');"
                  title="Klik hier om dit bestand te downloaden (rechter muisklik, opslaan als)."
      				onmousedown="cancelClick=true;" href="<mm:attachment />">
      				<mm:field name="creatiedatum" jspvar="creatiedatum" vartype="String" write="false">
      					<mm:time time="<%=creatiedatum%>" format="EEE d MMM yyyy HH:mm"/>h
      				</mm:field>
      			</a>	
      		</td>
      		<td style="width:33%;">
      			<mm:related path="gebruikt,users">
      				<nobr><mm:field name="users.voornaam"/> <mm:field name="users.tussenvoegsel"/> <mm:field name="users.achternaam"/></nobr> 
      			</mm:related>
      		</td>
      	</tr>
      	</mm:listnodes>
      </table>
      <% 
   } %>
</body>
</html>
</mm:locale>
</mm:cloud>