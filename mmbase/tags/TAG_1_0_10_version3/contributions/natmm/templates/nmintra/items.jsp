<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%

int thisOffset = 0;
try{
    if(!offsetId.equals("")){
        thisOffset = Integer.parseInt(offsetId);
        offsetId ="";
    }
} catch(Exception e) {} 

%><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 


%><td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
    <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td>
<table cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td><img src="media/spacer.gif" width="381" height="10"></td>
        <td><img src="media/spacer.gif" width="100" height="10"></td>
        <td><img src="media/spacer.gif" width="10" height="10"></td>
    </tr>
    <tr>
        <td colspan="3" align="left">
            <table cellpadding="0" cellspacing="0">
            <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%@include file="includes/relatedteaser.jsp" 
            %><img src="media/spacer.gif" width="1" height="1"></td>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="3">
        <table cellpadding="0" cellspacing="0" align="left">
        <tr><%
            int numberOfColumns = 2;
            int colNumber = 1; 
            while(colNumber<=numberOfColumns) { 
                %><td><%@include file="includes/itemstable.jsp" %></td><%
                if(colNumber<numberOfColumns) { 
                    %><td><img src="media/spacer.gif" width="10" height="1"></td><% 
                } colNumber++; 
            } %></tr>
            </table>
            </td>
            <td><img src="media/spacer.gif" width="10" height="1"></td>
        </tr>
        </table>
        </td>
    </tr>
</table><%

// show navigation to other pages if there are more than 10 articles
int listSize = 0; 
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,linklijst"
	><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
></mm:list><% if(listSize>10) { 
%><table cellpadding="0" cellspacing="0" border="0" align="center">
    <tr>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><img src="media/spacer.gif" width="1" height="1"></td>
        <td><div><%
            if(thisOffset>0) { 
                %><a href="items.jsp<%= templateQueryString  %>&offset=<%= thisOffset-1 %>">[<<- vorige ]</a>&nbsp;&nbsp;<%
            }for(int i=0; i < (listSize/10 + 1); i++) { 
                if(i==thisOffset) {
                    %><%= i+1 %>&nbsp;&nbsp;<%
                } else { 
                    %><a href="items.jsp<%=  templateQueryString  %>&offset=<%= i %>"><%= i+1 %></a>&nbsp;&nbsp;<%
                } 
            }
            if(thisOffset+1<(listSize/10 + 1)) { 
                %><a href="items.jsp<%= templateQueryString %>&offset=<%= thisOffset+1 %>">[volgende ->>]</a><%
            } 
        %></div>
        </td>
    </tr>
    <tr>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
    </tr>
</table><%
}%><%@include file="includes/pageowner.jsp" 
    %></td>
    <td><img src="media/spacer.gif" width="10" height="1"></td>
</tr>
</table>
</div>
</td>
<td><%

// *********************************** right bar *******************************
%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>