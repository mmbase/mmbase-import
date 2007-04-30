<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ page import = "java.io.*,java.util.*" 
%><mm:content postprocessor="reducespace">
<mm:cloud method="delegate">
  <mm:import externid="learnobject" required="true"/>
  <!-- TODO Need this page? -->
  <jsp:directive.include file="/shared/setImports.jsp" />

  <%-- remember this page --%>
  <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids,learnobject">
    <mm:param name="learnobjecttype">learnblocks</mm:param>
  </mm:treeinclude>
  
  <html>
    <head>
      <title>Learnblock content</title>
      <link rel="stylesheet" type="text/css" href="${mm:treefile('/css/base.css', pageContext, includePath)}" />
    </head>
    <body>
    <div class="learnenvironment">
      <mm:node number="$learnobject">
        <mm:nodeinfo type="type">
            <!-- 
                 It's a bit ugly that SCORM specify code occurs here.
                 But listen, earlier this file was nearly _completely_ dedicated to scorm stuff....
            -->

          <c:choose>
            <c:when test="${_ eq 'htmlpages'}">
              <c:if test="${! empty _node.path}">
                <mm:hasnode number="component.scorm" >
                  <mm:include page="/scorm/player/index.jspx" referids="learnobject@node" />
                </mm:hasnode>
                <mm:hasnode number="component.scorm" inverse="true">
                  <di:translate key="scorm.you_have_to_turn_on_the_scorm_module" />
                </mm:hasnode>
              </c:if>
              <mm:field name="content" escape="none" />              
            </c:when>
            <c:otherwise>
              <mm:treeinclude page="/education/pages/content.jsp" objectlist="$includePath" referids="$referids,learnobject" />
              <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids,learnobject@node_id">
                <mm:param name="path_segment">../</mm:param>            
              </mm:treeinclude>
            </c:otherwise>
          </c:choose>
        </mm:nodeinfo>            
      </mm:node>
    
      <jsp:directive.include file="../includes/descriptionrel_link.jsp" />      
      <mm:treeinclude page="/education/prev_next.jsp" referids="includePath" objectlist="$includePath" />
      
    </div>
    <mm:node number="$learnobject">
      <jsp:directive.include file="../includes/component_link.jsp" />
    </mm:node>
    
    </body>
  </html>
</mm:cloud>
</mm:content>
