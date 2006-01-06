<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import="java.awt.*" %>

<% String thisRequestURL = HttpUtils.getRequestURL(request).toString(); %>
<base href="<%= thisRequestURL.substring(0, thisRequestURL.lastIndexOf("/")) %>/" > 

<mm:import externid="username" from="parameters" />
<mm:cloud method="http" logon="$username" jspvar="cloud">

<% int offsetCounter = 0;
   String iartId = request.getParameter("iart") ;
   if ( ! (iartId==null || iartId.equals(""))) { 
		offsetCounter = Integer.parseInt(iartId);
		if(offsetCounter<0) offsetCounter = 0;
   }
   int windowWidth = 600;
   int numberInRow = (windowWidth/100);
   int numberInTable = 2*numberInRow;
%> 

<html>
<head>
<title>image_gallery</title>
<link rel="stylesheet" type="text/css" href="css/editors.css">
<SCRIPT LANGUAGE="JavaScript">
<%= "<!--" %>
var cancelClick = false;
function doDelete(prompt) {
				var conf;
				if (prompt && prompt!="") {
					conf = confirm(prompt);
				} else conf=true;
				cancelClick=true;
				return conf;
			}
<%= "//-->" %>
</SCRIPT>

</head>
<body topmargin="2" rightmargin="3" leftmargin="3">

<%-- table of images --%>	
<% int imageCounter = 0; %>
<table border="0" cellspacing="0" cellpadding="0">

<tr>
<%-- check if there are previous images --%>
<td colspan="<%= numberInRow/2 %>">
<% if(!(offsetCounter==0)) { %>
	<a class="subHeader" href="image_gallery.jsp?iart=<%= offsetCounter - numberInTable %>">View the previous <%= numberInTable %> images</a>
<% } %>
</td>
<%-- check if there are next images --%>
<td colspan="<%= numberInRow - numberInRow/2 %>"><div align="right">
<mm:list path="images" 	orderby="images.title" directions="UP"
	offset="<%= "" + (offsetCounter + numberInTable) %>" max="1">
	<a class="subHeader" href="image_gallery.jsp?iart=<%= offsetCounter + numberInTable %>">View the next <%= numberInTable %> images</a>
</mm:list>
</div></td>
<tr>
<%-- list images in table rows --%>
<tr>
<mm:list path="images" 	orderby="images.title" directions="UP"
	offset="<%= "" + offsetCounter  %>" max="<%= "" + numberInTable %>">
	<td valign="top" align="center" class="blauw"><div align="center">
		<% String images_number = ""; %>
		<mm:field name="images.number" jspvar="dummy01" vartype="String" write="false">
			<% images_number = dummy01; %>
		</mm:field>
		<% boolean hasRelation = false; %>
		<mm:list nodes="<%= images_number %>" path="images,posrel,paragraphs,posrel,articles">
			<a href="/mmapps/editwizard/jsp/wizard.jsp?wizard=wizards/articles/articles&nodepath=articles&objectnumber=<mm:field name="articles.number" />">
			<img title="<mm:field name="articles.title" />" border="0" src="media/ed_wizard_a.gif"></a>
			<% hasRelation = true; %>
		</mm:list>
		<mm:list nodes="<%= images_number %>" path="images,posrel,websites">
			<a href="/mmapps/editwizard/jsp/wizard.jsp?wizard=wizards/websites/websites&nodepath=websites&objectnumber=<mm:field name="websites.number" />">
			<img title="<mm:field name="websites.name" />" border="0" src="media/ed_wizard_w.gif"></a>
			<% hasRelation = true; %>
		</mm:list>
		<mm:list nodes="<%= images_number %>" path="images,posrel,pages">
			<a href="/mmapps/editwizard/jsp/wizard.jsp?wizard=wizards/pages/pages&nodepath=pages&objectnumber=<mm:field name="pages.number" />">
						<img title="<mm:field name="pages.menuname" /> <mm:field name="pages.title" />" border="0" src="media/ed_wizard_p.gif"></a>
			<% hasRelation = true; %>
		</mm:list>		
		<% if(!hasRelation) { %>
			<a href="image_delete.jsp?iart=<%= iartId %>&image=<%= images_number %>">
			<img onclick="return doDelete('Are you sure you want to delete this image?');" 
				 onmousedown="cancelClick=true;"
				 title="Delete image"	 
				 height="20" width="20" border="0" src="/mmapps/editwizard/media/remove.gif"></a>
		<% } %>
		<br>
		<a href="/mmapps/editwizard/jsp/wizard.jsp?wizard=wizards/images/images&nodepath=images&objectnumber=<%= images_number %>">
		<mm:node number="<%= images_number %>">
			<img src=<mm:image template="s(100)" /> width="100" border="0" alt="Edit <mm:field name="title" />">
		</mm:node>
		</a><br>
	</div></td>
	<% imageCounter++; %>
	<% if((imageCounter%numberInRow==0)&&
         	(imageCounter!=numberInTable)) { %> </tr><tr> <% } %>
</mm:list>
<% while(!(imageCounter%numberInRow==0)) { %>
	 <td>&nbsp;</td> 
	<% imageCounter++; %>
<% } %>
</tr>

</table>
</body>
</html>
</mm:cloud>
