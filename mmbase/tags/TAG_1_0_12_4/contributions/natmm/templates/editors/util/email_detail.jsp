<%@page import="nl.leocms.authorization.*,org.mmbase.bridge.*,nl.leocms.util.*,nl.leocms.workflow.*,nl.leocms.content.*" %>
<%@include file="/taglibs.jsp" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<mm:cloud jspvar="cloud">
<mm:locale language="nl">
<mm:import jspvar="paginaID" externid="p"/>
<mm:import jspvar="rubriekID" externid="r"/>
<mm:import jspvar="sp" externid="sp"/>
<mm:import jspvar="constraints" externid="constraints"/>
<mm:import jspvar="day" externid="day"/>
<mm:import jspvar="month" externid="month"/>
<mm:import jspvar="year" externid="year"/>
<mm:import jspvar="period" externid="period"/>
<mm:import jspvar="selection" externid="selection"/>
<mm:import jspvar="count" externid="count"/>
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body leftmargin="10" topmargin="10" marginwidth="0" marginheight="0" style="overflow:auto;">
<table class="formcontent" style="width:100%;">
   <tr>
      <td valign="bottom" width="60%">
	 <h4>
	    Statistieken&nbsp; 
	    <% if ((!rubriekID.equals("-1"))&&(!paginaID.equals("-1"))) {%>
	       <mm:node number="<%= rubriekID %>"><mm:field name="naam"/></mm:node>
	       &nbsp;-&nbsp;
	       <mm:node number="<%= paginaID %>"><mm:field name="titel"/></mm:node>
	    <% } else {%>
	       Totaal
	    <% } %>	
	 </h4>
      </td>
      <td valign="top">
  	 (verstuurd naar <a href="mailto:informatieaanvraag@natuurmonumenten.nl">
	 <nobr>informatieaanvraag@natuurmonumenten.nl</nobr></a>)
      </td>
      <td valign="top">
         <% String sParams = "?day="+ day + "&month=" + month + "&year=" + year + "&period=" + period + "&selection=" + selection; %>
         <a href="email_stats.jsp<%= sParams %>"><img alt="Terug naar Statistieken pagina" src="../img/left.gif" border="0"></a>
      </td>
   </tr>
</table>
<%= sp %>
<br/>
<br/>
<% if (Integer.parseInt(count)<51) {%>
   <%= (Integer.parseInt(count)==1 ? "Er is 1 email bericht." : "Er zijn " + count + "email berichten." ) %>
<% } else {%>
   Er zijn meer dan 50 email berichten. Alleen de eerste 50 worden getoond.
<% } %>
<br/>
<br/>
<% String [] sArMailStatus = new String[5]; 
   sArMailStatus[0] = "waiting to be scheduled";
   sArMailStatus[1] = "delivered";
   sArMailStatus[2] = "delivery failed";
   sArMailStatus[3] = "marked as spam";
   sArMailStatus[4] = "scheduled, waiting to be delivered";%>
<table class="formcontent" style="width:100%;margin-bottom:20px;">
   <% int rowCount = 1; %>
   <tr bgcolor="6B98BD">
      <td><strong>From</strong></td>
      <td><strong>Message</strong></td>
      <td><strong>Status</strong></td>			
   </tr>
   <% String sNodes = paginaID;
      String sPath = "pagina,related,email";
      if (paginaID.equals("-1")) { 
         sNodes = "form_template";
         sPath= "paginatemplate,gebruikt,pagina,related,email"; 
      }%>
   <mm:list nodes="<%= sNodes %>" path="<%= sPath %>" constraints="<%= constraints %>" 
            fields="email.from,email.mailedtime,email.mailstatus,email.body" max="50">
      <tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
         <td valign="top">
      	    <mm:field name="email.from"/>
       	    <br/>
	    send:&nbsp;
	    <mm:field name="email.mailedtime" jspvar="mailedtime" vartype="String" write="false">
	       <mm:time time="<%=mailedtime%>" format="dd-MM-yyyy"/>
	    </mm:field>	
         </td>
         <td valign="top">
     	    <mm:field name="email.body" jspvar="sMessage" vartype="String" write="false">
    	       <% sMessage = sMessage.substring(sMessage.indexOf("<html>")+6);
	          sMessage = sMessage.substring(0,sMessage.indexOf("</html>")); 
	          sMessage = sMessage.replaceAll("<br/><br/>","<br/>"); %>
	       <%= sMessage %>
	    </mm:field>
         </td>
         <td valign="top">
            <mm:field name="email.mailstatus" jspvar="iStatus" vartype="Integer">
	       <%= sArMailStatus[iStatus.intValue()] %>
	    </mm:field>
         </td>
      </tr>
   </mm:list>
</table>
</body>
</html>
</mm:locale>
</mm:cloud>