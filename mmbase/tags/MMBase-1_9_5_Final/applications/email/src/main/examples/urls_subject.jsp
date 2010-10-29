<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud><mm:import externid="usernumber" />
<mm:node number="$usernumber">update for 
<mm:field name="firstname" />
<mm:field name="lastname" /> !
</mm:node>
</mm:cloud>
