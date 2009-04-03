<%@page import = "java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%
String [] all_metavalues = { "Aardrijkskunde", "Alfabetisering NT1", "Alfabetisering NT2", "Algemene natuurwetenschappen", "Arabisch", "Beroepsgerichte vakken", "Bevordering van gezond gedrag", "Bevordering van sociale redzaamheid waaronder gedrag in het verkeer", "Biologie", "CKV", "Duits", "Economie", "Engels", "Engelse taal", "Expressieactiviteiten", "Filosofie", "Frans", "Geestelijke stromingen", "Geschiedenis", "Geschiedenis en staatsinrichting", "Grieks", "Handvaardigheid", "Informatica", "Informatiekunde", "Italiaans", "Kennis van de wereld", "Klassieke culturele vorming", "Kunstvakken", "Latijn", "Lichamelijke opvoeding", "Maatschappelijke verhoudingen (incl. staatsinrichting)", "Maatschappijleer", "Management en organisatie", "Muziek", "Natuur (biologie)", "Natuurkunde", "Nederlands", "Nederlandse taal", "Overstijgende vakken", "Rekenen / wiskunde", "Rekenen en wiskunde", "Russisch", "Scheikunde", "Spaans", "Techniek", "Tekenen", "Turks", "Verzorging", "Wiskunde", "Wiskunde en rekenen", "Zintuiglijke en lichamelijke oefening"};
%>
<mm:listnodescontainer type="metavocabulary" >
<mm:constraint field="value" value="Primair onderwijs" />
<mm:listnodes>
<mm:node id="cs1">
1. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlandse taal");
   metavalues.add("Engelse taal");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Geschiedenis");
   metavalues.add("Natuur (biologie)");
   metavalues.add("Maatschappelijke verhoudingen (incl. staatsinrichting)");
   metavalues.add("Geestelijke stromingen");
   metavalues.add("Rekenen en wiskunde");
   metavalues.add("Expressieactiviteiten");
   metavalues.add("Bevordering van sociale redzaamheid waaronder gedrag in het verkeer");
   metavalues.add("Bevordering van gezond gedrag");
   metavalues.add("Zintuiglijke en lichamelijke oefening");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd1_" + i %>">
            <mm:createrelation source="cs1" destination="<%= "cd1_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:composite operator="OR">
   <mm:constraint field="value" value="HAVO eerste fase" />
   <mm:constraint field="value" value="VWO eerste fase" />
</mm:composite>
<mm:listnodes>
<mm:node id="cs2">
2. Creating constraints for (2x) <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Engels");
   metavalues.add("Frans");
   metavalues.add("Duits");
   metavalues.add("Latijn");
   metavalues.add("Grieks");
   metavalues.add("Geschiedenis en staatsinrichting");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Economie");
   metavalues.add("Wiskunde");
   metavalues.add("Natuurkunde");
   metavalues.add("Scheikunde");
   metavalues.add("Biologie");
   metavalues.add("Verzorging");
   metavalues.add("Informatiekunde");
   metavalues.add("Techniek");
   metavalues.add("Lichamelijke opvoeding");
   metavalues.add("CKV");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd2_" + i %>">
            <mm:createrelation source="cs2" destination="<%= "cd2_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="VMBO" />
<mm:listnodes>
<mm:node id="cs3">
3. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Engels");
   metavalues.add("Maatschappijleer");
   metavalues.add("Kunstvakken");
   metavalues.add("Lichamelijke opvoeding");
   metavalues.add("Frans");
   metavalues.add("Duits");
   metavalues.add("Geschiedenis en staatsinrichting");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Wiskunde");
   metavalues.add("Natuurkunde");
   metavalues.add("Scheikunde");
   metavalues.add("Biologie");
   metavalues.add("Economie");
   metavalues.add("Verzorging");
   metavalues.add("Informatiekunde");
   metavalues.add("Techniek");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd3_" + i %>">
            <mm:createrelation source="cs3" destination="<%= "cd3_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="Praktijkonderwijs" />
<mm:listnodes>
<mm:node id="cs4">
4. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Rekenen en wiskunde");
   metavalues.add("Informatiekunde");
   metavalues.add("Lichamelijke opvoeding");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd4_" + i %>">
            <mm:createrelation source="cs4" destination="<%= "cd4_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="HAVO tweede fase" />
<mm:listnodes>
<mm:node id="cs5">
5. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Engels");
   metavalues.add("Frans");
   metavalues.add("Duits");
   metavalues.add("Spaans");
   metavalues.add("Russisch");
   metavalues.add("Italiaans");
   metavalues.add("Arabisch");
   metavalues.add("Turks");
   metavalues.add("Fries");
   metavalues.add("Algemene natuurwetenschappen");
   metavalues.add("Geschiedenis");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Maatschappijleer");
   metavalues.add("Economie");
   metavalues.add("Wiskunde");
   metavalues.add("Natuurkunde");
   metavalues.add("Scheikunde");
   metavalues.add("Biologie");
   metavalues.add("Filosofie");
   metavalues.add("Management en organisatie");
   metavalues.add("Informatica");
   metavalues.add("Informatiekunde");
   metavalues.add("Lichamelijke opvoeding");
   metavalues.add("CKV");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd5_" + i %>">
            <mm:createrelation source="cs5" destination="<%= "cd5_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="VWO tweede fase" />
<mm:listnodes>
<mm:node id="cs6">
6. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Engels");
   metavalues.add("Frans");
   metavalues.add("Duits");
   metavalues.add("Spaans");
   metavalues.add("Russisch");
   metavalues.add("Italiaans");
   metavalues.add("Arabisch");
   metavalues.add("Turks");
   metavalues.add("Fries");
   metavalues.add("Latijn");
   metavalues.add("Grieks");
   metavalues.add("Algemene natuurwetenschappen");
   metavalues.add("Geschiedenis");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Maatschappijleer");
   metavalues.add("Economie");
   metavalues.add("Wiskunde");
   metavalues.add("Natuurkunde");
   metavalues.add("Scheikunde");
   metavalues.add("Biologie");
   metavalues.add("Management en organisatie");
   metavalues.add("Informatica");
   metavalues.add("Lichamelijke opvoeding");
   metavalues.add("Klassieke culturele vorming");
   metavalues.add("CKV");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd6_" + i %>">
            <mm:createrelation source="cs6" destination="<%= "cd6_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="Speciaal onderwijs" />
<mm:listnodes>
<mm:node id="cs7">
7. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlandse taal");
   metavalues.add("Engelse taal");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Geschiedenis");
   metavalues.add("Natuur (biologie)");
   metavalues.add("Maatschappelijke verhoudingen (incl. staatsinrichting)");
   metavalues.add("Geestelijke stromingen");
   metavalues.add("Rekenen en wiskunde");
   metavalues.add("Expressieactiviteiten");
   metavalues.add("Bevordering van sociale redzaamheid waaronder gedrag in het verkeer");
   metavalues.add("Bevordering van gezond gedrag");
   metavalues.add("Zintuiglijke en lichamelijke oefening");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd7_" + i %>">
            <mm:createrelation source="cs7" destination="<%= "cd7_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="voortgezet speciaal onderwijs" />
<mm:listnodes>
<mm:node id="cs8">
8. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Geschiedenis");
   metavalues.add("Aardrijkskunde");
   metavalues.add("Maatschappijleer");
   metavalues.add("Wiskunde en rekenen");
   metavalues.add("Muziek");
   metavalues.add("Tekenen");
   metavalues.add("Handvaardigheid");
   metavalues.add("Lichamelijke opvoeding");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd8_" + i %>">
            <mm:createrelation source="cs8" destination="<%= "cd8_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="Middelbaar beroepsonderwijs" />
<mm:listnodes>
<mm:node id="cs9">
9. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Beroepsgerichte vakken");
   metavalues.add("Overstijgende vakken");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd9_" + i %>">
            <mm:createrelation source="cs9" destination="<%= "cd9_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
<mm:listnodescontainer type="metavocabulary">
<mm:constraint field="value" value="(Volwassenen)educatie" />
<mm:listnodes>
<mm:node id="cs10">
10. Creating constraints for <mm:field name="value" /><br/>
<%
if(true) {
   Vector metavalues = new Vector();
   metavalues.add("Nederlands");
   metavalues.add("Alfabetisering NT1");
   metavalues.add("Alfabetisering NT2");
   metavalues.add("Engels");
   metavalues.add("Rekenen / wiskunde");
   metavalues.add("Kennis van de wereld");
   for(int i=0; i<all_metavalues.length; i++) {
      if(!metavalues.contains(all_metavalues[i])) {
         %>
         <mm:listnodescontainer type="metavocabulary" >
         <mm:constraint field="value" value="<%= all_metavalues[i] %>" />
         <mm:listnodes>
            <mm:node id="<%= "cd10_" + i %>">
            <mm:createrelation source="cs10" destination="<%= "cd10_" + i %>" role="constraints">
               <mm:setfield name="type">3</mm:setfield> <!-- forbidden -->
            </mm:createrelation>
            </mm:node>
         </mm:listnodes>
         </mm:listnodescontainer>
         <%
      }
   }
}
%>
</mm:node>
</mm:listnodes>
</mm:listnodescontainer>
11. Creating group constraint for: Beoogde eindgebruiker, Schooltype and Vakleergebied<br/>
<%
if(true) {
   %>
   <mm:createnode type="group_constraints" id="group_constraint">
      <mm:setfield name="name">Beoogde eindgebruiker, Schooltype and Vakleergebied dependency</mm:setfield>
      <mm:setfield name="description">This constraint takes care that at least one of these metadefinitions is answered</mm:setfield>
      <mm:setfield name="maxvalues">9999</mm:setfield>
      <mm:setfield name="minvalues">1</mm:setfield>
      <mm:setfield name="handler">NOutOfM</mm:setfield>
   </mm:createnode>
   <mm:listnodescontainer type="metadefinition">
      <mm:composite operator="OR">
         <mm:constraint field="name" value="Beoogde eindgebruiker" />
         <mm:constraint field="name" value="Schooltype" />
         <mm:constraint field="name" value="Vakleergebied" />
      </mm:composite>
      <mm:listnodes>
         <mm:node id="destination">
            <mm:createrelation source="group_constraint" destination="destination" role="posrel" />
         </mm:node>
      </mm:listnodes>
   </mm:listnodescontainer>
   <mm:listnodescontainer type="metastandard" >
      <mm:constraint field="name" value="RdMC" />
      <mm:listnodes>
         <mm:node id="rdmc">
            <mm:createrelation source="rdmc" destination="group_constraint" role="related" />
         </mm:node>
      </mm:listnodes>
   </mm:listnodescontainer>
   <%
}
%>
</mm:cloud>
</mm:content>
Done. 
