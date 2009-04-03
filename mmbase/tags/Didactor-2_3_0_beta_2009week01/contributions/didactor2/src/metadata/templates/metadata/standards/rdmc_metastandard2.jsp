<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Omgaan met een groep" />
<mm:listnodes>
<mm:node id="n1755" >
<%
if(true) {
String [] metavalues = { "leiding geven aan groepsprocessen", "begeleiden (kleine) groepen", "orde handhaven, werkklimaat realiseren" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1755_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1755" destination="<%= "n1755_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Uitvoeren van lessen, leerproces" />
<mm:listnodes>
<mm:node id="n1763" >
<%
if(true) {
String [] metavalues = { "begeleiden van leerproces", "directe instructie", "evalueren van leerproces", "hanteren van onderwijsmethoden, gebruik schoolboeken", "introductie", "leerproces voorbereiden incl. lokaal, middelen en tijdsplanning", "specifiek vak en vakdidactisch handelen", "start van een les, proces incl. doelstellingen", "toetsen van leerproces, cijfers geven en feedback", "uitvoering", "verschillende werkvormen hanteren", "werken met (integrale) opdrachten, cases", "werken met nieuwe media (video/dvd/cd-rom/ICT)", "huiswerk opdrachten geven en controleren..." };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1763_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1763" destination="<%= "n1763_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Opvoeden op school" />
<mm:listnodes>
<mm:node id="n1753" >
<%
if(true) {
String [] metavalues = { "creëren en handhaven van veilige (leer)omgeving", "opstellen en handhaven van schoolregels", "participatie buitenschoolse activiteiten", "toezicht in de gangen en de pauzes" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1753_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1753" destination="<%= "n1753_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Hanteren van onverwachte situaties" />
<mm:listnodes>
<mm:node id="n1751" >
<%
if(true) {
String [] metavalues = { "didactische situaties", "situaties in school", "situaties met en tussen leerlingen", "situaties met externen" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1751_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1751" destination="<%= "n1751_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Gesprekken met leerlingen" />
<mm:listnodes>
<mm:node id="n1749" >
<%
if(true) {
String [] metavalues = { "advies gesprekken voeren (school- en beroespkeuze)", "begeleidingsgesprek voeren", "disciplinegesprek voeren", "groepsgesprekken met leerlingen voeren", "hulpgesprek voeren", "slecht nieuwsgesprek voeren" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1749_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1749" destination="<%= "n1749_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Omgaan met verschillen" />
<mm:listnodes>
<mm:node id="n1757" >
<%
if(true) {
String [] metavalues = { "Omgaan met leermoeilijkheden, leerstoornissen en achterstanden", "omgaan met verschillen in cultuur, sekse, waarden en normen", "omgaan met verschillen in taal, leerstijl, motivatie en tempo" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1757_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1757" destination="<%= "n1757_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Samenwerken met collega's en werken in een organisatie" />
<mm:listnodes>
<mm:node id="n1761" >
<%
if(true) {
String [] metavalues = { "een bijdrage leveren aan de organisatie", "een bijdrage leveren aan de schoolontwikkeling", "evenementen (excursies, open dag e.d.) organiseren en uitvoeren", "participeren in overlegvormen", "samenwerken in taakgerichte teams", "zorg structuur binnen school vormgeven en uitvoeren" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1761_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1761" destination="<%= "n1761_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Contacten met ouders en instanties" />
<mm:listnodes>
<mm:node id="n1747" >
<%
if(true) {
String [] metavalues = { "contacten met instanties en personen buiten de school", "gesprekken met ouders", "informatieavonden" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1747_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1747" destination="<%= "n1747_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Onderzoek en ontwikkeling van eigen opvattingen en competenties" />
<mm:listnodes>
<mm:node id="n1759" >
<%
if(true) {
String [] metavalues = { "beroepshouding expliciteren (eigen visie, opvattingen etc.)", "communiceren hierover b.v. intervisie, collegiale consultatie", "methodisch werken, eigen werkconcept hanteren incl. scholing", "reflecteren op eigen handelen en vastleggen" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n1759_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n1759" destination="<%= "n1759_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Primair onderwijs" />
<mm:listnodes>
<mm:node id="n2063" >
<%
if(true) {
String [] metavalues = { "Onderbouw", "Middenbouw", "Bovenbouw" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2063_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2063" destination="<%= "n2063_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Voortgezet onderwijs" />
<mm:listnodes>
<mm:node id="n2066" >
<%
if(true) {
String [] metavalues = { "Havo", "VWO", "Voorbereidend beroepsonderwijs", "Praktijkonderwijs" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2066_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2066" destination="<%= "n2066_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Havo" />
<mm:listnodes>
<mm:node id="n2247" >
<%
if(true) {
String [] metavalues = { "HAVO eerste fase", "HAVO tweede fase" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2247_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2247" destination="<%= "n2247_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="HAVO tweede fase" />
<mm:listnodes>
<mm:node id="n2244" >
<%
if(true) {
String [] metavalues = { "cultuur en maatschappij", "economie en maatschappij", "natuur en gezondheid", "natuur en techniek" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2244_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2244" destination="<%= "n2244_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="VWO" />
<mm:listnodes>
<mm:node id="n2269" >
<%
if(true) {
String [] metavalues = { "VWO eerste fase", "VWO tweede fase" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2269_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2269" destination="<%= "n2269_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="VWO tweede fase" />
<mm:listnodes>
<mm:node id="n2266" >
<%
if(true) {
String [] metavalues = { "cultuur en maatschappij", "economie en maatschappij", "natuur en gezondheid", "natuur en techniek" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2266_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2266" destination="<%= "n2266_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Voorbereidend beroepsonderwijs" />
<mm:listnodes>
<mm:node id="n2271" >
<%
if(true) {
String [] metavalues = {  "VMBO", "Lwoo (leerwegondersteunend onderwijs)" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2271_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2271" destination="<%= "n2271_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="VMBO" />
<mm:listnodes>
<mm:node id="n2272" >
<%
if(true) {
String [] metavalues = { "VMBO eerste fase", "VMBO tweede fase" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2272_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2272" destination="<%= "n2272_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="VMBO tweede fase" />
<mm:listnodes>
<mm:node id="n2274" >
<%
if(true) {
String [] metavalues = { "Leerweg basisberoepsgericht", "Leerweg gemengd", "Leerweg kaderberoepsgericht", "Leerweg theoretisch", "Sector Economie", "Sector Landbouw", "Sector Techniek", "Sector Zorg en welzijn" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2274_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2274" destination="<%= "n2274_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="BVE" />
<mm:listnodes>
<mm:node id="n2315" >
<%
if(true) {
String [] metavalues = {  "Middelbaar beroepsonderwijs", "(Volwassenen)educatie" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2315_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2315" destination="<%= "n2315_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Middelbaar beroepsonderwijs" />
<mm:listnodes>
<mm:node id="n2303" >
<%
if(true) {
String [] metavalues = { "BBL (beroepsbegeleidende leerweg)", "BOL (beroepsopleidende leerweg)" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2303_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2303" destination="<%= "n2303_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="(Volwassenen)educatie" />
<mm:listnodes>
<mm:node id="n2310" >
<%
if(true) {
String [] metavalues = { "Breed maatschappelijk functioneren", "Nederlands als tweede taal", "Sociale redzaamheid", "Voortgezet algemeen volwassenenonderwijs (vavo)" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2310_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2310" destination="<%= "n2310_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Hoger onderwijs" />
<mm:listnodes>
<mm:node id="n2316" >
<%
if(true) {
String [] metavalues = { "HBO", "WO" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2316_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2316" destination="<%= "n2316_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Speciaal onderwijs" />
<mm:listnodes>
<mm:node id="n2321" >
<%
if(true) {
String [] metavalues = { "Speciaal basisonderwijs", "Speciaal onderwijs ", "Speciaal voortgezet onderwijs", "Voortgezet speciaal onderwijs" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2321_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2321" destination="<%= "n2321_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Speciaal basisonderwijs" />
<mm:listnodes>
<mm:node id="n2328" >
<%
if(true) {
String [] metavalues = { "In hun ontwikkeling bedreigde kleuters (iobk)", "Leer- en opvoedingsmoeilijkheden (lom)", "Moeilijk lerende kinderen (mlk)" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2328_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2328" destination="<%= "n2328_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Speciaal voortgezet onderwijs" />
<mm:listnodes>
<mm:node id="n2329" >
<%
if(true) {
String [] metavalues = { "Leer- en opvoedingsmoeilijkheden (lom)", "Moeilijk lerende kinderen (mlk)" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2329_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2329" destination="<%= "n2329_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Speciaal onderwijs " />
<mm:listnodes>
<mm:node id="n2341" >
<%
if(true) {
String [] metavalues = { "Cluster 1: Slechtziend", "Cluster 2: Doof / Spraakmoeilijkheden", "Cluster 3: Motorisch / zeer moeilijk lerende kinderen (zmlk) / langdurig zieke kinderen (lzk) met een lichamelijke handicap", "Cluster 4: Zeer moeilijk opvoedbare kinderen (zmok) / langdurig zieke kinderen (lzk) zonder een lichamelijke handicap" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2341_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2341" destination="<%= "n2341_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
</mm:createrelation>
<%
}
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Voortgezet speciaal onderwijs" />
<mm:listnodes>
<mm:node id="n2351" >
<%
if(true) {
String [] metavalues = { "Cluster 1: Slechtziend", "Cluster 2: Doof / Spraakmoeilijkheden", "Cluster 3: Motorisch / zeer moeilijk lerende kinderen (zmlk) / langdurig zieke kinderen (lzk) met een lichamelijke handicap", "Cluster 4: Zeer moeilijk opvoedbare kinderen (zmok) / langdurig zieke kinderen (lzk) zonder een lichamelijke handicap" };
for(int i=0; i<metavalues.length; i++) {
%>
<mm:createnode type="metavocabulary" id="<%= "n2351_" + i %>" >
<mm:setfield name="value"><%= metavalues[i] %></mm:setfield>
</mm:createnode>
<mm:createrelation source="n2351" destination="<%= "n2351_" + i %>" role="posrel">
   <mm:setfield name="pos"><%= i+1 %></mm:setfield>
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
