<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:createnode type="metastandard" id="n1692" >
<mm:setfield name="name">RdMC</mm:setfield>
<mm:setfield name="isused">1</mm:setfield>
</mm:createnode>
<%
if(true) {
String [] metadef_name = { "Didactische functie", "Titel", "Producttype", "Beoogde eindgebruiker", "Schooltype", "Vakleergebied", "Datum creatie", "Datum laatste wijziging", "Bestandsformaat", "Bestandsgrootte", "Technische vereiste", "Afspeelduur", "Didactisch scenario", "Aggregatieniveau", "Beroepssituatie", "Competentie", "Onderwerp", "Omschrijving", "Versie", "Status", "Benodigde tijd", "Rechten", "Sleutelwoorden" };
String [] metadef_description = { "", "", "Type van het product (in de technische en niet de didactische betekenis)", "", "", "", "", "", "", "", "De technische faciliteiten die nodig zijn om dit leerobject te gebruiken. Als er meerdere technische vereisten zijn, dan zijn ze allemaal noodzakelijk, dat wil zeggen dat de logische connector EN (AND) is.", "Tijd die het kost om een aaneengesloten (leer)object af te spelen op de bedoelde snelheid ervan.", "Onderwijsleersituatie waar het product cf. de keuze bij didactische functie wordt gebruikt ", "", "", "", "", "", "", "", "Tijd die het bij benadering of gewoonlijk van de beoogde gebruikersgroep vergt om met of aan dit product te werken", "In deze categorie worden de auteursrechten van dit leerobject beschreven en de voorwaarden waaronder men het mag gebruiken. ", "Karakterisering van het product " };
String [] metadef_type = { "1", "3", "1", "1", "1", "1", "3", "2", "3", "3", "3", "3", "3", "3", "1", "1", "3", "3", "3", "3", "3", "3", "1" };
String [] metadef_required = { "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
String [] metadef_maxvalues = { "1", "1", "9999", "9999", "9999", "9999", "1", "1", "1", "1", "9999", "1", "1", "1", "10", "3", "1", "1", "1", "1", "1", "1", "9999" };
String [] metadef_minvalues = { "1", "1", "1", "0", "0", "0", "1", "1", "1", "1", "1", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
String [] metadef_handler = {   "",  "",  "",  "",  "",  "",  "CreationDate", "", "MimeType", "FileSize", "StreamingHandler", "", "", "", "", "", "", "", "", "", "", "", "" };
for(int i=0; i<metadef_name.length; i++) {
%>
<mm:createnode type="metadefinition" id="<%= "n1692_" + i %>" >
<mm:setfield name="name"><%= metadef_name[i] %></mm:setfield>
<mm:setfield name="description"><%= metadef_description[i] %></mm:setfield>
<mm:setfield name="type"><%= metadef_type[i] %></mm:setfield>
<mm:setfield name="required"><%= metadef_required[i] %></mm:setfield>
<mm:setfield name="maxvalues"><%= metadef_maxvalues[i] %></mm:setfield>
<mm:setfield name="minvalues"><%= metadef_minvalues[i] %></mm:setfield>
<mm:setfield name="handler"><%= metadef_handler[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1692" destination="<%= "n1692_" + i %>" role="posrel" >
<mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
<%-- 

Repeating metastandards is not supported at this time

<mm:createnode type="metastandard" id="n1687" >
<mm:setfield name="name">Stakeholder</mm:setfield>
<mm:setfield name="isused">0</mm:setfield>
</mm:createnode>
<%
if(true) {
String [] metadef_name = { "Rol", "Naam" };
String [] metadef_description = { "", "" };
String [] metadef_type = { "1", "3" };
String [] metadef_required = { "1", "1" };
String [] metadef_maxvalues = { "1", "-1" };
String [] metadef_minvalues = { "0", "-1" };
String [] metadef_handler = { "", "" };
for(int i=0; i<metadef_name.length; i++) {
%>
<mm:createnode type="metadefinition" id="<%= "n1687_" + i %>" >
<mm:setfield name="name"><%= metadef_name[i] %></mm:setfield>
<mm:setfield name="description"><%= metadef_description[i] %></mm:setfield>
<mm:setfield name="type"><%= metadef_type[i] %></mm:setfield>
<mm:setfield name="required"><%= metadef_required[i] %></mm:setfield>
<mm:setfield name="maxvalues"><%= metadef_maxvalues[i] %></mm:setfield>
<mm:setfield name="minvalues"><%= metadef_minvalues[i] %></mm:setfield>
<mm:setfield name="handler"><%= metadef_handler[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1687" destination="<%= "n1687_" + i %>" role="posrel" >
<mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Rol" />
<mm:listnodes>
<mm:node id="n1688" >
<%
if(true) {
String [] metavalues = { "Auteur", "Co-auteur", "Eindverantwoordelijke", "Overig" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1688_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1688" destination="<%= "n1688_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>

--%>
<%
if(true) {
String [] metadef_name = { "Stakeholder" };
String [] metadef_description = { "Vul in: 'naam (rol)'. Voor rol kunt u kiezen uit: Auteur, Co-auteur, Eindverantwoordelijke of Overig." };
String [] metadef_type = { "3" };
String [] metadef_required = { "1" };
String [] metadef_maxvalues = { "-1" };
String [] metadef_minvalues = { "-1" };
String [] metadef_handler = { "" };
for(int i=0; i<metadef_name.length; i++) {
%>
<mm:createnode type="metadefinition" id="stakeholder" >
<mm:setfield name="name"><%= metadef_name[i] %></mm:setfield>
<mm:setfield name="description"><%= metadef_description[i] %></mm:setfield>
<mm:setfield name="type"><%= metadef_type[i] %></mm:setfield>
<mm:setfield name="required"><%= metadef_required[i] %></mm:setfield>
<mm:setfield name="maxvalues"><%= metadef_maxvalues[i] %></mm:setfield>
<mm:setfield name="minvalues"><%= metadef_minvalues[i] %></mm:setfield>
<mm:setfield name="handler"><%= metadef_handler[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1692" destination="stakeholder" role="posrel" >
<mm:setfield name="pos">99</mm:setfield>
</mm:createrelation>
<%
}
}
%>


<mm:createnode type="metastandard" id="n1684" >
<mm:setfield name="name">Taal</mm:setfield>
<mm:setfield name="isused">0</mm:setfield>
</mm:createnode>
<%
if(true) {
String [] metadef_name = { "Taal" };
String [] metadef_description = { "" };
String [] metadef_type = { "1" };
String [] metadef_required = { "0" };
String [] metadef_maxvalues = { "-1" };
String [] metadef_minvalues = { "-1" };
String [] metadef_handler = { "taal" };
for(int i=0; i<metadef_name.length; i++) {
%>
<mm:createnode type="metadefinition" id="<%= "n1684_" + i %>" >
<mm:setfield name="name"><%= metadef_name[i] %></mm:setfield>
<mm:setfield name="description"><%= metadef_description[i] %></mm:setfield>
<mm:setfield name="type"><%= metadef_type[i] %></mm:setfield>
<mm:setfield name="required"><%= metadef_required[i] %></mm:setfield>
<mm:setfield name="maxvalues"><%= metadef_maxvalues[i] %></mm:setfield>
<mm:setfield name="minvalues"><%= metadef_minvalues[i] %></mm:setfield>
<mm:setfield name="handler"><%= metadef_handler[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1684" destination="<%= "n1684_" + i %>" role="posrel" >
<mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Beroepssituatie" />
<mm:listnodes>
<mm:node id="n1721" >
<%
if(true) {
String [] metavalues = { "Contacten met ouders en instanties", "Gesprekken met leerlingen", "Hanteren van onverwachte situaties", "Huiswerk opdrachten geven en controleren...", "Omgaan met een groep", "Omgaan met verschillen", "Onderzoek en ontwikkeling van eigen opvattingen en competenties", "Samenwerken met collega's en werken in een organisatie", "Uitvoeren van lessen, leerproces" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1721_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1721" destination="<%= "n1721_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Schooltype" />
<mm:listnodes>
<mm:node id="n1701" >
<%
if(true) {
String [] metavalues = { "BVE", "Hoger onderwijs", "Pre primair onderwijs", "Primair onderwijs", "Speciaal onderwijs", "Voortgezet onderwijs" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1701_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1701" destination="<%= "n1701_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Beoogde eindgebruiker" />
<mm:listnodes>
<mm:node id="n1699" >
<%
if(true) {
String [] metavalues = { "(School)directie", "Aanstaande en beginnende docenten", "Coaches en mentoren", "Leraren in opleiding/zij-instromers", "Opleiders op de lerarenopleiding", "Opleiders op school", "Schoolleiders", "Schoolmanagement" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1699_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1699" destination="<%= "n1699_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Producttype" />
<mm:listnodes>
<mm:node id="n1697" >
<%
if(true) {
String [] metavalues = { "Afbeelding", "Animatie", "Document", "Geluidsfragment", "Video" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1697_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1697" destination="<%= "n1697_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Didactische functie" />
<mm:listnodes>
<mm:node id="n1693" >
<%
if(true) {
String [] metavalues = { "diagram", "diaplaatje", "examen", "experiment", "figuur", "grafiek", "leestekst", "oefening", "probleemstelling", "register", "simulatie", "tabel", "voordracht", "vragenlijst", "zelfbeoordeling" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1693_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1693" destination="<%= "n1693_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Taal" />
<mm:listnodes>
<mm:node id="n1685" >
<%
if(true) {
String [] metavalues = { "nl", "en", "de", "fr" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1685_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1685" destination="<%= "n1685_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metadefinition" >
<mm:constraint field="name" value="Vakleergebied" />
<mm:listnodes>
<mm:node id="subject" >
<%
if(true) {
String [] metavalues = { 
"Aardrijkskunde", "Alfabetisering NT1", "Alfabetisering NT2", "Algemene natuurwetenschappen", "Arabisch", "Beroepsgerichte vakken", "Bevordering van gezond gedrag", "Bevordering van sociale redzaamheid waaronder gedrag in het verkeer", "Biologie", "CKV", "Duits", "Economie", "Engels", "Engelse taal", "Expressieactiviteiten", "Filosofie", "Frans", "Geestelijke stromingen", "Geschiedenis", "Geschiedenis en staatsinrichting", "Grieks", "Handvaardigheid", "Informatica", "Informatiekunde", "Italiaans", "Kennis van de wereld", "Klassieke culturele vorming", "Kunstvakken", "Latijn", "Lichamelijke opvoeding", "Maatschappelijke verhoudingen (incl. staatsinrichting)", "Maatschappijleer", "Management en organisatie", "Muziek", "Natuur (biologie)", "Natuurkunde", "Nederlands", "Nederlandse taal", "Overstijgende vakken", "Rekenen / wiskunde", "Rekenen en wiskunde", "Russisch", "Scheikunde", "Spaans", "Techniek", "Tekenen", "Turks", "Verzorging", "Wiskunde", "Wiskunde en rekenen", "Zintuiglijke en lichamelijke oefening"};
for(int i=0; i<metavalues.length; i++) {
   %>
   <mm:createnode type="metavocabulary" id="<%= "subject_" + i %>" >
   <mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
   </mm:createnode>
   <mm:createrelation source="subject" destination="<%= "subject_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>

</mm:cloud>
</mm:content>
Done. 
