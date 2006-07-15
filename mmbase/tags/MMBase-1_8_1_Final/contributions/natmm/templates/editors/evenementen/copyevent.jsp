<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*" 
%><%@include file="/taglibs.jsp"  
%><mm:import externid="e" jspvar="eventId" id="eventId">-1</mm:import
><mm:import externid="d" jspvar="dateId" id="dateId">-1</mm:import><%
String nowSec = "" + ( (new Date().getTime())/1000 + 24*60*60);
if(eventId.indexOf("-")==-1) {
      // fields that will not be copied: bron, status ??
  %><mm:cloud method="http" rank="basic user" jspvar="cloud"
   ><mm:node number="<%= eventId %>" jspvar="orgEvent" 
   ><mm:createnode type="evenement" id="newEvent"
      ><mm:setfield name="titel"><%= "Copy van " + orgEvent.getStringValue("titel") %></mm:setfield
      ><mm:setfield name="titel_zichtbaar"><%= orgEvent.getStringValue("titel_zichtbaar") %></mm:setfield
      ><mm:setfield name="tekst"><%= orgEvent.getStringValue("tekst") %></mm:setfield
      ><mm:setfield name="omschrijving"><%= orgEvent.getStringValue("omschrijving") %></mm:setfield
      ><mm:setfield name="groepsexcursie"><%= orgEvent.getStringValue("groepsexcursie") %></mm:setfield
      ><mm:setfield name="min_aantal_deelnemers"><%= orgEvent.getStringValue("min_aantal_deelnemers") %></mm:setfield
      ><mm:setfield name="max_aantal_deelnemers"><%= orgEvent.getStringValue("max_aantal_deelnemers") %></mm:setfield
      ><mm:setfield name="cur_aantal_deelnemers">0</mm:setfield
      ><mm:setfield name="metatags"><%= orgEvent.getStringValue("metatags") %></mm:setfield
      ><mm:setfield name="aanmelden_vooraf"><%= orgEvent.getStringValue("aanmelden_vooraf") %></mm:setfield
      ><mm:setfield name="achteraf_bevestigen"><%= orgEvent.getStringValue("achteraf_bevestigen") %></mm:setfield
      ><mm:setfield name="voorkeur_verplicht"><%= orgEvent.getStringValue("voorkeur_verplicht") %></mm:setfield
      ><mm:setfield name="begininschrijving"><%= orgEvent.getStringValue("begininschrijving") %></mm:setfield
      ><mm:setfield name="eindinschrijving"><%= orgEvent.getStringValue("eindinschrijving") %></mm:setfield
      ><mm:setfield name="begindatum"><%= nowSec %></mm:setfield
      ><mm:setfield name="einddatum"><%= nowSec %></mm:setfield
      ><mm:setfield name="embargo"><%= orgEvent.getStringValue("embargo") %></mm:setfield
      ><mm:setfield name="verloopdatum"><%= orgEvent.getStringValue("verloopdatum") %></mm:setfield
      ><mm:setfield name="lokatie"><%= orgEvent.getStringValue("lokatie") %></mm:setfield
      ><mm:setfield name="soort"><%= orgEvent.getStringValue("soort") %></mm:setfield
      ><mm:setfield name="status"><%= orgEvent.getStringValue("status") %></mm:setfield
      ><mm:setfield name="isspare"><%= orgEvent.getStringValue("isspare") %></mm:setfield
      ><mm:setfield name="isoninternet"><%= orgEvent.getStringValue("isoninternet") %></mm:setfield
      ><mm:setfield name="iscanceled"><%= orgEvent.getStringValue("iscanceled") %></mm:setfield
   ></mm:createnode
   
   ><mm:related path="readmore,extra_info"
      ><mm:remove referid="extra_info"
      /><mm:node element="extra_info" id="extra_info" 
      /><mm:createrelation source="newEvent" destination="extra_info" role="readmore"
   /></mm:related
   
   ><mm:related path="posrel,deelnemers_categorie"
      ><mm:remove referid="posrel_pos"
      /><mm:remove referid="deelnemers_categorie"
      /><mm:field name="posrel.pos" id="posrel_pos" write="false" 
      /><mm:node element="deelnemers_categorie" id="deelnemers_categorie" 
      /><mm:createrelation source="newEvent" destination="deelnemers_categorie" role="posrel"
         ><mm:setfield name="pos"><mm:write referid="posrel_pos" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ><mm:related path="related,evenement_type"
      ><mm:remove referid="evenement_type"
      /><mm:node element="evenement_type" id="evenement_type" 
      /><mm:createrelation source="newEvent" destination="evenement_type" role="related"
   /></mm:related
   
   ><mm:related path="posrel,vertrekpunten"
      ><mm:remove referid="vertrekpunten"
      /><mm:node element="vertrekpunten" id="vertrekpunten" 
      /><mm:createrelation source="newEvent" destination="vertrekpunten" role="posrel"
   /></mm:related
   
   ><mm:related path="related,natuurgebieden"
      ><mm:remove referid="natuurgebieden"
      /><mm:node element="natuurgebieden" id="natuurgebieden" 
      /><mm:createrelation source="newEvent" destination="natuurgebieden" role="related"
   /></mm:related
   
   ><mm:related path="readmore,paragraaf"
      ><mm:remove referid="readmore_readmore"
      /><mm:remove referid="paragraaf"
      /><mm:field name="readmore.readmore" id="readmore_readmore" write="false"
      /><mm:node element="paragraaf" id="paragraaf" 
      /><mm:createrelation source="newEvent" destination="paragraaf" role="readmore"
         ><mm:setfield name="readmore"><mm:write referid="readmore_readmore" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ><mm:related nodes="<%= eventId %>" path="posrel,bevestigings_teksten"
      ><mm:remove referid="posrel_pos"
      /><mm:remove referid="bevestigings_teksten"
      /><mm:field name="posrel.pos" id="posrel_pos" write="false"
      /><mm:node element="bevestigings_teksten" id="bevestigings_teksten" 
      /><mm:createrelation source="newEvent" destination="bevestigings_teksten" role="posrel"
         ><mm:setfield name="pos"><mm:write referid="posrel_pos" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ><mm:related path="readmore,afdelingen"
      ><mm:remove referid="readmore_readmore"
      /><mm:remove referid="afdelingen"
      /><mm:field name="readmore.readmore" id="readmore_readmore" write="false"
      /><mm:node element="afdelingen" id="afdelingen" 
      /><mm:createrelation source="newEvent" destination="afdelingen" role="readmore"
         ><mm:setfield name="readmore"><mm:write referid="readmore_readmore" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ><mm:related path="readmore,medewerkers"
      ><mm:remove referid="readmore_readmore"
      /><mm:remove referid="medewerkers"
      /><mm:field name="readmore.readmore" id="readmore_readmore" write="false"
      /><mm:node element="medewerkers" id="medewerkers" 
      /><mm:createrelation source="newEvent" destination="medewerkers" role="readmore"
         ><mm:setfield name="readmore"><mm:write referid="readmore_readmore" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ><mm:related path="readmore,questions"
      ><mm:remove referid="readmore_readmore"
      /><mm:remove referid="questions"
      /><mm:field name="readmore.readmore" id="readmore_readmore" write="false"
      /><mm:node element="questions" id="questions" 
      /><mm:createrelation source="newEvent" destination="questions" role="readmore"
         ><mm:setfield name="readmore"><mm:write referid="readmore_readmore" /></mm:setfield
      ></mm:createrelation
   ></mm:related
   
   ></mm:node
   ></mm:cloud><%
} 
%>