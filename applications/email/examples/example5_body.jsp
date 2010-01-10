<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <mm:import externid="usernumber" />
  <mm:node number="$usernumber">
Hi <mm:field name="firstname" /> <mm:field name="lastname" /> !

Nice to see you again, These are your settings :

firstname : <mm:field name="firstname" />
lastname : <mm:field name="lastname" />
email : <mm:field name="email" />
account : <mm:field name="account" />
password : <mm:field name="password" />

Bye,
MMBase Email Team.
</mm:node>
</mm:cloud>
