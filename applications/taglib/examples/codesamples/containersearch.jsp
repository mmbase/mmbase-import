<!-- simple example of generic search tool -->
<mm:import externid="type">news</mm:import> <!-- you can add type=.. to the url -->

<table>
  <tr><mm:fieldlist nodetype="$type" type="list"><th><mm:fieldinfo type="guitype" /></th></mm:fieldlist></tr><!-- show the field names -->
  <tr><mm:fieldlist nodetype="$type" type="list"><th><mm:fieldinfo type="search" /></th></mm:fieldlist></tr> <!-- show shearch boxes -->

  <!-- for the 'paging' url's we need a 'base' url which the current search query -->
  <mm:url id="baseurl" write="false">
   <mm:fieldlist nodetype="$type" type="list"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
  </mm:url>

  <mm:listnodescontainer type="$type">
    <!-- apply the search query -->
    
  Total number of nodes before constraint: <mm:size /><br />
  <mm:constraint field="title" operator="LIKE" value="%XML%" />
  Total number of found nodes: <mm:size /><br />
  <mm:listnodes>
    <mm:nodeinfo type="gui" /><br />
  </mm:listnodes>
</mm:listnodescontainer>
