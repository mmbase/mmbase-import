<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">poster_newposter</mm:import>
<mm:import externid="postareaid" />
<mm:import externid="feedback">none</mm:import>


<%--
    find out if there are forum rules. if so, the user must accept them, expressed by
    field 'rulesaccepted'. No user node is created while this field is set to 'no'.
    if there are no rules, we set rulesaccepted to 'yes'. it amounts to the same thing.
--%>
<mm:import externid="rulesaccepted">no</mm:import>
<mm:compare referid="rulesaccepted" value="no">
    <mm:node referid="forumid">
        <mm:relatednodes type="forumrules" searchdirs="destination" role="related">
            <mm:import id="rulesid"><mm:field name="number" /></mm:import>
        </mm:relatednodes>
    </mm:node>
</mm:compare>
<mm:notpresent referid="rulesid">
    <mm:import id="rulesaccepted" reset="true">yes</mm:import>
</mm:notpresent>

<%-- login part --%>
<%@ include file="../getposterid.jsp" %>
<%@ include file="../thememanager/loadvars.jsp" %>
<%-- end login part --%>

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="hasnick"><mm:field name="hasnick" /></mm:import>
</mm:nodefunction>


<%--  action check--%>
<mm:import externid="action" />
<mm:present referid="action">

    <%--  create a new poster based on an entee-ng account. only when the rules are accepted! --%>
    <mm:compare value="createposter" referid="action">
        <mm:compare referid="rulesaccepted" value="yes">
            <mm:import reset="true" id="account" externid="newaccount" /><%--entree account id--%>
            <mm:import reset="true" id="password" externid="newpassword" />
            <mm:import reset="true" id="confirmpassword" externid="newconfirmpassword" />
            <mm:import id="firstname" externid="newfirstname" />
            <mm:import id="lastname" externid="newlastname" />
            <mm:import id="email" externid="newemail" />
            <mm:import id="location" externid="newlocation" />
            <mm:import id="gender" externid="newgender" />

            <%-- this function can return:  [createerror|inuse|ok|passwordnotequal|firstnameerror|lastnameerror|emailerror]--%>
            <mm:compare referid="hasnick" value="false">
                <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPoster" referids="forumid,account,password,confirmpassword,firstname,lastname,email,gender,location" /></mm:import>
            </mm:compare>
            <%-- this function can return: [ok|createerror|inuse|passwordnotequal|nickinuse|firstnameerror|lastnameerror|emailerror] --%>
            <mm:compare referid="hasnick" value="true">
                <mm:import id="nick" externid="newnick" />
                <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPosterNick" referids="forumid,account,password,confirmpassword,nick,firstname,lastname,email,gender,location" /></mm:import>
            </mm:compare>
        </mm:compare>
    </mm:compare>
</mm:present>
<%-- end action check --%>

    <mm:locale language="$lang">
    <%@ include file="../loadtranslations.jsp" %>

    <html>
        <head>
           <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
           <title>MMBob</title>
        </head>
        <body>

        <div class="header">
            <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
            <jsp:include page="<%=headerpath%>"/>
        </div>

        <div class="bodypart">


            <mm:include page="../path.jsp?type=$pathtype" />

            <%--  there are rules, so you have to accept --%>
            <mm:present referid="rulesid">
                <mm:node referid="rulesid">
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="80%">
                        <tr>
                            <th colspan="2"><mm:field name="title" /></th>
                        </tr>
                        <tr>
                            <td colspan="2"><br /><br /><mm:field name="body" escape="p" /><br /><br /></td>
                        </tr>
                        <tr>
                            <mm:link page="newentreengposter.jsp" referids="forumid,postareaid,action" >
                                <mm:param name="newaccount" value="${param.newaccount}" />
                                <mm:param name="newpassword" value="${param.newpassword}" />
                                <mm:param name="newconfirmpassword" value="${param.newconfirmpassword}" />
                                <mm:param name="newfirstname" value="${param.newfirstname}" />
                                <mm:param name="newlastname" value="${param.newlastname}" />
                                <mm:param name="newemail" value="${param.newemail}" />
                                <mm:param name="newlocation" value="${param.newlocation}" />
                                <mm:param name="newgender" value="${param.newgender}" />
                                <form action="${_}" method="post">
                                    <td align="middle" width="50%">
                                        <center>
                                            <input type="submit" value="${mlg.I_ACCEPT_THESE_RULES}" />
                                            <input type="hidden" name="rulesaccepted" value="yes" />
                                        </center>
                                    </td>
                                </form>
                            </mm:link>

                            <mm:link page="../index.jsp" referids="forumid" >
                            <form action="${_}" method="post">
                                <td>
                                    <center> <input type="submit" value="${mlg.I_REFUSE_THESE_RULES}" /></center>
                                </td>
                            </form>
                    </mm:link>
                    </tr>
                </table>
                </mm:node>
            </mm:present>

            <%--  there are no forum ruls.--%>
            <mm:notpresent referid="rulesid">

                <%--  The poster is not created yet--%>
                <mm:compare referid="feedback" value="none">
                    <%-- <p>debug: no forumrules, no feedback</p> --%>
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
                        <mm:link page="newentreengposter.jsp" referids="forumid,rulesaccepted">
                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                            <form action="${_}" method="post">

                                <%--  no entree id found (not logged in)--%>
                                <mm:compare referid="entree" value="null">
                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.Account"/></th>
                                        <td> <input name="newaccount" value="" style="width: 100%" /> </td>
                                    </tr>

                                    <mm:compare referid="hasnick" value="true">
                                        <tr>
                                            <th width="150" >Nick</th>
                                            <td> <input name="newnick" value="" style="width: 100%" /> </td>
                                        </tr>
                                    </mm:compare>

                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.Password"/></th>
                                        <td>
                                            <input name="newpassword" style="width: 100%" type="password"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.ConfirmPassword"/></th>
                                        <td> <input name="newconfirmpassword" style="width: 100%" type="password"/> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Firstname"/></th>
                                        <td> <input name="newfirstname" value="" style="width: 100%" /> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Lastname"/></th>
                                        <td> <input name="newlastname" value="" style="width: 100%" /> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Email"/></th>
                                        <td> <input name="newemail" value="" style="width: 100%" /> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Location"/></th>
                                        <td> <input name="newlocation" value="" style="width: 100%" /> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Gender"/></th>
                                        <td>
                                            <select name="newgender">
                                                <option value="unknown">Unknown</option>
                                                <option value="male"><mm:write referid="mlg.Male"/></option>
                                                <option value="female"><mm:write referid="mlg.Female"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </mm:compare>
                                <%--  end no entree id found (not logged in)--%>

                                <%-- there is an entree id (logged in)--%>
                                <mm:compare referid="entree" value="null" inverse="true">
                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.Account"/></th>
                                        <td>
                                            <input type="hidden" name="newaccount" value="<%= request.getHeader("sm_user") %>">
                                            <%= request.getHeader("sm_user") %>
                                            <input type="hidden" name="newpassword" value="<%= request.getHeader("aad_nummer") %>">
                                            <input type="hidden" name="newconfirmpassword" value="<%= request.getHeader("aad_nummer") %>">
                                        </td>
                                    </tr>
                                    <mm:compare referid="hasnick" value="true">
                                        <tr>
                                            <th width="150" >Nick</th>
                                            <td> <input name="newnick" value="" style="width: 100%" /> </td>
                                        </tr>
                                    </mm:compare>
                                    <tr>
                                        <th><mm:write referid="mlg.Firstname"/></th>
                                        <td>
                                            <input type="hidden" name="newfirstname" value="<%= request.getHeader("aad_voornaam") %>">
                                            <%= request.getHeader("aad_voornaam") %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Lastname"/></th>
                                        <td>
                                            <mm:import id="tan"><%= request.getHeader("aad_achternaam") %></mm:import>
                                            <mm:compare referid="tan" value="null" inverse="true">
                                                <input type="hidden" name="newlastname" value="<%= request.getHeader("aad_achternaam") %>">
                                                <%= request.getHeader("aad_achternaam") %>
                                            </mm:compare>

                                            <mm:compare referid="tan" value="null">
                                                <input type="hidden" name="newlastname" value="   ">
                                                missing
                                            </mm:compare>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Email"/></th>
                                        <td>
                                            <input name="newemail" value="" style="width: 100%" value="<%= request.getHeader("aad_emailadres") %>" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Location"/></th>
                                        <td> <input name="newlocation" value="" style="width: 100%" /> </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Gender"/></th>
                                        <td>
                                            <select name="newgender">
                                                <option value="male"><mm:write referid="mlg.Male"/></option>
                                                <option value="female"><mm:write referid="mlg.Female"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </mm:compare>
                                <%-- end there is an entree id (logged in)--%>

                                <tr>
                                    <th colspan="2">
                                        <input type="hidden" name="action" value="createposter">
                                        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
                                    </th>
                                </tr>
                            </form>
                        </mm:link>
                    </table>
                </mm:compare>
                <%--  end: The poster is not created yet--%>
            </mm:notpresent>

            <%--  poster creation did result into:  [createerror|inuse|ok|passwordnotequal]. this is always true! --%>
            <mm:compare referid="feedback" value="none" inverse="true">
                <mm:compare referid="feedback" value="ok" inverse="true">
                    <%-- <p>debug: no there is feedback, but it is not 'ok'</p> --%>
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
                        <tr>
                            <th colspan="2">
                                <font color="red"> ***
                                <mm:compare referid="feedback" value="inuse"><mm:write referid="mlg.Account_allready_in_use"/></mm:compare>
                                <mm:compare referid="feedback" value="nickinuse"><mm:write referid="mlg.Nick_allready_in_use"/></mm:compare>
                                <mm:compare referid="feedback" value="passwordnotequal"><mm:write referid="mlg.Password_notequal"/></mm:compare>
                                <mm:compare referid="feedback" value="firstnameerror">Firstname invalid</mm:compare>
                                <mm:compare referid="feedback" value="lastnameerror">Surname invalid</mm:compare>
                                <mm:compare referid="feedback" value="emailerror">Email invalid</mm:compare>
                                ***
                                </font>
                            </th>
                        </tr>

                        <mm:link page="newentreengposter.jsp" referids="forumid,rulesaccepted">
                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                            <form action="${_}" method="post">

                                <%--  no entree id found (not logged in)--%>
                                <%--  i don't know why this is. it seems to me that if there is no entree id, the whole deal is off, and you first have to loginto entree ????--%>
                                <mm:compare referid="entree" value="null">
                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.Account"/></th>
                                        <td>
                                            <mm:import externid="newaccount" />
                                            <mm:write referid="newaccount" >
                                                <mm:write />
                                                <input name="newaccount" value="${_}" type="hidden" />
                                            </mm:write>
                                        </td>
                                    </tr>

                                    <mm:compare referid="hasnick" value="true">
                                        <tr>
                                            <th width="150" >Nick</th>
                                            <td>
                                                <mm:import externid="newnick" />
                                                <mm:write referid="newnick">
                                                    <input name="newnick" value="${_}" style="width: 100%" />
                                                </mm:write>
                                            </td>
                                        </tr>
                                    </mm:compare>

                                    <mm:import externid="newpassword" />
                                    <mm:write referid="newpassword">
                                        <input name="newpassword" type="hidden" value="${_}" />
                                    </mm:write>

                                    <mm:import externid="newconfirmpassword" />
                                    <mm:write referid="newconfirmpassword">
                                        <input name="newconfirmpassword" type="hidden" value="${_}"  />
                                    </mm:write>

                                    <tr>
                                        <th><mm:write referid="mlg.Firstname"/></th>
                                        <td>
                                            <mm:import externid="newfirstname" />
                                            <mm:write referid="newfirstname" >
                                                <mm:write />
                                                <input name="newfirstname" value="${_}" type="hidden"  />
                                            </mm:write>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Lastname"/></th>
                                        <td>
                                            <mm:import externid="newlastname" />
                                            <mm:write referid="newlastname" >
                                                <mm:write />
                                                <input name="newlastname" value="${_}" type="hidden" />
                                            </mm:write>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Email"/></th>
                                        <td>
                                            <mm:import externid="newemail" />
                                            <mm:write referid="newemail" >
                                                <mm:write />
                                                <input name="newemail" value="${_}" type="hidden" />
                                            </mm:write>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Location"/></th>
                                        <td>
                                            <mm:import externid="newlocation" />
                                            <input name="newlocation" value="<mm:write referid="newlocation" />" style="width: 100%" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Gender"/></th>
                                        <td>
                                            <select name="newgender">
                                                <mm:import externid="newgender" />
                                                <mm:write referid="newgender">
                                                    <c:if test="${_ == 'unknown'}"><c:set var="su"value="selected"></c:set></c:if>
                                                    <c:if test="${_ == 'male'}"><c:set var="sm"value="selected"></c:set></c:if>
                                                    <c:if test="${_ == 'female'}"><c:set var="sf"value="selected"></c:set></c:if>
                                                </mm:write>
                                                <option value="unknown" ${su} >Unknown</option>
                                                <option value="male" ${sm}><mm:write referid="mlg.Male"/></option>
                                                <option value="female" ${sf}><mm:write referid="mlg.Female"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </mm:compare>
                                <%--  end no entree id found (not logged in)--%>

                                <%-- there is an entree id (logged in)--%>
                                <mm:compare referid="entree" value="null" inverse="true">
                                    <tr>
                                        <th width="150" ><mm:write referid="mlg.Account"/></th>
                                        <td>
                                            <input type="hidden" name="newaccount" value="<%= request.getHeader("sm_user") %>">
                                            <%= request.getHeader("sm_user") %>
                                            <input type="hidden" name="newpassword" value="<%= request.getHeader("aad_nummer") %>">
                                            <input type="hidden" name="newconfirmpassword" value="<%= request.getHeader("aad_nummer") %>">
                                        </td>
                                    </tr>

                                    <mm:compare referid="hasnick" value="true">
                                        <tr>
                                            <th width="150" >Nick</th>
                                            <td>
                                                <mm:import externid="newnick" />
                                                <input name="newnick" value="<mm:write referid="newnick"/>" style="width: 100%" />
                                            </td>
                                        </tr>
                                    </mm:compare>

                                    <tr>
                                        <th><mm:write referid="mlg.Firstname"/></th>
                                        <td>
                                            <input type="hidden" name="newfirstname" value="<%= request.getHeader("aad_voornaam") %>">
                                            <%= request.getHeader("aad_voornaam") %>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Lastname"/></th>
                                        <td>
                                            <mm:import id="tan"><%= request.getHeader("aad_achternaam") %></mm:import>
                                            <mm:compare referid="tan" value="null" inverse="true">
                                                <input type="hidden" name="newlastname" value="<%= request.getHeader("aad_achternaam") %>">
                                                <%= request.getHeader("aad_achternaam") %>
                                            </mm:compare>

                                            <mm:compare referid="tan" value="null">
                                                <input type="hidden" name="newlastname" value="   ">
                                                missing
                                            </mm:compare>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th><mm:write referid="mlg.Email"/></th>
                                        <td>
                                            <input type="hidden" name="newemail" value="<%= request.getHeader("aad_emailadres") %>" />
                                            <%= request.getHeader("aad_emailadres") %>
                                        </td>
                                    </tr>

                                    <tr>
                                        <th><mm:write referid="mlg.Location"/></th>
                                        <td>
                                            <mm:import externid="newlocation" />
                                            <input name="newlocation" value="<mm:write referid="newlocation" />" style="width: 100%" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Gender"/></th>
                                        <td>
                                            <c:remove var="su"/>
                                            <c:remove var="sm"/>
                                            <c:remove var="sf"/>
                                            <select name="newgender">
                                            <mm:import externid="newgender" />
                                                  <mm:write referid="newgender">
                                                    <c:if test="${_ == 'unknown'}"><c:set var="su"value="selected"></c:set></c:if>
                                                    <c:if test="${_ == 'male'}"><c:set var="sm"value="selected"></c:set></c:if>
                                                    <c:if test="${_ == 'female'}"><c:set var="sf"value="selected"></c:set></c:if>
                                                </mm:write>
                                                <option value="unknown" ${su} >Unknown</option>
                                                <option value="male" ${sm}><mm:write referid="mlg.Male"/></option>
                                                <option value="female" ${sf}><mm:write referid="mlg.Female"/></option>
                                            </select>
                                        </td>
                                    </tr>
                                </mm:compare>
                                <%-- end there is an entree id (logged in)--%>

                                <tr>
                                    <th colspan="2">
                                        <input type="hidden" name="action" value="createposter">
                                        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
                                    </th>
                                </tr>
                            </form>
                        </mm:link>
                    </table>
                    <%--  end: feedback != ok--%>
                </mm:compare>
            <%--  end: feedback != none--%>
            </mm:compare>

            <mm:compare referid="feedback" value="ok">
                <%-- <p>debug: there is feedback and it is 'ok'</p> --%>
                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="60%">
                    <tr>
                        <th><mm:write referid="mlg.Account_created"/></th>
                    </tr>
                    <tr>
                        <td>
                            <mm:write referid="mlg.Your_account_is_created_you_may"/>
                            <mm:link page="../index.jsp" referids="forumid" > <a href="${_}"><mm:write referid="mlg.login"/></a></mm:link>
                        </td>
                    <tr>
                </table>
            </mm:compare>

        </div>

        <div class="footer">
            <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
            <jsp:include page="<%=footerpath%>"/>
        </div>


        </body>
    </html>
</mm:locale>

</mm:content>
</mm:cloud>
