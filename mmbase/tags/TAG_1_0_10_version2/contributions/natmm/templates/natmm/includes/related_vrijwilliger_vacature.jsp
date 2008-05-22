<%@include file="/taglibs.jsp" %>
<%
String vacatureID = request.getParameter("v");
ArrayList al = new ArrayList();
al.add("omschrijving_fra"); 
al.add("omschrijving_de");
al.add("functienaam"); 
al.add("embargo"); 
al.add("verloopdatum"); 
al.add("omschrijving");		
al.add("functieinhoud"); 
al.add("functieomvang"); 
al.add("duur"); 
al.add("afdeling"); 
al.add("opleidingseisen"); 
al.add("competenties"); 
al.add("salarisschaal");
al.add("omschrijving_eng");

ArrayList aLabels = new ArrayList();
aLabels.add("omschrijving_fra"); 
aLabels.add("omschrijving_de");
aLabels.add("Functienaam vrijwilliger"); 
aLabels.add("embargo"); 
aLabels.add("verloopdatum"); 
aLabels.add("omschrijving");    
aLabels.add("Wat verwachten wij van de vrijwilliger?"); 
aLabels.add("Functieomvang"); 
aLabels.add("Duur"); 
aLabels.add("Standplaats"); 
aLabels.add("opleidingseisen"); 
aLabels.add("Extra opmerkingen"); 
aLabels.add("Wat bieden wij?");
aLabels.add("omschrijving_eng");

%>
<%@include file="../includes/time.jsp" %>
<%@include file="../includes/calendar.jsp" %>
<mm:cloud>
<mm:node number="<%= vacatureID %>">
<span class="colortitle"><mm:field name="titel"/></span><br/>
<table class="dotline"><tr><td height="3"></td></tr></table>
<mm:field name="verloopdatum" jspvar="verloopdatum" vartype="Long" write="false">
<% 
if(verloopdatum.longValue()<nowSec) { 
   %>
   Deze vacature is reeds gesloten.
   <%
} else {
   %>
   <table width="100%" cellspacing="0" cellpadding="0">
   <% Iterator ial = al.iterator();
      Iterator itLabels = aLabels.iterator();
      
   	while(ial.hasNext()) {
   		String sElem = (String) ial.next();
         String elemLabel = (String) itLabels.next();
   %>
   		<mm:field name="<%= sElem%>" jspvar="thisField" vartype="String" write="false">
   			<mm:isnotempty>
   				<tr>
   					<% 
                  if (sElem.indexOf("omschrijving")==-1) {
                     %>
   						<mm:fieldlist fields="<%= sElem %>">
   							<td valign="top">
   								<span class="colortitle">
                              <% if(sElem.equals("embargo")) { %>
                                 Gepubliceerd&nbsp;op
                              <% } else if(sElem.equals("verloopdatum")) { %>
                                 Sluitingsdatum
                              <% } else { %>
                                 <%= elemLabel %>
                              <% } %>
                           </span>
   							</td>
   							<td valign="top">	
   								&nbsp;&nbsp;|&nbsp;&nbsp;
   							</td>
   							<td>	
   								<% if(sElem.equals("embargo")||sElem.equals("verloopdatum")) { 
                                 long td = Integer.parseInt(thisField); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
                                 String dateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
                                 %>
                                 <%= dateStr %>
                           <% } else { %>
                              <mm:fieldinfo type="guivalue" />
                           <% } %>
   							</td>
   						</mm:fieldlist>	
   						<% 
                  } else { 
                     %>
   					   <td colspan="3" style="padding-top:7px;padding-bottom:15px;"><mm:fieldinfo type="guivalue" /></td>
   						<% 
                  } %>		
   				</tr>		
   			</mm:isnotempty>
   		</mm:field>
   	<% }%>
      <mm:relatednodes type="attachments" orderby="title">
         <mm:first>
            <tr>
         		<td valign="top">
         			<span class="colortitle">Vrijwilligersprofiel</span>
         		</td>
         		<td valign="top">	
         			&nbsp;&nbsp;|&nbsp;&nbsp;
         		</td>
         		<td>	
         </mm:first>
         <a href="<mm:attachment />" title="download <mm:field name="filename" />" target="_blank" class="attachment"><mm:field name="title" /></a><br/>
         <mm:last>
      	      </td>
            </tr>
         </mm:last>
      </mm:relatednodes>
   </table>
   <%
} %>
</mm:field>
</mm:node>
</mm:cloud>