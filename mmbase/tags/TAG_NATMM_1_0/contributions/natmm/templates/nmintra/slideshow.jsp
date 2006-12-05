<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<% 
String pageUrl = "slideshow.jsp?p=" + paginaID + "&u=" + shop_itemId + "&offset=" + offsetId + "&i="; 

String previousImage = "-1";
String nextImage = "-1";
String thisImage = "";
String otherImages = "";
int totalNumberOfImages = 1;
int thisImageNumber = 1;
%>
<%@include file="includes/splitimagelist.jsp" %>
<html>
<head>
<title><mm:node number="<%= subsiteID %>"><mm:field name="naam" /></mm:node
	><mm:node number="<%= paginaID %>"> -	<mm:field name="titel" /></mm:node
	><mm:node number="<%= shop_itemId %>" notfound="skipbody"> -	<mm:field name="title" /></mm:node
	><mm:node number="<%= thisImage %>"> -	<mm:field name="title" /></mm:node>
</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<meta http-equiv="imagetoolbar" content="no">
</head>
<body class="popup">
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td class="titlebar" style="padding:3px;height:79px;">
	<img style="float:right;margin-top:2px;margin-right:5px;" src="media/styles/oranje_helmgras_logo.gif" border="0">
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td class="titlebar" width="0%" style="padding-left:1px;padding-top:2px;"><a href="#" onClick="window.close()"><img src="media/kruis_wit_op_oranje.gif" border="0" alt=""></a></td>
			<td class="titlebar" style="padding-left:8px;padding-top:2px;"><a href="#" onClick="window.close()" class="white">Sluit dit venster</a></td>
		</tr>
		<tr>
			<td><img src="media/spacer.gif" width="1" height="57" border="0" alt=""></td>
			<td class="titlebar" style="vertical-align:bottom;padding-left:8px;padding-top:2px;">
			<%= (new String("Kijk dichterbij")).toUpperCase() 
			%></td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td class="middle" style="height:27px;width:100%;text-align:center;">
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
						<tr><td style="text-align:center;"><a href="<%= pageUrl %><%= previousImage %>"><img src="media/pijl_oranje_op_wit_terug.gif" border="0" alt=""><a></td></tr>
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
      			   %>
                  <a href="<mm:attachment />" target="_blank">
                     <% if(attachments_filename.indexOf(".pdf")>-1){ 
                        %><img src="media/pdf.gif" alt="<mm:field name="title" />" border="0"><%
                     } else if(attachments_filename.indexOf(".doc")>-1){ 
                        %><img src="media/word.gif" alt="<mm:field name="title" />" border="0"><%
                     } else if(attachments_filename.indexOf(".xls")>-1){
                        %><img src="media/xls.gif" alt="<mm:field name="title" />" border="0"><%
                     } else if(attachments_filename.indexOf(".ppt")>-1){ 
                        %><img src="media/ppt.gif" alt="<mm:field name="title" />" border="0"><%
                     } else { 
                        %><img src="media/txt.gif" alt="<mm:field name="title" />" border="0"><%
                     } %><br/>
                     DOWNLOAD <%= attachments_filename.toUpperCase() %>
                  </a>
                  <br/><br/><br/><br/><br/><br/><%
					}
				%></mm:field
				></td>
			</mm:listnodes>
			<td style="text-align:right;padding-top:150px;padding-right:25px;padding-left:5px;"><% 
				if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
					%><table cellspacing="0" cellpadding="0">
						<tr><td style="text-align:center;"><a href="<%= pageUrl %><%= nextImage %>"><img src="media/pijl_oranje_op_wit.gif" border="0" alt=""><a></td></tr>
						<tr><td style="padding-top:5px;"><a class="nav" href="<%= pageUrl %><%= nextImage %>">volgende<a></td></tr>
					</table><% 
				} 
			%></td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td class="titlebar" style="padding:1px;padding-left:25px;">
	<% if(!shop_itemId.equals("-1")) { 
		%><mm:node number="<%= shop_itemId %>">
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td class="titlebar"><mm:field name="subtitle" /> <span style="font-weight:normal;">|</span></td>
					<td>&nbsp;&nbsp;<strong><mm:field name="title" /></strong></td>
				</tr>
			</table>
		</mm:node><% 
	} else {
		%><img src="media/spacer.gif" alt="" border="0" width="1" height="7"><%
	} %>
	</td>
</tr>
</table>
</body>
</html>
</cache:cache>
</mm:cloud>
