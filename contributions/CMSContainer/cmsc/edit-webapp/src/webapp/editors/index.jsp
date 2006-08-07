<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@include file="globals.jsp" %>
<mm:import externid="bottomurl" from="parameters">dashboard.jsp</mm:import>
<mm:locale language="client">
<mm:cloud loginpage="login.jsp" rank="basic user">
<html:html xhtml="true">
	<head><title><fmt:message key="editors.title" /></title>
	<script type="text/javascript" src="editors.js"></script>
   </head>
   <mm:url page="topmenu.jsp" id="toppane" write="false">
      <mm:param name="bottomurl"><mm:write referid="bottomurl"/></mm:param>
   </mm:url>
   <mm:url page="${bottomurl}" id="bottompane" write="false"/>
<frameset rows="75,*,46" framespacing="0" border="0">
		<frame src="<mm:url referid="toppane"/>" name="toppane" frameborder="0" scrolling="no" noresize="noresize" style="border: 0px" />
		<frame src="<mm:url referid="bottompane"/>" name="bottompane" frameborder="0" scrolling="auto" onload="window.toppane.initMenu();"/>
		<frame src="footer.html" name="footerpane" frameborder="0" scrolling="no"/>
	</frameset>

</html:html>
</mm:cloud>
</mm:locale>