<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>
<h2>present</h2>
<mm:import externid="a_param" required="true" />

<% try { %>
<mm:import externid="b_param" required="true" />
WRONG: required attribute of import didn't throw exception<br />
<% } catch (Exception e) { %>
Ok, required attribute threw exception <br />
<% } %>

<mm:import externid="c_param" />

<mm:notpresent referid="c_param">
Ok, c_param was not present indeed.<br />
</mm:notpresent>
<mm:present referid="present">
WRONG: c_param is not present!<br />
</mm:present>

<mm:notpresent referid="d_param">
Ok, d_param was not present indeed (not even registered).<br />
</mm:notpresent>
<mm:present referid="present">
WRONG: d_param is not present!<br />
</mm:present>
Writing a_param: <mm:write referid="a_param" />, <mm:write value="$a_param" /> <br />
Writing c_param: <mm:write referid="c_param" />, <mm:write value="$c_param" /> (should be empty)<br />
Writing d_param:
<% try { %>
 <mm:write referid="d_param" />
 WRONG, should have thrown exception 
<% } catch (Exception e) { %>
Ok, threw exception 
<% } %>
<% try { %>
 <mm:write value="$d_param" /> 
 WRONG, should have thrown exception
<% } catch (Exception e) { %>
Ok, threw exception 
<% } %>
<br />
testing isempty:<br />
<mm:import id="empty" />
<mm:write referid="c_param"><mm:isempty>yes (not present)</mm:isempty></mm:write>,
<mm:write referid="empty"><mm:isempty>yes (is really empty)</mm:isempty></mm:write>,
<mm:write value="$empty"><mm:isempty>yes (is really empty)</mm:isempty></mm:write>,
<mm:write value=""><mm:isempty>yes (specified empty)</mm:isempty></mm:write><br />
testing isnotempty:<br />
<mm:import id="notempty"> </mm:import>
<mm:write referid="a_param"><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write>,
<mm:write value="$a_param"><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write>,
<mm:write referid="notempty"><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write>,
<mm:write value="$notempty"><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write>,
<mm:write value=" "><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write>
<mm:write value="hello"><mm:isnotempty>yes (<mm:write />)</mm:isnotempty></mm:write><br />
testing compare:<br />
With 'decimal'<br />
<mm:import id="a1" vartype="decimal">21</mm:import>
<mm:import id="b1" >21.0</mm:import>
<mm:compare referid="a1" value="$b1" >Ok <mm:write value="$a1 == $b1" /></mm:compare>
<mm:compare referid="a1" value="$b1" inverse="true" >WRONG <mm:write value="$a1 != $b1" /></mm:compare>
<br />
<mm:compare referid="a1" referid2="b1" >Ok <mm:write value="$a1 == $b1" /></mm:compare>
<mm:compare referid="a1" referid2="b1" inverse="true" >WRONG <mm:write value="$a1 != $b1" /></mm:compare>
<br />
With 'string' <br />
<mm:import id="a2" >21</mm:import>
<mm:import id="b2" >21.0</mm:import>
<mm:compare referid="a2" value="$b2" >WRONG <mm:write value="$a2 == $b2" /></mm:compare>
<mm:compare referid="a2" value="$b2" inverse="true" >Ok<mm:write value="$a2 != $b2" /></mm:compare>
<br />
With 'integer' <br />
<mm:import id="a3" vartype="integer">21</mm:import>
<mm:import id="b3" >21.0</mm:import>
<mm:compare referid="a3" value="$b3" >Ok <mm:write value="$a3 == $b3" /></mm:compare>
<mm:compare referid="a3" value="$b3" inverse="true" >WRONG<mm:write value="$a3 != $b3" /></mm:compare>
<br />


<hr />
<a href="<mm:url page="present.jsp" />">present.jsp</a>
<hr />
</body>
</html>