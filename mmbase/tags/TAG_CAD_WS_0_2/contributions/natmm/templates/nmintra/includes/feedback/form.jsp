<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="../templateheader.jsp" %>
<%@include file="../calendar.jsp" %>
<%@include file="script.jsp" %>
<%@include file="../getresponse.jsp" %>
<mm:import externid="object" jspvar="objectId">-1</mm:import>
<mm:import externid="field" jspvar="fieldId">titel</mm:import>
<mm:import externid="param" jspvar="paramId">o</mm:import>
<mm:import externid="ntype" jspvar="ntypeId">object</mm:import>
<mm:import externid="by" jspvar="byId">de organisator</mm:import>
<%
String sUrl = "";
if (ntypeId.equals("opleiding")) { 
	sUrl = "educations.jsp"; 
} else {
	sUrl = "event_blueprints.jsp";
} 
if(!postingStr.equals("")){

  postingStr += "|";
	boolean bAllFieldsOk = true;
	String warningMessage = "U bent vergeten de volgende velden in te vullen:<ul>";
	HashMap hm = new HashMap();
	hm.put("namesender","Uw naam");
	hm.put("emailsender","Uw email");
	hm.put("text","Uw mening over de opleiding");
  hm.put("score","Een cijfer");

	Set set = hm.entrySet(); 
	Iterator it = set.iterator();

	while (it.hasNext()) {
		Map.Entry me = (Map.Entry)it.next(); 
		String answerValue = getResponseVal((String)me.getKey(),postingStr);
		if (answerValue.equals("")) {
			warningMessage += "<li>" + me.getValue() + "</li>";
			bAllFieldsOk = false;
		}
	}
	

	if (bAllFieldsOk) {

		String messageTitle = "Bedankt voor uw feedback!";
		String messageBody = "Uw feedback zal binnen een dag worden toegevoegd aan de " + ntypeId;
		String messageHref = sUrl + "?p=" + paginaID + "&" + paramId + "=" + objectId;
		String messageLinktext = "terug naar de " + ntypeId;
		String messageLinkParam = "";
		ArrayList al = new ArrayList();
		al.add("namesender");
		al.add("emailsender");
    al.add("namereceiver");
		al.add("topic");
	  al.add("text");
		al.add("score");
		Iterator itr = al.iterator();
		String sFieldname = "";
    %>
		<mm:createnode type="feedback" id="feedback">
			<% while (itr.hasNext()) { 
					sFieldname = (String)itr.next();%>
					<mm:setfield name="<%= sFieldname %>"><%= getResponseVal(sFieldname,postingStr) %></mm:setfield>
			<% } %>
			<mm:setfield name="begindate"><%= nowSec %></mm:setfield>
		</mm:createnode>
		<mm:node number="<%= objectId %>" id="education"/>
		<mm:createrelation role="related" source="education" destination="feedback" />
    <cache:flush scope="application" group="<%= paginaID %>" />
		<%@include file="../showmessage.jsp" %><%
	} else {
		String messageTitle = "Uw mening kon helaas nog niet worden verwerkt";
		String messageBody = warningMessage + "</ul>";
		String messageHref = "javascript:history.go(-1)";
		String messageLinktext = "terug naar het formulier";
		String messageLinkParam = ""; %>
		<%@include file="../showmessage.jsp" %><%
	}	

} else {
   %>
   <form name="formulier" method="post">
   	<table cellpadding="0" cellspacing="0" align="left" border="0" style="width:100%;margin-left:10px;">
   	<tr>
   		<td style="padding-bottom:10px;">
        <b>
            Feedback op:&nbsp;<mm:node number="<%= objectId %>"><mm:field name="<%= fieldId %>"/></mm:node>
   			</b>
   		</td>
   	</tr>
   	<tr>
   		<td>Uw naam(*)</td>
   	</tr>
   	<tr>
   		<td><input type="text" name="namesender" size="50"></td>
   	</tr>
   	<tr>
   		<td>Uw email(*)</td>
   	</tr>
   	<tr>
   		<td><input type="text" name="emailsender" size="50"></td>
   	</tr>
      <tr>
   		<td>Naam van de <%= ntypeId %></td>
   	</tr>
   	<tr>
   		<td><input type="text" name="topic" value="<mm:node number="<%= objectId %>"><mm:field name="<%= fieldId %>"/></mm:node>" size="50"></td>
   	</tr>
   	<tr>
   		<td>Naam van <%= byId %></td>
   	</tr>
   	<tr>
   		<td><input type="text" name="namereceiver" size="50"></td>
   	</tr>
   	<tr>
   		<td>Uw mening(*)</td>
   	</tr>
   	<tr>
   		<td><textarea rows="3" cols="52" name="text" wrap="physical"></textarea></td>
   	</tr>
   	<tr>
   		<td>Uw beoordeling, een cijfer van 0 en met 10. (*)</td>
   	</tr>
   	<tr>
   		<td>
   		   <select name="score" style="width:282px;">
                        <option value="">Selecteer
   				<% for(int i=10; i>-1; i--) { %>
   				      	<option value="<%= i %>"><%= i %>
   				<% } %>
   			</select>	
   		</td>
   	</tr>
   	<tr>
   		<td style="padding-left:166px;padding-top:10px;padding-bottom:10px;">
         <% String qStr = request.getQueryString();
            int pstPos = qStr.indexOf("&pst=");
            if(pstPos>-1) {
               qStr = qStr.substring(0,pstPos);
            }
         %>
   		<a href="<%= sUrl %>?<%= qStr %>" onClick="return createPosting(this);"
   			>verstuur je feedback</a><img src="media/spacer.gif" width="10" height="1"></div>
   		</td>
   	</tr>
   	<tr>
   		<td>
        (*) vul minimaal deze velden in i.v.m. een correcte afhandeling. 
   		</td>
   	</tr>
   	</table>
   </form>
   <%
} %>
</mm:cloud>