<%
int listSize = lu.count(sEvents,",")+1;   // ** number of shown events **
int pageSize = 5;   // ** number of events per page **		
int thisOffset = 0;
try{
  if(!offsetId.equals("")){
	thisOffset = Integer.parseInt(offsetId);
	offsetId ="";
  }
} catch(Exception e) {} 
int lastPage = (thisOffset+1)*pageSize;
if(lastPage>listSize) { lastPage = listSize; }

%>
<div class="pageheader">Zoekresultaten</div>
<br/>
<span class="black">
<% 
if(!sEvents.equals("")){
  %>
	<mm:list nodes="<%= sEvents %>" path="evenement_blueprint" fields="evenement_blueprint.number,evenement_blueprint.titel"
			orderby="evenement_blueprint.titel" directions="UP"
			offset="<%= "" + thisOffset*pageSize %>" max="<%= "" + pageSize %>">
	   <mm:node element="evenement_blueprint" jspvar="tE">	 
		   <%
      	String sUrl = searchUrl + "&ev=" + tE.getStringValue("number");
	      %>
   	   <a href="<%= sUrl %>"><mm:field name="titel"/></a><br/>
     		<mm:field name="tekst" jspvar="sText" vartype="String" write="false">
	   		<%
            if (sText!=null) {
             sText = HtmlCleaner.cleanText(sText,"<",">","").trim();
             if(!sText.equals("")) { 
                int spacePos = sText.indexOf(" ",200); 
                if(spacePos>-1) { 
                 sText = sText.substring(0,spacePos);
                } 
                %>
                <%= sText %>
                <br/>
                <%
              }
            } 
            %>
	      </mm:field>
      	<% int iScore = 0; %>
	      <mm:related path="feedback">
   	      <mm:field name="feedback.score" jspvar="score" vartype="Integer" write="false">
      	    <% iScore += score.intValue(); %>
         	</mm:field>
	         <mm:last>
   	      <mm:size jspvar="size" vartype="Integer" write="false">
      	      <% iScore = new Double(iScore/size.intValue()).intValue(); %>
         	</mm:size>
	         <nobr>Feedback van collega's&nbsp;<a href="<%= sUrl %>#feedback" title="Bekijk feedback"><img src="media/icon_rating_<%= iScore %>.gif" border="0"></nobr>
   	      </mm:last>		   
      	</mm:related>
	   </mm:node>
   	<br/><br/>
	</mm:list>
  <% 
} else { 
  %>
	Er zijn geen activiteiten gevonden, die voldoen aan uw selectie criteria. Pas uw selectie criteria aan om wel resultaten te vinden.
  <% 
} %>
</span>
<br/>
<br/>
<% 
if(listSize>pageSize) { 
   // *** Some examples
   // 61/15 = 4 but should be 5. So (61-1)/15+1 = 60/15 + 1 = 4 + 1 = 5
   // 60 also holds (60-1)/15+1 = 59/15 + 1 = 3 + 1 = 4
   int numberOfPages = (listSize-1)/pageSize+1;
   %>
   <table width="344" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td>
      <table border="0" cellpadding="0" cellspacing="0">
   	   <tr>
   	   <td width="52">
   	      &nbsp;Pagina
   	   </td>
      	<%
      	if(thisOffset>0) { 
      	%>
      		<td>
      			<a href="event_blueprints.jsp?<%= searchUrl %>&offset=<%= thisOffset-1 %>"><<</a>
      		</td><% 
      	} %>
      	<td><%
      	for(int i=0; i < numberOfPages; i++) { 
      		 if(i==thisOffset) {
      			 %>&nbsp;<%= i+1 %>&nbsp;<%
      		 } else { 
      			 %>&nbsp;<a href="event_blueprints.jsp?<%= searchUrl %>&offset=<%=i %>"><%= i+1 %></a>&nbsp;
      			 <%
      		 }
      		 if(i<(listSize/pageSize)) { %> <% }
      	}
      	%>
      	</td><%
      	if(thisOffset+1<numberOfPages) { 
        		%><td>
      			<a href="event_blueprints.jsp?<%= searchUrl %>&offset=<%=thisOffset+1 %>">>></a>
      		</td><%
      	} %>
      	</tr>
      	
      </table>
      </td>
   </tr>
   </table>
   <% 
} %>
<br>
<br>
