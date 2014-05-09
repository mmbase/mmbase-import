<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" 
%><mm:content type="text/html">
<html>
<head>
  <title>Testing MMBase/taglib</title>
</head>
<body>
  <h1>Comparator - SHUFFLE</h1>
<%!
  public static class myComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2) {
       Node n1 = (Node) o1;
       Node n2 = (Node) o2;
       return n2.getIntValue("number") + n2.getIntValue("otype") - n1.getIntValue("number") - n1.getIntValue("otype");
    }
}
%>


<mm:cloud>
  <h2>SHUFFLE</h2>
  <mm:listnodescontainer type="object">
    <mm:sortorder field="number" direction="down" />
    <mm:maxnumber value="100" />
    <mm:listnodes max="10" comparator="SHUFFLE">
      <mm:field name="number" /> <mm:field name="otype" />: <mm:function name="gui"/> <br />
    </mm:listnodes>
  </mm:listnodescontainer>
  <hr />
  <h2>Another strange comparator</h2>
  <mm:listnodescontainer type="object">
    <mm:sortorder field="number" direction="down" />
    <mm:maxnumber value="100" />
    <mm:listnodes max="10" comparator="myComparator">
      <mm:field name="number" /> <mm:field name="otype" />: <mm:function name="gui"/> <br />
    </mm:listnodes>
  </mm:listnodescontainer>

</mm:cloud>
<hr />
<a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>
</mm:content>