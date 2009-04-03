<%-- what's wrong with using mm:related? --%>
<mm:list nodes="$_node" path="agendas,eventrel,items" constraints="eventrel.stop > $startseconds AND eventrel.start < $endseconds">
   <mm:import jspvar="itemNumber"><mm:field name="items.number"/></mm:import>
   <%
      linkedlist.add( itemNumber );
      typeoflinked.put( itemNumber, typeof );
   %>
</mm:list>

