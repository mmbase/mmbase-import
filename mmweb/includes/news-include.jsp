<%-- news with related persons (posrel), mmevents (related), images (posrel), urls (posrel) 
	attachments (posrel) --%>
<mm:present referid="newsnr">
	<mm:node referid="newsnr" notfound="skipbody">
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr>
	  <td width="540" valign="top">	
	  <div class="newstitle"><mm:field name="title" /></div>
	  <mm:field name="subtitle"><mm:isnotempty><div class="newssubtitle"><mm:write /></div></mm:isnotempty></mm:field>
	  <div class="newssubtitle">
		<mm:related path="mmevents" 
			fields="mmevents.start"
			max="1"><span class="newssubL"><mm:field name="mmevents.start"><mm:time format="dd MMMM yyyy hh:mm" /></mm:field></span>
		</mm:related>
		<mm:related path="posrel,persons" 
			fields="persons.firstname,persons.suffix,persons.lastname"
			max="1"><span class="newssubR"><mm:first>By:</mm:first> <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" /><mm:last inverse="true">,</mm:last></span>
		</mm:related>
	  </div>
	  </td>
	  <td>&nbsp;</td>
	</tr><tr>
	  <td width="540" valign="top">	
	  <mm:field name="html(intro)" escape="none"><mm:isnotempty><div class="newsintro"><mm:write /></div></mm:isnotempty></mm:field>
	  <mm:remove referid="news"/><mm:import id="news"/><%@ include file="/includes/urls.jsp" %>
	  <%@ include file="/includes/attachment.jsp" %>
	  <div class="newsbody"><mm:field name="html(body)" escape="none" /></div>
	  </td>
	  <td align="left" valign="top">
	  <!-- images -->
	  <mm:related path="posrel,images" fields="posrel.pos" orderby="posrel.pos">
	  	  <mm:first><table border="0" cellspacing="4" cellpadding="0"></mm:first>
	  	  <tr><td align="center" valign="top">
		  <mm:node element="images">
		  <a href="#" onClick="javascript:launchCenter('<mm:url page="includes/slideshow.jsp">
			<mm:param name="image"><mm:field name="number" /></mm:param></mm:url>', 'center', 550, 740)" title="Click to enlarge image"><img src="<mm:image template="s(120)" />" alt="<mm:field name="title" />" width="120" border="0" /></a>
		  <mm:field name="title"><mm:isnotempty><br /><span class="imgtitle"><mm:write /> </span></mm:isnotempty></mm:field>
		  </mm:node>
		  </td></tr>
		  <mm:last></table></mm:last>
	  </mm:related>
	  </td>
	</tr>
	</table>
	</mm:node>
</mm:present>
<%-- /news --%>
