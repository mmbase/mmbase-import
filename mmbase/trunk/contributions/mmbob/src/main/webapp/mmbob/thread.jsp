<%--  show a post thread--%>
<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:import externid="forumid" />
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
    <mm:import externid="page">1</mm:import>
    <mm:import externid="postingid" />

    <%--check if post thread exists, if not, go back to postarea overview--%>
    <mm:node notfound="skipbody" number="${postthreadid}">
        <mm:nodeinfo type="type">
            <mm:compare value="postthreads">
                <mm:import id="test">true</mm:import>
            </mm:compare>
        </mm:nodeinfo>
    </mm:node>

    <mm:notpresent referid="test">
        <jsp:forward page="postarea.jsp">
            <jsp:param name="forumid" value="${forumid}"/>
            <jsp:param name="postareaid" value="${postareaid}"/>
        </jsp:forward>
    </mm:notpresent>

    <%@ include file="thememanager/loadvars.jsp" %>

    <html>
        <head>
           <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
           <title>MMBob</title>
           <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
        </head>
        <body>

        <!-- login part -->
        <%@ include file="getposterid.jsp" %>
        <!-- end login part -->

        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
            <mm:field name="postingsperpage" id="pagesize" write="false"/>
            <mm:field name="replyoneachpage" id="replyoneachpage" write="false"/>
        </mm:nodefunction>

        <%--  TODO: hardcode this value in a template ???--%>
        <mm:notpresent referid="pagesize"><mm:import id="pagesize">20</mm:import></mm:notpresent>

        <mm:present referid="postingid">
            <mm:import id="page" reset="true"><mm:function set="mmbob" name="getPostingPageNumber" referids="forumid,postareaid,postthreadid,postingid,pagesize" /></mm:import>
        </mm:present>

        <!-- action check -->
        <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

        <mm:nodefunction set="mmbob" name="getPostThreadInfo" referids="forumid,postareaid,postthreadid,pagesize">
            <mm:compare referid="page" value="-1">
                <mm:import id="page" reset="true"><mm:field name="pagecount" /></mm:import>
            </mm:compare>
            <mm:import id="threadstate"><mm:field name="threadstate" /></mm:import>
            <mm:import id="threadmood"><mm:field name="threadmood" /></mm:import>
            <mm:import id="threadtype"><mm:field name="threadtype" /></mm:import>
        </mm:nodefunction>

        <%--Check if the poster is an moderator --%>
        <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
            <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
            <mm:import id="guestwritemodetype"><mm:field name="guestwritemodetype" /></mm:import>
            <mm:import id="smileysenabled"><mm:field name="smileysenabled" /></mm:import>
            <mm:import id="privatemessagesenabled"><mm:field name="privatemessagesenabled" /></mm:import>
            <mm:compare referid="posterid" value="-1" inverse="true">
                <mm:import id="guestwritemodetype" reset="true">open</mm:import>
            </mm:compare>
        </mm:nodefunction>

        <%-- reset the threadstate if the poster is a moderator --%>
        <mm:compare referid="ismoderator" value="true">
            <mm:import reset="true" id="threadstate">normal</mm:import>
        </mm:compare>

        <mm:locale language="$lang">
            <%@ include file="loadtranslations.jsp" %>
            <div class="header">
                <mm:function set="mmbob" name="getForumHeaderPath" referids="forumid">
                    <jsp:include page="${_}"/>
                </mm:function>
            </div>

            <%-- show the path--%>
            <div class="bodypart">
                <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
                    <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
                    <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
                    <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
                    <mm:import id="active_firstname"><mm:field name="active_firstname" /></mm:import>
                    <mm:import id="active_lastname"><mm:field name="active_lastname" /></mm:import>
                    <mm:include page="path.jsp?type=postthread" referids="logoutmodetype,posterid,forumid,active_nick" />
                </mm:nodefunction>

                <mm:compare referid="image_logo" value="" inverse="true">
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr>
                            <th colspan="2" align="left">
                                <center><img src="${image_logo}" width="60%" ></center>
                            </th>
                        </tr>
                    </table>
                </mm:compare>

                <%--  quick pick of forum area--%>
                <table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
                    <tr>
                    <form action="thread.jsp?forumid=${forumid}" method="post">
                        <b><mm:write referid="mlg.Area_name"/>:</b>
                        <select name="postareaid" onChange="submit()">
                            <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid" id="gpa">
                                <c:if test="${gpa.id == postareaid}"><c:set var="selected">selected</c:set></c:if>
                                <option value="${gpa.id}" ${selected} ><mm:field name="name" /></option>
                            </mm:nodelistfunction>
                        </select>
                    </form>
                    </tr>
                </table>

                <%--  paging navigation--%>
                <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
                    <mm:import id="lastpage"><mm:field name="lastpage" /></mm:import>
                    <table cellpadding="0" cellspacing="0" style="margin-top : 4px;" width="95%">
                        <tr>
                            <td align="left">
                                <b> <mm:write referid="mlg.Pages"/> (<mm:field name="pagecount" id="pagecount" />) <mm:field name="navline" /> </b>
                            </td>
                            <td align="right">
                                <a href="<mm:field name="emailonchange"><mm:compare value="false"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">threademailon</mm:param></mm:url>">Email : <mm:write referid="mlg.off" /></a></mm:compare><mm:compare value="true"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">threademailoff</mm:param></mm:url>">Email : <mm:write referid="mlg.on" /></a></mm:compare></mm:field> |
                                <a href="<mm:field name="bookmarked"><mm:compare value="false"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">bookmarkedon</mm:param></mm:url>">Bookmarked : <mm:write referid="mlg.off" /></a></mm:compare><mm:compare value="true"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">bookmarkedoff</mm:param></mm:url>">Bookmarked : <mm:write referid="mlg.on" /></a></mm:compare></mm:field> | <a href="<mm:url page="bookmarked.jsp" referids="forumid" />">Bookmarked</a> | <a href="<mm:url page="search.jsp" referids="forumid,postareaid,postthreadid" />"><mm:write referid="mlg.Search" /></a>&nbsp;
                            </td>
                        </tr>
                    </table>
                </mm:nodefunction>

                <%-- show the postings--%>
                <table cellpadding="" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
                    <mm:nodelistfunction set="mmbob" name="getPostings" referids="forumid,postareaid,postthreadid,posterid,page,pagesize,imagecontext" id="gp">
                        <mm:first>
                            <tr align="left">
                                <th width="25%" align="left"><mm:write referid="mlg.Member"/></th>
                                <th align="left"><mm:write referid="mlg.Topic"/>: <mm:field name="subject" /></th>
                            </tr>
                        </mm:first>

                        <tr align="left">
                            <%--  show the date of the posting--%>
                            <td class="${gp.tdvar}" align="left">
                                <a name="${gp.id}"></a>
                                <mm:field name="posttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                            </td>

                            <%--  show all the buttons for possible actions--%>
                            <td class="${gp.tdvar}" align="right">
                                <mm:import id="toid" reset="true"><mm:field name="posterid" /></mm:import>
                                <mm:import id="postingid" reset="true"><mm:field name="id" /></mm:import>
                                <mm:compare referid="guestwritemodetype" value="open">
                                    <mm:compare referid="privatemessagesenabled" value="true">
                                        <mm:link page="newprivatemessage.jsp" referids="forumid,postareaid,postthreadid,postingid,toid" >
                                            <a href="${_}"><img src="<mm:write referid="image_privatemsg" />"  border="0" /></a>
                                        </mm:link>
                                        <mm:link page="newreportmessage.jsp" referids="forumid,postareaid,postthreadid,postingid" >
                                            <a href="${_}"><img src="<mm:write referid="image_reportmsg" />"  border="0" /></a>
                                        </mm:link>
                                    </mm:compare>
                                    <mm:compare referid="threadstate" value="closed" inverse="true">
                                        <mm:compare referid="threadstate" value="pinnedclosed" inverse="true">
                                            <mm:link page="posting.jsp" referids="forumid,postareaid,postthreadid,posterid,pagesize,page,postingid" >
                                                <a href="${_}"><img src="<mm:write referid="image_quotemsg" />"  border="0" /></a>
                                            </mm:link>
                                        </mm:compare>
                                    </mm:compare>
                                </mm:compare>

                                <c:if test="${gp.ismoderator == 'true' || (threadstate != 'pinnedclosed' && gp.isowner == 'true')}">
                                    <mm:link page="editpost.jsp" referids="forumid,postareaid,postthreadid,postingid">
                                        <a href="${_}"><img src="<mm:write referid="image_medit" />"  border="0" /></a>
                                    </mm:link>

                                    <mm:link page="removepost.jsp" referids="forumid,postareaid,postthreadid,postingid">
                                        <a href="${_}"><img src="<mm:write referid="image_mdelete" />"  border="0" /></a>
                                    </mm:link>
                                </c:if>
                            </td>
                        <%--  end show all the buttons for possible actions--%>

                        </tr>
                        <tr>
                            <%--  show all user information--%>
                            <td class="${gp.tdvar}" valign="top" align="left" >
                                <p>
                                    <mm:field name="guest">
                                        <mm:compare value="true"><b><mm:field name="poster" /></b></mm:compare>
                                        <mm:compare value="true" inverse="true">
                                            <mm:link page="profile.jsp" referids="forumid,postareaid,postthreadid">
                                                <mm:param name="posterid" value="${gp.posterid}" />
                                                <mm:param name="type" value="poster_thread" />
                                                    <b> <a href="${_}"><mm:field name="poster" /></b>

                                                    <%--don't show the full name when it is empty--%>
                                                    <mm:import id="fullname" reset="true"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
                                                    <mm:write referid="fullname" >
                                                        <mm:compare value=" " inverse="true"> (<mm:write/>)</mm:compare>
                                                    </mm:write>
                                                    <br />
                                                    <mm:field name="avatar">
                                                      <mm:compare value="-1" inverse="true">
                                                        <mm:node number="${_}">
                                                            <mm:image template="s(80x80)" ><img src="${_}" width="80" border="0"></mm:image>
                                                        </mm:node>
                                                      </mm:compare>
                                                    </mm:field>
                                                </a>
                                            </mm:link>
                                            <p />

                                            <mm:write referid="mlg.Level"/> : <mm:field name="levelgui" /><br /> <img src="<mm:field name="levelimage" />" /><br />
                                            <mm:write referid="mlg.Posts"/> : <mm:field name="accountpostcount" /><br />
                                            <mm:write referid="mlg.Gender"/> : <mm:field name="gender" /><br />
                                            <mm:write referid="mlg.Location"/> : <mm:field name="location" /><br />
                                            <mm:write referid="mlg.Member_since"/> : <mm:field name="firstlogin"><mm:time format="d MMMM  yyyy" /></mm:field><br />
                                            <mm:write referid="mlg.Last_visit"/> : <mm:field name="lastseen"><mm:time format="d/MM/yy HH:mm" /> </mm:field><br />
                                        </mm:compare>
                                    </mm:field>
                                    <br /><br />
                                    </p> user info
                                </td>
                                <%--  endshow all user information--%>

                                <%-- show message edit time, body and signature--%>
                                <td class="${gp.tdvar}" valign="top" align="left">
                                    <mm:field name="edittime">
                                        <mm:compare value="-1" inverse="true">
                                            <mm:write referid="mlg.last_time_edited"/> : <mm:field name="edittime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                        </mm:compare>
                                    </mm:field>
                                    <p />

                                    <mm:field name="body" />
                                    <br />

                                    <mm:field name="signature">
                                        <mm:compare value="" inverse="true">
                                            _______<br />
                                            <mm:field name="signature" escape="p" />
                                        </mm:compare>
                                    </mm:field>
                                    <br />
                                </td>
                                <%-- end show message edit time, body and signature--%>
                            </tr>
                        </mm:nodelistfunction>
                </table>
                <%--end of postings--%>

                <%--  page navigation--%>
                <table cellpadding="0" cellspacing="0" style="margin-top : 2px;" width="95%">
                    <tr><td align="left">
                          <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
                            <b> <mm:write referid="mlg.Pages"/> (<mm:field name="pagecount" />) <mm:field name="navline" /> </b>
                          </mm:nodefunction>
                      </b>
                    </td></tr>
                </table>


                <%--  figure out if the reply box must be shown, and if so, show it.--%>
                <mm:import id="showreply">true</mm:import>
                <mm:compare referid="replyoneachpage" value="false">
                    <mm:compare referid="lastpage" value="false">
                        <mm:import id="showreply" reset="true">false</mm:import>
                    </mm:compare>
                </mm:compare>

                <mm:compare referid="showreply" value="true">
                    <mm:compare referid="threadstate" value="closed" inverse="true">
                        <mm:compare referid="threadstate" value="pinnedclosed" inverse="true">
                            <mm:compare referid="guestwritemodetype" value="open">
                                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
                                    <tr><th colspan="3"><mm:write referid="mlg.Reply"/></th></tr>

                                    <%--  check for errors--%>
                                    <mm:import externid="error" from="session">none</mm:import>
                                    <mm:compare referid="error" value="none" inverse="true">
                                        <tr>
                                            <th colspan="3">
                                                <mm:compare referid="error" value="no_subject">
                                                    <font color="red"><mm:write referid="mlg.problem_missing_topic" /></font>
                                                </mm:compare>

                                                <mm:compare referid="error" value="no_body">
                                                    <font color="red"><mm:write referid="mlg.problem_missing_body" /></font>
                                                </mm:compare>

                                                <mm:compare referid="error" value="maxpostbodysize">
                                                    <font color="red"><mm:write referid="mlg.problem_maxpostbodysize" /></font>
                                                </mm:compare>

                                                <mm:compare referid="error" value="duplicate_post">
                                                    <font color="red"><mm:write referid="mlg.problem_already_posted" /></font>
                                                </mm:compare>

                                                <mm:compare referid="error" value="illegal_html">
                                                    <font color="red"><mm:write referid="mlg.problem_illegal_html" /></font>
                                                </mm:compare>

                                                <mm:compare referid="error" value="speed_posting">
                                                    <mm:import externid="speedposttime" from="session">60</mm:import>
                                                    <font color="red"><mm:write referid="mlg.problem_speedposting" /><mm:write referid="speedposttime" /> sec ***</font>
                                                </mm:compare>
                                            </th>
                                        </tr>
                                    </mm:compare>
                                    <mm:import id="page" reset="true">-1</mm:import>

                                    <mm:link page="thread.jsp" referids="forumid,postareaid,postthreadid,page" >
                                        <form action="${_}#reply" method="post" name="posting">

                                            <%-- poster name--%>
                                            <tr>
                                                <th width="25%"><mm:write referid="mlg.Name"/></th>

                                                <td>
                                                    <mm:compare referid="posterid" value="-1" inverse="true">
                                                        <mm:write referid="active_nick" />
                                                        <input name="poster" type="hidden" value="${active_nick}" >

                                                        <%--don't show the full name when it is empty--%>
                                                        <mm:import id="fullname" reset="true"><mm:write referid="active_firstname" /> <mm:write referid="active_lastname" /></mm:import>
                                                        <mm:write referid="fullname" >
                                                            <mm:compare value=" " inverse="true"> (<mm:write referid="fullname"/>)</mm:compare>
                                                        </mm:write>
                                                    </mm:compare>

                                                    <mm:compare referid="posterid" value="-1">
                                                        <input name="poster" type="hidden" style="width: 99%" value="${mlg.guest}" >
                                                        <mm:write referid="mlg.guest"/>
                                                    </mm:compare>
                                                </td>
                                            </tr>

                                            <%-- message body--%>
                                            <tr>
                                                <th>
                                                    <mm:write referid="mlg.Reply"/>
                                                    <mm:compare referid="smileysenabled" value="true">
                                                        <center>
                                                            <table width="100">
                                                                <tr><th><%@ include file="includes/smilies.jsp" %></th></tr>
                                                            </table>
                                                        </center>
                                                    </mm:compare>
                                                </th>

                                                <td>
                                                    <mm:compare referid="error" value="none" inverse="true">
                                                        <mm:import id="localbody" externid="body" from="session"/><%--  it needs a new id, becouse ${body} will find the field in the session too--%>
                                                        <mm:import id="error" reset="true">none</mm:import>
                                                        <mm:write referid="error" session="error" />
                                                    </mm:compare>
                                                        <textarea name="body" rows="5" style="width: 99%">${localbody}</textarea>
                                                </td>
                                            </tr>


                                            <%--  commit form--%>
                                            <tr>
                                                <td colspan="3">
                                                    <input type="hidden" name="action" value="postreply">
                                                    <center><input type="submit" value="${mlg.Post_reply}"/></center>
                                                </td>
                                            </tr>
                                          </form>
                                    </mm:link>

                                    <a name="reply"></a>
                                </table>
                            </mm:compare>
                        </mm:compare>
                    </mm:compare>
                </mm:compare>
                <%--  end figure out if the reply box must be shown, and if so, show it.--%>
                <br />
                <br />
            </div>

            <div class="footer">
                <mm:function set="mmbob" name="getForumFooterPath" referids="forumid">
                    <jsp:include page="${_}"/>
                </mm:function>
            </div>

        </mm:locale>

        </body>
    </html>
</mm:cloud>
