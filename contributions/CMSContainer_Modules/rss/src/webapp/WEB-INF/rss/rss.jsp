<?xml version="1.0" encoding="UTF-8" ?>
<%@page language="java" contentType="text/xml; charset=utf-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://finalist.com/csmc" prefix="cmsc" %>
<%@page import="com.finalist.cmsc.repository.ContentElementUtil"%>

<mm:content type="text/xml" encoding="UTF-8">
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="contenttypes" jspvar="contenttypes"><%= ContentElementUtil.CONTENTELEMENT %></mm:import>
<mm:import externid="title" jspvar="title" />

<mm:cloud jspvar="cloud" >
<c:set var="renderUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/content/"/>

<rss version="2.0">
    <channel>
        <title>${title}</title>
        <link>${link}</link>
        <language>${language}</language>
        <description>${description}</description>
        <copyright>${copyright}</copyright>
        <managingEditor>${managingEditor}</managingEditor>
        <webMaster>${webMaster}</webMaster>
        <mm:isnotempty referid="results">
            <mm:list referid="results">
             <item>
                  <mm:field name="${contenttypes}.number" id="number">
                     <mm:node number="${number}">
                      <title><mm:field name="title"/></title>
                      <link>${renderUrl}<mm:field name="number"/></link>
                      <description><mm:field name="subtitle"/> <mm:field name="intro"/></description>
                      <author></author>
                      <category></category>
                      <comments></comments>
                      <pubDate>
                              <mm:field name="publishdate" id="articleDate">
                                  <mm:time time="$articleDate" format="rfc822" />
                              </mm:field>
                      </pubDate>
                     </mm:node>
                  </mm:field>   
                 </item>
                 <mm:remove referid="articleDate" />
                     
            </mm:list>
        </mm:isnotempty>
    </channel>
</rss>
</mm:cloud>
</mm:content>