<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud method='asis' attribute</h1>
    <p>
      Of course, 'asis' must port over pages.
      <mm:cloud method="asis" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must not be anonymous, but 'foo')
      </mm:cloud>
    </p>
    <p>
      Logging out for next page: <mm:cloud method="logout" />
    </p>
  <hr />
  <a href="cloud3.jsp">Previous</a><br />
  <form name="test" enctype="multipart/form-data" action="cloud5.jsp">
    <input type="hidden" name="from" value="4" />
    <a href="#" onClick="document.forms['test'].submit();" >next</a>
        <input type="hidden"                    name="searchfields" value="firstname,lastname" />
          <input type="text"                      name="searchvalue" />
          <!-- input type="submit" value="OK" -->
          <input type="hidden" name="wizard"      value="tasks/people" />
          <input type="hidden" name="nodepath"    value="people" />
          <input type="hidden" name="fields"      value="number,firstname,middle,lastname" />
          <input type="hidden" name="orderby"     value="lastname" />
          <input type="hidden" name="constraints" value="owner = 'werkbank'" />
          <input type="hidden" name="language"    value="nl" />
  </form>
  <br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
