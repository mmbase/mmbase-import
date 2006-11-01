<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
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
  <mm:relatednodes type="portfolios" max="1">
    <mm:import id="portfolios_found" reset="true">true</mm:import>
  </mm:relatednodes>
  <mm:compare referid="portfolios_found" value="true" inverse="true">
    adding portfolios to user <mm:field name="username"/> (node number <mm:field name="number"/>)<br/>
    <mm:createnode type="portfolios" id="development_portfolio">
      <mm:setfield name="type">0</mm:setfield>
      <mm:setfield name="name">Development portfolio</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="person" destination="development_portfolio" role="related"/>
    <mm:createnode type="portfolios" id="assessment_portfolio">
      <mm:setfield name="type">1</mm:setfield>
      <mm:setfield name="name">Assessment portfolio</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="person" destination="assessment_portfolio" role="related"/>
    <mm:createnode type="portfolios" id="showcase_portfolio">
      <mm:setfield name="type">2</mm:setfield>
      <mm:setfield name="name">Showcase portfolio</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="person" destination="showcase_portfolio" role="related"/>
    <mm:createnode type="folders" id="folder0">
      <mm:setfield name="name">OER PTA</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder0" role="posrel">
      <mm:setfield name="pos">0</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder1">
      <mm:setfield name="name">EVC</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder1" role="posrel">
      <mm:setfield name="pos">1</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder2">
      <mm:setfield name="name">Studiewijzers</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder2" role="posrel">
      <mm:setfield name="pos">2</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder3">
      <mm:setfield name="name">Cijferlijst</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder3" role="posrel">
      <mm:setfield name="pos">3</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder4">
      <mm:setfield name="name">Bijlagen</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder4" role="posrel">
      <mm:setfield name="pos">4</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder5">
      <mm:setfield name="name">Beroepshouding</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder5" role="posrel">
      <mm:setfield name="pos">5</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder6">
      <mm:setfield name="name">BPV</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder6" role="posrel">
      <mm:setfield name="pos">6</mm:setfield>
    </mm:createrelation>
    <mm:createnode type="folders" id="folder7">
      <mm:setfield name="name">Intake</mm:setfield>
    </mm:createnode>
    <mm:createrelation source="assessment_portfolio" destination="folder7" role="posrel">
      <mm:setfield name="pos">7</mm:setfield>
    </mm:createrelation>
  </mm:compare>
</mm:listnodes>

done<br/>

</mm:cloud>
</mm:content>
</html>
