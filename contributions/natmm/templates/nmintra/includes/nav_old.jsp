<% 
// *** referer is used to open navigation on a page which is not visible in the navigation ***
String tmp_pageId = pageId;
if(!refererId.equals("")) { 
   boolean pageIsVisible = false;
   %><mm:list nodes="<%= rubriekId %>" path="pijler,posrel,page" max="1" constraints="<%= "page.number='" + pageId + "'" %>"><%
      pageIsVisible = true;
   %></mm:list><%
   if(!pageIsVisible) { 
      %><mm:list nodes="<%= rubriekId %>" path="pijler,posrel,page1,dposrel,page2" max="1" constraints="<%= "page2.number='" + pageId + "'" %>"><%
         pageIsVisible = true;
      %></mm:list><%
   }
   if(!pageIsVisible) { pageId = refererId; }
}
// *** page: translate alias back into number ***
%><mm:node number="<%= pageId %>" notfound="skipbody"
    ><mm:field name="number" jspvar="page_number" vartype="String" write="false"><%
        pageId = page_number; 
    %></mm:field
></mm:node><%
// *** pijler: translate alias back into number ***
if(!rubriekId.equals("")) { 
    %><mm:node number="<%= rubriekId %>"
        ><mm:field name="number" jspvar="pijler_number" vartype="String" write="false"><%
            rubriekId = pijler_number; 
        %></mm:field
    ></mm:node><% 
} 
boolean bIsFirst = false;
%><div class="navlist">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><img src="media/spacer.gif" width="1" height="527"></td>
<td>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
    <td><img src="media/spacer.gif" width="158" height="25"></td>
</tr>
<mm:list nodes="<%= websiteId %>" path="site,posrel,page" constraints="posrel.pos='1'"
    ><mm:field name="page.number" jspvar="page_number" vartype="String" write="false"><%
    if(isPreview||isVisible(cloud,websiteId,null,page_number,visitorGroup,out)) {
        String pijler_number = "";
        if(pageId.equals("")) { pageId = page_number; }
        %><tr><td style="padding-left:19px;padding-bottom:7px;">
            <a href=<mm:node element="page"><%@include file="../includes/pageurl.jsp" %></mm:node
                > class="menuItem<mm:field name="page.number"><mm:compare value="<%= pageId %>">Active</mm:compare></mm:field
                    >"><mm:field name="page.title" /></a>
        </td></tr><%
    }
    %></mm:field
></mm:list
><mm:list nodes="<%= websiteId %>" path="site,posrel,pijler"
    orderby="posrel.pos" directions="UP"
    ><mm:field name="pijler.number" jspvar="pijler_number" vartype="String" write="false"><%

    // *** list the pijlers ***
    %><mm:list nodes="<%= pijler_number %>" path="pijler,posrel,page"
                orderby="posrel.pos" directions="UP" max="1"
        ><mm:field name="page.number" jspvar="page_number" vartype="String" write="false"><%
        if(isPreview||isVisible(cloud,websiteId,pijler_number,page_number,visitorGroup,out)) {             
            if(pageId.equals("")) { pageId = page_number; }
            String thisPijlerClass = "menuItem"; 
            if(rubriekId.equals(pijler_number)) { thisPijlerClass = "menuItem"; } 
            if(!bIsFirst) { 
                  %><tr><td><img src="media/spacer.gif" width="1" height="7"></td></tr><% 
                  bIsFirst = false;
            } %>
              <tr>
                <td style="padding-left:19px;letter-spacing:1px;"><a href=<mm:node element="page"><%@include file="../includes/pageurl.jsp" %></mm:node
                    > class="menuItem<mm:field name="pijler.number"
                                ><mm:compare value="<%= rubriekId %>"
                                    ><mm:field name="page.number"
                                        ><mm:compare value="<%= pageId %>"
                                            ><mm:field name="posrel.pos"
                                                ><mm:compare value="0"
                                                    >Active</mm:compare
                                            ></mm:field
                                        ></mm:compare
                                    ></mm:field
                                ></mm:compare
                            ></mm:field
                            >"><mm:field name="pijler.title" /></a>
                </td></tr><%
             }
        %></mm:field
    ></mm:list><%
    if(rubriekId.equals(pijler_number)) { 

    // *** list the pages ***
    %><mm:list nodes="<%= pijler_number %>" path="pijler,posrel,page"
            orderby="posrel.pos" directions="UP" constraints="posrel.pos <> '0'"
        ><mm:field name="page.number" jspvar="super_page" vartype="String" write="false"><%
        if(isPreview||isVisible(cloud,websiteId,pijler_number,super_page,visitorGroup,out)) { 
           if(pageId.equals("")) { pageId = super_page; }
            %><tr>
                <td style="padding-left:19px;"><table border="0" cellpadding="0" cellspacing="0"><tr><td style="color:white;"><li></td>
                    <td style="letter-spacing:1px;"><a href=<mm:node element="page"><%@include file="../includes/pageurl.jsp" %></mm:node
                        > class="menuItem<mm:field name="page.number"><mm:compare value="<%= pageId %>">Active</mm:compare></mm:field
                            >"><mm:field name="page.title" /></a></td></tr></table>
                </td>
            </tr><% 

            // *** lets look whether there are subpages under this page ***
            // *** only show subpages if the super_page or one of its sub_pages is active ***
            boolean subPageOpen = pageId.equals(super_page); 
            if(!subPageOpen) { 
                %><mm:list nodes="<%= super_page %>" path="page1,dposrel,page2"
                    orderby="dposrel.pos" directions="UP"
                    ><mm:field name="page2.number" jspvar="sub_page" vartype="String" write="false"><%
                        if(pageId.equals(sub_page)) { subPageOpen = true; } 
                    %></mm:field
                ></mm:list><% 
            } 
            if(subPageOpen) { 
                %><mm:list nodes="<%= super_page %>" path="page1,dposrel,page2"
                    orderby="dposrel.pos" directions="UP"
                    ><mm:field name="page2.number" jspvar="sub_page" vartype="String" write="false"><%
                        if(isPreview||isVisible(cloud,websiteId,pijler_number,sub_page,visitorGroup,out)) { 
                            if(pageId.equals("")) { pageId = sub_page; }
                            %><mm:node element="page2"
                            ><tr><td style="padding-left:36px;">
                                <table border="0" cellpadding="0" cellspacing="0"><tr><td style="color:white;font-weight:bold;">-&nbsp;</td>
                                <td style="letter-spacing:1px;"><a href=<%@include file="../includes/page2url.jsp"
                                    %> class="menuItem<% if(sub_page.equals(pageId)) { %>Active<% } 
                                        %>"><mm:field name="title" /></a></td></tr></table>
                            </td></tr>
                            </mm:node><%
                        } 
                    %></mm:field
                ></mm:list><% 
            }
        }
        %></mm:field
    ></mm:list><% 
    }
    
    %></mm:field
></mm:list
></table>
        </td>
    </tr>
</table></div>
<% // *** reset pageId to original value, if referer is used ***
if(!refererId.equals("")) { pageId = tmp_pageId; }
%>