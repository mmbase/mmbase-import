<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">


  <%@include file="/shared/setImports.jsp" %>
  <html>
  <head>
     <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
  </head>
  <body>
  <div class="learnenvironment">
  <mm:import externid="learnobject" required="true"/>
  
  <!-- TODO show the flash animation -->
  
  
  <%-- remember this page --%>
  <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>
    <mm:param name="learnobjecttype">tips</mm:param>
  </mm:treeinclude>
  
  <mm:import externid="fb_madetest"/>
    <mm:present referid="fb_madetest">
      <mm:node number="$fb_madetest" notfound="skip">
          <mm:relatednodes type="tests">
              <mm:import id="page">/education/tests/feedback.jsp</mm:import>
              <a href="<mm:treefile page="$page" objectlist="$includePath" referids="$referids">
                           <mm:param name="tests"><mm:field name="number"/></mm:param>
                           <mm:param name="madetest"><mm:write referid="fb_madetest"/></mm:param>
                       </mm:treefile>"><di:translate key="education.backtotestresults" /></a><br/>
              <mm:remove referid="page"/>
          </mm:relatednodes>
      </mm:node>
  </mm:present>
  
  <mm:node number="$learnobject">
  
    <mm:field name="showtitle">
      <mm:compare value="1">
        <mm:field name="name" write="true"/>
      </mm:compare>
    </mm:field>
     
    <mm:field name="text" write="true"/> 
    
   
    <mm:relatednodes type="images">
      <mm:field name="showtitle">
        <mm:compare value="1">
          <h3><mm:field name="title"/></h3>
        </mm:compare>
      </mm:field>
           
     <img src="<mm:image />" width="200" border="0" align="right" />         
     <mm:field name="description" escape="none"/> 
            
    </mm:relatednodes>
      
    <mm:relatednodes type="attachments" role="related">
      <h3><mm:field name="title"/></h3>

      <p>

      <i><mm:field name="description" escape="inline"/></i><br>

      <a href="<mm:attachment/>"><img src="<mm:treefile page="/education/gfx/attachment.gif" objectlist="$includePath" />" border="0" title="Download <mm:field name="title"/>" alt="Download <mm:field name="title"/>"></a>

      </p>
    </mm:relatednodes>
           
  </mm:node>
  </div>
  </body>
  </html>
</mm:cloud>
</mm:content>
 
