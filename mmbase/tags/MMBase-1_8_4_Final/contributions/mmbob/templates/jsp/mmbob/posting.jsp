<%--  create a posting--%>
<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:import externid="forumid" />
    <%@ include file="thememanager/loadvars.jsp" %>

    <mm:import externid="page" />
    <mm:import externid="pagesize" />
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />

    <!-- login part -->
    <%@ include file="getposterid.jsp" %>
    <!-- end login part -->

    <mm:locale language="$lang">
        <%@ include file="loadtranslations.jsp" %>

        <!-- action check -->
        <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

        <mm:node referid="postthreadid">
          <mm:field name="state">
            <mm:import id="tstate"><mm:field name="state" /></mm:import>
            <mm:compare value="closed"><mm:import id="noedit">true</mm:import></mm:compare>
          </mm:field>
        </mm:node>
        <%--
            here this function is called to determin the page we are going to have to return to. but this dous not work
            when the post will land on a new page, becouse you don't go there. But the funny thing is, value -1 will always
            get you to the last page, and it seems to me that what you want anyway.
            Perhaps i'm wrong?
        --%>

        <%--
        <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
            <mm:field name="pagecount" id="pagecount" write="false" />
        </mm:nodefunction>
        --%>
        <mm:import id="pagecount">-1</mm:import>

        <html>
        <head>
           <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
           <title>MMBob</title>
           <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
        </head>
        <body>

        <%--  show the path--%>
        <div class="header">
            <mm:function set="mmbob" name="getForumHeaderPath" referids="forumid">
                <jsp:include page="${_}"/>
            </mm:function>
        </div>

        <div class="bodypart">
            <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
            <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
            <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
            <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
            <mm:import id="active_firstname"><mm:field name="active_firstname" /></mm:import>
            <mm:import id="active_lastname"><mm:field name="active_lastname" /></mm:import>
            <mm:include page="path.jsp?type=postthread" referids="logoutmodetype,posterid,forumid,active_nick" />
        </mm:nodefunction>


        <%--  show the main form--%>
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px; border: 1px solid red"  width="75%">
            <mm:import externid="postingid" />

            <mm:notpresent referid="noedit">
                <tr>
                    <th colspan="3"><mm:write referid="mlg.Compose_message" /> </th>
                </tr>
                <mm:node referid="postingid">
                <form action="<mm:url page="thread.jsp">
                    <mm:param name="forumid" value="$forumid" />
                    <mm:param name="postareaid" value="$postareaid" />
                    <mm:param name="postthreadid" value="$postthreadid" />
                    <mm:param name="page" value="$pagecount" />
                    </mm:url>#reply" method="post" name="posting">
                    <%--  info on the current poster--%>
                    <tr>
                        <th width="20%"><mm:write referid="mlg.Name" /></th>
                        <td colspan="2">
                            <mm:compare referid="posterid" value="-1" inverse="true">
                                <mm:write referid="active_nick" />
                                    <mm:import id="fullname"><mm:write referid="active_firstname" /> <mm:write referid="active_lastname" /></mm:import>
                                    <mm:write referid="fullname">
                                        <mm:compare value=" " inverse="true">(<mm:write/>)</mm:compare>
                                    </mm:write>
                                <input name="poster" type="hidden" value="<mm:write referid="active_nick" />" >
                            </mm:compare>
                            <mm:compare referid="posterid" value="-1"><input name="poster" size="32" value="gast" ></mm:compare>
                        </td>
                    </tr>
                    <%--subject line--%>
                    <tr>
                        <th><mm:write referid="mlg.Topic" /></th>
                        <td colspan="2"><input name="subject" style="width: 100%" value="Re: <mm:field name="subject" />" > </td>
                    </tr>
                    <%--message line--%>
                    <tr>
                        <th valign="top"><mm:write referid="mlg.Message" />
                            <center><table width="99"><tr><th><%@ include file="includes/smilies.jsp" %></th></tr></table></center>
                        </th>
                        <td colspan="2">
                            <textarea name="body" rows="20" style="width: 100%">[quote poster="<mm:field name="c_poster"/>"]<mm:formatter xslt="xslt/posting2textarea.xslt"><mm:field name="body" /></mm:formatter>[/quote]</textarea>
                        </td>
                    </tr>
                    <tr>
                        <th>&nbsp;</th>
                        <td>
                            <input type="hidden" name="action" value="postreply">
                            <center><input type="submit" value="<mm:write referid="mlg.Save" />"></center>
                            </form>
                        </td>
                        <td>
                            <%--  this forum is for the cancel button. why?--%>
                            <form action="<mm:url page="thread.jsp">
                            <mm:param name="forumid" value="$forumid" />
                            <mm:param name="postareaid" value="$postareaid" />
                            <mm:param name="postthreadid" value="$postthreadid" />
                            <mm:param name="page" value="$pagecount" />
                            </mm:url>"
                            method="post">
                                <p />
                                <center> <input type="submit" value="<mm:write referid="mlg.Cancel" />"> </center>
                            </form>
                        </td>
                    </tr>
                </mm:node>
            </mm:notpresent>

            <mm:present referid="noedit">
                <tr>
                    <th colspan="3"><mm:write referid="mlg.Topic_closed_by_moderator" /></th>
                </tr>
                <td>
                <form action="<mm:url page="thread.jsp">
                <mm:param name="forumid" value="$forumid" />
                <mm:param name="postareaid" value="$postareaid" />
                <mm:param name="postthreadid" value="$postthreadid" />
                </mm:url>"
                method="post">
                <p />
                    <center> <input type="submit" value="<mm:write referid="mlg.Ok"/>"> </center>
                </form>
                </td>
                </tr>
            </mm:present>
            </table>

        </div>

        <div class="footer">
            <mm:function set="mmbob" name="getForumFooterPath" referids="forumid">
                <jsp:include page="${_}"/>
            </mm:function>
        </div>

        </body>
        </html>

    </mm:locale>
</mm:cloud>


