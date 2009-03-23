<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><%@include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="deepcopy.title">
   <script type="text/javascript">
            parent.frames['pages'].location.reload();
   </script>
   <style type="text/css">
   p { 
      margin-left:10px;
   }
   .contents{
      margin-left:10px;
   }
  input {
         margin-left:20px;
  }
	</style>
</cmscedit:head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="deepcopy.title" titleClass="side_block_green">

<form action="?">
      <div class="contents">
         <fmt:message key="deepcopy.pages.size">
            <fmt:param value="${pages}"/>
         </fmt:message>
      </div>
</form>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>