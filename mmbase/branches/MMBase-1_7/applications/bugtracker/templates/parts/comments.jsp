<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
		<th>
			  Statement from the maintainer 
		</th>
		<td>
			<mm:field name="rationale">
			<mm:compare value="" inverse="true">
				<mm:field name="html(rationale)" />
			</mm:compare>
			<mm:compare value="">
				no maintainer statement yet
			</mm:compare>
			</mm:field>
		</td>
</tr>
<tr>
		<th>
			  User comments 
		</th>
		<td>
				<table border="0">
				<mm:related path="rolerel,comments">
				<tr>
				<td valign="top" width="200">
				<mm:node element="comments">
					<mm:relatednodes type="users" >
					  <mm:field name="firstname" /> <mm:field name="lastname" />
					</mm:relatednodes>
				</mm:node>
				</td>
				<td valign="top" width="430">
				<mm:first><mm:import id="commentsfound" /></mm:first>
				title <mm:field name="comments.title" />
				<mm:field name="comments.body" />

				</mm:related>
				</td>
				</tr>
				</table>
				<mm:present referid="commentsfound" inverse="true">
					No user comments yet
				</mm:present>
				Add comment <a href="addComment.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />&commenttype=regular"><img src="images/arrow-right.gif"></a><br />
		</td>
</tr>
</table>
