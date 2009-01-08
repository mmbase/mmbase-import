<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
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
   Changes made in this update:<br/>
   1. added the url-rewriting (changed web.xml, classes and templates)<br/>
   2. moved includes to subdirectories per template<br/>
   3. added ContentAfdelingen builder functionality to builder afdelingen.xml<br/>
   4. added titel field to afdelingen editwizard, to force updating of titel_eng field in natuurgebieden<br/>
   5. added text to vacature.jsp in case there is no vacature<br/>
   6. use functions from SubscribeForm to check memberId and zipCode in checkmembership.jsp<br/>
   7. added commit.jsp template for the natuurboek event<br/>
   <mm:node number="492" id="parent" />
   <mm:createnode type="rubriek" id="rubriek">
      <mm:setfield name="naam">Test rubriek</mm:setfield>  
      <mm:setfield name="url">0</mm:setfield> 
   </mm:createnode>
   <mm:createrelation source="parent" destination="rubriek" role="parent"><mm:setfield name="pos">0</mm:setfield></mm:createrelation>
   <mm:createnode type="paginatemplate" id="cpagetemplate">
      <mm:setfield name="naam">bevestig_formulier</mm:setfield>  
      <mm:setfield name="url">confirm.jsp</mm:setfield> 
   </mm:createnode>
   <mm:createnode type="pagina" id="cpage">
      <mm:setfield name="titel">Afmelden voor het natuurboek</mm:setfield>
      <mm:setfield name="titel_zichtbaar">1</mm:setfield>
   </mm:createnode>
   <mm:node referid="cpage">
      <mm:createalias>afmeldenboek</mm:createalias>
   </mm:node>
   <mm:createrelation source="rubriek" destination="cpage" role="posrel"><mm:setfield name="pos">10</mm:setfield></mm:createrelation>
   <mm:createrelation source="cpage" destination="cpagetemplate" role="gebruikt" />
   <mm:createnode type="formulier" id="cform">
      <mm:setfield name="titel">Het Natuurboek cadeau voor alle leden</mm:setfield>
      <mm:setfield name="omschrijving">
         <p>
         In mei 2006 ontvangt u als lid van Natuurmonumenten Het Natuurboek met al onze gebieden. Een prachtig boek vol voorpret voor een dagje of weekendje uit in de natuur. Natuurlijk is het mogelijk dit boek niet te ontvangen. Dit kunt u op deze pagina aan ons doorgeven.
         <br/><br/>
         Geef uw lidmaatschapsnummer en postcode op om u af te melden voor Het Natuurboek.
         </p>
      </mm:setfield>
      <mm:setfield name="titel_fra">Afmelding verstuurd</mm:setfield>
      <mm:setfield name="omschrijving_fra">
      <p>
      Hartelijk bedankt. Het formulier is verzonden en ontvangen door onze Ledenservice. Wij verwerken dit zo spoedig mogelijk in onze administratie. Op uw verzoek ontvangt u het Natuurboek niet.
      <br/><br/>
      Met vriendelijke groet,
      <br/><br/>
      Ledenservice Natuurmonumenten
      </p></mm:setfield>
      <mm:setfield name="emailadressen">natuurboek@natuurmonumenten.nl</mm:setfield>
      <mm:setfield name="emailonderwerp">AFMELDEN</mm:setfield>
   </mm:createnode>
   <mm:createrelation source="cpage" destination="cform" role="posrel" />
   8. added configurable texts to routes.jsp + removed extra page.<br/>
   9. default email addresses in form.jsp are now from NatMMConfig.java<br/>
   10. set expiretime of cookie for temporary memberid to one day<br/>
   11. added membershipform. Including: 
   <ul>
   <li>builder members.xml,</li>
   <li>relation members-related-events_attachments</li>
   <li>relation users-gebruikt-events_attachments</li>
   <li>and struts-config</li>
   </ul>
   <mm:createnode type="paginatemplate" id="pagetemplate">
      <mm:setfield name="naam">lid worden</mm:setfield>  
      <mm:setfield name="url">membership.jsp</mm:setfield> 
   </mm:createnode>
   <mm:createnode type="pagina" id="page">
      <mm:setfield name="titel">Lid worden</mm:setfield>
      <mm:setfield name="titel_zichtbaar">1</mm:setfield>
      <mm:setfield name="kortetitel">Help de natuur in Nederland beschermen en word lid!</mm:setfield>
      <mm:setfield name="omschrijving"><p>De natuur staat steeds meer onder druk. Natuurmonumenten wil de natuur veilig stellen voor nu en later zodat u nu en ook in de toekomst van de natuur kan blijven genieten. Ook uw steun hebben we hierbij nodig.</p><br/><p>Vanaf &euro; 2,- per maand bent u al lid.</p><br/><p>Als welkomstgeschenk ontvangt u de 'Natuurwijzer', met informatie over ruim 350 natuurgebieden, elk kwartaal  ontvangt u het fraai geillustreerde magazine Natuurbehoud, u ontvangt korting op excursies en producten, u kunt gebruik maken van de speciale ledenaanbiedingen en u heeft natuurlijk de zekerheid dat de natuur wordt beschermd.</p></mm:setfield>
   </mm:createnode>
   <mm:createrelation source="rubriek" destination="page" role="posrel"><mm:setfield name="pos">20</mm:setfield></mm:createrelation>
   <mm:node referid="page">
      <mm:createalias>lidworden</mm:createalias>
   </mm:node>
   <mm:createrelation source="page" destination="pagetemplate" role="gebruikt" />
   Done.
   </body>
</html>
</mm:cloud>
