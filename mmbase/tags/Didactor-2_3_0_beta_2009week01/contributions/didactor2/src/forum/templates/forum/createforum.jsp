<%@page language="java" contentType="text/html; charset=utf-8" autoFlush="false"%>
<%--
This page allows a teacher or administrator to create a new forum.
Calls itself to create the forum and foward to the newly created forum.
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="oscache" prefix="os" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp" >
  <%@ include file="/shared/setImports.jsp"%>
  <mm:import id="name" externid="name" />

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Forum</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
  <img src="<mm:treefile write="true" page="/gfx/icon_forum.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="forum" alt="forum" /> Forum
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

  <mm:isempty referid="name">
    <script language="javascript">
      function submitForm(name) {
        document.forms[name].submit();
      }
    </script>


          <mm:treefile jspvar="postpage" write="false" page="/forum/createforum.jsp" objectlist="$includePath" referids="$referids" />
          <form method="post" name="newforum" action="<%=postpage%>">
          
  <table cellspacing="0" cellpadding="0" border="0">
	<tr>
	    <td colspan="2">
	      <di:translate key="forum.create_forum_name" /> :
	      <input type="text" name="name" class="forminput" style="width:240px;">&nbsp;
	    </td>
	</tr>
	<tr>
	  <td align="center">
	    <div class="button1">
	    <a href="javascript:submitForm('newforum');"><di:translate key="forum.create" /></a>
	    </div>
	  </td>
	  <td>
	    <div class="button1">
	    <a href="javascript:history.go(-1);"><di:translate key="forum.back" /></a>
	    </div>
	  </td>
	</tr>
  </table>
          
            <%--<table cellspacing=2>
              <tr>
                <td colspan=2><di:translate key="forum.create_forum_name" /> :</td>
              </tr>
              <tr>
                <td><input type="text" name="name" class="forminput" style="width:240px;">&nbsp;</td>
                <td>
                  <mm:treeinclude write="true" page="/forum/default.jsp" objectlist="$includePath" referids="referids">
                    <mm:param name="caption"><di:translate key="forum.create" /></mm:param>
                    <mm:param name="onclick">javascript:submitForm('newforum');</mm:param>
                  </mm:treeinclude>
                </td>
              </tr>
              <tr>
                <td></td>
                <td>
                  <mm:treeinclude write="true" page="/forum/default.jsp" objectlist="$includePath" referids="referids">
                    <mm:param name="caption"><di:translate key="forum.back" /></mm:param>
                    <mm:param name="onclick">javascript:history.go(-1);</mm:param>
                  </mm:treeinclude>
                 </td>
               </tr>
             </table>--%>
             
           </form>

  </mm:isempty>


  <mm:isnotempty referid="name">
    <mm:import jspvar="sclass" vartype="String"><mm:write referid="class"/></mm:import>
    <os:flush scope="application" group="<%="forumlist_"+sclass%>"/>
    <mm:transaction name="createfolder">
      <mm:createnode type="forums" id="newForum">
        <mm:setfield name="name"><mm:write referid="name"/></mm:setfield>
        <mm:setfield name="type">0</mm:setfield>
      </mm:createnode>
      <mm:node number="$class" id="oldclass">
        <mm:createrelation role="related" source="oldclass" destination="newForum"/>
      </mm:node>
    </mm:transaction>
    <mm:node referid="newForum">
      <mm:field id="forum" name="number" write="false"/>
    </mm:node>

    <mm:treefile jspvar="forward" write="false" page="/forum/forum.jsp" objectlist="$includePath" referids="$referids" escapeamps="false">
      <mm:param name="forum"><mm:write referid="forum" /></mm:param>
    </mm:treefile>
    <%
      response.sendRedirect(forward);
    %>
  </mm:isnotempty>
</div>
</div>
</mm:cloud>
