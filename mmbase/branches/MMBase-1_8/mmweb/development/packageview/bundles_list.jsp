<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="id" />
<mm:import externid="page" />
<mm:import externid="portal" />
<TR>
		<td>
			<a href="<mm:url page="index.jsp" referids="page,portal">
			           <mm:param name="main" value="bundle" />
			           <mm:param name="id" value="$id" />
			         </mm:url>"><mm:write referid="id" /></a>
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
			<A HREF="<mm:url page="index.jsp" referids="page,portal">
			           <mm:param name="main" value="bundle" />
			           <mm:param name="id" value="$id" />
			         </mm:url>"><img src="/media/arrow-right.gif" border="0" /></A>
		</td>
</tr>
