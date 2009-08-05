<mm:related path="posrel,urls" 
  fields="posrel.pos,urls.url,urls.name,urls.description"
  orderby="posrel.pos" directions="UP">
  <mm:first>
  <table class="relationcontainer">
  <tr><th>Urls</th></tr>
  <tr><td>
  </mm:first>
    <p><a href="<mm:field name="urls.url" />"><mm:field name="urls.name"
    ><mm:isnotempty><mm:write /></mm:isnotempty></mm:field
	><mm:field name="urls.name"><mm:isempty><mm:field name="urls.url" jspvar="urls_url" vartype="String" write="false" ><% 
	   if (urls_url.length()>50) { 
		 urls_url =urls_url.substring(0,50);
	   }
	%><%= urls_url %></mm:field></mm:isempty></mm:field></a>
    <mm:field name="urls.description"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field></p>
  <mm:last>
  </td>
  </tr>
  </table>
  </mm:last>
</mm:related>
