<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0">
  <html  xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>Testing MMBase/taglib</title>
      <style>
        body {
        background-color: white;
        color: black;
        }
        table {
        width: 100%;
        border: solid 1px;
        }
        td, th {
        width: 33%;
        border-bottom: 1px dotted #444;

        }
        tr {

        }
      </style>
    </head>
    <body>
      <mm:import id="list" vartype="list">1,2,3,4</mm:import>

      <h1>Testing MMBase/taglib - Contexts nesting and vars</h1>
      <h2>(a) Page Context</h2>

      <mm:import id="aa">A</mm:import>
      <mm:import id="ab">B</mm:import>
      <mm:import id="ac">C</mm:import>


      <table>
        <tr><th>is</th><th>should be</th><th>remarks</th></tr>
        <mm:context>
          <mm:import externid="aa" from="parent" required="true"/>
          <mm:import id="ab">BB</mm:import>
          <mm:import id="ac" reset="true">CC</mm:import>
          <mm:import id="ae">EE</mm:import>
          <tr><td><mm:write referid="aa" />, ${aa}</td><td>A, A</td></tr>
          <tr><td><mm:write referid="ab" />, ${ab}</td><td>BB, BB</td></tr>
          <tr><td><mm:write referid="ac" />, ${ac}</td><td>CC, CC</td></tr>
          <tr><td><mm:write referid="ae" />, ${ae}</td><td>EE, EE</td></tr>
        </mm:context>

        <tr><td><mm:write referid="aa" />, ${aa}</td><td>A, A</td></tr>
        <tr><td><mm:write referid="ab" />, ${ab}</td><td>B, B</td></tr>
        <tr><td><mm:write referid="ac" />, ${ac}</td><td>C, C</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
        <tr>
          <td><c:catch var="e"><mm:write referid="ae" /></c:catch>${e}, (${ae})</td>
          <td>an exception, ()</td>
        </tr>
      </table>

      <h2>(b) Context 'test'</h2>
      <mm:context id="test">
        <mm:import id="ba">A</mm:import>
        <mm:import id="bb">B</mm:import>
        <mm:import id="bc">C</mm:import>

        <table>
          <tr><th>is</th><th>should be</th><th>remarks</th></tr>
          <mm:context>
            <mm:import externid="ba" from="parent" required="true"/>
            <mm:import id="bb">BB</mm:import>
            <mm:import id="bc" reset="true">CC</mm:import>
            <mm:import id="bd" context="test">DD</mm:import>
            <mm:import id="be">EE</mm:import>
            <tr><td><mm:write referid="ba" />, ${ba}</td><td>A, A</td></tr>
            <tr><td><mm:write referid="bb" />, ${bb}</td><td>BB, BB</td></tr>
            <tr><td><mm:write referid="bc" />, ${bc}</td><td>CC, CC</td></tr>
            <tr><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
            <tr><td><mm:write referid="be" />, ${be}</td><td>EE, EE</td></tr>
          </mm:context>

          <tr><td><mm:write referid="ba" />, ${ba}</td><td>A, A</td></tr>
          <tr><td><mm:write referid="bb" />, ${bb}</td><td>B, B</td></tr>
          <tr><td><mm:write referid="bc" />, ${bc}</td><td>C, C</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
          <tr><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
          <tr>
            <td><c:catch var="e"><mm:write referid="be" /></c:catch>${e}, (${be})</td>
            <td>an exception, ()</td>
          </tr>
        </table>
      </mm:context>

      <h3>(c) Lists</h3>
      <mm:import id="ca">a</mm:import>
      <mm:stringlist referid="list">
        <mm:import id="ca" reset="true"><mm:write /></mm:import>
        <mm:import id="cb"><mm:write /></mm:import>
      </mm:stringlist>
      <table>
        <tr><th>is</th><th>should be</th><th>remarks</th></tr>
        <tr><td><mm:write referid="ca" />, ${ca}</td><td>4, 4</td></tr>
        <tr><td><mm:write referid="cb" />, ${cb}</td><td>4, 4</td><td><a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
      </table>

      <hr />
      <mm:escape escape="links">$URL$</mm:escape>


    </body>
  </html>
</jsp:root>
