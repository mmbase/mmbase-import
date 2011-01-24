<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
    <mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
    <mm:compare referid="forumid" value="unknown">
        <mm:import id="key"><%= request.getHeader("host") %></mm:import>
        <mm:import id="forumalias"><mm:function set="mmbob" name="getForumAlias" referids="key" /></mm:import>
        <mm:compare referid="forumalias" value="unknown" inverse="true">
            <mm:import id="forumid" reset="true" jspvar="forumid"><mm:write referid="forumalias" /></mm:import>
        </mm:compare>
    </mm:compare>
    <%@ include file="thememanager/loadvars.jsp" %>
    <html>
    <head>
       <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
       <title>MMBob</title>
    </head>
    <body>

    <mm:compare referid="forumid" value="unknown">
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
            <tr><th>MMBob system error <mm:write referid="forumalias" /></th></tr>
            <tr><td height="40"><b>ERROR: </b> No forum id is provided, if this is a new install try <a href="forums.jsp">forums.jsp</a> instead to create a new forum.</td></tr>
        </table>
    </mm:compare>

    <mm:compare referid="forumid" value="unknown" inverse="true">
    <!-- login part -->
      <%@ include file="getposterid.jsp" %>
    <!-- end login part -->

    <mm:locale language="$lang">
    <%@ include file="loadtranslations.jsp" %>

    <!-- action check -->
    <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

    <div class="header">
        <mm:function set="mmbob" name="getForumHeaderPath" referids="forumid">
            <jsp:include page="${_}"/>
        </mm:function>
    </div>

    <div class="bodypart">

        <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid" id="frm">
            <mm:import id="loginsystemtype"><mm:field name="loginsystemtype" /></mm:import>
            <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
            <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
            <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
            <mm:include page="path.jsp?type=index" referids="logoutmodetype,forumid,posterid,active_nick" />


            <%--forum header block--%>
            <table cellpadding="0" cellspacing="0" class="list"  style="margin-top : 10px;" width="95%">
                <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
                <tr>
                    <%-- login secion (http, entree, entree-ng) --%>
                    <mm:compare referid="posterid" value="-1">
                        <mm:compare referid="loginsystemtype" value="http">
                            <th width="100">
                                <mm:field name="accountcreationtype">
                                    <mm:compare value="open"><a href="newposter.jsp?forumid=${forumid}"><mm:write referid="image_guest"/></a></mm:compare>
                                </mm:field>
                            </th>

                            <td align="left">
                                <form action="login.jsp?forumid=${forumid}" method="post">
                                    <mm:present referid="loginfailed">
                                        <br />
                                        <center>
                                            <h4>
                                                <mm:write referid="loginfailedreason">
                                                    <mm:compare value="account blocked"> ** <mm:write referid="mlg.Account_disabled"/> ** </mm:compare>
                                                    <mm:compare value="account not valid"> ** <mm:write referid="mlg.Account_not_found" /> ** </mm:compare>
                                                    <mm:compare value="password not valid"> ** <mm:write referid="mlg.Wrong_password" /> ** </mm:compare>
                                                </mm:write>
                                            </h4>
                                        </center>
                                    </mm:present>

                                    <mm:notpresent referid="loginfailed">
                                        <h4><mm:write referid="mlg.Welcome" /> <mm:write referid="mlg.on_the" /> <mm:field name="name" /> <mm:write referid="mlg.forum" /> !</h4>
                                        <p />
                                            <b><mm:write referid="mlg.login" /></b>
                                        <p />
                                    </mm:notpresent>
                                    <center>
                                        <a href="remail.jsp?forumid=${forumid}"><mm:write referid="mlg.forgot_your_password" /></a>
                                    </center>
                                    <p />
                                    <mm:write referid="mlg.account" /> : <input size="12" name="account"/><br />
                                    <mm:write referid="mlg.password" /> : <input size="12" type="password" name="password"/>
                                    <input type="submit" value="${mlg.login}" />
                                </form>
                                <p />
                            </td>
                        </mm:compare>

                        <mm:compare referid="loginsystemtype" value="entree">
                            <th width="100"> </th>
                            <td align="left">
                                <form action="entree.jsp?forumid=${forumid}" method="post">
                                    <mm:present referid="loginfailed">
                                        <br />
                                        <center>
                                            <h4>
                                                <mm:write referid="loginfailedreason">
                                                    <mm:compare value="account blocked"> ** <mm:write referid="mlg.Account_disabled"/> ** </mm:compare>
                                                    <mm:compare value="account not valid"> <center> ** Geen lid van dit forum ** </center> </mm:compare>
                                                    <mm:compare value="password not valid"> ** <mm:write referid="mlg.Wrong_password" /> ** </mm:compare>
                                                </mm:write>
                                            </h4>
                                        </center>
                                    </mm:present>

                                    <mm:notpresent referid="loginfailed"> </mm:notpresent>
                                    <center><input type="submit" value="Inloggen via Entree" /></center>
                                </form>
                                <p />
                            </td>
                        </mm:compare>


                        <mm:compare referid="loginsystemtype" value="entree-ng">
                            <th width="100"> </th>
                            <td align="left">
                                <form action="login/entree-ng.jsp?forumid=${forumid}" method="post">
                                    <mm:present referid="loginfailed">
                                        <br />
                                        <center>
                                            <h4>
                                                <mm:write referid="loginfailedreason">
                                                    <mm:compare value="account blocked"> ** <mm:write referid="mlg.Account_disabled"/> ** </mm:compare>
                                                    <mm:compare value="account not valid"> <center> ** Geen lid van dit forum ** </center> </mm:compare>
                                                    <mm:compare value="password not valid"> ** <mm:write referid="mlg.Wrong_password" /> ** </mm:compare>
                                                </mm:write>
                                            </h4>
                                        </center>
                                    </mm:present>

                                    <mm:notpresent referid="loginfailed"> </mm:notpresent>
                                    <center><input type="submit" value="Inloggen via EntreeNG" /></center>
                                </form>
                                <p />
                            </td>
                        </mm:compare>
                    </mm:compare>
                    <%-- end login secion (http, entree, entree-ng) --%>

                    <%--  you are logged in, user info--%>
                    <mm:compare referid="posterid" value="-1" inverse="true">
                        <th width="100">
                            <a href="profile.jsp?forumid=${forumid}&posterid=${posterid}">
                                <mm:field name="active_nick" /><br />
                                <mm:field name="active_avatar">
                                    <mm:compare value="-1" inverse="true">
                                        <mm:node number="$_"><img src="<mm:image template="s(80x80)" />" width="80" border="0"></mm:node>
                                    </mm:compare>
                                </mm:field>
                            </a>

                            <mm:compare referid="logoutmodetype" value="open">
                                  <a href="logout.jsp?forumid=${forumid}"><mm:write referid="mlg.Logout" /></a>
                            </mm:compare>
                        </th>

                        <td align="left" valign="top">
                            <mm:compare referid="image_logo" value="" inverse="true">
                                <center> <img src="${image_logo}" width="98%"> </center>
                            </mm:compare>

                            <mm:compare referid="image_logo" value="">
                                <h4>
                                    <mm:write referid="mlg.Welcome" />  <mm:field name="active_firstname" />  <mm:field name="active_lastname" /> (<mm:field name="active_nick" />)
                                    <br />
                                    <mm:write referid="mlg.on_the" />  <mm:field name="name" />  <mm:write referid="mlg.forum" /> !
                                </h4>
                                <p />
                            </mm:compare>

                            <mm:write referid="mlg.last_time_logged_in" /> :
                            <mm:field name="active_lastseen">
                                <mm:compare value="" inverse="true">
                                    <mm:field name="active_lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                </mm:compare>
                            </mm:field>

                            <br />
                            <mm:write referid="mlg.member_since" /> :
                            <mm:field name="active_firstlogin">
                                <mm:compare value="" inverse="true">
                                    <mm:field name="active_firstlogin"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                </mm:compare>
                            </mm:field>
                            <br />

                            <mm:field name="privatemessagesenabled">
                                <mm:compare value="true"><mm:write referid="mlg.number_of_messages" /> : <mm:field name="active_postcount" /></mm:compare>
                            </mm:field>

                            <%-- TODO: not yet implemented
                            <mm:write referid="mlg.Level" /> : <mm:field name="active_level" />--%>

                            <p>
                            <br />
                            <mm:field name="privatemessagesenabled">
                                <mm:compare value="true">
                                    <mm:nodefunction set="mmbob" name="getMailboxInfo" referids="forumid,posterid">
                                        <mm:param name="mailboxid" value="Inbox" />
                                        <b><mm:write referid="mlg.you_have"/>
                                        <mm:field name="messagecount">
                                            <mm:compare value="">
                                                0 <a href="privatemessages.jsp?forumid=${forumid}"><mm:write referid="mlg.private_messages"/></a>
                                            </mm:compare>

                                            <mm:compare value="" inverse="true">
                                                <mm:field id="messagecount" name="messagecount" />
                                                <a href="privatemessages.jsp?forumid=${forumid}">
                                                    <mm:compare referid="messagecount" value="1"> <mm:write referid="mlg.private_message"/> </mm:compare>
                                                    <mm:compare referid="messagecount" value="1" inverse="true"> <mm:write referid="mlg.private_messages"/> </mm:compare>
                                                </a>
                                                (<mm:field name="messagenewcount" /> <mm:write referid="mlg.new"/> <mm:write referid="mlg.and"/> <mm:field name="messageunreadcount" /> <mm:write referid="mlg.unread"/>)
                                            </mm:compare>
                                        </mm:field>
                                        </b>
                                    </mm:nodefunction>
                                </mm:compare>
                            </mm:field>

                            <h4>
                                <mm:write referid="mlg.At_the_moment" /> : <mm:field id="postersonline" name="postersonline" />
                                <mm:compare referid="postersonline" value="1"> <mm:write referid="mlg.member" /> </mm:compare>
                                <mm:compare referid="postersonline" value="1" inverse="true"><mm:write referid="mlg.members" /> </mm:compare>
                                <mm:write referid="mlg.online" />.
                            </h4>
                            </p>
                        </td>
                    </mm:compare>
                    <%--  end you are logged in, user info--%>


                    <%--  forum statistics--%>
                    <th width="250" align="left" valign="top">
                        <b><mm:write referid="mlg.Areas" /></b> : <mm:field name="postareacount" /> <b><mm:write referid="mlg.Topics" /></b> : <mm:field name="postthreadcount" /><br />
                        <b><mm:write referid="mlg.Messages" /></b> : <mm:field name="postcount" /> <b><mm:write referid="mlg.Views" /> </b> : <mm:field name="viewcount" /><br />
                        <b><mm:write referid="mlg.Members" /></b> : <mm:field name="posterstotal" /> <b><mm:write referid="mlg.New" /></b> : <mm:field name="postersnew" /> <b><mm:write referid="mlg.Online"/></b> : <mm:field name="postersonline" /><p />
                        <b><mm:write referid="mlg.Last_posting"/></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg.by"/> <mm:field name="lastposter" /> '<mm:field name="lastsubject" />'</mm:compare><mm:compare value="-1"><mm:write referid="mlg.no_messages"/></mm:compare></mm:field>
                    </th>
                    <%--  end forum statistics--%>
                </tr>
            </table>
        </mm:nodefunction>
        <%--  end of forum header block--%>


        <%--  options menu--%>
        <table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
          <tr>
               <td align="right">
                    <mm:compare referid="posterid" value="-1" inverse="true">
                        <mm:link page="profile.jsp" referids="forumid,posterid">
                        <a href="${_}"><mm:write referid="mlg.Profile_settings" /></a> |
                        </mm:link>
                    </mm:compare>
                    <mm:node referid="forumid">
                        <mm:relatednodes type="forumrules">
                            <mm:link page="rules.jsp" referids="forumid">
                                <mm:param name="rulesid"><mm:field name="number" /></mm:param>
                                <a href="${_}"><mm:write referid="mlg.Forum_rules" /></a> |
                            </mm:link>
                        </mm:relatednodes>
                    </mm:node>
                    <mm:link page="moderatorteam.jsp" referids="forumid" >
                        <a href="${_}"><mm:write referid="mlg.The_moderator_team" /></a> |
                    </mm:link>
                    <mm:link page="onlineposters.jsp" referids="forumid" >
                        <a href="${_}"><mm:write referid="mlg.Members_online" /></a> |
                    </mm:link>
                    <mm:link page="allposters.jsp" referids="forumid" >
                        <a href="${_}"><mm:write referid="mlg.All_members" /></a> |
                    </mm:link>
                    <mm:link page="bookmarked.jsp" referids="forumid" >
                        <a href="${_}"><mm:write referid="mlg.bookmarked"/></a> |
                    </mm:link>
                    <mm:link page="search.jsp" referids="forumid" >
                        <a href="${_}"><mm:write referid="mlg.Search" /></a>
                    </mm:link>
               </td>
          </tr>
        </table>
        <%--  end of options menu--%>


        <%--  show post areas as tree--%>
        <mm:compare referid="navigationmethod" value="tree">
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
                <tr>
                    <th><mm:write referid="mlg.area_name" /></th>
                    <th><mm:write referid="mlg.topics" /></th>
                    <th><mm:write referid="mlg.messages" /></th>
                    <th><mm:write referid="mlg.views" /></th>
                    <th><mm:write referid="mlg.last_posting" /></th>
                </tr>
                <mm:import externid="tree">root</mm:import>
                <mm:compare referid="tree" value="root" inverse="true">
                    <tr><th colspan="5" align="left" height="25"><mm:write referid="tree" /></th></tr>
                </mm:compare>

                <mm:nodelistfunction set="mmbob" name="getTreePostAreas" referids="forumid,posterid,tree" id="tpa">
                    <mm:field name="nodetype">
                        <mm:compare value="area">
                            <mm:import id="guestreadmodetype" reset="true"><mm:field name="guestreadmodetype" /></mm:import>
                            <mm:compare referid="posterid" value="-1" inverse="true">
                                <mm:import id="guestreadmodetype" reset="true">open</mm:import>
                            </mm:compare>
                            <mm:compare referid="guestreadmodetype" value="open">
                                <tr>
                                    <td align="left">
                                        <a href="postarea.jsp?forumid=${forumid}&postareaid=${tpa.id}"><mm:field name="shortname" /></a>
                                        <p/>
                                        <mm:field name="description" />
                                        <p />
                                        <mm:write referid="mlg.Moderators" /> : <mm:field name="moderators" />
                                        <p />
                                    </td>
                                    <td><mm:field name="postthreadcount" /></td>
                                    <td><mm:field name="postcount" /></td>
                                    <td><mm:field name="viewcount" /></td>
                                    <td align="left" valign="top">
                                        <mm:field name="lastposttime">
                                            <mm:compare value="-1" inverse="true">
                                                <mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                                <mm:write referid="mlg.by" />
                                                <mm:field name="lastposternumber">
                                                    <mm:compare value="-1" inverse="true">
                                                        <a href="profile.jsp?forumid=${forumid}&posterid=${tpa.lastposternumber}"><mm:field name="lastposter" /></a>
                                                    </mm:compare>
                                                    <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
                                                </mm:field>
                                                <p />
                                                <mm:field name="lastsubject" />
                                            </mm:compare>
                                            <mm:compare value="-1"><mm:write referid="mlg.no_messages" /></mm:compare>
                                        </mm:field>
                                        <mm:field name="lastpostthreadnumber">
                                            <mm:compare value="-1" inverse="true">
                                                <a href="thread.jsp?forumid=${forumid}&postareaid=${tpa.id}&postthreadid=${tpa.lastposternumber}&page=-1#reply">&gt;</a>
                                            </mm:compare>
                                        </mm:field>
                                    </td>
                                </tr>
                            </mm:compare>
                        </mm:compare>
                        <mm:compare value="subarea">
                            <tr>
                                <th colspan="5" align="left" height="25">
                                    <mm:link page="index.jsp" referids="forumid">
                                        <mm:param name="tree"><mm:field name="name" /></mm:param>
                                        <a href="${_}"> <mm:field name="name" /></a>
                                    </mm:link>
                                    &nbsp;&nbsp;&nbsp;( <mm:field name="areacount" /> <mm:write referid="mlg.Areas" />, <mm:field name="postthreadcount" /> <mm:write referid="mlg.topics" />, <mm:field name="postcount" /> <mm:write referid="mlg.messages" />, <mm:field name="viewcount" /> <mm:write referid="mlg.views" /> )
                                </th>
                            </tr>
                        </mm:compare>
                    </mm:field>
                </mm:nodelistfunction>
            </table>
        </mm:compare>
        <%--  end show post areas as tree--%>


        <%--  show post areas as list--%>
        <mm:compare referid="navigationmethod" value="list">
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
                <tr>
                    <th><mm:write referid="mlg.area_name" /></th>
                    <th><mm:write referid="mlg.topics" /></th>
                    <th><mm:write referid="mlg.messages" /></th>
                    <th><mm:write referid="mlg.views" /></th>
                    <th><mm:write referid="mlg.last_posting" />
                    </th>
                </tr>
                <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
                    <mm:import id="guestreadmodetype" reset="true"><mm:field name="guestreadmodetype" /></mm:import>
                    <mm:compare referid="posterid" value="-1" inverse="true">
                        <mm:import id="guestreadmodetype" reset="true">open</mm:import>
                    </mm:compare>
                    <mm:compare referid="guestreadmodetype" value="open">
                    <tr>
                        <td align="left">
                            <a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="name" /></a>
                            <p/>
                            <mm:field name="description" />
                            <p />
                            <mm:write referid="mlg.Moderators" /> : <mm:field name="moderators" />
                            <p />
                         </td>
                        <td><mm:field name="postthreadcount" /></td>
                        <td><mm:field name="postcount" /></td>
                        <td><mm:field name="viewcount" /></td>
                        <td align="left" valign="top">
                            <mm:field name="lastposttime">
                                <mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg.by" />
                                    <mm:field name="lastposternumber">
                                        <mm:compare value="-1" inverse="true">
                                            <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
                                        </mm:compare>
                                        <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
                                    </mm:field>
                                    <p />
                                    <mm:field name="lastsubject" />
                                </mm:compare>
                                <mm:compare value="-1"><mm:write referid="mlg.no_messages" /></mm:compare>
                            </mm:field>
                            <mm:field name="lastpostthreadnumber">
                                <mm:compare value="-1" inverse="true">
                                    <mm:link page="thread.jsp" referids="forumid" escape="text/xml">
                                        <mm:param name="postareaid" ><mm:field name="id" /></mm:param>
                                        <mm:param name="postthreadid" ><mm:field name="lastpostthreadnumber" /></mm:param>
                                        <mm:param name="page" value="-1" />
                                        <a href="${_}#reply">&gt;</a>
                                        </mm:link>
                                    </mm:compare>
                            </mm:field>
                        </td>
                    </tr>
                    </mm:compare>
                </mm:nodelistfunction>
            </table>
        </mm:compare>
        <%--  end show post areas as list--%>

        <%-- admin options for forums--%>
        <mm:compare referid="adminmode" value="true">
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                <tr>
                    <th align="left"><mm:write referid="mlg.Admin_tasks" /></th>
                </tr>
                <tr>
                    <td align="left">
                        <p />
                        <mm:link page="admin/changeforum.jsp" referids="forumid">
                            <a href="${_}"><mm:write referid="mlg.change_forum"/></a><br/>
                         </mm:link>

                         <mm:link page="admin/newadministrator.jsp" referids="forumid">
                            <a href="${_}"><mm:write referid="mlg.Add_administrator"/></a><br/>
                         </mm:link>

                         <mm:link page="admin/removeadministrator.jsp" referids="forumid">
                            <a href="${_}"><mm:write referid="mlg.Remove_administrator"/></a><br/>
                         </mm:link>

                         <mm:link page="admin/newpostarea.jsp" referids="forumid">
                            <a href="${_}"><mm:write referid="mlg.add_new_area"/></a><br/>
                         </mm:link>

                        <mm:link page="admin/profiles.jsp" referids="forumid">
                            <a href="${_}"><mm:write referid="mlg.Profile_management"/></a><br/>
                         </mm:link>

                        <p />
                    </td>
                </tr>
            </table>
        </mm:compare>
        <%-- end admin options for forums--%>

    </div>
    <div class="footer">
        <mm:function set="mmbob" name="getForumFooterPath" referids="forumid">
            <jsp:include page="${_}"/>
        </mm:function>
    </div>

    </mm:locale>
    </mm:compare>

    </body>
    </html>

    </mm:content>
</mm:cloud>
