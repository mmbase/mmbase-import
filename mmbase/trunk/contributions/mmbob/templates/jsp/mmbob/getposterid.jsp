<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import id="entree" reset="true"><%= request.getHeader("aad_nummer") %></mm:import>
<mm:import id="entree" reset="true">null</mm:import>

<mm:import id="host" reset="true"><%= request.getRemoteAddr() %></mm:import>
<mm:compare referid="entree" value="null">
  <mm:import id="posterid" externid="pid$forumid" from="session">-1</mm:import>
  <mm:compare referid="posterid" value="-1">
	<mm:import id="password" externid="cwf$forumid" from="cookie" />
	<mm:import id="account" externid="caf$forumid" from="cookie" />
	<mm:compare referid="account" value="" inverse="true">
		<mm:nodefunction set="mmbob" name="forumLogin" referids="forumid,account,password">
			<mm:remove referid="posterid" />
			<mm:remove referid="loginfailed" />
			<mm:field name="state">
			  <mm:compare value="failed">
				<mm:import id="loginfailed">true</mm:import>
               			<mm:import id="posterid">-1</mm:import>
				<mm:write referid="posterid" session="pid$forumid" /> 
                                <mm:import id="loginfailedreason"><mm:field name="reason"/></mm:import>
			  </mm:compare>
			  <mm:compare value="passed">
				<mm:import id="loginfailed">false</mm:import>
               			<mm:import id="posterid"><mm:field name="posterid"/></mm:import>
				<mm:write referid="posterid" session="pid$forumid" /> 
				<mm:booleanfunction set="mmbob" name="setRemoteAddress" referids="forumid,posterid,host" />
			  </mm:compare>
                         </mm:field>
		</mm:nodefunction>
	</mm:compare>
  </mm:compare>
</mm:compare>

<mm:compare referid="entree" value="null" inverse="true">
		<mm:import id="account"><%= request.getHeader("sm_user") %></mm:import>
		<mm:import id="password"><%= request.getHeader("aad_nummer") %></mm:import>
		<mm:nodefunction set="mmbob" name="forumLogin" referids="forumid,account,password">
			<mm:remove referid="posterid" />
			<mm:remove referid="loginfailed" />
			<mm:field name="state">
			  <mm:compare value="failed">
				<mm:import id="loginfailed">true</mm:import>
               			<mm:import id="posterid">-1</mm:import>
				<mm:write referid="posterid" session="pid$forumid" /> 
                                <mm:import id="loginfailedreason"><mm:field name="reason"/></mm:import>
			  </mm:compare>
			  <mm:compare value="passed">
				<mm:import id="loginfailed">false</mm:import>
               			<mm:import id="posterid"><mm:field name="posterid"/></mm:import>
				<mm:booleanfunction set="mmbob" name="setRemoteAddress" referids="forumid,posterid,host" />
			  </mm:compare>
                         </mm:field>
		</mm:nodefunction>
</mm:compare>

<mm:compare referid="posterid" value="">
	<mm:remove referid="posterid" />
	<mm:import id="posterid">-1</mm:import>
	<mm:write referid="posterid" session="pid$forumid" />
</mm:compare>


<mm:import externid="lang" />
<mm:present referid="forumid">
<mm:node number="$forumid">
	<mm:present referid="lang" inverse="true">
        <mm:remove referid="lang" />
        <mm:import id="lang"><mm:field name="language" /></mm:import>
	</mm:present>
</mm:node>
</mm:present>
