<%
int TITLE = 1;
int DATE = 2;
int QUOTE = 3;

int menuType = TITLE;
int articlePerPage = 10;
String artikelConstraint = "";
String artikelOrderby = "contentrel.pos";
String artikelDirections = "UP";
%>
<mm:relatednodes type="menutemplate" path="related,menutemplate">
   <mm:field name="url" jspvar="url" vartype="String" write="false">
      <% 
      if(url.indexOf("quote")>-1) { 
         menuType = QUOTE;
         articlePerPage = 5;
         artikelConstraint = "artikel.begindatum < '" + nowSec + "'"; 
         artikelOrderby = "artikel.begindatum";
         artikelDirections = "DOWN";
      } 
      if(url.indexOf("date")>-1) {
         menuType = DATE;
         articlePerPage = 7;
         artikelConstraint = "artikel.begindatum < '" + nowSec + "'"; 
         artikelOrderby = "artikel.begindatum";
         artikelDirections = "DOWN";
      }
      %>
   </mm:field>
</mm:relatednodes>
