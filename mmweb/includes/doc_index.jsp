<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<mm:import externid="docnr"/>
<td>

<mm:present inverse="true" referid="docnr">
    <mm:list path="pages,articles" fields="articles.number,articles.body" nodes="$page">
        <br/>
        <h3><mm:field name="articles.title"/></h3>
        <br/>
        <mm:field name="html(articles.body)"/>
    </mm:list>
    <mm:list path="pages,documentation" nodes="$page" orderby="documentation.title" directions="down" >
        <li><a href="/index.jsp?portal=199&page=25011&docnr=<mm:field name="documentation.number"/>"><mm:field name="documentation.title"/></a>
    </mm:list>
</mm:present>

<mm:present referid="docnr">
    <mm:node referid="docnr">
        <br/>
        <h4><a href="/index.jsp?portal=199&page=25011">Back to overview</a></h4>
        <br/>
        <h3><mm:field name="title"/></h3>
        <br/>
        <mm:field name="html(body)"/>
    </mm:node>
</mm:present>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
