<mm:related path="posrel,persons"
  fields="posrel.pos,persons.number,persons.firstname,persons.lastname"
  orderby="posrel.pos" directions="UP">
  <mm:first>
  <table class="relationcontainer">
  <tr><th>Persons</th></tr>
  <tr><td>
  </mm:first>
     <p><a target="_blank" href="../people/index.jsp?person=<mm:field name="persons.number"/>"><mm:field name="persons.firstname" /> <mm:field name="persons.lastname" /></a>
     <br /></p>
  <mm:last>
	 </td>
  </tr>
  </table>
  </mm:last>
</mm:related>
