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

    <mm:compare referid="whatselected" value="0">
        <div class="mainContent">
          <div class="contentHeader"><di:translate key="pop.selectstudent" /></div>
          <div class="contentBody">
            <b><di:translate key="pop.explanatorytitle" /></b><br/><br/>
            <di:translate key="pop.explanatorybody" />
          </div>
        </div>
    </mm:compare>
    <mm:compare referid="whatselected" value="0" inverse="true">
      <div class="mainContent">
          <mm:compare referid="currentfolder" value="1">
              <mm:compare referid="whatselected" value="class">
                <jsp:include page="coachpredetail.jsp"/>
              </mm:compare>
              <mm:compare referid="whatselected" value="wgroup">
                <jsp:include page="coachpredetail.jsp"/>
              </mm:compare>
            <mm:compare referid="whatselected" value="student">
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
          </mm:compare>

      </div>
    </mm:compare>


</mm:cloud>
</mm:content>