
<mm:import externid="action" />
<mm:present referid="action">
<mm:cloud logon="admin" pwd="dontusesvp">

<mm:remove referid="portal" />
<mm:remove referid="page" />
<mm:import externid="portal" jspvar="portal" />
<mm:import externid="page" jspvar="page2" />


<mm:compare value="sendaccountinfo" referid="action">
    <mm:import externid="email" />
    <mm:listnodes type="users" constraints="email='$email'" max="1">
	<mm:import id="account"><mm:field name="account" /></mm:import>
	<mm:import id="password"><mm:field name="password" /></mm:import>
	<mm:createnode id="emailnode" type="email">
		<mm:setfield name="mailtype">1</mm:setfield>
		<mm:setfield name="to"><mm:write referid="email" /></mm:setfield>
		<mm:setfield name="from">bugtracker@mmbase.org</mm:setfield>
		<mm:setfield name="subject">Your MMBase BugTracker account</mm:setfield>
<mm:setfield name="body">
Your account info for the MMBase Bugtracker :

url: http://www.mmbase.org/bug
account : <mm:write referid="account" />
password : <mm:write referid="password" />
</mm:setfield>
	</mm:createnode>
    </mm:listnodes> 

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
        <mm:listnodes type="users" constraints="(account='$account' AND password='$password')">
                <mm:import id="usernumber" jspvar="usernumber" ><mm:field name="number"/></mm:import>
        </mm:listnodes>
        <mm:present referid="usernumber">
            <mm:write referid="account" cookie="ca" />
            <mm:write referid="password" cookie="cw" />
		<mm:import id="message">login</mm:import>
        </mm:present>
        <mm:notpresent referid="usernumber">
		<mm:import id="message">should have jumper</mm:import>
    		<%response.sendRedirect("/development/bugtracker/changeUser.jsp?error=login&portal="+portal+"&page="+page2);%>
        </mm:notpresent>
</mm:compare>

<mm:compare value="updatebug" referid="action">
<mm:import externid="updater" />
<mm:import externid="bugreport" />
<mm:import externid="newissue" />
<mm:import externid="newbtype" />
<mm:import externid="newbstatus" />
<mm:import externid="newversion" />
<mm:import externid="newefixedin" />
<mm:import externid="newfixedin" />
<mm:import externid="newbpriority" />
<mm:import externid="newdescription" />
<mm:import externid="newrationale" />
<mm:import externid="newarea" />
<mm:import externid="oldarea" />
<mm:import externid="oldarearel" />
<% int now=(int)(System.currentTimeMillis()/1000); %>

	<mm:node id="usernode" number="$updater" />

	<!-- get all the vars for the history copy -->
	<mm:node id="bugreportnode" number="$bugreport">
		<mm:import id="oldissue"><mm:field name="issue" /></mm:import>
		<mm:import id="oldbpriority"><mm:field name="bpriority" /></mm:import>
		<mm:import id="oldbtype"><mm:field name="btype" /></mm:import>
		<mm:import id="oldversion"><mm:field name="version" /></mm:import>
		<mm:import id="oldefixedin"><mm:field name="efixedin" /></mm:import>
		<mm:import id="oldfixedin"><mm:field name="fixedin" /></mm:import>
		<mm:import id="oldbstatus"><mm:field name="bstatus" /></mm:import>
		<mm:import id="olddescription"><mm:field name="description" /></mm:import>
		<mm:import id="oldrationale"><mm:field name="rationale" /></mm:import>
		<mm:import id="oldtime"><mm:field name="time" /></mm:import>
	</mm:node>

	<mm:createnode id="updatenode" type="bugreportupdates">
		<mm:setfield name="issue"><mm:write referid="oldissue" /></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="oldbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="oldbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="oldversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="oldefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="oldfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="oldbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="olddescription" /></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="oldrationale" /></mm:setfield>
		<mm:setfield name="time"><mm:write referid="oldtime" /></mm:setfield>
	</mm:createnode>

	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='submitter'" max="1">
			<mm:import id="submitter"><mm:field name="users.number" /></mm:import>
			<mm:import id="submitterrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>
	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='updater'" max="1">
			<mm:import id="oldupdater"><mm:field name="users.number" /></mm:import>
			<mm:import id="oldupdaterrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>

    	<mm:createrelation role="related" source="bugreportnode" destination="updatenode" />

	<mm:node number="$bugreport">
		<mm:setfield name="issue"><mm:write referid="newissue" /></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="newbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="newefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="newfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="newrationale" /></mm:setfield>
		<mm:setfield name="time"><%=now%></mm:setfield>
	</mm:node>

    <mm:present referid="oldupdater" inverse="true">
    <mm:present referid="submitter">
	<mm:node id="oldsubmitternode" number="$submitter" />
    	<mm:createrelation role="rolerel" source="updatenode" destination="oldsubmitternode">
		<mm:setfield name="role">submitter</mm:setfield>
    	</mm:createrelation>
    </mm:present>
    </mm:present>

    <mm:present referid="oldupdater">
	<mm:node id="oldupdaternode" number="$oldupdater" />
    	<mm:createrelation role="rolerel" source="updatenode" destination="oldupdaternode">
		<mm:setfield name="role">updater</mm:setfield>
    	</mm:createrelation>
	<mm:deletenode number="$oldupdaterrel" />
    </mm:present>

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
		<mm:setfield name="issue"><mm:write referid="newissue" /></mm:setfield>
		<mm:setfield name="bugid"><%=newid%></mm:setfield>
		<mm:setfield name="bstatus">1</mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
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
        <mm:import externid="newtitle" />
        <mm:import externid="newtext" />
        <mm:import externid="newuser" />
	<mm:node referid="bugreport" id="bugreportnode" />
	<mm:node referid="newuser" id="usernode" />
	<mm:createnode id="commentnode" type="comments">
		<mm:setfield name="title"><mm:write referid="newtitle" /></mm:setfield>
		<mm:setfield name="body"><mm:write referid="newtext" /></mm:setfield>
	</mm:createnode>
   	<mm:createrelation role="rolerel" source="bugreportnode" destination="commentnode">
		<mm:setfield name="role">regular</mm:setfield>
	</mm:createrelation>
   	<mm:createrelation role="related" source="usernode" destination="commentnode" />
</mm:compare>

<mm:compare value="addmaintainer" referid="action">
<mm:import externid="maintainer" />
<mm:node id="bugnode" number="$bugreport" />
<mm:node id="usernode" number="$maintainer" />
<mm:createrelation role="rolerel" source="bugnode" destination="usernode">
	<mm:setfield name="role">maintainer</mm:setfield>
</mm:createrelation>
</mm:compare>

<mm:compare value="removemaintainer" referid="action">
    <mm:import externid="maintainerrel" />
    <mm:deletenode referid="maintainerrel" />
</mm:compare>

<mm:compare value="removemyselfinterested" referid="action">
    <mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="users.number=$user and rolerel.role='interested'">
	<mm:node element="rolerel">
		<mm:deletenode />
	</mm:node>
    </mm:list>
</mm:compare>

<mm:compare value="addmyselfinterested" referid="action">
    <mm:node id="bugnode" number="$bugreport" />
    <mm:node id="usernode" number="$user" />
    <mm:createrelation role="rolerel" source="bugnode" destination="usernode">
		<mm:setfield name="role">interested</mm:setfield>
    </mm:createrelation>
</mm:compare>

</mm:cloud>
</mm:present>
