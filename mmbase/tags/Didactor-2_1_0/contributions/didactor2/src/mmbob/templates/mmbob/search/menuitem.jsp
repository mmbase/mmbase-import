<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@ include file="/shared/globalLang.jsp" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="mmbob">
    <option value="mmbob" selected="selected"><di:translate key="mmbob.forum" /></option>
</mm:compare>
<mm:compare referid="search_component" value="mmbob" inverse="true">
    <option value="mmbob"><di:translate key="mmbob.forum" /></option>
</mm:compare>

