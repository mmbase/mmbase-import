<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="java.util.Iterator,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <link href="../style.css" type="text/css" rel="stylesheet"/>
      <title><fmt:message key="search.title" /></title>
      <script src="search.js"type="text/javascript" ></script>
      <script src="content.js"type="text/javascript" ></script>
      <script src="../utils/window.js" type="text/javascript"></script>
      <script src="../utils/rowhover.js" type="text/javascript"></script>
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
         <mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
         <html:form action="/editors/repository/SearchAction" method="post">
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
<%@include file="searchform.jsp" %>

         </html:form>

<mm:import externid="results" jspvar="nodeList" vartype="List" />
<%@include file="searchpages.jsp" %>

         <!-- Start printing the results -->
         <table border="0" width="100%" class="listcontent">
            <tr class="listheader">
               <mm:compare referid="linktochannel" value="" inverse="true">
                  <th>&nbsp;</th>
               </mm:compare>
               <th>                                    <a href="#" class="headerlink" onclick="orderBy('number');"><fmt:message key="locate.numbercolumn" /></a></th>
               <th style="width: 1%;">                 <a href="#" class="headerlink" onclick="orderBy('otype');"><fmt:message key="locate.typecolumn" /></a></th>
               <th style="width: 100px;" nowrap="true"><a href="#" class="headerlink" onclick="orderBy('title');"><fmt:message key="locate.titlecolumn" /></a></th>
               <th><fmt:message key="locate.creationchannelcolumn" /></th>
               <th><fmt:message key="locate.authorcolumn" /></th>
               <th style="width: 110px;"><fmt:message key="locate.lastmodifiedcolumn" /></th>
               <th>&nbsp;</th>
               <th>&nbsp;</th>
               <th>&nbsp;</th>
            </tr>
            	<mm:list referid="results">
            		<mm:node element="${contenttypes}" jspvar="node">
                        <tr onMouseOver="objMouseOver(this);"
                            onMouseOut="objMouseOut(this);">
                            <mm:compare referid="linktochannel" value="" inverse="true">
                               <td>
                                  <a title="<fmt:message key="search.link" />" href="LinkToChannelAction.do?objectnumber=<%=node.getStringValue("number")%>&channelnumber=<mm:write referid="linktochannel"/>">
	                                  <img src="../img/link.gif" width="15" height="15" alt="<fmt:message key="search.link" />"/>
                                  </a>
                               </td>
                            </mm:compare>
                           <td><mm:field name="number"/></td>
                           <td><mm:nodeinfo type="guitype"/></td>
                           <td><mm:field name="title"/></td>
                           <td>
                              <a href="#" onClick="showChannels(<mm:field name="number"/>);" title="<fmt:message key="search.showchannels" />">
                                 <% if (RepositoryUtil.hasCreationChannel(node)) { %>
                                    <% Node cc = RepositoryUtil.getCreationChannel(node); %>
                                    <% if (cc != null) { %>
                                          <%= cc.getStringValue("name") %>
                                    <% } %>
                                 <% } else { %>
                                    -- GEEN --
                                 <% } %>
                              </a>
                           </td>
                           <td>
                           	  <mm:relatednodes role="authorrel" type="user">
                           	  	<mm:field name="username" />
                           	  </mm:relatednodes>
                           </td>
                           <td>
                              <mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field>
                           </td>
                           <td style="padding:0px" align="right">
                              <a href="<mm:url page="../WizardInitAction.do">
                                          <mm:param name="objectnumber"><mm:field name="number"/></mm:param>
                                          <mm:param name="returnurl" value="<%="../editors/repository/SearchAction.do" + request.getAttribute("geturl")%>" />
                                       </mm:url>">
                                  <img src="../img/edit.gif" title="<fmt:message key="search.edit" />"/>
                              </a>
                            </td>
                           <td style="padding:0px" align="right">
                              <a href="#" onClick="showItem(<mm:field name="number"/>);" title="<fmt:message key="search.showitem" />">
                                 <img src="../img/details.gif" alt="<fmt:message key="search.showitem" />" />
                              </a>
                           </td>
                           <td style="padding:0px" align="right">
                              <a href="#" onClick="showChannels(<mm:field name="number"/>);" title="<fmt:message key="search.showchannels" />">
                                 <img src="../img/tree.gif" alt="<fmt:message key="search.showchannels" />" />
                              </a>
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