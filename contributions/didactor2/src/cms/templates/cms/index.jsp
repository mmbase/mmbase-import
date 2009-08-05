<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="page" required="true"/>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><mm:node referid="page"><mm:field name="title"/></mm:node></title>
  </mm:param>
</mm:treeinclude>


<div class="rows">

<div class="navigationbar">
<div class="titlebar">
</div>
</div>

<div class="folders">

<div class="folderHeader">
</div>

<div class="folderBody">
<mm:node referid="provider">
	<mm:relatednodes role="posrel" type="pages" orderby="posrel.pos">
	    <mm:field name="number" id="menupage" write="false">
			<mm:treeinclude page="/cms/pagemenuitem.jsp" objectlist="$includePath" referids="page,menupage,$referids"/>
		</mm:field>
	</mm:relatednodes>
</mm:node>
</div>
</div>

<div class="mainContent">
<mm:node referid="page">
  <div class="contentHeader">
	<mm:field name="name"/>
  </div>

  <div class="contentSubHeader">

  </div>

  <div class="contentBodywit">

<mm:import id="layout"><mm:field name="layout"/></mm:import>
<mm:import id="imagelayout"><mm:field name="imagelayout"/></mm:import>

    <mm:field name="showtitle">
       <mm:compare value="1">
          <h1> <mm:field name="name"/></h1>
       </mm:compare>
    </mm:field>





  <mm:import jspvar="text" reset="true"><mm:field name="text" escape="none"/></mm:import>
 
  <table width="100%" border="0" class="Font">

  

  <mm:compare referid="layout" value="0">

  <tr><td width="50%"><%@include file="/shared/cleanText.jsp"%></td></tr>

  <tr><td><%@include file="/education/pages/images.jsp"%></td></tr>

  </mm:compare>

  <mm:compare referid="layout" value="1">

  <tr><td  width="50%"><%@include file="/education/pages/images.jsp"%></td></tr>

  <tr><td><%@include file="/shared/cleanText.jsp"%></td></tr>

  </mm:compare>

  <mm:compare referid="layout" value="2">

  <tr><td><%@include file="/shared/cleanText.jsp"%></td>

      <td><%@include file="/education/pages/images.jsp"%></td></tr>

  </mm:compare>

  <mm:compare referid="layout" value="3">

  <tr><td><%@include file="/education/pages/images.jsp"%></td>

      <td><%@include file="/shared/cleanText.jsp"%></td></tr>

  </mm:compare>

 

  </table>

 

    <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos">

      <h3><mm:field name="title"/></h3>

     <p>

      <i><mm:field name="description" escape="inline"/></i><br>

      <a href="<mm:attachment/>"><img src="<mm:treefile page="/education/gfx/attachment.gif" objectlist="$includePath" />" border="0" title="Download" alt="Download <mm:field name="title"/>"></a>

    </p>

    </mm:relatednodes>



  <div class="audiotapes">

    <mm:relatednodes type="audiotapes" role="posrel" orderby="posrel.pos">

        <h3><mm:field name="title"/></h3>

      <p>



        <i><mm:field name="subtitle"/></i>

      </p>

      <i><mm:field name="intro" escape="p"/></i>

      <p>

      <mm:field name="body" escape="inline"/><br>

      <a href="<mm:field name="url" />"><img src="<mm:treefile page="/education/gfx/audio.gif" objectlist="$includePath" />" border="0" title="Beluister" alt="Beluister <mm:field name="title" />"></a></b>

      </p>

    </mm:relatednodes>

  </div>



  <div class="videotapes">

    <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">

      <p>

        <h3><mm:field name="title"/></h3>

        <i><mm:field name="subtitle"/></i>

      </p>

      <i><mm:field name="intro" escape="p"/></i>

      <p>

      <mm:field name="body" escape="inline"/><br>

     <a href="<mm:field name="url" />"><img src="<mm:treefile page="/education/gfx/video.gif" objectlist="$includePath" />" border="0" title="Bekijk" alt="Bekijk <mm:field name="title" />"></a>

      </p>

    </mm:relatednodes>

  </div>



  <div class="urls">

    <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">

      <mm:field name="showtitle">
        <mm:compare value="1">
          <h3><mm:field name="name"/></h3>
        </mm:compare>
      </mm:field>

      <p>

      <i><mm:field name="description" escape="inline"/></i><br/>

      <a href="<mm:field name="url"/>" target="_blank"><mm:field name="url"/></a>

      </p>

    </mm:relatednodes>

  </div>

  </div>
</mm:node>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
