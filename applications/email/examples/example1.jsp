<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<body>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">



		<!--  create the email node -->
                <mm:createnode id="mail1" type="email">
                        <mm:setfield name="from">daniel@mmbase.org</mm:setfield>
                        <mm:setfield name="to">daniel@mmbase.org</mm:setfield>
                        <mm:setfield name="subject">my first mmbase mail !!</mm:setfield>
                        <mm:setfield name="body">Just testing email</mm:setfield>
                </mm:createnode>


		<!-- send the email node  (default type is oneshot) -->	
		<mm:node referid="mail1">
			<mm:function name="mail" />
		</mm:node>

</mm:cloud>
</body>
</html>
