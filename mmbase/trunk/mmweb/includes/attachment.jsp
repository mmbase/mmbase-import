<mm:related path="posrel,attachments" orderby="posrel.pos">
<mm:node element="attachments">
<mm:first><%-- create table with header row --%>
   <table class="relationcontainer" <mm:present referid="twocolumns">style="width:100%;"</mm:present>>
     <tr>
       <th <mm:present referid="twocolumns">colspan="2"</mm:present>>Attachments</th>
     </tr>
</mm:first>
<mm:present referid="twocolumns"><mm:odd><tr><mm:import id="oddattachment" /></mm:odd></mm:present>
<mm:notpresent referid="twocolumns"><tr></mm:notpresent>
	<td <mm:present referid="twocolumns">style="width:50%;"</mm:present>>
    <p>
	<a href="<mm:attachment />"><mm:field name="filename" jspvar="fn" vartype="String" write="false"><%
	  if (fn.indexOf(".pdf") > -1) {
		  %><img src="/media/pdf.gif" alt="pdf" /><%
	  } else if (fn.indexOf(".bmp") > -1) { 
		  %><img src="/media/bmp.gif" alt="bmp" /><%
	  } else if (fn.indexOf(".png") > -1) { 
		  %><img src="/media/png.gif" alt="png" /><%
	  } else if (fn.indexOf(".gif") > -1) { 
		  %><img src="/media/gif.gif" alt="gif" /><%
	  } else if (fn.indexOf(".jpg") > -1 || fn.indexOf(".jpeg") > -1) { 
		  %><img src="/media/jpg.gif" alt="jpg" /><%
	  } else if (fn.indexOf(".ppt") > -1) {
		  %><img src="/media/ppt.gif" alt="ppt" /><%
	  } else if (fn.indexOf(".xls") > -1) { 
		  %><img src="/media/xls.gif" alt="xls" /><%
	  } else if (fn.indexOf(".doc") > -1) { 
		  %><img src="/media/word.gif" alt="word doc" /><%
	  } else if (fn.indexOf(".zip") > -1) { 
		  %><img src="/media/zip.gif" alt="zip" /><%
	  } else {
		  %><img src="/media/file.gif" alt="file" /><%
	  } 
	%></mm:field> <mm:field name="title" /></a>
	<mm:field name="description"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field>
    </p>
	</td>
<mm:present referid="twocolumns"><mm:even></tr><mm:remove referid="oddattachment" /></mm:even></mm:present>
<mm:notpresent referid="twocolumns"></tr></mm:notpresent>
<mm:last>
	<mm:present referid="oddattachment">
		<td>&nbsp;</td></tr>
	</mm:present>
	</table>
</mm:last>
</mm:node>
</mm:related>
