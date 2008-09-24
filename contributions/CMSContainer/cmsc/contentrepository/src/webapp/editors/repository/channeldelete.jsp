<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="channeldelete.title">
   <style type="text/css">
      input { width: 100px;}
   </style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters" />
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<body>
   <cmscedit:sideblock title="channeldelete.title" titleClass="side_block_green">
      <p>
         <fmt:message key="channeldelete.subtitle" /> 
         <mm:node referid="number">
            <b><mm:field name="name" /></b>
         </mm:node>
      </p>
      <p>
         <fmt:message key="channeldelete.confirm" />
         <br/>
      </p>
         <form action="?">
            <input type="hidden" name="number" value="<mm:write referid="number"/>" /> 
            <input type="submit" name="remove" value="<fmt:message key="channeldelete.yes" />" />
           <input type="button" onClick="document.location.href='Content.do?parentchannel=<mm:write referid="number"/>&amp;direction=down'" value="<fmt:message key="channeldelete.no" />"/>
         </form>
   </cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>