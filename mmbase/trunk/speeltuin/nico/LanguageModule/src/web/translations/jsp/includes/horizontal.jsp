<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%-- Div which always moves to the left of the frame --%>
<div id="divStayLeft" style="position:absolute;background-color:#FFFFFF;">

<table>
<tr><td class="listcanvas">
	<div title="" class="subhead"><nobr>
		<mm:listnodes type="locales" constraints="[languagecode] = '$locallanguage' AND [countrycode] = '$localcountry'">
			<mm:field name="name"/> &nbsp; ( <mm:field name="languagecode"/> &nbsp; <mm:field name="countrycode"/> )
		</mm:listnodes>&nbsp
	</nobr></div>

<mm:node number="$objectnumber">
	<table class="itemlist">
	<% for (int a = 0;a < fieldList.size(); a++) {
		String fieldname = (String)fieldList.get(a); 
		String guiname = "";%>
			<mm:fieldlist fields="<%= fieldname %>">
				<mm:fieldinfo type="guiname" jspvar="gui" vartype="String">
				<% guiname = gui; guifieldList.add(gui); %>
				</mm:fieldinfo>
			</mm:fieldlist>

		<tr><td class="fieldprompt" >
			<span prompt="<%= guiname %>" class="valid">
				<%= guiname %>
			</span>
		</td></tr>
		<tr><td class="field">
		<div style="width:<%= clientwidth - 25 %>;border-style:solid;border-color:#000000;border-width:1;">
		    <mm:field name="<%= fieldname %>" />&nbsp;
		<div>
		</td></tr>
	<% } %>
	</table>
</mm:node>

</td></tr>
<tr><td><img src="media/x.gif" height="1" width="<%= clientwidth %>"></td></tr>
<tr><td align="center" class="listcanvas">

<hr noshade="true" size="1" color="#005A4A" />
<p>
<%  String close = request.getParameter("close"); 
    if (close == null) close = "false";
%>
<a title="Cancel this task, changes will NOT be saved." 
	class="bottombutton" 
	id="bottombutton-cancel" 
	href="javascript:doCancel(<%= close %>);">cancel</a>
            -
<a titlenosave="The changes cannot be saved, since some data is not filled in correctly." 
	titlesave="Store all changes." 
	unselectable="on" 
	id="bottombutton-save" 
	href="javascript:doSave(<%= close %>);" 
	class="bottombutton" 
	title="Store all changes." 
	otherforms="valid">save</a>
</p>
<hr noshade="true" size="1" color="#005A4A" />

</td></tr>
</table>
</div>
<script src="../javascript/float.js" language="javascript"><!--help IE--></script>



<table>
<tr>
<td><img src="media/x.gif" height="1" width="<%= clientwidth %>"></td>

<mm:list nodes="$portal" path="portals,activerel,locales" orderby="locales.name" directions="up">
	<mm:node element="locales">
		<mm:import id="currentlocale" reset="true"><mm:field name="languagecode"/>_<mm:field name="countrycode"/></mm:import>

		<mm:compare referid="currentlocale" referid2="locallocale" inverse="true">
			<mm:field name="languagecode" jspvar="languagecode" vartype="String">
			<mm:field name="countrycode" jspvar="countrycode" vartype="String">

			<td>
			<table>
				<tr>
				<td class="listcanvas">
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
					<tr>
					<td class="fieldprompt">
						<span prompt="<%= guiname %>" class="valid"> <%= guiname %> </span>
					</td>
					</tr>
					<mm:import id="inputname">field/<mm:field name="languagecode"/>/<mm:field name="countrycode"/>/<%= fieldname %></mm:import>
					<tr>
					<td class="field">
						<% String inputtype = (String) inputList.get(a);
						String rows = "10";
						if (inputtype.startsWith("html")) {
							if (inputtype.length() > 4) {
								rows = inputtype.substring(4);
							}
						%>
							<textarea style="width:<%= clientwidth %>;"
				                name="<mm:write referid="inputname"/>"
			    	            ftype="html"
            				    class="input" wrap="soft"
			            	    rows="<%= rows %>"><%= fieldvalue %></textarea>
						<% } else { 
							if (inputtype.length() > 4) {
								rows = inputtype.substring(4);
							%>
							<textarea style="width:<%= clientwidth %>;"
				                name="<mm:write referid="inputname"/>"
            				    class="input" wrap="soft"
			            	    rows="<%= rows %>"><%= fieldvalue %></textarea>
							<% }
							else {
							%>
							<input type="text" style="width:<%= clientwidth %>;" 
							       value="<%= fieldvalue %>" name="<mm:write referid="inputname"/>">
							<% }
						} %>
					</td>
					</tr>
					<mm:remove referid="inputname"/>
					</table>
				<% } %>
				</td></tr>
			</table>	
			</td>
			</mm:field>
			</mm:field>
		</mm:compare>
	</mm:node>
</mm:list>

</tr>
</table>