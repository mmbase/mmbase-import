<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>
<% 
if(!articleId.equals("-1")) {
   %><mm:node number="<%= articleId %>"><mm:import id="extratext"> - <mm:field name="titel" /></mm:import></mm:node><%
} 
%><td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr><tr>
<td colspan="2" class="transperant" valign="top">
      <div class="<%= infopageClass %>" id="infopage">
      <%
         if(articleId.equals("-1")) { 
           %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel">
               <mm:field name="artikel.titel" jspvar="title" vartype="String" write="false"
               ><br/><br/>
               <span class="black"><b><%= title.toUpperCase() %></b></span></mm:field><br/>
          	   <span class="black"><mm:field name="artikel.intro"/></span>
            </mm:list>
            <% boolean useImage = true; %>
            <mm:node number="<%= paginaID %>">
               <mm:field name="titel_fra"><mm:compare value="0"><% useImage = false; %></mm:compare></mm:field>
            </mm:node>
            <% if(useImage) { 
               %><mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" max="1"
                     ><img src="<mm:node element="images"><mm:image /></mm:node>" alt="" border="0" usemap="#imagemap">
               </mm:list>
               <map name="imagemap"><%
                  	String targetObject = "artikel";
                  	String readmoreUrl = "ippolygon.jsp?p=" + paginaID; 
                  	if(!refererId.equals("")) { readmoreUrl += "&referer=" + refererId; }
                  	readmoreUrl += "&article=";
                  	%><%@include file="includes/imap/relatedpolygons.jsp" %><%
                  	readmoreUrl = "ippolygon.jsp?referer=" + paginaID + "&p=";
                  	targetObject = "pagina2";
                  	%><%@include file="includes/imap/relatedpolygons.jsp" 
                %></map><%
            } else {
               String targetObject = "artikel";
            	String readmoreUrl = "ippolygon.jsp?p=" + paginaID; 
            	if(!refererId.equals("")) { readmoreUrl += "&referer=" + refererId; }
            	readmoreUrl += "&article=";
            	%><%@include file="includes/relatedlinkeditems.jsp" %><%
            	readmoreUrl = "ippolygon.jsp?referer=" + paginaID + "&p=";
            	targetObject = "pagina2";
            	%><%@include file="includes/relatedlinkeditems.jsp" %><%
            }
         } else { 
            %>
            <%@include file="includes/back_print.jsp" %>
            <mm:list nodes="<%= articleId %>" path="artikel"
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
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
