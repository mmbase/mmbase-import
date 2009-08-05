<% 
String mapAction = request.getParameter("mapAction"); if(mapAction == null){ mapAction = "url";}
String prov = request.getParameter("prov"); if(prov == null){ prov = "";}
String activeLayer ="";

if(prov.equals("prov_gr")){activeLayer = "layer1";}
if(prov.equals("prov_dr")){activeLayer = "layer2";}
if(prov.equals("prov_fr")){activeLayer = "layer3";}
if(prov.equals("prov_nh")){activeLayer = "layer4";}
if(prov.equals("prov_fl")){activeLayer = "layer5";}
if(prov.equals("prov_ov")){activeLayer = "layer6";}
if(prov.equals("prov_ge")){activeLayer = "layer7";}
if(prov.equals("prov_ut")){activeLayer = "layer8";}
if(prov.equals("prov_zu")){activeLayer = "layer9";}
if(prov.equals("prov_nb")){activeLayer = "layer10";}
if(prov.equals("prov_li")){activeLayer = "layer11";}
if(prov.equals("prov_ze")){activeLayer = "layer12";}
%>
<SCRIPT language=JavaScript>
//Define global variables
 	var totalLayersInLoop=16;
	var layerNumShowing=1;
	var layerStyleRef;
	var layerRef;
	var styleSwitch;
	var lockedLayer = "layer13";
	var releaseLayer = "layer13";
	function init(){
       	  if(navigator.appName == "Netscape") {
		    layerStyleRef="layer.";
 			layerRef="document.layers";
			styleSwitch="";
			}else{
 			layerStyleRef="layer.style.";
			layerRef="document.all";
 			styleSwitch=".style";
			} 
	}
	function showLayerNumber(number){
	 		var layerNumToShow=number;
			hideLayer(eval('"layer' + layerNumShowing+'"'));
	 		showLayer(eval('"layer' + layerNumToShow+'"'));
	 		layerNumShowing=layerNumToShow;
			showLayer(lockedLayer);
	}
	function showLayer(layerName){
		init();
		eval(layerRef+'["'+layerName+'"]'+styleSwitch+'.visibility="visible"'); 	
	}
	
	function hideLayer(layerName){
		init();
	 	eval(layerRef+'["'+layerName+'"]'+styleSwitch+'.visibility="hidden"'); 	
	}
	function setProv(p,l){
	for (i=0;i<document.route.prov.length;i++){
		if(document.route.prov[i].value == p){
			document.route.prov[i].selected = true;
			lockLayer(l);
			hideLayer(releaseLayer);
			releaseLayer = lockedLayer;
		}
	}
	}
	function lockLayer(layerName){
		lockedLayer = layerName;
	} 
	
	function mapLayer(l){

	var ml = "";
	
	if( l == 'prov_gr') {  ml = 1;}
	if( l == 'prov_dr') {  ml = 2;}
	if( l == 'prov_fr') {  ml = 3;}
	if( l == 'prov_nh') {  ml = 4;}
	if( l == 'prov_fl') {  ml = 5;}
	if( l == 'prov_ov') {  ml = 6;}
	if( l == 'prov_ge') {  ml = 7;}
	if( l == 'prov_ut') {  ml = 8;}
	if( l == 'prov_zu') {  ml = 9;}
	if( l == 'prov_nb') {  ml = 10;}
	if( l == 'prov_li') {  ml = 11;}
	if( l == 'prov_ze') {  ml = 12;}

	return ml;
	}

function selectLayer(s){
	if(s != ''){
		var l =  mapLayer(s);
		var layerName = "layer"+l;
		setProv(s,layerName);
		showLayerNumber(l);
	}
}

</SCRIPT>
<STYLE type="text/css">#layer00{Z-INDEX: 1; VISIBILITY:visible ; POSITION: absolute;}	
#layer0 {Z-INDEX: 10000; VISIBILITY:visible ; POSITION: absolute;}	
#layer1 {Z-INDEX: 10;  VISIBILITY: hidden; POSITION: absolute;}
#layer2 {Z-INDEX: 15;  VISIBILITY: hidden; POSITION: absolute;}
#layer3 {Z-INDEX: 20; VISIBILITY: hidden; POSITION: absolute;}
#layer4 {Z-INDEX: 25; VISIBILITY: hidden; POSITION: absolute;}	
#layer5 {Z-INDEX: 30; VISIBILITY: hidden; POSITION: absolute;}	
#layer6 {Z-INDEX: 35;  VISIBILITY: hidden; POSITION: absolute;}
#layer7 {Z-INDEX: 40; VISIBILITY: hidden; POSITION: absolute;}
#layer8 {Z-INDEX: 45; VISIBILITY: hidden; POSITION: absolute;}	
#layer9 {Z-INDEX: 50; VISIBILITY: hidden; POSITION: absolute;}	
#layer10 {Z-INDEX: 55; VISIBILITY: hidden; POSITION: absolute;}
#layer11 {Z-INDEX: 60; VISIBILITY: hidden; POSITION: absolute;}	
#layer12 {Z-INDEX: 65; VISIBILITY: hidden; POSITION: absolute;}	
#layer13 {Z-INDEX: 70; VISIBILITY: hidden; POSITION: absolute;}
</STYLE>			
<MAP name="nederland">
   <AREA alt="Groningen" shape="poly" coords="89,0,87,7,85,16,83,21,87,24,92,18,98,22,108,35,114,16,102,5,97,5" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_gr','layer1')<%}else{%>natuurgebieden.jsp?p=p_gr<% } %>" target="main" onMouseOver="javascript:showLayerNumber(1)">
   <AREA alt="Drenthe" shape="poly" coords="108,44,99,45,99,47,94,44,92,47,88,46,83,42,82,36,86,32,89,31,88,24,92,18,98,22,108,34" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_dr','layer2')<%}else{%>natuurgebieden.jsp?p=p_dr<% } %>" target="main" onMouseOver="javascript:showLayerNumber(2)">
   <AREA alt="Friesland" shape="poly" coords="60,34,69,34,73,37,81,37,88,30,87,23,82,21,86,14,86,6,74,4,55,7,45,15,48,17,59,10,70,7,74,7,74,9,65,13,60,18" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_fr','layer3')<%}else{%>natuurgebieden.jsp?p=p_fr<% } %>" target="main" onMouseOver="javascript:showLayerNumber(3)">
   <AREA alt="Noord-Holland" shape="poly" coords="34,57,34,58,38,41,41,25,45,17,48,20,46,26,46,30,50,30,52,31,54,37,57,38,58,41,56,45,54,46,51,46,53,52,50,58,57,61,55,67,48,62,43,64,37,64,37,59" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_nh','layer4')<%}else{%>natuurgebieden.jsp?p=p_nh<% } %>" target="main" onMouseOver="javascript:showLayerNumber(4)">
   <AREA alt="Flevoland" shape="poly" coords="63,62,72,53,74,48,76,45,76,41,70,34,65,39,65,46,57,53,52,56,55,59" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_fl','layer5')<%}else{%>natuurgebieden.jsp?p=p_fl<% } %>" target="main" onMouseOver="javascript:showLayerNumber(5)">
   <AREA alt="Overijssel" shape="poly" coords="80,65,81,65,99,71,107,66,110,60,107,52,99,50,98,46,94,44,90,47,82,42,82,36,78,37,73,37,76,43,75,50,80,55" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_ov','layer6')<%}else{%>natuurgebieden.jsp?p=p_ov<% } %>" target="main" onMouseOver="javascript:showLayerNumber(6)">
   <AREA alt="Gelderland" shape="poly" coords="78,86,82,81,87,82,90,84,101,78,104,74,96,69,79,64,79,54,74,48,70,55,63,62,61,62,66,77,54,78,50,84,57,88,63,84,72,88,79,89" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_ge','layer7')<%}else{%>natuurgebieden.jsp?p=p_ge<% } %>" target="main" onMouseOver="javascript:showLayerNumber(7)">
   <AREA alt="Utrecht" shape="poly" coords="44,76,42,64,47,62,54,66,57,60,61,62,65,76,55,78" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_ut','layer8')<%}else{%>natuurgebieden.jsp?p=p_ut<% } %>" target="main" onMouseOver="javascript:showLayerNumber(8)">
   <AREA shape="poly" coords="104,1,112,0,0,0,0,94,16,79,25,70,38,37,42,17,59,2,86,2,102,3,109,12,113,18,112,32,109,44,109,52,110,65,103,71,102,81,83,84,86,98,84,111,84,118,83,130,79,133,66,133,66,128,72,117,43,104,34,102,27,108,16,112,7,108,0,112,2,132,114,134,114,2,111,0,106,1" nohref onMouseOver="javascript:showLayerNumber(13)">
   <AREA alt="Zuid-Holland" shape="poly" coords="20,76,28,67,34,57,36,58,36,64,42,64,43,75,53,77,49,82,36,88,27,91,15,83" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_zu','layer9')<%}else{%>natuurgebieden.jsp?p=p_zu<% } %>" target="main" onMouseOver="javascript:showLayerNumber(9)">
   <AREA alt="Noord-Brabant" shape="poly" coords="66,113,68,109,76,106,72,98,79,98,73,88,62,84,57,88,46,83,27,92,26,103,31,106,32,103,31,100,35,99,36,101,40,101,43,97,43,102,49,102,50,100,56,109" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_nb','layer10')<%}else{%>natuurgebieden.jsp?p=p_nb<% } %>" target="main" onMouseOver="javascript:showLayerNumber(10)">
   <AREA alt="Limburg" shape="poly" coords="71,133,80,133,82,125,76,121,84,115,82,110,85,106,85,98,82,93,82,90,73,88,79,98,72,98,75,106,66,112,74,114,71,122" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_li','layer11')<%}else{%>natuurgebieden.jsp?p=p_li<% } %>" target="main" onMouseOver="javascript:showLayerNumber(11)">
   <AREA alt="Zeeland" shape="poly" coords="26,104,22,109,17,112,1,106,3,93,15,84,26,91" target="_top" href="<% if(mapAction.equals("form")){%>javascript:setProv('prov_ze','layer12')<%}else{%>natuurgebieden.jsp?p=p_ze<% } %>" target="main" onMouseOver="javascript:showLayerNumber(12)">
</MAP>
<DIV id="layer00"><IMG src="media/images/maps/ne.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer0"><IMG src="media/images/maps/blank.gif" width="115" height="135" border="0" alt="" usemap="#nederland"></DIV>
<DIV id="layer1"><IMG src="media/images/maps/ne_gro.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer2"><IMG src="media/images/maps/ne_dre.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer3"><IMG src="media/images/maps/ne_fri.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer4"><IMG src="media/images/maps/ne_nho.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer5"><IMG src="media/images/maps/ne_fle.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer6"><IMG src="media/images/maps/ne_ove.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer7"><IMG src="media/images/maps/ne_gel.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer8"><IMG src="media/images/maps/ne_utr.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer9"><IMG src="media/images/maps/ne_zho.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer10"><IMG src="media/images/maps/ne_bra.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer11"><IMG src="media/images/maps/ne_lim.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer12"><IMG src="media/images/maps/ne_zee.gif" width="115" height="135" border="0" alt=""></DIV>
<DIV id="layer13"><IMG src="media/images/maps/blank.gif" width="115" height="135" border="0" alt=""></DIV>
<script>
<% if(!activeLayer.equals("")){%>
	showLayer('<%=activeLayer%>');
<% } %>
</script>