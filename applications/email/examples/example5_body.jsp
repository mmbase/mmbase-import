<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="usernumber" />
<mm:node number="$usernumber">
Hi <mm:field name="firstname" /> <mm:field name="lastname" /> !
<p />
Nice to see you again, These are your settings :
<p>
firstname : <mm:field name="firstname" /><br/>
lastname : <mm:field name="lastname" /><br/>
email : <mm:field name="email" /><br/>
account : <mm:field name="account" /><br/>
password : <mm:field name="password" /><br/>
<p>
Bye,<br />
MMBase Email Team.
</mm:node>
</mm:cloud>
