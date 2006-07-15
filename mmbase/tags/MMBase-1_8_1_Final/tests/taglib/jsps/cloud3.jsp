<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud without attributes</h1>
    <p>
      <mm:cloud >
        You are logged in as: <mm:cloudinfo type="user" />
        (must be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='asis' attribute</h1>
    <p>
      <mm:cloud method="asis" >
        You are logged in as: <mm:cloudinfo type="user" />
        (must not be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='anonymous' attribute</h1>
    <p>
      <mm:cloud method="anonymous" >
        You are logged in as: <mm:cloudinfo type="user" />
        (must be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='asis' attribute (again)</h1>
    <p>
      Anonymous cloud will not replace the one in the session.
      <mm:cloud method="asis" >
        You are logged in as: <mm:cloudinfo type="user" />
        (must not be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud explicit login (wrong password)</h1>
    <p>
      <% try { %>
      <mm:cloud logon="foo" pwd="wrong password" method="sessionlogon">
        Should not see this (<mm:cloudinfo type="user" />)
      </mm:cloud>
      Should not see this.
      <% } catch (Exception e) { %>
      Ok this lead to an exception (<%= e.getMessage() %>)
      <% } %>
    </p>
    <h1>Cloud explicit login (with foo/bar)</h1>
    <p>
      <% try { %>
      <mm:cloud logon="foo" pwd="bar" method="sessionlogon">
        Should see this.
      </mm:cloud>
      <% } catch (Exception e) { %>
      Should not see this, (at least not if user foo with password bar exists). <%= e.getMessage() %>
      <% } %>
    </p>
    <mm:cloud method="logout" />
    <h1>Cloud explicit login (with foo/bar)</h1>
    <p>
      <% try { %>
      <mm:cloud logon="foo" pwd="bar">
        Should see this. <mm:cloudinfo type="user" /> (must be 'foo')
      </mm:cloud>
      <% } catch (Exception e) { %>
      Should not see this, (at least not if user foo with password bar exists). <%= e.getMessage() %>
      <% } %>
    </p>
    <h1>'asis' again</h1>
    <p>
      <mm:cloud method="asis" >
        You are logged in as: <mm:cloudinfo type="user" />
        (must be 'foo')
      </mm:cloud>
    </p>
    <hr />
    <a href="<mm:url page="cloud2.jsp" />">Previous</a><br />
    <a href="<mm:url page="cloud4.jsp" />">Next</a>
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
