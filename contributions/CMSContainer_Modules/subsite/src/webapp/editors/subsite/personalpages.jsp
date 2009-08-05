<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@page import="org.mmbase.bridge.Cloud" 
%><%@page import="org.mmbase.bridge.Node" 
%><%@page import="org.mmbase.bridge.NodeList" 
%><%@page import="org.mmbase.bridge.util.SearchUtil" 
%><%@page import="org.mmbase.remotepublishing.*" 
%><%@page import="com.finalist.cmsc.subsite.util.SubSiteUtil" 
%><%@page import="com.finalist.cmsc.services.publish.Publish"
%><%@page import="org.mmbase.bridge.BridgeException"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="site.personal.personalpages">
	<script src="<cmsc:staticurl page='/editors/repository/content.js'/>" type="text/javascript"></script>
	<script src="<cmsc:staticurl page='/editors/repository/search.js'/>" type="text/javascript"></script>
</cmscedit:head>
<body>
<script type="text/javascript">
    <c:if test="${not empty param.message}">
    addLoadEvent(alert('${param.message}'));
    </c:if>
    <c:if test="${not empty param.refreshchannel}">
    addLoadEvent(refreshChannels);
    </c:if>
    addLoadEvent(alphaImages);
</script>

<mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="returnurl"/>

<mm:import externid="subsite" from="parameters" />


<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

<div class="content">
   <div class="tabs">
      <div class="tab_active">
         <div class="body">
            <div>
               <a href="#" onClick="selectTab('basic');"><fmt:message key="site.personal.personalpages" /></a>
            </div>
         </div>
      </div>
   </div>
</div>

<div class="editor">
<div class="body">

<mm:listnodes type="subsite" orderby="title">
<mm:first>
   <c:set var="subsiteExists" value="true"/>
   <p><%@include file="personalpages_newbuttons.jsp" %></p>
</mm:first>
</mm:listnodes>

<html:form action="/editors/subsite/SubSiteAction" method="post">
	<html:hidden property="action" value="${action}"/>
	<html:hidden property="search" value="true"/>
	<html:hidden property="offset"/>
	<html:hidden property="order"/>
	<html:hidden property="direction"/>
	<mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
	
	<table>
     <c:if test="${subsiteExists eq true}">
        <tr>
           <td style="width:105px"><fmt:message key="subsite.name" />:</td>
           <td>
              <cmsc:select var="subsite" onchange="document.forms[0].submit();">
              <mm:listnodes type="subsite" orderby="title">
                 <mm:field name="number" id="subsitenumber" write="false" vartype="String" />
                 <cmsc:option value="${subsitenumber}" name="${_node.title}" />
              </mm:listnodes>
              </cmsc:select>
            </td>
        </tr>
     </c:if>
     <c:if test="${subsiteExists ne true}">
       <tr>
          <td colspan="2"><b><fmt:message key="subsite.notfound" /></b></td>
       </tr>
	  </c:if>
    <tr>
       <td style="width:105px"><fmt:message key="subsitedelete.subtitle" /></td>
       <td colspan="3"><html:text property="title" style="width:200px"/></td>
       
       <td style="width:20px">
       </td>
   </tr>
   <tr>
      <td></td>
   <td>
     <input type="submit" class="button" name="submitButton" onClick="setOffset(0);" value="<fmt:message key="site.personal.search" />"/>
     </td>
   </tr>
	</table>
</html:form>
</div>
</div>

<div class="editor">
<br />

<div class="ruler_green"><div><fmt:message key="site.personal.personalpages"/></div></div>
<div class="body">


<c:set var="listSize" value="${resultCount}"/>
<c:set var="resultsPerPage" value="${SearchForm.keywords}"/>
<c:set var="offset" value="${SearchForm.offset}"/>
<c:set var="extraparams" value="&subsite=${subsite}&title=${SearchForm.title}&order=${SearchForm.order}"/>

<mm:isempty referid="results" inverse="true">
   <%@ include file="../pages.jsp" %>
</mm:isempty>

<table>
<thead>
    <tr>
        <th></th>
        <th><a href="#" class="headerlink" onClick="orderBy('title');" ><fmt:message key="pp.title" /></a></th>
        <th><a href="#" class="headerlink" onclick="orderBy('creationdate');" ><fmt:message key="pp.creationdate" /></a></th>
        <th><a href="#" class="headerlink" onclick="orderBy('publishdate');" ><fmt:message key="pp.publishdate" /></a></th>
    </tr>
</thead>
<tbody class="hover">

<mm:list referid="results" jspvar="node" max="${resultsPerPage}">
   <mm:field name="personalpage.number" id="number">
	   <mm:node number="${number}" jspvar="ppNode">
		   <tr <mm:even inverse="true">class="swap"</mm:even>>
		   <td style="white-space: nowrap;">
		   
		   <mm:field name="number"  write="false" id="nodenumber">
         <a href="../subsite/SubSiteEdit.do?number=${nodenumber}"
		       title="<fmt:message key="pp.content.edit" />"><img src="../gfx/icons/edit.png" width="16" height="16"
		                                                       title="<fmt:message key="pp.content.edit" />"
		                                                       alt="<fmt:message key="pp.content.edit" />"/></a>
		  
<%
	   int remoteNumber = Publish.getRemoteNumber(ppNode);
      String appPath = "/content/" + remoteNumber;
      if (remoteNumber == -1) { 
    	  appPath = ""; 
    	}
      request.setAttribute("appPath", appPath);
%>
         <c:if test="${not empty appPath}">
            <a href="<%=Publish.getRemoteUrl(appPath)%>" 
               title="<fmt:message key="pp.content.preview" />" target="_blank"><img src="../gfx/icons/preview.png" width="16" height="16"
                                                             title="<fmt:message key="pp.content.preview" />"
                                                             alt="<fmt:message key="pp.content.preview" />"/></a>
         </c:if>
          <a href="../subsite/SubSiteDelete.do?number=${nodenumber}"
		       title="<fmt:message key="pp.content.delete" />"><img src="../gfx/icons/delete.png" width="16" height="16"
		                                                       title="<fmt:message key="pp.content.delete" />"
		                                                       alt="<fmt:message key="pp.content.delete" />"/></a>
		   <% request.removeAttribute("appPath"); %>              
		   </mm:field>
		   </td>
		   <td>
		      <b><mm:field name="title" /></b>
		   </td>
		   <%--
		   <td>
		   <a href="../subsite/PersonalPageElements.do?personalpage=<mm:field name="number" />">Edit Articles</a>
		   </td>
         --%>
		   
		   <td>
            <mm:field name="creationdate"><cmsc:dateformat displaytime="true"/></mm:field>
         </td>
         <td>
		      <mm:field name="publishdate"><cmsc:dateformat displaytime="true"/></mm:field>
		   </td>
		   
		   </tr>
	   </mm:node>
   </mm:field>
</mm:list>
<%-- Now print if no results --%>
<mm:isempty referid="results">
   <tr><td><b><fmt:message key="site.personal.nonefound" /></b></td></tr>
</mm:isempty>
</tbody>
</table>

<mm:isempty referid="results" inverse="true">
<%@ include file="../pages.jsp" %>
</mm:isempty>
   
<br />

</div>
</div>

</mm:cloud>

</body>
</html:html>
</mm:content>