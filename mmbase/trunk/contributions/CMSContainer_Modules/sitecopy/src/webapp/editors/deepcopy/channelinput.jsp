<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="deepcopy.title">
	<style type="text/css">
	p { 
      margin-left:10px;
   }
   .contents{
      margin-left:10px;
   }
  input {
         margin-left:10px;
  }
	</style>
   <script language="javascript">
      function moveContent() {
        openPopupWindow('selectchannel', 340, 400);
      }
      function selectChannel(channel, path) {
        document.forms[0]["targetchannel"].value=channel;
        document.forms[0]["path"].value=path;
   }
   </script>
</cmscedit:head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<mm:import externid="parent" from="parameters" />
<mm:import externid="destination" from="parameters" />

<body>
<cmscedit:sideblock title="deepcopy.title" titleClass="side_block_green">
   <form action="<c:url value='/editors/site/SiteCopy.do'/>" name="site">
      <input type="hidden" name="parent" value="<mm:write referid="parent" />"/>
      <input type="hidden" name="destination" value="<mm:write referid="destination" />"/>
      <div class="contents">
         <br/>
         <input type="checkbox" name="content"  value="1" /><fmt:message key="deepcopy.copyrelatedcontents" /><br/><br/>
        <fmt:message key="deepcopy.destination.channel.title" />  
        <input type="hidden" name="targetchannel" id="targetchannel" size="12"/>
        <input type="text" name="path" id="path" size="30"/>
        <a href="<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />" target="selectchannel" onclick="moveContent()"><fmt:message key="deepcopy.search" /> </a><br/>
         <br/><fmt:message key="deepcopy.note" /><br/> 
         <br/>
         <input type="button" value="<fmt:message key="deepcopy.button.back" />" onclick="history.back(-1)">
         <input type="submit" value="<fmt:message key="deepcopy.button.save" />">
      </div>
   </form>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>