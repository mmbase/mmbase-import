<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
String scriptStr = "";
int p_number = 1;
%><table width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td width="15%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<td width="65%">
	<img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
	
	<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel,posrel,paragraaf"
				constraints="contentrel.pos=13" orderby="posrel.pos" directions="UP"
	><mm:first><mm:field name="artikel.intro" /></mm:first><br>
	<mm:field name="posrel.pos" jspvar="posrel2_pos" vartype="String" write="false"><% 
	if(posrel2_pos.equals("1")) {			// **************** total costs ****************************
		offsetID = (String) session.getAttribute("totalcosts"); 
		if(offsetID !=null) { 
			%><img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
			<table width="100%" cellspacing="0" cellpadding="0">
			<tr><td class="titlebar" colspan="2"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
			<tr><td class="carteven"  style="text-align:left;"><span style="font-size:12px;font-weight:bold;">
					<mm:field name="paragraaf.titel" /></span></td>
				<td class="carteven" style="font-size:12px;font-weight:bold;padding-right:5px;text-align:right;"><%
				if(!offsetID.equals("-1")) { 
					%>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(offsetID))/100) %><%
				} else {
					%>nog onbekend<% 
				} %></td></tr>
			<tr><td class="titlebar" colspan="2"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
			</table><%
		} 
	} else if(posrel2_pos.equals("8")) {	// **************** obligatory fields text ******************
		%><table width="100%" cellspacing="0" cellpadding="0"><tr>
			<td class="creditline" style="text-align:left;"><mm:field name="paragraaf.omschrijving" /></td>
		</tr></table>
		<img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
		<%
	} else if(posrel2_pos.equals("9")) {	// **************** send order ******************
			%><table width="180" cellspacing="0" cellpadding="0" align="right">
			<tr>
			<td class="titlebar" style="vertical-align:middle;padding-left:4px;padding-right:2px;" width="100%">
				<nowrap><a href="javascript:changeIt('<mm:url page="<%= pageUrl + "&p=bestel&t=send" %>" />');" class="white"><mm:field name="paragraaf.omschrijving" /></a></td>
			<td class="titlebar" style="padding:2px;" width="100%">
				<a href="javascript:changeIt('<mm:url page="<%= pageUrl + "&p=bestel&t=send" %>" />');"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
			</tr>
		</table><%
	} else {								// **************** input fields ****************************
		String answerValue = "";
		if(posrel2_pos.equals("2")) { 
			answerValue = (String)  session.getAttribute("q0");
			if(answerValue==null) answerValue = "";
			%><table width="100%" cellspacing="0" cellpadding="0">
			<form name="formulier" method="post" action="javascript:changeIt('<mm:url page="<%= pageUrl + "&p=bestel&t=send&pst=" %>" />');"><tr>
				<td class="titlebar" style="width:40%;vertical-align:middle;background-color:#5D5D5D;">
				<input type="radio" name="gender" value="m" <% if(answerValue.equals("m")) { %>CHECKED<% } %>>Dhr
				<input type="radio" name="gender" value="f" <% if(answerValue.equals("f")) { %>CHECKED<% } %>>Mevr</td>
				<td style="width:60%;height:17px;"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
			</tr><%
			
		} 
		answerValue = (String) session.getAttribute("q" + p_number);
		if(answerValue==null) answerValue = "";
		%><tr><td colspan="2"><img src="media/spacer.gif" height="7" width="1" border="0" alt=""></td></tr>
		<tr>
		<td class="titlebar" style="width:40%;vertical-align:middle;padding-left:4px;background-color:#5D5D5D;"><mm:field name="paragraaf.omschrijving" 
			/><% if(!posrel2_pos.equals("6")) { %>*<% } %></td>
		<td class="titlebar" style="width:60%;vertical-align:middle;text-align:right;padding-right:1px;background-color:#5D5D5D;">
			<input type="text" name="q<%= p_number %>" style="width:100%;height:15px;" value="<%= answerValue %>"></td>
		</tr><%
		
		scriptStr += "var answer = escape(document.formulier.elements[\"q" + p_number + "\"].value);\n"
					+ "href += \"$q" + p_number + "=\" + answer; \n";
		
		if(posrel2_pos.equals("7")) { 
			%></form></table><%
		}
		p_number++;
	}
	%></mm:field
	></mm:list>
	</td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180"><img src="media/spacer.gif" height="1" width="180" border="0" alt=""><br>
	<table width="100%" cellspacing="0" cellpadding="0">
	<tr><td style="padding:4px;padding-top:14px;">
		<mm:import id="isfirst"
		/><bean:message bundle="LEOCMS" key="shoppingcartform.back_to_form" 
			/><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel"
			constraints="contentrel.pos=4"
				><mm:node element="artikel"
				><%@include file="../includes/relatedlinks.jsp" 
			%></mm:node
		></mm:list>
	</td></tr>
	</table>
	</td>
</tr>
</table>
<%-- ********************* create the javascript for posting the values *******************
--%>
<script language="javascript" type="text/javascript">
<%= "<!--" %>
function changeIt(url) {
var href = "&pst=";
var answer = document.formulier.gender;
for (var i=0; i < answer.length; i++){
	if (answer[i].checked) {
		var rad_val = answer[i].value;
		if(rad_val != '') {	href += "$q0=" + rad_val; }
	}
}
<%= scriptStr %>
if(url!=null) {
	document.location =  url + href; 
	return false;
} else {
	return href;
}
}
<%= "//-->" %>
</script>
</mm:cloud>
