<table class="body">
	<tr><td class="listcanvas" colspan="2">
	<div title="" class="subhead"><nobr>
		<mm:listnodes type="locales" constraints="[languagecode] = '$locallanguage' AND [countrycode] = '$localcountry'">
			<mm:field name="name"/> &nbsp; ( <mm:field name="languagecode"/> &nbsp; <mm:field name="countrycode"/> )
		</mm:listnodes>&nbsp
	</nobr></div>

<mm:node number="$objectnumber">
	<table class="itemlist">
	<tbody class="fieldset">
	<% for (int a = 0;a < fieldList.size(); a++) {
		String fieldname = (String)fieldList.get(a); 
		String guiname = "";%>
		
		<mm:fieldlist fields="<%= fieldname %>">
		<span prompt="<mm:fieldinfo type="guiname"/>" class="valid">
			<mm:fieldinfo type="guiname" jspvar="gui" vartype="String">
				<% guiname = gui;
				guifieldList.add(gui); %>
			</mm:fieldinfo>
		</span>&nbsp;
		</mm:fieldlist>

			<tr>
			<td class="fieldprompt">
				<span prompt="<%= guiname %>" class="valid">
				<%= guiname %>
				</span>&nbsp;
			</td>
			<td colspan="1" class="field">
			    <mm:field name="<%= fieldname %>" />&nbsp;
			</td>
			</tr>
	<% } %>
	</tbody>
	</table>
</mm:node>

<mm:list nodes="$portal" path="portals,activerel,locales" orderby="locales.name" directions="up">
	<mm:node element="locales">
		<mm:import id="currentlocale" reset="true"><mm:field name="languagecode"/>_<mm:field name="countrycode"/></mm:import>

		<mm:compare referid="currentlocale" referid2="locallocale" inverse="true">
			<mm:field name="languagecode" jspvar="languagecode" vartype="String">
			<mm:field name="countrycode" jspvar="countrycode" vartype="String">
			
				<tr><td class="listcanvas" colspan="2">
					<div title="" class="subhead"><nobr>
						<mm:field name="name"/> &nbsp; 
						( <mm:field name="languagecode"/> 
						&nbsp; <mm:field name="countrycode"/> )
					</nobr></div>

				<% for (int a = 0;a < fieldList.size(); a++) {
					String fieldname = (String)fieldList.get(a); 
					String guiname = (String) guifieldList.get(a);
					String fieldvalue = translationModule.lookupTranslation(objectnumber.intValue(), fieldname, languagecode,countrycode);
					if (fieldvalue == null ) fieldvalue = "";
				%>
					<table class="itemlist">
					<tbody class="fieldset">
					<tr>
					<td class="fieldprompt">
						<span prompt="<%= guiname %>" class="valid"> <%= guiname %> </span>&nbsp;
					</td>
					<mm:import id="inputname">field/<mm:field name="languagecode"/>/<mm:field name="countrycode"/>/<%= fieldname %></mm:import>
					<td colspan="2" class="field">
						<span>
						<% String inputtype = (String) inputList.get(a);
						String rows = "10";
						if (inputtype.startsWith("html")) {
							if (inputtype.length() > 4) {
								rows = inputtype.substring(4);
							}
						%>
							<textarea
				                name="<mm:write referid="inputname"/>"
			    	            ftype="html"
            				    class="input" wrap="soft"
			            	    cols="80" rows="<%= rows %>"><%= fieldvalue %></textarea>
						<% } else { 
							if (inputtype.length() > 4) {
								rows = inputtype.substring(4);
							%>
							<textarea
				                name="<mm:write referid="inputname"/>"
            				    class="input" wrap="soft"
			            	    cols="80" rows="<%= rows %>"><%= fieldvalue %></textarea>
							<% }
							else {
							%>
							<input type="text" value="<%= fieldvalue %>" size="80" name="<mm:write referid="inputname"/>">
							<% }
						} %>

						</span> &nbsp;
					</td>
					<mm:remove referid="inputname"/>
					</tr>
					</tbody>
					</table>
				<% } %>
				</td></tr>
				
			</mm:field>
			</mm:field>
		</mm:compare>
	</mm:node>
</mm:list>

<tr><td align="center" colspan="2">

<hr noshade="true" size="1" color="#005A4A" />
<p>
<%  String close = request.getParameter("close"); 
    if (close == null) close = "false";
%>
<a title="Cancel this task, changes will NOT be saved." 
	class="bottombutton" 
	id="bottombutton-cancel" 
	href="javascript:doCancel(<%=close%>);">cancel</a>
            -
<a titlenosave="The changes cannot be saved, since some data is not filled in correctly." 
	titlesave="Store all changes." 
	unselectable="on" 
	id="bottombutton-save" 
	href="javascript:doSave(<%=close%>);" 
	class="bottombutton" 
	title="Store all changes." 
	otherforms="valid">save</a>
</p>
<hr noshade="true" size="1" color="#005A4A" />

</td></tr>
</table>
