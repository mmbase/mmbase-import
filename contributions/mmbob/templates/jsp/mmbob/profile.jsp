<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<mm:cloud sessionname="forum">
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="adminmode">false</mm:import>
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
<%@ include file="loadtranslations.jsp" %>

<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <%@ include file="header.jsp" %>
</div>
                                                                                              
<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr><th colspan="2" align="left">
                                        
			</th>
			</tr>

</table>

  <%-- own profile (editable) --%>
  <mm:compare referid="profileid" referid2="posterid">
  <div id="profileb">
    <div id="tabs">
      <ul>
        <mm:compare value="personal" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="personal" referid="profile" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="personal" />
        </mm:url>"><mm:write referid="mlg_personal"/></a>
        </li>
        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
        <mm:field name="contactinfoenabled">
          <mm:compare value="true">
        <mm:compare value="contact" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="contact" referid="profile" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="contact" />
        </mm:url>"><mm:write referid="mlg_contact"/></a>
        </li>
        </mm:compare>
        </mm:field>
        </mm:nodefunction>
        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
        <mm:field name="avatarsdisabled">
          <mm:compare value="false">
<mm:import id="avatardisabled"/>
        <mm:compare value="avatar" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="avatar" referid="profile" inverse="true">
        <li>
        </mm:compare>

 
        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="avatar" />
        </mm:url>"><mm:write referid="mlg_avatar"/></a>
        </li>

        </mm:compare>
        </mm:field>
        </mm:nodefunction>
      </ul>
    </div>
    
    <div id="profile">
	<mm:compare value="personal" referid="profile">
 	  <form action="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>" method="post"> 
        <mm:node number="$profileid">
        <mm:functioncontainer>
          <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
          <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">

        <div class="row">
        <span class="feedback">
        <mm:import externid="feedback_message" from="session" id="feedback"/>
        <mm:compare referid="feedback" value="true">
          <mm:write referid="mlg_ProfileUpdated"/>
        </mm:compare>
        <mm:compare referid="feedback" value="false">
          <mm:write referid="mlg_ProfileUpdateFailed"/>
        </mm:compare>
        <mm:compare referid="feedback" value="passwordchanged">
          <mm:write referid="mlg_PasswordChanged"/>
<mm:import id="accounttocookie"><mm:field name="account" /></mm:import>
          <mm:write referid="accounttocookie" cookie="caf$forumid"/>
        </mm:compare>
        <mm:compare referid="feedback" value="newpasswordnotequal">
          <mm:write referid="mlg_Password_notequal"/>
        </mm:compare>
        </span>
        </div>
        <div class="row">
          <input type="hidden" name="action" value="editposter" />
          <span class="label"><mm:write referid="mlg_Account"/></span>
          <span class="formw"><mm:field name="account" /></span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Firstname"/></span>
          <span class="formw">
            <input name="newfirstname" type="text" size="25" value="<mm:field name="firstname" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Lastname"/></span>
          <span class="formw">
            <input name="newlastname" type="text" size="25" value="<mm:field name="lastname" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Email"/></span>
          <span class="formw">
            <input name="newemail" type="text" size="25" value="<mm:field name="email" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Location"/></span>
          <span class="formw">
            <input name="newlocation" type="text" size="25" value="<mm:field name="location" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Gender"/></span>
          <span class="formw">
				<mm:field name="gender">
				<select name="newgender">
				<mm:compare value="male">
				<option value="male"><mm:write referid="mlg_Male"/></option>
				<option value="female"><mm:write referid="mlg_Female"/></option>
				</mm:compare>
				<mm:compare value="male" inverse="true">
				<option value="female"><mm:write referid="mlg_Female"/></option>
				<option value="male"><mm:write referid="mlg_Male"/></option>
				</mm:compare>
				</select>
				</mm:field>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_NewPassword"/></span>
          <span class="formw">
            <input name="newpassword" type="password" size="25"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_NewConfirmPassword"/></span>
          <span class="formw">
            <input name="newconfirmpassword" type="password" size="25" />
          </span>
        </div>
        <%-- TODO: not yet implemented
          <div class="row">
          <span class="label"><mm:write referid="mlg_Level"/></span>
          <span class="formw">
            level123
          </span>
        </div>--%>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Posts"/></span>
          <span class="formw">
            <mm:field name="accountpostcount" />
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Member_since"/></span>
          <span class="formw">
            <mm:field name="firstlogin"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Last_visit"/></span>
          <span class="formw">
            <mm:field name="lastseen"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
          </span>
        </div>    
        </mm:nodefunction>
        </mm:functioncontainer>    
        </mm:node>

        <div class="row">
          <span class="label"></span>
          <span class="formw">
            <input type="submit" value="<mm:write referid="mlg_Save"/>" />
          </span>
        </div>

    </mm:compare>
        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
        <mm:field name="contactinfoenabled">
          <mm:compare value="true">
    <mm:compare value="contact" referid="profile">
        <mm:write referid="mlg_Not_implemented"/>  
    </mm:compare>
</mm:compare>
</mm:field>
</mm:nodefunction>

    <mm:compare value="avatar" referid="profile">
      <form enctype="multipart/form-data" action="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$posterid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        <mm:param name="referrer" value="profile.jsp" />
        </mm:url>" method="post"> 
        <mm:node number="$profileid">
          <mm:functioncontainer>
            <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
        <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
        <div class="row">
          <span class="label"><mm:write referid="mlg_Current_avatar"/></span>
          <span class="formw">
	    <mm:field name="avatar">
		<mm:compare value="-1" inverse="true">
               <mm:node number="$_">
                 <img src="<mm:image template="s(80x80)" />" width="80" border="0">
               </mm:node>
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
          <span class="label"><mm:write referid="mlg_Upload_avatar"/></span>
          <span class="formw">
            <mm:fieldlist nodetype="images" fields="handle">
            <mm:fieldinfo type="input"/>
            </mm:fieldlist>
            <input type="submit" name="addavatar" value="<mm:write referid="mlg_Upload"/>" />
          </span>
        </div>
          </mm:compare>
        </mm:field>
        <mm:field name="avatarsgalleryenabled">
          <mm:compare value="true">
        <div class="row">
          <span class="label"><mm:write referid="mlg_Select_avatar_from_the_list"/></span>
          <span class="formw">
            <input type="submit" name="selectavatar" value="<mm:write referid="mlg_Select"/>"/>
          </span>
        </div>
          </mm:compare>
        </mm:field>
        </mm:nodefunction>
    </mm:compare>

    <div class="spacer">&nbsp;</div>

    </form>
    </div>
  </mm:compare>

  
  <%-- other profile (non-editable) --%>
  <mm:compare referid="profileid" referid2="posterid" inverse="true">
  <div id="profileb">
    <div id="tabs">
      <ul>
        <mm:compare value="personal" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="personal" referid="profile" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="personal" />
        </mm:url>"><mm:write referid="mlg_personal"/></a>
        </li>
        <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
        <mm:field name="contactinfoenabled">
          <mm:compare value="true">
        <mm:compare value="contact" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="contact" referid="profile" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="contact" />
        </mm:url>"><mm:write referid="mlg_contact"/></a>
        </li>
</mm:compare>
</mm:field>
</mm:nodefunction>
    <mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
      <mm:field name="avatarsdisabled">
        <mm:compare value="false">
        <mm:compare value="avatar" referid="profile">
        <li class="selected">
        </mm:compare>
        <mm:compare value="avatar" referid="profile" inverse="true">
        <li>
        </mm:compare>

        <a href="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="avatar" />
        </mm:url>"><mm:write referid="mlg_avatar"/></a>
        </li></mm:compare></mm:field></mm:nodefunction>
      </ul>
    </div>
    
    <div id="profile">
	<mm:compare value="personal" referid="profile">

        <mm:node number="$profileid">
        <mm:functioncontainer>
          <mm:field name="account"><mm:param name="posterid" value="$_" /></mm:field>
          <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
        <div class="row">
          <span class="label"><mm:write referid="mlg_Account"/></span>
          <span class="formw"><mm:field name="account" /></span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Firstname"/></span>
          <span class="formw"><mm:field name="firstname" /></span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Lastname"/></span>
          <span class="formw"><mm:field name="lastname" /></span>
        </div>
        <%-- TODO: gebruiker moet zelf kiezen of email getoond wordt of niet, voor nu default niet
        <div class="row">
          <span class="label"><mm:write referid="mlg_Email"/></span>
          <span class="formw"><mm:field name="email" /></span>
        </div>--%>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Location"/></span>
          <span class="formw"><mm:field name="location" /></span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Gender"/></span>
          <span class="formw"><mm:field name="gender" /></span>
        </div>
        <%-- TODO: not yet implemented
        <div class="row">
          <span class="label"><mm:write referid="mlg_Level"/></span>
          <span class="formw">level123</span>
        </div>--%>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Messages"/></span>
          <span class="formw"><mm:field name="accountpostcount" /></span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Member_since"/></span>
          <span class="formw">
            <mm:field name="firstlogin"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg_Last_visit"/></span>
          <span class="formw">
            <mm:field name="lastseen"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
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
    boe
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
          <span class="label"><mm:write referid="mlg_Avatar"/></span>
          <span class="formw">
	    <mm:field name="avatar">
		<mm:compare value="-1" inverse="true">
               <mm:node number="$_">
                 <img src="<mm:image template="s(80x80)" />" width="80" border="0">
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

    </div>
  </mm:compare>

</div>

<div class="footer">
  <%@ include file="footer.jsp" %>
</div>
                                                                                              
</body>
</html>
</mm:locale>
</mm:content>
</mm:cloud>
