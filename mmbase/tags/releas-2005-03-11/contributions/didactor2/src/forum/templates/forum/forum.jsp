<%--
  This template shows the forum
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@ page import="java.text.SimpleDateFormat,
                 java.util.Calendar"%>
<%@taglib uri="oscache" prefix="os" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Forum</title>
  </mm:param>
</mm:treeinclude>

  <mm:import id="forum" externid="forum"/>

  <mm:node referid="class">
     <di:hasrole role="teacher">
      <mm:relatednodes type="forums" id="forumlist" orderby="name"/>
    </di:hasrole>
    <di:hasrole role="teacher" inverse="true">
      <mm:relatednodes type="forums" id="forumlist" orderby="name" constraints="[type] = 0"/>
    </di:hasrole>

    <mm:list referid="forumlist">
      <mm:first>
        <mm:isempty referid="forum">
          <mm:remove referid="forum" />
          <mm:import id="forum"><mm:field name="number" /></mm:import>
        </mm:isempty>
      </mm:first>
    </mm:list>
  </mm:node>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
   <img src="<mm:treefile write="true" page="/gfx/icon_forum.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="forum" /> Forum
  </div>
</div>
<div class="folders">
  <div class="folderHeader">
        <td class="tableheader"><di:translate  id="forum">forum</di:translate></td>
  </div>
  <div class="folderBody">
     <di:hasrole role="teacher">
          <mm:treeinclude write="true" page="/forum/headerlink.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="icon" value="nieuw forum"/>
            <mm:param name="text"><di:translate  id="newforum">New Forum</di:translate></mm:param>
            <mm:param name="link"><mm:treefile write="true" page="/forum/createforum.jsp" objectlist="$includePath" referids="$referids"/></mm:param>
          </mm:treeinclude>
     </di:hasrole>
     <di:hasrole role="teacher" inverse="true">
       &nbsp;
     </di:hasrole>





  <mm:isnotempty referid="forum">

     <mm:import jspvar="includePath" vartype="String"><mm:write referid="includePath"/></mm:import>
      <mm:import jspvar="sclass" vartype="String"><mm:write referid="class"/></mm:import>
      <mm:import jspvar="forum" vartype="String"><mm:write referid="forum"/></mm:import>

  <mm:import jspvar="isTeacher" vartype="String">false</mm:import>
  <di:hasrole role="teacher">
      <mm:import jspvar="isTeacher" vartype="String">true</mm:import>
  </di:hasrole>
  <br /><br />
          <!-- linker navigatie -->
          <%-- cache this part, only to be reset by the creating a new forum
          <os:cache key="<%=includePath+"//forum/forum.jsp_list_"+sclass+"_"+forum+"_"+isTeacher%>" groups="<%="forumlist_"+sclass%>" time="3600">
 --%>
            <mm:list referid="forumlist">
              <mm:import id="thisforum"><mm:field name="number" /></mm:import>
              <mm:import id="icon"><mm:compare referid="thisforum" referid2="forum">mapopen</mm:compare><mm:compare referid="thisforum" referid2="forum" inverse="true">mapdicht</mm:compare></mm:import>
              <mm:treeinclude write="true" page="/forum/listlink.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="link">
                   <mm:treefile write="true" page="/forum/forum.jsp" objectlist="$includePath" referids="$referids">
                     <mm:param name="forum"><mm:field name="number"/></mm:param>
                  </mm:treefile>
                </mm:param>
                <mm:param name="name"><mm:field name="name"/></mm:param>
                <mm:param name="icon"><mm:write referid="icon" /></mm:param>
              </mm:treeinclude>
              <mm:remove referid="icon" />
              <mm:remove referid="thisforum" />
            </mm:list>
<%--
          </os:cache>
--%>

    </mm:isnotempty>

  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
    <mm:isnotempty referid="forum">
          <mm:node referid="forum">
            <mm:field name="name"/>
          </mm:node>
    </mm:isnotempty>
    <mm:isempty referid="forum">
&nbsp;
    </mm:isempty>


  </div>

  <div class="contentSubHeader">

    <mm:isnotempty referid="forum">
              <mm:treeinclude write="true" page="/forum/headerlink.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="icon" value="nieuw onderwerp"/>
                <mm:param name="text"><di:translate  id="newtopic">New topic</di:translate></mm:param>
                <mm:param name="link"><mm:treefile write="true" page="/forum/createthread.jsp" objectlist="$includePath" referids="$referids">
                  <mm:param name="forum" value="$forum" />
                  </mm:treefile></mm:param>
              </mm:treeinclude>

              <di:hasrole role="teacher">
                <mm:node referid="forum">
                  <mm:field name="type" id="type" write="false"/>
                  <mm:compare referid="type" value="0">
                    <mm:treeinclude write="true" page="/forum/headerlink.jsp" objectlist="$includePath" referids="$referids">
                      <mm:param name="icon" value="sluit forum voor studenten"/>
                      <mm:param name="text"><di:translate id="closeforum" >Close forum for students</di:translate></mm:param>
                      <mm:param name="link"><mm:treefile write="true" page="/forum/changeforumtype.jsp" objectlist="$includePath" referids="$referids">
                              <mm:notpresent referid="class"><mm:param name="provider" value="$provider"/></mm:notpresent>
                              <mm:param name="number" value="$forum"/>
                              <mm:param name="forumtype" value="1"/>
                            </mm:treefile></mm:param>
                    </mm:treeinclude>
                  </mm:compare>

                  <mm:compare referid="type" value="1">
                    <mm:treeinclude write="true" page="/forum/headerlink.jsp" objectlist="$includePath" referids="$referids">
                      <mm:param name="icon" value="open forum voor studenten"/>
                      <mm:param name="text"><di:translate id="openforum" >open forum voor studenten</di:translate></mm:param>
                      <mm:param name="link"><mm:treefile write="true" page="/forum/changeforumtype.jsp" objectlist="$includePath" referids="$referids">
                              <mm:notpresent referid="class"><mm:param name="provider" value="$provider"/></mm:notpresent>
                              <mm:param name="number" value="$forum"/>
                              <mm:param name="forumtype" value="0"/>
                            </mm:treefile></mm:param>
                    </mm:treeinclude>
                  </mm:compare>
                </mm:node>
              </di:hasrole>
    </mm:isnotempty>
    <mm:isempty referid="forum">
&nbsp;
    </mm:isempty>


  </div>

  <div class="contentBody">



 <mm:isempty referid="forum">
    <b>Er zijn nog geen forums aangemaakt door docenten!</b>
  </mm:isempty>

  <mm:isnotempty referid="forum">
          <div id="tableDiv">
          </div>


     <mm:import jspvar="includePath" vartype="String"><mm:write referid="includePath"/></mm:import>
      <mm:import jspvar="sclass" vartype="String"><mm:write referid="class"/></mm:import>
      <mm:import jspvar="forum" vartype="String"><mm:write referid="forum"/></mm:import>

    <%-- cache this part, only to be when a new thread or message is created in this forum
    <os:cache key="<%=includePath+"//forum/forum.jsp_index_"+forum%>" groups="<%="forum_"+forum%>" time="3600">
--%>
      <mm:node referid="forum">

        <mm:import id="forumnumber"><mm:field name="number" /></mm:import>

        <mm:relatednodescontainer type="forumthreads">

          <di:table maxitems="10">
            <di:row>
              <di:headercell sortfield="name" default="true"><di:translate  id="table_name" >Topic</di:translate></di:headercell>
              <di:headercell><di:translate  id="table_no_posts" >Number</di:translate></di:headercell>
              <di:headercell><di:translate  id="table_latest_post" >Latest</di:translate></di:headercell>
            </di:row>

            <mm:listnodes>
              <di:row>

                <di:cell>
                  <a href="<mm:treefile page="/forum/thread.jsp" objectlist="$includePath" referids="$referids">
                             <mm:param name="forum"><mm:write referid="forumnumber"/></mm:param>
                             <mm:param name="thread"><mm:field name="number"/></mm:param>
                           </mm:treefile>">
                    <mm:field name="name" />
                  </a>
                </di:cell>

                <di:cell>
                  <mm:import id="messagecount">0</mm:import>
                  <mm:related path="forummessages" orderby="forummessages.date" directions="down" id="myforummessages">
                    <mm:first>
                      <mm:remove referid="messagecount"/>
                      <mm:import id="messagecount"><mm:size/></mm:import>
                    </mm:first>
                  </mm:related>
                  <mm:write referid="messagecount"/>
                </di:cell>

                <di:cell>
                  <mm:related referid="myforummessages">
                    <mm:first>
                      <mm:import id="lastdate" jspvar="lastdate"><mm:field name="forummessages.date" /></mm:import>
                      <%
                        java.util.Date dat = new java.util.Date(1000 * (long)Integer.parseInt(lastdate));
                        java.text.SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy HH:mm");
                        String datum = format.format(dat);
                      %>
                     <mm:field name="owner" id="owner" write="false"/>
                     <mm:import id="writer"> </mm:import>

                     <mm:listnodescontainer type="people">
                       <mm:constraint field="username" operator="EQUAL" referid="owner"/>

                       <mm:listnodes>
                         <mm:remove referid="writer"/>
                         <mm:import id="writer"><mm:field name="html(firstname)"/> <mm:field name="html(lastname)"/></mm:import>
                       </mm:listnodes>
                     </mm:listnodescontainer>

                     <mm:import id="lastmsg"><%=datum%> <di:translate  id="by_user">by</di:translate> <mm:write referid="writer"/></mm:import>
                     <mm:write referid="lastmsg"/>

                    </mm:first>
                  </mm:related>
                </di:cell>

              </di:row>
            </mm:listnodes>

          </di:table>

        </mm:relatednodescontainer>


      </mm:node>
<%--
    </os:cache>
--%>

  </mm:isnotempty>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
