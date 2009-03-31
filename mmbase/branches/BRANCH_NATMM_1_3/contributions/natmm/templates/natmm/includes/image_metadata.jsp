<%-- NMCMS-639 --%>
<ul>
     <li>number: <mm:field name="number"/></li>
     <li>title: <mm:field name="title"/></li>
     <li>omschrijving: <mm:field name="omschrijving"/></li>
     <li>alt_tekst: <mm:field name="alt_tekst"/></li>
     <li>filename: <mm:field name="filename"/></li>
     <li>type: <mm:field name="itype"/></li>
     <li>bron: <mm:field name="bron"/></li>
     <li>redactionele aantekening: <mm:field name="metatags"/></li>
     <li>creatiedatum: 
         <mm:field name="creatiedatum" jspvar="creatiedatum" vartype="String" write="false">
            <mm:time time="<%=creatiedatum%>" format="EEE d MMM yyyy HH:mm"/>h
         </mm:field>
     </li>
     <li>datumlaatstewijziging: 
         <mm:field name="datumlaatstewijziging" jspvar="datumlaatstewijziging" vartype="String" write="false">
            <mm:time time="<%=datumlaatstewijziging%>" format="EEE d MMM yyyy HH:mm"/>h
         </mm:field>
     </li>
     <li>
        trefwoorden:
        <mm:related path="related,keywords">
            <mm:field name="keywords.word"/>
        </mm:related>
     </li>
     <mm:field name="reageer" jspvar="popup" write="false"/>
     <% String popup = (String)request.getAttribute("popup"); %>
     <li>popup: <%= ("0".equals(popup) ? "nee" : "ja") %></li>
</ul>
