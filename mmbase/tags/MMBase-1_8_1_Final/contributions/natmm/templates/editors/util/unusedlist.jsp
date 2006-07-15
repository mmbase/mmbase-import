<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
<link rel="stylesheet" type="text/css" href="css/editors.css">
</head>
<body>
<% 
String [] paths = {  "articles", "paragraphs","attachments","images" };
%>
<h3>Overview of unused items.</h3>
Click on an item to edit. Select items and click the button on the bottom of this frame to delete.
<form name="unusedform" method="post" target="">
<% for(int i=0; i< paths.length; i++) { 
    %><h3><%= paths[i].toUpperCase() %></h3>
    <mm:import externid="<%= paths[i] %>" jspvar="prevUnused" vartype="String"/><%

    if(prevUnused==null) { // initialisation of form
        prevUnused = ""; 
    } else if(prevUnused.equals("")) { // no unused items found in last iteration
        prevUnused = "-1"; 
    } else { // remove leading comma in list of unused items
        prevUnused = prevUnused.substring(1);
    }
    String nextUnused = "";
    
    if(!prevUnused.equals("-1")) {
        %><mm:list nodes="<%= prevUnused %>" path="<%= paths[i] %>"
        ><mm:node element="<%= paths[i] %>"
        ><mm:remove referid="n"
        /><mm:countrelations id="n" write="false"
        /><mm:compare referid="n" value="0"
            ><mm:field name="number" jspvar="number" vartype="String" write="false"
            ><mm:import externid="<%= "p" + number %>"
            /><mm:compare referid="<%= "p" + number %>" value="delete"
                ><mm:deletenode number="<%= number %>"
            /></mm:compare
            ><mm:compare referid="<%= "p" + number %>" value="delete" inverse="true"
                ><input type="checkbox" name="<%= "p" + number %>" value="delete">
                <a target="unusededit" href="/mmbase/edit/wizard/jsp/wizard.jsp?referrer=/editors/unusedempty.jsp&wizard=wizards/<%= paths[i] %>/<%= paths[i] %>&nodepath=<%= paths[i] %>&objectnumber=<%= number %>"><img src="media/ed_wizard.gif" alt="" border="0">&nbsp;<mm:field name="title" /></a><br><%
                nextUnused += "," + number;
            %></mm:compare
           ></mm:field
        ></mm:compare
        ></mm:node
        ></mm:list><% 
    } 
    if(prevUnused.equals("-1")||nextUnused.equals("")) {
        %>No unused <%= paths[i] %> found.<br><%
    }
    %><input type="hidden" name="<%= paths[i] %>" value="<%= nextUnused %>" /><%
}
%><br><br>
<input type="submit" name="delete" value="delete selected items" />
</form>

</body>
</html>
</mm:cloud>
