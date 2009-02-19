<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="asis">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">

<html xmlns="http://www.w3.org/TR/xhtml">
  <head>
    <title>License Information</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css">
  </head>

  <body>

  <table summary="license information">
    <tr>
      <th colspan="2">License Information &amp; Thanks to</th>
   </tr>
   <tr>
     <td class="multidata" colspan="2">
       <h2>Welcome to MMBase Content management system.</h2>
       <h3>Version: <%= org.mmbase.Version.get() %></h3>
       <p>
         If you are running on the binary distribution: It is shipped with a database that is
         written in Java: <a href="http://www.hsqldb.org">Hsqldb</a>, configured for <strong>memory
         only</strong>.  MMBase does support both commercial and opensource databases and you must
         make a decision on which database you want to run an actual site. You can use HSQL for
         that, but you must configure it to write it's data to disk then. HSQL is useable for small
         and simple sites, for bigger sites consider something like MySQL or Postgresql.
       </p>
       <p>
         If you wish to build your own MMBase version from the sourcecode, you are encouraged to
         download the <i>source distribution</i>.
       </p>
       <p>If you run into problems you can get help from the following sources :
         <ul>
           <li>Shipped documentation - the readme, releasenotes and installation docs. These documents are in the root of the binary distro.</li>
           <li>The MMBase website - <a href="http://www.mmbase.org" target="_ext">http://www.mmbase.org</a>.</li>
           <li>Contact information - <a href="http://www.mmbase.org/communication" target="_ext">http://www.mmbase.org/communication</a>.</li>
           <li>The mailinglists - <a href="http://lists.mmbase.org" target="_ext">http://lists.mmbase.org</a>.</li>
           <li>The developers irc channel - see <a href="http://www.mmbase.org/irc" target="_ext">irc page for mmbase</a>.</li>
           <li>The MMBase bugtracker - <a href="http://www.mmbase.org/bug" target="_ext">http://www.mmbase.org/bug</a>.</li>
         </ul>
       </p>
       <p><i> -- The MMBase Release Team.</i></p>
     </td>
   </tr>

   <tr class="footer">
     <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
     <td class="data">Return to home page</td>
   </tr>
  </table>
  </body>
</html>

</mm:cloud>
