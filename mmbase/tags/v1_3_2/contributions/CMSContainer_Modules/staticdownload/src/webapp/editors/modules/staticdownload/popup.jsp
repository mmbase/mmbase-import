<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="download.${param.field}.title" /></title>
  <link href="../../css/main.css" type="text/css" rel="stylesheet" />
</head>

    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="download.${param.field}.title" /></a>
                </div>
            </div>
        </div>
    </div>
<body>
	<div class="editor" style="width: auto;">
		<div class="body">	
	<mm:cloud>
		<mm:node number="${param.number}">
<pre>
<mm:field name="${param.field}"/>
</pre>
		</mm:node>
	</mm:cloud>
</div>
</div>
</body>
</html:html>
</mm:content>


