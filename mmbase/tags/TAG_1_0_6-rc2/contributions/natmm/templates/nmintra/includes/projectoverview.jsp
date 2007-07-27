<td colspan="2"><%@include file="../includes/pagetitle.jsp" %></td>
</tr>
<tr>
<td colspan="2" style="padding-left:10px;" class="transperant" valign="top">
<div class="<%= infopageClass %>" id="infopage">
<%@include file="back_print.jsp" %>
<mm:node number="<%= projectId %>">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td width="80%">
				<b><span class="pageheader"><span class="dark"><mm:field name="titel"/></span></span></b>
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Type project&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"><mm:related path="posrel,projecttypes"
								><span class="black"><mm:field name="projecttypes.name"
							/></span></mm:related
						></td>
					</tr>
					<tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Looptijd&nbsp;|&nbsp;</b></span></td>
						<td><span class="black"><%@include file="../includes/dateperiod.jsp" %></span></td>
					</tr>
					<tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Resultaat&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"><span class="black"><mm:field name="goal"/></span></td>
					</tr>
					<mm:related path="readmore,afdelingen" orderby="readmore.pos" directions="UP"
						><mm:first><tr>
							<td style="width:200px;text-align:right;"><span class="black"><b>Betrokken afdelingen&nbsp;|&nbsp;</b></span></td>
							<td style="width:350px;"></mm:first
						><mm:first inverse="true">, </mm:first
							><span class="black"><mm:field name="afdelingen.naam"
						/></span><mm:last></td>
					</tr>
						</mm:last
					></mm:related
					><mm:related path="readmore,medewerkers" orderby="readmore.pos" directions="UP"
						><mm:first><tr>
							<td style="width:200px;text-align:right;"><span class="black"><b>
                     <mm:field name="medewerkers.gender">
                        <mm:compare value="0">Projectleidster</mm:compare>
                        <mm:compare value="1">Projectleider</mm:compare>
                     </mm:field>							
							&nbsp;|&nbsp;</b></span></td>
							<td style="width:350px;"></mm:first
						><mm:first inverse="true">, </mm:first
							><a href="smoelenboek.jsp?p=wieiswie&employee=<mm:field name="medewerkers.number" 
								/>"><span style="text-decoration:underline;" class="dark">
									<mm:field name="medewerkers.titel"/></span></a>
             <mm:last></td>
					   </tr>
						</mm:last
					></mm:related>
					<%-- not used by M. Driessen 24.11.2005
					<tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Beoogde resultaten&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"><mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP"
								><mm:first><ul type="square" class="black"></mm:first>
								<li><mm:field name="paragraaf.title"/></li>
								<mm:last></ul></mm:last
							></mm:related></td>
					</tr>
					<tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Project verloop&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"><span class="black"><mm:field name="description" /></span></td>
					</tr>
					--%>
			   </table>
			</td>
			<td>&nbsp;</td>
		</tr>
	<mm:related path="phaserel,phases" orderby="phaserel.begindate" directions="UP"
		><% String phasesName = ""; 
		%><mm:field name="phases.name" jspvar="dummy" vartype="String" write="false"
			><% phasesName = dummy;
		%></mm:field
		><mm:node element="phaserel"
			><tr style="padding-top:18px;">
			<td width="80%">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td style="width:200px;"></td>
					<td style="width:350px;"><span class="black"><b><%= phasesName.toUpperCase() %></b></span></td>
				</tr>
				<%-- not used by M. Driessen 24.11.2005
				<tr>
					<td style="width:200px;text-align:right;"><span class="black"><b>Looptijd&nbsp;|&nbsp;</b></span></td>
					<td style="width:350px;"><span class="black"><%@include file="../includes/dateperiod.jsp" %></span></td>
				</tr>
				--%>
				<mm:related path="readmore,afdelingen" orderby="readmore.pos" directions="UP"
					><mm:first><tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Betrokken afdelingen&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"></mm:first
					><mm:first inverse="true">, </mm:first
						><span class="black"><mm:field name="afdelingen.name"
					/></span><mm:last></td>
				</tr>
					</mm:last
				></mm:related
				><mm:related path="readmore,medewerkers" orderby="readmore.pos" directions="UP"
					><mm:first><tr>
						<td style="width:200px;text-align:right;"><span class="black"><b>Betrokken medewerkers&nbsp;|&nbsp;</b></span></td>
						<td style="width:350px;"></mm:first
					><mm:first inverse="true">, </mm:first
						><a href="smoelenboek.jsp?p=wieiswie&employee=<mm:field name="medewerkers.number" 
							/>"><span style="text-decoration:underline;" class="dark">
								<mm:field name="medewerkers.firstname"/> <mm:field name="employees.lastname"
						/></span></a><mm:last></td>
				</tr>
					</mm:last
				></mm:related
				><tr>
					<td colspan="2"><span class="black"><mm:field name="description" /></span></td>
				</tr>
				</table>
			</td>
			<td><mm:related path="readmore,contentblocks" orderby="readmore.pos" directions="UP"
					><mm:first><table border="0" cellpadding="0" cellspacing="0" width="100%"></mm:first>
						<tr>
							<td>
					<mm:node element="contentblocks">
                                           <%
                                           String styleClass = "black";
                                           String styleClassDark = "dark";
                                           %>
                                           <%@include file="../includes/contentblockdetails.jsp" %>
                                        </mm:node>
							</td>
						</tr>
					<mm:last></table></mm:last
				></mm:related>
			</td>
		</tr>
		</mm:node
	></mm:related>
	<tr>
	   <td colspan="2" style="padding-top:10px;padding-right:10px;">
	   <%@include file="../includes/contentblocks.jsp" %>
	   </td>
	</tr>
	</table>
</mm:node
></div>
</td>
