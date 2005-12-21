<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="cmshelp">
    <option value="cms" selected="selected"><di:translate key="cms.cms" /></option>
</mm:compare>
<mm:compare referid="search_component" value="cms" inverse="true">
    <option value="cms"><di:translate key="cms.cms" /></option>
</mm:compare>
