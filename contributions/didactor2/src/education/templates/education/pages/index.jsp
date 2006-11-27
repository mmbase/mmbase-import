<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%>
<mm:content postprocessor="reducespace">

<mm:cloud method="delegate">
  <mm:import externid="learnobject" required="true"/>
  <di:event eventtype="visit_page" eventvalue="${learnobject}" note="visit page"/>
  <jsp:directive.include file="/shared/setImports.jsp" />
  <!-- TODO Where to display images, audiotapes, videotapes and urls -->
  <!-- TODO How to display objects -->
  <html>
    <head>
      <title>Test Feedback</title>
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
    </head>
    <body>
      <div class="learnenvironment">
        <mm:treeinclude page="/education/pages/content.jsp" objectlist="$includePath"
                        referids="$referids,learnobject" />
        <mm:node number="$learnobject">
          <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids,learnobject@node_id">
            <mm:param name="path_segment">../</mm:param>
          </mm:treeinclude>
        </mm:node>
        <jsp:directive.include file="../includes/descriptionrel_link.jsp" />

        <mm:node number="$learnobject" jspvar="nodeLearnObject">
          <jsp:directive.include file="../includes/component_link.jsp" />
        </mm:node>
        <p>

          <a href="javascript:parent.previousContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_last.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="<di:translate key="education.previous" />" alt="<di:translate key="education.previous" />" /></a>
          <a href="javascript:parent.previousContent();" class="path"><di:translate key="education.previous" /></a>

          <img src="gfx/spacer.gif" width="15" height="1" title="" alt="" /><a href="javascript:parent.nextContent();" class="path"><di:translate key="education.next" /></a>
          <a href="javascript:parent.nextContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="<di:translate key="education.next" />" alt="<di:translate key="education.next" />" /></a>
        </p>
      </div>
    </body>
  </html>
</mm:cloud>
</mm:content>

