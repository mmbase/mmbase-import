<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content  type="text/plain" postprocessor="reducespace">
<mm:import externid="text"><%= org.mmbase.Version.get() %></mm:import>
<mm:import id="myperlprogram">
$a = "<mm:write referid="text" />";
$a =~ tr/a-zA-Z0-9/b-zaB-ZA1-90/;
print $a;
</mm:import>
<mm:write referid="text" /> --> <mm:write escape="perl" value="$myperlprogram" />
</mm:content>

