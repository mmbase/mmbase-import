<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>

<% boolean bibliotheekStyle = !printPage && NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek"); %>
<td <% if(bibliotheekStyle) { %>colspan="2"<% } %>><%@include file="includes/pagetitle_vraagbaak.jsp" %></td>

<% 
   String rightBarTitle = "";
%>
<td><%@include file="includes/rightbartitle.jsp" %></td>
   
</tr>
<tr>
<td class="transperant" <% if(NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek")) { %>colspan="2"<% } %>>
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
    <a name="top"></a>
    
    <div align="right">
       <mm:node number="<%= rbLogoID %>" notfound="skipbody"><img src="<mm:image template='s(120x80)'/>" border="0" alt=""></mm:node>
    </div>
    
    <% 
      if(!"false".equals(request.getParameter("showteaser"))) { 
         %>

         <mm:node number="<%= paginaID %>">
           <mm:field name="titel_zichtbaar">
              <mm:compare value="0" inverse="true">
                <table bgcolor="#9ab7e3" width="100%" >
                <tr>
                   <td>
                      <div style="float:left;" class="pageheader"><mm:field name="titel" /></div>
                      
                     <%
                     if(!printPage) {
                       %>
                       <div style="float:right; letter-spacing:1px;">
                         <nobr>
                           <a href="javascript:history.go(-1);">terug</a> /
                           <a target="_blank" href="?<%= request.getQueryString() %>&pst=|action=print">print</a>
                         </nobr>
                       </div>
                       <%
                     }
                     %>                      
                      
                   </td>
                </tr>
                </table>
                <mm:import id="title_is_shown" />
              </mm:compare>
           </mm:field>
           <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
            <% 
               if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
                  %>
                <mm:notpresent referid="title_is_shown"><br/></mm:notpresent>
                <span class="black"><mm:write /></span>
                <br/><br/>
                <%
               } 
            %>
            </mm:field>
         </mm:node>

         <%
      }
      String startnodeId = articleId;
      String articlePath = "artikel";
      String articleOrderby = "";
      String vraagPath = "vraagbaak";
      String vraagOrderby = "";
      if(articleId.equals("-1")) { 
      startnodeId = paginaID;
      articlePath = "pagina,contentrel,artikel";
      articleOrderby = "contentrel.pos";
      vraagPath = "pagina,contentrel,vraagbaak";
      vraagOrderby = "contentrel.pos";
      
      }
      %>
      <%-- list of vraagbaaks --%>
      <mm:list nodes="<%= startnodeId %>"  path="<%= vraagPath %>" orderby="<%= vraagOrderby %>"
         ><mm:size jspvar="size" write="false" >
         <mm:isgreaterthan value="1">
        	<mm:first><p class="black"><b>Inhoud:</b></mm:first>
         	<mm:node element="vraagbaak">  
          		<li/> <a href="<%= ph.createPaginaUrl(startnodeId,request.getContextPath()) %>#<mm:field name="number"/>"><mm:field name="titel"/></a>
         	</mm:node>
		</mm:isgreaterthan>
      	</mm:size>
      </mm:list>
      </p><br/>
      
      <mm:list nodes="<%= startnodeId %>"  path="<%= articlePath %>" orderby="<%= articleOrderby %>"
         ><%@include file="includes/relatedarticle.jsp" 
      %></mm:list>
      
      <%-- import vraagbaaks --%>
      <% String currentVraag = ""; %>
      <mm:list nodes="<%= startnodeId %>"  path="<%= vraagPath %>" orderby="<%= vraagOrderby %>"
         >
         <mm:node element="vraagbaak" id="this_vraagbaak">  
         <mm:field name="number" jspvar="dummy" vartype="String" write="false" ><% currentVraag = dummy; %></mm:field>
         <a name="<%=currentVraag%>"></a>
         <jsp:include page="includes/relatedvraagbaak.jsp">
         	<jsp:param name="v" value="<%=currentVraag%>"/>
         	<jsp:param name="c" value="<%=startnodeId%>"/>
            <jsp:param name="rb" value="<%=iRubriekStyle%>"/>
            <jsp:param name="rbid" value="<%=rubriekId%>"/>
            <jsp:param name="pgid" value="<%=paginaID%>"/>     
            <jsp:param name="ssid" value="<%=subsiteID%>"/>       
         </jsp:include>
         </mm:node>
     </mm:list>
       
      <mm:node number="<%= paginaID %>">
         <%@include file="includes/relatedcompetencies.jsp" %>
      </mm:node>
      <%@include file="includes/pageowner.jsp" 
    %></td>
</tr>
</table>
</div>
</td>

<% 

if(!printPage) { 
   // *********************************** right bar *******************************
   String styleClass = "white";
   String styleClassDark = "white";
         
%>
   <td style="padding-left:10px;">
   <div class="rightcolumn" id="rightcolumn">
   
   <p>
   <%@include file="includes/contentblock_letterindex.jsp" %>
   </p><br/>
   
   <mm:list nodes="<%= paginaID %>" path="pagina,readmore,contentblocks" orderby="readmore.pos">
      <mm:node element="contentblocks">
         <%@include file="includes/contentblockdetails.jsp" %>
      </mm:node>
      <br/>
   </mm:list>
   </div>
   </td><%
} %>

<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
