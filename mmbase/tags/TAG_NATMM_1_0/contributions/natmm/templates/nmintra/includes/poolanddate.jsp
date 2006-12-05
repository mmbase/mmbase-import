<% if(true) { 
   String poolAndDate = "";
   %><mm:node referid="this_article"
       ><mm:related path="posrel,pools" orderby="pools.name"
         ><mm:field name="pools.name" jspvar="pools_name" vartype="String" write="false"><%
            if(!poolAndDate.equals("")) poolAndDate += ", ";
            poolAndDate += pools_name;
         %></mm:field
       ></mm:related><%
       if("1".equals(showDate)) {
         if(!poolAndDate.equals("")) { poolAndDate += " / ";  }
         %>
         <mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false">
            <%
            long td = Integer.parseInt(artikel_begindatum); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
            poolAndDate += cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
            %>
         </mm:field>
         <%
       }
       if(!poolAndDate.equals("")) {
         %><span class="normal"><%= poolAndDate %></span><br/><%
       }
   %></mm:node><%
} %>