<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<%@page import="java.util.Iterator,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <link href="../../style.css" type="text/css" rel="stylesheet"/>
      <title><fmt:message key="locate.title" /></title>
      <script src="../search.js"type="text/javascript" ></script>
      <script src="../content.js"type="text/javascript" ></script>
      <script src="../../utils/window.js" type="text/javascript"></script>
      <script src="../../utils/rowhover.js" type="text/javascript"></script>
		<script type="text/javascript">
			function selectElement(element, title) {
				window.top.opener.selectElement(element, title);
				window.top.close();
			}
		</script>
   </head>
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
         <mm:import externid="returnurl"/>
         <mm:import externid="linktochannel"/>
         <mm:import externid="parentchannel" jspvar="parentchannel"/>
         <mm:compare referid="linktochannel" value="" inverse="true">
	         <table style="width: 100%; vertical-alignment: top;">
	            <tr>
	               <td>
	                  <h3>
	                       <fmt:message key="locate.channel" >
		                      <fmt:param ><mm:node number="${linktochannel}"> <mm:field name="name"/></mm:node></fmt:param>
	                        </fmt:message>
	                  </h3>
	               </td>
	            </tr>
	         </table>
         </mm:compare>
         <mm:import id="searchinit"><c:url value='/editors/repository/select/LocateInitAction.do'/></mm:import>
         <html:form action="/editors/repository/select/LocateAction" method="post">
            <html:hidden property="parentchannel"/>
            <html:hidden property="linktochannel"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>

            <mm:present referid="returnurl">
               <input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/>
               <a href="<mm:url page="${returnurl}"/>" title="<fmt:message key="locate.back" />"><fmt:message key="locate.back" /></a>
            </mm:present>

<mm:import externid="contenttypes" jspvar="contenttypes"><%= ContentElementUtil.CONTENTELEMENT %></mm:import>
<%@include file="../searchform.jsp" %>

         </html:form>

<mm:import externid="results" jspvar="nodeList" vartype="List" />
<%@include file="../searchpages.jsp" %>

         <!-- Start printing the results -->
         <table border="0" width="100%" class="listcontent">
            <tr class="listheader">
               <th>                                    <a href="#" class="headerlink" onclick="orderBy('number');"><fmt:message key="locate.numbercolumn" /></a></th>
               <th style="width: 1%;">                 <a href="#" class="headerlink" onclick="orderBy('otype');"><fmt:message key="locate.typecolumn" /></a></th>
               <th style="width: 100px;" nowrap="true"><a href="#" class="headerlink" onclick="orderBy('title');"><fmt:message key="locate.titlecolumn" /></a></th>
               <th><fmt:message key="locate.creationchannelcolumn" /></th>
               <th><fmt:message key="locate.authorcolumn" /></th>
               <th style="width: 110px;"><fmt:message key="locate.lastmodifiedcolumn" /></th>
            </tr>
            	<mm:list referid="results">
            		<mm:node element="${contenttypes}" jspvar="node">
						<mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title"/>');</mm:import>
                        <tr onMouseOver="objMouseOver(this);"
                            onMouseOut="objMouseOut(this);"
                            href="<mm:write referid="url"/>"><td onMouseDown="objClick(this);">
                           <td onMouseDown="objClick(this);"><mm:field name="number"/></td>
                           <td onMouseDown="objClick(this);"><mm:nodeinfo type="guitype"/></td>
                           <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                           <td>
                              <% if (RepositoryUtil.hasCreationChannel(node)) { %>
                                 <% Node cc = RepositoryUtil.getCreationChannel(node); %>
                                 <% if (cc != null) { %>
                                       <a href="SelectorContent.do?channel=<%= cc.getNumber() %>" target="`selectchannels">
                                          <%= cc.getStringValue("name") %>
                                       </a>
                                 <% } %>
                              <% } else { %>
                                 -- GEEN --
                              <% } %>
                           </td>
                           <td onMouseDown="objClick(this);">
                           	  <mm:relatednodes role="authorrel" type="user">
                           	  	<mm:field name="username" />
                           	  </mm:relatednodes>
                           </td>
                           <td onMouseDown="objClick(this);">
                              <mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field>
                           </td>
                        </tr>
                    </mm:node>
                 </mm:list>
               </table>
            <% } %>
      </mm:cloud>
   </body>
</html:html>
</mm:content>