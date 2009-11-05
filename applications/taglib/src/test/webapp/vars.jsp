<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><html>
  <title>Testing MMBase/taglib</title>
  <body>
    <h1>Testing MMBase/taglib</h1>
    <h2>vars</h2>

    <mm:import id="a">A</mm:import>
    <mm:import id="b">B</mm:import>
    <mm:import id="c">C</mm:import>
    <mm:import id="list" vartype="list">1,2,3,4</mm:import>

    <table width="100%" border="1">
      <tr><th>is</th><th>should be</th></tr>
    <mm:context>
      <mm:import externid="a" from="parent" required="true"/>
      <mm:import id="b">BB</mm:import>
      <mm:import id="c" reset="true">CC</mm:import>
      <mm:import id="d">DD</mm:import>
      <tr><td><mm:write referid="a" />, ${a}</td><td>A, A</td></tr>
      <tr><td><mm:write referid="b" />, ${b}</td><td>BB, BB</td></tr>
      <tr><td><mm:write referid="c" />, ${c}</td><td>CC, CC</td></tr>
      <tr><td><mm:write referid="d" />, ${d}</td><td>DD, DD</td></tr>
    </mm:context>

    <tr><td><mm:write referid="a" />, ${a}</td><td>A, A</td></tr>
    <tr><td><mm:write referid="b" />, ${b}</td><td>B, B</td></tr>
    <tr><td><mm:write referid="c" />, ${c}</td><td>CC, CC</td></tr>
    <tr><td><mm:write referid="d" />, ${d}</td><td>DD, DD</td></tr>

    <tr><th colspan="100">lists</th></tr>

    <mm:context>
      <tr><td><mm:write referid="a" />, ${a}</td><td>A, A</td></tr>
      <tr><td><mm:write referid="b" />, ${b}</td><td>B, B</td></tr>
      <tr><td><mm:write referid="c" />, ${c}</td><td>CC, CC</td></tr>
      <tr><td><mm:write referid="d" />, ${d}</td><td>DD, DD</td></tr>
      <mm:stringlist referid="list">
        <mm:first>
          <mm:import externid="a" from="parent" required="true"/>
        </mm:first>
      </mm:stringlist>
    </mm:context>
    </table>


  </body>
</html>
