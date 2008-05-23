<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="nl.leocms.content.ContentUtil"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
ContentUtil cUtil = new ContentUtil(cloud);
%>
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten Add User</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
    <mm:listnodes type="contentelement" jspvar="node" orderby="number" directions="down" max="10000" 
      constraints="number!='89598'">
    <%
    *** this also includes deelnemers (should not be the case!!!) ***

    boolean needsUpdate = true; 
    boolean ttExists = false;
    boolean crExists = false;
    boolean srExists = false;
    boolean hrExists = false;
    boolean usExists = false;
    String rRubriek = "natuurin_rubriek";
    
    %><mm:field name="titel"><mm:isnotempty><% ttExists = true; %></mm:isnotempty></mm:field
    ><mm:related path="pagina,rubriek"
      ><mm:field name="rubriek.number" jspvar="rr_number" vartype="String" write="false"
            ><% rRubriek = rr_number; 
         %></mm:field
     ></mm:related
     ><mm:related path="creatierubriek,rubriek"><% crExists = true; %></mm:related
     ><mm:related path="hoofdrubriek,rubriek"><% hrExists = true; %></mm:related
     ><mm:related path="subsite,rubriek"><% srExists = true; %></mm:related
     ><mm:related path="schrijver,users"><% usExists = true; %></mm:related><% 
     needsUpdate = !( ttExists && crExists && srExists && usExists ); 
     if(needsUpdate) { 
         %><mm:field name="soort"><mm:compare value="child"><% needsUpdate = false; %></mm:compare></mm:field><% 
         if(needsUpdate) { 
            if(!crExists) { cUtil.addCreatieRubriek(node,rRubriek); }
            if(!hrExists) { cUtil.addHoofdRubriek(node,rRubriek); }
            if(!srExists) { cUtil.addSubsite(node,rRubriek); }
            if(!usExists) { cUtil.addSchrijver(node,"admin"); }
            node.commit(); // ** to update titel, something sophisticated is needed in case titel is empty **
         } 
      } 
      %></mm:listnodes>
   </body>
</html>
</mm:cloud>