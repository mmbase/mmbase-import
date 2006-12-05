<%@page import="nl.leocms.util.tools.HtmlCleaner,java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<%
String objectID = request.getParameter("o");
String pannoConstraint = "(panno.embargo < '" + (nowSec+quarterOfAnHour) + "') AND (panno.reageer='0' OR panno.verloopdatum > '" + nowSec + "' )";
%>
<mm:node number="<%=objectID%>">
   <mm:related path="readmore,panno" constraints="<%= pannoConstraint %>" max="1">
   <mm:node element="panno">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
      	<td>
            <mm:field name="titel_fra" jspvar="titel" vartype="String" write="false">
      	      <mm:isnotempty><span class="colortitle"><%= titel.toUpperCase() %></span><br></mm:isnotempty>
      		</mm:field>
      		<mm:field name="omschrijving_fra" jspvar="omschrijving" vartype="String" write="false">
               <% if(omschrijving!=null&&!HtmlCleaner.cleanText(omschrijving,"<",">","").trim().equals("")) { 
                  %><%= omschrijving %><br/>
               <% } %>
      		</mm:field>
      	</td>
      </tr>
      </table>
      <br/>
    </mm:node>
    </mm:related>
</mm:node>
</mm:cloud>