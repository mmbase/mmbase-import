<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
		<td COLSPAN="6">
			  The changed you can make to this bugreport depend on your user level<br><br>
			<mm:present referid="user" inverse="true">
				You need to be logged in, goto MyBug or Mainpage
			</mm:present>


			<!-- user and a commitor -->
			<mm:present referid="user">
				<mm:node referid="user">
				<mm:present referid="commitor">
					You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of commitor this allows you the following actions<br><br>
					Update report <A HREF="updateBugreport.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />

					<mm:present referid="hasmaintainers" inverse="true">
						Delete this bugreport <A HREF="showMessage.jsp?action=deletebugreport&portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
					</mm:present>

					Add maintainer <A HREF="addMaintainer.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
					Remove maintainer <A HREF="removeMaintainer.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
				<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'">
					Remove yourself as interested <A HREF="fullview.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />&portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&action=removemyselfinterested"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
					<mm:import id="userfound" />
				</mm:list>
				<mm:present referid="userfound" inverse="true">
				Add yourself as interested <A HREF="fullview.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />&portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&action=addmyselfinterested"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
				</mm:present>
				</mm:present>


			<!-- user but not a commitor -->
				<mm:present referid="commitor" inverse="true">
					You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of developer this allows you the following actions<p />
					<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'">
						Remove yourself as interested <A HREF="executes/removeMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
						<mm:import id="userfound" />
					</mm:list>
					<mm:present referid="userfound" inverse="true">
						Add yourself as interested <A HREF="executes/addMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow-right.gif" BORDER="0"></A><p />
					</mm:present>
				</mm:present>
				</mm:node>
			</mm:present>
		</td>
</tr>
</table>
