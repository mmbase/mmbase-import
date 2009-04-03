<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:import externid="type" />
<mm:cloud method="asis">

  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuEmail">
      <mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids" write="false">
        <mm:param name="so">down</mm:param>
        <a href="${_}" class="menubar">
        </mm:treefile>
    </mm:compare>

    <mm:compare referid="type" value="option">
      <option value="<mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">
    </mm:compare>
    <% int total = 0; %>
    <di:translate key="email.emailtitle" />
    <mm:node number="$user">
      <mm:relatednodescontainer type="mailboxes">
        <mm:constraint field="type" value="2" inverse="true" /> <!-- don't count removed items -->
        <mm:relatednodes>
          <mm:relatednodescontainer type="emails">
            <mm:constraint field="type" value="2" operator="=" /> <%-- find new mails --%>
            <mm:import id="size" jspvar="size" vartype="Integer"><mm:size /></mm:import>
            <% total+=size.intValue(); %>
          </mm:relatednodescontainer>
        </mm:relatednodes>
      </mm:relatednodescontainer>  
    </mm:node>
    (<%= total %>)
    <mm:compare referid="type" value="div">
    </a>
  </div>
</mm:compare>
<mm:compare referid="type" value="option">
</option>
</mm:compare>
</mm:cloud>
