<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<html>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

migrating portfolio<br/>

<mm:listnodes type="components" constraints="name = 'portfolio'" max="1">
  <mm:import id="component_found">true</mm:import>
</mm:listnodes>

<mm:present referid="component_found" inverse="true">
  <mm:createnode type="components">
    creating components node<br/>
    <mm:setfield name="name">portfolio</mm:setfield>
    <mm:setfield name="classname">nl.didactor.component.portfolio.DidactorPortfolio</mm:setfield>
  </mm:createnode>
</mm:present>

<mm:listnodes type="people" id="person">
  <mm:import id="portfolios_found">false</mm:import>
  <mm:relatednodes type="portfolios" id="portfolio" constraints="m_type=1">
    <mm:relatednodes type="folders" constraints="folders.name != 'Assessment'">
        <mm:deletenode deleterelations="true"/>
    </mm:relatednodes>
    <mm:remove referid="assessmentfolder"/>
    <mm:relatednodes type="folders" constraints="folders.name = 'Assessment'" max="1">
        <mm:import id="assessmentfolder" reset="true"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
    <mm:notpresent referid="assessmentfolder">
      <mm:createnode type="folders" id="assessmentfolder">
          <mm:setfield name="name">Assessment</mm:setfield>
      </mm:createnode>
      <mm:createrelation source="portfolio" destination="assessmentfolder" role="posrel">
          <mm:setfield name="pos">1</mm:setfield>
      </mm:createrelation>
    </mm:notpresent>
  </mm:relatednodes>
  
  </mm:listnodes>

done<br/>

</mm:cloud>
</mm:content>
</html>
