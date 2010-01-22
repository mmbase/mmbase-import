<%@ include file="../jspbase.jsp" %>
<mm:cloud method="delegate" authenticate="class">
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="action" />
<mm:import externid="forumid" />
<mm:import externid="folderaction" />

<!-- login part -->
<%@ include file="../getposterid.jsp" %>
<!-- end login part -->
<%-- grmbl....this way this file will be include twice, but it's needed here --%>
<%-- TODO: fix this --%>
<%@ include file="../thememanager/loadvars.jsp" %>
<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>
<mm:import id="adminmode">false</mm:import>
<mm:import externid="admincheck" />
<mm:present referid="admincheck">
      <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
        <mm:remove referid="adminmode" />
        <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
      </mm:nodefunction>
</mm:present>

<mm:import id="moderatormode">false</mm:import>
<mm:import externid="moderatorcheck" />
<mm:present referid="moderatorcheck">
          <mm:import externid="postareaid" />
          <mm:import externid="page">1</mm:import>
          <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
                <mm:remove referid="moderatormode" />
                <mm:import id="moderatormode"><mm:field name="ismoderator" /></mm:import>
          </mm:nodefunction>
          <mm:remove referid="postareaid"/>
</mm:present>

<mm:compare value="postreply" referid="action">
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
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


<mm:compare value="changesignature" referid="action">
    <mm:import externid="sigid" />
    <mm:import externid="newbody" />
    <mm:import externid="newmode" />
    <mm:import externid="newencoding">plain</mm:import>
    <mm:function set="mmbob" name="changeSignature" referids="forumid,posterid,sigid,newbody,newmode,newencoding" />
</mm:compare>

<mm:compare value="setsinglesignature" referid="action">
    <mm:import externid="newbody" />
    <mm:import externid="newencoding">plain</mm:import>
    <mm:function set="mmbob" name="setSingleSignature" referids="forumid,posterid,newbody,newencoding" />
    <mm:import id="feedback">signaturesaved</mm:import>
        <mm:write referid="feedback" session="feedback_message"/>
</mm:compare>

<mm:compare value="addsignature" referid="action">
    <mm:import externid="newbody" />
    <mm:import externid="newmode" />
    <mm:import externid="newencoding">plain</mm:import>
    <mm:function set="mmbob" name="addSignature" referids="forumid,posterid,newbody,newmode,newencoding" />
</mm:compare>


<mm:compare value="removefolder" referid="action">
    <mm:import externid="foldername" />
    <mm:booleanfunction set="mmbob" name="removeFolder" referids="forumid,posterid,foldername">
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="threademailoff" referid="action">
    <mm:import externid="postthreadid" />
    <mm:import id="state">false</mm:import>
    <mm:booleanfunction set="mmbob" name="setEmailOnChange" referids="forumid,postthreadid,posterid,state">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="threademailon" referid="action">
    <mm:import externid="postthreadid" />
    <mm:import id="state">true</mm:import>
    <mm:booleanfunction set="mmbob" name="setEmailOnChange" referids="forumid,postthreadid,posterid,state">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="bookmarkedoff" referid="action">
    <mm:import externid="postthreadid" />
    <mm:import id="state">false</mm:import>
    <mm:booleanfunction set="mmbob" name="setBookmarkedChange" referids="forumid,postthreadid,posterid,state">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="bookmarkedon" referid="action">
    <mm:import externid="postthreadid" />
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
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
    <mm:import externid="postingid" />
    <mm:import externid="subject" />
    <mm:import externid="body" />
    <mm:booleanfunction set="mmbob" name="editPost" referids="forumid,postareaid,postthreadid,postingid,posterid,subject,body,imagecontext">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="editpostthread" referid="action">
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
    <mm:import externid="mood" />
    <mm:import externid="state" />
    <mm:import externid="ttype" id="type" />
    <mm:booleanfunction set="mmbob" name="editPostThread" referids="forumid,postareaid,postthreadid,posterid,mood,state,type">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="movepostthread" referid="action">
    <mm:import externid="postareaid" />
    <mm:import externid="postthreadid" />
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
    <mm:import id="profileid" externid="profileid" />
    <mm:import id="newconfirmpassword" externid="newconfirmpassword" />
    <mm:import id="feedback"><mm:function set="mmbob" name="editProfilePoster" referids="forumid,posterid,profileid,firstname,lastname,email,gender,location,newpassword,newconfirmpassword"/></mm:import>
        <mm:write referid="feedback" session="feedback_message"/>

        <mm:compare referid="feedback" value="passwordchanged">
          <mm:write referid="newpassword" cookie="cwf$forumid" />
        </mm:compare>

          <mm:import id="guipos">-1</mm:import>
         <mm:nodelistfunction set="mmbob" name="getProfileValues" referids="forumid,profileid@posterid,guipos">
        <mm:import id="pname" reset="true"><mm:field name="name" /></mm:import>
        <mm:field name="type">
        <mm:compare value="string">
        <mm:import externid="$pname" id="pvalue" reset="true" />
        <mm:import id="fb2" reset="true"><mm:function set="mmbob" name="setProfileValue" referids="forumid,profileid@posterid,pname,pvalue"/></mm:import>
        </mm:compare>
        <mm:compare value="field">
        <mm:import externid="$pname" id="pvalue" reset="true" />
        <mm:import id="fb2" reset="true"><mm:function set="mmbob" name="setProfileValue" referids="forumid,profileid@posterid,pname,pvalue"/></mm:import>
        </mm:compare>
        <mm:compare value="date">
        <mm:import externid="birthday_day" />
        <mm:import externid="birthday_month" />
        <mm:import externid="birthday_year" />
        <mm:import id="pvalue" reset="true"><mm:write referid="birthday_day" />-<mm:write referid="birthday_month" />-<mm:write referid="birthday_year" /></mm:import>
        <mm:import id="fb2" reset="true"><mm:function set="mmbob" name="setProfileValue" referids="forumid,profileid@posterid,pname,pvalue"/></mm:import>
        </mm:compare>
        </mm:field>
     </mm:nodelistfunction>
 </mm:compare>

<mm:compare value="true" referid="adminmode">
<mm:compare value="changepostarea" referid="action">
    <mm:import externid="name" />
    <mm:import externid="description" />
    <mm:import externid="postareaid" />
    <mm:booleanfunction set="mmbob" name="changePostArea" referids="forumid,postareaid,name,description,posterid@activeid">
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="changeforum" referid="action">
    <mm:import externid="name" />
    <mm:import externid="newlang" />
    <mm:import externid="description" />
    <mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,newlang,description,posterid@activeid" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="addwordfilter" referid="action">
    <mm:import externid="name" />
    <mm:import externid="value" />
    <mm:booleanfunction set="mmbob" name="addWordFilter" referids="forumid,name,value,posterid@activeid" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="removewordfilter" referid="action">
    <mm:import externid="name" />
    <mm:booleanfunction set="mmbob" name="removeWordFilter" referids="forumid,name,posterid@activeid" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="changerules" referid="action">
    <mm:import externid="rulesid" />
    <mm:import externid="title" />
    <mm:import externid="body" />
    <mm:node referid="rulesid">
        <mm:setfield name="title"><mm:write referid="title" /></mm:setfield>
        <mm:setfield name="body"><mm:write referid="body" /></mm:setfield>
    </mm:node>
</mm:compare>

<mm:compare value="addrules" referid="action">
    <mm:import externid="title" />
    <mm:import externid="body" />
    <mm:node id="forumnode" referid="forumid" />
    <mm:createnode id="rulesid" type="forumrules">
        <mm:setfield name="title"><mm:write referid="title" /></mm:setfield>
        <mm:setfield name="body"><mm:write referid="body" /></mm:setfield>
    </mm:createnode>
    <mm:createrelation role="related" source="forumnode" destination="rulesid" />
</mm:compare>

<mm:compare value="changeconfig" referid="action">
    <mm:import externid="loginsystemtype" />
    <mm:import externid="loginmodetype" />
    <mm:import externid="logoutmodetype" />
    <mm:import externid="guestreadmodetype" />
    <mm:import externid="guestwritemodetype" />
    <mm:import externid="avatarsuploadenabled" />
    <mm:import externid="avatarsgalleryenabled" />
    <mm:import externid="navigationmethod" />
    <mm:import externid="alias" />
    <mm:booleanfunction set="mmbob" name="changeForumConfig" referids="forumid,loginsystemtype,loginmodetype,logoutmodetype,guestreadmodetype,guestwritemodetype,avatarsuploadenabled,avatarsgalleryenabled,navigationmethod,alias,posterid@activeid" >
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="changelayout" referid="action">
    <mm:import id="postcount" externid="forumpostingsperpage" />
    <mm:booleanfunction set="mmbob" name="changeForumPostingsPerPage" referids="forumid,posterid,postcount" >
    </mm:booleanfunction>
    <mm:import reset="true" id="count" externid="forumpostingsoverflowpostarea" />
    <mm:booleanfunction set="mmbob" name="changeForumPostingsOverflowPostArea" referids="forumid,posterid,count" >
    </mm:booleanfunction>
    <mm:import reset="true" id="count" externid="forumpostingsoverflowthreadpage" />
    <mm:booleanfunction set="mmbob" name="changeForumPostingsOverflowThreadPage" referids="forumid,posterid,count" >
    </mm:booleanfunction>
    <mm:import reset="true" id="delay" externid="forumspeedposttime" />
    <mm:booleanfunction set="mmbob" name="changeForumSpeedPostTime" referids="forumid,posterid,delay" >
    </mm:booleanfunction>
    <mm:import reset="true" id="value" externid="forumreplyoneachpage" />
    <mm:booleanfunction set="mmbob" name="changeForumReplyOnEachPage" referids="forumid,posterid,value" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="changethemedefault" referid="action">
    <mm:import externid="svalue" />
        <mm:import externid="sname" />
    <mm:booleanfunction set="thememanager" name="setCSSValue" referids="sname,svalue" >
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="changethemecolor" referid="action">
    <mm:import externid="svalue" />
        <mm:import externid="sname" />
    <mm:booleanfunction set="thememanager" name="setCSSValue" referids="sname,svalue" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="changethemefont" referid="action">
    <mm:import externid="svalue" />
        <mm:import externid="sname" />
    <mm:booleanfunction set="thememanager" name="setCSSValue" referids="sname,svalue" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="changethemefontsize" referid="action">
    <mm:import externid="svalue" />
        <mm:import externid="sname" />
    <mm:booleanfunction set="thememanager" name="setCSSValue" referids="sname,svalue" >
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="changepostareaconfig" referid="action">
    <mm:import externid="postareaid" />
    <mm:import externid="guestreadmodetype" />
    <mm:import externid="guestwritemodetype" />
    <mm:import externid="threadstartlevel" />
    <mm:import externid="position" />
    <mm:booleanfunction set="mmbob" name="changePostAreaConfig" referids="forumid,postareaid,guestreadmodetype,guestwritemodetype,threadstartlevel,position,posterid@activeid" >
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="newmoderator" referid="action">
    <mm:import externid="newmoderator" />
    <mm:import externid="postareaid" />
    <mm:booleanfunction set="mmbob" name="newModerator" referids="forumid,postareaid,posterid,newmoderator">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="newadministrator" referid="action">
    <mm:import externid="newadministrator" />
    <mm:booleanfunction set="mmbob" name="newAdministrator" referids="forumid,posterid,newadministrator">
    </mm:booleanfunction>
</mm:compare>

<mm:compare value="removemoderator" referid="action">
    <mm:import externid="remmoderator" />
    <mm:import externid="postareaid" />
    <mm:booleanfunction set="mmbob" name="removeModerator" referids="forumid,postareaid,posterid,remmoderator">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="removeadministrator" referid="action">
    <mm:import externid="remadministrator" />
    <mm:booleanfunction set="mmbob" name="removeAdministrator" referids="forumid,posterid,remadministrator">
    </mm:booleanfunction>
</mm:compare>


<mm:compare value="removeposter" referid="action">
     <mm:import externid="removeposterid" />
         <mm:booleanfunction set="mmbob" name="removePoster" referids="forumid,removeposterid,posterid">
        </mm:booleanfunction>
</mm:compare>

<mm:compare value="disableposter" referid="action">
         <mm:import externid="disableposterid" />
         <mm:booleanfunction set="mmbob" name="disablePoster" referids="forumid,disableposterid,posterid">
        </mm:booleanfunction>
</mm:compare>

<mm:compare value="enableposter" referid="action">
         <mm:import externid="enableposterid" />
         <mm:booleanfunction set="mmbob" name="enablePoster" referids="forumid,enableposterid,posterid">
        </mm:booleanfunction>
</mm:compare>

</mm:compare>

<mm:compare value="true" referid="moderatormode">

<mm:compare value="removepostthread" referid="action">
        <mm:import externid="postareaid" />
        <mm:import externid="postthreadid" />
        <mm:booleanfunction set="mmbob" name="removePostThread" referids="forumid,postareaid,postthreadid">
        </mm:booleanfunction>
</mm:compare>

</mm:compare>

<mm:compare value="removepost" referid="action">
   <%-- moderators may alway remove postings --%>
   <mm:compare value="true" referid="moderatormode">
        <mm:import externid="postareaid" />
        <mm:import externid="postthreadid" />
        <mm:import externid="postingid" />
        <mm:booleanfunction set="mmbob" name="removePost" referids="forumid,postareaid,postthreadid,postingid,posterid">
        </mm:booleanfunction>
   </mm:compare>

   <%-- users may remove their own postings --%>
   <mm:compare value="true" referid="moderatormode" inverse="true">
        <mm:import externid="postareaid" />
        <mm:import externid="postthreadid" />
        <mm:import externid="postingid" />
        <mm:booleanfunction set="mmbob" name="removePost" referids="forumid,postareaid,postthreadid,postingid,posterid"></mm:booleanfunction>
   </mm:compare>
</mm:compare>

</mm:locale>
</mm:content>
</mm:cloud>
