<%@include file="/taglibs.jsp" %>
<mm:content type="text/html" escaper="none">
<mm:cloud jspvar="cloud">
   <%@include file="includes/templateheader.jsp" %>
   <%@include file="includes/cacheparams.jsp" %>
   
   <%@include file="includes/vastgoed/override_templateparams.jsp" %>
   
   <%
   String baseURL = request.getContextPath();
   %>
   
   <% (new SimpleStats()).pageCounter(cloud,application,paginaID,request); %>
   <%@include file="includes/getresponse.jsp" %>
   <html>
   <head>
      <base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
      <link rel="stylesheet" type="text/css" href="css/main.css">
      <link rel="stylesheet" type="text/css" href="<%= styleSheet %>" />
      <link rel="stylesheet" type="text/css" href="css/vastgoed.css" />
      <title><% 
         if(isPreview) { %>PREVIEW: <% } 
         %><mm:node number="<%= subsiteID %>" notfound="skipbody"><mm:field name="naam" /></mm:node
         > <mm:node number="<%= paginaID %>" notfound="skipbody">
            <%--<mm:field name="titel" />--%>
      </mm:node></title>
      <meta http-equiv="imagetoolbar" content="no">
      <script language="javascript" src="scripts/launchcenter.js"></script>
      <script language="javascript" src="scripts/cookies.js"></script>
      <script language="javaScript" src="scripts/screensize.js"></script>
      <script type="text/javascript">
      function resizeBlocks() {	
      var MZ=(document.getElementById?true:false); 
      var IE=(document.all?true:false);
      var wHeight = 0;
      var infoPageDiff = 87;
      var navListDiff = 62;
      <mm:notpresent referid="showprogramselect">
        var smoelenBoekDiff = 378;
      </mm:notpresent>
      <mm:present referid="showprogramselect">
        var smoelenBoekDiff = 414;
      </mm:present>
      var linkListDiff = 511;
      var rightColumnDiff = 109;
      var minHeight = 300;
      if(IE){ 
        wHeight = document.body.clientHeight;
        if(wHeight>minHeight) {
          if(document.all['infopage']!=null) { 
            document.all['infopage'].style.height = (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.all['navlist']!=null) { 
            document.all['navlist'].style.height = (wHeight>navListDiff ? wHeight - navListDiff : 0); }
          if(document.all['smoelenboeklist']!=null) {
            document.all['smoelenboeklist'].style.height = (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); }
          if(document.all['rightcolumn']!=null) {
            document.all['rightcolumn'].style.height = (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); }
          if(document.all['linklist']!=null) {
            document.all['linklist'].style.height = (wHeight>linkListDiff ? wHeight - linkListDiff : 0); }
        }
      } else if(MZ){
        wHeight = window.innerHeight;
        if(wHeight>minHeight) {
          if(document.getElementById('infopage')!=null) {
            document.getElementById('infopage').style.height= (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.getElementById('navlist')!=null) {
            document.getElementById('navlist').style.height= (wHeight>navListDiff ? wHeight - navListDiff : 0); } 
          if(document.getElementById('smoelenboeklist')!=null) {
            document.getElementById('smoelenboeklist').style.height= (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); } 
          if(document.getElementById('rightcolumn')!=null) {
            document.getElementById('rightcolumn').style.height= (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); } 
          if(document.getElementById('linklist')!=null) {
            document.getElementById('linklist').style.height= (wHeight>linkListDiff ? wHeight - linkListDiff : 0); } 
        }
      }
      return false;
      }
      </script>
      <% 
      if(printPage) { 
      %>
      <style>
         body {
         overflow: auto;
         background-color: #FFFFFF
         }
      </style>
      <%
      } %>      
      
      <title>bestelformulier plotopdrachten</title>
      
      <bean:define id="natGebMap" property="natGebMap" name="KaartenForm" type="java.util.Map" />
      <bean:define id="gebiedMap" property="gebiedMap" name="KaartenForm" type="java.util.Map" />
      <bean:define id="selKaartMap" property="selKaartMap" name="KaartenForm" type="java.util.Map" />
      <bean:define id="rad_Schaal" property="rad_Schaal" name="KaartenForm" type="String" />
      
      <script type="text/javascript">
<!--
<%@include file="includes/vastgoed/giveinfo.js" %>
-->
      </script>
      <script type="text/javascript">
<!--
arr_NatGeb = new Array
(
 <%
      Set firstKeySet = natGebMap.keySet();
      Iterator firstIterator = firstKeySet.iterator();
      while (firstIterator.hasNext()) {
         out.println("new Array (");
         String firstKey = (String) firstIterator.next();
         Map valuesMap = (TreeMap) natGebMap.get(firstKey);
         Set valuesKeySet = valuesMap.keySet();
         Iterator valuesIterator = valuesKeySet.iterator();
         int index = 0;
         while (valuesIterator.hasNext()) {
            index++;
            String valuesKey = (String) valuesIterator.next();
            Boolean isSelected = (Boolean) valuesMap.get(valuesKey);
            
            out.print("new Array(\"" + valuesKey + "\", " + index + ", " + isSelected + ")");
            if (valuesIterator.hasNext()) {
               out.println(",");
            }
         }
         out.print(" ) ");
         if (firstIterator.hasNext()) {
            out.println(",");
         }
      }   
%>
);

arr_Areaal = new Array
(
 <%
      firstKeySet = gebiedMap.keySet();
      firstIterator = firstKeySet.iterator();
      while (firstIterator.hasNext()) {
         out.println("new Array (");
         String firstKey = (String) firstIterator.next();
         Map valuesMap = (TreeMap) gebiedMap.get(firstKey);
         Set valuesKeySet = valuesMap.keySet();
         Iterator valuesIterator = valuesKeySet.iterator();
         int index = 0;
         while (valuesIterator.hasNext()) {
            index++;
            String valuesKey = (String) valuesIterator.next();
            Boolean isSelected = (Boolean) valuesMap.get(valuesKey);
            
            out.print("new Array(\"" + valuesKey + "\", " + index + ", " + isSelected + ")");
            if (valuesIterator.hasNext()) {
               out.println(",");
            }
         }
         out.print(" ) ");
         if (firstIterator.hasNext()) {
            out.println(",");
         }
      }   
%>
);

-->
      </script>
      
      
      <script type="text/javascript">
function jsc_ClearUnselectedOptions()
{
	var selectCtrlArray = new Array(document.KaartenForm.sel_NatGeb, document.KaartenForm.sel_Areaal);
	var selectCtrl;
	var i;
	// for top two kart options, clear selection if the kart option is not applicable. for more usability
	for (iCtrl = 0; iCtrl < selectCtrlArray.length; iCtrl++) {
		selectCtrl = selectCtrlArray[iCtrl];
		if (!document.KaartenForm.rad_Gebied[iCtrl].checked) {
			for (i = 0; i < selectCtrl.options.length; i++)
			{
				selectCtrl.options[i].selected = false; 
			}
		}
	}
}

function jsc_VulSelectUitArray(selectCtrl, itemArray)
{
	var i, j;
	// leeg de tweede lijst
	for (i = selectCtrl.options.length; i >= 0; i--)
		{
			selectCtrl.options[i] = null; 
		}
		
	if (itemArray != null)
	{
		j = 0;
	}
	else
	{	
		j = 0;}
		if (itemArray != null)
		{
		// nieuwe items toevoegen
		for (i = 0; i < itemArray.length; i++)
		{
			selectCtrl.options[j] = new Option(itemArray[i][0], itemArray[i][0], true, itemArray[i][2]);
			if (itemArray[i][0] != null)
				{
					selectCtrl.options[j].value = itemArray[i][0]; 
				}
			j++;
		}
	// eerste item selecteren voor tweede lijst, is nu uitgeschakeld
	//selectCtrl.options[0].selected = true;
	}
}

function jsc_GeefInfo(id_DIV)
//DIV zichtbaar -> maak onzichtbaar, DIV onzichtbaar -> maak zichtbaar
{
	if (id_DIV.style.display=="none"){id_DIV.style.display=""}
	else{id_DIV.style.display="none"}
}

function small_window(NaamPagina) {
  var newWindow;
  wwidth = 984;
  wheight = 728;
  wleft = (screen.width - wwidth) / 2;
  wtop = (screen.height - wheight) / 2;

  if (wleft < 0) {
    wleft = 0;
  }
  if (wtop < 0) {
    wtop = 0;
  }
  
var props = 'scrollBars=yes,resizable=no,toolbar=no,status=0,minimize=no,statusbar=0,menubar=no,directories=no,width='+wwidth+',height='+wheight+',top='+wtop+',left='+wleft;
<%
   StringBuffer pageUrl = javax.servlet.http.HttpUtils.getRequestURL(request);
%>
var fullLink = '<%=pageUrl.substring(0, pageUrl.lastIndexOf("/") + 1)%>';
for (var i = 0; i < document.KaartenForm.sel_Kaart.length; i++) {
	if (document.KaartenForm.sel_Kaart[i].selected) {
		var kartNode = document.KaartenForm.sel_Kaart[i].value;
		fullLink += NaamPagina + "?node=" + kartNode;
		var windowName = kartNode;
		newWindow = window.open(fullLink, windowName, props);
		newWindow.focus();
	}
}
}

function enable0()
{
  document.getElementById("sel_Beheereenheden").disabled = false;
  document.getElementById("sel_Beheereenheden").selectedIndex = 0;
  document.getElementById("sel_NatGeb").disabled = false;
}
function disable00()
{
  deselect('sel_NatGeb');
  deselect('sel_Beheereenheden');  
  document.getElementById("sel_Beheereenheden").disabled=true;
  document.getElementById("sel_NatGeb").disabled=true;
  document.getElementById("sel_NatGeb").length=0;  
}

function enable1()
{
  document.getElementById("sel_gebieden").disabled = false;
  document.getElementById("sel_gebieden").selectedIndex = 0;
  document.getElementById("sel_Areaal").disabled = false;
}
function disable01()
{
  deselect('sel_Areaal');
  deselect('sel_gebieden');
  document.getElementById("sel_gebieden").disabled=true;
  document.getElementById("sel_Areaal").disabled=true;
  document.getElementById("sel_Areaal").length=0;
}

function enable3()
{
  txtcolor = '#000000';
  document.getElementById('td_Xlo').style.color=txtcolor;
  document.getElementById('td_Ylo').style.color=txtcolor;
  document.getElementById('td_Xrb').style.color=txtcolor;
  document.getElementById('td_Yrb').style.color=txtcolor;
  document.getElementById("linksX").disabled=false;
  document.getElementById("linksY").disabled=false;
  document.getElementById("rechtsX").disabled=false;
  document.getElementById("rechtsY").disabled=false;
}
function disable03()
{
  txtcolor = '#999999';
  document.getElementById('td_Xlo').style.color=txtcolor;
  document.getElementById('td_Ylo').style.color=txtcolor;
  document.getElementById('td_Xrb').style.color=txtcolor;
  document.getElementById('td_Yrb').style.color=txtcolor;   
  document.getElementById("linksX").disabled=true;
  document.getElementById("linksX").value="";
  document.getElementById("linksY").disabled=true;
  document.getElementById("linksY").value="";
  document.getElementById("rechtsX").disabled=true;
  document.getElementById("rechtsX").value="";
  document.getElementById("rechtsY").disabled=true;
  document.getElementById("rechtsY").value="";
}

function deselect(id) 
{
// alert("before: selected="+id);
   if (document.getElementById(id).multiple)
    for (var i=0;i < document.getElementById(id).length; i++) 
    {
      //alert(document.getElementById(id).options[i].text)
      document.getElementById(id).options[i].selected = false;
    }
    else
    {
     document.getElementById(id).selectedIndex = -1;
    }
}

function jsc_defaultOptie() {
  <logic:equal name="KaartenForm" property="rad_Gebied" value="Natuurgebied">
  	jsc_optie0();
  </logic:equal>
    <logic:equal name="KaartenForm" property="rad_Gebied" value="Eenheid">
  	jsc_optie1();
  </logic:equal>
  <logic:equal name="KaartenForm" property="rad_Gebied" value="Nederland">
  	jsc_optie2();
  </logic:equal>
  <logic:equal name="KaartenForm" property="rad_Gebied" value="Coordinaten">
  	jsc_optie3();
  </logic:equal>
}

var imagesNat = new Array();
var imagesEen = new Array();
var imagesNede = new Array();
var imagesCoor = new Array();

function jsc_optie0()
{
disable01();
disable03();
jsc_VulSelectUitArray(document.KaartenForm.sel_NatGeb, arr_NatGeb[document.KaartenForm.sel_Beheereenheden.selectedIndex]);
jsc_ClearUnselectedOptions();

<% ArrayList kartTypes = (ArrayList) selKaartMap.get("Natuurgebied"); %>

document.KaartenForm.sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied = 'Natuurgebied' AND hidden = '0'" orderby="positie" directions="UP">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
	<mm:index jspvar="topIndex" write="false" vartype="Integer">
		document.KaartenForm.sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(nodeNumber)%>);
	imagesNat[<%=topIndex%>-1] = "media/vastgoed/Nicolao_Visscher.jpg";  
	<mm:relatednodes type="images" max="1">
		imagesNat[<%=topIndex%>-1] = "<mm:image template="s(132x106)" />";  
	</mm:relatednodes>	
	</mm:index>
	</mm:field>
	</mm:field>
</mm:listnodes>
jsc_setPicture(null);
}

function jsc_optie1()
{
disable00();
disable03();
jsc_VulSelectUitArray(document.KaartenForm.sel_Areaal, arr_Areaal[document.KaartenForm.sel_gebieden.selectedIndex]);
jsc_ClearUnselectedOptions();

<% kartTypes = (ArrayList) selKaartMap.get("Eenheid"); %>

document.KaartenForm.sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied = 'Eenheid' AND hidden = '0'" orderby="positie" directions="UP">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
	<mm:index jspvar="topIndex" write="false" vartype="Integer">
		document.KaartenForm.sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(nodeNumber)%>);
	imagesEen[<%=topIndex%>-1] = "media/vastgoed/Nicolao_Visscher.jpg";  
	<mm:relatednodes type="images" max="1">
		imagesEen[<%=topIndex%>-1] = "<mm:image template="s(132x106)" />";  
	</mm:relatednodes>	
	</mm:index>
	</mm:field>
	</mm:field>
</mm:listnodes>
jsc_setPicture(null);
}

function jsc_optie2()
{
disable00();
disable01();
disable03();
jsc_ClearUnselectedOptions();
<% kartTypes = (ArrayList) selKaartMap.get("Nederland"); %>

document.KaartenForm.sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied = 'Nederland' AND hidden = '0'" orderby="positie" directions="UP">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
	<mm:index jspvar="topIndex" write="false" vartype="Integer">
		document.KaartenForm.sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(nodeNumber)%>);
	imagesNede[<%=topIndex%>-1] = "media/vastgoed/Nicolao_Visscher.jpg";  
	<mm:relatednodes type="images" max="1">
		imagesNede[<%=topIndex%>-1] = "<mm:image template="s(132x106)" />";  
	</mm:relatednodes>	
	</mm:index>
	</mm:field>
	</mm:field>
</mm:listnodes>
jsc_setPicture(null);
}

function jsc_optie3()
{
disable00();
disable01();
jsc_ClearUnselectedOptions();
<% kartTypes = (ArrayList) selKaartMap.get("Coordinaten"); %>

document.KaartenForm.sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied = 'Coordinaten' AND hidden = '0'" orderby="positie" directions="UP">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
		<mm:index jspvar="topIndex" write="false" vartype="Integer">
		document.KaartenForm.sel_Kaart[<%=topIndex%>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(nodeNumber)%>);
	imagesCoor[<%=topIndex%>-1] = "media/vastgoed/Nicolao_Visscher.jpg";  
	<mm:relatednodes type="images" max="1">
		imagesCoor[<%=topIndex%>-1] = "<mm:image template="s(132x106)" />";  
	</mm:relatednodes>	
	</mm:index>
	</mm:field>
	</mm:field>
</mm:listnodes>
jsc_setPicture(null);
}

function enable4()
{
  document.getElementById("sel_Schaal").disabled = false;
  document.getElementById("sel_Schaal").selectedIndex = 3;
}

function enable5()
{
  document.getElementById("sel_Formaat").disabled = false;
  document.getElementById("sel_Formaat").selectedIndex = 0;
}


function disable1()
{
  deselect('sel_Schaal');
  deselect('sel_Formaat');
  document.getElementById("sel_Schaal").disabled = true;
  document.getElementById("sel_Formaat").disabled = true;
}

function jsc_optie4()
{
  disable1();
  enable4();
}

function jsc_optie5()
{
  disable1();
  enable5();
}

function jsc_setPicture(selectedIndex) {
	if ((selectedIndex != null) && (selectedIndex != -1)) {
	// use Natuurgebied
	if(document.KaartenForm.rad_Gebied[0].checked) {
		document.getElementById("kartPicture").src=imagesNat[selectedIndex]; 
 	}
 	// use Eenheid
	if(document.KaartenForm.rad_Gebied[1].checked) {
		document.getElementById("kartPicture").src=imagesEen[selectedIndex]; 
 	}
 	// use Nederland
	if(document.KaartenForm.rad_Gebied[2].checked) {
		document.getElementById("kartPicture").src=imagesNede[selectedIndex]; 
 	}
 	// use Coordinaten
	if(document.KaartenForm.rad_Gebied[3].checked) {
		document.getElementById("kartPicture").src=imagesCoor[selectedIndex]; 
 	}
 	} else {
 	// page load here. set to first selected
 	var iKaart = 0
 	while (iKaart < document.KaartenForm.sel_Kaart.length) {
 	if (document.KaartenForm.sel_Kaart[iKaart].selected) {
 		jsc_setPicture(iKaart);
 		}
 	iKaart++;
 	} 	
 	if (selectedIndex == -1) {
 		// all pictures unselected
 		document.getElementById("kartPicture").src = "media/vastgoed/Nicolao_Visscher.jpg";
 	}
 	}
}

// ragGebied radio and a certain kart should be selected before form submit
function validationMessage() {
	//rad_Gebied check
	var radioChecked = false;
	var i = 0;
	while((i < document.KaartenForm.rad_Gebied.length) && (radioChecked == false)) {
		radioChecked = document.KaartenForm.rad_Gebied[i].checked;
		i++;
	}
	if(!radioChecked) {
		alert("Geen gebied geselecteerd of coördinaten opgegeven.");
		return false;
	}
	// gebied and eenheid/regio/provincie validations
	if ((document.KaartenForm.rad_Gebied[0].checked) && (document.KaartenForm.sel_NatGeb.selectedIndex == -1)) {
		alert("Geen gebied geselecteerd.");
		return false;
	}
	if ((document.KaartenForm.rad_Gebied[1].checked) && (document.KaartenForm.sel_Areaal.selectedIndex == -1)) {
		alert("Geen eenheid/regio/provincie geselecteerd.");
		return false;
	}
	// coordinates
	if (document.KaartenForm.rad_Gebied[3].checked) {
		var linksX = parseInt(document.KaartenForm.linksX.value.replace(".", ""));
		var linksY = parseInt(document.KaartenForm.linksY.value.replace(".", ""));
		var rechtsX = parseInt(document.KaartenForm.rechtsX.value.replace(".", ""));
		var rechtsY = parseInt(document.KaartenForm.rechtsY.value.replace(".", ""));
		
		if ((linksX ==="") || (linksY ==="") || (rechtsX ==="") || (rechtsY ==="")
			|| (isNaN(linksX)) || (isNaN(linksY)) || (isNaN(rechtsX)) || (isNaN(rechtsY)) ) {
		alert("Geen coördinaten opgegeven.");
		return false;		
		}
		// range checks
		if ((linksX <0) || (linksX >300000) || (rechtsX <0) || (rechtsX >300000) || (linksY <300000) || (linksY >650000) || (rechtsY <300000) || (rechtsY >650000) ) {
		alert("De coördinaten moeten aan de volgende voorwaarden voldoen: 0 <= X <= 300.000 en 300.000 <= Y <= 650.000.");
		return false;	
		}
		if ((linksX  - rechtsX >= 0) || (linksY - rechtsY >= 0)) {
		alert("De coördinaten voldoen niet aan de voorwaarden: XLinksonder < Xrechtsboven en Ylinksonder < Yrechtsboven.");
		return false;	
		}
	}	
	// map selection
	var kartSelected = false;
	i = 0;
	while((i < document.KaartenForm.sel_Kaart.length) && (kartSelected == false)) {
		kartSelected = document.KaartenForm.sel_Kaart[i].selected;
		i++;
	}
	if(!kartSelected) {
		alert("Geen kaartsoort geselecteerd.");
		return false;
	}
	return true;
}

//
function doOnLoad() {
	jsc_defaultOptie(); 
   jsc_VulSelectUitArray(document.KaartenForm.sel_NatGeb, arr_NatGeb[document.KaartenForm.sel_Beheereenheden.selectedIndex]); 
   jsc_VulSelectUitArray(document.KaartenForm.sel_Areaal, arr_Areaal[document.KaartenForm.sel_gebieden.selectedIndex]);
	setScreenSize();
   //document.getElementById("sel_Formaat").disabled=true;
   
   //alert("<%=rad_Schaal%>");
   
   <% if ("formaat".equals(rad_Schaal)) { %>
      deselect('sel_Schaal');
      document.getElementById("sel_Schaal").disabled = true;
   <% } else { %>
      deselect('sel_Formaat');
      document.getElementById("sel_Formaat").disabled = true;   
   <% } %>

}
      </script>
      
      <style type="text/css">
         <!--

DIV.Pagina
{
	position: absolute;
	z-index:3;
}

DIV.Info
{
	position: relative;
	Color: #CC0033;
	cursor: hand;
	width: 29px;
	height: 24px;
	background-image: url(media/vastgoed/Info.png);
}

DIV.Schermuitleg 
{
	position: relative;
	height: auto;
	width: 470px;
	margin-top: .6em;
	margin-right: 3em;
	margin-left: 0;
	margin-bottom: .6em;
	padding-top: .75em;
	padding-right: 6px;
	padding-left: .75em;
	padding-bottom: .75em;
}


         -->
      </style>
   </head>
   
   <body <% 
         if(!printPage) { 
         %>onLoad="javascript:resizeBlocks();doOnLoad();<mm:present referid="extraload"><mm:write referid="extraload" /></mm:present
   >" onResize="javascript:resizeBlocks();" onUnLoad="javascript:setScreenSize()"<%
         } else {
   %>onLoad="self.print();"<% 
         }
   %>>
   <%@include file="/editors/paginamanagement/flushlink.jsp" %>
   <table background="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>.jpg" cellspacing="0" cellpadding="0" border="0">
   <% 
   if(!printPage) { 
   %>
   <%@include file="includes/searchbar.jsp" %>
   <tr>
      <td class="black"><img src="media/spacer.gif" width="195" height="1"></td>
      <td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
      <td class="black"><img src="media/spacer.gif" width="251" height="1"></td>
   </tr>
   <% 
   } 
   %>
   <tr>
      <% 
      if(!printPage) { 
      %><td rowspan="2"><%@include file="includes/nav.jsp" %></td><% 
      } 
      %>
      <%@include file="includes/calendar.jsp" %>   
      
      <% boolean twoColumns = !printPage && ! NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek"); %>
      <td <% if(!twoColumns) { %>colspan="2"<% } %>><%@include file="includes/pagetitle.jsp" %></td>
      <% 
      if(twoColumns) {
         String rightBarTitle = "";
      %><td><%@include file="includes/rightbartitle.jsp" %></td><%
      } %>
   </tr>
   <tr>
   <td class="transperant" <% if(NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek")) { %>colspan="2"<% } %>>
   <div class="<%= infopageClass %>" id="infopage">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr><td style="padding:10px;padding-top:18px;">
   <a name="top">
   <%--%@include file="includes/back_print.jsp" %>--%>
   
   <html:form action="/nmintra/KaartenAction" method="POST" onsubmit="return validationMessage()">
      <table>
         <tr>
            <td><h3>Bestelformulier</h3></td>
         </tr>
         <tr>
            <td width="450">
               Selecteer gebieden of geef co&ouml;rdinaten:
            </td>
            <td align="right">	
               <a href="javascript:giveInfo(0, '<%=baseURL%>');">
                  <img src="media/vastgoed/Info.png" width="29" height="24" border=0 alt="Klik hier voor uitleg" title="Klik hier voor uitleg">
               </a>
            </td>
         </tr> 
      </table>	
      
      
      <table width ="500"  class="vastgoed_medium" border="0" cellspacing="0">
         <tr>
            <td width="20">
               <html:radio property="rad_Gebied" value="Natuurgebied" onclick="jsc_optie0();enable0();" style="background:vastgoed_medium"/>
            </td>
            <td width="220">Natuurgebied:</td>
            
            <td>&nbsp;</td>
         </tr>
         <tr>
            <td rowspan="2" height="110">&nbsp;</td>
            <td></td>
            <td rowspan="2" width="249" valign="top">
               <select id="sel_NatGeb" NAME="sel_NatGeb" style="width:100%;" size="6" multiple="true">
               </select>
            </td>
            
         </tr>
         <tr>
            <td height="70" valign="top">
               <html:select styleId="sel_Beheereenheden" style="width:100%;" property="sel_Beheereenheden" onclick="jsc_VulSelectUitArray(this.form.sel_NatGeb, arr_NatGeb[this.selectedIndex]);">
                  <html:options property="gebiedList"/>
               </html:select>
            </td>
         </tr>
         <tr height="1" bgcolor="#FFFFFF">
            
            <td></td>
            <td></td>
            <td></td>
         </tr>
      </table>
      
      
      <table width ="500"  class="vastgoed_light" border="0" cellspacing="0">
         <tr>
            <td width="20">
               <html:radio property="rad_Gebied" value="Eenheid" onclick="jsc_optie1();enable1();" style="background:vastgoed_light"/>
            </td>
            <td width="220">Eenheid / Regio / Provincie:</td>
            <td>&nbsp;</td>
         </tr>
         <tr>
            <td rowspan="2" height="110">&nbsp;</td>
            <td></td>
            <td rowspan="2" width="249" valign="top">
               
               <select id="sel_Areaal" NAME="sel_Areaal" style="width:100%;" size="6">
               </select>
            </td>
         </tr>
         <tr>
            <td height="70" valign="top">
               <html:select styleId="sel_gebieden" style="width:100%;" property="sel_gebieden" onclick="jsc_VulSelectUitArray(this.form.sel_Areaal, arr_Areaal[this.selectedIndex]);">
                  <html:option value="Eenheid">Eenheid</html:option>
                  <html:option value="Provincie">Provincie</html:option>
                  <html:option value="Regio">Regio</html:option>
               </html:select>
            </td>
         </tr>
         <tr height="1" bgcolor="#FFFFFF">
            <td></td>
            <td></td>
            
            <td></td>
         </tr>
      </table>
      
      
      <table width ="500"  class="vastgoed_medium" border="0" cellspacing="0">
         <tr>
            <td width="20" height="20" valign="top">
               <html:radio property="rad_Gebied" value="Nederland" onclick="jsc_optie2();" style="background:vastgoed_medium"/>
            </td>
            
            <td width="220" valign="top">Nederland:</td>
         </tr>
         <tr height="1" bgcolor="#FFFFFF">
            <td></td>
            <td></td>
            <td></td>
         </tr>
      </table>
      
      <table width="500" class="vastgoed_light" border="0" cellspacing="0">	
         <tr>
            <td width="20">
               <html:radio property="rad_Gebied" value="Coordinaten" onclick="jsc_optie3();enable3();" style="background:vastgoed_light"/>
            </td>
            <td colspan="4">Coördinaten:&nbsp;</td>
            <td>&nbsp;</td>
         </tr>
         
         <tr>
            <td>&nbsp;</td>
            <td id="td_Xlo" width="150" style="text-align:right;">linksonder X:&nbsp;</td>
            <td width="50">
               <html:text styleId="linksX" style="width:100%;" property="linksX" size="7"/>
            </td>
            <td id="td_Ylo" width="50" style="text-align:right;">Y:&nbsp;</td>
            <td width="50">
               <html:text styleId="linksY" style="width:100%;" property="linksY" size="7"/>
            </td>
            <td>&nbsp;</td>
         </tr>
         <tr>
            <td>&nbsp;</td>
            <td id="td_Xrb" style="text-align:right;">rechtsboven X:&nbsp;</td>
            <td>
               <html:text styleId="rechtsX" style="width:100%;" property="rechtsX" size="7"/>
            </td>
            <td id="td_Yrb" style="text-align:right;">Y:&nbsp;</td>
            <td>
               <html:text styleId="rechtsY" style="width:100%;" property="rechtsY" size="7"/>    
            </td>
            <td>&nbsp;</td>
         </tr>
      </table>
      
      <br>
      
      
      <table>
         <tr>
            <td width="450">
               Selecteer gewenste kaarten:
            </td>
            <td align="right">	
               <a href="javascript:giveInfo(1, '<%=baseURL%>');">
                  <img src="media/vastgoed/Info.png" width="29" height="24" border=0 alt="Klik hier voor uitleg" title="Klik hier voor uitleg">
               </a>
            </td>
         </tr> 
      </table>		
      
      
      <table width="500"  class="vastgoed_medium" border="0" cellspacing="0">
         <tr>
            <td width="96" align="left">Kaarten:&nbsp;<br>klik op afbeelding voor vergroting en informatie</td>
            <td width="139">
               <img id="kartPicture" style="cursor:pointer" src="media/vastgoed/Nicolao_Visscher.jpg" width="132" height="107" border="0" alt="Klik hier voor vergroting en meer gegevens van deze kaart"  title="Klik hier voor vergroting en meer gegevens van deze kaart" onClick="javascript:small_window('includes/vastgoed/kaart_popup.jsp');">
               
            </td>
            <td width="249">
               <%-- ** If multiple karts need to be selected add multiple="multiple" to the html:select below ** --%>
               <html:select style="width:100%;" property="sel_Kaart" size="6" onchange="jsc_setPicture(this.selectedIndex);" multiple="true">
               </html:select>
            </td>
         </tr>
      </table>
      <br>
      
      
      <table>
         
         <tr>
            <td width="450">
               Geef de schaal of het formaat  en het aantal:
            </td>
            <td align="right">	
               <a href="javascript:giveInfo(2, '<%=baseURL%>');">
                  <img src="media/vastgoed/Info.png" width="29" height="24" border=0 alt="Klik hier voor uitleg" title="Klik hier voor uitleg" >
               </a>
            </td>
         </tr> 
      </table>	
      
      
      <table width="500" border="0" cellspacing="0">
         
         <tr class="vastgoed_medium">
            <td width="20">
               <html:radio property="rad_Schaal" value="schaal" style="background:vastgoed_medium" onclick="jsc_optie4();"/>
            </td>
            <td width="100" align="right">schaal:&nbsp;</td>
            <td width="100">
               <html:select styleId="sel_Schaal" style="width:100%;" property="schaal">
                  <html:option value="1:500">1:500</html:option>
                  <html:option value="1:1.000">1:1.000</html:option>
                  <html:option value="1:2.500">1:2.500</html:option>
                  <html:option  value="1:5.000">1:5.000</html:option>
                  <html:option value="1:7.500">1:7.500</html:option>
                  <html:option value="1:10.000">1:10.000</html:option>
                  <html:option  value="1:12.500">1:12.500</html:option>
                  <html:option value="1:15.000">1:15.000</html:option>
                  <html:option value="1:17.500">1:17.500</html:option>
                  <html:option value="1:20.000">1:20.000</html:option>
                  <html:option value="1:25.000">1:25.000</html:option>
               </html:select>
            </td>
            <td width="200">&nbsp;</td>
            <td>&nbsp;</td>
         </tr>
         <tr height="1" bgcolor="#FFFFFF">
            
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
         </tr>
         <tr class="vastgoed_light">
            <td><html:radio property="rad_Schaal" value="formaat" style="background:vastgoed_light" onclick="jsc_optie5();"/></td>
            <td align="right">formaat:&nbsp;</td>
            
            <td>
               <html:select styleId="sel_Formaat" style="width:100%;" property="formaat">
                  <html:option value="A4">A4</html:option>
                  <html:option value="A3">A3</html:option>
                  <html:option value="A2">A2</html:option>
                  <html:option value="A1">A1</html:option>
                  <html:option value="A0">A0</html:option>
                  
               </html:select>
            </td>
            <td></td>
            <td></td>
         </tr>
         <tr height="10" bgcolor="#FFFFFF">
            <td></td>
            <td></td>
            
            <td></td>
            <td></td>
            <td></td>
         </tr>
         <tr class="vastgoed_medium">
            <td>
               <html:radio property="rad_Gevouwen" value="gevouwen" style="background:vastgoed_medium"/>
            </td>
            <td align="left">&nbsp;gevouwen&nbsp;</td>
            
            <td></td>
            <td></td>
            <td></td>
         </tr>
         <tr height="1" bgcolor="#FFFFFF">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
            <td></td>
         </tr>
         <tr class="vastgoed_light">
            <td><html:radio property="rad_Gevouwen" value="opgerold" style="background:vastgoed_light"/></td>
            <td align="left">&nbsp;opgerold&nbsp;</td>
            <td></td>
            <td></td>
            <td></td>
            
         </tr>
         <tr height="10" bgcolor="#FFFFFF">
            <td></td>
            <td></td>
            
            <td></td>
            <td></td>
            <td></td>
         </tr>
         <tr class="vastgoed_medium">
            <td></td>
            <td>aantal:&nbsp;</td>
            <td>
               <html:select style="width:100%;" property="aantal">
                  <% for (int iAantal = 1; iAantal<=10; iAantal++) { %>
                  <html:option value="<%=String.valueOf(iAantal)%>"><%=iAantal%></html:option>
                  <% } %>
               </html:select>
            </td>
            <td></td>
            <td></td>
            
         </tr>
      </table>
      
      <br>
      
      <table>
         <tr>
            <td width="450">
               Opmerkingen:
            </td>
            <td align="right">	
               <a href="javascript:giveInfo(3, '<%=baseURL%>');">
                  
                  <img src="media/vastgoed/Info.png" align="right" width="29" height="24" border=0 alt="Klik hier voor uitleg" title="Klik hier voor uitleg" >
               </a>
            </td>
         </tr> 
      </table>	
      
      <table width="500" class="vastgoed_light" border="0" cellspacing="0">
         <tr height="5">
            <td width="5"></td>
            <td width="440"></td>
            <td width="50"></td>
            
            <td width="5"></td>
         </tr>
         <tr>
            <td></td>
            <td colspan="2">
               <html:textarea property="opmerkingen" style="width:486px;" rows="5"></html:textarea>
            </td>
            <td></td>
            
         </tr>
      </table>	
      
      <table width="500" border="0" cellspacing="0">
         <tr>
            <td width="100%">&nbsp;</td>
         </tr> 
      </table>	
      
      <table width="500" class="vastgoed_medium" border="0" cellspacing="0">
         <tr height="5">
            <td width="1"></td>
            <td></td>
            <td></td>
            <td width="1"></td>
         </tr>
         <tr height="20">
            <td></td>
            <td>&nbsp;</td>
            <td align="right" style="text-align:right">
               <html:submit property="send" value="Toevoegen aan mijn bestelling" />
            </td>
            <td>&nbsp;</td>
         </tr>
         <tr height="5">
            <td></td>
            <td colspan="3">
               <br/>
               <%String gotoKartLink = "/nmintra/KaartenAction.eb" + rubriekParams + "&shopping_cart"; %>
               <html:link 
                  page="<%=gotoKartLink%>">
                  <p align="right">Bekijk mijn bestelling...&nbsp;&nbsp</p>
               </html:link>
               
            </td>
         </tr>
      </table>
      
      <input type="hidden" name="number" value="<%=request.getParameter("number")%>"/>
      
      <input type="hidden" name="rb" value="<%=iRubriekStyle%>"/>
      <input type="hidden" name="rbid" value="<%=rubriekId%>"/>
      <input type="hidden" name="pgid" value="<%=paginaID%>"/>
      <input type="hidden" name="ssid" value="<%=subsiteID%>"/>
   </html:form>
   
   <% 
   if(twoColumns) { 
   // *********************************** right bar *******************************
   String styleClass = "white";
   String styleClassDark = "white";
   
   %><td style="padding-left:10px;">
      <div class="rightcolumn" id="rightcolumn">
         
         <mm:list nodes="<%= paginaID %>" path="pagina,readmore,contentblocks" orderby="readmore.pos">
            <mm:node element="contentblocks">
               <%@include file="includes/contentblockdetails.jsp" %>
            </mm:node>
            <br/>
         </mm:list>
      </div>
   </td><%
   } %>
   
   
   <%@include file="includes/footer.jsp" %>
</mm:cloud>
</mm:content>