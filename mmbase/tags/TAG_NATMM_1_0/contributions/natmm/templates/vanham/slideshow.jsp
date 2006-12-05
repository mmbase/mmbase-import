<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%> 
<%@include file="includes/templateheader.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/functions.jsp" %>
<% 

String previousImage = "-1";
String nextImage = "-1";
String thisImage = "";
String otherImages = "";
int totalNumberOfImages = 1;
int thisImageNumber = 1;
%><%@include file="includes/splitimagelist.jsp" 
%><% String pageUrl = "slideshow.jsp?i="; 
%><html>
<head>
<title>VAN HAM<mm:node number="<%= thisImage %>"> - <mm:field name="title" /></mm:node></title>
<link rel="stylesheet" type="text/css" href="css/website.css">
<style>
td { 
   width: 30px;
   height: 30px;
   font-size: 140%;
   text-align: center;
} 
</style>
<meta http-equiv="imagetoolbar" content="no">
</head>
<body class="popup">
<table cellpadding="0" cellspacing="0" border="0" style="width:780px;">
<tr>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
</tr>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td colspan="21" class="def" style="width:690px;padding-top:65px;padding-bottom:16px;">
      <mm:node number="<%= thisImage %>" jspvar="dummy">
         <a href="javascript:void(0);" onClick="window.close()" title="<bean:message bundle="<%= "VANHAM." + language %>" key="slide.click.on.photo" />">
            <img src="<mm:image template="s(350)" />" style="margin-bottom:16px;" border="0"></a><br/>
         <b><%= LocaleUtil.getField(dummy,"titel",language) %></b><br/>
         <%= LocaleUtil.getField(dummy,"omschrijving",language) %><br/>
         <mm:field name="bron">
            <mm:isnotempty><bean:message bundle="<%= "VANHAM." + language %>" key="slide.photography" />: <mm:write /><br/></mm:isnotempty>
         </mm:field>
      </mm:node>
   </td>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td class="vh" style="width:30px;">V</td>
   <td class="vh" style="width:30px;">A</td>
   <td class="vh" style="width:30px;">N</td>
   <td colspan="18" style="width:90px;"></td>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td class="vh" style="width:30px;">H</td>
   <td class="vh" style="width:30px;">A</td>
   <td class="vh" style="width:30px;">M</td>
   <td colspan="18"></td>
</tr>
</table>
</body>
</html>
</mm:cloud>
