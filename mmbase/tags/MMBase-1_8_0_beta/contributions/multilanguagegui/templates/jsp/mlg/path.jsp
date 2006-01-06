<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="type" />
<mm:compare value="index" referid="type">
	<tr><td align="left" colspan="3">
	<a href="<mm:url page="index.jsp" />">home</a>
	</td></tr>
</mm:compare>

<mm:compare value="set" referid="type">
	<tr><td align="left" colspan="3">
	<mm:import externid="setname" />
	<a href="<mm:url page="index.jsp">
		</mm:url>">
	home</a> &gt;
	<a href="<mm:url page="set.jsp">
		<mm:param name="setname" value="$setname" />
		</mm:url>">
	<mm:write referid="setname" /></a> 
	</td></tr>
</mm:compare>


<mm:compare value="keyword" referid="type">
	<tr><td align="left" colspan="4">
	<mm:import externid="setname" />
	<mm:import externid="keyword" />
	<a href="<mm:url page="index.jsp">
		</mm:url>">
	home</a> &gt;
	<a href="<mm:url page="set.jsp">
		<mm:param name="setname" value="$setname" />
		</mm:url>">
	<mm:write referid="setname" /></a> &gt;
	<a href="<mm:url page="keyword.jsp">
		<mm:param name="setname" value="$setname" />
		<mm:param name="keyword" value="$keyword" />
		</mm:url>">
	'<mm:write referid="keyword" />'</a>
	</td></tr>
</mm:compare>

</mm:cloud>
