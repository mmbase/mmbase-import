<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />
<mm:import externid="version" />
<mm:import externid="provider" />

<mm:compare value="install" referid="action">
	<mm:functioncontainer>
	 <mm:param name="id" value="$id" />
  	 <mm:param name="version" value="$version" />
	 <mm:param name="provider" value="$provider" />
	 <mm:booleanfunction set="mmpm" name="installPackage">
	</mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>


<mm:compare value="installbundle" referid="action">
	<mm:functioncontainer>
	 <mm:param name="id" value="$id" />
  	 <mm:param name="version" value="$version" />
	 <mm:param name="provider" value="$provider" />
	 <mm:booleanfunction set="mmpm" name="installBundle">
	 </mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>

<mm:compare value="uninstall" referid="action">
	<mm:functioncontainer>
	 <mm:param name="id" value="$id" />
  	 <mm:param name="version" value="$version" />
	 <mm:param name="provider" value="$provider" />
	 <mm:booleanfunction set="mmpm" name="uninstallPackage">
	 </mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>


<mm:compare value="uninstallbundle" referid="action">
	<mm:functioncontainer>
	 <mm:param name="id" value="$id" />
   	 <mm:param name="version" value="$version" />
	 <mm:param name="provider" value="$provider" />
	 <mm:booleanfunction set="mmpm" name="uninstallBundle">
	 </mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>

</mm:cloud>
