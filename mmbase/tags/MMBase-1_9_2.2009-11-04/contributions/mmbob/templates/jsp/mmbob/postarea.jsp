<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
    <mm:import externid="forumid" />
    <mm:import externid="postareaid" />

    <%--check if post area exists, if not, go back to forum overview--%>
    <mm:node notfound="skipbody" number="${postareaid}">
        <mm:nodeinfo type="type">
            <mm:compare value="postareas">
                <mm:import id="test">true</mm:import>
            </mm:compare>
        </mm:nodeinfo>
    </mm:node>

    <mm:notpresent referid="test">
        <jsp:forward page="index.jsp">
            <jsp:param name="forumid" value="${forumid}"/>
        </jsp:forward>
    </mm:notpresent>

    <%@ include file="thememanager/loadvars.jsp" %>
    <html>
    <head>
       <link rel="stylesheet" type="text/css" href="${style_default}" />
       <title>MMBase Forum</title>
    </head>
    <body>

    <mm:import externid="adminmode">false</mm:import>


    <mm:import externid="page">1</mm:import>

    <!-- login part -->
    <%@ include file="getposterid.jsp" %>

    <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
      <mm:field name="postingsperpage" id="pagesize" write="false"/>
    </mm:nodefunction>

    <mm:notpresent referid="pagesize">
        <mm:import id="pagesize">10</mm:import>
    </mm:notpresent>


    <!-- action check -->
    <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

    <mm:locale language="$lang">
        <%@ include file="loadtranslations.jsp" %>

        <div class="header">
            <mm:function set="mmbob" name="getForumHeaderPath" referids="forumid">
                <jsp:include page="${_}"/>
            </mm:function>
        </div>

        <div class="bodypart">

            <%--  show the path--%>
            <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
                <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
                <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
                <mm:include page="path.jsp?type=postarea" referids="logoutmodetype,forumid,posterid,active_nick" />
            </mm:nodefunction>
            <%--  end show the path--%>

            <%--  post area information--%>
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                <tr>
                    <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
                        <mm:import id="guestwritemodetype"><mm:field name="guestwritemodetype" /></mm:import>
                        <mm:import id="threadstartlevel"><mm:field name="threadstartlevel" /></mm:import>
                        <mm:compare referid="posterid" value="-1" inverse="true">
                            <mm:import id="guestwritemodetype" reset="true">open</mm:import>
                        </mm:compare>
                        <mm:import id="navline"><mm:field name="navline" /></mm:import>
                        <mm:import id="pagecount"><mm:field name="pagecount" /></mm:import>
                        <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
                        <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
                        <th colspan="2" align="left">
                            <mm:compare referid="image_logo" value="" inverse="true">
                                <center><img src="<mm:write referid="image_logo" />" width="60%" ></center>
                                <br />
                            </mm:compare>
                            <b><mm:write referid="mlg.Area_name"/></b> : <mm:field name="name" />
                            <b><mm:write referid="mlg.Topics"/></b> : <mm:field name="postthreadcount" />
                            <b><mm:write referid="mlg.Messages"/></b> : <mm:field name="postcount" />
                            <b><mm:write referid="mlg.Views"/></b> : <mm:field name="viewcount" /><br />
                            <b><mm:write referid="mlg.Last_posting"/></b> :
                            <mm:field name="lastposttime">
                                <mm:compare value="-1" inverse="true">
                                    <mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                    <b><mm:write referid="mlg.by"/></b> <mm:field name="lastposter" /> <b> : '</b><mm:field name="lastsubject" /><b>'</b>
                                </mm:compare>
                                <mm:compare value="-1">
                                    <mm:write referid="mlg.no_messages"/>
                                </mm:compare>
                            </mm:field>
                            <br />
                        </mm:nodefunction>
                        <br />
                        <b><mm:write referid="mlg.Moderators"/></b> :
                        <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
                            <mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
                        </mm:nodelistfunction>
                    </th>
                </tr>
            </table>
            <%--  end post area information--%>

            <%--  quick pick area and context menu--%>
            <table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
                <tr>
                    <td align="left" />
                        <form action="postarea.jsp?forumid=${forumid}" method="post">
                            <b><mm:write referid="mlg.Area_name"/></b>
                            <select name="postareaid" onChange="submit()">
                                <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid" id="gpa">
                                    <c:if test="${gpa.id == postareaid}"><c:set var="selected">selected</c:set></c:if>
                                    <option value="${gpa.id}" ${selected} ><mm:field name="name" /></option>
                                    <c:remove var="selected" />
                                </mm:nodelistfunction>
                            </select>
                        </form>
                    </td>
                    <td align="right">
                        <a href="bookmarked.jsp?forumid=${forumid}">Bookmarked</a> |
                        <mm:link page="search.jsp" referids="forumid,postareaid">
                            <a href="${_}"><mm:write referid="mlg.Search" /></a>&nbsp;
                        </mm:link>
                    </td>
                </tr>
            </table>
            <%--  end quick pick area and context menu--%>

            <%--  the table with all the post areas--%>
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
                <tr>
                    <th width="15" class="state">&nbsp;</th>
                    <th width="15">&nbsp;</th>
                    <th><mm:write referid="mlg.topic"/></th>
                    <th><mm:write referid="mlg.author"/></th>
                    <th><mm:write referid="mlg.replies"/></th>
                    <th><mm:write referid="mlg.views"/></th>
                    <th><mm:write referid="mlg.last_posting"/></th>
                    <mm:compare referid="ismoderator" value="true">
                        <th><mm:write referid="mlg.moderator"/></th>
                    </mm:compare>
                </tr>

                <mm:nodelistfunction set="mmbob" name="getPostThreads" referids="forumid,postareaid,posterid,page,pagesize" id="pt">
                    <tr>
                        <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td>
                        <td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td>
                        <td align="left">
                            <a href="thread.jsp?forumid=${forumid}&postareaid=${postareaid}&postthreadid=${pt.id}"> <mm:field name="shortname" /> </a>
                            <mm:field name="navline" />
                            <mm:field name="emailonchange">
                                <mm:compare value="true">[email]</mm:compare>
                            </mm:field>
                            <mm:field name="bookmarked">
                                <mm:compare value="true">[bookmarked]</mm:compare>
                            </mm:field></td>
                        <td align="left"><mm:field name="creator" /></td>
                        <td align="left"><mm:field name="replycount" /></td>
                        <td align="left"><mm:field name="viewcount" /></td>
                        <td align="left">
                            <mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                            <mm:write referid="mlg.by"/>
                            <mm:field name="lastposternumber">
                                <mm:compare value="-1" inverse="true">
                                    <a href="profile.jsp?forumid=${forumid}&posterid=${pt.lastposternumber}"><mm:field name="lastposter" /></a>
                                </mm:compare>
                                <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
                            </mm:field>
                            <a href="thread.jsp?forumid=${forumid}&postareaid=${postareaid}&postthreadid=${pt.id}&page=${pt.pagecount}#p${lastpostnumber}">&gt;</a>
                        </td>

                        <mm:compare referid="ismoderator" value="true">
                            <td>
                                <a href="removepostthread.jsp?forumid=${forumid}&postareaid=${postareaid}&postthreadid=${pt.id}">X</a> /
                                <a href="editpostthread.jsp?forumid=${forumid}&postareaid=${postareaid}&postthreadid=${pt.id}">E</a> /
                                <a href="movepostthread.jsp?forumid=${forumid}&postareaid=${postareaid}&postthreadid=${pt.id}">M</a>
                            </td>
                        </mm:compare>
                    </tr>
                </mm:nodelistfunction>
            </table>
            <%--  end the table with all the post areas--%>

            <%--  page navigation--%>
            <mm:compare referid="pagecount" value="1" inverse="true">
                <mm:compare referid="pagecount" value="0" inverse="true">
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" border="0" width="95%">
                        <tr>
                            <td align="left"> <mm:write referid="mlg.Pages"/> : <mm:write referid="navline" /></td>
                        </tr>
                    </table>
                </mm:compare>
            </mm:compare>
            <%--  end page navigation--%>

            <%--  can this user add a forum post? if yes: show button--%>
            <table cellpadding="0" cellspacing="0" style="margin-top : 5px;" width="95%">
                    <tr>
                        <td align="left">
                        <mm:compare referid="threadstartlevel" value="">
                            <mm:compare referid="guestwritemodetype" value="open">
                                <a href="newpost.jsp?forumid=${forumid}&postareaid=${postareaid}"><img src="<mm:write referid="image_newmsg" />" border="0" /></a>
                            </mm:compare>
                        </mm:compare>

                        <mm:compare referid="threadstartlevel" value="moderator">
                            <mm:compare referid="ismoderator" value="true">
                                <a href="newpost.jsp?forumid=${forumid}&postareaid=${postareaid}"><img src="<mm:write referid="image_newmsg" />" border="0" /></a>
                            </mm:compare>
                        </mm:compare>
                        </td>
                    </tr>
            </table>
            <%--  end can this user add a forum post? if yes: show button--%>

            <br />
            <table>
                <tr>
                    <td valign="top" width="50%">
                        <%--  icons explained--%>
                        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-left : 30px" align="left">
                            <tr>
                                <td align="left">
                                    <br />
                                    <mm:write referid="image_state_normal" /> <mm:write referid="mlg.open_topic"/><p />
                                    <mm:write referid="image_state_normalnew" /> <mm:write referid="mlg.open_topic_unread"/><p />
                                    <mm:write referid="image_state_hot" /> <mm:write referid="mlg.open_topic_popular"/><p />
                                    <mm:write referid="image_state_hotnew" /> <mm:write referid="mlg.open_topic_popular_unread"/><p />
                                    <mm:write referid="image_state_pinned" /> <mm:write referid="mlg.pinned_topic"/><p />
                                    <mm:write referid="image_state_closed" /> <mm:write referid="mlg.closed_topic"/><p />
                                    <mm:write referid="image_state_pinnedclosed" /> <mm:write referid="mlg.pinnedclosed_topic"/><p />
                                    <mm:write referid="image_state_normalme" /> <mm:write referid="mlg.topic_to_which_you_have_contributed"/><p />
                                </td>
                            </tr>
                        </table>
                        <%--  end icons explained--%>
                    </td>
                    <td valign="top" width="50%">
                        <%--  overview rights current user--%>
                        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-left : 30px" align="left">
                            <tr>
                                <td align="left">
                            <br />
                            <mm:compare referid="threadstartlevel" value="">
                                <mm:write referid="mlg.You_may_post_new_threads"/> <p />
                            </mm:compare>

                            <mm:compare referid="threadstartlevel" value="all">
                                <mm:write referid="mlg.You_may_post_new_threads"/> <p />
                            </mm:compare>

                            <mm:compare referid="threadstartlevel" value="moderator">
                                <mm:compare referid="ismoderator" value="true">
                                    <mm:write referid="mlg.You_may_post_new_threads"/> <p />
                                </mm:compare>
                                <mm:compare referid="ismoderator" value="false">
                                    <mm:write referid="mlg.You_may_not_post_new_threads"/> <p />
                                </mm:compare>
                            </mm:compare>
                            <mm:write referid="mlg.You_may_post_new_replies"/> <p />
                            <mm:write referid="mlg.You_may_edit_your_posts"/> <p />
                            <mm:write referid="mlg.You_may_delete_your_posts"/> <p />
                            </td></tr>
                        </table>
                        <%--  end overview rights current user--%>
                    </td>
                </tr>
            </table>

            <%--  administrator tasks--%>
            <mm:compare referid="isadministrator" value="true">
                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;margin-bottom : 20px;" width="95%">
                    <tr>
                        <th align="left"><mm:write referid="mlg.Admin_tasks"/></th>
                    </tr>

                    <tr>
                        <td align="left">
                        <p />
                        <mm:link page="admin/changepostarea.jsp" referids="forumid,postareaid">
                            <a href="${_}"><mm:write referid="mlg.change_area"/></a><br />
                        </mm:link>

                        <mm:link page="admin/removepostarea.jsp" referids="forumid,postareaid" >
                            <a href="${_}"><mm:write referid="mlg.remove_area"/></a><br />
                        </mm:link>

                        <mm:link page="admin/newmoderator.jsp" referids="forumid,postareaid">
                            <a href="${_}"><mm:write referid="mlg.add_moderator"/></a><br />
                        </mm:link>

                        <mm:link page="admin/removemoderator.jsp" referids="forumid,postareaid">
                            <a href="${_}"><mm:write referid="mlg.remove_moderator"/></a><br />
                        </mm:link>
                    </td>
                </tr>
            </table>
            <br />
            </mm:compare>
            <%--  end administrator tasks--%>
        </div>

        <div class="footer">
            <mm:function set="mmbob" name="getForumFooterPath" referids="forumid">
                <jsp:include page="${_}"/>
            </mm:function>
        </div>

        </body>
        </html>

        </mm:locale>
    </mm:content>
</mm:cloud>
