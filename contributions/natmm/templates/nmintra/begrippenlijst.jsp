<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>

<%@include file="includes/calendar.jsp" %>

<%@include file="includes/vastgoed/override_templateparams.jsp" %>

<%@include file="includes/header.jsp" %>

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

<% boolean bibliotheekStyle = !printPage && NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek"); %>
<td <% if(bibliotheekStyle) { %>colspan="2"<% } %>>

<table border="0" cellpadding="0" cellspacing="0">
   <tr>
      <%-- <td><img src="media/rdcorner.gif" style="filter:alpha(opacity=75)"></td> --%>
      <td class="transperant" style="width:100%;"><img src="media/spacer.gif" width="1" height="6"><br></td>
      <td class="transperant"><img src="media/spacer.gif" width="10" height="28"></td>
   </tr>
</table>

</td>

<% 
   String rightBarTitle = "";
%>
   
<td><%@include file="includes/rightbartitle.jsp" %></td>
</tr>
<tr>
<td class="transperant" <% if(NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek")) { %>colspan="2"<% } %>>
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">

<% if(vraagNode != null) { %>
	 
	<%-- directly forwarding to not the vraagbaak, but a/the page that includes the vraagbaak --%>
    <% String vraagpageLink = ""; %>
  
    <mm:node number="<%=vraagNode%>" >
		<mm:relatednodes type="pagina" max="1" >
  			<mm:field name="number" jspvar="page_node" vartype="String" write="false" >
  			<% vraagpageLink = "vraagbaak.jsp?p=" + page_node + "#" + vraagNode; %>
			</mm:field>
		</mm:relatednodes>
    </mm:node>
    
    <mm:redirect page="<%=vraagpageLink%>" />
      
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
    		<mm:relatednodes path="related,vraagbaak" searchdir="source" orderby="titel">
	    		&nbsp;&nbsp;<a href='?v=<mm:field name="number"/>' class="underlined"><mm:field name="titel"/></a><br/>
	    	</mm:relatednodes>
	    	<br/>
    	</div>
    	
    </mm:listnodes>
    
    <% } %>
     
    
    </td>
</tr>
</table>
</div>
</td>



<% 

if(!printPage) { 
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

</mm:cloud>
