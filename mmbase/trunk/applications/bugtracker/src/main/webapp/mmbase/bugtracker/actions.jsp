<%@page import="java.util.*"%>
<mm:import externid="action" />
<mm:present referid="action">
<mm:cloud logon="admin" method="delegate" authenticate="class">

<mm:url id="baseurl" write="false" referids="parameters,$parameters" page='<%=request.getScheme() + "://" + request.getServerName() +  request.getRequestURI() %>'>
   <mm:param name="btemplate" value="fullview.jsp" />
</mm:url>

<mm:import id="from"><%= org.mmbase.util.xml.UtilReader.get("bugtracker.xml").getProperties().getProperty("from", "Bugtracker <bugtracker@mmbase.org>")%></mm:import>

 <mm:import id="works">yep</mm:import>

<mm:compare value="sendaccountinfo" referid="action">
  <mm:import externid="email" />
  <mm:listnodescontainer type="users">
    <mm:constraint field="email" referid="email"/>
    <mm:maxnumber value="1"/>
    <mm:listnodes>
      <mm:field id="account" name="account" write="false"/>
      <mm:field id="password" name="password" write="false" />
      <mm:createnode id="emailnode" type="email">
        <mm:setfield name="mailtype">1</mm:setfield>
        <mm:setfield name="to"><mm:write referid="email" /></mm:setfield>
        <mm:setfield name="from"><mm:write referid="from" /></mm:setfield>
        <mm:setfield name="subject">Your MMBase BugTracker account</mm:setfield>
<mm:setfield name="body">
Your account info for the MMBase Bugtracker :

url: http://www.mmbase.org/bug
account : <mm:write referid="account" />
password : <mm:write referid="password" />
</mm:setfield>
	    </mm:createnode>
     <mm:node referid="emailnode">
       <mm:field name="mail(oneshot)" /> 
     </mm:node>
    </mm:listnodes> 
  </mm:listnodescontainer>

    <mm:present referid="emailnode">
	<mm:import id="message">email</mm:import>
    </mm:present>
    <mm:present referid="emailnode" inverse="true">
	<mm:import id="message">emailnotfound</mm:import>
    </mm:present>
</mm:compare>

<mm:compare value="checkuser" referid="action">
  <mm:import externid="account" />
  <mm:import externid="password" />
	<mm:listnodescontainer type="users">
	  <mm:constraint field="account" value="$account" />
	  <mm:constraint field="password" value="$password" />
    <mm:listnodes>
      <mm:import id="usernumber" jspvar="usernumber" ><mm:field name="number"/></mm:import>
    </mm:listnodes>
    </mm:listnodescontainer>
    <mm:present referid="usernumber">
      <mm:write referid="account" cookie="ca" />
      <mm:write referid="password" cookie="cw" />
      <%--  I HATE MMBASE. where is the mm:export 
	    <mm:write id="message" session="message">login</mm:write>
      --%>
        <mm:import id="message">login</mm:import>
        </mm:present>
      <mm:notpresent referid="usernumber">
        <mm:import id="message">failedlogin</mm:import>
        <% request.setAttribute("btemplate","changeUser.jsp"); %>
    	    <%-- response.sendRedirect("/development/bugtracker/changeUser.jsp?error=login&portal="+portal+"&page="+page2); --%>
      </mm:notpresent>
</mm:compare>

<mm:compare value="updatebug" referid="action">
  <mm:import externid="updater" required="true"/>
  <mm:import externid="bugreport" required="true"/>
  <mm:import externid="newissue" required="true"/>
  <mm:import externid="newbtype" required="true"/>
  <mm:import externid="newbstatus" required="true"/>
  <mm:import externid="newversion" required="true"/>
  <mm:import externid="newefixedin" required="true"/>
  <mm:import externid="newfixedin" required="true"/>
  <mm:import externid="newbpriority" required="true"/>
  <mm:import externid="newdescription" required="true"/>
  <mm:import externid="newrationale" required="true"/>
  <mm:import externid="newarea" required="true"/>
  <mm:import externid="oldarea" required="true"/>
  <mm:import externid="oldarearel" required="true"/>
  <% int now=(int)(System.currentTimeMillis()/1000); %>

	<mm:node id="usernode" number="$updater" />

	<!-- get all the vars for the history copy -->
	<mm:node id="bugreportnode" number="$bugreport">
		<mm:import id="oldissue"><mm:field name="issue" escape="none"/></mm:import>
		<mm:import id="oldbpriority"><mm:field name="bpriority" /></mm:import>
		<mm:import id="oldbtype"><mm:field name="btype" /></mm:import>
		<mm:import id="oldversion"><mm:field name="version" /></mm:import>
		<mm:import id="oldefixedin"><mm:field name="efixedin" /></mm:import>
		<mm:import id="oldfixedin"><mm:field name="fixedin" /></mm:import>
		<mm:import id="oldbstatus"><mm:field name="bstatus" /></mm:import>
		<mm:import id="olddescription"><mm:field name="description" escape="none"/></mm:import>
		<mm:import id="oldrationale"><mm:field name="rationale" escape="none"/></mm:import>
		<mm:import id="oldtime"><mm:field name="time" /></mm:import>
	</mm:node>

  <!-- create en bugreportupdates node with a copy of the current node -->
	<mm:createnode id="bugreportupdate" type="bugreportupdates">
		<mm:setfield name="issue"><mm:write referid="oldissue" escape="none"/></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="oldbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="oldbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="oldversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="oldefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="oldfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="oldbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="olddescription" escape="none"/></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="oldrationale" escape="none"/></mm:setfield>
		<mm:setfield name="time"><mm:write referid="oldtime" /></mm:setfield>
	</mm:createnode>

  <!-- get the id of the submitter and submitterrel -->
	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='submitter'" max="1">
			<mm:import id="submitter"><mm:field name="users.number" /></mm:import>
			<mm:import id="submitterrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>

  <!-- get the id of the lastupdateuser and lastupdateuserrel -->
	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='updater'" max="1">
			<mm:import id="lastupdateuser"><mm:field name="users.number" /></mm:import>
			<mm:import id="lastupdateuserrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>

  <!-- create a relation between the bugreport and the bugreportupdates node -->
  <mm:createrelation role="related" source="bugreportnode" destination="bugreportupdate" />

  <!-- override the current bugreport with the new values -->
	<mm:node number="$bugreport">
		<mm:setfield name="issue"><mm:write referid="newissue" escape="none"/></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="newbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="newefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="newfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" escape="none"/></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="newrationale" escape="none"/></mm:setfield>
		<mm:setfield name="time"><%=now%></mm:setfield>
	</mm:node>

  <!-- if the bug report never was updated -->
  <mm:present referid="lastupdateuser" inverse="true">
    <!-- and the bug has a submitter, create a new relation between the submitter and the bugreportupdate -->
    <mm:present referid="submitter">
	    <mm:node id="oldsubmitternode" number="$submitter" />
    	<mm:createrelation role="rolerel" source="bugreportupdate" destination="oldsubmitternode">
		    <mm:setfield name="role">submitter</mm:setfield>
      </mm:createrelation>
    </mm:present>
  </mm:present>

  <!-- if the bug report was updated before -->
  <mm:present referid="lastupdateuser">
    <!-- create a relation between the bugreportupdate and the lastupdater -->
    <mm:node id="lastupdateusernode" number="$lastupdateuser" />
    <mm:createrelation role="rolerel" source="bugreportupdate" destination="lastupdateusernode">
      <mm:setfield name="role">updater</mm:setfield>
    </mm:createrelation>
    <!-- also remove the lastupdater from the bugreport node -->
	  <mm:deletenode number="$lastupdateuserrel" />
  </mm:present>

  <!-- create a relation between the current updater and the bugreport -->
  <mm:createrelation role="rolerel" source="bugreportnode" destination="usernode">
    <mm:setfield name="role">updater</mm:setfield>
  </mm:createrelation>

  <!-- replace the link to a different area node if needed -->
  <mm:compare referid="oldarea" referid2="newarea" inverse="true">
	  <mm:deletenode referid="oldarearel" />
	  <mm:node id="newareanode" referid="newarea" />
    <mm:createrelation role="related" source="bugreportnode" destination="newareanode" />
  </mm:compare>
  <mm:import id="message">updatebug</mm:import>

  <% Set users = new HashSet() ; %>
  <mm:node referid="bugreportnode">
    <mm:related path="rolerel,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
    <mm:related path="areas,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
  </mm:node>
  <% Iterator userIterator = users.iterator(); 
     while(userIterator.hasNext()){
      String userNumber = (String)userIterator.next();
  %>
  
     <mm:context>
      <mm:node number="<%= userNumber %>" id="myUser"/>
      <mm:createnode id="emailnode" type="email">
        <mm:setfield name="mailtype">1</mm:setfield>
        <mm:setfield name="to"><mm:node referid="myUser"><mm:field name="email" /></mm:node></mm:setfield>
        <mm:setfield name="from"><mm:write referid="from" /></mm:setfield>
        <mm:setfield name="subject">[BT][<mm:node referid="newarea"><mm:field name="name"/></mm:node> update #<mm:node referid="bugreportnode"><mm:field name="bugid" />] <mm:field name="issue" escape="none"/></mm:node></mm:setfield>
        <mm:setfield name="body"><mm:node number="$updater">updater: <mm:field name="gui()"/> </mm:node>
           <mm:node number="$bugreport">
issue      :<mm:field name="issue" escape="none"/>
bug status : <mm:field name="bstatus"><mm:compare value="1">Open</mm:compare><mm:compare value="2">Accepted</mm:compare><mm:compare value="3">Rejected</mm:compare><mm:compare value="4">Pending</mm:compare><mm:compare value="5">Integrated</mm:compare><mm:compare value="6">Closed</mm:compare></mm:field>
rationale  :<mm:field name="rationale"/>

<mm:url referid="baseurl" escapeamps="false">
    <mm:param name="bugnumber"><mm:field name="bugid" /></mm:param>
</mm:url>
           </mm:node>
        </mm:setfield>
	    </mm:createnode>
     <mm:node referid="emailnode">
       <mm:field name="mail(oneshot)" /> 
     </mm:node>
    </mm:context>
  <% } %>

</mm:compare>

<mm:compare value="newbug" referid="action">
	<mm:import externid="submitter" />
	<mm:import externid="newissue" />
	<mm:import externid="newbtype" />
	<mm:import externid="newversion" />
	<mm:import externid="newbpriority" />
	<mm:import externid="newdescription" />
	<mm:import externid="newarea" />
	<% int now=(int)(System.currentTimeMillis()/1000); %>
	<mm:node id="usernode" number="$submitter" />
	<mm:node id="poolnode" number="Bugtracker.Start" />
	
	<mm:listnodes type="bugreports" orderby="bugid" directions="down"  max="1">
		<mm:import id="oldid"><mm:field name="bugid" /></mm:import>
	</mm:listnodes>
	<% int newid=1; %>
	<mm:present referid="oldid">
		<mm:import id="tmpid" jspvar="tmpid"><mm:write referid="oldid" /></mm:import>
		<% try {
			newid=Integer.parseInt(tmpid)+1;
		   } catch(Exception e) {}
		%>
	</mm:present>
	<mm:createnode id="bugreportnode" type="bugreports">
		<mm:setfield name="issue"><mm:write referid="newissue" escape="none"/></mm:setfield>
		<mm:setfield name="bugid"><%=newid%></mm:setfield>
		<mm:setfield name="bstatus">1</mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" escape="none"/></mm:setfield>
		<mm:setfield name="rationale"></mm:setfield>
		<mm:setfield name="time"><%=now%></mm:setfield>
	</mm:createnode>
  <mm:createrelation role="related" source="bugreportnode" destination="poolnode" />
  <mm:createrelation role="rolerel" source="bugreportnode" destination="usernode">
    <mm:setfield name="role">submitter</mm:setfield>
  </mm:createrelation>
	<mm:node id="areanode" number="$newarea" />
  <mm:createrelation role="related" source="bugreportnode" destination="areanode" />
  <mm:import id="message">newbug</mm:import>
  <% request.setAttribute("btemplate","fullview.jsp"); %>
  <mm:node referid="bugreportnode">
    <mm:field name="number" jspvar="bugreportNumber">
    <% request.setAttribute("bugreport",bugreportNumber); %>
    </mm:field>
  </mm:node>
  <% Set users = new HashSet() ; %>
  <mm:node referid="bugreportnode">
    <mm:related path="rolerel,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
    <mm:related path="areas,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
  </mm:node>
  <% Iterator userIterator = users.iterator(); 
     while(userIterator.hasNext()){
      String userNumber = (String)userIterator.next();
  %>
  
     <mm:context>
      <mm:node number="<%= userNumber %>" id="myUser"/>
      <mm:createnode id="emailnode" type="email">
        <mm:setfield name="mailtype">1</mm:setfield>
        <mm:setfield name="to"><mm:node referid="myUser"><mm:field name="email" /></mm:node></mm:setfield>
        <mm:setfield name="from"><mm:write referid="from" /></mm:setfield>
        <mm:setfield name="subject">[BT][<mm:node referid="newarea"><mm:field name="name"/></mm:node> #<mm:node referid="bugreportnode"><mm:field name="bugid" />]NEW <mm:field name="issue" escape="none"/></mm:node></mm:setfield>
        <mm:setfield name="body">
           <mm:node referid="bugreportnode">
issue      :<mm:field name="issue" escape="none"/>
bug status : <mm:field name="bstatus"><mm:compare value="1">Open</mm:compare><mm:compare value="2">Accepted</mm:compare><mm:compare value="3">Rejected</mm:compare><mm:compare value="4">Pending</mm:compare><mm:compare value="5">Integrated</mm:compare><mm:compare value="6">Closed</mm:compare></mm:field>
rationale  :<mm:field name="rationale"/>

<mm:url referid="baseurl" escapeamps="false">
    <mm:param name="bugnumber"><mm:field name="bugid" /></mm:param>
</mm:url>
           </mm:node>
        </mm:setfield>
	    </mm:createnode>
     <mm:node referid="emailnode">
       <mm:field name="mail(oneshot)" /> 
     </mm:node>
    </mm:context>
  <% } %>
</mm:compare>

<mm:compare value="deletebugreport" referid="action">
    <mm:import externid="bugreport" jspvar="bugreport" />
    <mm:node id="bugnode" number="$bugreport">
	<mm:relatednodes type="mmevents">
		<mm:deletenode deleterelations="true" />
	</mm:relatednodes>
	<mm:relatednodes type="bugreportupdates">
		<mm:deletenode deleterelations="true" />
	</mm:relatednodes>
	<mm:deletenode deleterelations="true" />
    </mm:node>
    <mm:import id="message">reportdeleted</mm:import>
</mm:compare>

<mm:compare value="addadmin" referid="action">
    <mm:import externid="newadmin" />
    <mm:node id="groupnode" number="BugTracker.Admins" />
    <mm:node id="usernode" referid="newadmin" />
    <mm:createrelation role="related" source="groupnode" destination="usernode" />
</mm:compare>

<mm:compare value="removeadmin" referid="action">
    <mm:import externid="deleteadmin">none</mm:import>
    <mm:node number="BugTracker.Admins">
	<mm:related path="insrel,users" constraints="users.number=$deleteadmin">
		<mm:node element="insrel">
    			<mm:deletenode />
		</mm:node>
	</mm:related>
    </mm:node>
</mm:compare>

<mm:compare value="addcommitor" referid="action">
    <mm:import externid="newcommitor" />
    <mm:node id="groupnode" number="BugTracker.Commitors" />
    <mm:node id="usernode" referid="newcommitor" />
    <mm:createrelation role="related" source="groupnode" destination="usernode" />
</mm:compare>

<mm:compare value="removecommitor" referid="action">
    <mm:import externid="deletecommitor">none</mm:import>
    <mm:node number="BugTracker.Commitors">
	<mm:related path="insrel,users" constraints="users.number=$deletecommitor">
		<mm:node element="insrel">
    			<mm:deletenode />
		</mm:node>
	</mm:related>
    </mm:node>
</mm:compare>

<mm:compare value="addarea" referid="action">
    <mm:import externid="newarea" />
    <mm:compare referid="newarea" value="" inverse="true">
    <mm:node id="poolnode" number="BugTracker.Start" />
    <mm:createnode id="areanode" type="areas">
	<mm:setfield name="name"><mm:write referid="newarea" /></mm:setfield>
    </mm:createnode>
    <mm:createrelation role="related" source="poolnode" destination="areanode" />
    </mm:compare>
</mm:compare>

<mm:compare value="removearea" referid="action">
    <mm:import externid="deletearea" />
    <mm:deletenode referid="deletearea" deleterelations="true" />
</mm:compare>

<mm:compare value="updateuser" referid="action">
    <mm:import externid="account" />
    <mm:import externid="password" />
    <mm:import externid="firstname" />
    <mm:import externid="lastname" />
    <mm:import externid="email" />
    <mm:node referid="user">
	<mm:setfield name="firstname"><mm:write referid="firstname" /></mm:setfield>
	<mm:setfield name="lastname"><mm:write referid="lastname" /></mm:setfield>
	<mm:setfield name="email"><mm:write referid="email" /></mm:setfield>
    </mm:node>
</mm:compare>

<mm:compare value="addcomment" referid="action">
        <mm:import externid="newtitle" required="true"/>
        <mm:import externid="newtext" required="true"/>
        <mm:import externid="newuser" required="true"/>
        <mm:import externid="bugreport" required="true"/>
	<mm:node referid="bugreport" id="bugreportnode" />
	<mm:node referid="newuser" id="usernode" />
	<mm:createnode id="commentnode" type="comments">
		<mm:setfield name="title"><mm:write referid="newtitle" escape="none" /></mm:setfield>
		<mm:setfield name="body"><mm:write referid="newtext" escape="none" /></mm:setfield>
	</mm:createnode>
   	<mm:createrelation role="rolerel" source="bugreportnode" destination="commentnode">
		<mm:setfield name="role">regular</mm:setfield>
	</mm:createrelation>
   	<mm:createrelation role="related" source="usernode" destination="commentnode" />
  <% Set users = new HashSet() ; %>
  <mm:node referid="bugreportnode">
    <mm:relatednodes type="areas" max="1">
       <mm:node id="newarea"/>
    </mm:relatednodes>
    <mm:related path="rolerel,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
    <mm:related path="areas,users"  fields="users.number" distinct="true">
       <mm:node element="users" jspvar="luser">
            <% users.add("" + luser.getNumber()); %>
       </mm:node>
     </mm:related>
  </mm:node>
  <% Iterator userIterator = users.iterator(); 
     while(userIterator.hasNext()){
      String userNumber = (String)userIterator.next();
  %>
  
     <mm:context>
      <mm:node number="<%= userNumber %>" id="myUser"/>
      <mm:createnode id="emailnode" type="email">
        <mm:setfield name="mailtype">1</mm:setfield>
        <mm:setfield name="to"><mm:node referid="myUser"><mm:field name="email" /></mm:node></mm:setfield>
        <mm:setfield name="from"><mm:write referid="from" /></mm:setfield>
        <mm:setfield name="subject">[BT][<mm:node referid="newarea"><mm:field name="name"/></mm:node> #<mm:node referid="bugreportnode"><mm:field name="bugid" />]COMMENT <mm:field name="issue" escape="none"/></mm:node></mm:setfield>
        <mm:setfield name="body">
           <mm:node referid="bugreportnode">
issue      :<mm:field name="issue" escape="none"/>
bug status : <mm:field name="bstatus"><mm:compare value="1">Open</mm:compare><mm:compare value="2">Accepted</mm:compare><mm:compare value="3">Rejected</mm:compare><mm:compare value="4">Pending</mm:compare><mm:compare value="5">Integrated</mm:compare><mm:compare value="6">Closed</mm:compare></mm:field>
rationale  :<mm:field name="rationale"/>

user <mm:node referid="newuser"><mm:field name="account"/></mm:node> commented
<mm:write referid="newtitle" escape="none"/>
<mm:write referid="newtext" escape="none"/>


<mm:url referid="baseurl" escapeamps="false">
     <mm:param name="bugnumber"><mm:field name="bugid" /></mm:param>
</mm:url>
           </mm:node>
        </mm:setfield>
	    </mm:createnode>
     <mm:node referid="emailnode">
       <mm:field name="mail(oneshot)" /> 
     </mm:node>
    </mm:context>
  <% } %>
</mm:compare>

<mm:compare value="addmaintainer" referid="action">
<mm:import externid="maintainer" required="true"/>
<mm:import externid="bugreport" required="true"/>
<mm:node id="bugnode" number="$bugreport" />
<mm:node id="usernode" number="$maintainer" />
<mm:createrelation role="rolerel" source="bugnode" destination="usernode">
	<mm:setfield name="role">maintainer</mm:setfield>
</mm:createrelation>
     <% request.setAttribute("btemplate","fullview.jsp"); %>
     <% request.setAttribute("flap","change"); %>
	   <mm:import id="message">maintaineradded</mm:import>
</mm:compare>

<mm:compare value="removemaintainer" referid="action">
<mm:import externid="bugreport" required="true"/>
    <mm:import externid="maintainerrel" />
    <mm:deletenode referid="maintainerrel" />
	   <mm:import id="message">maintainerremoved</mm:import>
</mm:compare>

<mm:compare value="removemyselfinterested" referid="action">
 <mm:context>
    <mm:import externid="bugreport" required="true"/>
     <%@include file="login.jsp"%>
    <mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="users.number=$user and rolerel.role='interested'">
	<mm:node element="rolerel">
		<mm:deletenode />
	</mm:node>
    </mm:list>
 </mm:context>
	  <mm:import id="message">removedmyselfinterested</mm:import>
     <% request.setAttribute("btemplate","fullview.jsp"); %>
     <% request.setAttribute("flap","change"); %>
</mm:compare>

<mm:compare value="addmyselfinterested" referid="action">
 <mm:context>
    <mm:import externid="bugreport" required="true"/>
     <%@include file="login.jsp"%>
    <mm:node id="bugnode" number="$bugreport" />
    <mm:node id="usernode" number="$user" />
    <mm:createrelation role="rolerel" source="bugnode" destination="usernode">
		<mm:setfield name="role">interested</mm:setfield>
    </mm:createrelation>
     <% request.setAttribute("btemplate","fullview.jsp"); %>
     <% request.setAttribute("flap","change"); %>
  </mm:context>
	<mm:import id="message">myselfinterestedadded</mm:import>
</mm:compare>
<mm:present referid="message">
<mm:write referid="message" write="false" jspvar="msg">
<%--
<script>
	alert("message <mm:write referid="message"/>");
</script>
--%>
        <% request.setAttribute("message",msg); %>
</mm:write>
</mm:present>
</mm:cloud>
<mm:notpresent referid="works">
<div style="border: 1px solid black;">
<div style="border: 2px dotted red; background: yellow;font-weight: bold;color: red">
<img src="images/icon_error.gif"/>&nbsp;Failed to get a cloud via de delegate method.
add<br/>
<code style="background: white">
&lt;authenticate class="<%= this.getClass().getName().replaceAll("\\.","\\.") %>"&gt;<br/>
&nbsp;&lt;property name="username" value="admin" /&gt;<br/>
&lt;/authenticate&gt;<br/>
</code> to  classauthentication.xml
</div>
</div>
</mm:notpresent>
</mm:present>
