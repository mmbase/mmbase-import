<html xmlns="http://www.w3.org/1999/xhtml">
    <mm:link page="profile.jsp" id="link">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        <head>
            <meta http-equiv="refresh" content="5; url=${link}"/>
            <title>MMBase Forum Profile</title>
               <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
          </head>
          <body>
                <div id="profile">
                    <c:choose>
                        <c:when test="${empty error}">
                            <p><mm:write referid="mlg.ProfileUpdated"/></p>
                            <mm:booleanfunction set="mmbob" name="profileUpdated" referids="forumid,posterid"/>
                        </c:when>
                        <c:otherwise> <p>${error}</p> </c:otherwise>
                    </c:choose>
                    <br />
                <a href="${_}"><mm:write referid="mlg.Profile_returnto"/></a>
                </div>
          </body>
    </mm:link>
</html>