<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="theme.style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="pathtype">allposters</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="../getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
    <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
    <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
    <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
</mm:nodefunction>

<form action="<mm:url page="profile.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:present referid="postareaid">
        <mm:param name="postareaid" value="$postareaid" />
        </mm:present>
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profileid" value="$profileid" />
        </mm:url>" method="post">
        <input type="hidden" name="action" value="editposter" />
        <input type="hidden" name="profileid" value="profileid" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">

  <tr>
    <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid,profileid@posterid">
    <th><mm:write referid="mlg.Account" /> : <mm:field name="account" /></th>
  </tr>
  <tr><td>
        <div class="row">
          <span class="label"><mm:write referid="mlg.Firstname"/></span>
          <span class="formw">
            <input name="newfirstname" type="text" size="25" value="<mm:field name="firstname" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg.Lastname"/></span>
          <span class="formw">
            <input name="newlastname" type="text" size="25" value="<mm:field name="lastname" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg.Email"/></span>
          <span class="formw">
            <input name="newemail" type="text" size="25" value="<mm:field name="email" />"/>
          </span>
        </div>
        <div class="row">
          <span class="label"><mm:write referid="mlg.Location"/></span>
          <span class="formw">
            <input name="newlocation" type="text" size="25" value="<mm:field name="location" />"/>
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
    </td></tr>
	  <mm:import id="guipos">-1</mm:import>
          <mm:nodelistfunction set="mmbob" name="getProfileValues" referids="forumid,profileid@posterid,guipos">
	<tr><td> 
        <div class="row">
          <span class="label"><mm:field name="guiname" /></span>
          <span class="formw">
	    <mm:field name="edit"><mm:compare value="true">
	    <mm:field name="type">
	    <mm:compare value="string"><input name="<mm:field name="name" />" type="text" size="25" value="<mm:field name="value" />"/></mm:compare>
	    <mm:compare value="field"><textarea rows="7" cols="25" name="<mm:field name="name" />"><mm:field name="value" /></textarea></mm:compare>
	    <mm:compare value="date"><mm:import id="bname"><mm:field name="name" /></mm:import><mm:import id="bvalue"><mm:field name="value" /></mm:import><mm:include page="bdate.jsp" referids="bname,bvalue" /></mm:compare>
	    </mm:field>
	    </mm:compare>
	    <mm:compare value="false">
	    <mm:field name="type">
	    <mm:compare value="string"><input name="<mm:field name="name" />" type="text" size="25" value="<mm:field name="value" />"/></mm:compare>
	    <mm:compare value="field"><textarea rows="7" cols="25" name="<mm:field name="name" />"><mm:field name="value" /></textarea></mm:compare>
	    <mm:compare value="date"><mm:import id="bname"><mm:field name="name" /></mm:import><mm:import id="bvalue"><mm:field name="value" /></mm:import><mm:include page="bdate.jsp" referids="bname,bvalue" /></mm:compare>
	    <font color="red">*</font>
	    </mm:field>


	    </mm:compare>
	    </mm:field>
            (synced : <mm:field name="synced" />)
          </span>
        </div>
	  </mm:nodelistfunction>
        <div class="row">
          <span class="label"></span>
          <span class="formw">
	   <table>
	    <tr><td width="200" align="center">
            <input type="submit" value="<mm:write referid="mlg.Save"/>" />
	    </td>   
	</form>
<form action="<mm:url page="profiles.jsp" referids="forumid" />" method="post">
		<td width="200" align="center">
		<input type="submit" value="<mm:write referid="mlg.Cancel"/>" align="right" />
		</td></tr>
	</form>
		</table>
          </span>
        </div>
	</td>
	</tr>
</table>
</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</mm:locale>
                                                                                              
</body>
</html>
</mm:content>
</mm:cloud>
