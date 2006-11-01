<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<%--<di:hasrole role="teacher">--%>
<html>
<head>
<title>ISBO Rapport</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
   <script>
if (top == self) {
    var loc = document.location.href;
    loc = loc.replace(/&amp;/ig,'&').replace(/(education\/).*/,"$1wizards/index.jsp");
    document.location.href = loc;
}
</script>
</head>
<body>
<table class="head">
   <tr class="headsubtitle">
      <td>ISBO Rapport</td>
    </tr>
</table>
<table class="body">
   <tr class="searchcanvas">
      <td>
         <table class="searchcontent">
         	<mm:listnodes type="classes">
				<mm:field name="name" id="classname">
					<mm:field name="number" id="classnum">
 			       		<mm:relatednodes type="educations">
							<tr>
								<td><a href="../report.db?education=<mm:field name="number"/>&class=<mm:write referid="classnum"/>"><mm:write referid="classname"/> (<mm:field
									name="name" /></a>)
								</td>
							</tr>
						</mm:relatednodes>
					</mm:field>
				</mm:field>
			</mm:listnodes>
	</table></td></tr></table>
</body>
</html>

<%--</di:hasrole>--%>
</mm:cloud>
</mm:content>
