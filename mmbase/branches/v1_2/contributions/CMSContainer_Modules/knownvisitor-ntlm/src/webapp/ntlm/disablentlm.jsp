<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="org.mmbase.bridge.*,
				org.mmbase.util.logging.*" %><%@
page import="java.util.*"%><%@
taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%><%@ 
taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %><%@ 
taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %><%@ 
taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %><%@
taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@
taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %><%@
taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %><%@
taglib uri="http://finalist.com/csmc" prefix="cmsc" %><mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:cloud jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
	<mm:listnodes type="properties" constraints="key='knownvisitor-ntlm.enabled'">
		<mm:setfield name="value">false</mm:setfield>
	</mm:listnodes>
	<mm:listnodes type="properties" constraints="key='knownvisitor-ntlm.enabled'">
		<mm:field name="key"/> <mm:field name="value"/>
	</mm:listnodes>
</html:html>
</mm:cloud>
</mm:content>