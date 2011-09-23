<%@ include file="../jspbase.jsp" %>
<mm:cloud>
    <mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
        <mm:import externid="forumid" />
        <mm:import externid="sub">info</mm:import>

        <%-- theme information--%>
        <%@ include file="../thememanager/loadvars.jsp" %>

        <%--  action check--%>
        <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

        <%-- login part --%>
        <%@ include file="../getposterid.jsp" %>

        <mm:locale language="$lang">
            <%--  multilanguage --%>
            <%@ include file="../loadtranslations.jsp" %>

            <html>
            <head>
               <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
               <title>MMBob</title>
            </head>
            <body>

            <div class="header">
                <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
                <jsp:include page="${headerpath}"/>
            </div>

            <div class="bodypart">
              <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
              <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
              <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
              <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
              </mm:nodefunction>
              <mm:include page="../path.jsp?type=subindex" referids="logoutmodetype,forumid,posterid,active_nick" />

            <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
                <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
            </mm:nodefunction>

            <mm:compare referid="isadministrator" value="true">

                <%--  show the top menu--%>
                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="75%" align="center">
                    <tr>
                        <mm:import id="menu" vartype="List">info,layout,theme,login,rules,filter</mm:import>
                        <mm:stringlist referid="menu" id="item">
                            <c:choose> <c:when test="${sub == item}"><th align="center"/></c:when> <c:otherwise><td align="center"/></c:otherwise> </c:choose>
                            <mm:link page="changeforum.jsp" referids="forumid">
                                <mm:param name="sub" value="${item}" />
                                <a href="${_}">${item}</a>
                            </mm:link>
                            <c:choose> <c:when test="${sub == item}"></th></c:when> <c:otherwise></td></c:otherwise> </c:choose>
                        </mm:stringlist>

                    </tr>
                </table>
                <%--  end show the top menu--%>

                <%--  forum info--%>
                <mm:compare referid="sub" value="info">
                    <mm:node number="$forumid" id="forum">
                        <mm:link page="changeforum.jsp" referids="forumid">
                            <form action="${_}" method="post">
                                <input type="hidden" name="admincheck" value="true">
                                <input type="hidden" name="action" value="changeforum">

                                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                                    <tr>
                                        <th colspan="3"><mm:write referid="mlg.Change_existing_forum" /></th>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Name"/></th>
                                        <td colspan="2"><input name="name" size="70" value="${forum.name}" style="width: 100%"></td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Language"/></th>
                                        <td colspan="2" align="left">
                                            <select name="newlang">
                                                <mm:import id="tmpname">mmbob</mm:import>
                                                <mm:nodelistfunction set="mlg" name="getLanguagesInSet" referids="tmpname@setname">
                                                    <mm:param name="setname" value="mmbob" />
                                                    <mm:field name="name">
                                                        <mm:import id="selected" reset="true"><mm:compare value="${forum.language}">selected</mm:compare></mm:import>
                                                        <option ${selected}><mm:write/></option>
                                                    </mm:field>
                                                    <mm:remove referid="selected"/>
                                                </mm:nodelistfunction>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><mm:write referid="mlg.Description"/></th>
                                        <td colspan="2"> <textarea name="description" rows="5" style="width: 100%"><mm:field name="description" /></textarea></td>
                                    </tr>
                                    <tr>
                                        <th>&nbsp;</th>
                                        <td align="center"><input type="submit" value="${mlg.Save}"> </td>
                                        <td align="center"><input type="button" value="${mlg.Cancel}" onClick="document.location='changeforum.jsp?forumid=${forumid}'" /></td>
                                    </tr>
                                </table>
                            </form>
                        </mm:link>
                    </mm:node>
                </mm:compare>
                <%--  end forum info--%>

                <%--  layout settings --%>
                <mm:compare referid="sub" value="layout">
                    <mm:link page="changeforum.jsp" referids="forumid,sub" >
                        <form action="${_}" method="post" />
                            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                                <tr>
                                    <th colspan="3">Layout settings</th>
                                </tr>
                                <tr>
                                    <th>Number of msg per page</th>
                                    <td><input name="forumpostingsperpage" value="<mm:function set="mmbob" name="getForumPostingsPerPage" referids="forumid,posterid" />" size="3"></td>
                                </tr>
                                <tr>
                                    <th>Postings overflow postarea</th>
                                    <td><input name="forumpostingsoverflowpostarea" value="<mm:function set="mmbob" name="getForumPostingsOverflowPostArea" referids="forumid,posterid" />" size="3"></td>
                                </tr>
                                <tr><th>Postings overflow threadpage</th>
                                <td><input name="forumpostingsoverflowthreadpage" value="<mm:function set="mmbob" name="getForumPostingsOverflowThreadPage" referids="forumid,posterid" />" size="3"></td>
                                </tr>
                                <tr>
                                    <th>Speedpost time</th>
                                    <td><input name="forumspeedposttime" value="<mm:function set="mmbob" name="getForumSpeedPostTime" referids="forumid,posterid" />" size="3"></td>
                                </tr>
                                <tr>
                                    <th>Reply on each page</th>
                                    <td>
                                        <select name="forumreplyoneachpage">
                                            <mm:booleanfunction set="mmbob" name="getForumReplyOnEachPage" referids="forumid,posterid">
                                                <option value="true">True</option>
                                                <option value="false">False</option>
                                            </mm:booleanfunction>
                                            <mm:booleanfunction inverse="true" set="mmbob" name="getForumReplyOnEachPage" referids="forumid,posterid">
                                                <option value="false">False</option>
                                                <option value="true">True</option>
                                            </mm:booleanfunction>
                                        </select>
                                    </td>
                                </tr>
                                <input type="hidden" name="admincheck" value="true">
                                <input type="hidden" name="action" value="changelayout">
                                <tr>
                                    <td align="center"><input type="submit" value="${mlg.Save}"></td>
                                    <td align="center"><input type="button" value="${mlg.Cancel}" onClick="document.location='changeforum.jsp?forumid=${forumid}'" /></td>
                                </tr>
                            </table>
                        </form>
                    </mm:link>
                </mm:compare>
                <%--  end layout settings --%>


                <%--  theme settings --%>
                <mm:compare referid="sub" value="theme">
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                    <tr>
                        <th colspan="3">Theme settings for <mm:write referid="themename" /> ( <mm:write referid="themeid" /> ) </th>
                    </tr>

                    <tr>
                        <th width="20%">Background color</th>
                        <td>
                            <mm:import id="sename" reset="true">default/body/background</mm:import>
                            <mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
                        </td>
                    </tr>
                    <tr>
                        <th>Font</th>
                        <td>
                            <mm:import id="sename" reset="true">default/body/font-family</mm:import>
                            <mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
                        </td>
                    </tr>
                    <tr>
                        <th>Font Size</th>
                        <td>
                            <mm:import id="sename" reset="true">default/body/font-size</mm:import>
                            <mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
                        </td>
                    </tr>
                        <tr>
                        <th>Font Color</th>
                        <td>
                            <mm:import id="sename" reset="true">default/body/color</mm:import>
                            <mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
                        </td>
                    </tr>
                    </table>
                </mm:compare>
                <%--  end theme settings --%>


                <%--  login settings --%>
                <mm:compare referid="sub" value="login">
                    <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                        <mm:link page="changeforum.jsp" referids="forumid,sub" >
                            <form action="${_}" method="post">
                                <input type="hidden" name="admincheck" value="true">
                                <input type="hidden" name="action" value="changeconfig">

                                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                                    <tr>
                                        <th colspan="3">Login instellingen</th>
                                    </tr>

                                    <mm:import id="name">loginsystem</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="loginsystemtype" value="fixed" />
                                        </mm:compare>
                                        <mm:compare value="true">
                                            <tr>
                                                <th width="30%">Login System</th>
                                                <td colspan="2" align="left">
                                                    <mm:import id="currenttype"><mm:field name="loginsystemtype"/></mm:import>
                                                    <mm:import id="options" reset="true" vartype="List">http,entree,entree-ng,default</mm:import>
                                                    <select name="loginsystemtype">
                                                        <mm:stringlist referid="options" id="option">
                                                            <c:set var="selected"><c:if test="${currenttype == option}">selected</c:if></c:set>
                                                            <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">loginmode</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="loginmodetype" value="fixed" />
                                        </mm:compare>
                                        <mm:compare value="true">
                                            <tr>
                                                <th width="30%">Login Mode</th>
                                                <td colspan="2" align="left">
                                                    <select name="loginmodetype">
                                                        <mm:import id="options" reset="true" vartype="List">open,closed,default</mm:import>
                                                        <mm:import id="currentvalue"><mm:field name="loginmodetype"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                                <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                                <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                </select>
                                            </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">logoutmode</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="logoutmodetype" value="fixed" />
                                        </mm:compare>
                                        <mm:compare value="true">
                                            <tr>
                                                <th>LogoutMode</th>
                                                <td colspan="2" align="left">
                                                    <select name="logoutmodetype">
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="logoutmodetype"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                            <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                            <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                </select>
                                            </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">guestreadmode</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="guestreadmodetype" value="fixed" />
                                        </mm:compare>

                                        <mm:compare value="true">
                                            <tr>
                                                <th>GuestReadMode</th>
                                                <td colspan="2" align="left">
                                                    <select name="guestreadmodetype">
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="guestreadmodetype"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                            <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                            <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">guestwritemode</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="guestwritemodetype" value="fixed" />
                                        </mm:compare>

                                        <mm:compare value="true">
                                            <tr>
                                                <th>GuestWriteMode</th>
                                                <td colspan="2" align="left">
                                                    <select name="guestwritemodetype">
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="guestwritemodetype"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                            <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                            <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">avatarsupload</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="avatarsuploadenabled" value="fixed" />
                                        </mm:compare>

                                        <mm:compare value="true">
                                            <tr>
                                                <th>AvatarUpload</th>
                                                <td colspan="2" align="left">
                                                    <select name="avatarsuploadenabled">
                                                        <mm:import id="options" reset="true" vartype="List">true,false,default</mm:import>
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="avatarsuploadenabled"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                                <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                                <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">avatarsgallery</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="avatarsgalleryenabled" value="fixed" />
                                        </mm:compare>

                                        <mm:compare value="true">
                                            <tr>
                                                <th>AvatarGallery</th>
                                                <td colspan="2" align="left">
                                                    <select name="avatarsgalleryenabled">
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="avatarsgalleryenabled"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                                <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                                <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>

                                    <mm:import id="name" reset="true">navigationmethod</mm:import>
                                    <mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
                                        <mm:compare value="false">
                                            <input type="hidden" name="navigationmethod" value="fixed" />
                                        </mm:compare>

                                        <mm:compare value="true">
                                            <tr>
                                                <th>Navigation Method</th>
                                                <td colspan="2" align="left">
                                                    <select name="navigationmethod">
                                                        <mm:import id="options" reset="true" vartype="List">list,tree</mm:import>
                                                        <mm:import id="currentvalue" reset="true"><mm:field name="navigationmethod"/></mm:import>
                                                        <mm:remove referid="option"/>

                                                        <mm:stringlist referid="options" id="option">
                                                                <c:set var="selected"><c:if test="${option == currentvalue}">selected</c:if></c:set>
                                                                <option ${selected}>${option}</option>
                                                        </mm:stringlist>
                                                    </select>
                                                </td>
                                            </tr>
                                        </mm:compare>
                                    </mm:function>
                                    <tr>
                                        <th>Url Alias</th>
                                        <td colspan="2" align="left">
                                            <mm:field name="alias" > <input name="alias" value="${_}" size="15" /> </mm:field>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>&nbsp;</th>
                                        <td align="center" ><input type="submit" value="${mlg.Save}"></td>
                                        <td align="center"><input type="button" value="${mlg.Cancel}" onClick="document.location='changeforum.jsp?forumid=${forumid}'" /></td>
                                    </tr>
                            </table>
                        </form>
                    </mm:link>
                </mm:nodefunction>
                </mm:compare>
                <%--  end of login settings--%>

                <%--  rules--%>
                <mm:compare referid="sub" value="rules">
                    <mm:node number="$forumid">
                        <mm:link page="changeforum.jsp" referids="forumid,sub">
                            <form action="${_}" method="post">
                                <input type="hidden" name="sub" value="rules" />
                                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                                    <tr>
                                        <th colspan="3">Change forum rules</th>
                                    </tr>

                                    <mm:import id="rulesid">-1</mm:import>

                                    <mm:relatednodes type="forumrules">
                                        <mm:import id="rulesid" reset="true"><mm:field name="number" /></mm:import>
                                        <input type="hidden" name="action" value="changerules" />
                                        <input type="hidden" name="rulesid" value="${rulesid}" />
                                        <tr>
                                            <th>Title</th>
                                            <td colspan="2"><input name="title" value="<mm:field name="title" />" size="70" style="width: 100%"></td>
                                        </tr>
                                        <tr>
                                            <th>Rules</th><td colspan="2"><textarea name="body" rows="15" style="width: 100%"><mm:field name="body" /></textarea></td>
                                        </tr>
                                    </mm:relatednodes>

                                    <mm:compare referid="rulesid" value="-1">
                                        <input type="hidden" name="action" value="addrules" />
                                        <input type="hidden" name="rulesid" value="${rulesid}" />
                                        <tr>
                                            <th>Title</th>
                                            <td colspan="2"><input name="title" value="" size="70" style="width: 100%"></td>
                                        </tr>
                                        <tr>
                                            <th>Rules</th><td colspan="2"><textarea name="body" rows="15" cols="70"></textarea></td>
                                        </tr>
                                    </mm:compare>

                                    <tr>
                                        <th>&nbsp;</th>
                                        <td align="center" >
                                            <input type="hidden" name="admincheck" value="true">
                                            <input type="submit" value="${mlg.Save}">
                                        </td>
                                        <td align="center">
                                            <input type="button" value="${mlg.Cancel}" onclick="document.location='changeforum.jsp?forumid=${forumid}'"/>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                        </mm:link>
                    </mm:node>
                </mm:compare>
                <%--  end of rules--%>

                <%--  word filter--%>
                <mm:compare referid="sub" value="filter">
                    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
                        <tr>
                            <th colspan="4">Forum filter</th>
                        </tr>
                        <mm:nodelistfunction set="mmbob" name="getFilterWords" referids="forumid" id="filter">
                            <mm:link page="changeforum.jsp" referids="forumid,sub" >
                                <form action="${_}" method="post" id="__${filter.name}_form">
                                    <input type="hidden" name="admincheck" value="true">
                                    <input type="hidden" name="action" id="__${filter.name}_action">
                                    <input type="hidden" name="name" value="${filter.name}">
                                    <tr>
                                        <th> <mm:field name="name" /> </th>
                                        <td> <input name="value" style="width: 98%" value="${filter.value}"> </td>
                                        <td><input type="button" value="${mlg.Save}" onClick="document.getElementById('__${filter.name}_action').value='addwordfilter'; document.getElementById('__${filter.name}_form').submit()" /></td>
                                        <td><input type="button" value="${mlg.Delete}" onClick="document.getElementById('__${filter.name}_action').value='removewordfilter'; document.getElementById('__${filter.name}_form').submit()" /></td>
                                    </tr>
                                </form>
                            </mm:link>
                            <mm:last>
                                <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post">
                                    <input type="hidden" name="admincheck" value="true">
                                    <input type="hidden" name="action" value="addwordfilter">
                                    <tr>
                                        <th><input name="name" style="width: 98%"></th>
                                        <td><input name="value" style="width: 98%"></td>
                                        <td><input type="submit" value="<mm:write referid="mlg.Save"/>" /></td>
                                        <td>&nbsp;</td>
                                    </tr>
                                </form>
                            </mm:last>
                        </mm:nodelistfunction>
                    </table>
                </mm:compare>
                <%--  end word filter--%>

            </mm:compare>
            <%--  end 'isadministrator'--%>

            <mm:compare referid="isadministrator" value="false">
                <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="55%" align="center">
                    <tr><th>MMBob system error</th></tr>
                    <tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
                </table>
            </mm:compare>

            </div>

            <div class="footer">
                <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
                <jsp:include page="${footerpath}"/>
            </div>

            </body>
            </html>

        </mm:locale>
    </mm:content>
</mm:cloud>

