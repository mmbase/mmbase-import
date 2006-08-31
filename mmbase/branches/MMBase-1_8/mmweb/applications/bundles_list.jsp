<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="id" />
<TR>
		<td>
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="bundle" /><mm:param name="id" value="$id" /></mm:url>"><mm:write referid="id" /></a>
		</td>
		<td>
	            <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/maintainer.txt</mm:import>
		    <mm:include page="$file" />
		</td>
		<td>
	            <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/type.txt</mm:import>
		    <mm:include page="$file" />
		</td>
		<td>
	            <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/version.txt</mm:import>
		    <mm:include page="$file" />
		</td>
		<td width="15">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="bundle" /><mm:param name="id" value="$id" /></mm:url>"><IMG SRC="images/arrow-right.gif" BORDER="0" ALIGN="left"></A>
		</td>
</tr>
