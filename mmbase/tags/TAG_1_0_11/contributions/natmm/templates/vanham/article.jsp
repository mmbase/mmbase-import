<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/functions.jsp" %>
<%@include file="includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
  <%
  imgFormat = ""; 
  String shortyRol = ""; 
  int iParCntr = 1;
  boolean showNextDotLine = false;
  if(artikelID.equals("-1")) { %>
   <mm:list nodes="<%=paginaID%>" path="pagina,contentrel,artikel" fields="artikel.number" orderby="contentrel.pos" directions="up" max="1">
    <mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
      <% artikelID = artikel_number;%>
    </mm:field>
   </mm:list><%
  }
  String text = "";
  %>
  <table cellpadding="0" cellspacing="0" border="0" style="width:780px;">
  <%@include file="includes/nav.jsp" %>
  <tr>
    <td></td>
    <td></td>
    <td></td>
    <td colspan="21">
    <table cellpadding="3" cellspacing="0" border="1"  class="content">
      <tr class="doc"><td></td></tr>
      <mm:node number="<%=artikelID%>" jspvar="article" notfound="skipbody">
      <tr>
        <td class="def" style="width:690px;padding-top:14px;padding-bottom:14px;"><a name="0" id="0"></a>
          <mm:field name="titel_zichtbaar"
               ><mm:compare value="0" inverse="true"><% 
                  text = LocaleUtil.getField(article,"titel",language, "");
                  if(!"".equals(text)) {
                     %><span class="colortitle"><%= text.toUpperCase() %></span><br/><br/><%
                  } 
               %></mm:compare
            ></mm:field><%
            // ** todo: space in front of image when right aligning ***
            boolean floatingText = true;
            %><mm:related path="posrel,images" max="1"
               ><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"><%
                  floatingText = !posrel_pos.equals("6"); 
               %></mm:field
            ></mm:related><%
            if(!floatingText) { %><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="vertical-align:top;"><% } 
            %><%@include file="includes/image_logic.jsp"
            %><% if(!floatingText) { %></td><td style="vertical-align:top;"><% 
            }
            text = LocaleUtil.getField(article,"intro",language, "");
            if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
               %><strong><%= text %></strong><%
               if(text.toUpperCase().indexOf("<P>")==-1) { %><br/><% } 
               showNextDotLine = true;
            } 
            text = LocaleUtil.getField(article,"tekst",language, "");
            if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
               %><%= text %><% 
               if(text.toUpperCase().indexOf("<P>")==-1) { %><br/><% }
               showNextDotLine = true;
            } 
            if(!floatingText) { %></td></tr></table><% } 
            %></p><mm:field name="reageer" jspvar="showdotline" vartype="String" write="false"
               ><mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos"
                  ><%@include file="includes/relatedparagraph.jsp" 
               %></mm:related
               ></mm:field>
        </td>
      </tr>
      </mm:node>
    </table>
    </td>
  </tr>
  </table>
  <br/>
</mm:log>
</mm:cloud>
<%@include file="includes/templatefooter.jsp" %>