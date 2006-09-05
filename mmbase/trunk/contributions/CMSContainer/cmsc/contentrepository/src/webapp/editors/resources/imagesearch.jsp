<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../../globals.jsp" %>
<fmt:setBundle basename="cmsc-repository" scope="request" />
<%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:import externid="mode" id="mode">search</mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
	  <link href="../css/main.css" type="text/css" rel="stylesheet" />
      <title><fmt:message key="images.title" /></title>

      <script src="../repository/search.js"type="text/javascript" ></script>
      <script src="../repository/content.js"type="text/javascript" ></script>
      <script src="../utils/window.js" type="text/javascript"></script>
      <script src="../utils/rowhover.js" type="text/javascript"></script>
      <script type="text/javascript" src="../utils/transparent_png.js" ></script>
		<script type="text/javascript">
			function selectElement(element, title, src, width, height) {
				
				if(window.top.opener != undefined) {
					window.top.opener.selectElement(element, title, src, width, height);
					window.top.close();
				}
			}
			
			function showInfo(objectnumber) {
				openPopupWindow('imageinfo', '900', '500', 'imageinfo.jsp?objectnumber='+objectnumber);
            }
		</script>
   </head>
   <body onload="alphaImages()">
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either: search of select --%>

      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="images.title" /></a>
               </div>
            </div>
         </div>
         <div class="tab">
            <div class="body">
               <div>
                  <a href="imageupload.jsp"><fmt:message key="images.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      
     <div class="editor" style="height:500px">
      <div class="body">

         <mm:import id="searchinit"><c:url value='/editors/resources/ImageInitAction.do'/></mm:import>
          <html:form action="/editors/resources/ImageAction" method="post">
			<html:hidden property="action" value="${action}"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>

			<mm:import id="contenttypes" jspvar="contenttypes">images</mm:import>
			<%@include file="imageform.jsp" %>

         </html:form>
 

<div class="ruler_green"><div><fmt:message key="images.results" /></div></div>
 
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>
         <table>
            <tr class="listheader">
               <th> </th>
               <th nowrap="true"><a href="#" class="headerlink" onclick="orderBy('title');"><fmt:message key="imagesearch.titlecolumn" /></a></th>
               <th><fmt:message key="imagesearch.filenamecolumn" /></th>
               <th><fmt:message key="imagesearch.mimetypecolumn" /></th>
               <th></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
             	<mm:listnodes referid="results">
                    <mm:import id="url">javascript:selectElement("<mm:field name="number"/>", "<mm:field name="title"/>","<mm:image />","<mm:field name="width"/>","<mm:field name="height"/>");</mm:import>
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                       <td style="white-space:nowrap;">
	                       <mm:compare referid="action" value="search">
                          <a href="<mm:url page="../WizardInitAction.do">
                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                     <mm:param name="returnurl" value="<%="../editors/resources/ImageAction.do" + request.getAttribute("geturl")%>" />
                                                  </mm:url>">
                              <img src="../gfx/icons/page_edit.png" title="<fmt:message key="imagesearch.icon.edit" />" /></a>
						  <mm:hasrank minvalue="administrator">
	                        <a href="<mm:url page="DeleteSecondaryContentAction.do" >
	                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
	                                                     <mm:param name="returnurl" value="<%="/editors/resources/ImageAction.do" + request.getAttribute("geturl")%>" />
	                                                  </mm:url>">
	                              <img src="../gfx/icons/delete.png" title="<fmt:message key="imagesearch.icon.delete" />"/></a>
	                     </mm:hasrank>
	                     </mm:compare>
                          <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" title="<fmt:message key="imagesearch.icon.info" />" /></a>
                       </td>
                       <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="itype"/></td>
                       <td onMouseDown="objClick(this);"><mm:image template="s(100x100)" mode="img" /></td>
                    </tr>
	             <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
              </mm:listnodes>
            </tbody>
         </table>
</c:if>
<c:if test="${resultCount == 0 && param.title != null}">
	<fmt:message key="imagesearch.noresult" />
</c:if>
      </mm:cloud>
</div>
</div>	
   </body>
</html:html>
</mm:content>