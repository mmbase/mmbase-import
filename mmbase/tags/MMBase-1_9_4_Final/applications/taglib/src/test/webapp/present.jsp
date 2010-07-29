<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<body>
<h1>Testing taglib</h1>
<h2>Testing import/present/notpresent/compare tags</h2>
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
Testing isgreaterthan/islessthan:<br />
With decimal<br />
<mm:import id="a4" vartype="decimal">10</mm:import>
<mm:import id="b4" vartype="decimal">9</mm:import>
<mm:isgreaterthan referid="a4" referid2="b4">OK <mm:write value="$a4 &gt; $b4" /><br /></mm:isgreaterthan>
<mm:islessthan    referid="a4" referid2="b4">WRONG <mm:write value="! $a4 &lt; $b4" /><br /></mm:islessthan>
With integer<br />
<mm:import id="a5" vartype="integer">10</mm:import>
<mm:import id="b5" vartype="integer">9</mm:import>
<mm:isgreaterthan referid="a5" referid2="b5">OK <mm:write value="$a5 &gt; $b5" /><br /></mm:isgreaterthan>
<mm:islessthan    referid="a5" referid2="b5">WRONG <mm:write value="$a5 &lt; $b5" /><br /></mm:islessthan>
With double/integer<br />
<mm:import id="a7" vartype="double">10</mm:import>
<mm:import id="b7" vartype="integer">9</mm:import>
<mm:import id="c7" vartype="double">9</mm:import>
<mm:isgreaterthan referid="a7" referid2="b7">OK <mm:write value="$a7 &gt; $b7" /><br /></mm:isgreaterthan>
<mm:islessthan    referid="a7" referid2="b7">WRONG <mm:write value="$a7 &lt; $b7" /><br /></mm:islessthan>
<mm:compare       referid="b7" referid2="c7">OK <mm:write value="$b7 == $c7" /><br /></mm:compare>
<mm:compare       referid="c7" referid2="b7">OK <mm:write value="$c7 == $b7" /><br /></mm:compare>
With string (default)<br />
<mm:import id="a6">bcd</mm:import>
<mm:import id="b6">abc</mm:import>
<mm:isgreaterthan referid="a6" referid2="b6">OK <mm:write value="$a6 &gt; $b6" /><br /></mm:isgreaterthan>
<mm:islessthan    referid="a6" referid2="b6">WRONG <mm:write value="$a6 &lt; $b6" /><br /></mm:islessthan>
Also trying a few with 'writer' functionality (testing $_ too):<br />
<mm:write referid="a6">
  <mm:isgreaterthan referid2="b6">OK <mm:write value="$_ &gt; $b6" /><br /></mm:isgreaterthan>
  <mm:islessthan    referid2="b6">WRONG <mm:write value="$_ &lt; $b6" /><br /></mm:islessthan>
</mm:write>
Also with 'value' attribute:<br />
<mm:write referid="a6">
  <mm:isgreaterthan value="$b6">OK <mm:write value="$_ &gt; $b6" /><br /></mm:isgreaterthan>
  <mm:islessthan    value="$b6">WRONG <mm:write value="$_ &lt; $b6" /><br /></mm:islessthan>
</mm:write>
<mm:write referid="a5">
  <mm:isgreaterthan value="$b5">OK <mm:write value="$_ &gt; $b5" /><br /></mm:isgreaterthan>
  <mm:islessthan    value="$b5">WRONG <mm:write value="$_ &lt; $b5" /><br /></mm:islessthan>
</mm:write>

Testing writer functionality of field tag<br />
<mm:import id="node"       externid="testnode"       from="session" />
<mm:node referid="node">
<mm:field name="title">
  <mm:isnotempty>
    Ok. title is indeed not empty: <mm:write />(should show title)<br />
  </mm:isnotempty>
  <mm:isempty>
    WRONG. Title is certain not empty. <br />
  </mm:isempty>
  <mm:isgreaterthan value="zzzz">
    WRONG. Title is smaller than 'zzz'<br />
  </mm:isgreaterthan>
  <mm:islessthan value="zzzz">
    Ok <mm:write /> is less than 'zzz'<br />
  </mm:islessthan>
</mm:field>
<mm:field name="intro">
  <mm:isnotempty>
    WRONG. Intro should be empty.<br />
  </mm:isnotempty>
  <mm:isempty>
    Ok. Intro indeed should be empty.<br />
  </mm:isempty>
</mm:field>
Should see nothing here: <mm:field id="node_title" name="title" write="false" /><br />
But see the title here: <mm:field referid="node_title" /><br />
</mm:node>
<p>
Testing 'reset' attribute.<br />
<mm:import id="some_id">testtest</mm:import>
<% try { %>
 <mm:import id="some_id">test2test2</mm:import>
 WRONG, should have thrown exception.<br />
<% } catch (Exception e) { %>
Ok, threw exception without 'reset'.<br />
<% } %>

<% try { %>
 <mm:import id="some_id" reset="true">test<mm:write referid="some_id" />test2</mm:import>
Ok, did not throw exception with 'reset'.<br />
<% } catch (Exception e) { %>
 WRONG, should not have thrown exception with reset="true".
 <%= e.getMessage() %>
 <br />
<% } %>
<% try { %>
 <mm:import externid="a_param" id="some_id" reset="true">bla bla </mm:import>
Ok, did not throw exception with 'reset'.<br />
<% } catch (Exception e) { %>
 WRONG, should not have thrown exception with reset="true".
 <%= e.getMessage() %>
 <br />
<% } %>
<% try { %>
 <mm:import externid="a_param" id="some_id" >bla bla </mm:import>
 WRONG, should not have thrown exception without reset="true".
<% } catch (Exception e) { %>
  Ok, threw exception without 'reset'.<br />
 <br />
<% } %>
</p>

<h2>Combo with lists</h2>
<mm:import id="testlist" vartype="list">A,B,C,D,E,F</mm:import>


 <% try { %> 
<mm:stringlist referid="testlist">
 <mm:import id="some_id" reset="true">bla bla bla</mm:import>
</mm:stringlist>
  Ok, did not even give exception in list <br />
 <% } catch (Exception e) { %>
  WRONG  reset in list gave exception! <br />
 <% } %>

<% try { %>
<mm:stringlist referid="testlist">
   <mm:write id="some_list_id" />
   <mm:first inverse="true">
     <mm:present referid="some_list_id">
       <mm:index>
         <mm:compare value="2">
           Ok, in second iteration the id can be overwritten, but yet is present (already in parent)<br />
         </mm:compare>
       </mm:index>
     </mm:present>
     <mm:notpresent referid="some_list_id">
        WRONG, 'some_list_id' was registered in first iteration!.<br />
     </mm:notpresent>
   </mm:first>
</mm:stringlist>
Ok, did not throw exception with id in a list (Does in 1.6)<br />
 <% } catch (Exception e) { %>
 WRONG, should not have thrown exception.<br />
<% } %>


<%-- // catching exception in tomcat 5 (perhaps also 4?) goes wrong with lists. Having done this, 'writers' don't work any more.
<% try { %>
<mm:stringlist referid="testlist">
   <mm:write id="some_list_id" />
</mm:stringlist>
 WRONG, should have thrown exception (id was used already).<br />
 <% } catch (Exception e) { %>
1.  Ok, did throw exception.<br />
<% } %>

<% try { %>
<mm:stringlist referid="testlist">
    <mm:write id="some_list_id" />
</mm:stringlist>
WRONG, should have thrown exception (id was used already).<br />
<% } catch (Exception e) { %>
2. Ok, did throw exception.<br />
<% } %>    

--%>
<hr />
<mm:stringlist referid="testlist">
  <mm:first>
    <mm:remove referid="some_list_id" />
  </mm:first>
</mm:stringlist>

<mm:present referid="some_list_id">
  WRONG, 'testlist' was removed,<br />
</mm:present>
<mm:present referid="some_list_id" inverse="true">
  Ok, 'testlist' was removed (in a list)<br />
</mm:present>

<mm:import id="abcde" />


<mm:present referid="abcde">
  Should show<br />
</mm:present>
<mm:notpresent referid="abcde">
  WRONG, should not show.<br />
</mm:notpresent>


<hr />
This link should result an exception xy: <a href="<mm:url page="present.jsp" />">present.jsp</a><br />
<a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />
</body>
</html>