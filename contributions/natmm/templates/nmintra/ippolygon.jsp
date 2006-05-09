<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" %><% 
if(!articleId.equals("")) {
   %><mm:node number="<%= articleId %>"><mm:import id="extratext"> - <mm:field name="titel" /></mm:import></mm:node><%
} 
%><td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr><tr>
<td colspan="2" class="transperant" valign="top">
      <div class="<%= infopageClass %>">
      <%
         if(articleId.equals("")) { 
           %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel">
               <mm:field name="artikel.titel" jspvar="title" vartype="String" write="false"
               ><br/><br/>
               <span class="black"><b><%= title.toUpperCase() %></b></span></mm:field><br/>
          	   <span class="black"><mm:field name="artikel.intro"/></span>
            </mm:list>
            <% boolean useImage = true; %>
            <mm:node number="<%= pageId %>">
               <mm:field name="titel_fra"><mm:compare value="0"><% useImage = false; %></mm:compare></mm:field>
            </mm:node>
            <% if(useImage) { 
               %><mm:list nodes="<%= pageId %>" path="pagina,posrel,images" max="1"
                     ><img src="<mm:node element="images"><mm:image /></mm:node>" alt="" border="0" usemap="#imagemap">
               </mm:list>
               <map name="imagemap"><%
                  	String targetObject = "artikel";
                  	String readmoreUrl = "ippolygon.jsp?p=" + pageId; 
                  	if(!refererId.equals("")) { readmoreUrl += "&referer=" + refererId; }
                  	readmoreUrl += "&article=";
                  	%><%@include file="includes/relatedpolygons.jsp" %><%
                  	readmoreUrl = "ippolygon.jsp?referer=" + pageId + "&p=";
                  	targetObject = "pagina2";
                  	%><%@include file="includes/relatedpolygons.jsp" 
                %></map><%
            } else {
               String targetObject = "artikel";
            	String readmoreUrl = "ippolygon.jsp?p=" + pageId; 
            	if(!refererId.equals("")) { readmoreUrl += "&referer=" + refererId; }
            	readmoreUrl += "&article=";
            	%><%@include file="includes/relatedlinkeditems.jsp" %><%
            	readmoreUrl = "ippolygon.jsp?referer=" + pageId + "&p=";
            	targetObject = "pagina2";
            	%><%@include file="includes/relatedlinkeditems.jsp" %><%
            }
         } else { 
            if(!postingStr.equals("|action=print")) {
               %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&pst=|action=print">print</a></div><%
            } 
            %><mm:list nodes="<%= articleId %>" path="artikel"
               ><table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr><td style="padding:10px;padding-top:18px;">
                     <%@include file="includes/relatedarticle.jsp" %>
                  </td></tr>
               </table>      
            </mm:list><%
         } 
      %>
      </div>
</td>
<%@include file="includes/footer.jsp" 
%></mm:cloud>
