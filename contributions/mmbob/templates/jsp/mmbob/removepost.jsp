<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="postingid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<div class="header">
    <%@ include file="header.jsp" %>
</div>
                                                                                              
<div class="bodypart">

<mm:node referid="postingid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" width="95%">
  <tr><th width="25%" align="left">Poster</th><th align="left">Onderwerp : <mm:field name="subject" /></th></tr>
  <mm:import id="tdvar">listpaging</mm:import>
  <tr>
    <td class="<mm:write referid="tdvar" />" align="left">
      <mm:field name="createtime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
    </td>
    <td class="<mm:write referid="tdvar" />" align="right">
    </td>
  </tr>
 
  <td class="<mm:write referid="tdvar" />" valign="top" align="left">
    <p>
      <b><mm:field name="poster" /></b>
    </p>
  </td>

  <td class="<mm:write referid="tdvar" />" valign="top" align="left">
    <mm:field name="edittime"><mm:compare value="-1" inverse="true">** Laatste keer aangepast op : <mm:field name="edittime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field></mm:compare><p /></mm:field>
                                                                                                
               <mm:formatter xslt="xslt/posting2xhtmlLight.xslt">
                <mm:function referids="imagecontext,themeid" name="escapesmilies">
                <mm:write/>
                </mm:function>
              </mm:formatter>
                                                                                                
    <br /><br /><br /><br /><br />
  </td>
 </tr>
</table>
</mm:node>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" width="95%">
  <tr><th colspan="3" align="center" >Bericht echt verwijderen ??</th></tr>
  <tr><td>
  <form action="<mm:url page="postarea.jsp">
					<mm:param name="forumid" value="$forumid" />
					<mm:param name="postareaid" value="$postareaid" />
					<mm:param name="postthreadid" value="$postthreadid" />
					<mm:param name="postingid" value="$postingid" />
				</mm:url>" method="post">
	<input type="hidden" name="moderatorcheck" value="true">
	<input type="hidden" name="action" value="removepost">
	<p />
	<center>
	<input type="submit" value="Ja, Verwijderen">
  	</form>
	</td>
	<td>
  	<form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="Oops, Nee">
  	</form>
	</td>
	</tr>

</table>
</div>                                                                                               

<div class="footer">
  <%@ include file="footer.jsp" %>
</div>

</body>
</html>
</mm:content>
</mm:cloud>

