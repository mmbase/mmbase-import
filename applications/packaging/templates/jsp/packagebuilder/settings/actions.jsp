<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />
<mm:import externid="version" />
<mm:import externid="provider" />

<mm:compare value="changesettings" referid="action">
	<mm:function set="mmpm" name="changeSettings">
		<mm:import externid="providername" />
		<mm:import externid="callbackurl" />
  		<mm:setparam name="providername" value="$providername" />
  		<mm:setparam name="callbackurl" value="$callbackurl" />
		<mm:resultnode />
	</mm:function>
</mm:compare>

</mm:cloud>
