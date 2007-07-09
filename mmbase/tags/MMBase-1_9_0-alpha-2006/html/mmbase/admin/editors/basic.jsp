<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Basic Editors</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
  
  <table summary="basic editors">
    <tr>
      <th class="header" colspan="2">MMBase Basic Editors</th>
    </tr>
    <tr>
      <td class="multidata" colspan="2">
        <p>
          MMBase is based on the single idea of creating a object cloud that represents what you want to share with people.
          We create objects like images, newsitems, or urls and store them as objects in MMBase. We then create relations between them.
          This makes it possible for MMBase to generate basic (generic) editors, since they all have the same basic idea of how an object
          is represented, as well as the relations to other objects.
        </p>
        <p>
          Generic editors are meant primarily for site administrators. For your users you are encouraged
          to set up the task-based (specific) editwizards. In the urls given below we have setup an admin
          account so you can edit/change the objects within the installed cloud.
        </p>
      </td>
    </tr>
    <tr><th class="header" colspan="2">URLs</th></tr>
    <mm:haspage page="/mmbase/edit">
      <tr>
        <td class="linkdata" colspan="2"><a href="<mm:url page="/mmbase/edit/basic/" />">The MMBase Editors ('basic') : <mm:url page="/mmbase/edit/basic/" absolute="true" /></a></td>
      </tr>
    </mm:haspage>
    <mm:haspage page="/mmbase/edit/my_editors">
      <tr>
        <td class="linkdata" colspan="2"><a href="<mm:url page="/mmbase/edit/my_editors/" />">Alternate generic Editors ('my editors') : <mm:url page="/mmbase/edit/my_editors/" absolute="true" /></a></td>
      </tr>
    </mm:haspage>
    <mm:haspage page="/mmbase/edit/mmeditors">
      <tr>
        <td class="linkdata" colspan="2"><a href="<mm:url page="/mmbase/edit/mmeditors/" />">Classic style generic editors ('mmeditors') : <mm:url page="/mmbase/edit/mmeditors/" absolute="true" /></a></td>
      </tr>
    </mm:haspage>
    <mm:haspage page="/mmbase/edit/x">
      <tr>
        <td class="linkdata" colspan="2"><a href="<mm:url page="/mmbase/edit/x/" />">'New technology' editors with JSPX, CSS, Javascript, MMBase taglib 2: <mm:url page="/mmbase/edit/x/" absolute="true" /></a>. Still experimental, and don't not work in all browers.</td>
      </tr>
    </mm:haspage>
    <tr class="footer">
      <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" /></td>
      <td class="data">Return to home page</td>
    </tr>
  </table>
</body>
</html>
</mm:cloud>
