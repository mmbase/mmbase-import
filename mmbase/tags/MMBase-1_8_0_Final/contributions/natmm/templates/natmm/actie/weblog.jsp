<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="../includes/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<% 
Calendar cal = Calendar.getInstance();
cal.setTime(now);
cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0);
long nowDay = cal.getTime().getTime()/1000; // the begin of today
long oneDay = 24*60*60;
%>
<mm:node number="<%= paginaID %>">
  <%@include file="includes/navsettings.jsp" %>
  <% 
  if(artikelID.equals("-1")) { 
    // last article before tomorrow
    objectConstraint = "artikel.begindatum < '" + (nowDay+oneDay) + "'";  %>
    <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="begindatum" directions="DOWN" max="1"
       constraints="<%= objectConstraint %>">
       <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
          <% artikelID = artikel_number;%>
       </mm:field>
    </mm:relatednodes>
    <%   
  } 
  %>
  <table cellspacing="0" cellpadding="0" width="744px;" align="center" border="0" valign="top">
    <tr>
      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:4px">
         <%@include file="includes/homelink.jsp" %>
         <mm:relatednodes type="teaser" path="rolerel,teaser" constraints="rolerel.rol='1'">
         <div style="padding-bottom:10px">
            <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
              <mm:relatednodes type="images" path="posrel,images" constraints="posrel.pos='1'">
                <tr>
                  <td><img src='<mm:image template="s(170)+part(0,0,170,98)" />' alt="<mm:field name="alt_tekst" />"></td>
                </tr>
              </mm:relatednodes>
              <tr>
                <td style="padding:5px 10px 10px 10px">
                  <b>Wie is&hellip;?</b><br/>
                  <mm:field name="titel" write="false" jspvar="teaser_titel" vartype="String">
		            <mm:field name="titel_zichtbaar" write="false" jspvar="teaser_tz" vartype="String">
                     <%
                     if(!teaser_titel.equals("")&&!teaser_tz.equals("0")){ 
                        %><span style="font:bold 110%;color:red">></span> <span class="colortitle"><%=teaser_titel%></span><br><%
                     }
                     %>
                  </mm:field>
                  </mm:field>
            		<mm:field name="omschrijving" write="false" jspvar="teaser_omschrijving" vartype="String">
                     <%
                     String description = HtmlCleaner.cleanBRs(HtmlCleaner.cleanPs(teaser_omschrijving)).trim();
                     if(!description.equals("")){
                        %><%=teaser_omschrijving%><%
                     }
                     %> 
                  </mm:field>
                </td>
              </tr>
            </table>
          </div>
          </mm:relatednodes>
          <%@include file="includes/mailtoafriend.jsp" %>
          <br/>
          <jsp:include page="../includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
            <jsp:param name="tl" value="asis" />
          </jsp:include>
      </td>

      <td style="padding:10px;padding-left:20px;vertical-align:top;width:350px;">
         <mm:node number="<%= artikelID%>" notfound="skip">
            <%@include file="includes/image_logic.jsp" %>
            <mm:node number="<%= paginaID %>">
              <div class="colortitle" style="font:bold 110%;"><mm:field name="titel"/></div>
              <div style="padding-bottom:5px;"><b><mm:field name="kortetitel"/></b></div>
            </mm:node>
            <span style="font:bold 110%;color:red">></span>
            <span class="colortitle"><mm:field name="titel"/></span>
            <span class="colortxt"><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
            ><mm:time time="<%=artikel_begindatum%>" format="d MMM yyyy"/></mm:field></span><br/>
            <mm:field name="intro" jspvar="text" vartype="String" write="false">
            <% 
            if(text!=null && !HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) {
              %><b><%= text %></b><%
            } %>
            </mm:field>
            <mm:field name="tekst" />
            <mm:relatednodes type="attachments" path="related,attachments" orderby="attachments.title">
               <%
                String imgName = ""; 
                String docType = "";
               %>
               <mm:field name="filename" jspvar="dummy" vartype="String" write="false">
                  <%@include file="includes/attachmentsicon.jsp"%>
               </mm:field>
               <mm:first>
                  <table class="dotline"><tr><td height="3"></td></tr></table>
               </mm:first>
               <span style="padding-left:5px; padding-right:5px"><a href="<mm:attachment />"><img src="../<%= imgName 
                 %>" alt="download <%= docType %>: <mm:field name="title"
                 />" border="0" style="vertical-align:text-bottom" /></a></span>
            </mm:relatednodes>
            <% 
            int iParCntr = 1;
            boolean showNextDotLine = false;
            boolean floatingText = true;
            %>
            <mm:field name="reageer" jspvar="showdotline" vartype="String" write="false"
            ><mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos"
               ><%@include file="../includes/relatedparagraph.jsp" 
            %></mm:related
            ><mm:related path="readmore,paragraaf" fields="paragraaf.number" orderby="readmore.pos"
               ><%@include file="../includes/relatedparagraph.jsp" 
            %></mm:related
            ></mm:field>
         </mm:node>
         <br/>
         <br/>
         <jsp:include page="../includes/shorty.jsp">
            <jsp:param name="s" value="<%= artikelID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sp" value="artikel,rolerel" />
            <jsp:param name="sr" value="1" />
         </jsp:include>
      </td>
      <td style="padding-right:10px;padding-left:10px;padding-bottom:10px;padding-top:10px;vertical-align:top;width:190px;">
         <jsp:include page="includes/nav.jsp">
            <jsp:param name="a" value="<%= artikelID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
         </jsp:include>
   		<jsp:include page="../includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
   	   </jsp:include>
         <br/>
         <jsp:include page="../includes/shorty.jsp">
            <jsp:param name="s" value="<%= artikelID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sp" value="artikel,rolerel" />
            <jsp:param name="sr" value="2" />
         </jsp:include>
      </td>
    </tr>
  </table>
</mm:node>
<%@include file="includes/footer.jsp" %>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
