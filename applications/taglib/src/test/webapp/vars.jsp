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
        th.id {
        width: 4%;
        }
        th {
        width: 32%;
        border-bottom: 1px dotted #444;

        }
        dt {
          font-style: italic;
        }

      </style>
    </head>
    <body>
      <mm:import id="list" vartype="list">1,2,3,4</mm:import>

      <h1>Testing MMBase/taglib - Contexts nesting and vars</h1>

      <p>Testing various situations with mm:import, mm:write, and mm:context. Situations are named a-d and correspond with sections in the document, tests are also named with a letter. Resulting in variable names [a-d][a-z]. The values of the variables are always uppercase values of the name (or chains thereof), or sometimes numbers (corresponding to the index in a list while it was set)</p>
      <h2>(a) Page Context</h2>
      <dl>
        <dt>a</dt><dd>Set before context, imported in context</dd>
        <dt>b</dt><dd>Set before context, also set in context</dd>
        <dt>c</dt><dd>Set before context, reset in context</dd>
        <dt>d</dt><dd>Not set before context, set in parent in the context (only used in (b)</dd>
        <dt>e</dt><dd>Not set before context, set in context</dd>
      </dl>
      <mm:import id="aa">A</mm:import>
      <mm:import id="ab">B</mm:import>
      <mm:import id="ac">C</mm:import>


      <table>
        <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
        <mm:context>
          <mm:import externid="aa" from="parent" required="true"/>
          <mm:import id="ab">BB</mm:import>
          <mm:import id="ac" reset="true">CC</mm:import>
          <mm:import id="ae">EE</mm:import>
          <tr><td>a a</td><td><mm:write referid="aa" />, ${aa}</td><td>A, A</td></tr>
          <tr><td>a b</td><td><mm:write referid="ab" />, ${ab}</td><td>BB, BB</td></tr>
          <tr><td>a c</td><td><mm:write referid="ac" />, ${ac}</td><td>CC, CC</td></tr>
          <tr><td>a e</td><td><mm:write referid="ae" />, ${ae}</td><td>EE, EE</td></tr>
        </mm:context>

        <tr><td>a a</td><td><mm:write referid="aa" />, ${aa}</td><td>A, A</td></tr>
        <tr><td>a b</td><td><mm:write referid="ab" />, ${ab}</td><td>B, B</td></tr>
        <tr><td>a c</td><td><mm:write referid="ac" />, ${ac}</td><td>C, C</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
        <tr>
          <td>a e</td>
          <td><c:catch var="e"><mm:write referid="ae" /></c:catch>${e}, (${ae})</td>
          <td>an exception, ()</td>
          <td> Fails in 1.9</td>
        </tr>
      </table>

      <h2>(b) Context 'test'</h2>
      <p>Like (a), but in a context named 'test'</p>
      <mm:context id="test">
        <mm:import id="ba">A</mm:import>
        <mm:import id="bb">B</mm:import>
        <mm:import id="bc">C</mm:import>

        <table>
          <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
          <mm:context>
            <mm:import externid="ba" from="parent" required="true"/>
            <mm:import id="bb">BB</mm:import>
            <mm:import id="bc" reset="true">CC</mm:import>
            <mm:import id="bd" context="test">DD</mm:import>
            <mm:import id="be">EE</mm:import>
            <tr><td>b a</td><td><mm:write referid="ba" />, ${ba}</td><td>A, A</td></tr>
            <tr><td>b b</td><td><mm:write referid="bb" />, ${bb}</td><td>BB, BB</td></tr>
            <tr><td>b c</td><td><mm:write referid="bc" />, ${bc}</td><td>CC, CC</td></tr>
            <tr><td>b d</td><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
            <tr><td>b e</td><td><mm:write referid="be" />, ${be}</td><td>EE, EE</td></tr>
          </mm:context>

          <tr><td>b a</td><td><mm:write referid="ba" />, ${ba}</td><td>A, A</td></tr>
          <tr><td>b b</td><td><mm:write referid="bb" />, ${bb}</td><td>B, B</td></tr>
          <tr><td>b c</td><td><mm:write referid="bc" />, ${bc}</td><td>C, C</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
          <tr><td>b d</td><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
          <tr>
            <td>b e</td>
            <td><c:catch var="e"><mm:write referid="be" /></c:catch>${e}, (${be})</td>
            <td>an exception, ()</td>
            <td> Fails in 1.9</td>
          </tr>
        </table>
      </mm:context>

      <h2>(c) Lists</h2>
      <dl>
        <dt>a</dt><dd>Set before list, reset in list</dd>
        <dt>b</dt><dd>Not set before list, set in list</dd>
        <dt>c</dt><dd>Set before list, set in list (which should give excpetion)</dd>
        <dt>d</dt><dd>Like (c), but only set once in the list</dd>
      </dl>
      <mm:import id="ca">A</mm:import>
      <mm:import id="cc">C</mm:import>
      <mm:import id="cd">D</mm:import>
      <table>
        <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
        <mm:stringlist referid="list" max="3">
          <mm:import id="ca" reset="true"><mm:write /></mm:import>
          <tr>
            <td>c a</td>
            <td><mm:write referid="ca" />,${ca}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <mm:import id="cb"><mm:write /></mm:import>
          <tr>
            <td>c b</td>
            <td><mm:write referid="cb" />,${cb}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <tr>
            <td>c c</td>
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
          <mm:first>
            <tr>
              <td>c d</td>
              <td>
                <c:catch var="e"><mm:import id="cd"><mm:write /></mm:import></c:catch>
                <jsp:text>${e}</jsp:text>
                <mm:write referid="cd" />, ${cd}
              </td>
              <td>An exception</td>
            </tr>
          </mm:first>
        </mm:stringlist>
        <tr><td>c a</td><td><mm:write referid="ca" />, ${ca}</td><td>3, 3</td></tr>
        <tr><td>c b</td><td><mm:write referid="cb" />, ${cb}</td><td>3, 3</td><td><a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
        <tr><td>c c</td><td><mm:write referid="cc" />, ${cc}</td><td>C, C</td><td>3,3 in MMBase 1.8 (See remarks about exception in first iteration)</td></tr>
        <tr><td>c d</td><td><mm:write referid="cd" />, ${cd}</td><td>D, D</td></tr>
      </table>

      <h2>(d) List in context</h2>
      <p>Like (c), but in a context (named 'test2')</p>
      <mm:context id="test2">
        <mm:import id="da">A</mm:import>
        <mm:import id="dc">C</mm:import>
        <mm:import id="dd">D</mm:import>
        <table>
          <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
          <mm:stringlist referid="list" max="3">
            <mm:import id="da" reset="true"><mm:write /></mm:import>
            <tr>
              <td>d a</td>
              <td><mm:write referid="da" />,${da}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <mm:import id="db"><mm:write /></mm:import>
            <tr>
              <td>d b</td>
              <td><mm:write referid="db" />,${db}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <tr>
              <td>d c</td>
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
            <mm:first>
              <tr>
                <td>d d</td>
                <td>
                  <c:catch var="e"><mm:import id="dd"><mm:write /></mm:import></c:catch>
                  <jsp:text>${e}</jsp:text>
                  <mm:write referid="dd" />, ${dd}
                </td>
                <td>An exception</td>
              </tr>
            </mm:first>
          </mm:stringlist>
          <tr><td>d a</td><td><mm:write referid="da" />, ${da}</td><td>3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d b</td><td><mm:write referid="db" />, ${db}</td><td>3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d c</td><td><mm:write referid="dc" />, ${dc}</td><td>C, C</td><td>3,2 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d d</td><td><mm:write referid="dd" />, ${dd}</td><td>D, D</td></tr>
        </table>
      </mm:context>


      <hr />
      <mm:escape escape="links">$URL$</mm:escape>


    </body>
  </html>
</jsp:root>
