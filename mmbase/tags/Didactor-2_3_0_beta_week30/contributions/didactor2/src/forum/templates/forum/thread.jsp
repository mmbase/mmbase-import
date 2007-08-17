<%@page language="java" contentType="text/html; charset=utf-8" %>
<%--
This page show the threads of a certain forum, with at the bottom an input to create a new message.
This message is also create by this page.
--%>
<%@ page import="java.text.SimpleDateFormat,
                 java.util.Calendar"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="oscache" prefix="os" %>

<mm:content postprocessor="none" expires="0"><%-- postprocess="none" because of textarea interaction --%>

<mm:cloud name="mmbase" loginpage="/login.jsp" jspvar="cloud">
  <%@ include file="/shared/setImports.jsp"%>

  <%@include file="/education/wizards/roles_defs.jsp" %>
  <mm:import id="editcontextname" reset="true">docent schermen</mm:import>
  <%@include file="/education/wizards/roles_chk.jsp" %>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>Forum</title>
    </mm:param>
  </mm:treeinclude>

  <mm:import id="forum" externid="forum" jspvar="forum"/>
  <mm:import id="thread" externid="thread" jspvar="thread"/>

  <mm:import externid="action_back"/>
  <mm:present referid="action_back">
    <mm:redirect page="/forum/forum.jsp" referids="$referids,forum"/>
  </mm:present>

  
  <mm:islessthan referid="rights" referid2="RIGHTS_RW" inverse="true">
    <mm:import id="isTeacher">true</mm:import>
  </mm:islessthan>
  <mm:islessthan referid="rights" referid2="RIGHTS_RW">
    <mm:import id="isTeacher">false</mm:import>
  </mm:islessthan>

  <mm:import id="insertedmessageok"></mm:import>
  <mm:import id="message" externid="message" />
  <mm:import externid="title"/>
  <mm:isnotempty referid="message">
    <mm:isnotempty referid="title">
    <os:flush group="<%="forum_"+forum%>" scope="application" />
    <mm:transaction name="postmessage">
      <mm:node id="threadnode" referid="thread" />
      <mm:createnode id="messagenode" type="forummessages">
	<mm:setfield name="title"><mm:write referid="title" escape="none"/></mm:setfield>
        <mm:setfield name="body"><mm:write referid="message" escape="none"/></mm:setfield>
        <mm:setfield name="date"><%=(new java.util.Date()).getTime() / 1000%></mm:setfield>
      </mm:createnode>

      <mm:createrelation source="threadnode" destination="messagenode" role="related" />
    </mm:transaction>
    <mm:import id="insertedmessageok" reset="true">1</mm:import>
    </mm:isnotempty>
  </mm:isnotempty>


  <mm:import id="delnr" externid="delnr" />
  <mm:isnotempty referid="delnr">
    <mm:compare referid="isTeacher" value="true">
      <mm:transaction name="delmessage">
        <mm:deletenode deleterelations="true" number="$delnr" />
        <os:flush group="<%="forum_"+forum%>" scope="application" />
      </mm:transaction>
    </mm:compare>
  </mm:isnotempty>

  <script>
    function submitForm(name) {
      document.forms[name].submit();
    }
  </script>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
  <img src="<mm:treefile write="true" page="/gfx/icon_forum.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="forum" alt="forum" /> Forum
  </div>
</div>
<div class="folders">
  <div class="folderHeader">
        <!-- kop -->
  </div>
  <div class="folderBody">
    <mm:notpresent referid="print">

  <table cellspacing=1 width="100%" class="Font">
        <td >
          <mm:compare referid="isTeacher" value="true">
            <form name="delthread<mm:write referid="thread" />"
                  action="<mm:treefile write="true" page="/forum/deletethread.jsp" objectlist="$includePath" referids="$referids"/>"
                  method="post">
            <input type="hidden" name="delthread" value="<mm:write referid="thread" />">
            <input type="hidden" name="forum" value="<mm:write referid="forum" />">
              <input type="submit" value="<di:translate key="forum.remove_thread" />" class="formbutton">
            </form>
          </mm:compare>
        </td>
      </tr>
    </mm:notpresent>
   </table>

    &nbsp;
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">

        <mm:node referid="forum">
            <a href="<mm:treefile write="true" page="/forum/forum.jsp"
			objectlist="$includePath" referids="$referids">
                  <mm:param name="forum"><mm:write referid="forum" /></mm:param>
               </mm:treefile>" class="tableheader">
              <mm:field name="name" /></a>
        </mm:node>
        &gt;
        <mm:node referid="thread">
          <mm:field name="name" />
        </mm:node>


    &nbsp;
  </div>
  <div class="contentBodywit" style="padding-top: 5.2em">

   <mm:node referid="thread">
      <mm:import jspvar="includePath" vartype="String"><mm:write referid="includePath"/></mm:import>
      <mm:import jspvar="isTeacher" vartype="String"><mm:write referid="isTeacher" /></mm:import>
<%--
      <os:cache key="<%=includePath + "/forum/thread.jsp_" + thread + "_" + isTeacher%>" groups="<%="forum_"+forum%>" time="3600">
--%>
        <mm:relatednodes type="forummessages" orderby="date">
          <a name="<mm:field name="number"/>"></a>
    	    <h3><mm:field name="title"/></h3>
              <mm:field name="body" />
	    <p>
	    
              <!-- pers. info -->
	      <em>
              <mm:field name="owner" id="owner" write="false"/>
              <mm:listnodescontainer type="people">
              <mm:constraint field="username" operator="EQUAL" referid="owner"/>
              <mm:listnodes>
                <mm:field name="html(firstname)" write="true"/> <mm:field name="html(lastname)" write="true"/>
              </mm:listnodes>
              </mm:listnodescontainer>

              <mm:remove referid="owner"/>
              <br>
              <mm:import id="datum" jspvar="datum"><mm:field name="date" /></mm:import>
              <%
                  java.util.Date dat = new java.util.Date(1000 * (long)Integer.parseInt(datum));
                  java.text.SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy HH:mm");
                  datum = format.format(dat);
              %>
              <%=datum%>
              <mm:remove referid="datum" />
	      </em>
              <mm:compare referid="isTeacher" value="true">
                <form name="del<mm:field name="number" />" method="post">
                <input type="hidden" name="forum" value="<mm:write referid="forum"/>">
               <input type="hidden" name="delnr" value="<mm:field name="number" />">
                <input type="submit" value="<di:translate key="forum.remove" />" class="formbutton">
                </form>
              </mm:compare>
    
       </mm:relatednodes>
<%--
      </os:cache>
--%>
      <mm:notpresent referid="print">
            <form method="post" name="newmessage" action="<mm:treefile page="/forum/thread.jsp" objectlist="$includePath" referids="$referids"/>">
                 <input type="hidden" name="thread" value="<mm:write referid="thread"/>">
               <input type="hidden" name="forum" value="<mm:write referid="forum"/>">
	      <input type="text" name="title" value="<mm:isempty referid="insertedmessageok"><mm:write referid="title"/></mm:isempty>" size="40"><di:translate key="forum.title" /><br>
              <textarea cols=60 rows=6 name="message"><mm:isempty referid="insertedmessageok"><mm:write referid="message"/></mm:isempty></textarea><di:translate key="forum.message" />
	      <br clear="all">
	      <input type="submit" value="<di:translate key="forum.post" />" class="formbutton" name="action_submit">
	      <input type="submit" value="<di:translate key="forum.back" />" class="formbutton" name="action_back">
          </form>
      </mm:notpresent>
    </mm:node>
   </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
