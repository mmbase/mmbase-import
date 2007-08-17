<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<!-- TODO Make the editbox appear small and large -->

<!-- TODO Check for styles -->

<!-- TODO What if layout isn't 0 or 1. Log this situation? -->

<!-- TODO Is there always one open answer given or more? -->



<mm:node number="$question" jspvar="qq">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2></h2>
    </mm:compare>
  </mm:field>

  <p/>

  
  
<%
String proba=(String) request.getAttribute("number");
//System.out.println("proba="+proba);
//System.out.println("number;"+qq.getNumber());
//System.out.println("type:"+qq.getFieldValue("otype"));
//System.out.println("owner:"+qq.getFieldValue("owner"));
// mapa parametara vezanih za pitanje
java.util.Map varMap = new java.util.HashMap();
org.mmbase.bridge.NodeList l = qq.getRelatedNodes("variables");
java.util.Iterator itr = l.iterator();
while (itr.hasNext()) {
	org.mmbase.bridge.Node v = (org.mmbase.bridge.Node)itr.next(); 
    varMap.put("[%"+v.getValue("name")+"%]", v.getValue("value"));
}
//System.out.println("  varMap=" + varMap);
	
// description form question

String desc = qq.getStringValue("description");

// repalce
java.util.Iterator itr1 = varMap.keySet().iterator();
while (itr1.hasNext()) {
	Object key = itr1.next();
	String val = (String)varMap.get(key);
    String keyString=(String) key;
    //System.out.println("keyString="+keyString);
    //System.out.println("val="+val);
    
    String forChange="[{][%]["+keyString+"][%][}]";
	desc = desc.replaceAll(forChange, val);
    //System.out.println(desc);
	   
    
}

%>
 
  <i><%=desc%></i>

 

  <p/>

  <mm:field name="text" escape="none"/>

  <p/>
  




  <mm:import id="layout"><mm:field name="layout"/></mm:import>



  <%-- Generate large input field --%>

  <mm:compare referid="layout" value="0">

    <input type="text" size="500" name="<mm:write referid="question"/>"/>

    <br/>

  </mm:compare>



  <%-- Generate small input field --%>

  <mm:compare referid="layout" value="1">

    <input type="text" size="100" name="<mm:write referid="question"/>"/>

    <br/>

  </mm:compare>
  
 <link rel="icon" href="/mmbase/style/images/favicon.ico" type="image/x-icon" />  


</mm:node>

</mm:cloud>

</mm:content>
