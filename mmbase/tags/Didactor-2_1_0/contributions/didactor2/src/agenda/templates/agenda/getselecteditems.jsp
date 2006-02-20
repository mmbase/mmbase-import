<mm:list nodes="$agenda" path="agendas,eventrel,items" constraints="eventrel.stop > $startseconds AND eventrel.start < $endseconds">
   <mm:field name="items.number" jspvar="itemNumber" vartype="String" write="false">
   <%
      linkedlist.add( itemNumber );
   %>
   </mm:field>
</mm:list>

