<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:content type="text/html" expires="0">
<mm:cloud method="asis">

  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>

  <mm:import externid="bugreport" />
  <mm:import externid="flap">overview</mm:import>
  
  
  <mm:node number="$bugreport">
    <mm:related path="rolerel,users" constraints="rolerel.role='maintainer'" max="1">
      <mm:import id="hasmaintainers">yes</mm:import>
    </mm:related>
    

    <mm:related path="rolerel,users" constraints="rolerel.role='submitter'">
      <mm:import id="submitter"><mm:field name="users.number" /></mm:import>
    </mm:related>
    
    <%@ include file="parts/flaps.jsp" %>

		<mm:compare referid="flap" value="overview">
      <%@ include file="parts/overview.jsp" %> 
      <%@ include file="parts/comments.jsp" %> 
    </mm:compare>
		<mm:compare referid="flap" value="history">
      <%@ include file="parts/history.jsp" %>
    </mm:compare>
		<mm:compare referid="flap" value="change">
      <%@ include file="parts/change.jsp" %>
    </mm:compare>

</mm:node>
</mm:cloud>
</mm:content>
