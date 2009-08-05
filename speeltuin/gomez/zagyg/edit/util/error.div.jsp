<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
<%
    String domain = request.getServerName();
    if (domain.startsWith("www.")) {
        domain = domain.substring(4);
    }
    String webmasterEmail = "webmaster@"+domain;

    // prepare error details
    String msg = "";
    {
    // request properties
    Enumeration en = request.getHeaderNames();
    while (en.hasMoreElements())
    {
        String name = (String) en.nextElement();
        msg += name+": "+request.getHeader(name)+"\n";
    }
    msg += "\n";
    msg += "method: "+request.getMethod()+"\n";
    msg += "querystring: "+request.getQueryString()+"\n";
    msg += "requesturl: "+request.getRequestURL()+"\n";
    msg += "\n";

    // request parameters
    en = request.getParameterNames();
    while (en.hasMoreElements())
    {
        String name = (String) en.nextElement();
        msg += name+": "+request.getParameter(name)+"\n";
    }
    msg += "\n";

  if (cloud.getUser() != null) {
      msg += "cloud-user: " + cloud.getUser().getIdentifier() + "\n";
  } else {
     msg += "cloud-user: NULL\n";
 }
    msg += "\n";


    // add stack stacktrace
    if (exception instanceof BridgeException
        && ((BridgeException)exception).getCause() != null)
    {
        StringWriter wr = new StringWriter();
        PrintWriter pw = new PrintWriter(wr);
        pw.println("Exception:");
        exception.printStackTrace(new PrintWriter(wr));
        pw.println("\nCause:");
        Throwable ex = ((BridgeException)exception).getCause();
        ex.printStackTrace(new PrintWriter(wr));
        msg += wr.toString();
    }
    else
    {
        StringWriter wr = new StringWriter();
        PrintWriter pw = new PrintWriter(wr);
        pw.println("Exception:");
        exception.printStackTrace(pw);
        msg += wr.toString();
    }
    }

    // prepare error ticket
    java.text.DateFormat simple = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    String ticket = simple.format(new java.util.Date());

    // write errors to mmbase log
    log.error(ticket+":\n"+msg);
%>
  <div id="content">
    <h1>Oeps, er is een fout opgetreden!</h1>
    <p>
   Er is iets misgegaan: <%= exception.getMessage() %>.
  </p>
  <p>
    Als het niet duidelijk is wat er aan de hand is, kunt u het beste de beheerder van deze site
    benaderen, onder vermelding van het volgende nummer:
    </p>
    <p>
        <font color="red"><big><%= ticket %></big></font>
    </p>
    <p>
   en de onderstaande detail gegevens:
    </p>
<textarea rows="100" style="width:100%">
<%= Encode.encode("ESCAPE_XML",msg) %>
</textarea>
   </div>
</mm:log>
</mm:cloud>