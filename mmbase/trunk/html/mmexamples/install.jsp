<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<html>
<head>
  <title>MMBase Demo installation</title>
  <link rel="stylesheet" href="../mmbase/style/css/mmbase.css" type="text/css" />
</head>

<body >
<table align="center" width="97%" cellspacing="1" cellpadding="3" border="0">
<tr>
    <th class="main" colspan="3">MMBase Demo Installation</th>
</tr>
<tr>
    <td colspan="3">
<br />
This section will help you install the various example applications that are shipped with MMBase.
The applications are listed in the order in which they should be installed (some applications depend on each other).
To install an application, click the 'INSTALL' link.<br />
The Yahoo, Community, and BugTracker applications require some additional actions that can not (yet) be automatically started.<br />
These are explained as you install them. You are advised to follow the notes.<br />
You can return to this installation script any time.<br />
<br />
    </td>
</tr>
<tr>
<th>Step</th>
<th>Task</th>
<th>Progress</th>
</tr>
<mm:import externid="installstep" jspvar="installstep" vartype="Integer">-1</mm:import>
        <%
           String[] steps= {
            "Resources",
            "MyNews",
            "Community",
            "BugTracker",
            "Codings",
            "RichText",
            "MyCompany",
            "MyUsers",
            "MyYahoo"
          };
           boolean first=true;
           boolean installed=false;
           NodeManager versions=cloud.getNodeManager("versions");
           Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");

           for (int step=0; step<steps.length; step++) {             
             String app = steps[step];
             NodeList nl = versions.getList("name='" + app + "'", null, null);
             installed = nl.size()>0;

             String msg="";
             if (installstep.intValue()==step) {
              // install this step
              try {
                Hashtable params=new Hashtable();
                params.put("APPLICATION",app);
                mmAdmin.process("LOAD",app,params,request,response);
                msg="<p style=\"white-space:pre;\">"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
              } catch (Exception e ) {
                msg="<p style=\"white-space:pre;\"> Error: "+e+"</p>";
              }
              installed=true;
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
                     Installed,
                     <a href="<mm:url page='<%="install.jsp?installstep="+step+"#step"+step%>' />">[review&nbsp;installation&nbsp;notes]</a>

                <% } else { %>
                <a href="<mm:url page='<%="install.jsp?installstep="+step+"#step"+step%>' />">[INSTALL&nbsp;NOW]</a>
                <% } %>
    </td>
</tr>
        <% } %>

<tr><td colspan="3">&nbsp;</td></tr>

</table>
<hr />
<a href="<mm:url page="." />"> back </a>
</body>
</html>
</mm:cloud>