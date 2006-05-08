<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
<link rel="stylesheet" type="text/css" href="../css/editorstyle.css">
</head>
<body style="overflow:auto;padding:5px;">
Images that are set to original size:<br/><br/>
<table cellpadding="0" cellspacing="0" style="width:auto;">
<tr>
   <td>titel</td>
   <td>filename</td>
   <td>schermgrootte</td>
</tr>
<mm:list path="paragraaf,posrel,images" constraints="posrel.pos='0' AND screensize LIKE '%410%'">
   <tr>
      <td><mm:field name="images.title" /></td>
      <td><mm:field name="images.titel_fra" /></td>
      <td><mm:field name="images.screensize"/></td>
   </tr>
   <mm:node element="posrel">
      <mm:setfield name="pos">4</mm:setfield>
   </mm:node>
</mm:list>
</table>
</body>
</html>
</mm:cloud>
