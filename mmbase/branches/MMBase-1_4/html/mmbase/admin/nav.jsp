<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">

<%!
    String menu ="";
    String submenu="";

    String mainmenu(String value, String category, String subcategory) {

    String result="&nbsp;&nbsp;";
    result += "<a href=\"default.jsp?menu="+value+"&submenu=1&category="+category+"&subcategory="+subcategory+"\" target=\"_top\">";
    if (value.equals(menu)) {
        result+="<span class=\"currentmenuitem\">";
    } else {
        result+="<span class=\"menuitem\">";
    }
    result+="<strong>"+category.toUpperCase()+"</strong></span></a>";
    return result;
}

    String submenu(String value, String subvalue, String category, String subcategory) {

    String result="&nbsp;&nbsp;";
    result += "<a href=\"default.jsp?menu="+value+"&submenu="+subvalue+"&category="+category+"&subcategory="+subcategory+"\" target=\"_top\">";
    if (value.equals(menu) && subvalue.equals(submenu)) {
        result+="<span class=\"currentmenuitem\">";
    } else {
        result+="<span class=\"menuitem\">";
    }
    result+="<strong>"+subcategory.toUpperCase()+"</strong></span></a>";
    return result;
}
%>

<html>
<head>
<%
    menu=request.getParameter("menu");
    submenu=request.getParameter("submenu");
%>
<link rel="stylesheet" href="css/mmbase.css" type="text/css">
<title>Navigation Bar</title>
</head>
<body class="navigationbar">
<table summary="navigation">
<tr>
<td width="50">
<img src="images/logo.gif" border="0" alt="MMBase">
</td>
<td width="850" border="0">
    <%=mainmenu("1","about","license")%>
    <%=mainmenu("2","editors","basic")%>
    <%=mainmenu("3","admin","servers")%>
    <%=mainmenu("4","tools","cache")%>
	<hr />
	
	<% if("1".equals(menu)) { %>
        <%=submenu("1","1","about", "license")%>
	<% } else if("2".equals(menu)) { %>
        <%=submenu("2","1","editors", "basic")%>
	<% } else if("3".equals(menu)) { %>
        <%=submenu("3","1","admin", "servers")%>
        <%=submenu("3","2","admin", "builders")%>
        <%=submenu("3","3","admin", "applications")%>
        <%=submenu("3","4","admin", "modules")%>
        <%=submenu("3","5","admin", "databases")%>
        <%=submenu("3","6","admin", "documentation")%>
	<% } else if("4".equals(menu)) { %>
        <%=submenu("4","1","tools", "cache")%>
        <%=submenu("4","2","tools", "email")%>
	<% } %>
</td>
</tr>
</table>
</body>
</html>

