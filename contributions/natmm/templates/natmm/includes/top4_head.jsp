  <link rel="stylesheet" type="text/css" href="<%= (isSubDir? "../" : "" ) %>hoofdsite/themas/main.css"  title="default" />
  <%-- link rel="stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.0.css"  / --%>
  <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.2.css" title="groot" / --%>
  <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.4.css" title="groter" / --%>
  <script type="text/javascript" language="javascript" src="<%= (isSubDir? "../" : "" ) %>scripts/launchcenter.js"></script>
  <%--<script type="text/javascript" language="javaScript" src="<%= (isSubDir? "../" : "" ) %>scripts/skyscraper_cookie.js"></script>  --%>
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
</head>
<body style="margin:0;" id="nm_body" <%-- onLoad="loadSkyscraper();" --%> <mm:present referid="onload_statement">onLoad="<mm:write referid="onload_statement"/>"</mm:present> <%= (iRubriekLayout==NatMMConfig.DEMO_LAYOUT && path.equals("portal.jsp") ? "onload='StartClock()' onunload='KillClock()'" : "" ) 
   %>>