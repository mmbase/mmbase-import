<%@ include file="jspbase.jsp" %>
<mm:cloud method="delegate" authenticate="class">
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="selectedavatar"/>
<mm:import externid="pathtype">poster_index</mm:import>
<mm:import externid="avatarset">otherset</mm:import>

<%-- imports when request is multipart post --%>
<mm:notpresent referid="selectedavatar">
    <mm:import externid="_handle" from="multipart"/>
    <mm:import externid="_handle_size" from="multipart"/>
    <mm:compare referid="_handle_size" value="0" inverse="true">
        <mm:import externid="addavatar" from="multipart"/>
        <mm:import externid="selectavatar" from="multipart"/>
        <mm:import externid="otheravatarset" from="multipart"/>
        <mm:import externid="avatarsets" from="multipart"/>
        <mm:import externid="_handle_name" from="multipart"/>
    </mm:compare>
</mm:notpresent>



<%-- imports when request is not multipart post --%>
<mm:present referid="selectedavatar">
  <mm:import externid="addavatar"/>
  <mm:import externid="selectavatar"/>
  <mm:import externid="selectedavatarnumber"/>
  <mm:import externid="avatarsets" />
  <mm:import externid="deleteavatarnumber" />
</mm:present>

<mm:import externid="forumid"/>
<mm:import externid="referrer" jspvar="referrer"/>
<mm:import externid="postareaid" />
<mm:import externid="posterid" id="profileid" />
<mm:import externid="profile"/>

<%-- login part --%>
<%@ include file="getposterid.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%-- end login part --%>

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

    <%--handle the upload of a new avatar--%>
    <mm:present referid="addavatar">
        <c:choose>
            <c:when test="${_handle.size < (1024 * 1024)}">
                <mm:transaction name="avatartrans">
                    <mm:node id="posternode" number="$posterid">
                    <mm:relatednodes type="avatarsets"  role="related" searchdir="destination">
                            <mm:first><mm:import id="userset"><mm:field name="number"/></mm:import></mm:first>
                        </mm:relatednodes>
                        <mm:related path="rolerel,images" fields="rolerel.role,rolerel.number" constraints="rolerel.role='avatar'">
                            <mm:first><mm:import id="presentavatar"><mm:field name="rolerel.number"/></mm:import></mm:first>
                        </mm:related>
                    </mm:node>

                    <%--create the image--%>
                    <mm:createnode id="avatarnode" type="images">
                        <mm:setfield name="title">Uploaded avatar(${_handle.name})</mm:setfield>
                        <mm:fieldlist fields="handle">
                        <mm:fieldinfo type="useinput" />
                        </mm:fieldlist>
                    </mm:createnode>
                    <mm:createrelation source="posternode" destination="avatarnode" role="rolerel">
                        <mm:setfield name="role">avatar</mm:setfield>
                    </mm:createrelation>

                    <%--delete the current avatar--%>
                    <mm:present referid="presentavatar">
                        <mm:node referid="presentavatar">
                            <mm:deletenode/>
                        </mm:node>
                    </mm:present>

                    <%--create an avatar set for this user if it is not there yet--%>
                    <mm:notpresent referid="userset">
                        <mm:createnode id="userset" type="avatarsets">
                            <mm:setfield name="name"><mm:node referid="posterid"><mm:field name="account"/></mm:node> 's set</mm:setfield>
                        </mm:createnode>
                        <mm:createrelation source="posternode" destination="userset" role="related" />
                    </mm:notpresent>

                    <mm:createrelation source="userset" destination="avatarnode" role="posrel" />
                </mm:transaction>
            </c:when>
            <c:otherwise>
                <c:set var="error"><mm:write referid="mlg.Upload_avatar_error_tolarge"/></c:set>
            </c:otherwise>
        </c:choose>

    </mm:present>

    <%--coose a new avatar from your avatar set--%>
    <mm:present referid="selectedavatarnumber">
        <mm:transaction name="avatartrans">
            <mm:node id="posternode" number="$posterid">
                <mm:relatednodes type="avatarsets">
                    <mm:first><mm:import id="userset"><mm:field name="number"/></mm:import></mm:first>
                </mm:relatednodes>
                <mm:related path="rolerel,images" fields="rolerel.role,rolerel.number" constraints="rolerel.role='avatar'">
                    <mm:first><mm:import id="presentavatar"><mm:field name="rolerel.number"/></mm:import></mm:first>
                </mm:related>
            </mm:node>

            <%-- remove the relation to the current avatar--%>
            <mm:present referid="presentavatar">
                <mm:node referid="presentavatar">
                    <mm:deletenode/>
                </mm:node>
            </mm:present>

            <mm:node id="avatarnode" referid="selectedavatarnumber"/>
            <%--check if the selected node exists as part of the avatar set--%>
            <mm:present referid="userset">
                <mm:node referid="userset">
                    <mm:related path="images" fields="images.number" constraints="images.number = ${avatarnode.number}" >
                        <mm:import id="avatarExists">true</mm:import>
                    </mm:related>
                </mm:node>
            </mm:present>

            <%--create an avatar set if not there yet--%>
            <mm:notpresent referid="userset">
                <mm:createnode id="userset" type="avatarsets">
                    <mm:setfield name="name"><mm:node referid="posterid"><mm:field name="account"/></mm:node> 's set</mm:setfield>
                </mm:createnode>
                <mm:createrelation source="posternode" destination="userset" role="related" />
            </mm:notpresent>

            <%--create a relation to the (new) avatar set--%>
            <mm:notpresent referid="avatarExists">
                <mm:createrelation source="userset" destination="avatarnode" role="posrel" />
            </mm:notpresent>

            <%-- set the selected image as avatar--%>
            <mm:createrelation source="posternode" destination="avatarnode" role="rolerel">
                <mm:setfield name="role">avatar</mm:setfield>
            </mm:createrelation>
        </mm:transaction>
    </mm:present>

  <mm:present referid="deleteavatarnumber">
      <mm:node id="posternode" number="$posterid">
        <mm:relatednodes type="avatarsets">
          <mm:first><mm:import id="userset"><mm:field name="number"/></mm:import></mm:first>
        </mm:relatednodes>
      </mm:node>

      <mm:node id="avatarnode" referid="deleteavatarnumber"/>
      <mm:present referid="userset">
        <mm:import id="constraint">images.number = <mm:node referid="avatarnode"><mm:field name="number"/></mm:node></mm:import>
        <mm:node referid="userset">
          <mm:related path="posrel,images" fields="images.number" constraints="$constraint" >
            <mm:node element="posrel">
              <mm:deletenode/>
            </mm:node>
          </mm:related>
        </mm:node>
      </mm:present>
  </mm:present>

<mm:present referid="addavatar">
  <%@ include file="includes/profile_updated.jsp" %>
</mm:present>

<mm:present referid="selectedavatarnumber">
  <%@ include file="includes/profile_updated.jsp" %>
</mm:present>

<mm:notpresent referid="selectedavatarnumber">
<mm:notpresent referid="addavatar">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>MMBob Member Profile</title>
    <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
  </head>
  <body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">

    <mm:include page="path.jsp?type=$pathtype" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr><th colspan="2" align="left">

            </th>
            </tr>

</table>


    <form enctype="multipart/form-data" action="<mm:url page="actions_avatar.jsp">
      <mm:param name="forumid" value="$forumid" />
      <mm:param name="postareaid" value="$postareaid" />
      <mm:param name="posterid" value="$posterid" />
      <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
      <mm:param name="profile" value="$profile" />
      <mm:param name="referrer" value="profile.jsp" />
      </mm:url>" method="post">

    <div id="profileb">
      <div id="tabs">
      <ul>
        <mm:compare value="ownset" referid="avatarset">
        <li class="selected">
        </mm:compare>
        <mm:compare value="ownset" referid="avatarset" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="avatarset" value="ownset"  />
        <mm:param name="selectedavatar" value="true" />
        <mm:param name="profile" value="$profile" />
        </mm:url>"><mm:write referid="mlg.own_avatars"/></a>
        </li>
        <mm:compare value="otherset" referid="avatarset">
        <li class="selected">
        </mm:compare>
        <mm:compare value="otherset" referid="avatarset" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="avatarset" value="otherset" />
        <mm:param name="selectedavatar" value="true " />
        <mm:param name="profile" value="$profile" />
        </mm:url>"><mm:write referid="mlg.other_avatars"/></a>
        </li>
      </ul>
    </div>

    <mm:compare value="otherset" referid="avatarset">
    <div id="profile">
      <div class="row">
        <mm:node referid="forumid">
          <mm:relatednodescontainer type="avatarsets">

            <mm:relatednodes>
              <mm:first>
                <mm:notpresent referid="avatarsets">
                  <mm:import externid="avatarsets" vartype="Integer">
                    <mm:field name="number" />
                  </mm:import>
                </mm:notpresent>
                <mm:import id="headdisplayed">true</mm:import>
                <span class="label"><mm:write referid="mlg.Select_category"/>:</span>
                <span class="formw">
                  <select name="avatarsets">
              </mm:first>
              <option <mm:field name="number"><mm:compare referid2="avatarsets">selected="true"</mm:compare> value="<mm:write/>"></mm:field><mm:field name="name"/></option>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>

        <mm:present referid="postareaid">
          <mm:compare referid="postareaid" value="" inverse="true">
            <mm:node referid="postareaid">
              <mm:relatednodescontainer type="avatarsets">

                <mm:relatednodes>
                  <mm:first>
                    <mm:notpresent referid="avatarsets">
                      <mm:import externid="avatarsets" vartype="Integer">
                        <mm:field name="number" />
                      </mm:import>
                    </mm:notpresent>
                    <mm:notpresent referid="headdisplayed">
                      <mm:import id="headdisplayed">true</mm:import>
                      <span class="label"><mm:write referid="mlg.Select_category"/>:</span>
                      <span class="formw">
                        <select name="avatarsets">
                    </mm:notpresent>
                  </mm:first>
                  <option <mm:field name="number"><mm:compare referid2="avatarsets">selected="true"</mm:compare> value="<mm:write/>"></mm:field><mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:node>
          </mm:compare>
        </mm:present>
        <mm:present referid="headdisplayed">
        </select>
          <input type="submit" name="otheravatarset" value="OK" />
        </span>
        </mm:present>


     <mm:notpresent referid="headdisplayed">
       <span class="label"><mm:write referid="mlg.No_avatars_installed"/></span>

     </mm:notpresent>

</div>

      <div class="row">
      <mm:notpresent referid="avatarsets">
        <%--<mm:listnodes type="avatarsets" max="1">
          <mm:import externid="avatarsets"><mm:field name="number"/></mm:import>
        </mm:listnodes>--%>
        &nbsp;
      </mm:notpresent>
      <mm:present referid="avatarsets">
        <mm:node referid="avatarsets">
          <mm:relatednodes type="images">
            <div class="avatarimage">
            <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:param name="avatarsets" value="$avatarsets" />
        <mm:field id="avatarnumber" name="number"/>
        <mm:param name="selectedavatarnumber" value="$avatarnumber" />

        <mm:param name="selectedavatar" value="true" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>">
            <img src="<mm:image template="s(80x80)" />" width="80" border="0"></a>
            </div>
          </mm:relatednodes>
        </mm:node>
      </mm:present>

      </div>
      </mm:compare>

    <mm:compare value="ownset" referid="avatarset">
    <div id="profile">

      <div class="row">

      <mm:node number="$profileid">
        <mm:related path="avatarsets,images" fields="images.number">
        <mm:field id="avatarnumber" name="images.number" write="false"/>

        <div class="avatarimage">
          <div class="deleteavatar">
            <a href="<mm:url page="actions_avatar.jsp">
            <mm:param name="forumid" value="$forumid" />
            <mm:param name="postareaid" value="$postareaid" />
            <mm:param name="posterid" value="$posterid" />
            <mm:param name="avatarset" value="ownset" />
            <mm:param name="deleteavatarnumber" value="$avatarnumber" />
            <mm:param name="selectedavatar" value="true" />
            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
            <mm:param name="profile" value="$profile" />
            </mm:url>"><img height="6" width="6" src="images/delete.gif" /></a>
         </div>

          <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:param name="avatarsets" value="$avatarsets" />
        <mm:param name="selectedavatarnumber" value="$avatarnumber" />
        <mm:param name="selectedavatar" value="true" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>">
<mm:node element="images">
            <img src="<mm:image template="s(80x80)" />" width="80" border="0">
</mm:node>
</a>
          </div>
          </mm:related>
        </mm:node>

      </div>
      </mm:compare>

    <div class="spacer">&nbsp;</div>

    </div>
    </div>
  </form>

</div>
<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</body>
</html>

</mm:notpresent></mm:notpresent>

</mm:locale>
</mm:content>
</mm:cloud>
