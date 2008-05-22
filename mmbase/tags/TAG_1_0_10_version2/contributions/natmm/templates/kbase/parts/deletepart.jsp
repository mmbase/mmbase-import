  <%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  <%@include file="basics.jsp"%>
  <mm:cloud jspvar="wolk" method="asis" >
  <mm:import externid="kb_submit" />
  <mm:import externid="expanded"      jspvar="expanded" vartype="String"/>
  <mm:import externid="node"          jspvar="node" vartype="String"/>
  <mm:import externid="qnode"         jspvar="qnode" vartype="String"/>
  <mm:import externid="anode"         jspvar="anode" vartype="String"/>
  <mm:import externid="action"        jspvar="action" vartype="String" />
  <mm:import externid="type"          jspvar="type" vartype="String" />
    
  
  <div class="form">
    <mm:present referid="anode">
       <mm:deletenode number="$anode" deleterelations="true">
         Het volgende antwoord  is verwijdert:<br/><br/>
         <mm:field name="answer" />
       </mm:deletenode>   
    </mm:present>
    <mm:present referid="qnode">
       <mm:deletenode number="$qnode" deleterelations="true">
         De volgende vraag  is verwijdert:<br/><br/>
         <mm:field name="question" />
       </mm:deletenode>   
    </mm:present>
    <br/><br/><a href="/kbase/index.jsp">Klik hier om de pagina te vernieuwen</a>
  </div>
  </mm:cloud>
