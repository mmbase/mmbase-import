<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/time.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/getstyle.jsp" %>
<mm:cloud jspvar="cloud">
<%

String shortyRol = "";
PaginaHelper ph = new PaginaHelper(cloud);

if(artikelID != null) { 
%><mm:node number="<%=artikelID%>" notfound="skipbody">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td style="vertical-align:top;padding-right:10px;width:374px;">
   	<% if(!natuurgebiedID.equals("-1")){%>
   		<mm:node number="<%=natuurgebiedID%>">
   			<span class="colortitle"><mm:field name="naam" /></span><br>
   			<jsp:include page="../includes/panno_extratext.jsp">
               <jsp:param name="o" value="<%= natuurgebiedID %>" />
            </jsp:include>
   		</mm:node>
   	<% } %>
      <jsp:include page="../../includes/artikel_1_column.jsp">
         <jsp:param name="o" value="<%= artikelID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
      </jsp:include>
	</td>
	<td style="vertical-align:top;padding-left:10px;width:185px;">
      <jsp:include page="../../includes/shorty.jsp">
	      <jsp:param name="s" value="<%= natuurgebiedID %>" />
	      <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sp" value="natuurgebieden,posrel" />
	      <jsp:param name="sr" value="2" />
	   </jsp:include>
	   <% if(!natuurgebiedID.equals("-1")){%>
      <mm:listcontainer path="natuurgebieden,posrel,artikel">
		<mm:constraint field="artikel.number" operator="EQUAL" value="<%=artikelID%>" inverse="true" />
		<mm:list nodes="<%=natuurgebiedID%>" fields="natuurgebieden.naam,artikel.titel,artikel.number" orderby="posrel.pos">
				<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
				<mm:first>
					<span class="colortitle">Meer over <mm:field name="natuurgebieden.naam" /></span><br>
					<table width="100%" cellspacing="0" cellpadding="0">
					<tr>
						<td height="1" class="leftnavline"><img src="media/trans.gif" width="185" height="1" vspace="0" border="0" alt=""></td>
					</tr>
				</mm:first>
   				<tr>
   					<td align="left">
   						<a href="natuurgebieden.jsp?a=<%=artikel_number%>&n=<%=natuurgebiedID%>" class="subnavbutton <%if(artikelID.equals(artikel_number)){%> subnavbutton-high<% } %>"><mm:field name="artikel.titel" /></a>
   					</td>
   				</tr>
   				<tr>
   					<td height="1" class="leftnavline"><img src="media/trans.gif" width="185" height="1" vspace="0" border="0" alt=""></td>
   				</tr>
				<mm:last>
					</table><br>
				</mm:last>
			</mm:field>
		</mm:list>
		</mm:listcontainer>
		<% } 
		
		// *** Activities ***
      // *** sChildConstraints should hold for the child, relation type constraints should hold for the parent ***
      HashSet events = (HashSet) application.getAttribute("events");
      if(events!=null&&events.size()>0) { // *** add natuurgebied to search
         String thisEvents = events.toString();
         events = new HashSet();
         %><mm:list nodes="<%= thisEvents.substring(1,thisEvents.length()-1) %>" path="evenement,related,natuurgebieden"
                   constraints="<%= "natuurgebieden.number='" + natuurgebiedID + "'" %>" fields="evenement.number"
            ><mm:field name="evenement.number" write="false" jspvar="parent_number" vartype="String"><%
               events.add(parent_number);
            %></mm:field
         ></mm:list><%
      }
      if(events!=null&&events.size()>0) {
         String thisEvents = events.toString();
         %><mm:list nodes="<%= thisEvents.substring(1,thisEvents.length()-1) %>" path="evenement,related,evenement_type" 
            fields="evenement_type.number" distinct="true" orderby="evenement_type.naam">
				<mm:first>
					<span class="colortitle">Activiteiten</span><br>
					<ul>
				</mm:first>
				<mm:node element="evenement_type">
   				   <li><a href="events.jsp?p=agenda&n=<%=natuurgebiedID%>&activity_type_<mm:field name="number" />=on&<%= (String) application.getAttribute("events_url") %>&search=on">
   				         <mm:field name="naam" /></a></li>
				</mm:node>
				<mm:last>
      			</ul>
      			<br>
      			<table class="dotline"><tr><td height="3"></td></tr></table>
            </mm:last>
		   </mm:list><%
      } %>
		<mm:list nodes="<%=natuurgebiedID%>" path="natuurgebieden,rolerel,artikel"
		          fields="artikel.number" distinct="true" orderby="artikel.titel"
                constraints="<%=(new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour) %>">
				<mm:first>
					<span class="colortitle">Routes</span><br>
					<ul>
				</mm:first>
						<li><a href="routes.jsp?n=<%=natuurgebiedID%>">
		               <mm:field name="artikel.titel" /></a></li>
				<mm:last>
      			</ul>
      			<table class="dotline"><tr><td height="3"></td></tr></table>
            </mm:last>
		</mm:list>	
      <mm:context>
      <% 
      
      // *** teaser (in de buurt) ***
      int maxShorties =50;
      int shortyCnt =0; 
      String[] shortyID = new String[maxShorties];
      %>
      <mm:listcontainer path="natuurgebieden,readmore,teaser">
   		<mm:list nodes="<%=natuurgebiedID%>" fields="teaser.number,teaser.titel"  max="<%=String.valueOf(maxShorties)%>" orderby="readmore.pos">
   			<mm:first>
   			<span class="colortitle">In de buurt</span><br>
   			</mm:first>
   			<mm:field name="teaser.number" write="false" jspvar="teaser_number" vartype="String">
   			<%  shortyID[shortyCnt] = teaser_number;
   				shortyCnt++;
   			%>
   			</mm:field>
     		</mm:list>
      </mm:listcontainer>
      <ul>
      <mm:import id="divstyle" />
      <mm:import id="hrefclass" />
      <% for (int i =0; i<shortyCnt;i++){ %>
      	<%@include file="../../includes/shorty_logic_2.jsp" %>
      	<mm:node number="<%=shortyID[i]%>">
      		<mm:field name="titel" write="false" jspvar="teaser_titel" vartype="String">
      		<mm:field name="omschrijving" write="false" jspvar="teaser_omschrijving" vartype="String">
      			<li class="colortxt"><% linkTXT = teaser_titel; altTXT=""; %>
      				 <%@include file="../../includes/validlink.jsp" %> 
      		</mm:field>
      		</mm:field>
      	</mm:node>
      <% } %>
      <mm:remove referid="hrefclass" />
      <mm:remove referid="divstyle" />
      </ul>
      <% if(shortyCnt != 0){%>
      	<table class="dotline"><tr><td height="3"></td></tr></table>
      <% } %>
      </mm:context>	
		<% // *** readmore paragraphs with icons (in het kort) *** 
      boolean parFound = false;
      %>
      <mm:list nodes="<%=natuurgebiedID%>" path="natuurgebieden,readmore,paragraaf" fields="paragraaf.titel,paragraaf.tekst,readmore.readmore"
         constraints="readmore.readmore='OppLig'">
			<mm:first>
				<span class="colortitle">In het kort</span><br>
				<table width="100%" cellspacing="0" cellpadding="0">
            <% parFound = true; %>
			</mm:first>
				<tr><td style="vertical-align:top;" colspan="2">Oppervlakte en ligging:</td></tr>
            <tr><td style="vertical-align:top;" colspan="2"><mm:field name="paragraaf.tekst" /></td></tr>
		</mm:list>
		<mm:list nodes="<%=natuurgebiedID%>" path="natuurgebieden,readmore,paragraaf" fields="paragraaf.titel,paragraaf.tekst,readmore.readmore"
         orderby="readmore.pos,readmore.readmore" constraints="readmore.readmore!='OppLig'">
			<mm:first>
		      <% if(!parFound) { %>
				   <span class="colortitle">In het kort</span><br>
				   <table width="100%" cellspacing="0" cellpadding="0">
            <% parFound = true;
            } %>
			</mm:first>
				<tr><td style="vertical-align:top;"><img src="media/images/icons/<mm:field name="readmore.readmore" />.gif" alt="" width="14" height="14" border="0" vspace="2"></td>
            <td style="vertical-align:top;padding-left:2px;">
               <mm:field name="paragraaf.tekst" write="false" jspvar="paragraaf_tekst" vartype="String">
                  <%= (paragraaf_tekst!=null ? paragraaf_tekst.substring(0,1).toUpperCase()+paragraaf_tekst.substring(1) : "") %>
               </mm:field>
            </td></tr>
		</mm:list>
      <% if(parFound) { %>
		   </table>
      <% } %>
	</td>
</tr>
</table>
</mm:node>
<%} else{ %>
geen artikel
<% } %>
</mm:cloud>