<table border="0" width="650">
<TR>
		<TD BGCOLOR="#00425A" COLSPAN="6">
			  <br>
			  Statement from the maintainer <br><br>
			<font color="#ffffff">
			<mm:field name="rationale">
			<mm:compare value="" inverse="true">
				<mm:field name="html(rationale)" />
			</mm:compare>
			<mm:compare value="">
				no maintainer statement yet
			</mm:compare>
			</mm:field>
			</font>
		</TD>
</TR>
<TR>
		<TD BGCOLOR="#00425A" COLSPAN="6">
			  <br>
			  User comments <br><br>
			<font color="#ffffff">
				<table border="1">
				<mm:related path="rolerel,comments">
				<tr>
				<td valign="top" width="200">
				type : <font color="#ffffff"><mm:field name="rolerel.role" /></font><br /><br />
				<mm:node element="comments">
				name : <font color="#ffffff"><mm:relatednodes type="users" ><mm:field name="firstname" /> <mm:field name="lastname" /> </mm:relatednodes></font>
				</mm:node>
				</td>
				<td valign="top" width="430">
				<mm:first><mm:import id="commentsfound" /></mm:first>
				title <font color="#ffffff"><mm:field name="comments.title" /></font><br>
				<br>
				<mm:field name="comments.body" />

				</mm:related>
				</td>
				</tr>
				</table>
				<mm:present referid="commentsfound" inverse="true">
					No user comments yet
				</mm:present>
				<br /><br /><br /><br />
				Add comment <a href="addComment.jsp?bugreport=<mm:write referid="bugreport" />&commenttype=regular"><img src="images/arrow.gif"></a><br />
			</font>
		</TD>
</TR>
</table>
