<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" escaper="inline">
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
        <mm:field name="articles.body" escape="p"/>
    </mm:list>
    <mm:list path="pages,documentation" nodes="$page" orderby="documentation.title" directions="down" >
        <li><a href="<mm:url referids="portal,page"><mm:param name="docnr"><mm:field name="documentation.number"/></mm:param></mm:url>"><mm:field name="documentation.title"/></a>
    </mm:list>
</mm:present>

<mm:present referid="docnr">
    <mm:node referid="docnr">
        <br/>
        <h4><a href="<mm:url referids="portal,page" />">Back to overview</a></h4>
        <br/>
        <h3><mm:field name="title"/></h3>
        <br/>
        <mm:field name="body" escape="p"/>
    </mm:node>
</mm:present>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
</mm:content>