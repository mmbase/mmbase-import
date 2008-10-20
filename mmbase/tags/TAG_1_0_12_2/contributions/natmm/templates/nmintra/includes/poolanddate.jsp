<% String poolAndDate = "";
   String artikelBegindatum = "";
   %><mm:node referid="this_article">
       <mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false"><%
        artikelBegindatum = artikel_begindatum;
       %></mm:field>
       <mm:related path="posrel,pools" orderby="pools.name">
        <mm:field name="pools.name" jspvar="pools_name" vartype="String" write="false"><%
            if(!poolAndDate.equals("")) poolAndDate += ", ";
            poolAndDate += pools_name;
         %></mm:field>
       </mm:related><%
       long td = Integer.parseInt(artikelBegindatum);
       td = 1000 * td;
       Date dd = new Date(td);
       cal.setTime(dd);
       if("1".equals(showDate) && cal.getTimeInMillis() > 0) {
         if(!poolAndDate.equals("")) { poolAndDate += " / "; }
         poolAndDate += cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR);
       }
       if(!poolAndDate.equals("")) {
         %><span class="normal"><%= poolAndDate %></span><br/><%
       }
   %></mm:node>