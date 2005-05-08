<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import id="dac"><mm:function set="mmbob" name="getDefaultAccount" /></mm:import>
<mm:import id="dpw"><mm:function set="mmbob" name="getDefaultPassword" /></mm:import>
</mm:cloud>
<mm:cloud sessionname="forum" username="$dac" password="$dpw">
<mm:import externid="action" />
<mm:import externid="forumid" />
<mm:import externid="folderaction" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->
<%-- grmbl....this way this file will be include twice, but it's needed here --%>
<%-- TODO: fix this --%>
<%@ include file="thememanager/loadvars.jsp" %>

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

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
	<mm:booleanfunction set="mmbob" name="postReply" referids="forumid,postareaid,postthreadid,poster,subject,body">
	</mm:booleanfunction>
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
          <mm:write referid="newpassword" cookie="cwf$forumid" />
        </mm:compare>
 </mm:compare>

<mm:compare value="true" referid="adminmode">
<mm:compare value="newpostarea" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:nodefunction set="mmbob" name="newPostArea" referids="forumid,name,description">
	</mm:nodefunction>
</mm:compare>

<mm:compare value="changepostarea" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="changePostArea" referids="forumid,postareaid,name,description">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="changeforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="newlang" />
	<mm:import externid="description" />
	<mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,newlang,description" >
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


<mm:compare value="changeconfig" referid="action">
	<mm:import externid="loginmodetype" />
	<mm:import externid="logoutmodetype" />
	<mm:import externid="guestreadmodetype" />
	<mm:import externid="guestwritemodetype" />
	<mm:import externid="avatarsuploadenabled" />
	<mm:import externid="avatarsgalleryenabled" />
	<mm:booleanfunction set="mmbob" name="changeForumConfig" referids="forumid,loginmodetype,logoutmodetype,guestreadmodetype,guestwritemodetype,avatarsuploadenabled,avatarsgalleryenabled" >
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="newmoderator" referid="action">
	<mm:import externid="newmoderator" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="newModerator" referids="forumid,postareaid,posterid,newmoderator">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="removemoderator" referid="action">
	<mm:import externid="remmoderator" />
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="removeModerator" referids="forumid,postareaid,posterid,remmoderator">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="removepostarea" referid="action">
	<mm:import externid="postareaid" />
	<mm:booleanfunction set="mmbob" name="removePostArea" referids="forumid,postareaid">
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
        <mm:node referid="postingid">
          <mm:import id="postingowner"><mm:field name="c_poster"/></mm:import> 
          <mm:node referid="posterid">
            <mm:import id="currentaccount"><mm:field name="account" /></mm:import> 
             <mm:compare referid="postingowner" referid2="currentaccount"> 
               <mm:booleanfunction set="mmbob" name="removePost" referids="forumid,postareaid,postthreadid,postingid,posterid"></mm:booleanfunction>
             </mm:compare>
          </mm:node>
        </mm:node> 
   </mm:compare>
</mm:compare>

<mm:compare value="newforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="language" />
	<mm:import id="newaccount" externid="account" />
	<mm:import id="newpassword" externid="password" />
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,newaccount@account,newpassword@password">
	</mm:nodefunction>
</mm:compare>


<mm:compare value="changeconfigs" referid="action">
	<mm:import externid="loginmodetype" />
	<mm:import externid="logoutmodetype" />
	<mm:import externid="guestreadmodetype" />
	<mm:import externid="guestwritemodetype" />
	<mm:import externid="avatarsuploadenabled" />
	<mm:import externid="avatarsgalleryenabled" />
	<mm:import externid="contactinfoenabled" />
	<mm:import externid="smileysenabled" />
	<mm:import externid="privatemessagesenabled" />
	<mm:import externid="postingsperpage" />
	<mm:booleanfunction set="mmbob" name="changeForumsConfig" referids="loginmodetype,logoutmodetype,guestreadmodetype,guestwritemodetype,avatarsuploadenabled,avatarsgalleryenabled,contactinfoenabled,smileysenabled,privatemessagesenabled,postingsperpage" >
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="removeforum" referid="action">
	<mm:import externid="remforum" />
	<mm:booleanfunction set="mmbob" name="removeForum" referids="remforum">
	</mm:booleanfunction>
</mm:compare>

</mm:locale>
</mm:cloud>
