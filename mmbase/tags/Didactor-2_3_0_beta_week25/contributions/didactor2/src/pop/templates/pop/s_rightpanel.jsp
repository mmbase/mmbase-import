<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace"  escaper="none">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="getids.jsp" %>

  <% boolean isEmpty = true; 
     String msgString = "";
  %>

      <div class="mainContent">
        <mm:compare referid="popcmd" value="getinvite">
          <mm:import id="currentpop" reset="true">0</mm:import>
        </mm:compare>
        <mm:compare referid="currentpop" value="-1">
          <div class="contentBody"> 
            <mm:compare referid="student" referid2="user">
              <p><di:translate key="pop.msgfornopop" /></p>
            </mm:compare>
            <mm:compare referid="student" referid2="user" inverse="true">
              <mm:node number="$student">
                <mm:import id="studentfullname"><mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/></mm:import>
                <p><di:translate key="pop.msgfornopop1" /> <mm:write referid="studentfullname"/> <di:translate key="pop.msgfornopop2" />
                   <mm:write referid="studentfullname"/> <di:translate key="pop.msgfornopop3" /></p>
                <mm:remove referid="studentfullname"/>
              </mm:node>
            </mm:compare>
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
            <mm:compare referid="popcmd" value="continue">
              <mm:remove referid="popcmd"/>
              <mm:import id="popcmd">editcomp</mm:import>
            </mm:compare>
            <mm:compare referid="popcmd" value="savecomp">
              <%@ include file="savecomp.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgselfgradedone" /></mm:import>
              <% msgString = dummy; %>
              <mm:remove referid="popcmd"/>
              <mm:import id="popcmd">no</mm:import>
            </mm:compare>
            <mm:compare referid="popcmd" value="sendinvite">
              <%@ include file="sendinvite.jsp" %>
              <mm:import id="dummy" jspvar="dummy" vartype="String" reset="true"><di:translate key="pop.msgsendinvitedone" /></mm:import>
              <% msgString = dummy; %>
              <mm:remove referid="popcmd"/>
              <mm:import id="popcmd">editcomp</mm:import>
            </mm:compare>
            <mm:compare referid="popcmd" value="invite">
              <%@ include file="invite.jsp" %>
              <mm:remove referid="popcmd"/>
              <mm:import id="popcmd">-1</mm:import>
            </mm:compare>
            <mm:compare referid="popcmd" value="getinvite">
              <%@ include file="getinvite.jsp" %>
              <mm:remove referid="popcmd"/>
              <mm:import id="popcmd">-1</mm:import>
            </mm:compare>
            <mm:compare referid="popcmd" value="editcomp">
              <jsp:include page="compedit.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
            <mm:compare referid="popcmd" value="no">
              <jsp:include page="comptable.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
          </mm:compare>
          <mm:compare referid="currentfolder" value="1">
              <div class="contentHeader"><di:translate key="pop.progressmonitor" />
                <%@include file="nameintitle.jsp" %>
              </div>
              <mm:compare referid="popcmd" value="intake">
                <mm:import id="competencies" jspvar="competencies" />
                <jsp:include page="intaketest.jsp">
                  <jsp:param name="competencies" value="<%= competencies %>"/>
                </jsp:include>
                <mm:remove referid="popcmd"/>
                <mm:import id="popcmd">-1</mm:import>
              </mm:compare>
              <mm:compare referid="popcmd" value="detail">
                <jsp:include page="progressdetail.jsp"/>
                <mm:remove referid="popcmd"/>
                <mm:import id="popcmd">-1</mm:import>
              </mm:compare>
              <mm:compare referid="popcmd" value="no">
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
            <mm:compare referid="popcmd" value="-1" inverse="true">
              <jsp:include page="todolist.jsp">
                <jsp:param name="msg" value="<%= msgString %>"/>
              </jsp:include>
            </mm:compare>
          </mm:compare>
        </mm:compare>
      </div>


</mm:cloud>
</mm:content>
