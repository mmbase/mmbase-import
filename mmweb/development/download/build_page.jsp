<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*, java.io.*, java.text.* " session="true"%>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>

<td>
<mm:import externid="dir" jspvar="dir" />

<h2><mm:write referid="dir" /></h2>
<% File baseDir = new File("/home/nightlybuild/builds"); %>

<%! class DirSorter implements Comparator {
     public int compare(Object o1, Object o2) {
           File f2 =(File) o2;
           File f1 = (File) o1;
           return f2.getName().compareTo(f1.getName());
     }
}

void showDir(File baseDir, String dirName, javax.servlet.jsp.JspWriter out) throws IOException {
  File dir = new File(baseDir, dirName);
  File[] content = dir.listFiles(); 
  Arrays.sort(content, new DirSorter());
  out.println("<table>");
  for (int i=0; i< content.length ; i++) {
    File f = (File) content[i];
       out.println("<tr><td class='link'><nobr><a href='" + dirName + "/" + f.getName() +"'>" + f.getName() + "</a></nobr></td></tr>");
  }
  out.println("</table>");
}

%>
<%
   showDir(baseDir, dir, out); 
%>
<mm:url id="back" referids="portal,page" page="index.jsp" write="false" />
<%@include file="/includes/backbutton.jsp" %>
</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
