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
		<script type="text/javascript">
			function selectElement(element, title, src) {
				
				if(window.top.opener != undefined) {
					window.top.opener.selectElement(element, title, src);
					window.top.close();
				}
			}
		</script>
   </head>
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

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
               <th><a href="#" class="headerlink" onclick="orderBy('number');"><fmt:message key="imagesearch.numbercolumn" /></a></th>
               <th nowrap="true"><a href="#" class="headerlink" onclick="orderBy('title');"><fmt:message key="imagesearch.titlecolumn" /></a></th>
               <th><fmt:message key="imagesearch.filenamecolumn" /></th>
               <th><fmt:message key="imagesearch.mimetypecolumn" /></th>
               <th></th>
            </tr>
            <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
             	<mm:list referid="results">
             	  <mm:node element="${contenttypes}" jspvar="node">
                    <mm:import id="url">javascript:selectElement("<mm:field name="number"/>", "<mm:field name="title"/>","<mm:image />");</mm:import>
                    <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                       <td onMouseDown="objClick(this);">
                        <a href="<mm:url page="../WizardInitAction.do">
                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                     <mm:param name="returnurl" value="<%="../editors/resources/ImageAction.do" + request.getAttribute("geturl")%>" />
                                                  </mm:url>">
                              <img src="../gfx/icons/page_edit.png" /></a>
                       </td>
                       <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
                       <td onMouseDown="objClick(this);"><mm:field name="itype"/></td>
                       <td onMouseDown="objClick(this);"><mm:image template="s(100x100)" mode="img" /></td>
                    </tr>
                 </mm:node>
	             <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
              </mm:list>
            </tbody>
         </table>
</c:if>
      </mm:cloud>
</div>
</div>	
   </body>
</html:html>
</mm:content>