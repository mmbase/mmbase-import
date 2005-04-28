<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!-- mm:cloud jspvar="cloud" -->
<mm:listnodes type="people" constraints="username!='admin'">
   <mm:field name="lastname"/><br/>
   <mm:deletenode deleterelations="true" />
</mm:listnodes>
</mm:cloud>

