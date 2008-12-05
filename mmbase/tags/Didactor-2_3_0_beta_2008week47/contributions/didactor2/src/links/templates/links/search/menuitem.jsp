<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
  <mm:cloud method="delegate" jspvar="cloud">

    <mm:import externid="search_component"/>

    <mm:compare referid="search_component" value="agenda">
      <option value="agenda" selected="selected"><di:translate key="agenda.agenda" /></option>
    </mm:compare>

    <mm:compare referid="search_component" value="agenda" inverse="true">
      <option value="agenda"><di:translate key="agenda.agenda" /></option>
    </mm:compare>

  </mm:cloud>
</mm:content>
