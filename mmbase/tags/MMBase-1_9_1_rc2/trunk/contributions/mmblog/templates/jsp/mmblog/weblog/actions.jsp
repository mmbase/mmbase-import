<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="basic user">
<mm:import externid="action" />
<mm:compare value="updateentry" referid="action">
	<mm:import externid="weblogid" jspvar="weblogid" />
	<mm:import externid="weblogrelid" />
	<mm:import externid="weblogentryid" />
	<mm:import externid="newstate" />
	<mm:import externid="newtitle" />
	<mm:import externid="newbody" />
	<mm:node referid="weblogentryid">
	  <mm:setfield name="title"><mm:write referid="newtitle" /></mm:setfield>
	  <mm:setfield name="body"><mm:write referid="newbody" /></mm:setfield>
	</mm:node>
	<mm:node referid="weblogrelid">
	  <mm:setfield name="state"><mm:write referid="newstate" /></mm:setfield>
	</mm:node>
</mm:compare>


<mm:compare value="createentry" referid="action">
	<mm:import externid="weblogid" jspvar="weblogid" />
	<mm:import externid="newstate" />
	<mm:import externid="newtitle" />
	<mm:import externid="newbody" />

          <!--- create a new entry  -->
	  <mm:createnode id="newweblogentrynode" type="weblogentries">
	  <mm:setfield name="title"><mm:write referid="newtitle" /></mm:setfield>
	  <mm:setfield name="body"><mm:write referid="newbody" /></mm:setfield>
	  </mm:createnode>

          <!--- create a relation between the new weblogentry and the weblog -->
	  <mm:node id="weblognode" referid="weblogid" />
	  <mm:createrelation id="newweblogrelnode" source="weblognode" destination="newweblogentrynode" role="weblogrel">
		<mm:setfield name="state"><mm:write referid="newstate" /></mm:setfield>
          </mm:createrelation>

	  <!-- we have a new node so jump to that one -->
	  <mm:import id="tnn" jspvar="tnn"><mm:node referid="newweblogentrynode"><mm:field name="number" /></mm:node></mm:import>
	  <mm:import id="tnr" jspvar="tnr"><mm:node referid="newweblogrelnode"><mm:field name="number" /></mm:node></mm:import>
	  <%response.sendRedirect("index.jsp?main=weblog&sub=entry&weblogid="+weblogid+"&weblogrelid="+tnr+"&weblogentryid="+tnn);%>

</mm:compare>

<mm:compare value="deleteentry" referid="action">
	<mm:import externid="weblogentryid" />
	<mm:import externid="weblogid" />
	<mm:node referid="weblogentryid">
	<mm:deletenode deleterelations="true" />
	 ** deleted entry <mm:field name="title" /> **
	</mm:node>
</mm:compare>
</mm:cloud>
