<%@ include file="jspbase.jsp" %>
<mm:cloud>
    <mm:import externid="action" />
    <mm:import externid="forumid" />
    <mm:import externid="folderaction" />
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
    <mm:import externid="delpostingid" />
    <mm:import externid="newbody" />
    <mm:import externid="newmode" />
    <mm:import externid="newencoding">plain</mm:import>

    <!-- login part -->
    <%@ include file="getposterid.jsp" %>
    <!-- end login part -->

    <%@ include file="thememanager/loadvars.jsp" %>

    <mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">

        <mm:locale language="$lang">
            <%@ include file="loadtranslations.jsp" %>

            <mm:import id="adminmode">false</mm:import>
            <mm:import externid="admincheck" />
            <mm:present referid="admincheck">
                <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
                    <mm:import id="adminmode" reset="true"><mm:field name="isadministrator" /></mm:import>
                </mm:nodefunction>
            </mm:present>

            <mm:import id="moderatormode">false</mm:import>
            <mm:import externid="moderatorcheck" />
            <mm:present referid="moderatorcheck">
                  <mm:import externid="page">1</mm:import>
                  <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
                        <mm:import id="moderatormode" reset="true"><mm:field name="ismoderator" /></mm:import>
                  </mm:nodefunction>
            </mm:present>

            <%--actions--%>
            <mm:compare value="postreply" referid="action">
                    <mm:import externid="poster"/>
                    <mm:compare referid="posterid" value="-1">
                      <mm:import reset="true" id="poster"><mm:write referid="poster"/> (<mm:write referid="mlg.not_registered" />)</mm:import>
                    </mm:compare>
                <mm:import externid="subject" />
                <mm:import externid="body" />
                <mm:nodefunction set="mmbob" name="postReply" referids="forumid,postareaid,postthreadid,poster,subject,body">
                    <mm:write referid="body" session="body" />
                    <mm:import id="error"><mm:field name="error" /></mm:import>
                    <mm:import id="speedposttime"><mm:field name="speedposttime" /></mm:import>
                    <mm:write referid="speedposttime" session="speedposttime" />
                    <mm:write referid="error" session="error" />
                </mm:nodefunction>
            </mm:compare>

            <mm:compare value="newfolder" referid="action">
                <mm:import externid="newfolder" />
                <mm:nodefunction set="mmbob" name="newFolder" referids="forumid,posterid,newfolder">
                </mm:nodefunction>
            </mm:compare>

            <mm:compare value="removefolder" referid="action">
                <mm:import externid="foldername" />
                <mm:booleanfunction set="mmbob" name="removeFolder" referids="forumid,posterid,foldername">
                </mm:booleanfunction>
            </mm:compare>


            <mm:compare value="changesignature" referid="action">
                <mm:import externid="sigid" />
                <mm:function set="mmbob" name="changeSignature" referids="forumid,posterid,sigid,newbody,newmode,newencoding" />
            </mm:compare>

            <mm:compare value="setsinglesignature" referid="action">
                <mm:function set="mmbob" name="setSingleSignature" referids="forumid,posterid,newbody,newencoding" />
                <mm:write value="signaturesaved" session="feedback_message"/>
            </mm:compare>

            <mm:compare value="addsignature" referid="action">
                <mm:function set="mmbob" name="addSignature" referids="forumid,posterid,newbody,newmode,newencoding" />
            </mm:compare>




            <mm:compare value="threademailoff" referid="action">
                <mm:import id="state">false</mm:import>
                <mm:booleanfunction set="mmbob" name="setEmailOnChange" referids="forumid,postthreadid,posterid,state">
                </mm:booleanfunction>
            </mm:compare>


            <mm:compare value="threademailon" referid="action">
                <mm:import id="state">true</mm:import>
                <mm:booleanfunction set="mmbob" name="setEmailOnChange" referids="forumid,postthreadid,posterid,state">
                </mm:booleanfunction>
            </mm:compare>



            <mm:compare value="bookmarkedoff" referid="action">
                <mm:import id="state">false</mm:import>
                <mm:booleanfunction set="mmbob" name="setBookmarkedChange" referids="forumid,postthreadid,posterid,state">
                </mm:booleanfunction>
            </mm:compare>


            <mm:compare value="bookmarkedon" referid="action">
                <mm:import id="state">true</mm:import>
                <mm:booleanfunction set="mmbob" name="setBookmarkedChange" referids="forumid,postthreadid,posterid,state">
                </mm:booleanfunction>
            </mm:compare>

            <mm:compare value="removeprivatemessage" referid="action">
                <mm:import externid="mailboxid" />
                <mm:import externid="messageid" />
                <mm:node referid="messageid">
                    <mm:deletenode deleterelations="true" />
                            <mm:import id="mbn"><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:import>
                        <mm:import id="resultcode" ><mm:function set="mmbob" name="signalMailboxChange" referids="forumid,posterid,mbn@mailboxid" /></mm:import>
                </mm:node>
            </mm:compare>


            <mm:compare value="newprivatemessage" referid="action">
                <mm:import externid="poster" />
                <mm:import externid="to" />
                <mm:import externid="subject" />
                <mm:import externid="body" />
                <mm:nodefunction set="mmbob" name="newPrivateMessage" referids="forumid,to,poster,subject,body">
                </mm:nodefunction>
            </mm:compare>


            <mm:compare value="editpost" referid="action">
                <mm:import externid="postingid" />
                <mm:import externid="subject" />
                <mm:import externid="body" />
                <mm:booleanfunction set="mmbob" name="editPost" referids="forumid,postareaid,postthreadid,postingid,posterid,subject,body,imagecontext">
                </mm:booleanfunction>
            </mm:compare>


            <mm:compare value="editpostthread" referid="action">
                <mm:import externid="mood" />
                <mm:import externid="state" />
                <mm:import externid="ttype" id="type" />
                <mm:booleanfunction set="mmbob" name="editPostThread" referids="forumid,postareaid,postthreadid,posterid,mood,state,type"/>
            </mm:compare>


            <mm:compare value="movepostthread" referid="action">
                <mm:import externid="newpostareaid" />
                <mm:booleanfunction set="mmbob" name="movePostThread" referids="forumid,postareaid,postthreadid,posterid,newpostareaid">
                </mm:booleanfunction>
            </mm:compare>

            <mm:compare value="editposter" referid="action">
                <mm:import id="firstname" externid="newfirstname" />
                <mm:import id="lastname" externid="newlastname" />
                <mm:import id="email" externid="newemail" />
                <mm:import id="gender" externid="newgender" />
                <mm:import id="location" externid="newlocation" />
                <mm:import id="newpassword" externid="newpassword" />
                <mm:import id="newconfirmpassword" externid="newconfirmpassword" />
                <mm:import id="feedback"><mm:function set="mmbob" name="editPoster" referids="forumid,posterid,firstname,lastname,email,gender,location,newpassword,newconfirmpassword"/></mm:import>
                <mm:write referid="feedback" session="feedback_message"/>

                <mm:compare referid="feedback" value="passwordchanged">
                    <mm:write referid="newpassword" id="cwf$forumid" session="mmbobe" />
                </mm:compare>

                <mm:import id="guipos" reset="true">0</mm:import>
                <mm:nodelistfunction set="mmbob" name="getProfileValues" referids="forumid,posterid,guipos" id="field">
                <mm:import id="pname" reset="true"><mm:field name="name" /></mm:import>
                    <c:if test="${field.edit == true}">
                        <c:choose>
                            <c:when test="${field.type == 'date'}">
                                <c:set var="day">${pname}_day</c:set>
                                <c:set var="month">${pname}_month</c:set>
                                <c:set var="year">${pname}_year</c:set>
                                <mm:import id="pvalue" reset="true">${param[day]}-${param[month]}-${param[year]}</mm:import>
                            </c:when>
                            <c:otherwise>
                                <mm:import id="pvalue" reset="true">${param[pname]}</mm:import>
                            </c:otherwise>
                        </c:choose>
                        <mm:import id="fb2" reset="true">
                            <mm:function set="mmbob" name="setProfileValue" referids="forumid,posterid,pname,pvalue"/>
                        </mm:import>
                    </c:if>
                 </mm:nodelistfunction>
             </mm:compare>

             <%--  admin actions--%>
            <mm:compare value="true" referid="adminmode">
                <mm:compare value="removeposter" referid="action">
                     <mm:import externid="removeposterid" />
                     <mm:booleanfunction set="mmbob" name="removePoster" referids="forumid,removeposterid,posterid"/>
                </mm:compare>

                <mm:compare value="disableposter" referid="action">
                     <mm:import externid="disableposterid" />
                     <mm:booleanfunction set="mmbob" name="disablePoster" referids="forumid,disableposterid,posterid"/>
                </mm:compare>

                <mm:compare value="enableposter" referid="action">
                         <mm:import externid="enableposterid" />
                         <mm:booleanfunction set="mmbob" name="enablePoster" referids="forumid,enableposterid,posterid">
                        </mm:booleanfunction>
                </mm:compare>
            </mm:compare>
            <%--  end admin actions--%>

            <%--  moderator actions--%>
            <c:if test="${action=='removepostthread' && moderatormode=='true'}">
                <mm:booleanfunction set="mmbob" name="removePostThread" referids="forumid,postareaid,postthreadid"/>
            </c:if>

            <%--  the function figures out if the user has the right privs.--%>
            <mm:compare value="removepost" referid="action">
                <mm:booleanfunction set="mmbob" name="removePost" referids="forumid,postareaid,postthreadid,delpostingid@postingid,posterid"/>
            </mm:compare>

        </mm:locale>
    </mm:content>
</mm:cloud>
