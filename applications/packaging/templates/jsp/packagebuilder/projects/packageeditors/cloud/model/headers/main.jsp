<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
 <TR>
	<!-- NeededBuilders -->
	<mm:compare referid="editor" value="neededbuilders" inverse="true">
		<td align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="neededbuilders" />
		</mm:url>">Builders</a>
		</td>
	</mm:compare>
	<mm:compare referid="editor" value="neededbuilders">
		<th align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="neededbuilders" />
		</mm:url>">Builders</a>
		</th>
	</mm:compare>

	<!-- allowedrelations -->
	<mm:compare referid="editor" value="allowedrelations" inverse="true">
		<td align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="allowedrelations" />
		</mm:url>">Defined Relations</a>
		</td>
	</mm:compare>
	<mm:compare referid="editor" value="allowedrelations">
		<th align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="allowedrelations" />
		</mm:url>">Defined Relations</a>
		</th>
	</mm:compare>


	<!-- NeededRelDefs -->
	<mm:compare referid="editor" value="neededreldefs" inverse="true">
		<td align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="neededreldefs" />
		</mm:url>">Relation Types</a>
		</td>
	</mm:compare>
	<mm:compare referid="editor" value="neededreldefs">
		<th align="middle" width="30%">
		<a href="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename">
		<mm:param name="editor" value="neededreldefs" />
		</mm:url>">Relation Types</a>
		</th>
	</mm:compare>
  </tr>
</table>
