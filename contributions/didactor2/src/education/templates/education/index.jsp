<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
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

<mm:import externid="learnobject"/>
<mm:import externid="learnobjecttype"/>

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
    if (realdiv != null) {
        realdiv.setAttribute("opened", "1");
        realdiv.style.display = "block";
        realimg.src = ITEM_OPENED;
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
  <br/>

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
        <%
          for (int i=2; i<depth.intValue(); i++) {
        %>
          <img src="<mm:write referid="gfx_item_none" />" />
        <%
          }
        %>

        <script>
        addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
        </script>

        <img class="imgClosed" src="<mm:write referid="gfx_item_closed" />" id="img<mm:field name="number"/>" onclick="openClose('div<mm:field name="number"/>','img<mm:field name="number"/>')" />

          <a href="javascript:openContent( '<mm:nodeinfo type="type"/>', '<mm:field name="number"/>' ); openOnly('div<mm:field name="number"/>','img<mm:field name="number"/>');"><mm:field name="name"/></a> 
        <br/>
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
</script>

</mm:node>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids "/>
</fmt:bundle>
</mm:cloud>
</mm:content>
