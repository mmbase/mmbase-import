   <link rel="stylesheet" type="text/css" href="<%= (isSubDir? "../" : "" ) %>hoofdsite/themas/main.css"  title="default" />
   <%-- link rel="stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.0.css"  / --%>
   <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.2.css" title="groot" / --%>
   <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.4.css" title="groter" / --%>
   <script type="text/javascript" language="javaScript" src="<%= (isSubDir? "../" : "" ) %>scripts/launchcenter.js"></script>
   <link rel="stylesheet" type="text/css" href="<%= (isSubDir? "../" : "" ) %><%= styleSheet %>" />
	<title><mm:node number="root"><mm:field name="naam"/></mm:node>: <mm:node number="<%= rubriekID %>"><mm:field name="naam" id="rubriek_naam" /></mm:node
	   ><mm:node number="<%= paginaID %>"
	      ><mm:field name="titel_zichtbaar"
	         ><mm:compare value="0" inverse="true"
               ><mm:field name="titel"
	               ><mm:compare referid2="rubriek_naam" inverse="true"
	                  >- <mm:write 
                  /></mm:compare
               ></mm:field
            ></mm:compare
	      ></mm:field
	   ></mm:node></title>
	<% 
	if(iRubriekLayout==NatMMConfig.DEMO_LAYOUT) {
		// google analytics code for demo.mediacompetence.com
		%>
		<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
		</script>
		<script type="text/javascript">
			_uacct = "UA-495341-2";
			urchinTracker();
		</script>
		<% 
	} %>
</head>
<body style="margin:0;" id="nm_body" <mm:present referid="onload_statement">onLoad="<mm:write referid="onload_statement"/>"</mm:present>>