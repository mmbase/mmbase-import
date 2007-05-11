<%@page contentType="text/xml" pageEncoding="utf-8"%>
<%@page import="java.util.Vector,nl.didactor.taglib.TranslateTable,java.util.StringTokenizer"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="message" jspvar="message"/>
<mm:import externid="locale" jspvar="locale"/>
<%
  String value = null;

  String queryString = request.getQueryString();
  StringTokenizer st = new StringTokenizer(queryString, "&");
  while (st.hasMoreTokens()) {
    String token = st.nextToken();
    StringTokenizer st2 = new StringTokenizer(token, "=");
    String key = null;
    String val = null;
    if (st2.hasMoreTokens()) {
      key = st2.nextToken();
    }
    if (st2.hasMoreTokens()) {
      val = st2.nextToken();
    }
    if ("value".equals(key)) {
      StringBuffer res = new StringBuffer();
      for (int i=0; i<val.length(); i++) {
        char c = val.charAt(i);
        if (c == '%') {
          i++;
          c = val.charAt(i);
          if (c == 'u') {
            // 4 more unicode chars
            String n = val.substring(i+1, i+5);
            res.append((char)Integer.parseInt(n, 16));
            i+=4;
          } else {
            i++;
            char d = val.charAt(i);
            res.append((char)Integer.parseInt("" + c + d, 16));
          }
        } else {
          res.append(c);
        }
      }
      value = res.toString();
    }
  }

  TranslateTable.changeTranslation(message, locale, value);
  TranslateTable.save();
%>
</mm:cloud>
</mm:content>

