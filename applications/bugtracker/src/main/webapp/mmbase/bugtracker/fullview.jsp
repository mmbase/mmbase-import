<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:content type="text/html" expires="0">
<mm:cloud method="asis">

  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>

  <mm:import externid="bugnumber" />
  <mm:present referid="bugnumber">
    <mm:listnodescontainer type="bugreports">
       <mm:constraint field="bugid" value="$bugnumber" />
       <mm:listnodes max="1">
	 <mm:node id="bugreport" />
       </mm:listnodes>
    </mm:listnodescontainer>
  </mm:present>
  <mm:notpresent referid="bugnumber">
    <mm:import externid="bugreport" required="true" />
  </mm:notpresent>
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
