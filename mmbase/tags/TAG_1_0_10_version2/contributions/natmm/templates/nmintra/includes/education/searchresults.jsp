<%
int listSize = educations.size();   // ** number of shown educations **
int pageSize = 5;                   // ** number of educations per page **
if(action.equals("print")) { pageSize = 9999; } 
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
if (!sEducations.equals("")) {
  %>
	<mm:list nodes="<%= sEducations %>" path="educations" fields="educations.number,educations.titel"
			orderby="educations.titel" directions="UP"
			offset="<%= "" + thisOffset*pageSize %>" max="<%= "" + pageSize %>">
	   <mm:node element="educations" jspvar="tE">	 
		   <%
       String sUrl = "educations.jsp?p="+paginaID+"&e="+tE.getStringValue("number")+"&k="+keywordId+"&pool="+poolId+"&pr="+providerId+"&c="+competenceId;
	     String sProvider = "Intern"; 
		   String sSubsidie = "";	
       %>
	     <a href="<%= sUrl %>"><mm:field name="titel"/></a><br/>
   	   <mm:related path="providers">
      	  <mm:field name="providers.naam" jspvar="dummy" vartype="String" write="false">
          	 <% sProvider = dummy; %>
	        </mm:field>
   	      <mm:field name="providers.funding" jspvar="providers_funding" vartype="String" write="false">
      	    <%
            if (providers_funding.equals("1")) {
         	       sSubsidie = "/&nbsp;<a href=\"article.jsp?p=subsidie\">Subsidie</a>&nbsp;"; 
         		}
            %>
	         </mm:field>
   	      </mm:related>
      	  <%= sProvider + "&nbsp;" + "<nobr>" + sSubsidie + "</nobr>" %>
         	<% int iScore = 0; %>
	        <mm:related path="feedback">
   	        <mm:field name="feedback.score" jspvar="score" vartype="Integer" write="false">
      	      <% iScore += score.intValue(); %>
         	  </mm:field>
	          <mm:last>
   	          <mm:size jspvar="size" vartype="Integer" write="false">
      	       <% iScore = new Double(iScore/size.intValue()).intValue(); %>
         	    </mm:size>
	            <nobr>/&nbsp;Beoordeling van collega's&nbsp;<a href="<%= sUrl %>#feedback" title="Bekijk feedback"><img src="media/icon_rating_<%= iScore %>.gif" border="0"></nobr>
   	        </mm:last>		   
      	  </mm:related>
	     </mm:node>
   	   <br/><br/>
	</mm:list>
  <% 
} else {
  %>	
	Er zijn geen opleidingen gevonden, die voldoen aan uw selectie criteria. Pas uw selectie criteria aan om wel resultaten te vinden.
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
   String url = "educations.jsp?p=" + paginaID
               + "&termsearch=" + termSearchId
               + "&pool=" + poolId
               + "&pr=" + providerId
               + "&k=" + keywordId
               + "&c=" + competenceId;
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
      			<a href="<%= url %>&offset=<%= thisOffset-1 %>"><<</a>
      		</td><% 
      	} %>
      	<td><%
      	for(int i=0; i < numberOfPages; i++) { 
      		 if(i==thisOffset) {
      			 %>&nbsp;<%= i+1 %>&nbsp;<%
      		 } else { 
      			 %>&nbsp;<a href="<%= url %>&offset=<%=i %>" id="grijs_ul"><%= i+1 %></a>&nbsp;
      			 <%
      		 }
      		 if(i<(listSize/pageSize)) { %> <% }
      	}
      	%>
      	</td><%
      	if(thisOffset+1<numberOfPages) { 
        		%><td>
      			<a href="<%= url %>&offset=<%=thisOffset+1 %>">>></a>
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
