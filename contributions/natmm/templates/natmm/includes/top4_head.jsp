   <link rel="stylesheet" type="text/css" href="<%= (!subDir.equals("")? "../" : "" ) %>hoofdsite/themas/main.css"  title="default" />
   <%-- link rel="stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.0.css"  / --%>
   <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.2.css" title="groot" / --%>
   <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.4.css" title="groter" / --%>
   <script type="text/javascript" language="javaScript" src="<%= (!subDir.equals("")? "../" : "" ) %>scripts/launchcenter.js"></script>
   <link rel="stylesheet" type="text/css" href="<%= (!subDir.equals("")? "../" : "" ) %><%= styleSheet %>" />
	<title>Natuurmonumenten: <mm:node number="<%= rubriekID %>"><mm:field name="naam" id="rubriek_naam" /></mm:node
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
</head>
<body style="margin:0;" id="nm_body" <mm:present referid="onload_statement">onLoad="<mm:write referid="onload_statement"/>"</mm:present>>