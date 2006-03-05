<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud jspvar='cloud' rank='administrator'>
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;background-color:#E4F0F7;">
      <h3>Emailadressen van gebruikers:</h3>
      <% boolean isFirst = true; %>
      <mm:listnodes type="users" orderby="emailadres"
         ><mm:field name="emailadres" jspvar="address" vartype="String" write="false"><%
             address.replace(';',',');
             if(address.indexOf("@")==-1) { 
               if(isFirst) { 
                  %><h5 style="color:red;">De volgende emailadressen zijn ongeldig:</h5><% 
                  isFirst = false;
               }
               %><%= address %> van account <mm:field name="account" /><br/><%
             }
         %></mm:field
      ></mm:listnodes>
      <% 
      String lastAddress = "";
      isFirst = true;
      %>
      <mm:listnodes type="users" orderby="emailadres"
         ><mm:field name="emailadres" jspvar="address" vartype="String" write="false"><%
             address.replace(';',',');
             if(address.indexOf("@")!=-1) {
               if(address.equals(lastAddress)) { 
                  if(isFirst) { 
                     %><h5 style="color:red;">De volgende emailadressen worden gebruikt door meer dan 1 gebruiker:</h5><% 
                     isFirst = false;
                  }
                  %><%= address %> van account <mm:field name="account" /><br/><%
               }
               lastAddress = address;
             }
         %></mm:field
      ></mm:listnodes>
      <mm:listnodes type="rubriek">
         <mm:field name="naam" id="rubriek_naam" write="false" />
         <%
         String addresses = ""; 
         %>
         <mm:related path="rolerel,users" orderby="users.emailadres" fields="users.emailadres" distinct="true"
            ><mm:field name="users.emailadres" jspvar="address" vartype="String" write="false"><%
             address.replace(';',',');
             if(address.indexOf("@")!=-1) {                       
               addresses += address + ", ";
             }
            %></mm:field
         ></mm:related>
         <%
         if(!addresses.equals("")) {
            %>
            <h5><mm:write referid="rubriek_naam" /></h5>
            <%= addresses %>
            <% 
         } %>
      </mm:listnodes>
   </body>
</html>
</mm:cloud>