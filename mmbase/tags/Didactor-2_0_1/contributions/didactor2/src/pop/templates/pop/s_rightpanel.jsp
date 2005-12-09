<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="getids.jsp" %>

  <% boolean isEmpty = true; 
     String msgString = "";
  %>

      <div class="mainContent">
        <mm:compare referid="command" value="getinvite">
          <mm:import id="currentpop" reset="true">0</mm:import>
        </mm:compare>
        <mm:compare referid="currentpop" value="-1">
          <div class="contentBody"> 
            <p><di:translate key="pop.msgfornopop" /></p>
          </div>
        </mm:compare>
        <mm:compare referid="currentpop" value="-1" inverse="true">
          <mm:compare referid="currentfolder" value="-1">
            <div class="contentHeader"><di:translate key="pop.competencies" /> <mm:compare referid="currentprofile" value="-1" inverse="true"
                ><mm:node number="$currentprofile"><mm:field name="name"/></mm:node></mm:compare>
              <%@include file="nameintitle.jsp" %>
            </div>
            <%@ include file="todo.jsp" %>
            <%@ include file="docs.jsp" %>
            <mm:compare referid="command" value="continue">
              <mm:remove referid="command"/>
              <mm:import id="command">editcomp</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="savecomp">
              <%@ include file="savecomp.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgselfgradedone" /></mm:import>
              <% msgString = dummy; %>
              <mm:remove referid="command"/>
              <mm:import id="command">no</mm:import>
            </mm:compare>
            <mm:compare referid="command" value="sendinvite">
              <%@ include file="sendinvite.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgsendinvitedone" /></mm:import>
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
              <div class="contentHeader"><di:translate key="pop.progressmonitor" />
                <%@include file="nameintitle.jsp" %>
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
          <mm:compare referid="currentfolder" value="2">
            <div class="contentHeader"><di:translate key="pop.todoitems" />
              <%@include file="nameintitle.jsp" %>
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


</mm:cloud>
</mm:content>