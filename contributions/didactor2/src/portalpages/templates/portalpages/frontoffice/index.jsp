<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.HashMap"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud">

  <%@include file="/shared/setImports.jsp" %>
  <fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <mm:import id="gfx_item_none"><mm:treefile page="/gfx/spacer.gif" objectlist="$includePath" referids="$referids" /></mm:import>
  <mm:import id="gfx_item_opened"><mm:treefile page="/gfx/icon_arrow_tab_open.gif" objectlist="$includePath" referids="$referids" /></mm:import>
  <mm:import id="gfx_item_closed"><mm:treefile page="/gfx/icon_arrow_tab_closed.gif" objectlist="$includePath" referids="$referids" /></mm:import>
  <script type="text/javascript">
<!-- 
  var ITEM_NONE = "<mm:write referid="gfx_item_none" />";
  var ITEM_OPENED = "<mm:write referid="gfx_item_opened" />";
  var ITEM_CLOSED = "<mm:write referid="gfx_item_closed" />";
  var currentnumber = -1;
  var contenttype = new Array();
  var contentnumber = new Array();
  
  function addContent( type, number ) {
    contenttype[contenttype.length] = type;
    contentnumber[contentnumber.length] = number;
    if ( contentnumber.length == 1 ) {
      currentnumber = contentnumber[0];
    }
  }
  
  function nextContent() {
    for(var count = 0; count <= contentnumber.length; count++) {
  	  if ( contentnumber[count] == currentnumber ) {
  	    if ( count < contentnumber.length ) {
  	      var opentype = contenttype[count+1];
  	      var opennumber = contentnumber[count+1];
  	    }
  	  }
  	}
  	openContent( opentype, opennumber );
    openOnly('div'+opennumber,'img'+opennumber);
  }
  
  function previousContent() {
    for(var count = 0; count <= contentnumber.length; count++) {
  	  if ( contentnumber[count] == currentnumber ) {
  	    if ( count > 0 ) {
  	      var opentype = contenttype[count-1];
  	      var opennumber = contentnumber[count-1];
  	    }
  	  }
  	}
    openContent( opentype, opennumber );
    openOnly('div'+opennumber,'img'+opennumber);
  }
  
  function openContent( type, number ) {
    if ( number > 0 ) {
      currentnumber = number;
    }
    switch ( type ) {
      case "portalpagesnodes":
	  
	//    note that document.content is not supported by mozilla! 
	//    so use frames['content'] instead
	  
        frames['content'].location.href='<mm:treefile page="/portalpages/frontoffice/show_node.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
        break;
      case "simplecontents":
        frames['content'].location.href='<mm:treefile page="/portalpages/frontoffice/show_content.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
        break;
    }
  }
  
  function openClose(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);
    
    if (realdiv != null) {
      if (realdiv.getAttribute("opened") == "1") {
        realdiv.setAttribute("opened", "0");
        realdiv.style.display = "none";
        realimg.src = ITEM_CLOSED;
      } else {
        realdiv.setAttribute("opened", "1");
        realdiv.style.display = "block";
        realimg.src = ITEM_OPENED;
      }
    }
  }
  
  function openOnly(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);
    // alert("openOnly("+div+","+img+"); - "+realdiv);
    if (realdiv != null) {
        realdiv.setAttribute("opened", "1");
        realdiv.style.display = "block";
        realimg.src = ITEM_OPENED;
        
        var className = realdiv.className;
        if (className) {
            // ignore "lbLevel" in classname to get the level depth
            var level = className.substring(7,className.length);
            // alert("level = "+level);
            var findparent = realdiv;
            var findparentClass = className;
            if (level > 1) {
                // also open parents
                do {
                    findparent = findparent.parentNode;
                    findparentClass = findparent.className || "";
                } while (findparent && findparentClass.indexOf("lbLevel") != 0);
                if (findparent) {
                    var divid = findparent.id;
                    var imgid = "img"+divid.substring(3,divid.length);
                    openOnly(divid,imgid);
                }
            }
        }        
    }
    else { // find enclosing div
        var finddiv = realimg;
        while (finddiv != null && (! finddiv.className || finddiv.className.substring(0,7) != "lbLevel")) {
            finddiv = finddiv.parentNode;
            // if (finddiv.className) alert(finddiv.className.substring(0,7));
        }
        if (finddiv != null) {
            var divid = finddiv.id;
            var imgid = "img"+divid.substring(3,divid.length);
            openOnly(divid,imgid);
        }
    }
  }
  
  function closeAll() {
    var divs = document.getElementsByTagName("div");
    for (i=0; i<divs.length; i++) {
      var div = divs[i];
      var cl = "" + div.className;
      if (cl.match("lbLevel")) {
        divs[i].style.display = "none";
      }
    }
  }
  
  function removeButtons() {
    // Remove all the buttons in front of divs that have no children
    var imgs = document.getElementsByTagName("img");
    for (i=0; i<imgs.length; i++) {
      var img = imgs[i];
      var cl = "" + img.className;
      if (cl.match("imgClose")) {
        if (img.getAttribute("haschildren") != "1") {
          img.src = ITEM_NONE;
        }
      }
    }
  }
  
  //-->
  </script>
  <% int openDivs = 0; %>
  <% System.out.println("\r\n\r\n"); %>
  <div class="rows">
    <div class="navigationbar">
      <div class="pathbar">
        <mm:node number="$provider">
          <mm:field name="name"/>
        </mm:node>
      </div>
   <!-- menu left previous,next (vorige,volgende) -->
      <div class="stepNavigator">
        <br/><br/>
	      <a href="javascript:previousContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_last.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="vorige" alt="vorige" /></a>
	      <a href="javascript:previousContent();" class="path">vorige</a><img src="gfx/spacer.gif" width="15" height="1" title="" alt="" /><a href="javascript:nextContent();" class="path">volgende</a>
	      <a href="javascript:nextContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="volgende" alt="volgende" /></a>
      </div>
    </div>
    
    <div class="folders">
      <div class="folderLesBody">

      <!-- get first, usually "system" tree root, it is unvisible, from PP container -->
        <mm:listnodes type="portalpagescontainers">    
          <mm:first>  
            <mm:field id="containernode" name="number" write="false"/>
          </mm:first>   
        </mm:listnodes> 
        
      <!-- get node provider -->
        <mm:node number="$containernode" notfound="skip">
          <script type="text/javascript">
            <!-- 
            addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
            //-->
          </script>
          
		      <mm:import id="previousnumber"><mm:field name="number"/></mm:import>
		      <mm:import id="presenttime"><mm:time time="now"/></mm:import>
          
          <mm:import id="previousDepth" jspvar="jsp_previousDepth" vartype="Integer" >-10</mm:import>
          
        <!-- get all related node to root from portalpages (PP) -->
		      <mm:relatednodescontainer type="portalpagesnodes" role="related">
          
          <!-- show only active pages, because this constraint will not work in tree, in below code it will not -->
          <!-- be possible to use grow and shrink (we will do this by hand) -->
		        <mm:constraint field="active" value="0"/>
            <mm:sortorder field="order_number" direction="up" />
            
  		      <mm:tree type="portalpagesnodes" role="childppnn" orderby="order_number" directions="up" searchdir="destination" maxdepth="3">
            
              <mm:import id="currentDepth" jspvar="jsp_currentDepth" vartype="Integer" ><mm:depth /></mm:import>
              <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>       
		          <mm:import id="nodenumber"><mm:field name="number"/></mm:import>
              
              <mm:node number="$nodenumber">
                <mm:import id="nodeActive"><mm:field name="active" /></mm:import>       
              </mm:node>

              <mm:compare referid="nodeActive" value="0">
              
                <mm:islessthan referid="currentDepth"  referid2="previousDepth" >
                  </div>
                  <% openDivs--; %>
                </mm:islessthan>
                
                <mm:isgreaterthan referid="currentDepth"  referid2="previousDepth" >
                  <% openDivs++; %>
                
  		            <div id="div<mm:write referid="previousnumber"/>" class="lbLevel<mm:depth/>">
  			          <script type="text/javascript">
  			            document.getElementById("img<mm:write referid="previousnumber" />").setAttribute("haschildren", 1);
  			          </script> 
                  
  		            <mm:import jspvar="depth" vartype="Integer"><mm:depth /></mm:import>
                  
  		            <mm:listcontainer path="portalpagesnodes,simplecontents">
                  
  	    	          <mm:constraint field="portalpagesnodes.number" value="${previousnumber}"/> 
  	    	          <mm:constraint value="${presenttime}" field="simplecontents.online_date" operator="LESS" />
  		    	        <mm:constraint value="${presenttime}" field="simplecontents.offline_date" operator="GREATER" />
    		    	      <mm:list>
          						<div style="padding: 0px 0px 0px <%= 6 + (depth.intValue()+1) * 8 %>px;">
                        <script type="text/javascript">
                          <!--
                          addContent('simplecontents','<mm:field name="simplecontents.number"/>');
                          //-->
                        </script>
  <!--  						        <img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" -->
  <!--                           id="img<mm:field name="simplecontents.number"/>" -->
  <!--                           onclick="openClose('div<mm:field name="simplecontents.number"/>','img<mm:field name="simplecontents.number"/>')" -->
  <!--                           style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />-->
                        <a href="javascript:openContent('simplecontents', '<mm:field name="simplecontents.number"/>' ); openOnly('div<mm:field name="simplecontents.number"/>','img<mm:field name="simplecontents.number"/>');" 
                           style="padding-left: 0px"><mm:field name="simplecontents.title"/></a>
    						      </div>
  			            </mm:list> 
    			        </mm:listcontainer> 
                  
  		          </mm:isgreaterthan>
                
  		          <mm:remove referid="previousnumber"/>
  		          <mm:import id="previousnumber"><mm:field name="number"/></mm:import>
  		          <mm:import jspvar="depth" vartype="Integer"><mm:depth /></mm:import>
                
  		          <div style="padding: 0px 0px 0px <%= 18 + depth.intValue() * 8 %>px;">
  		            <script type="text/javascript">
  				          <!--
  				          addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
  				          //-->
  				        </script>
  				        <img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" 
                       id="img<mm:field name="number"/>" onclick="openClose('div<mm:field name="number"/>','img<mm:field name="number"/>')" 
                       style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />
                  <a href="javascript:openContent('<mm:nodeinfo type="type"/>', '<mm:field name="number"/>' ); openOnly('div<mm:field name="number"/>','img<mm:field name="number"/>');" 
                     style="padding-left: 0px"><mm:field name="name"/></a>       
  		          </div>
                
                <mm:remove referid="haschildnodes"/>
                
              <!-- does this portalpage node has a subnodes -->
                <mm:listcontainer path="portalpagesnodes,childppnn,portalpagesnodes" >
  		            <mm:constraint field="portalpagesnodes.number" value="${nodenumber}"/>
  		            <mm:constraint field="portalpagesnodes.active" value="0"/>
  		            <mm:list>
       		          <mm:first><mm:import id="haschildnodes" reset="true">true</mm:import></mm:first>
  	              </mm:list>
  		          </mm:listcontainer>
                
              <!-- if there are no subnodes, there will be no subsequently grow event -->  
  		          <mm:notpresent referid="haschildnodes">
  
                <!-- list contents -->              
  		            <mm:listcontainer path="portalpagesnodes,simplecontents">
          	        <mm:constraint field="portalpagesnodes.number" value="${previousnumber}"/> 
           	        <mm:constraint value="${presenttime}" field="simplecontents.online_date" operator="LESS" />
  	    	          <mm:constraint value="${presenttime}" field="simplecontents.offline_date" operator="GREATER" />
                    
  	                <mm:list>
         			        <mm:first>
    	                  <div id="div<mm:write referid="previousnumber"/>" class="lbLevel<mm:depth/>">
    					          <script type="text/javascript">
    					            document.getElementById("img<mm:write referid="previousnumber" />").setAttribute("haschildren", 1);
    					          </script> 				        
    				          </mm:first>
    				          <div style="padding: 0px 0px 0px <%= 6 + (depth.intValue()+1) * 8 %>px;">
      						      <script type="text/javascript">
    	 		  				      <!--
    		  					      addContent('simplecontents','<mm:field name="simplecontents.number"/>');
    				  			      //-->
    					   	      </script>
  <!--                      <img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" -->
  <!--                           id="img<mm:field name="simplecontents.number"/>" -->
  <!--                           onclick="openClose('div<mm:field name="simplecontents.number"/>','img<mm:field name="simplecontents.number"/>')" -->
  <!--                           style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />-->
                        <a href="javascript:openContent('simplecontents', '<mm:field name="simplecontents.number"/>' ); openOnly('div<mm:field name="simplecontents.number"/>','img<mm:field name="simplecontents.number"/>');" 
                           style="padding-left: 0px"><mm:field name="simplecontents.title"/></a>
    				          </div>
                      
    				          <mm:last></div></mm:last>
                      
  		              </mm:list>  
  		            </mm:listcontainer>         
  		          </mm:notpresent> 
                
                <mm:remove referid="previousDepth" />
                <mm:import id="previousDepth" jspvar="jsp_previousDepth" vartype="integer" ><mm:depth /></mm:import>
              </mm:compare>
            </mm:tree>
            
          <!-- close all not closed divs -->
            <% while ( openDivs-- > 0 ) { %>
              </div>
            <% } %>
          </mm:relatednodescontainer>
      
        </mm:node>
      </div> <!-- class="folderLesBody" -->
    </div> <!-- class="folders" -->
  </div> <!--  class="rows" -->
  
  
  <script type="text/javascript">
    closeAll();
    <mm:present referid="learnobject">
			openContent('<mm:write referid="learnobjecttype"/>','<mm:write referid="learnobject"/>');
			openOnly('div<mm:write referid="learnobject"/>','img<mm:write referid="learnobject"/>');
    </mm:present>
    <mm:notpresent referid="learnobject">
        if (contentnumber.length >= 1) {
            openContent(contenttype[0],contentnumber[0]);
            openOnly('div'+contentnumber[0],'img'+contentnumber[0]);
        }       
    </mm:notpresent>
  </script>
  
  </fmt:bundle>
</mm:cloud>
</mm:content>
