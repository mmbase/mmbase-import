<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud">
<mm:node number="studentrole" id="studentrole" />
<mm:listnodes type="people" id="student">
   <mm:createrelation source="student" destination="studentrole" role="related" />
</mm:listnodes>
</mm:cloud>

