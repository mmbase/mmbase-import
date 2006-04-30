<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="includes/settings.jsp" %>
<mm:import externid="username" from="parameters" />
<mm:cloud method="http" logon="$username" jspvar="cloud">

<mm:import externid="website"></mm:import>

<mm:compare referid="website" value="" inverse="true">
  <mm:transaction id="post_site" name="trans_addsite" commitonclose="true"><mm:createnode type="websites" id="this_site"
			><mm:setfield name="name"><mm:write referid="website" /></mm:setfield
			><mm:setfield name="menuname">Natuurherstelproject</mm:setfield
		></mm:createnode
		><mm:node number="home" id="home" 
		/><mm:createrelation role="posrel" source="home" destination="this_site" 
		/><%-- 

		create the page: homepage
		--%><mm:createnode type="pages" id="homepage"
			><mm:setfield name="title">Home</mm:setfield
			><mm:setfield name="menuname"><mm:write referid="website" /></mm:setfield
		></mm:createnode
		><mm:createrelation role="posrel" source="this_site" destination="homepage" 
			><mm:setfield name="pos">1</mm:setfield
		></mm:createrelation
		><mm:node number="homepage_template" id="homepage_template" 
		/><mm:createrelation role="related" source="homepage" destination="homepage_template" 
		/><%-- 

		create the page: nieuws
		--%><mm:createnode type="pages" id="newspage"
			><mm:setfield name="title">Nieuws</mm:setfield
			><mm:setfield name="menuname"><mm:write referid="website" /></mm:setfield
		></mm:createnode
		><mm:createrelation role="posrel" source="homepage" destination="newspage" 
			><mm:setfield name="pos">1</mm:setfield
		></mm:createrelation
		><mm:node number="news_template" id="news_template" 
		/><mm:createrelation role="related" source="newspage" destination="news_template" 
		/><%-- 

		create the page: article
		--%><mm:createnode type="pages" id="articlepage"
			><mm:setfield name="title">Info</mm:setfield
			><mm:setfield name="menuname"><mm:write referid="website" /></mm:setfield
		></mm:createnode
		><mm:createrelation role="posrel" source="homepage" destination="articlepage" 
			><mm:setfield name="pos">2</mm:setfield
		></mm:createrelation
		><mm:node number="article_template" id="article_template" 
		/><mm:createrelation role="related" source="articlepage" destination="article_template" 
		/><%-- 

		create the page: thumbs
		--%><mm:createnode type="pages" id="thumbspage"
			><mm:setfield name="title">Fotoalbum</mm:setfield
			><mm:setfield name="menuname"><mm:write referid="website" /></mm:setfield
		></mm:createnode
		><mm:createrelation role="posrel" source="homepage" destination="thumbspage" 
			><mm:setfield name="pos">3</mm:setfield
		></mm:createrelation
		><mm:node number="thumbs_template" id="thumbs_template" 
		/><mm:createrelation role="related" source="thumbspage" destination="thumbs_template" 
	/></mm:transaction

><mm:list path="websites" orderby="websites.number" directions="down" max="1"
	><mm:field name="websites.number" jspvar="websites_number" vartype="String" write="false"
	><mm:field name="websites.name" jspvar="websites_name" vartype="String" write="false"
	><mm:createnode type="jumpers"
		><mm:setfield name="name"><%= websites_name %></mm:setfield
		><mm:setfield name="url">/index.jsp?w=<%= websites_number %></mm:setfield
	></mm:createnode
	></mm:field
	></mm:field
></mm:list
></mm:compare>

<html>
<head>
<title>Create Website</title>
<link rel="stylesheet" type="text/css" href="css/editors.css">
<script language="JavaScript">
var cancelClick = false;
function doDelete(prompt) {
				var conf;
				if (prompt && prompt!="") {
					conf = confirm(prompt);
				} else conf=true;
				cancelClick=true;
				return conf;
			}
</script>
<script language="JavaScript" src="scripts/launchcenter.js"></script>
</head>
<body topmargin="2" rightmargin="3" leftmargin="3">
<form method="POST" action="websites.jsp">
<table border="0" cellspacing="0" cellpadding="0" align="center">
<tr>
	<td colspan="2" class="prikbord_text">CRE&#203;ER EEN NIEUWE WEBSITE</td>
</tr>
<tr>
	<td>Naam van de website</td>
	<td>Cre&#235;er een nieuwe website</td>
</tr>
<tr>
	<td class="black"><img src="media/spacer.gif" width="1" height="1"></td>
	<td class="black"><img src="media/spacer.gif" width="1" height="1"></td>
</tr>
<tr>
	<td><input type="text" name="website"></td>
	<td><img src="media/spacer.gif" width="1" height="3"><br><div align="center"><input type="image"
			onclick="return doDelete('Are you sure you want to create a new website?');" 
			onmousedown="cancelClick=true;"
			title="Create new website" height="15" width="15" border="0" src="/mmapps/editwizard/media/new.gif"></a></div></td>
</tr>
<tr>
	<td><img src="media/spacer.gif" width="1" height="10"></td>
	<td><img src="media/spacer.gif" width="1" height="1"></td>
</tr>
<mm:list path="websites" orderby="websites.number" directions="down">
<mm:first>
<tr>
	<td colspan="2" class="prikbord_text">REEDS BESTAANDE WEBSITES</td>
</tr>
<tr>
	<td>Preview van de website</td>
	<td>Link naar de website</td>
</tr>
<tr>
	<td class="black"><img src="media/spacer.gif" width="1" height="1"></td>
	<td class="black"><img src="media/spacer.gif" width="1" height="1"></td>
</tr>
</mm:first>
<mm:field name="websites.number" jspvar="websites_number" vartype="String" write="false">
<tr>
	<td><a href="#" target="wizard" onClick="javascript:launchCenter('<%= websiteRoot %>/dev/index.jsp?w=<%= websites_number %>', 'preview', 600, 800);"><mm:field name="websites.name" /></a></td>
	<td><mm:list path="jumpers" constraints="<%= "jumpers.url LIKE '%w=" + websites_number + "%'" %>" max="1"
		><a target="_blank" href="/<mm:field name="jumpers.name"
			/>"><%= HttpUtils.getRequestURL(request).substring(0,HttpUtils.getRequestURL(request).toString().indexOf("editors"))
			%><mm:field name="jumpers.name"/></a></mm:list></td>
</tr>
</mm:field>
</mm:list>
</table>
</form>
</body>
</html>
</mm:cloud>
