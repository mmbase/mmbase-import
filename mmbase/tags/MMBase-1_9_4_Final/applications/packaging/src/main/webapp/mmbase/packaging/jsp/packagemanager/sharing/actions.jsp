<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />
<mm:import externid="version" />
<mm:import externid="provider" />

<mm:compare value="makeactive" referid="action">
	<mm:booleanfunction set="mmpm" name="makeShareActive" referids="id,version">
	</mm:booleanfunction>
</mm:compare>
<mm:compare value="makeinactive" referid="action">
	 <mm:booleanfunction set="mmpm" name="makeShareInactive" referids="id,version">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="addshare" referid="action">
	<mm:import externid="newshare" />
	<mm:functioncontainer>
	<mm:param name="id" value="$newshare" />
	<mm:booleanfunction set="mmpm" name="addShare">
	</mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>


<mm:compare value="addbundleshare" referid="action">
	<mm:import externid="newshare" />
	<mm:functioncontainer>
	<mm:param name="id" value="$newshare" />
	<mm:booleanfunction set="mmpm" name="addBundleShare" >
	</mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>

<mm:compare value="delshare" referid="action">
	  <mm:booleanfunction set="mmpm" name="delShare" referids="id">
	  </mm:booleanfunction>
</mm:compare>

<mm:compare value="delbundleshare" referid="action">
	  <mm:booleanfunction set="mmpm" name="delBundleShare" referids="id">
	  </mm:booleanfunction>
</mm:compare>

<mm:compare value="adduser" referid="action">
	<mm:import externid="newuser" />
	<mm:booleanfunction set="mmpm" name="addShareUser" referids="id,version,newuser">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addsharegroup" referid="action">
	<mm:import externid="newgroup" />
	<mm:booleanfunction set="mmpm" name="addShareGroup" referids="id,version,newgroup">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="addgroupuser" referid="action">
	<mm:import externid="group" />
	<mm:import externid="newuser" />
	<mm:booleanfunction set="mmpm" name="addGroupUser" referids="group,newuser">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="removegroupuser" referid="action">
	<mm:import externid="group" />
	<mm:import externid="deluser" />
	<mm:booleanfunction set="mmpm" name="removeGroupUser" referids="group,deluser">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="creategroup" referid="action">
	<mm:import externid="newgroup" />
	<mm:booleanfunction set="mmpm" name="createGroup" referids="newgroup">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="removegroup" referid="action">
	<mm:import externid="delgroup" />
	<mm:booleanfunction set="mmpm" name="removeGroup" referids="delgroup">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="newuser" referid="action">
	<mm:import externid="newuser" />
	<mm:import externid="newpassword" />
	<mm:import externid="newmethod" />
	<mm:import externid="newip" />
	<mm:booleanfunction set="mmpm" name="createNewUser" referids="newuser,newpassword,newmethod,newip">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="changeuser" referid="action">
	<mm:import externid="newaccount" />
	<mm:import externid="newpassword" />
	<mm:import externid="newmethod" />
	<mm:import externid="newhost" />
	<mm:functioncontainer>
  	   <mm:param name="account" value="$newaccount" />
  	   <mm:param name="password" value="$newpassword" />
  	   <mm:param name="method" value="$newmethod" />
  	   <mm:param name="host" value="$newhost" />
	   <mm:booleanfunction set="mmpm" name="changeUserSettings">
	  </mm:booleanfunction>
 	</mm:functioncontainer>
</mm:compare>

<mm:compare value="removeuser" referid="action">
	<mm:import externid="olduser" />
	  <mm:booleanfunction set="mmpm" name="removeShareUser" referids="id,version,olduser">
	  </mm:booleanfunction>
</mm:compare>


<mm:compare value="removesharegroup" referid="action">
	<mm:import externid="delgroup" />
	  <mm:booleanfunction set="mmpm" name="removeShareGroup" referids="id,version,delgroup">
	  </mm:booleanfunction>
</mm:compare>


<mm:compare value="deluser" referid="action">
	<mm:import externid="deluser" />
	  <mm:booleanfunction set="mmpm" name="delUser" referids="deluser">
	  </mm:booleanfunction>
</mm:compare>
</mm:cloud>
