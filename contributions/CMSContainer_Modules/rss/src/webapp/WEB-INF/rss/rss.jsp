<?xml version="1.0" encoding="UTF-8" ?>
<%@page language="java" contentType="text/xml; charset=utf-8" %>
<%@page session="false" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<mm:content type="text/xml" encoding="UTF-8">
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<mm:import externid="title" jspvar="title" />
<mm:cloud jspvar="cloud" >
<rss version="2.0">
    <channel>
        <title>${title}</title>
        <link>${link}</link>
        <language>${language}</language>
        <description>${description}</description>
        <c:if test="${not empty copyright}">
            <copyright>${copyright}</copyright>
        </c:if>
        <c:if test="${not empty managingEditor}">
        	<managingEditor>${managingEditor}</managingEditor>
        </c:if>
        <c:if test="${not empty webMaster}">
        	<webMaster>${webMaster}</webMaster>
        </c:if>
        <mm:listnodes referid="results">
        	<%-- make an absolute img tag for the first image and remove the jsessionid if present, rss readers don't want it --%>
            <mm:relatednodescontainer type="images" role="imagerel" searchdirs="destination">
                <mm:sortorder field="imagerel.pos" />
                <mm:relatednodes>
                    <mm:first>
                        <c:set var="mmbaseUrl">
                            <mm:image />
                        </c:set>
                        <c:set var="jsessionid" value=";jsessionid" />
                        <c:if test="${fn:containsIgnoreCase(mmbaseUrl, jsessionid)}">
                            <c:set var="mmbaseUrl" value="${fn:substringBefore(mmbaseUrl, ';')}" />
                        </c:if>
                        <c:set var="imgUrl">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${mmbaseUrl}</c:set>
                    </mm:first>
                </mm:relatednodes>
            </mm:relatednodescontainer>
            <item>
                  <title><mm:field name="title"/></title>
                  <link><cmsc:contenturl absolute="true" /></link>
                  <mm:field name="intro" jspvar="intro" write="false"/>
                  <cmsc:removehtml var="cleanIntro" html="${intro}"/>
                  <description>${cleanIntro}</description>
                  <pubDate><mm:field name="publishdate"><mm:time format="rfc822" /></mm:field></pubDate>
                  <guid><cmsc:contenturl absolute="true" /></guid>
                  <c:if test="${not empty imgUrl}">
                  <comments>${imgUrl}</comments>
                  </c:if>
             </item>
    	     <c:set var="imgUrl" value=""/>
        </mm:listnodes>
    </channel>
</rss>
</mm:cloud>
</mm:content>