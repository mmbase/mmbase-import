<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../top1_params.jsp" %>
<%@include file="../top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" >
<!-- <%= new java.util.Date() %> -->
<%
String imageId = request.getParameter("i");
String previousImage = "-1";
String nextImage = "-1";
String thisImage = "";
String otherImages = "";
int totalNumberOfImages = 1;
int thisImageNumber = 1;
%><%@include file="../splitimagelist.jsp" %><% 
String pageUrl = "slideshow.jsp?w=" + subsiteID + "&p=" + paginaID + "&u=" + shop_itemID + "&o=" + offsetID+ "&i=";

%><html>
<head>
<title><mm:node number="<%= subsiteID %>"><mm:field name="naam" /></mm:node
	><mm:node number="<%= paginaID %>"> -	<mm:field name="titel" /></mm:node
	><mm:node number="<%= shop_itemID %>" notfound="skipbody"> -	<mm:field name="titel" /></mm:node
	><mm:node number="<%= thisImage %>"> -	<mm:field name="titel" /></mm:node>
</title>
<link rel="stylesheet" type="text/css" href="../../hoofdsite/themas/main.css"  title="default" />
<% if(styleSheet!=null) { %><link rel="stylesheet" type="text/css" href="../../<%= styleSheet %>" /><% } %>
<meta http-equiv="imagetoolbar" content="no">
</head>
<body style="padding:0px;margin:0px;overflow:no;text-align:center;">
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td class="maincolor" style="padding:3px;height:79px;">
	<img style="float:right;margin-top:2px;margin-right:5px;" src="../../media/logos/naardermeer_logo.gif" border="0">
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td class="maincolor" width="0%" style="padding-left:1px;padding-top:2px;" >
        <a href="#" onClick="window.close()"><img src="../../media/shop/kruis_wit_op_oranje.gif" border="0" alt=""></a></td>
			<td class="maincolor" style="padding-left:8px;padding-top:2px;">
        <a href="#" onClick="window.close()" class="klikpad"><b><bean:message bundle="LEOCMS" key="shop.slideshow.close" /></b></a></td>
		</tr>
		<tr>
			<td><img src="../../media/trans.gif" width="1" height="57" border="0" alt=""></td>
			<td class="maincolor" style="vertical-align:bottom;padding-left:8px;padding-top:2px;">
			  <bean:message bundle="LEOCMS" key="items.lookcloser" />
      </td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td style="height:27px;width:100%;text-align:center;vertical-align:middle;">
		<strong><mm:node number="<%= thisImage %>"><mm:field name="title" /></mm:node></strong>
		&nbsp[ <%= thisImageNumber %> / <%= totalNumberOfImages  %> ]
	</td>
</tr>
<tr>
	<td>
		<table width="100%" height="426" cellpadding="0" cellspacing="0">
		<tr>
			<td style="padding-top:150px;padding-left:25px;padding-right:5px;"><% 
				if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
					%><table cellspacing="0" cellpadding="0">
						<tr><td style="text-align:center;"><a href="<%= pageUrl %><%= previousImage %>"><img src="../../media/shop/pijl_oranje_op_wit_terug.gif" border="0" alt=""><a></td></tr>
						<tr><td style="padding-top:5px;"><a class="nav" href="<%= pageUrl %><%= previousImage %>">vorige<a></td></tr>
					</table><% 
				} 
			%></td>
			<mm:listnodes type="images" constraints="<%= "number='" + thisImage + "'" %>"
				><td style="text-align:center;vertical-align:middle;width:100%;">
					<a href="#" onClick="window.close()" title="Klik op de foto om het venster te sluiten">
					<img src="<mm:image template="s(400x423)" />" border="0"></a>
				</td>
			</mm:listnodes
			><mm:listnodes type="attachments" constraints="<%= "number='" + thisImage + "'" %>"
				><td style="text-align:center;vertical-align:middle;width:100%;">
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
				></td>
			</mm:listnodes>
			<td style="text-align:right;padding-top:150px;padding-right:25px;padding-left:5px;"><% 
				if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
					%><table cellspacing="0" cellpadding="0">
						<tr><td style="text-align:center;"><a href="<%= pageUrl %><%= nextImage %>"><img src="../../media/shop/pijl_oranje_op_wit.gif" border="0" alt=""><a></td></tr>
						<tr><td style="padding-top:5px;"><a class="nav" href="<%= pageUrl %><%= nextImage %>">volgende<a></td></tr>
					</table><% 
				} 
			%></td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td class="maincolor" style="padding:1px;padding-left:25px;">
	<%
  if(!shop_itemID.equals("-1")) { 
		%><mm:node number="<%= shop_itemID %>" jspvar="thisArticle">
         <%
         String summary = thisArticle.getStringValue("intro"); 
         summary = HtmlCleaner.cleanText(summary,"<",">");
         int spacePos = summary.indexOf(" ",80); 
         if(spacePos>-1) { 
            summary =summary.substring(0,spacePos) + " &hellip;";
         } 
         %>
			<table cellspacing="0" cellpadding="0" style="width:100%;">
				<tr>
					<td class="maincolor"><%= summary %></td>
					<td style="text-align:right"><span class="maincolor">|</span>&nbsp;&nbsp;<strong><mm:field name="titel" /></strong></td>
				</tr>
			</table>
		</mm:node><% 
	} else {
		%><img src="../../media/trans.gif" alt="" border="0" width="1" height="7"><%
	} %>
	</td>
</tr>
</table>
</body>
</html>
</cache:cache>
</mm:cloud>