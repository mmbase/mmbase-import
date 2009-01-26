<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String styleSheet = request.getParameter("rs");
   String sID = request.getParameter("s");
   String shortyRol = request.getParameter("sr");
   int maxShorties = 20;
   imgFormat = "shorty";
   int padding = 3;
   if(request.getParameter("sp")!=null && request.getParameter("sp").equals("natuurgebieden,posrel")) { 
      padding = 0;
   } 
   PaginaHelper ph = new PaginaHelper(cloud);
%><%@include file="../includes/shorty_logic_1.jsp" %>
<% for (int i =0; i<shortyCnt;i++){ %>
	<%@include file="../includes/shorty_logic_2.jsp" %>
	<% // show the shorty %>
	<mm:node number="<%= shortyID[i] %>">
		<mm:field name="titel" write="false" jspvar="shorty_titel" vartype="String">
		<mm:field name="titel_zichtbaar" write="false" jspvar="shorty_tz" vartype="String">
		<mm:field name="omschrijving" write="false" jspvar="shorty_omschrijving" vartype="String">
      <% if((shortyCnt > 1)&&(i>0)){%>
			<table class="dotline"><tr><td height="3"></td></tr></table>
		<% } %>
		<table width="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td style="vertical-align:top;padding:<%= padding %>px;padding-top:0px;">
         		<%@include file="../includes/image_logic.jsp" %>
   				<% if(shortyRol.equals("1")){ // for the middle column %>
   				   <mm:present referid="relatedimagefound">
   				      </td>
   				      <td style="vertical-align:top;padding:3px;padding-top:0px;">
   				   </mm:present>
   				<% } %>
   				<% if(!shorty_titel.equals("")&&!shorty_tz.equals("0")){ %>
                     <% linkTXT = shorty_titel; %>
                     <mm:import id="divstyle">font-weight:bold;line-height:95%;</mm:import>
      			      <%@include file="../includes/validlink.jsp" %>
      			      <mm:remove referid="divstyle" />
   				<% } %>
   				<% if(!shorty_omschrijving.equals("")){ %><%= shorty_omschrijving %><% } %>
               <mm:import id="divstyle" />
    				<mm:import id="hrefclass" />
   				<% linkTXT =  HtmlCleaner.cleanBRs(HtmlCleaner.cleanPs(readmoreTXT)).trim(); %>
   			   <%@include file="../includes/validlink.jsp" %>
   			   <mm:remove referid="hrefclass" />
   			   <mm:remove referid="divstyle" />
				</td>
			</tr>
		</table>
      <% if(padding==0) { %><br/><% } %>
		</mm:field>
		</mm:field>
		</mm:field>
	</mm:node>
<% } %>
</mm:cloud>
		