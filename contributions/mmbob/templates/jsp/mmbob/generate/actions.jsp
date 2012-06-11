<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="administrator">
<mm:import externid="action" />
<mm:import externid="forumid" />
<mm:import externid="folderaction" />

<mm:compare value="generateposters" referid="action">
	<mm:import externid="generatecount" />
	<mm:import externid="generatedelay" />
	<mm:import externid="inforum" />
	<mm:booleanfunction set="mmbob" name="generatePosters" referids="generatecount,generatedelay,inforum">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="generateareas" referid="action">
	<mm:import externid="generatecount" />
	<mm:import externid="generatedelay" />
	<mm:import externid="inforum" />
	<mm:booleanfunction set="mmbob" name="generateAreas" referids="generatecount,generatedelay,inforum">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="generatethreads" referid="action">
	<mm:import externid="generatecount" />
	<mm:import externid="generatedelay" />
	<mm:import externid="inforum" />
	<mm:import externid="inpostarea" />
	<mm:booleanfunction set="mmbob" name="generateThreads" referids="generatecount,generatedelay,inforum,inpostarea">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="generatereplys" referid="action">
	<mm:import externid="generatecount" />
	<mm:import externid="generatedelay" />
	<mm:import externid="inforum" />
	<mm:import externid="inpostarea" />
	<mm:booleanfunction set="mmbob" name="generateReplys" referids="generatecount,generatedelay,inforum,inpostarea">
	</mm:booleanfunction>
</mm:compare>

</mm:cloud>
