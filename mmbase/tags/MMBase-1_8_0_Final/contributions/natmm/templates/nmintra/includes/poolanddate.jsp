<% if(true) { 
   String poolAndDate = "";
   %><mm:node referid="this_article"
       ><mm:related path="posrel,pools" orderby="pools.name"
         ><mm:field name="pools.name" jspvar="pools_name" vartype="String" write="false"><%
            if(!poolAndDate.equals("")) poolAndDate += ", ";
            poolAndDate += pools_name;
         %></mm:field
       ></mm:related
       ><mm:field name="embargo" jspvar="article_tdate" vartype="String" write="false"
       ><mm:field name="verloopdatum" jspvar="article_edate" vartype="String" write="false"><%
         long td = Integer.parseInt(article_tdate); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
         String tdateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
         td = Integer.parseInt(article_edate); td = 1000 * td; dd = new Date(td); cal.setTime(dd);
         String edateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
         if(showExpireDate!=null&&!showExpireDate.equals("-1")) { 
           if(tdateStr.equals(edateStr) && showExpireDate.equals("1") ) { showExpireDate = "2"; }
           if(!poolAndDate.equals("")) poolAndDate += " / ";
           if(showExpireDate.equals("0")) { poolAndDate += tdateStr; } 
           if(showExpireDate.equals("1")) { poolAndDate += tdateStr + " - " + edateStr; } 
           if(showExpireDate.equals("2")) { poolAndDate += edateStr; } 
         } %></mm:field
       ></mm:field><%
       if(!poolAndDate.equals("")) {
         %><span class="normal"><%= poolAndDate %></span><br><%
       }
   %></mm:node><%
} %>