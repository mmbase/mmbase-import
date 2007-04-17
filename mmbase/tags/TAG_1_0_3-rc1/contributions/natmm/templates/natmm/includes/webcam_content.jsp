<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>

<%
String styleSheet = request.getParameter("rs");
String lnRubriekID = request.getParameter("lnr");
String rnImageID = request.getParameter("rnimageid");

String shortyRol = ""; 

String attachmentID = request.getParameter("at");
%>
<mm:import externid="showdate" jspvar="showdateID">false</mm:import>
<mm:import externid="showpageintro">false</mm:import>
<mm:import externid="shownav">false</mm:import>
<mm:cloud jspvar="cloud">

<mm:node number="<%=paginaID%>" notfound="skipbody">

<table width="539px;" border="0" cellspacing="0" cellpadding="0">
   <tr>
   	<td style="vertical-align:top;padding-right:10px;padding-bottom:10px;width:364px;">
	
	<% 
	PaginaHelper pHelper = new PaginaHelper(cloud);
	if (attachmentID == null) {
	%>
	<p class="colortitle"><mm:field name="titel" vartype="String" /></p>
	
	<p><mm:field name="omschrijving" vartype="String" /></p>

	<mm:related path="contentrel,link" fields="link.titel, link.url" max="1">
		<iframe src="<mm:field name="link.url"/>" width="352" height="258" scrolling="no" frameborder="0" marginwidth="0"marginheight="0" ></iframe>
	</mm:related>
	     
	<p>
	<br/>
	<img src="media/logo_nm.gif"><img src="media/logo_nuon.gif">
	<br/>
	<span class="colortitle">Highlight video's</span>
	<br>
		<mm:related path="contentrel,attachments" fields="attachments.titel">
		<a href="<%= pHelper.createPaginaUrl(paginaID,request.getContextPath()) %>?at=<mm:field name="attachments.number" />">
		<img src="media/link_fun.gif" alt="" border="0" />
		<mm:field name="attachments.titel" />
		</a><br/>
	</mm:related>
	<br/>
	</p>
	
	<%
	} else {
	%>
	<mm:node number="<%= attachmentID %>">
	
	<p class="colortitle"><mm:field name="title" /></p>
	<%--<p><mm:field name="description" /></p>--%>
      
      <mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false"><%
					if(attachments_filename.indexOf(".mp3")>-1
								||attachments_filename.indexOf(".mpeg")>-1
								||attachments_filename.indexOf(".mpg")>-1){
						%><object id="MediaPlayer" 
							classid="CLSID:22D6f312-B0F6-11D0-94AB-0080C74C7E95"
							codebase="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701"
							standby="Bezig met laden ..."
							type="application/x-oleobject">
							<param name="FileName" value="<mm:attachment />">
							<param name="ShowStatusBar" value="1">
							<param name="AnimationAtStart" value="0">
						<embed type="application/x-mplayer2" 
							pluginspage="http://www.microsoft.com/Windows/Downloads/Contents/Products/MediaPlayer/"
							src="<mm:attachment />"
							name="MediaPlayer"
							showstatusbar="1"
							animationatstart="0">
						</embed>
						</object><%
					} else if(attachments_filename.indexOf(".swf")>-1){
						%><object id="FlashPlayer" 
							classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
							codebase="http://active.macromedia.com/flash2/cabs/swflash.cab#version=4,0,0,0"
							width=100% height=426>
							<param name=movie value="<mm:attachment />">
							<param name=quality value=high>
							<embed src="<mm:attachment />"
								quality=high
								width=100%
								height=426
								type="application/x-shockwave-flash"
								pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash">
							</embed>
						</object><%
					} else {
						%><iframe frameBorder='0' scrolling='yes' width='100%' height='100%' src='<mm:attachment />'></iframe><%
					}
				%></mm:field
				>
				
			</mm:node>
    <p>
	<br/>
	<img src="media/logo_nm.gif"><img src="media/logo_nuon.gif">
	<br/>
	<span class="colortitle">Highlight video's</span>
	<br/>
	
	<mm:related path="contentrel,attachments" fields="attachments.titel">
		<a href="<%= pHelper.createPaginaUrl(paginaID,request.getContextPath()) %>?at=<mm:field name="attachments.number" />">
		<img src="media/link_fun.gif" alt="" border="0" />
		<mm:field name="attachments.titel" />
		</a><br/>
	</mm:related>
	
	<br/>
	</p>
	
	<%
	}
	%>	
	
	</td>
   	<td style="vertical-align:top;padding-left:10px;width:175px;<jsp:include page="../includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">



<!-- link to weblog (pagina relation) -->
<p>
<mm:related path="readmore,pagina" fields="pagina.number,pagina.titel">
					<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">  
					<a href="<%= pHelper.createPaginaUrl(pagina_number,request.getContextPath()) %>?cp=<%=paginaID%>"><img src="media/arrowright_fun.gif" alt="" border="0" /></a>
					&nbsp;<b><mm:field name="pagina.titel" /></b>
					</mm:field>
</mm:related>
</p>				
<!-- -->	


         <jsp:include page="../includes/navright.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="lnr" value="<%= lnRubriekID %>" />
         </jsp:include>

         <jsp:include page="../includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
   	   </jsp:include>
   
   	   
   	   
	</td>
   </tr>
   </table>


</mm:node>

</mm:cloud>

