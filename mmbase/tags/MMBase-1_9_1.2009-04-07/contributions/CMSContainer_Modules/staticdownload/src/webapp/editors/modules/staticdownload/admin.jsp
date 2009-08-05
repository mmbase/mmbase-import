<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="staticdownload.admin.title" /></title>
  <link href="../../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="staticdownload.admin.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
      	<c:forEach var="p" items="${param}">
      		<c:if test="${fn:indexOf(p.key,'new_') == 0}">
      			<c:set var="newValue" value="${p.value}"/>
      			<c:set var="key" value="${fn:substring(p.key,4,fn:length(p.key))}"/>
	      			<c:set var="oldValueKey" value="old_${key}"/>
	      			<c:set var="oldValue" value="${param[oldValueKey]}"/>
	      			<c:if test="${newValue != oldValue}">
	      				<cmsc:setproperty key="${key}" value="${newValue}"/>
	      			</c:if>
      		</c:if>
      	</c:forEach>		
			<form method="post">
				<cmsc:moduleproperties module="staticdownload" var="properties"/>
				<c:forEach var="property" items="${properties}">
					<label style="width:200px"><fmt:message key="${property.key}" /></label>
					
					<input type="hidden" name="old_${property.key}" value="${property.value}"/>
					<input style="width:300px" type="text" name="new_${property.key}" value="${property.value}"/>
					
					<br/>
				</c:forEach>
				
				<c:set var="submitText"><fmt:message key="staticdownload.save" /></c:set>
				<input type="submit" value="${submitText}"/>
			</form>
		</div>
		<div class="side_block_end"></div>
	</div>	

</body>
</html:html>
</mm:content>