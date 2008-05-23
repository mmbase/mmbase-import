<%
// ** check whether documents root exists
%>
<mm:node number="documents_root" notfound="skipbody">
   <mm:import id="root_document_exists" />
</mm:node>
<mm:notpresent referid="root_document_exists">
   <% (new DirReader()).run(); %>
</mm:notpresent>
