<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />

<mm:compare value="addlanguage" referid="action">
	<mm:function set="mlg" name="addLanguage">
		<mm:import externid="setname" />
		<mm:import externid="keyword" />
		<mm:import externid="language" />
		<mm:import externid="value" />
		<mm:setparam name="setname" value="$setname" />
		<mm:setparam name="keyword" value="$keyword" />
		<mm:setparam name="language" value="$language" />
		<mm:setparam name="value" value="$value" />
		<mm:resultnode />
	</mm:function>
</mm:compare>



<mm:compare value="removelanguage" referid="action">
	<mm:function set="mlg" name="removeLanguage">
		<mm:import externid="setname" />
		<mm:import externid="keyword" />
		<mm:import externid="language" />
		<mm:setparam name="setname" value="$setname" />
		<mm:setparam name="keyword" value="$keyword" />
		<mm:setparam name="language" value="$language" />
		<mm:resultnode />
	</mm:function>
</mm:compare>

<mm:compare value="removekeyword" referid="action">
	<mm:function set="mlg" name="removeKeyword">
		<mm:import externid="setname" />
		<mm:import externid="keyword" />
		<mm:setparam name="setname" value="$setname" />
		<mm:setparam name="keyword" value="$keyword" />
		<mm:resultnode />
	</mm:function>
</mm:compare>

<mm:compare value="changelanguage" referid="action">
	<mm:function set="mlg" name="changeLanguage">
		<mm:import externid="setname" />
		<mm:import externid="keyword" />
		<mm:import externid="language" />
		<mm:import externid="value" />
		<mm:setparam name="setname" value="$setname" />
		<mm:setparam name="keyword" value="$keyword" />
		<mm:setparam name="language" value="$language" />
		<mm:setparam name="value" value="$value" />
		<mm:resultnode />
	</mm:function>
</mm:compare>


<mm:compare value="addkeyword" referid="action">
	<mm:function set="mlg" name="addKeyword">
		<mm:import externid="setname" />
		<mm:import externid="keyword" />
		<mm:setparam name="setname" value="$setname" />
		<mm:setparam name="keyword" value="$keyword" />
		<mm:resultnode />
	</mm:function>
</mm:compare>

</mm:cloud>
