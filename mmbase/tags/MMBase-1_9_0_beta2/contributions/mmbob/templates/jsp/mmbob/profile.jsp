<%--  view and edit user profile information--%>
<%--TODO: field isadministrator is not set anywhere (adminmode always false)--%>
<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:import id="entree" reset="true"><%= request.getHeader("aad_nummer") %></mm:import>
    <mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
        <mm:import externid="forumid" />
        <mm:import externid="pathtype">poster_index</mm:import>
        <mm:import externid="postareaid" />
        <mm:import externid="posterid" id="profileid" />
        <mm:import externid="profile">personal</mm:import>
        <mm:import id="feedbackdefault"></mm:import>
        <mm:write session="feedback_message" referid="feedbackdefault"/>

        <%-- login part --%>
        <%@ include file="getposterid.jsp" %>
        <%@ include file="thememanager/loadvars.jsp" %>
        <%-- end login part --%>

        <%-- action check --%>
        <mm:import externid="action" reset="true"/>
        <mm:present referid="action">
          <mm:include page="actions.jsp" />
        </mm:present>
        <%-- end action check --%>

        <mm:locale language="$lang">
            <html>
                <%@ include file="loadtranslations.jsp" %>
                <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
                      <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
                      <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
                      <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
                      <mm:import id="loginsystemtype"><mm:field name="loginsystemtype" /></mm:import>
                      <mm:compare referid="loginsystemtype" value="entree-ng">
                          <mm:import id="loginsystemtype" reset="true">entree</mm:import>
                      </mm:compare>
                      <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
                </mm:nodefunction>
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
                        <mm:include page="path.jsp?type=${empty param.type ? pathtype : param.type}" referids="logoutmodetype,forumid,posterid,active_nick" />

                        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                            <tr>
                                <th colspan="2" align="left"> </th>
                            </tr>
                        </table>

                        <div id="profileb">
                            <%-- own profile (editable)--%>
                            <mm:compare referid="profileid" referid2="posterid">

                                <%--  the top naviagtion--%>
                                <div id="tabs">
                                    <mm:link write="false" id="base" page="profile.jsp" referids="forumid,profileid@posterid">
                                        <mm:present referid="postareaid"> <mm:param name="postareaid" value="$postareaid" /> </mm:present>
                                        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                    </mm:link>
                                    <ul>

                                        <%-- personal tab--%>
                                        <mm:compare value="personal" referid="profile">
                                            <mm:import id="selected" reset="true">class="selected"</mm:import>
                                        </mm:compare>

                                        <li ${selected} >
                                            <mm:link referid="base" >
                                                <mm:param name="profile" value="personal" />
                                                <a href="${_}"><mm:write referid="mlg.personal"/></a>
                                            </mm:link>
                                        </li>


                                        <%-- contact tab (when contact info is enabled)--%>
                                        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                            <mm:field name="contactinfoenabled">
                                                <mm:compare referid="adminmode" value="true">

                                                    <mm:remove referid="selected"/>
                                                    <mm:compare referid="profile" value="contact">
                                                       <mm:import id="selected" reset="true">class="selected"</mm:import>
                                                    </mm:compare>

                                                    <li ${selected} >
                                                        <mm:link referid="base">
                                                            <mm:param name="profile" value="contact" />
                                                            <a href="${_}">admin info</a>
                                                        </mm:link>
                                                    </li>
                                                    <mm:remove referid="selected"/>
                                                </mm:compare>
                                            </mm:field>
                                        </mm:nodefunction>

                                        <%--  signature tab--%>
                                        <mm:remove referid="selected"/>
                                        <mm:compare value="signatures" referid="profile">
                                            <mm:import id="selected" reset="true">class="selected"</mm:import>
                                        </mm:compare>

                                        <li ${selected}>
                                        <mm:link referid="base" >
                                            <mm:param name="profile" value="signatures" />
                                            <a href="${_}"><mm:write referid="mlg.signature"/></a>
                                        </mm:link>
                                        </li>
                                        <mm:remove referid="selected"/>

                                        <%-- avatar tab--%>
                                        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                            <mm:field name="avatarsdisabled">
                                                <mm:compare value="false">
                                                    <mm:import id="avatardisabled"/>

                                                    <mm:remove referid="selected"/>
                                                    <mm:compare value="avatar" referid="profile">
                                                        <mm:import id="selected" reset="true">class="selected"</mm:import>
                                                    </mm:compare>

                                                    <li ${selected} >
                                                        <mm:link referid="base">
                                                            <mm:param name="profile" value="avatar" />
                                                            <a href="${_}"/><mm:write referid="mlg.avatar"/></a>
                                                        </mm:link>
                                                    </li>
                                                </mm:compare>
                                            </mm:field>
                                        </mm:nodefunction>
                                    </ul>
                                </div><!--end div 'tabs'-->
                                <%--  end the top naviagtion--%>


                                <div id="profile">
                                    <%--  profile section of the page--%>
                                    <mm:compare value="personal" referid="profile">
                                        <mm:link page="profile.jsp" referids="forumid,profileid@posterid">
                                            <mm:present referid="postareaid">
                                                <mm:param name="postareaid" value="$postareaid" />
                                            </mm:present>
                                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                            <mm:param name="profile" value="$profile" />

                                            <form action="${_}" method="post">
                                                <mm:node number="$profileid">
                                                    <mm:functioncontainer>
                                                        <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
                                                        <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid" id="pinfo">

                                                            <%-- feedback messages--%>
                                                            <div class="row">
                                                                <span class="feedback">
                                                                    <mm:import externid="feedback_message" from="session" id="feedback"/>
                                                                    <mm:compare referid="feedback" value="true">
                                                                        <font color="red"><b>** <mm:write referid="mlg.ProfileUpdated"/> **</b></font>
                                                                    </mm:compare>
                                                                    <mm:compare referid="feedback" value="false">
                                                                        <font color="red"><b>** <mm:write referid="mlg.ProfileUpdateFailed"/> **</b></font>
                                                                    </mm:compare>
                                                                    <mm:compare referid="feedback" value="profilechanged">
                                                                        <font color="red"><b>** <mm:write referid="mlg.ProfileUpdated"/> **</b></font>
                                                                        <mm:import id="accounttocookie"><mm:field name="account" /></mm:import>
                                                                        <mm:write referid="accounttocookie" cookie="caf$forumid"/>
                                                                    </mm:compare>
                                                                    <mm:compare referid="feedback" value="newpasswordnotequal">
                                                                          <font color="red"><b>** <mm:write referid="mlg.Password_notequal"/> **</b></font>
                                                                    </mm:compare>
                                                                </span>
                                                            </div>

                                                            <%-- nickname --%>
                                                            <div class="row">
                                                                 <input type="hidden" name="action" value="editposter" />
                                                                 <span class="label"><mm:write referid="mlg.Account"/></span>
                                                                 <span class="formw"><mm:field name="nick" /></span>
                                                            </div>

                                                            <%--  login system http--%>
                                                            <mm:compare referid="loginsystemtype" value="http">

                                                                <%--  show the default fields--%>
                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Firstname"/></span>
                                                                    <span class="formw">
                                                                      <input name="newfirstname" type="text" size="25" value="${pinfo.firstname}"/>
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Lastname"/></span>
                                                                    <span class="formw">
                                                                        <input name="newlastname" type="text" size="25" value="${pinfo.lastname}"/>
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Email"/></span>
                                                                    <span class="formw">
                                                                        <input name="newemail" type="text" size="25" value="${pinfo.email}"/>
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Location"/></span>
                                                                    <span class="formw">
                                                                        <input name="newlocation" type="text" size="25" value="${pinfo.location}"/>
                                                                    </span>
                                                                </div>

                                                                <%--  now show the profileEntryDef fields--%>
                                                                <%--
                                                                    <mm:compare value="string"><input name="<mm:field name="name" />" type="text" size="25" value="<mm:field name="value" />"/></mm:compare>
                                                                    <mm:compare value="field"><textarea rows="7" cols="25" name="<mm:field name="name" />"><mm:field name="value" /></textarea></mm:compare>
                                                                    <mm:compare value="date"><mm:import id="bname"><mm:field name="name" /></mm:import><mm:import id="bvalue"><mm:field name="value" /></mm:import><mm:include page="bdate.jsp" referids="bname,bvalue" /></mm:compare>
                                                                --%>
                                                                <mm:import id="guipos" reset="true">0</mm:import>
                                                                <mm:nodelistfunction set="mmbob" name="getProfileValues" referids="forumid,posterid,guipos" id="field">
                                                                    <div class="row">
                                                                        <span class="label"><mm:field name="guiname" /></span>
                                                                        <span class="formw">
                                                                            <mm:field name="edit">
                                                                                <mm:compare value="true">
                                                                                    <c:choose>
                                                                                        <c:when test="${field.type == 'string'}"> <input name="${field.name}" type="text" size="25" value="${field.value}"/></c:when>
                                                                                        <c:when test="${field.type == 'field'}"><textarea rows="7" cols="25" name="${field.name}">${field.value}</textarea></c:when>
                                                                                        <c:when test="${field.type == 'date'}">
                                                                                            <mm:include page="bdate.jsp">
                                                                                                <mm:param name="bname" value="${field.name}"  />
                                                                                                <mm:param name="bvalue" value="${field.value}" />
                                                                                            </mm:include>
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                </mm:compare>
                                                                                <mm:compare value="false">${field.value}</mm:compare>
                                                                            </mm:field>
                                                                        </span>
                                                                    </div>
                                                                </mm:nodelistfunction>

                                                                <%--  now show gender, new password, confirm new passwod--%>
                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Gender"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="gender">
                                                                            <select name="newgender">
                                                                                <mm:compare value="male">
                                                                                    <option value="male"><mm:write referid="mlg.Male"/></option>
                                                                                    <option value="female"><mm:write referid="mlg.Female"/></option>
                                                                                </mm:compare>
                                                                                <mm:compare value="male" inverse="true">
                                                                                    <option value="female"><mm:write referid="mlg.Female"/></option>
                                                                                    <option value="male"><mm:write referid="mlg.Male"/></option>
                                                                                </mm:compare>
                                                                            </select>
                                                                        </mm:field>
                                                                  </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.NewPassword"/></span>
                                                                    <span class="formw">
                                                                        <input name="newpassword" type="password" size="25"/>
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.NewConfirmPassword"/></span>
                                                                    <span class="formw">
                                                                        <input name="newconfirmpassword" type="password" size="25" />
                                                                    </span>
                                                                </div>
                                                            </mm:compare>
                                                            <%--  end login system http--%>



                                                            <%--  login system entree--%>
                                                            <mm:compare referid="loginsystemtype" value="entree">

                                                                <%--  start with the profile values--%>
                                                                <mm:import id="guipos" reset="true">0</mm:import>
                                                                <mm:nodelistfunction set="mmbob" name="getProfileValues" referids="forumid,posterid,guipos" id="pvalues">
                                                                    <div class="row">
                                                                        <span class="label"><mm:field name="guiname" /></span>
                                                                        <span class="formw">
                                                                            <mm:field name="edit">
                                                                                <mm:compare value="true">
                                                                                <%--
                                                                                    <mm:field name="type">
                                                                                        <mm:compare value="string"><input name="${pvalues.name}" type="text" size="25" value="${pvalues.value}"/></mm:compare>
                                                                                        <mm:compare value="field"><textarea rows="7" cols="25" name="${pvalues.name}"><mm:field name="value" /></textarea></mm:compare>
                                                                                        <mm:compare value="date">
                                                                                            <mm:import id="bname"><mm:field name="name" /></mm:import>
                                                                                            <mm:import id="bvalue"><mm:field name="value" /></mm:import>
                                                                                            <mm:include page="bdate.jsp" referids="bname,bvalue" />
                                                                                        </mm:compare>
                                                                                    </mm:field>
                                                                                    --%>
                                                                                    <c:choose>
                                                                                        <c:when test="${pvalues.type == 'string'}"> <input name="${pvalues.name}" type="text" size="25" value="${pvalues.value}"/></c:when>
                                                                                        <c:when test="${pvalues.type == 'field'}"><textarea rows="7" cols="25" name="${pvalues.name}">${pvalues.value}</textarea></c:when>
                                                                                        <c:when test="${pvalues.type == 'date'}">
                                                                                            <mm:include page="bdate.jsp">
                                                                                                <mm:param name="bname" value="${pvalues.name}"  />
                                                                                                <mm:param name="bvalue" value="${pvalues.value}" />
                                                                                            </mm:include>
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                </mm:compare>
                                                                                <mm:compare value="false">${pvalues.value}</mm:compare>
                                                                            </mm:field>
                                                                        </span>
                                                                    </div>
                                                                </mm:nodelistfunction>

                                                                <%--  now show the default fields, but you can't edit them here.--%>
                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Firstname"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="firstname" /> (entree)
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Lastname"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="lastname" /> (entree)
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Email"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="email" /> (entree)
                                                                </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Location"/></span>
                                                                    <span class="formw">
                                                                        <%--
                                                                        <input name="newlocation" type="text" size="25" value="<mm:field name="location" />"/>
                                                                        --%>
                                                                        <input name="newlocation" type="text" size="25" value="${pinfo.location}"/>
                                                                    </span>
                                                                </div>

                                                                <div class="row">
                                                                    <span class="label"><mm:write referid="mlg.Gender"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="gender">
                                                                            <select name="newgender">
                                                                                <mm:compare value="male">
                                                                                    <option value="male"><mm:write referid="mlg.Male"/></option>
                                                                                    <option value="female"><mm:write referid="mlg.Female"/></option>
                                                                                </mm:compare>
                                                                                <mm:compare value="male" inverse="true">
                                                                                    <option value="female"><mm:write referid="mlg.Female"/></option>
                                                                                    <option value="male"><mm:write referid="mlg.Male"/></option>
                                                                                </mm:compare>
                                                                            </select>
                                                                        </mm:field>
                                                                    </span>
                                                                </div>

                                                                <mm:write referid="entree" >
                                                                    <input name="newpassword" type="hidden" value="${_}" />
                                                                    <input name="newconfirmpassword" type="hidden" value="${_}" />
                                                                </mm:write>
                                                            </mm:compare>
                                                            <%--  end login system entree--%>

                                                            <%-- TODO: not yet implemented
                                                              <div class="row">
                                                              <span class="label"><mm:write referid="mlg.Level"/></span>
                                                              <span class="formw">
                                                                level123
                                                              </span>
                                                            </div> --%>

                                                            <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Posts"/></span>
                                                                <span class="formw">
                                                                    <mm:field name="accountpostcount" />
                                                                </span>
                                                            </div>

                                                            <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Member_since"/></span>
                                                                <span class="formw">
                                                                    <mm:field name="firstlogin"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                                                </span>
                                                            </div>

                                                            <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Last_visit"/></span>
                                                                <span class="formw">
                                                                    <mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                                                </span>
                                                            </div>
                                                        </mm:nodefunction>
                                                    </mm:functioncontainer>
                                                </mm:node>

                                                <div class="row">
                                                    <span class="label"></span>
                                                    <span class="formw">
                                                        <input type="submit" value="<mm:write referid="mlg.Save"/>" />
                                                    </span>
                                                </div>

                                            </form>
                                        </mm:link>


                                    </mm:compare>
                                    <%--  end profile section of the page--%>


                                    <%--  contact section of the page--%>
                                    <mm:compare value="contact" referid="profile">
                                        <div class="row" align="left">
                                            <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                                <mm:field name="contactinfoenabled">
                                                    <mm:compare referid="adminmode" value="true">
                                                        <mm:nodelistfunction set="mmbob" name="getRemoteHosts" referids="forumid,profileid@posterid">
                                                            <mm:compare referid="adminmode" value="true">
                                                                host : <mm:field name="host" /> lastchange : <mm:field name="lastupdatetime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field> updatecount : <mm:field name="updatecount" /><br />
                                                            </mm:compare>
                                                        </mm:nodelistfunction>
                                                    </mm:compare>
                                                </mm:field>
                                            </mm:nodefunction>
                                        </div>
                                    </mm:compare>
                                    <%--  end contact section of the page--%>

                                    <%--  signature part--%>
                                    <mm:compare value="signatures" referid="profile">
                                        <table class="layout" border="2">
                                            <mm:import id="maxsig">1</mm:import>
                                            <mm:import externid="feedback_message" from="session" id="feedback"/>
                                            <mm:present referid="feedback">
                                                <mm:compare value="signaturesaved" referid="feedback">
                                                    <tr>
                                                        <td align="left">
                                                            <font color="red"><b>** <mm:write referid="mlg.SignatureSaved"/> **</b></font>
                                                            <br />
                                                            <br />
                                                        </td>
                                                    </tr>
                                                </mm:compare>
                                            </mm:present>

                                            <mm:compare referid="maxsig" value="1">
                                                <mm:nodefunction set="mmbob" name="getSingleSignature" referids="forumid,posterid">
                                                    <mm:link page="profile.jsp" referids="forumid,posterid,profile" >
                                                        <form action="${_}" method="post">
                                                            <input type="hidden" name="action" value="setsinglesignature" />
                                                            <tr>
                                                                <td>
                                                                    <textarea name="newbody" rows="5" style="width: 100%"><mm:field name="body" /></textarea>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td align="middle"><center><input type="submit" value="${mlg.Save}" /></center> </td>
                                                            </tr>
                                                        </form>
                                                    </mm:link>
                                                </mm:nodefunction>
                                            </mm:compare>
                                        </table>

                                        <mm:compare referid="maxsig" value="1" inverse="true">
                                            <table>
                                                <mm:nodelistfunction set="mmbob" name="getSignatures" referids="forumid,posterid">
                                                    <mm:link page="profile.jsp" referids="forumid,posterid,profile" >
                                                        <form action="" method="post">
                                                            <input type="hidden" name="action" value="changesignature" />
                                                            <input type="hidden" name="sigid" value="<mm:field name="id" />" />
                                                            <tr>
                                                                <td align="middle">
                                                                    <select name="newmode">
                                                                        <mm:field name="mode">
                                                                            <c:choose>
                                                                                <c:when test="${_ == 'active'}"> <c:set var="actives" value="selected"/> </c:when>
                                                                                <c:when test="${_ == 'inactive'}"> <c:set var="inactives" value="selected"/> </c:when>
                                                                            </c:choose>
                                                                        </mm:field>
                                                                        <option value="active" ${actives} >Active</option>
                                                                        <option value="inactive" ${inactives} >Inactive</option>
                                                                        <option value="delete">Delete</option>
                                                                    </select>
                                                                    <br /><br />
                                                                    <input type="submit" value="save" />
                                                                </td>
                                                                <td>
                                                                    <textarea name="newbody" rows="5" cols="45"><mm:field name="body" /></textarea>
                                                                </td>
                                                            </tr>
                                                        </form>
                                                    </mm:link>
                                                </mm:nodelistfunction>

                                                <mm:link page="profile.jsp" referids="forumid,posterid,profile" >
                                                    <form action="" method="post">
                                                        <input type="hidden" name="action" value="addsignature" />
                                                        <tr>
                                                            <td align="middle">
                                                                <select name="newmode">
                                                                    <option value="create">Create</option>
                                                                </select>
                                                                <br /><br />
                                                                <input type="submit" value="save" />
                                                            </td>
                                                            <td>
                                                                <textarea name="newbody" rows="5" cols="45"></textarea>
                                                            </td>
                                                        </tr>
                                                    </form>
                                                </mm:link>
                                            </table>
                                        </mm:compare>

                                    </mm:compare>
                                    <%--  end signature part--%>


                                    <%--  avatar part--%>
                                    <mm:compare value="avatar" referid="profile">
                                        <mm:link page="actions_avatar.jsp">
                                            <mm:param name="forumid" value="$forumid" />
                                            <mm:present referid="postareaid">
                                                <mm:param name="postareaid" value="$postareaid" />
                                            </mm:present>
                                            <mm:param name="posterid" value="$posterid" />
                                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                            <mm:param name="profile" value="$profile" />
                                            <mm:param name="referrer" value="profile.jsp" />

                                            <form name="uploadform" enctype="multipart/form-data" action="${_}" method="post">
                                                <mm:node number="$profileid">
                                                    <mm:functioncontainer>
                                                        <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
                                                        <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
                                                            <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Current_avatar"/></span>
                                                                <span class="formw">
                                                                    <mm:field name="avatar">
                                                                        <mm:compare value="-1" inverse="true">
                                                                            <mm:node number="$_"><mm:image template="s(80x80)" ><img src="${_}" width="80" border="0"></mm:image></mm:node>
                                                                        </mm:compare>
                                                                    </mm:field>
                                                                    <%--
                                                                    <mm:related path="rolerel,images"
                                                                                fields="rolerel.role,images.number"
                                                                                constraints="rolerel.role='avatar'"
                                                                                orderby="rolerel.number"
                                                                                directions="down"
                                                                                max="1">
                                                                      <mm:node element="images">
                                                                        <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                                                                      </mm:node>
                                                                    </mm:related>--%>

                                                                </span>
                                                            </div>
                                                        </mm:nodefunction>
                                                    </mm:functioncontainer>
                                                </mm:node>

                                                <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                                    <mm:field name="avatarsuploadenabled">
                                                          <mm:compare value="true">
                                                              <div class="row">
                                                                  <span class="label"><mm:write referid="mlg.Upload_avatar"/></span>
                                                                  <span class="formw">
                                                                    <mm:fieldlist nodetype="images" node="" fields="handle"/>
                                                                    <input type="file" name="_handle" id="mm_handle" onChange="document.uploadform.addavatar.disabled=false" />
                                                                    <input disabled="true" type="submit" name="addavatar" value="${mlg.Upload}" />
                                                                </span>
                                                            </div>
                                                        </mm:compare>
                                                    </mm:field>
                                                    <mm:field name="avatarsgalleryenabled">
                                                        <mm:compare value="true">
                                                            <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Select_avatar_from_the_list"/></span>
                                                                <span class="formw">
                                                                    <input type="submit" name="selectavatar" value="${mlg.Select}"/>
                                                                </span>
                                                            </div>
                                                        </mm:compare>
                                                    </mm:field>
                                                </mm:nodefunction>
                                                <div class="spacer">&nbsp;</div>
                                            </form>
                                        </mm:link>
                                    </mm:compare>
                                    <%--  end avatar part--%>
                                </div> <!--  end div 'profile'-->

                            </mm:compare>
                            <%-- end own profile (editable)--%>

                            <%-- other profile (non-editable) --%>
                            <mm:compare referid="profileid" referid2="posterid" inverse="true">
                                <div id="tabs">
                                    <ul>
                                        <mm:remove referid="selected"/>
                                        <c:if test="${profile == 'personal'}"><mm:import id="selected" reset="true">class="selected"</mm:import></c:if>
                                        <li ${selected}>
                                            <mm:link page="profile.jsp" referids="forumid,profileid@posterid">
                                                <mm:present referid="postareaid">
                                                    <mm:param name="postareaid" value="$postareaid" />
                                                </mm:present>
                                                <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                                <mm:param name="profile" value="personal" />
                                                <a href="${_}"><mm:write referid="mlg.personal"/></a>
                                            </mm:link>
                                        </li>

                                        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                            <mm:field name="contactinfoenabled">
                                                <mm:compare value="true">
                                                    <mm:remove referid="selected"/>
                                                    <c:if test="${profile == 'contact'}"><mm:import id="selected" reset="true">class="selected"</mm:import></c:if>
                                                    <li ${selected} >
                                                        <mm:link page="profile.jsp" referids="forumid,profileid@posterid">
                                                            <mm:present referid="postareaid">
                                                                <mm:param name="postareaid" value="$postareaid" />
                                                            </mm:present>
                                                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                                            <mm:param name="profile" value="contact" />
                                                            <a href="${_}"><mm:write referid="mlg.contact"/></a>
                                                        </mm:link>
                                                    </li>
                                                </mm:compare>
                                            </mm:field>
                                        </mm:nodefunction>

                                        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                            <mm:field name="avatarsdisabled">
                                                <mm:compare value="false">
                                                    <mm:remove referid="selected"/>
                                                    <c:if test="${profile == 'avatar'}"><mm:import id="selected" reset="true">class="selected"</mm:import></c:if>
                                                    <li ${selected} >
                                                        <mm:link page="profile.jsp" referids="forumid,profileid@posterid">
                                                            <mm:present referid="postareaid">
                                                                <mm:param name="postareaid" value="$postareaid" />
                                                            </mm:present>
                                                            <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
                                                            <mm:param name="profile" value="avatar" />
                                                            <a href="${_}"><mm:write referid="mlg.avatar"/></a>
                                                        </mm:link>
                                                    </li>
                                                </mm:compare>
                                            </mm:field>
                                        </mm:nodefunction>
                                    </ul>
                                </div><!--end div 'tabs'-->

                                <div id="profile">
                                    <mm:compare value="personal" referid="profile">

                                        <mm:node number="$profileid">
                                            <mm:functioncontainer>
                                            <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
                                                <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Account"/></span>
                                                        <span class="formw"><mm:field name="nick" /></span>
                                                    </div>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Firstname"/></span>
                                                        <span class="formw"><mm:field name="firstname" /></span>
                                                    </div>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Lastname"/></span>
                                                        <span class="formw"><mm:field name="lastname" /></span>
                                                    </div>

                                                    <%-- TODO: gebruiker moet zelf kiezen of email getoond wordt of niet, voor nu default niet
                                                    <div class="row">
                                                    <span class="label"><mm:write referid="mlg.Email"/></span>
                                                    <span class="formw"><mm:field name="email" /></span>
                                                    </div>--%>
                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Location"/></span>
                                                        <span class="formw"><mm:field name="location" /></span>
                                                    </div>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Gender"/></span>
                                                        <span class="formw">
                                                            <mm:field name="gender">
                                                                <mm:compare value="male"><mm:write referid="mlg.Male"/></mm:compare>
                                                                <mm:compare value="female"><mm:write referid="mlg.Female"/></mm:compare>
                                                            </mm:field>
                                                        </span>
                                                    </div>

                                                    <%-- TODO: not yet implemented
                                                    <div class="row">
                                                    <span class="label"><mm:write referid="mlg.Level"/></span>
                                                    <span class="formw">level123</span>
                                                    </div>--%>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Messages"/></span>
                                                        <span class="formw"><mm:field name="accountpostcount" /></span>
                                                    </div>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Member_since"/></span>
                                                        <span class="formw">
                                                            <mm:field name="firstlogin"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                                        </span>
                                                    </div>

                                                    <div class="row">
                                                        <span class="label"><mm:write referid="mlg.Last_visit"/></span>
                                                        <span class="formw">
                                                            <mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
                                                        </span>
                                                    </div>
                                                </mm:nodefunction>
                                            </mm:functioncontainer>
                                        </mm:node>
                                    </mm:compare>

                                    <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                        <mm:field name="contactinfoenabled">
                                            <mm:compare value="true">
                                                <mm:compare value="contact" referid="profile">
                                                    <mm:nodelistfunction set="mmbob" name="getRemoteHosts" referids="forumid,profileid@posterid">
                                                        <mm:compare referid="adminmode" value="true">
                                                            host : <mm:field name="host" /> lastchange : <mm:field name="lastupdatetime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field> updatecount : <mm:field name="updatecount" /><br />
                                                        </mm:compare>
                                                    </mm:nodelistfunction>
                                                </mm:compare>
                                            </mm:compare>
                                        </mm:field>
                                    </mm:nodefunction>

                                    <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
                                        <mm:field name="avatarsdisabled">
                                            <mm:compare value="false">
                                                <mm:compare value="avatar" referid="profile">
                                                    <mm:node number="$profileid">
                                                        <mm:functioncontainer>
                                                        <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
                                                            <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
                                                                <div class="row">
                                                                <span class="label"><mm:write referid="mlg.Avatar"/></span>
                                                                    <span class="formw">
                                                                        <mm:field name="avatar">
                                                                            <mm:compare value="-1" inverse="true">
                                                                                <mm:node number="$_">
                                                                                    <mm:image template="s(80x80)"> <img src="${_}" width="80" border="0"></mm:image>
                                                                                </mm:node>
                                                                            </mm:compare>
                                                                        </mm:field>
                                                                    </span>
                                                                </div>
                                                            </mm:nodefunction>
                                                        </mm:functioncontainer>
                                                    </mm:node>
                                                </mm:compare>
                                            </mm:compare>

                                        </mm:field>
                                    </mm:nodefunction>

                                    <div class="spacer">&nbsp;</div>
                                </div> <!--end div 'profile'-->
                            </mm:compare>
                            <%-- end other profile (non-editable) --%>
                        </div> <!--end div id: profileb-->

                        <div class="footer">
                            <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
                            <jsp:include page="<%=footerpath%>"/>
                        </div>

                    </div> <!--end bodypart-->
                </body>
            </html>
        </mm:locale>
    </mm:content>
</mm:cloud>
