<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@ page  contentType="application/xml;charset=UTF-8"
%>
<mm:content
type="application/xml"
postprocessor="reducespace">
<div class="content">
<mm:cloud rank="didactor user">
  <mm:import externid="learnobject" required="true"/>
  <di:event eventtype="visit_page" eventvalue="${learnobject}" note="visit page"/>
  <jsp:directive.include file="/shared/setImports.jsp" />
  <!-- TODO Where to display images, audiotapes, videotapes and urls -->
  <!-- TODO How to display objects -->
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
      <mm:hasrelationmanager sourcemanager="$_node" destinationmanager="components" role="rolerel">            
        <jsp:directive.include file="../includes/component_link.jsp" />
      </mm:hasrelationmanager>
    </mm:node>
    
    
    <mm:treeinclude page="/education/prev_next.jsp" referids="includePath,provider" objectlist="$includePath" />
    
  </div>
</mm:cloud>
</div>
</mm:content>

