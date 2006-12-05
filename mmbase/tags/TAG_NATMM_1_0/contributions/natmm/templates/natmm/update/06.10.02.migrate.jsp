<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
   Remove some default relations which have been incorrectly assigned after migration.<br/>
   <mm:node number="natuurin_rubriek">
      <mm:related path="hoofdrubriek,contentelement" orderby="contentelement.number" constraints="contentelement.number<'130703'">
        <mm:node element="contentelement">
          <mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
           if(!nType.equals("evenement")) { 
              %>
              <mm:field name="number" />
              <mm:field name="creatiedatum" jspvar="creatiedatum" vartype="String" write="false"> -<mm:time time="<%=creatiedatum%>" format="dd-MM-yyyy"/> </mm:field>
              -<%= nType %> <mm:field name="titel" /><br/>
              <mm:related path="hoofdrubriek,rubriek"><mm:deletenode element="hoofdrubriek" /></mm:related>
              <mm:related path="creatierubriek,rubriek"><mm:deletenode element="creatierubriek" /></mm:related>
              <% 
           }
          %></mm:nodeinfo>
        </mm:node>
      </mm:related>
   </mm:node>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
