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
          <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids,learnobject@node_it">
            <mm:param name="path_segment">../</mm:param>
          </mm:treeinclude>
        </mm:node>
        <jsp:directive.include file="../includes/descriptionrel_link.jsp" />

        <mm:node number="$learnobject" jspvar="nodeLearnObject">
          <jsp:directive.include file="../includes/component_link.jsp" />
        </mm:node>

      </div>
    </body>
  </html>
</mm:cloud>
</mm:content>

