<mm:field name="sortedurls()" jspvar="urls" vartype="list">
   <%
      Iterator i = urls.iterator();
      while(i.hasNext()) {
         ResponseInfo ri = (ResponseInfo) i.next();
         out.println("<a  href='" + ri.getURL() + "'>" + ri.getFormat() + "</a>"); 
         if (i.hasNext()) out.println(",");
      }
   %>
</mm:field>
