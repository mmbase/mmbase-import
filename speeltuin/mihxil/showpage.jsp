<body>
<html>
<body>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<% if (! (request.getParameter("delete") == null)) {
      java.io.File f = new java.io.File((String) request.getParameter("delete"));
     if ( f.delete()) {
       out.println("'" + f.toString() + "' was deleted<br />");
     } else {
  out.println("'" + f.toString() + "' could not be deleted<br />");
     } 
 
} else try {
          
            
            java.io.File file = new java.io.File((String)request.getParameter("url"));
            out.println("<table>");
            if (file.isDirectory()) {
               java.io.File[] content = file.listFiles();
               for (int i=0; i< content.length; i++) {
                  java.io.File f = (java.io.File) content[i];
                  out.println("<tr><td>" + (f.isDirectory() ? "d" : "_") + (f.canRead() ?   "r" : "_") + (f.canWrite() ? "w" : "_") + "</td><td align='right'>" + f.length() + "&nbsp;</td><td>" +  new
               java.util.Date(f.lastModified()) + "</td><td><a href=\"?url=" + f.toString() +"\">" + f.toString() + "</a></td><td>" + (f.canWrite() ? "<a href=\"?delete=" + f.toString() + "\">delete</a>" : "")+ "</td></tr>" );
               }
            out.println("</table>");
            } else {
%>
<pre>
<mm:formatter format="escapexml">
<%
                 java.io.FileReader reader = new java.io.FileReader(file);
                 java.io.StringWriter string = new java.io.StringWriter();
                 int c = reader.read();
                  while (c != -1) {
                     string.write(c);
                    c = reader.read();
                }
                out.println(string.toString());
%>
</mm:formatter>
</pre> <%

            }
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString()); 
        }
%>

</body>
</html>
