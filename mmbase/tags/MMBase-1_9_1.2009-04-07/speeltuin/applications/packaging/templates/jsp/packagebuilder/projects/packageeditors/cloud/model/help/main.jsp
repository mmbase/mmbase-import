<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
<tr>
		<td colspan="8" align="left">
		<mm:write referid="editor">
		  <mm:import externid="mode" />
		  <mm:compare value="neededbuilders">	
		 	<a href="<mm:url page="../../../../index.jsp" referids="main,sub,id,package,mode,name" />"><img src="<mm:write referid="image_arrowleft" />" border="0"/></a> <mm:write referid="name" />
		  </mm:compare>
		  <mm:compare value="builderedit">	
		  	<mm:import externid="modelfilename" id="m" />
		  	<mm:import externid="builder" id="b" />
		 	<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,mode,name,m@modelfilename,b@builder" />"><img src="<mm:write referid="image_arrowleft" />" border="0"/></a> <mm:write referid="name" />
		  </mm:compare>
		  <mm:compare value="builderfieldedit">	
		  	<mm:import externid="modelfilename" id="m" />
		  	<mm:import externid="builder" id="b" />
		  	<mm:import id="e">builderedit</mm:import>
		 	<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,mode,name,m@modelfilename,b@builder,e@editor" />"><img src="<mm:write referid="image_arrowleft" />" border="0"/></a> <mm:write referid="name" />
		  </mm:compare>
		</mm:write>
		</B></FONT>
		</TD>
</tr>
</table>
