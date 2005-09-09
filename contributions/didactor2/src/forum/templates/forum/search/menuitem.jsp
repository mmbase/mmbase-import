<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:import externid="search_component"/>
<mm:compare referid="search_component" value="forum">
    <option value="forum" selected="selected"><di:translate id="forum">Forum</di:translate></option>
</mm:compare>
<mm:compare referid="search_component" value="forum" inverse="true">
    <option value="forum"><di:translate id="forum">Forum</di:translate></option>
</mm:compare>

