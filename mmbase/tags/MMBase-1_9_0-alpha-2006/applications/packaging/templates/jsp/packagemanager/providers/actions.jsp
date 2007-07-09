<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />
<mm:compare value="changesettings" referid="action">
	<mm:import externid="maintainer" />
	<mm:import externid="account" />
	<mm:import externid="password" />
	<mm:import externid="path" />
	<mm:functioncontainer>
	 <mm:param name="name" value="$id" />
  	 <mm:param name="maintainer" value="$maintainer" />
  	 <mm:param name="account" value="$account" />
  	 <mm:param name="password" value="$password" />
  	 <mm:param name="path" value="$path" />
	 <mm:booleanfunction set="mmpm" name="changeProviderSettings">
	 </mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>

<mm:compare value="addsubscribeprovider" referid="action">
	<mm:import externid="url" />
	<mm:functioncontainer>
  	 <mm:param name="url" value="$url" />
	 <mm:nodelistfunction set="mmpm" name="addSubscribeProvider">
	  <mm:import id="feedback"><mm:field name="feedback" /></mm:import>
	</mm:nodelistfunction>
	</mm:functioncontainer>
</mm:compare>

<mm:compare value="adddiskprovider" referid="action">
	<mm:import externid="path" />
	<mm:import externid="name" />
	<mm:functioncontainer>
  	 <mm:param name="name" value="$name" />
  	 <mm:param name="path" value="$path" />
	 <mm:nodelistfunction set="mmpm" name="addDiskProvider">
		<mm:import id="feedback"><mm:field name="feedback" /></mm:import>
	 </mm:nodelistfunction>
	</mm:functioncontainer>
</mm:compare>

<mm:compare value="delprovider" referid="action">
	<mm:functioncontainer>
  	<mm:param name="id" value="$id" />
	 <mm:booleanfunction set="mmpm" name="delProvider">
	 </mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>

</mm:cloud>
<mm:present referid="feedback">
<br />
<center>feedback : <mm:write referid="feedback" /></center>
<br />
</mm:present>
