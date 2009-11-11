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
          <td> Fails in 1.9</td>
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
            <td> Fails in 1.9</td>
          </tr>
        </table>
      </mm:context>

      <h2>(c) Lists</h2>
      <mm:import id="ca">A</mm:import>
      <mm:import id="cc">C</mm:import>
      <table>
        <tr><th>is</th><th>should be</th><th>remarks</th></tr>
        <mm:stringlist referid="list" max="3">
          <mm:import id="ca" reset="true"><mm:write /></mm:import>
          <tr>
            <td><mm:write referid="ca" />,${ca}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <mm:import id="cb"><mm:write /></mm:import>
          <tr>
            <td><mm:write referid="cb" />,${cb}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <tr>
            <td>
              <c:catch var="e"><mm:import id="cc"><mm:write /></mm:import></c:catch>
              <jsp:text>${e}</jsp:text>
              <mm:write referid="cc" />, ${cc}
            </td>
            <td>An exception</td>
            <td>
              <mm:index>
                <mm:isgreaterthan value="1">Fails in 1.8 (only exception in first iteration)</mm:isgreaterthan>
              </mm:index>
            </td>
          </tr>
        </mm:stringlist>
        <tr><td><mm:write referid="ca" />, ${ca}</td><td>3, 3</td></tr>
        <tr><td><mm:write referid="cb" />, ${cb}</td><td>3, 3</td><td><a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
        <tr><td><mm:write referid="cc" />, ${cc}</td><td>C, C</td><td>3,3 in MMBase 1.8 (See remarks about exception in first iteration)</td></tr>
      </table>

      <h2>(d) List in context</h2>
      <mm:context id="test2">
        <mm:import id="da">A</mm:import>
        <mm:import id="dc">C</mm:import>
        <table>
          <tr><th>is</th><th>should be</th><th>remarks</th></tr>
          <mm:stringlist referid="list" max="3">
            <mm:import id="da" reset="true"><mm:write /></mm:import>
            <tr>
              <td><mm:write referid="da" />,${da}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <mm:import id="db"><mm:write /></mm:import>
            <tr>
              <td><mm:write referid="db" />,${db}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <tr>
              <td>
                <c:catch var="e"><mm:import id="dc"><mm:write /></mm:import></c:catch>
                <jsp:text>${e}</jsp:text>
                <mm:write referid="dc" />, ${dc}
              </td>
              <td>An exception</td>
              <td>
                <mm:index>
                  <mm:isgreaterthan value="1">Fails in 1.8 (only exception in first iteration)</mm:isgreaterthan>
                </mm:index>
              </td>
            </tr>
          </mm:stringlist>
          <tr><td><mm:write referid="da" />, ${da}</td><td>3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td><mm:write referid="db" />, ${db}</td><td>3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td><mm:write referid="dc" />, ${dc}</td><td>C, C</td><td>3,2 in MMBase 1.8 (Fail)</td></tr>
        </table>
      </mm:context>


      <hr />
      <mm:escape escape="links">$URL$</mm:escape>


    </body>
  </html>
</jsp:root>
