<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="egemmail.title" /></title>
  <link href="../css/main.css" type="text/css" rel="stylesheet" />
  <script src="../repository/search.js"type="text/javascript" ></script>
</head>

<body onload="refreshChannels()">
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="egemmail.title" /></a>
                </div>
            </div>
        </div>
    </div>
<mm:cloud>
	<div class="editor">
	<div class="ruler_green"><div><fmt:message key="egemmail.export.done" /></div></div>
		<div class="body">		            
			<p>
				<fmt:message key="egemmail.export.intro" />
				<br/>
				<br/>
				<c:if test="${good > 0}">
					<fmt:message key="egemmail.export.good" />: ${good}<br/>
				</c:if>
				<c:if test="${wrong > 0}">
					<fmt:message key="egemmail.export.wrong" />:${wrong}
				</c:if>
			</p>
			
		</div>
		<div class="side_block_end"></div>
	</div>	
</mm:cloud>
</body>
</html:html>
</mm:content>