<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page import="java.util.*" %>
<script type="text/javascript" language="javascript">

// MM:what a hell!
//HV Menu- by Ger Versluis (http://www.burmees.nl/)
//Submitted to Dynamic Drive (http://www.dynamicdrive.com)
//Visit http://www.dynamicdrive.com for this script and more

function Go(){return}

</script>
<script type="text/javascript" language="javascript">
	var LowBgColor='#E6E9DD';				// Background color when mouse is not over
	var LowSubBgColor='#E6E9DD';			// Background color when mouse is not over on subs
	var HighBgColor='#C4E856';				// Background color when mouse is over
	var HighSubBgColor='#C4E856';			// Background color when mouse is over on subs
	var FontLowColor='#3E3E3E';				// Font color when mouse is not over
	var FontSubLowColor='#3E3E3E';			// Font color subs when mouse is not over
	var FontHighColor='#3E3E3E';			// Font color when mouse is over
	var FontSubHighColor='#3E3E3E';			// Font color subs when mouse is over
	var BorderColor='#F9FAFB';				// Border color
	var BorderSubColor='#F9FAFB';			// Border color for subs
	var BorderWidth=2;						// Border width
	var BorderBtwnElmnts=1;					// Border between elements 1 or 0
	var FontFamily="arial,helvetica,sans-serif"	// Font family menu items
	var FontSize=8;							// Font size menu items
	var FontBold=0;							// Bold menu items 1 or 0
	var FontItalic=0;						// Italic menu items 1 or 0
	var MenuTextCentered='left';			// Item text position 'left', 'center' or 'right'
	var MenuCentered='left';				// Menu horizontal position 'left', 'center' or 'right'
	var MenuVerticalCentered='top';			// Menu vertical position 'top', 'middle','bottom' or static
	var ChildOverlap=0;						// horizontal overlap child/ parent
	var ChildVerticalOverlap=0;				// vertical overlap child/ parent
	var StartTop=77;						// Menu offset x coordinate
	var StartLeft=0;						// Menu offset y coordinate
	var VerCorrect=0;						// Multiple frames y correction
	var HorCorrect=0;						// Multiple frames x correction
	var LeftPaddng=3;						// Left padding
	var TopPaddng=6;						// Top padding
	var FirstLineHorizontal=0;				// SET TO 1 FOR HORIZONTAL MENU, 0 FOR VERTICAL
	var MenuFramesVertical=1;				// Frames in cols or rows 1 or 0
	var DissapearDelay=1000;				// delay before menu folds in
	var TakeOverBgColor=1;					// Menu frame takes over background color subitem frame
	var FirstLineFrame='navig';				// Frame where first level appears
	var SecLineFrame='space';				// Frame where sub levels appear
	var DocTargetFrame='space';				// Frame where target documents appear
	var TargetLoc='';						// span id for relative positioning
	var HideTop=0;							// Hide first level when loading new document 1 or 0
	var MenuWrap=1;							// enables/ disables menu wrap 1 or 0
	var RightToLeft=0;						// enables/ disables right to left unfold 1 or 0
	var UnfoldsOnClick=0;					// Level 1 unfolds onclick/ onmouseover
	var WebMasterCheck=0;					// menu tree checking on or off 1 or 0
	var ShowArrow=0;						// Uses arrow gifs when 1
	var KeepHilite=1;						// Keep selected path highligthed
	var Arrws=['media/tri.gif',5,10,'media/tridown.gif',10,5,'media/trileft.gif',5,10];	// Arrow source, width and height

function BeforeStart(){return}
function AfterBuild(){return}
function BeforeFirstOpen(){return}
function AfterCloseAll(){return}

// Menu tree
//	MenuX=new Array(Text to show, Link, background image (optional), number of sub elements, height, width);
//	For rollover images set "Text to show" to:  "rollover:Image1.jpg:Image2.jpg"

<%--

count number off first line menus
--%><%	int numberofpages = 0; 
	int numberofportals = -1; // minus one since the current portal is excluded from the navigation
	int numberofblanks = 3;
	Vector currentPath = new Vector(); 
%><mm:listnodes type="portals" ><% numberofportals++; %></mm:listnodes
><mm:node number="$portal"
><mm:relatednodes type="pages" role="posrel" 
	><mm:countrelations type="pages" jspvar="dummy" vartype="Integer" write="false"
		><% try { 
			numberofpages += dummy.intValue();
		} catch(Exception e) { } 
		%></mm:countrelations
></mm:relatednodes
></mm:node>
var NoOffFirstLineMenus=  <%= numberofpages + numberofportals + numberofblanks %>;
<%--

********* add pages of this portal to navigation *********
--%>
<mm:node number="$portal">
<mm:related path="posrel,pages1">
<mm:field name="pages1.number" jspvar="pages1_number" vartype="String" write="false">
<%	
	int [] offset = new int[10];
	for(int d=0; d<offset.length; d++) {
     	   offset[d]= 0;
        }

	String [] lastPage = new String[10];
	for(int d=0; d<lastPage.length; d++) {
           lastPage[d]= "";
        }
	lastPage[0] = pages1_number;

	int depth = 1;
	boolean subPageFound = false;
while((depth>0||subPageFound)&&depth<10) { 
	subPageFound = false;
	%>
      <mm:list nodes="<%= lastPage[depth-1] %>" path="pages1,posrel,pages2" searchdir="destination" orderby="posrel.pos" directions="UP" max="1" offset="<%= ""+ offset[depth] %>">
	<mm:field name="pages2.number" jspvar="pages2_number" vartype="String" write="false">
	<%	
	subPageFound= true; 
	offset[depth]++;
	lastPage[depth] = pages2_number;
	depth ++;

	String pageTree = "" + offset[1];
	int d = 2;
	while(d<depth) {
		pageTree += "_" + (offset[d]);
		d++;
	}
			
	%>
<% // had to use the following loop because mm:countrelations has no searchdir: MM: aaarchch it has in 1.7 %>
        <% int numberOfSubPages=0; %>
	<mm:node element="pages2"><mm:relatednodes type="pages" role="posrel" searchdir="destination"><% numberOfSubPages++; %></mm:relatednodes></mm:node>
      Menu<%= pageTree %> = new Array("<mm:field name="pages2.title" />","<mm:url escapeamps="false" page="/index.jsp?portal=$portal"><mm:param name="page"><%= pages2_number %></mm:param></mm:url>","",<%= numberOfSubPages %>,26,142);
	     <mm:compare referid="page" value="<%= pages2_number %>">
	      <% for(int c=0; c<d; currentPath.add(lastPage[c++])) ;
		%>
	     </mm:compare>
	  </mm:field>
        </mm:list>
        <%
	if(!subPageFound) { // go one layer back
		offset[depth] = 0;
		depth--; 
	}
} 
if(currentPath.size()==0) { // curent page not found in the path from homepage, so the current page will be the homepage
	currentPath.add(pages1_number);
}
%></mm:field
></mm:related>
</mm:node>
<%--

********* add blanks to navigation ****************
--%><% int p = numberofpages; 
%><% for(int i=0; i<numberofblanks; i++) { 
	p++; %>
Menu<%= p %> = new Array("","","",0,26,142);
<% }

%><%--

********* add portals to navigation (exclude selected portal) ****************
--%><mm:compare referid="portal" value="home" inverse="true"
        ><mm:node number="home"
	><mm:field name="number" id="thisportal" write="false"
		><mm:compare referid="portal" value="$thisportal" inverse="true"
		><% p++; %>
Menu<%= p %> = new Array("<mm:field name="name"/>","/index.jsp?portal=<mm:field name="number"/>","",0,26,142);
		</mm:compare
	></mm:field
	><mm:remove referid="thisportal"
	/></mm:node
></mm:compare
><mm:list nodes="home" path="portals1,posrel,portals2"
	searchdir="destination" orderby="posrel.pos" directions="UP"
		><mm:field name="portals2.number" id="thisportal" write="false"
			><mm:compare referid="portal" value="$thisportal" inverse="true"
			><% p++; %>
Menu<%= p %> = new Array("<mm:field name="portals2.name" 
	/>","<mm:url page="/index.jsp"><mm:param name="portal"><mm:field name="portals2.number"/></mm:param></mm:url>","",0,26,142);
		</mm:compare
	></mm:field
><mm:remove referid="thisportal"
/></mm:list>

</script>
<script type="text/javascript" src="/scripts/menu_com.js" language="javascript"></script>
<%-- <script language="JavaScript"><%@include file="/scripts/menu_com.js"%></script> --%>

<noscript>Your browser does not support javascripts</noscript>
