<%@page import="java.util.*"%>
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
  request.getSession().invalidate();
  response.sendRedirect("../index.jsp"+s.toString());
  %>
