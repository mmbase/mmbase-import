<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<mm:locale language="nl">
<%
imgFormat = ""; 

String objectID = request.getParameter("o");
String formatID = request.getParameter("f"); if(formatID!=null) { imgFormat = formatID; }
String showintroID = request.getParameter("s"); if(showintroID==null) { showintroID = "true"; }
String showdateID = request.getParameter("q"); if(showdateID==null) { showdateID = "false"; }


String shortyRol = ""; 
int iParCntr = 1;
boolean showNextDotLine = false;
%>
<%@include file="../includes/getstyle.jsp" %>
<mm:node number="<%=objectID%>" notfound="skipbody">
<%-- For vertical logo of naardermeer site to be displayed without covering article content --%>
  <% if (isNaardermeer.equals("true")) { %>		
   	<table width="88%" cellpadding="0" cellspacing="0" border="0">
  <% } else { %>
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<% } %>
<tr>
	<td valign="top">
	<a name="0" id="0"></a>
   	<jsp:include page="../includes/panno_extratext.jsp">
         <jsp:param name="o" value="<%= objectID %>" />
      </jsp:include>
		<mm:field name="titel_zichtbaar"
         ><mm:compare value="0" inverse="true"
            ><mm:field name="titel" jspvar="titel" vartype="String" write="false"
               ><mm:isnotempty><span class="colortitle"><%= titel.toUpperCase() %></span><br></mm:isnotempty
            ></mm:field
         ></mm:compare
      ></mm:field><%
      if(showdateID.equals("true")) { 
         %><mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"
            ><mm:time time="<%=artikel_begindatum%>" format="dd-MM-yyyy"/></br></mm:field><% 
      }
      // ** todo: space in front of image when right aligning ***
      boolean floatingText = true;
      %><mm:related path="posrel,images" max="1"
         ><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"><%
            floatingText = !posrel_pos.equals("6"); 
         %></mm:field
      ></mm:related><%
      if(!floatingText) { %><table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td style="vertical-align:top;"><% } 
         %><%@include file="../includes/image_logic.jsp"
      %><% if(!floatingText) { %></td><td style="vertical-align:top;"><% 
      }
      if(showintroID.equals("true")) { 
         %><mm:field name="intro" jspvar="intro" vartype="String" write="false"
            ><% if(intro!=null&&!HtmlCleaner.cleanText(intro,"<",">","").trim().equals("")) { 
                  %><strong><%= intro %></strong><% if(intro.toUpperCase().indexOf("<P>")==-1) { %><br/><% } 
                  showNextDotLine = true;
            } 
         %></mm:field><% 
      } 
      %><mm:field name="tekst" jspvar="tekst" vartype="String" write="false"><%
         if(tekst!=null&&!HtmlCleaner.cleanText(tekst,"<",">","").trim().equals("")) { 
            %><%= tekst %><% if(tekst.toUpperCase().indexOf("<P>")==-1) { %><br/><% }
            showNextDotLine = true;
         } 
      %></mm:field><%
      if(!floatingText) { %></td></tr></table><% } 
      %></p><mm:field name="reageer" jspvar="showdotline" vartype="String" write="false"
         ><mm:related path="posrel,paragraaf" fields="paragraaf.number" orderby="posrel.pos"
            ><%@include file="../includes/relatedparagraph.jsp" 
         %></mm:related
         ><mm:related path="readmore,paragraaf" fields="paragraaf.number" orderby="readmore.pos"
            ><%@include file="../includes/relatedparagraph.jsp" 
         %></mm:related
      ></mm:field>
	</td>
</tr>
</table>
<br/>
</mm:node>
</mm:locale>
</mm:cloud>