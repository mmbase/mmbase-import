<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="learnobject" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<!-- TODO Where to display images, audiotapes, videotapes and urls -->
<!-- TODO How to display objects -->

<html>
<head>
   <title>Test Feedback</title>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
</head>
<body>
<mm:node number="$learnobject">
  <h2> <mm:field name="name"/></h2>

  <mm:field name="text" escape="none"/>
  <p/>

  <div class="images">
    <mm:relatednodes type="images">
      <mm:field name="title"/>
      <br/>
      <mm:field name="description"/>
      <br/>
      <img src="<mm:image />" width="100" border="0" />
    </mm:relatednodes>
  </div>

  <div class="audiotapes">
    <mm:relatednodes type="audiotapes">
      <mm:field name="title"/>
      <br/>
      <mm:field name="subtitle"/>
      <br/>
      <mm:field name="playtime"/>
      <br/>
      <mm:field name="intro"/>
      <br/>
      <mm:field name="body"/>
      <br/>
      <a href="<mm:field name="url" />"><mm:field name="title" /></a>
      <br/>
    </mm:relatednodes>
  </div>

  <div class="videotapes">
    <mm:relatednodes type="videotapes">
      <mm:field name="title"/>
      <br/>
      <mm:field name="subtitle"/>
      <br/>
      <mm:field name="playtime"/>
      <br/>
      <mm:field name="intro"/>
      <br/>
      <mm:field name="body"/>
      <br/>
      <a href="<mm:field name="url" />"><mm:field name="title" /></a>
      <br/>
    </mm:relatednodes>
  </div>

  <div class="urls">
    <mm:relatednodes type="urls">
      <a href="<mm:field name="url" />"><mm:field name="name"/></a>(<mm:field name="url"/>)
      <br/>
      <mm:field name="description"/>
      <br/>
    </mm:relatednodes>
  </div>
</mm:node>

</body>
</html>

</mm:cloud>
</mm:content>
