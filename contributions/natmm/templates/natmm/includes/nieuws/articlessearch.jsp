<% // *** support both pagina,contentrel,artikel and dossier,posrel,artikel 
articles = new TreeMap();
String articleConstraint = (new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
String articlePath = "contentrel,artikel";
String orderby = "artikel.begindatum";
int directions = 1; // down
%>
<mm:nodeinfo type="type">
   <mm:compare value="dossier">
      <% articlePath = "posrel,artikel"; %>
      <mm:notpresent referid="showdate">
        <%
        orderby = "posrel.pos";
        directions = -1; // up
        %>
      </mm:notpresent>
   </mm:compare>
</mm:nodeinfo>
<mm:related path="<%= articlePath %>" fields="artikel.number" constraints="<%= articleConstraint %>" searchdir="destination">
   <mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false">
   <mm:field name="<%= orderby %>" jspvar="key" vartype="Long" write="false">
      <%
       key = new Long(directions * key.longValue());
       while(articles.containsKey(key)) {
          key = new Long(key.longValue() + 1);
       }
       articles.put(key,article_number); 
      %>
   </mm:field>
   </mm:field>
</mm:related>