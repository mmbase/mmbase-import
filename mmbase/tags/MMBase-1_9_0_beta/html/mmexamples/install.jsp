<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" expires="0">
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<html>
<head>
  <title>MMBase Demo installation</title>
  <link rel="stylesheet" href="../mmbase/style/css/mmbase.css" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
</head>

<body >
<table>
  <tr>
    <th class="main" colspan="3">MMBase Demo Installation</th>
  </tr>
  <tr>
    <td colspan="3">
      <p>
        This section will help you install the various example applications that are shipped with MMBase.
        To install an application, click the 'INSTALL' link.
      </p>
      <p>
        These are explained as you install them. You are advised to follow the notes.
        You can return to this installation script any time.
      </p>
      <p>
        Please not that most applications below are going to install builder XML's in the
        builder/applications directory of your config. This won't work if your app-server can or may
        not write those resources. 
      </p>
    </td>
  </tr>
  <tr>
    <th>Step</th> <th>Task</th> <th>Progress</th>
  </tr>
  <mm:import externid="installstep" jspvar="installstep" vartype="Integer">-1</mm:import>
        <%
           String[] steps= {
            "Resources",
            "MyNews",
        /*    "Community", app, not included in distribution */
            "Codings",
        /*    "RichText", not included in distribution */
            "MyCompany",
        /*    "MyUsers", scan, not included in distribution */
            "MyYahoo"
          };
           NodeManager versions=cloud.getNodeManager("versions");
           Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
           boolean installed = false;
           for (int step=0; step<steps.length; step++) {             
             String app = steps[step];
             NodeList nl = versions.getList("name='" + app + "'", null, null);
             installed = nl.size() > 0;
             String msg="";
             if (installstep.intValue()==step) {
              // install this step
              try {
                Hashtable params=new Hashtable();
                params.put("APPLICATION",app);
                mmAdmin.process("LOAD",app,params,request,response);
                msg="<p style=\"white-space:pre;\">"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
                installed = true;                                              
              } catch (java.lang.reflect.UndeclaredThrowableException ute) {
                Throwable t = ute;
                while (t.getCause() != null) {
                    t = t.getCause();
                }
                msg="<p style=\"white-space:pre;\"> Error: "+ t + "</p>";
              } catch (Throwable e ) {
                msg="<p style=\"white-space:pre;\"> Error: " + e + "</p>";
              }
             }
        %>
<tr valign="top">
    <td><a name="step<%=step%>"></a><%=step+1%>:<%=app%></td>
        <td>
          <% try { %>
        <p><%=mmAdmin.getInfo("DESCRIPTION-"+app,request,response)%></p>
        <% } catch (Exception e) { msg = e.getMessage(); } %>
        <%=msg%>
    </td>
    <td class="link" >
      <% if (installed) {%>
        Installed
        <%-- TODO XXX doesn't work <a href="<mm:url page='<%="install.jsp?installstep="+step+"#step"+step%>' />">[review&nbsp;installation&nbsp;notes]</a> --%>        
      <% } else { %>
        <a href="<mm:url page='<%="install.jsp?installstep="+step+"#step"+step%>' />">[INSTALL&nbsp;NOW]</a>
      <% } %>
    </td>
  </tr>
  <% } %>
        
</table>

  <div class="link">
    <a href="<mm:url page="." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>
