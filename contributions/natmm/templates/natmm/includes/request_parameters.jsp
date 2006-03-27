<%
// *** these are the variables used to select a page and content on that page 
// *** make sure it really is a rubriek that is selected by parameter r
%>
<mm:import jspvar="rubriekID" externid="r">-1</mm:import>
<mm:import jspvar="paginaID" externid="p">-1</mm:import>
<mm:import jspvar="dossierID" externid="d">-1</mm:import>
<mm:import jspvar="artikelID" externid="a">-1</mm:import>
<mm:import jspvar="evenementID" externid="e">-1</mm:import>
<mm:import jspvar="natuurgebiedID" externid="n">-1</mm:import>
<mm:import jspvar="provID" externid="prov" id="prov">-1</mm:import>
<mm:import jspvar="vacatureID" externid="v">-1</mm:import>
<mm:import jspvar="imgID" externid="i">-1</mm:import>
<mm:import jspvar="personID" externid="pers">-1</mm:import>
<mm:import jspvar="offsetID" externid="offset" id="offset">0</mm:import>
<mm:import jspvar="ID" externid="id">-1</mm:import>
<%

// *** note: there is a difference between layout and style: style is always set, layout may have value parent_layout

// *** new layouts should be added to /editors/rubriek/rubriek.jsp ***
int PARENT_LAYOUT = -1;
int DEFAULT_LAYOUT = 0;
int SUBSITE1_LAYOUT = 1;
int SUBSITE2_LAYOUT = 2;
int SUBSITE3_LAYOUT = 3;


// *** default style should be set in top1_params.jsp and new layouts should be added to /editors/rubriek/rubriek.jsp ***
int PARENT_STYLE = -1;
int DEFAULT_STYLE = 7;
String [] style1 = {"vereniging","steun" ,"nieuws","natuurin","natuurgebieden","links" ,"fun"   ,"default","zoeken","winkel","vragen","naardermeer" };
String [] color1 = {"552500"    ,"990100","4A7934","D71920"  ,"BAC42B"        ,"9C948C","EC008C","1D1E94" ,"00AEEF","F37021","6C6B5C","F37021" }; // color + line leftnavpage
String [] color2 = {"E4BFA3"    ,"F7D6C3","B0DF9B","FFBDB7"  ,"EEF584"        ,"EDE9E6","FABFE2","96ADD9" ,"B2E7FA","FED9B2","D6D6D1","F9B790" }; // background leftnavpage_high
String [] color3 = {"050080"    ,"050080","050080","050080"  ,"050080"        ,"050080","050080","FFFFFF" ,"050080","050080","050080","FFFFFF"};  // footer links
%>