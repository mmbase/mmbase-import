<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="email">
    <option value="email" selected="selected"><fmt:message key="EMAIL" /></option>
</mm:compare>
<mm:compare referid="search_component" value="email" inverse="true">
    <option value="email"><fmt:message key="EMAIL" /></option>
</mm:compare>
</fmt:bundle>

