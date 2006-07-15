<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<body>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">


		<!--  create the email node -->
                <mm:createnode id="mail1" type="email">
                        <mm:setfield name="from">daniel@submarine.nl</mm:setfield>
                        <mm:setfield name="subject">test mmbase email</mm:setfield>
                        <mm:setfield name="body">Tralala</mm:setfield>
                </mm:createnode>

		<mm:node id="group1" number="groups.testgroep" />

		<mm:createrelation source="mail1" destination="group1" role="related" />


		<!-- start the mailer but return directly (background mailing) -->	
		<mm:node referid="mail1">
			<mm:field name="startmail(oneshot)" />
		</mm:node>


</mm:cloud>
</body>
</html>
