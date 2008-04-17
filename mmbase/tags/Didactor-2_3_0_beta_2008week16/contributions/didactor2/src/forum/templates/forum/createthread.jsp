<%@page language="java" contentType="text/html; charset=utf-8" autoFlush="false"%>
<%--
This page allows a teacher or administrator to create a new thread.
Calls itself to create the forum and foward to the forum in which the new thread has been created.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="oscache" prefix="os" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Forum</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
  <img src="<mm:treefile write="true" page="/gfx/icon_forum.gif" objectlist="$includePath" />" width="25" height="13" border="0"  title="forum" alt="forum" /> Forum
  </div>
</div>
<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    &nbsp;
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
    &nbsp;
  </div>
  <div class="contentBodywit">

  <mm:import id="name" externid="name" />
  <mm:import id="forum" externid="forum" jspvar="forum"/>

  <mm:isnotempty referid="name">
    <os:flush group="<%="forum_"+forum%>" scope="application" />
    <mm:transaction name="createfolder">
      <mm:createnode type="forumthreads" id="newThread">
        <mm:setfield name="name"><mm:write referid="name"/></mm:setfield>
      </mm:createnode>
      <mm:node number="$forum" id="oldforum">
        <mm:createrelation role="related" source="oldforum" destination="newThread"/>
      </mm:node>
    </mm:transaction>

    <mm:node referid="newThread">
      <mm:field id="threadNumber" name="number" write="false"/>
    </mm:node>

    <mm:treefile jspvar="forward" write="false" page="/forum/thread.jsp" objectlist="$includePath" referids="$referids" escapeamps="false">
      <mm:param name="forum"><mm:write referid="forum" /></mm:param>
      <mm:param name="thread"><mm:write referid="threadNumber" /></mm:param>
    </mm:treefile>
    <%
      response.sendRedirect(forward);
    %>
  </mm:isnotempty>

  <mm:isempty referid="name">
    <script language="javascript">
      function submitForm(name) {
        document.forms[name].submit();
      }
    </script>

    <mm:treefile jspvar="postpage" write="false" page="/forum/createthread.jsp" objectlist="$includePath" referids="$referids"/>

          <form method="post" name="newthread" action="<%=postpage%>">
            <input type="hidden" name="forum" value="<%=forum%>" />
            
  <table cellspacing="0" cellpadding="0" border="0">
	<tr>
	    <td colspan="2">
	      <di:translate key="forum.create_topic_name" /> :
	      <input type="text" name="name" class="forminput" style="width:240px;">&nbsp;
	    </td>
	</tr>
	<tr>
	  <td align="center">
	    <div class="button1">
	    <a href="javascript:submitForm('newthread');"><di:translate key="forum.create" /></a>
	    </div>
	  </td>
	  <td>
	    <div class="button1">
	    <a href="javascript:history.go(-1);"><di:translate key="forum.back" /></a>
	    </div>
	  </td>
	</tr>
  </table>
			
              <%--<tr>
                <td>
                  <mm:treeinclude write="true" page="/forum/default.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="caption"><di:translate key="forum.create" /></mm:param>
                    <mm:param name="onclick">javascript:submitForm('newthread');</mm:param>
                  </mm:treeinclude>
                </td>
                <td>
                  <mm:treeinclude write="true" page="/forum/default.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="caption"><di:translate key="forum.back" /></mm:param>
                    <mm:param name="onclick">javascript:history.go(-1);</mm:param>
                  </mm:treeinclude>
                </td>
              </tr>--%>
              
            </form>

  </mm:isempty>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
