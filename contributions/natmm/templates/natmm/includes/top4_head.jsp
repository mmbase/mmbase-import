  <link rel="stylesheet" type="text/css" href="<%= (isSubDir? "../" : "" ) %>hoofdsite/themas/main.css"  title="default" />
  <%-- link rel="stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.0.css"  / --%>
  <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.2.css" title="groot" / --%>
  <%-- link rel="alternate stylesheet" type="text/css" href="hoofdsite/themas/fontsize1.4.css" title="groter" / --%>
  <script type="text/javascript" language="javaScript" src="<%= (isSubDir? "../" : "" ) %>scripts/launchcenter.js"></script>
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
   <% 
   if(iRubriekLayout==NatMMConfig.DEMO_LAYOUT) {
      // google analytics code for demo.mediacompetence.com
      %>
      <style>
      td {
         color: black;
         font-size: 70%; 
      }
      A:link {
         color: black;
      }
      A:visited{
         color: black;
      }
      A:hover{
         color: black;
      }
      </style>
      <script type="text/javascript">
      <!--
      // please keep these lines on when you copy the source
      // made by: Nicolas - http://www.javascript-page.com
      
      var clockID = 0;
      
      function UpdateClock() {
         if(clockID) {
            clearTimeout(clockID);
            clockID  = 0;
         }
      
       var tDate = new Date();
       var minutes = tDate.getMinutes();
       if(minutes<10) { minutes = "0" + minutes; }
       var seconds = tDate.getSeconds();
       if(seconds<10) { seconds = "0" + seconds; }
         
       document.theClock.theTime.value = ""
                                         + tDate.getDate() + "-"
                                         + (tDate.getMonth()+1) + "-"
                                         + tDate.getFullYear() + "  "
                                         + tDate.getHours() + ":"
                                         + minutes + ":"
                                         + seconds;
         
         clockID = setTimeout("UpdateClock()", 1000);
      }
      function StartClock() {
         clockID = setTimeout("UpdateClock()", 500);
      }
      
      function KillClock() {
         if(clockID) {
            clearTimeout(clockID);
            clockID  = 0;
         }
      }
      
      //-->
      
      </script>
      <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
      </script>
      <script type="text/javascript">
         _uacct = "UA-495341-2";
         urchinTracker();
      </script>
      <% 
   } %>
</head>
<body style="margin:0;" id="nm_body" <%-- onLoad="loadSkyscraper();" --%> <mm:present referid="onload_statement">onLoad="<mm:write referid="onload_statement"/>"</mm:present> <%= (iRubriekLayout==NatMMConfig.DEMO_LAYOUT && path.equals("portal.jsp") ? "onload='StartClock()' onunload='KillClock()'" : "" ) 
   %>>