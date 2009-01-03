<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><fmt:setBundle basename="cmsc-reactions" scope="request" /><%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="reactions.title">
      <script src="../repository/search.js" type="text/javascript"></script>
      <script src="../repository/content.js" type="text/javascript"></script>
		<script type="text/javascript">
			function selectElement(element, title, src) {
				if(window.top.opener != undefined) {
					window.top.opener.selectElement(element, title, src);
					window.top.close();
				}
			}
			
			function showInfo(objectnumber) {
				openPopupWindow('reactioninfo', '500', '500', 'reactioninfo.jsp?objectnumber='+objectnumber);
            }
		</script>
</cmscedit:head>
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

<mm:import externid="action">search</mm:import><%-- either: search of select --%>

      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="reactions.title" /></a>
               </div>
            </div>
         </div>
      </div>

     <div class="editor" style="height:500px">
      <div class="body">

         <mm:import id="searchinit"><c:url value='/editors/resources/ReactionInitAction.do'/></mm:import>
         <html:form action="/editors/resources/ReactionAction" method="post">
			<html:hidden property="action" value="${action}"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>

<mm:import id="contenttypes" jspvar="contenttypes">reaction</mm:import>
<%@include file="reactionform.jsp" %>

         </html:form>
	</div>

<div class="ruler_green"><div><fmt:message key="reactions.results" /></div></div>

<div class="body">
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>

         <table>
            <tr class="listheader">
               <th> </th>
               <th><a href="#" class="headerlink" onClick="orderBy('name');"><fmt:message key="reactionsearch.namecolumn" /></a></th>
               <th><a href="#" class="headerlink" onClick="orderBy('title');"><fmt:message key="reactionsearch.titlecolumn" /></a></th>
               <th><fmt:message key="reactionsearch.articlecolumn" /></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
	           	<mm:listnodes referid="results">
	                  <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>');</mm:import>
	                  <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
	                     <td style="white-space:nowrap;">
  						      
                         <a href="#" onClick="showInfo(<mm:field name="number" />);" >
                           <img src="../gfx/icons/info.png" alt="<fmt:message key="reactionsearch.icon.info" />" title="<fmt:message key="reactionsearch.icon.info" />" />
                         </a>                        
                        
                        <c:if test="${action != 'select'}">
						      <mm:hasrank minvalue="administrator">
	                            <a href="<mm:url page="DeleteReactionAction.do" >
                                            <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                            <mm:param name="returnurl" value='<%="/editors/resources/ReactionAction.do" + request.getAttribute("geturl")%>' />
                                         </mm:url>">
	                            <img src="../gfx/icons/delete.png" title="<fmt:message key="reactionsearch.icon.delete" />"/></a>
	                          </mm:hasrank>
	                       </c:if>
                                                  
                         </td>
                         
                         <td onMouseDown="objClick(this);"><mm:field name="name"/></td>
	                 <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
	                 <mm:field name="number" write="false" jspvar="myId"/>
                         <td onMouseDown="objClick(this);">${titles[myId]}</td>
	                  </tr>
	               <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
	            </mm:listnodes>
	         </tbody>
         </table>
</c:if>
<c:if test="${resultCount == 0 && param.title != null}">
	<fmt:message key="reactionsearch.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
	<%@include file="../repository/searchpages.jsp" %>
</c:if>	
</mm:cloud>
   </body>
</html:html>
</mm:content>