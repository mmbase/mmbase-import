<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:content type="text/html" expires="0">
<mm:cloud method="delegate">
<%@include file="/shared/setImports.jsp" %>
<%@include file="thememanager/loadvars.jsp" %>
<%@include file="settings.jsp" %>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
    <title><di:translate key="mmbob.mmbaseforums" /></title>
  </head>
  <mm:import externid="forumid">unknown</mm:import>
  <mm:compare referid="forumid" value="unknown">
    <table align="center" cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%">
      <tr><th><di:translate key="mmbob.systemerror" /></th></tr>
      <tr><td height="40"><b><di:translate key="mmbob.error" /> : </b> <di:translate key="mmbob.noforums1" /> <a href="forums.jsp">forums.jsp</a> <di:translate key="mmbob.noforums2" />.</td></tr>
    </table>
  </mm:compare>
  
  <mm:compare referid="forumid" value="unknown" inverse="true">
    <jsp:directive.include file="getposterid.jsp" />

    <mm:log> FORUM ID ${forumid} </mm:log>
    <mm:locale language="$lang"> 

      <!-- action check -->
      <mm:import externid="action" />
      <mm:present referid="action">
        <mm:include page="actions.jsp" />
      </mm:present>
      <!-- end action check -->

      <center>
        <mm:include page="path.jsp?type=index" />
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
          <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">

          <mm:log> forum info ${_node.name} </mm:log>
          <mm:field id="adminmode" name="isadministrator" write="false" />
           <tr>
             <mm:compare referid="posterid" value="-1">
               <th width="100">
                 <a href="newposter.jsp?forumid=<mm:write referid="forumid" />"><img src="images/guest.gif" border="0" /></a>
               </th>
               <td align="left">
                 <form action="login.jsp?forumid=<mm:write referid="forumid" />" method="post">
                 <mm:present referid="loginfailed">
                   <br />
                   <center><h4><di:translate key="mmbob.wronglogin" /></h4></center>
                   <center> <a href="<mm:url page="remail.jsp" referids="forumid" />"><di:translate key="mmbob.forgotpasswordlink" /></a></center>
                   <p />
                 </mm:present>
                 <mm:notpresent referid="loginfailed">
                   <mm:field name="description" />
                   <p />
                   <b><di:translate key="mmbob.login" /></b><p />
                 </mm:notpresent>
                 <di:translate key="mmbob.account" /> : <input size="12" name="account">
                 <di:translate key="mmbob.password" /> : <input size="12" type="password" name="password">
                 <input type="submit" value="<di:translate key="mmbob.login" />" />
                 </form><p />
               </mm:compare>
               <mm:compare referid="posterid" value="-1" inverse="true">
                 <th width="100">
                   <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids"></mm:treefile>" target=_top>
                   <%--hh <mm:field name="active_account" />--%><di:translate key="mmbob.clickforprofile" /><br />
                   <mm:field name="active_avatar"><mm:compare value="-1" inverse="true">
                     <mm:node number="$_" notfound="skip">
                       <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                     </mm:node>
                   </mm:compare></mm:field></a>
                   <%-- hh a href="logout.jsp?forumid=<mm:write referid="forumid" />">Logout</a> --%>
                 </th>
                 <td align="left" valign="top">
                   <mm:compare referid="image_logo" value="" inverse="true">
                    <br />
                    <center><img src="<mm:write referid="image_logo" />" width="98%"></center>
                    <br />
                    </mm:compare>
                    <mm:compare referid="image_logo" value="">
                    <h4><di:translate key="mmbob.welcometoforum1" /> 
                    <di:hasrole role="administrator">admin</di:hasrole>
                    <mm:field name="active_firstname" /> <mm:field name="active_lastname" /> <%-- hh (<mm:field name="active_account" />) --%> <br /> <di:translate key="mmbob.welcometoforum2" />  <mm:field name="name" />  <di:translate key="mmbob.welcometoforum3" />.</h4><p />

                    </mm:compare>
                    <di:translate key="mmbob.lasttimelogin" /> : <mm:field name="active_lastseen"><mm:compare value="" inverse="true"><mm:field name="active_lastseen"><mm:time format="${timeFormat}" /></mm:field></mm:compare></mm:field><br />
                    <di:translate key="mmbob.membersince" /> : <mm:field name="active_firstlogin"><mm:compare value="" inverse="true"><mm:field name="active_firstlogin"><mm:time format="${timeFormat}" /></mm:field></mm:compare></mm:field><br />
                    <%-- hh <di:translate key="mmbob.numberofmessages" />: <mm:field name="active_postcount" />
                    Level : <mm:field name="active_level" /> 
                    <b>Je hebt 0 nieuwe en 0 ongelezen <a href="<mm:url page="privatemessages.jsp" referids="forumid" />">prive berichten</a></b>
                    <h4>Op dit moment: <mm:field name="postersonline" /> bezoekers online.</h4> --%>
                </mm:compare>
                </td>
                <th width="250" align="left" valign="top">
                <b><di:translate key="mmbob.infoaboutforum" /></b><br/>
                <b><di:translate key="mmbob.numberofareas" /></b> : <mm:field name="postareacount" /><br />
                <b><di:translate key="mmbob.numberoftopics" /></b> : <mm:field name="postthreadcount" /><br />
                <b><di:translate key="mmbob.numberofmessages" /></b> : <mm:field name="postcount" /><br />
                <b><di:translate key="mmbob.numberofviews" /></b> : <mm:field name="viewcount" /><br />
                <b><di:translate key="mmbob.numberofmembers" /></b> : <mm:field name="posterstotal" /><br />
                <b><di:translate key="mmbob.numberofnew" /></b> : <mm:field name="postersnew" /><br />
                <b><di:translate key="mmbob.numberonline" /></b> : <mm:field name="postersonline" /><p /><br />
                <b><di:translate key="mmbob.lastmessage" /></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="${timeFormat}" /></mm:field> <di:translate key="mmbob.visitorsonline1" /> <mm:field name="lastposter" /> '<mm:field name="lastsubject" />'</mm:compare><mm:compare value="-1"><di:translate key="mmbob.visitorsonline2" /></mm:compare></mm:field>
                </th>
            </tr> 
          </mm:nodefunction>
</table>


<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
  <tr>
   <td align="right">
    <a href="<mm:url page="moderatorteam.jsp" referids="forumid" />">
    <di:translate key="mmbob.moderatorteam" /></a> | <a href="<mm:url page="onlineposters.jsp" referids="forumid" />"><di:translate key="mmbob.membersonline" /> | <a href="<mm:url page="allposters.jsp" referids="forumid" />"><di:translate key="mmbob.allmembers" /></a></a>
   </td>
  </tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
   <tr><th><di:translate key="mmbob.areas" /></th>
        <th><di:translate key="mmbob.numberoftopics" /></th>
        <th><di:translate key="mmbob.numberofmessages" /></th>
        <th><di:translate key="mmbob.numberofviews" /></th>
        <th><di:translate key="mmbob.lastmessage" /></th></tr>
          <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
            <tr><td align="left"><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="name" /></a>
            <p/>

            <mm:field name="description" />
            <%-- hh <p />
            Moderators : <mm:field name="moderators" />
            <p /> --%>
             </td>
                <td><mm:field name="postthreadcount" /></td>
                <td><mm:field name="postcount" /></td>
                <td><mm:field name="viewcount" /></td>
                <td align="left" valign="top"><mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="${timeFormat}" /></mm:field> <di:translate key="mmbob.visitorsonline1" /> <mm:field name="lastposter" /><p /><mm:field name="lastsubject" /></mm:compare><mm:compare value="-1"><di:translate key="mmbob.visitorsonline2" /></mm:compare></mm:field></td>
            </tr>
          </mm:nodelistfunction>
        </table>
        <mm:compare referid="adminmode" value="true">
          <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
            <tr><th align="lef"><di:translate key="mmbob.adminfunctions" /></th></tr>
            <td>
              <p />
              <a href="<mm:url page="changeforum.jsp">
                <mm:param name="forumid" value="$forumid" />
                </mm:url>"><di:translate key="mmbob.changeforum" /></a><br />
                <a href="<mm:url page="newpostarea.jsp">
                  <mm:param name="forumid" value="$forumid" />
                  </mm:url>"><di:translate key="mmbob.addnewarea" /></a>
                  <p />
                </td>
              </tr>
    </table>
  </mm:compare>

</mm:locale>

</mm:compare>
</center>
</html>
</mm:cloud>
</mm:content>
