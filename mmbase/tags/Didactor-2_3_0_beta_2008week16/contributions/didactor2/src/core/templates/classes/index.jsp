<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator">
<mm:import id="orderby" externid="orderby">lastname</mm:import>
<mm:import id="directions" externid="directions">up</mm:import>
<mm:import id="offset" externid="offset">0</mm:import>
<% int pageLength = 250; %>
<html>
<head>
   <link href="../mmbase/edit/wizard/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="../mmbase/edit/wizard/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Resultaten Noise2Didactor conversie</title>
</head>
<body style="overflow:auto;">
<mm:listnodes type="people" max="5000">
   <mm:first>
      <mm:size jspvar="size" vartype="Integer"><% 
         for(int i=0; i< (size.intValue()/pageLength); i++) { 
            %><a href="index.jsp?orderby=<mm:write referid="orderby"/>&offset=<%= i*pageLength %>"><%= i %></a>&nbsp;<%
         } %>
      </mm:size>
   </mm:first>
</mm:listnodes><br/>
<b>Overzicht studenten</b><br/>
<div align="center">Sorteer: 
<mm:compare referid="directions" value="down"><a style="text-decoration:underline;" href="index.jsp?orderby=<mm:write referid="orderby"/>&directions=up">A..Z</a></mm:compare>
<mm:compare referid="directions" value="up"><a style="text-decoration:underline;" href="index.jsp?directions=down&orderby=<mm:write referid="orderby"/>">Z..A</a></mm:compare></div>
<mm:listnodes type="people" orderby="$orderby" offset="$offset" max="<%= "" + pageLength %>" directions="$directions">
   <mm:first><table  class="formcontent">
   <tr>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=initials&directions=<mm:write referid="directions" />">Initialen</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=firstname&directions=<mm:write referid="directions" />">Voornaam</a></td>
      <td class="fieldname">Tussenvoegsel</td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=lastname&directions=<mm:write referid="directions" />">Achternaam</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=address&directions=<mm:write referid="directions" />">Adres</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=zipcode&directions=<mm:write referid="directions" />">Postcode</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=city&directions=<mm:write referid="directions" />">City</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="index.jsp?orderby=description&directions=<mm:write referid="directions" />">OV Nummer</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="classes.jsp">Klassen</a></td>
      <td class="fieldname"><a style="text-decoration:underline;" href="workgroups.jsp">Werkgroep</a></td>
   </tr>
   </mm:first>
   <tr>
      <td><nobr><mm:field name="initials" /></nobr></td>
      <td><nobr><mm:field name="firstname" /></nobr></td>
      <td><nobr>???</td>
      <td><nobr><mm:field name="lastname" /></nobr></td>
      <td><nobr><mm:field name="address" /></nobr></td>
      <td><nobr><mm:field name="zipcode" /></nobr></td>
      <td><nobr><mm:field name="city" /></nobr></td>
      <td><nobr><mm:field name="description" /></nobr></td>
      <td><nobr>
         <mm:related path="classes">
            <mm:first inverse="true">, </mm:first>
            <mm:field name="classes.name" />
          </mm:related>
          </nobr>
      </td>
      <td><nobr>
          <mm:related path="workgroups">
            <mm:first inverse="true">, </mm:first>
            <mm:field name="workgroups.name" />
          </mm:related>
          </nobr>
      </td>
   </tr>
   <mm:last></table></mm:last>
</mm:listnodes>
</body>
</html>
</mm:cloud>

