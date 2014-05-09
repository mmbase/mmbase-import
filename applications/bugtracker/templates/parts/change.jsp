<table class="list">
<tr>
  <td colspan="6">
   The changes you can make to this bugreport depend on your user level<br><br>
   <mm:present referid="user" inverse="true">
     You need to be logged in, goto MyBug or Mainpage
   </mm:present>


   <!-- user and a commitor -->
   <mm:present referid="user">
   <mm:node referid="user">
    <mm:present referid="commitor">
       You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of commitor this allows you the following actions<br><br>
					<a href="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate">updateBugreport.jsp</mm:param></mm:url>">Update report <img src="images/arrow-right.png" border="0"></a><p />
					<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="action" value="deletebugreport"/></mm:url>" onclick="return  confirm('You are about to delete a bug report!');">Delete this bugreport <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />

					<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate">addMaintainer.jsp</mm:param></mm:url>">Add maintainer <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />

					<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate">removeMaintainer.jsp</mm:param></mm:url>">Remove maintainer <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />

				<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'">
					<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="action">removemyselfinterested</mm:param></mm:url>" max="1">Remove yourself as interested <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />
					<mm:import id="userfound" />
				</mm:list>

				<mm:present referid="userfound" inverse="true">
				<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="action">addmyselfinterested</mm:param></mm:url>">Add yourself as interested  <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />
				</mm:present>


				</mm:present>
			<!-- user but not a commitor -->
				<mm:present referid="commitor" inverse="true">
					You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of developer this allows you the following actions<p />
				<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'" max="1">
					<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="action">removemyselfinterested</mm:param></mm:url>">Remove yourself as interested <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />
					<mm:import id="userfound" />
				</mm:list>

				<mm:present referid="userfound" inverse="true">
				<A HREF="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="action">addmyselfinterested</mm:param></mm:url>">Add yourself as interested  <IMG SRC="images/arrow-right.png" BORDER="0"></A><p />
				</mm:present>
				</mm:present>
			</mm:node>
		</mm:present>
		</td>
</tr>
</table>
