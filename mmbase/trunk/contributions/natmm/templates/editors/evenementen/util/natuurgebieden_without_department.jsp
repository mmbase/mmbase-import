<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="nl.leocms.util.tools.HtmlCleaner"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
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
      <h3>Natuurgebieden zonder afdeling</h3>
      <!-- to trigger natuurgebieden.commit(); not done by ew -->
      <mm:listnodes type="natuurgebieden">
            <mm:setfield name="titel_eng">,-1,</mm:setfield>
      </mm:listnodes>
    <!--
      BC Brunssummerheide   40289
      <mm:node number="40289" id="br" />
      BC Oisterwijk 40285
      <mm:node number="40285" id="oi" />
      Noord-Brabant 41288, Limburg 41289
      
      BC Veluwezoom 40281  
      <mm:node number="40281" id="ve" />
      Gelderland  41283
      
      BC 's-Graveland 40277
      <mm:node number="40277" id="gr" />
      Noord-Holland 41285, Utrecht 41284
      
      BC De Wieden 40273
      <mm:node number="40273" id="wi" />
      Overijssel 41281, Flevoland 41282
      
      BC Dwingelderveld 40269
      <mm:node number="40269" id="dw" />
      Groningen 41290, Friesland 41279, Drenthe 41280
     -->
     <mm:listnodes type="natuurgebieden" constraints="titel_eng = ',-1,'" id="n">
         <mm:relatednodes type="provincies">
            <mm:field name="number">
               <mm:compare value="41288"> Noord-Brabant
                  <mm:createrelation source="n" destination="br" role="posrel" />
                  <mm:createrelation source="n" destination="oi" role="posrel" />
               </mm:compare>
               <mm:compare value="41289"> Limburg
                  <mm:createrelation source="n" destination="br" role="posrel" />
                  <mm:createrelation source="n" destination="oi" role="posrel" />
               </mm:compare>
               <mm:compare value="41283"> Gelderland
                  <mm:createrelation source="n" destination="ve" role="posrel" />
               </mm:compare>
               <mm:compare value="41285"> Noord-Holland
                  <mm:createrelation source="n" destination="gr" role="posrel" />
               </mm:compare>
               <mm:compare value="41284"> Utrecht
                  <mm:createrelation source="n" destination="gr" role="posrel" />
               </mm:compare>
               <mm:compare value="41281"> Overijssel
                  <mm:createrelation source="n" destination="wi" role="posrel" />
               </mm:compare>
               <mm:compare value="41282"> Flevoland 
                  <mm:createrelation source="n" destination="wi" role="posrel" />
               </mm:compare>
               <mm:compare value="41290"> Groningen
                  <mm:createrelation source="n" destination="dw" role="posrel" />
               </mm:compare>
               <mm:compare value="41279"> Friesland 
                  <mm:createrelation source="n" destination="dw" role="posrel" />
               </mm:compare>
               <mm:compare value="41280"> Drenthe 
                  <mm:createrelation source="n" destination="dw" role="posrel" />
               </mm:compare>
            </mm:field>
         </mm:relatednodes>
     </mm:listnodes>
   </body>
</html>
</mm:cloud>