<table border="0" width="650">
<TR>
		<TD BGCOLOR="#00425A" COLSPAN="6">
			  <br>
			  The changed you can make to this bugreport depend on your user level<br><br>
			<font color="#ffffff">
			<mm:present referid="user" inverse="true">
				You need to be logged in, goto MyBug or Mainpage
			</mm:present>


			<!-- user and a commitor -->
			<mm:present referid="user">
				<mm:node referid="user">
				<mm:present referid="commitor">
					You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of commitor this allows you the following actions<br><br>
					Update report <A HREF="updateBugreport.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR>
					<br>


					<mm:present referid="hasmaintainers" inverse="true">
						Delete this bugreport <A HREF="executes/deleteReport.jsp?bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR><BR>
					</mm:present>

					Add maintainer <A HREF="addMaintainer.jsp?bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR><BR>
					Remove maintainer <A HREF="removeMaintainer.jsp?bugreport=<mm:write referid="bugreport" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR><BR>
				<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'">
					Remove yourself as interested <A HREF="executes/removeMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR>
					<mm:import id="userfound" />
				</mm:list>
				<mm:present referid="userfound" inverse="true">
				Add yourself as interested <A HREF="executes/addMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR>
				</mm:present>
				</mm:present>




			<!-- user but not a commitor -->
				<mm:present referid="commitor" inverse="true">
					You (<mm:field name="firstname" /> <mm:field name="lastname" />) have the status of developer this allows you the following actions<br><br>
					<mm:list nodes="$bugreport" path="users,rolerel,bugreports" constraints="users.number=$user and rolerel.role='interested'">
						Remove yourself as interested <A HREF="executes/removeMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR>
						<mm:import id="userfound" />
					</mm:list>
					<mm:present referid="userfound" inverse="true">
						Add yourself as interested <A HREF="executes/addMyselfInterested.jsp?bugreport=<mm:write referid="bugreport" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0"></A><BR>
					</mm:present>
				</mm:present>
				</mm:node>
			</mm:present>
			</font>
		</TD>
</TR>
</table>
