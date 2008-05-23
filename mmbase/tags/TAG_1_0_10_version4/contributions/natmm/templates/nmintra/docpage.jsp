<%@page import="com.finalist.tree.*,nl.leocms.util.tools.documents.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/check_documents_root.jsp" %>
<% 
// if no documents are related to this page, find the document which filename is equal to the page title (=subtreeDoc)
// and add all documents under the subtreeDoc to this page
%><mm:node number="<%= paginaID %>" jspvar="thisPage">
   <mm:related path="posrel,documents" max="1" constraints="documents.type='file'">
      <mm:import id="page_contains_document" />
   </mm:related>
   <mm:notpresent referid="page_contains_document">
      <mm:listnodes type="documents" constraints="<%= "filename LIKE '%" + thisPage.getStringValue("titel") + "%'" %>" jspvar="subtreeDoc">
      <% nl.leocms.util.tools.documents.DirReader.mergeSubtree(cloud,thisPage,subtreeDoc); %>
      </mm:listnodes>
   </mm:notpresent>
</mm:node>
<%
// show navigation to other pages if there are more than pageSize documents

int thisOffset = 0;
try{
    if(!offsetId.equals("")){
        thisOffset = Integer.parseInt(offsetId);
        offsetId ="";
    }
} catch(Exception e) {} 

int pageSize = 20; 
int lastPage = (thisOffset+1)*pageSize;
int listSize = 0; 
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,documents"
   ><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
></mm:list><% 
if(lastPage>listSize) { lastPage = listSize; }
int numberOfPages = (listSize-1)/pageSize+1;
%>
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
        <table cellpadding="0" cellspacing="0" style="width:100%;">
        <tr><%
            int numberOfColumns = 2;
            int colNumber = 1; 
            while(colNumber<=numberOfColumns) { 
                %><td style="width:<%= 100/numberOfColumns %>%;"><%@include file="includes/documentstable.jsp" %></td><%
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
</table>
<%
if(listSize>pageSize) { 
   %><table cellpadding="0" cellspacing="0" border="0" align="center">
       <tr>
           <td><img src="media/spacer.gif" width="10" height="1"></td>
           <td><img src="media/spacer.gif" width="1" height="1"></td>
           <td><div><%
            if(thisOffset>0) { 
                %><a href="docpage.jsp<%= templateQueryString  %>&offset=<%= thisOffset-1 %>">[<<- vorige ]</a>&nbsp;<%
            } 
            for(int i=0; i < numberOfPages; i++) { 
                if(i==thisOffset) {
                    %>&nbsp;<%= i+1 %>&nbsp;<%
                } else { 
                    %>&nbsp;<a href="docpage.jsp<%=  templateQueryString  %>&offset=<%= i %>"><%= i+1 %></a>&nbsp;<%
                } 
                if(i<(listSize/pageSize)) { %><% } 
            }
            if(thisOffset+1<numberOfPages) { 
                %><a href="docpage.jsp<%= templateQueryString %>&offset=<%= thisOffset+1 %>">[volgende ->>]</a><%
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