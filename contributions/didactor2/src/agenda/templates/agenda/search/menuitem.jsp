<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<fmt:bundle basename="nl.didactor.component.agenda.AgendaMessageBundle">
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="agenda">
    <option value="agenda" selected="selected"><fmt:message key="CALENDAR" /></option>
</mm:compare>
<mm:compare referid="search_component" value="agenda" inverse="true">
    <option value="agenda"><fmt:message key="CALENDAR" /></option>
</mm:compare>
</fmt:bundle>