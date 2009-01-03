<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="attachments.title">
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
			openPopupWindow('attachmentinfo', '500', '500', 'attachmentinfo.jsp?objectnumber='+objectnumber);
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
                  <a href="#"><fmt:message key="attachments.title" /></a>
               </div>
            </div>
         </div>
         <div class="tab">
            <div class="body">
               <div>
                  <a href="attachmentupload.jsp?uploadAction=${action}"><fmt:message key="attachments.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>

     <div class="editor" style="height:500px">
      <div class="body">
         <mm:import id="searchinit"><c:url value='/editors/resources/AttachmentInitAction.do'/></mm:import>
         <html:form action="/editors/resources/AttachmentAction" method="post">
			<html:hidden property="action" value="${action}"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
<mm:import id="contenttypes" jspvar="contenttypes">attachments</mm:import>
<%@include file="attachmentform.jsp" %>
         </html:form>
	  </div>

<div class="ruler_green"><div><fmt:message key="attachments.results" /></div></div>

<div class="body">
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>

         <table>
            <tr class="listheader">
               <th> </th>
               <th nowrap="true"><a href="#" class="headerlink" onclick="orderBy('title');"><fmt:message key="attachmentsearch.titlecolumn" /></a></th>
               <th><fmt:message key="attachmentsearch.filenamecolumn" /></th>
               <th><fmt:message key="attachmentsearch.mimetypecolumn" /></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
	           	<mm:listnodes referid="results">
	                  <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:attachment escape="js-single-quotes"/>');</mm:import>
	                  <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
	                     <td style="white-space:nowrap;">
  						    <c:if test="${action != 'select'}">
	                          <a href="<mm:url page="../WizardInitAction.do">
                                         <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                         <mm:param name="returnurl" value='<%="../editors/resources/AttachmentAction.do" + request.getAttribute("geturl")%>' />
                                      </mm:url>">
                              <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="attachmentsearch.icon.edit" />" title="<fmt:message key="attachmentsearch.icon.edit" />" /></a>
						      <mm:hasrank minvalue="administrator">
	                            <a href="<mm:url page="DeleteSecondaryContentAction.do" >
                                            <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                            <mm:param name="returnurl" value='<%="/editors/resources/AttachmentAction.do" + request.getAttribute("geturl")%>' />
                                         </mm:url>">
	                            <img src="../gfx/icons/delete.png" alt="<fmt:message key="attachmentsearch.icon.delete" />" title="<fmt:message key="attachmentsearch.icon.delete" />"/></a>
	                          </mm:hasrank>
	                       </c:if>
	                       <a href="javascript:showInfo(<mm:field name="number" />)">
	                              <img src="../gfx/icons/info.png" alt="<fmt:message key="attachmentsearch.icon.info" />" title="<fmt:message key="attachmentsearch.icon.info" />" /></a>
                         </td>
	                     <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
	                     <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
	                     <td onMouseDown="objClick(this);"><mm:field name="mimetype"/></td>
	                  </tr>
	               <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
	            </mm:listnodes>
	         </tbody>
         </table>
</c:if>
<c:if test="${resultCount == 0 && param.title != null}">
	<fmt:message key="attachmentsearch.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
	<%@include file="../repository/searchpages.jsp" %>
</c:if>
</div>
</div>	
</mm:cloud>
</body>
</html:html>
</mm:content>