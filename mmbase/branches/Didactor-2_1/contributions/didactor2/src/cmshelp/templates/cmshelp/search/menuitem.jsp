<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="cmshelp">
    <option value="cmshelp" selected="selected"><di:translate key="cmshelp.cmshelp" /></option>
</mm:compare>
<mm:compare referid="search_component" value="cmshelp" inverse="true">
    <option value="cmshelp"><di:translate key="cmshelp.cmshelp" /></option>
</mm:compare>
