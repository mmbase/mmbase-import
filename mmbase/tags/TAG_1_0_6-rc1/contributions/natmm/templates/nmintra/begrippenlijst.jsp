<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%
String indexLetter = "A";
String passedLetter = (String) request.getParameter("k");
String vraagNode = (String) request.getParameter("v");

if ((passedLetter != null) && (passedLetter.matches("[A-Z]"))) {
indexLetter = passedLetter;
}

String indexConstraint = "left(word,1) ='" + indexLetter + "' order by word";
%>
<script>
function ShowHideLayer(divID) {
	var box = document.getElementById(divID);	
		
	if(box.style.display == "none" || box.style.display=="") {
		box.style.display = "block"; 		
	}
	else {
		box.style.display = "none";		
	}
}
</script>
<% boolean twoColumns = !printPage && ! NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek"); %>
<td <% if(!twoColumns) { %>colspan="2"<% } %>><%@include file="includes/pagetitle.jsp" %></td>
<% 
if(twoColumns) { 
   String rightBarTitle = "";
   %><td><%@include file="includes/rightbartitle.jsp" %></td><%
} %>
</tr>
<tr>
<td class="transperant" <% if(NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek")) { %>colspan="2"<% } %>>
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">

<% if(vraagNode != null) { %>
	 
	 <jsp:include page="includes/relatedvraagbaak.jsp">
         	<jsp:param name="v" value="<%=vraagNode%>"/>
     </jsp:include>
         
<% } else {%>

    <%@include file="includes/back_print.jsp" %>
    
    <div align="right">
       <mm:node number="<%= rbLogoID %>" notfound="skipbody"><img src="<mm:image template='s(120x80)'/>" border="0" alt=""></mm:node>
    </div>

<h3><%=indexLetter%></h3>

    <mm:listnodes type="vraagbaak_keywords" constraints="<%=indexConstraint%>">
    	<a href="javascript:ShowHideLayer(<mm:field name="number"/>)"><mm:field name="word"/></a>
    	<br/>
    	
    	<div id ="<mm:field name="number"/>" style="display:none;">
    		<mm:relatednodes path="related,vraagbaak" searchdir="source">
	    		&nbsp;&nbsp;<a href='?v=<mm:field name="number"/>' class="underlined"><mm:field name="titel"/></a><br/>
	    	</mm:relatednodes>
	    	<br/>
    	</div>
    	
    
    
    </mm:listnodes>
    
    <% } %>
    
    
    
    
    
    
    
    
    
    <%-- These are article related view items - MIGHT BE REMOVED 
    <% 
      if(!"false".equals(request.getParameter("showteaser"))) { 
         %>
         <%@include file="includes/relatedteaser.jsp" %>
         <%
      }
      String startnodeId = articleId;
      String articlePath = "artikel";
      String articleOrderby = "";
      if(articleId.equals("-1")) { 
      startnodeId = paginaID;
      articlePath = "pagina,contentrel,artikel";
      articleOrderby = "contentrel.pos";
      }
      %><mm:list nodes="<%= startnodeId %>"  path="<%= articlePath %>" orderby="<%= articleOrderby %>"
         ><%@include file="includes/relatedarticle.jsp" 
      %></mm:list>
      <mm:node number="<%= paginaID %>">
         <%@include file="includes/relatedcompetencies.jsp" %>
      </mm:node>
      <%@include file="includes/pageowner.jsp" 
    %>
     THE ABOVE BLOCK MIGHT BE SELECTIVELY REMOVED --%>
    
    
    
    
    </td>
</tr>
</table>
</div>
</td>



<% 
if(twoColumns) { 
   // *********************************** right bar *******************************
   String styleClass = "white";
   String styleClassDark = "white";
         
   %><td style="padding-left:10px;">
   <div class="rightcolumn" id="rightcolumn">
   
   <p>
   <%@include file="includes/contentblock_letterindex.jsp" %>
   </p><br/>
   
   <mm:list nodes="<%= paginaID %>" path="pagina,readmore,contentblocks" orderby="readmore.pos">
      <mm:node element="contentblocks">
         <%@include file="includes/contentblockdetails.jsp" %>
      </mm:node>
      <br/>
   </mm:list>
   </div>
   </td><%
} %>

<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
