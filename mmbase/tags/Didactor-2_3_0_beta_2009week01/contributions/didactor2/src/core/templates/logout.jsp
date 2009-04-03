<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@page buffer="200kb" %>
<mm:cloud method="asis">
  <mm:node number="$user" notfound="skip">
    <mm:setfield name="islogged">0</mm:setfield>
  </mm:node>
</mm:cloud>

<mm:remove from="session" referid="educationId" />

<mm:cloud method="logout">
  <mm:cloudinfo type="user" />
</mm:cloud>
<mm:redirect page="/" />
