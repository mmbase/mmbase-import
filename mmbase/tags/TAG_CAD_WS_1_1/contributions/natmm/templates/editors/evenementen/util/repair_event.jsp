<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<% 
String title = request.getParameter("title");
if(title==null) { title =  "Wandel/vaartocht Ankeveense plassen"; } 
%>     
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten Activiteiten Database</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
      <mm:listnodes type="evenement" constraints="<%= "titel LIKE '%" + title + "%'" %>" orderby="titel" directions="DOWN">
         <mm:first><table>
         <tr>
            <td>title</td>
            <td>number</td>
            <td>soort</td>
            <td>parent</td>
         </tr>         
         </mm:first>
         <tr>
            <td><mm:field name="titel" /></td>
            <td><mm:field name="number" /></td>
            <td><mm:field name="soort" /></td>
            <td>
               <mm:related path="partrel,evenement" constraints="evenement.soort='parent'">
                  <mm:field name="evenement.number" />
               </mm:related>
            </td>
         </tr>
         <mm:last></table></mm:last>
      </mm:listnodes>
   </body>
</html>
</mm:cloud>