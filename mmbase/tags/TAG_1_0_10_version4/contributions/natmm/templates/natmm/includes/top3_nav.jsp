<!-- <%= new java.util.Date() %> -->
<script type="text/javascript" src="scripts/milonic_src.js"></script>
<param copyright="JavaScript Menu by Milonic" value="http://www.milonic.com/"></param>
<script	type="text/javascript">
if(ns4)_d.write("<scr"+"ipt language=JavaScript src=scripts/mmenuns4.js><\/scr"+"ipt>");		
  else _d.write("<scr"+"ipt language=JavaScript src=scripts/mmenudom.js><\/scr"+"ipt>"); 
</script>
<SCRIPT language=JavaScript type=text/javascript>

horizontalMenuDelay = true; 

_menuCloseDelay=500;
_menuOpenDelay=350;
_scrollAmount=3;
_scrollDelay=20;
_followSpeed=5;
_followRate=40;
_subOffsetTop=10;
_subOffsetLeft=-5;
with(style1=new mm_style()){
   offcolor="#FFFFFF";
   offbgcolor="";
   oncolor="#FFFFFF";
   onbgcolor="";
   bordercolor="";
   borderstyle="";
   borderwidth=0;
   fontsize=11;
   fontstyle="normal";
   fontweight="bold";
   fontfamily="Arial";
   padding=2;
   pagebgcolor="";
   headercolor="";
   headerbgcolor="";
   align="center";
}
with(style2=new mm_style()){
   styleid=1;
   bordercolor="#<%= NatMMConfig.color2[iRubriekStyle] %>";
   borderstyle="solid";
   borderwidth=1;
   fontsize=12;
   fontstyle="normal";
   fontweight="normal";
   fontfamily="Arial";
   padding=1;
   pagebgcolor="#<%= NatMMConfig.color2[iRubriekStyle] %>";
   headercolor="#FFFFFF";
   headerbgcolor="#000099";
   offcolor="#050080";
   offbgcolor="#<%= NatMMConfig.color2[iRubriekStyle] %>";
   oncolor="#050080";
   onbgcolor="#FFFFFF";
}
<% 


TreeMap [] nodesAtLevel = new TreeMap[10];
nodesAtLevel[0] = new TreeMap();
nodesAtLevel[0].put(new Integer(0),subsiteID);

int depth = 0;
int top = 0;
if(iRubriekLayout==NatMMConfig.SUBSITE1_LAYOUT || iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT) {
	top = 76;
} else if (iRubriekLayout==NatMMConfig.DEMO_LAYOUT ) {
	top = 53;
}
// invariant: depth = level of present leafs (root has level 0)
while(depth>-1&&depth<10) { 
   
   String lastSubObject = "";
   if(nodesAtLevel[depth].isEmpty()) {
   
      // *** if this level is empty, try one level back ***
      depth--; 
   }
   if(depth>-1&&!nodesAtLevel[depth].isEmpty()) {
   
      // *** take next rubriek of highest level ***
      Integer firstKey = (Integer) nodesAtLevel[depth].firstKey();
      lastSubObject =  (String) nodesAtLevel[depth].get(firstKey);
      nodesAtLevel[depth].remove(firstKey);
      depth++;
      
      nodesAtLevel[depth] = (TreeMap) rubriekHelper.getSubObjects(lastSubObject);
      if(depth==1) { 
         %>
         with(milonic=new menuname("mainmenu")){
         top=<%= top %>;
         screenposition="center";
         style=style1;
         alwaysvisible=1;
         alignment="center";
         orientation="horizontal";
         <%  
      } else { 
         %>
         with(milonic=new menuname("r<%= lastSubObject %>")){
         itemwidth=150;
         style=style2;
         alignment="center";
         <%         
      }
      
      // *** show all subObjects, both pages and rubrieken
      TreeMap thisSubObjects = (TreeMap) nodesAtLevel[depth].clone();
      while(!thisSubObjects.isEmpty()) { 
      
         Integer thisKey = (Integer) thisSubObjects.firstKey();
         String sThisObject = (String) thisSubObjects.get(thisKey);
         thisSubObjects.remove(thisKey);
			
         %><mm:node number="<%= sThisObject %>" jspvar="thisObject"
            ><mm:nodeinfo  type="type" write="false" jspvar="nType" vartype="String"><%
               
               if(nType.equals("pagina")){ // *** show page
               
                  String sObjectTitle = thisObject.getStringValue("titel");   
                  if(depth==1) { sObjectTitle = "&nbsp;&nbsp;" + sObjectTitle + "&nbsp;&nbsp;"; } 
                  %>aI("text=<%= sObjectTitle %>;;url=<%= ph.createPaginaUrl(sThisObject,request.getContextPath()) %>;separatorsize=1")
                  <%
                  // *** object not longer needed
                  nodesAtLevel[depth].remove(thisKey);
                  
               } else { // *** show rubriek, which is a link to the first page in the rubriek
                  
                  String nextPage =  rubriekHelper.getFirstPage(sThisObject);
                  String sObjectTitle = thisObject.getStringValue("naam");   
                  if(depth==1) { sObjectTitle = "&nbsp;&nbsp;" + sObjectTitle + "&nbsp;&nbsp;"; } 
                  %>aI("text=<%= sObjectTitle %>;showmenu=r<%= sThisObject %>;url=<%= ph.createPaginaUrl(nextPage,request.getContextPath()) %>;separatorsize=1")
                  <%
               } 
            %></mm:nodeinfo
         ></mm:node><%
      }
   %>
   }
   <%
   }
} 
%>

drawMenus();
</SCRIPT>
