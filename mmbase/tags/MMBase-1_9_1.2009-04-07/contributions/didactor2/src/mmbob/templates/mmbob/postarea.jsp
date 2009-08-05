<jsp:root version="1.2"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          >
  <mm:cloud method="delegate">
    <mm:content type="text/html">
      <jsp:directive.include file="/shared/setImports.jsp" />
      <jsp:directive.include file="thememanager/loadvars.jsp" />
      <jsp:directive.include file="settings.jsp" /> <!-- foei -->
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="${style_default}" />
          <mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" write="false">
            <link rel="stylesheet" type="text/css" href="${_}" />
          </mm:treefile>
          <title><di:translate key="mmbob.mmbaseforum" /></title>
        </head>
        <mm:import externid="adminmode">false</mm:import>
        <mm:import externid="forumid" />
        <mm:import externid="postareaid" />
        <mm:import externid="page">1</mm:import>

        <jsp:directive.include file="getposterid.jsp" />

        <!-- action check -->
        <mm:import externid="action" />
        <mm:present referid="action">
          <mm:include page="actions.jsp" />
        </mm:present>
        <!-- end action check -->

        <mm:locale language="$lang">

          <center><!-- WTF -->

            <mm:include page="path.jsp?type=postarea" />

            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
              <tr>
                <th colspan="2" align="left">
                  <mm:compare referid="image_logo" value="" inverse="true">
                    <center><img src="${image_logo}" width="100%" /></center> <!-- center WTF -->
                    <br />
                  </mm:compare>
                  <mm:import id="pagesize"></mm:import>
                  <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page,pagesize">
                    <mm:field id="navline" name="navline" write="false" />
                    <mm:import id="pagecount"><mm:field name="pagecount" /></mm:import>

                    <mm:field name="pagecount" />
                    <b><di:translate key="mmbob.area" /></b> : <mm:field name="name" /><br />
                    <b><di:translate key="mmbob.numberoftopics" /></b> : <mm:field name="postthreadcount" /><br />
                    <b><di:translate key="mmbob.numberofmessages" /></b> : <mm:field name="postcount" /><br />
                    <b><di:translate key="mmbob.numberofviews" /></b> : <mm:field name="viewcount" /><br />
                    <b><di:translate key="mmbob.lastmessage" /></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="${timeFormat}" /></mm:field> <b><di:translate key="mmbob.visitorsonline1" /></b> <mm:field name="lastposter" /> <b> : '</b><mm:field name="lastsubject" /><b>'</b></mm:compare><mm:compare value="-1"><di:translate key="mmbob.visitorsonline2" /></mm:compare></mm:field><br />
                    <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
                  </mm:nodefunction>
                  <br />
                  <!-- hh
                       <b>Moderators</b> :
                       <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
                       <mm:field name="account" /> (<di:person />)<br />
                       </mm:nodelistfunction>
                  -->
                </th>
              </tr>
            </table>
            <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
              <tr>
                <!-- hh <th width="15">&nbsp;</th><th width="15">&nbsp;</th> -->
                <th><di:translate key="mmbob.topic" /></th>
                <th><di:translate key="mmbob.startedby" /></th>
                <th><di:translate key="mmbob.numberofmessages" /></th>
                <th><di:translate key="mmbob.numberofviews" /></th>
                <th><di:translate key="mmbob.lastmessage" /></th>
                <mm:compare referid="isadministrator" value="true"><th><di:translate key="mmbob.admin" /></th></mm:compare>
              </tr>
              <mm:nodelistfunction set="mmbob" name="getPostThreads" referids="forumid,postareaid,posterid,page">
                <tr>
                  <!-- hh <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td><td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td> -->
                  <td align="left">
                    <mm:link page="thread.jsp" referids="forumid,postareaid">
                      <mm:param name="postthreadid">${_node.id}</mm:param>
                      <a href="${_}"><mm:field name="name" /></a>
                    </mm:link>
                    <mm:field name="navline" escape="none" />
                  </td>
                  <td align="left"><mm:field name="creator" /></td>
                  <td align="left"><mm:field name="replycount" /></td>
                  <td align="left"><mm:field name="viewcount" /></td>
                  <td align="left"><mm:field name="lastposttime"><mm:time format="${timeFormat}" /></mm:field> door <mm:field name="lastposter" /></td>
                  <mm:compare referid="isadministrator" value="true">
                    <mm:link page="removepostthread.jsp" referids="forumid,postareaid">
                      <mm:param name="postthreadid"><mm:field name="id" /></mm:param>
                      <td>
                        <a href="${_}"><img src="${image_mdelete}"   border="0" /></a>
                        <!-- hh / <a href="<mm:url page="editpostthread.jsp" referids="forumid,postareaid"><mm:param name="postthreadid"><mm:field name="id" /></mm:param></mm:url>">E</a> -->
                      </td>
                    </mm:link>
                  </mm:compare>
                </tr>
              </mm:nodelistfunction>
            </table>
            PAGECOUNT ${pagecount}
            <mm:compare referid="pagecount" value="1" inverse="true">
              <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-right : 30px;" align="right">
                <tr>
                  <td><di:translate key="mmbob.pages" /> : <mm:write referid="navline" escape="none" /></td>
                </tr>
              </table>
            </mm:compare>
            <!-- useless use of tables, we hate it! -->
            <table cellpadding="0" cellspacing="0" style="margin-top : 5px; margin-left : 25px" align="left">
              <mm:link page="newpost.jsp" referids="forumid,postareaid">
                <tr><td><a href="${_}"><img src="${image_newmsg}" border="0" /></a></td></tr>
              </mm:link>
            </table>
            <!-- hh
                 <br />
                 <br />
                 <br />
                 <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-left : 30px" align="left">
                 <tr><td align="left">
                 <br />
                 <mm:write referid="image_state_normal" /> Open onderwerp<p />
                 <mm:write referid="image_state_normalnew" /> Open onderwerp met ongelezen reacties<p />
                 <mm:write referid="image_state_hot" /> Open populair onderwerp<p />
                 <mm:write referid="image_state_hotnew" /> Open populair onderwerp met ongelezen reacties&nbsp;<p />
                 <mm:write referid="image_state_pinned" /> Vastgezet onderwerp<p />
                 <mm:write referid="image_state_closed" /> Gesloten onderwerp<p />
                 <mm:write referid="image_state_normalme" />Onderwerp waaraan u hebt bijgedragen<p />
                 </td></tr>
                 </table>
                 <br /><br />
                 <br /><br />
                 <br /><br />
            -->
            <!-- WTF WTF, did we never hear of CSS or so: -->
            <br /><br /><br />
            <br /><br /><br />
            <br /><br /><br />
            <br /><br /><br />
            <mm:compare referid="isadministrator" value="true">
              <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;margin-left : 20px;" width="95%" align="left">
                <tr><th align="lef"><di:translate key="mmbob.adminfunctions" /></th></tr>
                <tr>
                  <td>
                    <p /> <!-- WTF, xhtml does _NOT_ work like this -->
                    <mm:link page="changepostarea.jsp" referids="forumid,postareaid">
                      <a href="${_}"><di:translate key="mmbob.changearea" /></a><br /> <!-- and br's are evil too -->
                    </mm:link>
                    <mm:link page="removepostarea.jsp" referids="forumid,postareaid">
                      <a href="${_}"><di:translate key="mmbob.removearea" /></a><br />
                    </mm:link>
                    <mm:link page="newmoderator.jsp" referids="forumid,postareaid">
                      <a href="${_}"><di:translate key="mmbob.addmoderator" /></a><br />
                    </mm:link>
                    <mm:link page="removemoderator.jsp" referids="forumid,postareaid">
                      <a href="${_}"><di:translate key="mmbob.removemoderator" /></a><br />
                    </mm:link>
                  </td>
                </tr>
              </table>
            </mm:compare>

          </center>
        </mm:locale>
      </html>
    </mm:content>
</mm:cloud>
</jsp:root>
