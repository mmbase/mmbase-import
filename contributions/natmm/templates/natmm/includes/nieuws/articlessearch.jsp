<% // *** support both pagina,contentrel,artikel and dossier,posrel,artikel 
articles = new TreeMap();
String articleConstraint = "(artikel.embargo < '" + (nowSec+quarterOfAnHour) + "') AND (artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
String articlePath = "contentrel,artikel";
%>
<mm:nodeinfo type="type">
   <mm:compare value="dossier">
      <% articlePath = "posrel,artikel"; %>
   </mm:compare>
</mm:nodeinfo>
<mm:related path="<%= articlePath %>" fields="artikel.number" max="30" orderby="artikel.begindatum" directions="down"
   constraints="<%= articleConstraint %>">
   <mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false">
   <mm:field name="artikel.begindatum" jspvar="article_begindatum" vartype="Long" write="false">
      <%
         while(articles.containsKey(article_begindatum)) {
            article_begindatum = new Long(article_begindatum.longValue() + 1);
         }
         articles.put(article_begindatum,article_number); 
      %>
   </mm:field>
   </mm:field>
</mm:related>
