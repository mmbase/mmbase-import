<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<body>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">


		<!--  create the email node -->
                <mm:createnode id="mail1" type="email">
                        <mm:setfield name="from">daniel@submarine.nl</mm:setfield>
                        <mm:setfield name="subject">http://localhost:9100/email/example5_subject.jsp</mm:setfield>
                        <mm:setfield name="body">http://localhost:9100/email/example5_body.jsp</mm:setfield>
                </mm:createnode>

		<mm:node id="group1" number="groups.testgroep" />

		<mm:createrelation source="mail1" destination="group1" role="related" />

                <!-- start the mailer but return directly (background mailing) -->
                <mm:node referid="mail1">
                  <mm:functioncontainer>
                     <mm:param name="type" value="oneshot" />
                     <mm:function name="startmail" />
                  </mm:functioncontainer>
                </mm:node>
</mm:cloud>
</body>
</html>
