<%@page import="java.util.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud rank="basic user" method="http" >
<%
  StringBuffer s=new StringBuffer();
  Map params=request.getParameterMap();
  Iterator i=params.keySet().iterator();
  boolean first=true;
  Object key;
  while(i.hasNext()){
    s.append(first?"?":"&");
    key=i.next();
    s.append((String)key+"="+((String[])params.get(key))[0]);
    first=false;
  }
    response.sendRedirect("../index.jsp"+s.toString());
  %>
    hallo
  </mm:cloud>

