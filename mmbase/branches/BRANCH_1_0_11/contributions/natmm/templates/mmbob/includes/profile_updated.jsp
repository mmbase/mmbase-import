<html <%-- hh xmlns="http://www.w3.org/1999/xhtml" --%>>
  <head>
    <meta http-equiv="refresh" content="5; url=<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        
        </mm:url>"/>
    <title>MMBase Forum Profile</title>
       <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
  </head>
  <body>
    <div id="profile">
    <p>Uw profiel is bijgewerkt
    <br />

    <mm:booleanfunction set="mmbob" name="profileUpdated" referids="forumid,posterid">
    </mm:booleanfunction>

    <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>">Klik hier om terug te keren naar het profiel</a>
    </div>

  </body>
</html>