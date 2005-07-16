<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
   <%

      String bundlePOP = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundlePOP = "nl.didactor.component.pop.PopMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundlePOP %>">
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>POP</title>
      <link rel="stylesheet" type="text/css" href="css/pop.css" />
    </mm:param>
  </mm:treeinclude>

  <% boolean isEmpty = true; 
     String msgString = "";
  %>

  <%@ include file="getids.jsp" %>

  <div class="rows">

    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" alt="<fmt:message key="POPfull"/>" /> <fmt:message key="POPfull"/>
      </div>		
    </div>

    <%@ include file="leftpanel.jsp" %>

    <di:hasrole referid="user" role="teacher" inverse="true">
      <mm:import id="whatselected" reset="true">student</mm:import>
    </di:hasrole>

    <%-- right section --%>
    <mm:compare referid="whatselected" value="0" inverse="true">
      <div class="mainContent">
        <mm:compare referid="command" value="getinvite">
          <mm:import id="currentpop" reset="true">0</mm:import>
        </mm:compare>
        <mm:compare referid="currentpop" value="-1">
          <div class="contentBody"> 
            <p><fmt:message key="MsgForNoPOP"/></p>
          </div>
        </mm:compare>
        <mm:compare referid="currentpop" value="-1" inverse="true">
          <mm:compare referid="currentfolder" value="-1">
            <div class="contentHeader"><fmt:message key="Competencies"/> <mm:compare referid="currentprofile" value="-1" inverse="true"
                ><mm:node number="$currentprofile"><mm:field name="name"/></mm:node></mm:compare>
              <di:hasrole referid="user" role="teacher">
                <mm:node number="$student">
                  : <mm:field name="firstname"/> <mm:field name="lastname"/>
                </mm:node>
              </di:hasrole>
            </div>
            <%@ include file="todo.jsp" %>
            <%@ include file="docs.jsp" %>
            <mm:compare referid="command" value="continue">
              <mm:remove referid="command"/>
              <mm:import id="command">editcomp</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="savecomp">
              <%@ include file="savecomp.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><fmt:message key="MsgSelfGradeDone"/></mm:import>
              <% msgString = dummy; %>
              <mm:remove referid="command"/>
              <mm:import id="command">no</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="sendinvite">
              <%@ include file="sendinvite.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><fmt:message key="MsgSendInviteDone"/></mm:import>
              <% msgString = dummy; %>
              <mm:remove referid="command"/>
              <mm:import id="command">editcomp</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="invite">
              <%@ include file="invite.jsp" %>
              <mm:remove referid="command"/>
              <mm:import id="command">-1</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="getinvite">
              <%@ include file="getinvite.jsp" %>
              <mm:remove referid="command"/>
              <mm:import id="command">-1</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="sendfeedback">
              <%@ include file="sendfeedback.jsp" %>
              <mm:remove referid="command"/>
              <mm:import id="command">no</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="editcomp">
              <jsp:include page="compedit.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
            <mm:compare referid="command" value="no">
              <jsp:include page="comptable.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
          </mm:compare>
          <mm:compare referid="currentfolder" value="1">
            <di:hasrole referid="user" role="teacher">
              <mm:compare referid="whatselected" value="class">
                <jsp:include page="coachpredetail.jsp"/>
              </mm:compare>
              <mm:compare referid="whatselected" value="wgroup">
                <jsp:include page="coachpredetail.jsp"/>
              </mm:compare>
            </di:hasrole>
            <mm:compare referid="whatselected" value="student">
              <div class="contentHeader"><fmt:message key="Progressmonitor"/>
                <di:hasrole referid="user" role="teacher">
                  <mm:node number="$student">
                    : <mm:field name="firstname"/> <mm:field name="lastname"/>
                  </mm:node>
                </di:hasrole>
              </div>
              <mm:compare referid="command" value="intake">
                <mm:import id="competencies" jspvar="competencies" />
                <jsp:include page="intaketest.jsp">
                  <jsp:param name="competencies" value="<%= competencies %>"/>
                </jsp:include>
                <mm:remove referid="command"/>
                <mm:import id="command">-1</mm:import>
              </mm:compare>
              <mm:compare referid="command" value="detail">
                <jsp:include page="progressdetail.jsp"/>
                <mm:remove referid="command"/>
                <mm:import id="command">-1</mm:import>
              </mm:compare>
              <mm:compare referid="command" value="no">
                <jsp:include page="voortgang.jsp">
                  <jsp:param name="msg" value="<%= msgString %>"/>
                </jsp:include>
              </mm:compare>
            </mm:compare>
          </mm:compare>
          <mm:compare referid="currentfolder" value="2">
            <div class="contentHeader"><fmt:message key="TodoItems"/>
              <di:hasrole referid="user" role="teacher">
                <mm:node number="$student">
                  : <mm:field name="firstname"/> <mm:field name="lastname"/>
                </mm:node>
              </di:hasrole>
            </div>
            <%@ include file="todo.jsp" %>
            <mm:compare referid="command" value="-1" inverse="true">
              <jsp:include page="todolist.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
          </mm:compare>
        </mm:compare>
      </div>
    </mm:compare>

    <mm:compare referid="whatselected" value="0">
      <di:hasrole referid="user" role="teacher">
        <div class="mainContent">
          <div class="contentHeader"><fmt:message key="SelectStudent"/></div>
          <div class="contentBody">
            <b><fmt:message key="ExplanatoryTitle"/></b><br/><br/>
            <fmt:message key="ExplanatoryBody"/>
          </div>
        </div>
      </di:hasrole>
    </mm:compare>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$popreferids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
