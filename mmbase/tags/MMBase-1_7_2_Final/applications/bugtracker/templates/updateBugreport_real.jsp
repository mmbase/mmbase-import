<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="user" />
<mm:import externid="bugreport" />
<mm:import externid="portal" />
<mm:import externid="page" />


<form action="showMessage.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />" method="POST">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">

<mm:node number="$bugreport">
<tr>
	<th>
	Type
	</th>
	<th>
	Priority
	</th>
	<th>
	Status
	<th>
	&nbsp;
	</th>
</tr>
<tr>
		<td>
			<mm:field name="btype">
			<SELECT NAME="newbtype">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>bug
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>wish
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>docbug
				<OPTION VALUE="4" <mm:compare value="4">SELECTED</mm:compare>>docwish
			</SELECT>
			</mm:field>
		</td>
		<td>
			<mm:field name="bpriority">
			<SELECT NAME="newbpriority">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>high
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>medium
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>low
			</SELECT>
			</mm:field>
		</td>

		<td>
			<mm:field name="bstatus">
			<SELECT NAME="newbstatus">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>open
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>accepted
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>rejected
				<OPTION VALUE="4" <mm:compare value="4">SELECTED</mm:compare>>pending
				<OPTION VALUE="5" <mm:compare value="5">SELECTED</mm:compare>>integrated
				<OPTION VALUE="6" <mm:compare value="6">SELECTED</mm:compare>>closed
			</SELECT>
			</mm:field>
		</td>
		<td>
		&nbsp;
		</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
	<th>
	Version
	</th>
	<th>
	Area
	</th>
	<th>
	Expected fixed in
	</th>
	<th>
	Fixed in
	</th>
</tr>
<tr>
		<td>
			<INPUT NAME="newversion" value="<mm:field name="version" />" SIZE="10">
		</td>
		<td>
		<SELECT NAME="newarea">
			<mm:relatednodes type="areas" max="1">
				<OPTION VALUE="<mm:field name="number" />"><mm:field name="substring(name,15,.)" />
			</mm:relatednodes>
			<mm:listnodes type="areas" orderby="name" >
			<OPTION VALUE="<mm:field name="number" />">
			<mm:field name="substring(name,15,.)" />
			</mm:listnodes>
		</SELECT>
		</td>
		<td>
			<INPUT NAME="newefixedin" value="<mm:field name="efixedin" />" SIZE="10">
		</td>
		<td>
			<INPUT NAME="newfixedin" value="<mm:field name="fixedin" />" SIZE="10">
		</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
	<th COLSPAN=5>
	Issue : give the issue in one line 
	</th>
</tr>
<tr>
		<td COLSPAN="5">
			&nbsp;&nbsp;<INPUT SIZE="70" NAME="newissue" value="<mm:field name="issue" escape="text/html/attribute"/>" >
		</td>
</tr>

</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">

<tr>
	<th COLSPAN=4>
	Description : Describe the issue as complete as possible 
	</th>
</tr>
<tr>
		<td COLSPAN="5">
			<TEXTAREA NAME="newdescription" COLS="70" ROWS="15" WRAP><mm:field name="description" escape="text/html"/></TEXTAREA>
		</td>
</tr>

</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">

<tr>
	<th COLSPAN="5">
	Rationale : explains the actions made by the maintainer 
	</th>
</tr>
<tr>
		<td COLSPAN="5">
			<TEXTAREA NAME="newrationale" COLS="70" ROWS="15" WRAP><mm:field name="rationale" /></TEXTAREA>
		</td>
</tr>

</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">

<tr>
	<th COLSPAN=2>
	Submitter
	</th>
	<th COLSPAN=2>
	&nbsp;	
	</th>
</tr>
<tr>
		<td COLSPAN="2">
			<mm:related path="insrel,areas" max="1">
			<INPUT NAME="oldarea" TYPE="hidden" VALUE="<mm:field name="number.areas" />">
			<INPUT NAME="oldarearel" TYPE="hidden" VALUE="<mm:field name="insrel.number" />">
			</mm:related>
</mm:node>
			<mm:node referid="user">
			<INPUT NAME="updater" TYPE="hidden" VALUE="<mm:field name="number" />">
			<INPUT NAME="bugreport" TYPE="hidden" VALUE="<mm:write referid="bugreport" />">
			&nbsp;&nbsp;
			<mm:field name="firstname" />
			<mm:field name="lastname" />
			 ( <mm:field name="email" /> )
			</mm:node>
		</td>
		<td COLSPAN="2">
			<input type="hidden" name="action" value="updatebug" />
			<CENTER><INPUT TYPE="submit" VALUE="SUBMIT UPDATE" />
		</td>
</tr>

</table>
</mm:cloud>
</FORM>
