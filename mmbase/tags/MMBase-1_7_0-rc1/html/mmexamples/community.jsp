<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" jspvar="cloud">
<html>
<head>
  <title>MMExamples - Community</title>
   <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body>
  <br />
  <table width="90%" align="center">
    <tr>
      <th colspan="2">Description of the Community examples</th>
    </tr>
    <tr>
      <td colspan="2" valign="top">
         <p>The community example shows all the features of the community functionality in MMBase.</p>
         <p>It displays a forum and a chat, it also shows the admin-possibilities.</p>
      </td>
    </tr>
    <tr>
      <th colspan="2">Location of the Community example</th>
    </tr>
    <tr>
        <td colspan="2" >
            <br />
            <mm:list path="versions" fields="versions.name,versions.type" constraints="versions.name='Community' AND versions.type='application'">
              <mm:first>
                <mm:import id="mynewsIsPresent">true</mm:import>
              </mm:first>
            </mm:list>
            <mm:notpresent referid="mynewsIsPresent">
              The Community application is not installed.<br />
              Please install before using it.<br />
              You can install the Community application by going to ADMIN -> APPLICATIONS
            </mm:notpresent>
            <mm:present referid="mynewsIsPresent">
              <%
                 org.mmbase.bridge.Module mod=null;
                 try {
                    mod= cloud.getCloudContext().getModule("communityprc");
                 } catch (Exception e) {}
                 if (mod==null) {
              %>
              The Community module "communityprc" is not active.<br />
              You will need to turn it active and restart the server to use the community.<br />
              You can activate the Community module by looking for the file "communityprc.xml" in the modules
              directory of your MMBase configuration, and setting the content of the "status' tag to "active".
              <% } else {
              %>
              <mm:url id="url" page="community/community.jsp" write="false" />
              This url will show the Community: <a href="<mm:write referid="url" />" target="community"><mm:write referid="url" /></a>
              <% } %>
            </mm:present>
            <br /><br />
        </td>
    </tr>
    <tr>
      <th>Cloud Design</th>
      <th>Picture</th>
    </tr>
    <tr>
      <td valign="top">
The image on the right shows the basic-design of the community-application.
Community is the toplevel node which can consists of multiple channels.
Each channel exists of one or more mesages.
If people must login before they can participate in a channel, there must be
a relation between the people node and the channel.
A community can be a forum or a chat.
      </td>
      <td>
        <a href="share/images/community_cloud.jpg" target="img"><img src="share/images/community_cloud.jpg" width="300" alt="cloud design"/></a>
      </td>
    </tr>
    <tr>
      <th>Manual</th>
      <th>Picture</th>
    </tr>
    <tr valign="top">
      <td>
The image on the right shows the homepage of the community-example.
This example shows you the basic features of the community application.
      </td>
      <td><a href="share/images/community_manual.jpg" target="img"><img src="share/images/community_manual.jpg" width="300" alt="community manual" /></a></td>
     </tr>
  </table>
</body>
</html>
</mm:cloud>





