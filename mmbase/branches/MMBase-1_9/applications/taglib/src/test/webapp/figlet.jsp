<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/plain">
<mm:import externid="text"><%= org.mmbase.Version.get()%></mm:import>
<mm:write escape="figlet" value="$text" />
</mm:content>

