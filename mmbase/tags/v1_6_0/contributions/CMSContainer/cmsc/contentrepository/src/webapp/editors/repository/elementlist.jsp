<%@ page language="java" contentType="text/html;charset=utf-8" 
%><%@ include file="globals.jsp" 
%><%@ page import="com.finalist.cmsc.repository.RepositoryUtil" 
%><%@ page import="com.finalist.cmsc.security.*" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="content.title">
    <script src="content.js" type="text/javascript"></script>
    <script type="text/javascript">
    <c:if test="${not empty param.message}">
    addLoadEvent(alert('${param.message}'));
    </c:if>
    <c:if test="${not empty param.refreshchannel}">
    addLoadEvent(refreshChannels);
    </c:if>
    <c:if test="${requestScope.refresh}">
    addLoadEvent(refreshChannels);
    </c:if>
    addLoadEvent(alphaImages);
</script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
<mm:import externid="direction" jspvar="direction"  from="parameters"/>
<mm:import externid="type" jspvar="elementype"  from="parameters" />
<c:set var="listUrl" value="${elementype == 'asset'?'asset.jsp':'content.jsp'}"/>
<div class="tabs">
    <!-- active TAB -->
    <div class="${(elementype == 'content' || elementype == null)?'tab_active':'tab'}">
        <div class="body">
            <div>
                <a href="Content.do?type=content&parentchannel=${parentchannel}&direction=${direction}" name="activetab"><fmt:message key="content.title"/></a>
            </div>
        </div>
    </div>
    <div class="${elementype == 'asset'?'tab_active':'tab'}">
      <div class="body">
         <div>
            <a href="Asset.do?type=asset&parentchannel=${parentchannel}&direction=${direction}"><fmt:message key="asset.title" /></a>
         </div>
      </div>
   </div>
</div>
<jsp:include page="${listUrl}"/>
</mm:cloud>
</body>
</html:html>
</mm:content>