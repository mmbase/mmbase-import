<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.HashMap"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="LEARNENVIRONMENTTITLE" /></title>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  </mm:param>
</mm:treeinclude>

<mm:node number="component.drm" notfound="skip">
    <mm:treeinclude page="/drm/testlicense.jsp" objectlist="$includePath" referids="$referids "/>
</mm:node>

<mm:import externid="learnobject" jspvar="learnObject"/>
<mm:import externid="learnobjecttype" jspvar="learnObjectType"/>
<mm:import jspvar="educationNumber"><mm:write referid="education"/></mm:import>
<%
    if (educationNumber != null && educationNumber.length() > 0) {
        session.setAttribute("lasteducation",educationNumber);
    }
    else {
        educationNumber = (String) session.getAttribute("lasteducation");
    }
    if (educationNumber != null && educationNumber.length() > 0) {
        HashMap bookmarks = (HashMap) session.getAttribute("educationBookmarks");
        if (bookmarks== null) {
            bookmarks = new HashMap();
            session.setAttribute("educationBookmarks",bookmarks);
        }
        if (learnObject != null && learnObject.length() > 0) {
            bookmarks.put(educationNumber+",learnobject",learnObject);
        }
        else {
            learnObject = (String) bookmarks.get(educationNumber+",learnobject");
            System.err.println("read "+educationNumber+",learnobject="+learnObject+" from session");
            if (learnObject != null) {
                %><mm:import id="learnobject" reset="true"><%= learnObject %></mm:import><%
            }
        }
        if (learnObjectType != null && learnObjectType.length() > 0) {
            bookmarks.put(educationNumber+",learnobjecttype",learnObjectType);
        }
        else  {
            learnObjectType = (String) bookmarks.get(educationNumber+",learnobjecttype");
            if (learnObjectType != null) { 
                %><mm:import id="learnobjecttype" reset="true"><%= learnObjectType %></mm:import><%
            }       
        }
        %><mm:import id="education" reset="true"><%= educationNumber %></mm:import><%
    }
%>
    
<!-- TODO some learnblocks/learnobjects may not be visible because the are not ready for elearning (start en stop mmevents) -->
<!-- TODO when refreshing the page (F5) the old iframe content is shown -->
<!-- TODO pre and postassessment are showed in the tree -->
<!-- TODO split index and tree code in two seperate jsp templates -->
<mm:import id="gfx_item_none"><mm:treefile page="/gfx/spacer.gif" objectlist="$includePath" referids="$referids" /></mm:import>
<mm:import id="gfx_item_opened"><mm:treefile page="/gfx/icon_arrow_tab_open.gif" objectlist="$includePath" referids="$referids" /></mm:import>
<mm:import id="gfx_item_closed"><mm:treefile page="/gfx/icon_arrow_tab_closed.gif" objectlist="$includePath" referids="$referids" /></mm:import>
<script type="text/javascript">
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
      case "educations":
	  
	//    note that document.content is not supported by mozilla! 
	//    so use frames['content'] instead
	  
        frames['content'].location.href='<mm:treefile page="/education/educations.jsp" objectlist="$includePath" referids="$referids"/>'+'&edu='+number;
        break;
      case "learnblocks":
        frames['content'].location.href='<mm:treefile page="/education/learnblocks/index.jsp" objectlist="$includePath" referids="$referids"/>'+'&learnobject='+number;
        break;
      case "tests":
        frames['content'].location.href='<mm:treefile page="/education/tests/index.jsp" objectlist="$includePath" referids="$referids"/>'+'&learnobject='+number;
        break;
      case "pages":
        frames['content'].location.href='<mm:treefile page="/education/pages/index.jsp" objectlist="$includePath" referids="$referids"/>'+'&learnobject='+number;
        break;
      case "flashpages":
        frames['content'].location.href='<mm:treefile page="/education/flashpages/index.jsp" objectlist="$includePath" referids="$referids"/>'+'&learnobject='+number;
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
</script>
<div class="rows">
  <div class="navigationbar">
    <div class="pathbar">
    <mm:node number="$education">
      <mm:field name="name"/>
	</div>
	<div class="stepNavigator">
    <a href="javascript:previousContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_last.gif" objectlist="$includePath" />" width="14" height="14" border="0" alt="vorige" /></a>
	<a href="javascript:previousContent();" class="path">vorige</a><img src="gfx/spacer.gif" width=15 height=1 alt="" /><a href="javascript:nextContent();" class="path">volgende</a>
    <a href="javascript:nextContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" width="14" height="14" border="0" alt="volgende" /></a>
    </mm:node>
	</div>
  </div>
<div class="folders">
  <div class="folderHeader">
    <fmt:message key="EDUCATION" />
  </div>
  <div class="folderLesBody">
<mm:node number="$education" notfound="skip">
  <script>
  javascript:addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
  </script>
  <img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" id="img<mm:field name="number"/>" onclick="openClose('div<mm:field name="number"/>','img<mm:field name="number"/>')" />
  <a href="javascript:openContent( '<mm:nodeinfo type="type"/>','<mm:field name="number"/>' ); openOnly('div<mm:field name="number"/>','img<mm:field name="number"/>');"><mm:field name="name"/></a> 
  <mm:import id="previousnumber"><mm:field name="number"/></mm:import>
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up" maxdepth="15">
      <%-- TODO here... to continue... How to implement --%>
      <%-- Determine if the learnobject is active or not --%>
    
      <mm:import id="learnobjectnumber"><mm:field name="number"/></mm:import>
      
  
      <mm:relatednodescontainer type="mmevents" id="my_mmevents">
        <mm:time time="now" id="currenttime" write="false"/>
	    <mm:constraint field="start" value="$currenttime" operator="LESS_EQUAL"/>
	    <mm:constraint field="stop" value="$currenttime" operator="GREATER_EQUAL"/>
	  <%-- TODO here... to continue --%>
      <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>
      <mm:grow>
        <div id="div<mm:write referid="previousnumber"/>" class="lbLevel<mm:depth/>">
        <mm:compare referid="nodetype" valueset="educations,learnblocks,tests,pages,flashpages,preassessments,postassessments">
          <script type="text/javascript">
            document.getElementById("img<mm:write referid="previousnumber" />").setAttribute("haschildren", 1);
          </script>
        </mm:compare>
        <mm:onshrink></div></mm:onshrink>
      </mm:grow>
      <mm:remove referid="previousnumber"/>
      <mm:import id="previousnumber"><mm:field name="number"/></mm:import>
      <mm:compare referid="nodetype" valueset="educations,learnblocks,tests,pages,flashpages,preassessments,postassessments">
        <mm:import jspvar="depth" vartype="Integer"><mm:depth /></mm:import>
        <div style="padding: 0px 0px 0px <%= 18 + depth.intValue() * 8 %>px;"><script>
        addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
        </script><img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" id="img<mm:field name="number"/>" onclick="openClose('div<mm:field name="number"/>','img<mm:field name="number"/>')" style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px"/><a href="javascript:openContent( '<mm:nodeinfo type="type"/>', '<mm:field name="number"/>' ); openOnly('div<mm:field name="number"/>','img<mm:field name="number"/>');" style="padding-left: 0px"><mm:field name="name"/></a>
          <mm:node number="component.pop" notfound="skip">
            <mm:relatednodes type="providers" constraints="providers.number=$provider">
              <mm:list nodes="$user" path="people,related,pop">
                <mm:first><%@include file="popcheck.jsp" %></mm:first>
              </mm:list>
            </mm:relatednodes>
          </mm:node> 
        </div>
      </mm:compare>
      <mm:shrink/>
      </mm:relatednodescontainer>
    </mm:tree>
  </mm:relatednodescontainer>
  </div>
</div>
<div class="mainContent">
  <div class="contentHeader">
    &nbsp;
  </div>
  <div class="contentBodywit">
	<iframe width="100%" height="100%" name="content" frameborder=0></iframe>
  </div>
</div>
</div>
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
</mm:node>
<br>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids "/>
</fmt:bundle>
</mm:cloud>
</mm:content>
