<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
%><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
    <tr>
        <td colspan="3" align="left">
            <table cellpadding="0" cellspacing="0">
            <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%@include file="includes/relatedteaser.jsp" 
            %><img src="media/spacer.gif" width="1" height="1"></td>
            </table>
        </td>
    </tr>
    <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
    <td>
<table cellpadding="0" cellspacing="0" align="center">
    <tr><%
    int numberOfColumns = 2;
    int colNumber = 1; 
    while(colNumber<=numberOfColumns) { 
        %><td><%@include file="includes/linkitems.jsp" %></td><%
        if(colNumber<numberOfColumns) { 
            %><td><img src="media/spacer.gif" width="10" height="1"></td><% 
        } colNumber++; 
    } %></tr>
    </table><%@include file="includes/pageowner.jsp" 
    %></td>
    <td><img src="media/spacer.gif" width="10" height="1"></td>
</tr>
</table>
</div>
</td><%

// *************************************** right bar *******************************
%><td>&nbsp;</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>