<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="faq">
    <option value="faq" selected="selected"><di:translate key="faq.faq" /></option>
</mm:compare>
<mm:compare referid="search_component" value="faq" inverse="true">
    <option value="faq"><di:translate key="faq.faq" /></option>
</mm:compare>

