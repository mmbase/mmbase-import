<%
String teaserConstraint = "teaser.embargo < " + nowSec + " AND teaser.verloopdatum > " + nowSec ;
String teaser_number = "";

// ** try to find the teaser with valid publication and expiration date 
%><mm:list nodes="<%= paginaID %>" path="pagina,rolerel,teaser" fields="teaser.number"
	orderby="rolerel.pos" directions="DOWN" max="1"
	><mm:field name="teaser.number" jspvar="dummy" vartype="String" write="false"
		><% teaser_number = dummy; 
	%></mm:field
></mm:list><%

if(teaser_number.equals("")) { 

   // ** no teaser with valid publication and expiration date, just take one
   %><mm:list nodes="<%= paginaID %>" path="pagina,rolerel,teaser" fields="teaser.number" 
   	orderby="rolerel.pos" directions="DOWN" max="1"
   	><mm:field name="teaser.number" jspvar="dummy" vartype="String" write="false"
   		><% teaser_number = dummy; 
   	%></mm:field
   ></mm:list><% 
} 
String url="";

%><mm:node number="<%= teaser_number %>" notfound="skipbody"
  ><mm:import id="extraload">javascript:populate()</mm:import
></mm:node>