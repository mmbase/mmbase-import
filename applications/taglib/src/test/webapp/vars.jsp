<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0">
  <html  xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>Testing MMBase/taglib</title>
      <style>
        html {
        background-color: #ccc;
        color: black;
        padding: 10px;
        }
        body {
        background-color: white;

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
        tr.todo {
        background-color: #faa;
        }

      </style>
      <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.js"></script>
      <script type="text/javascript">
        $(function() {
         $("table tr").each(function() {
           var actual = $(this).find("td:eq(1)").text();
           var expected = $(this).find("td:eq(2)").text();
           if (actual != expected) {
             $(this).addClass('todo');
           }
        });
        });
      </script>
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
        <dt>e</dt><dd>Not set before context, set in context, set after context</dd>
        <dt>f</dt><dd>Not set before context, set in context, set after context in list</dd>
      </dl>
      <mm:import id="aa">A</mm:import>
      <mm:import id="ab">B</mm:import>
      <mm:import id="ac">C</mm:import>


      <table>
        <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
        <mm:context id="contexta">
          <mm:import externid="aa" from="parent" required="true"/>
          <mm:import id="ab">BB</mm:import>
          <mm:import id="ac" reset="true">CC</mm:import>
          <mm:import id="ae">EE</mm:import>
          <mm:import id="af">FF</mm:import>
          <tr><td>a a</td><td><mm:write referid="aa" />, ${aa}</td><td>A, A</td></tr>
          <tr><td>a b</td><td><mm:write referid="ab" />, ${ab}</td><td>BB, BB</td></tr>
          <tr><td>a c</td><td><mm:write referid="ac" />, ${ac}</td><td>CC, CC</td></tr>
          <tr><td>a e</td><td><mm:write referid="ae" />, ${ae}</td><td>EE, EE</td></tr>
          <tr><td>a f</td><td><mm:write referid="af" />, ${af}</td><td>FF, FF</td></tr>
        </mm:context>

        <tr><td>a a</td><td><mm:write referid="aa" />, <mm:write referid="contexta.aa" />, ${aa}, ${contexta.aa}</td><td>A, A, A, A</td></tr>
        <tr><td>a b</td><td><mm:write referid="ab" />, <mm:write referid="contexta.ab" />, ${ab}, ${contexta.ab}</td><td>B, BB, B, BB</td></tr>
        <tr><td>a c</td><td><mm:write referid="ac" />, <mm:write referid="contexta.ac" />, ${ac}, ${contexta.ac}</td><td>C, CC, C, CC</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>


        <tr>
          <td>a e</td>
          <td>
            <c:catch var="e"><mm:write referid="ae" /></c:catch>
            <jsp:text>${empty e ? '' : 'an exception'}, (${ae})</jsp:text>
          </td>
          <td>an exception, ()</td>
          <td> Fails in 1.9, ${e}</td>
        </tr>
        <tr>
          <td>a e</td>
          <td>
            <c:catch var="e"><mm:import id="ae">EEE</mm:import></c:catch>
            <jsp:text>${empty e ? '' : 'an exception,'}</jsp:text>
            <mm:write referid="ae" />
            <jsp:text>, ${ae}</jsp:text>
          </td>
          <td>EEE, EEE</td>
          <td>${e}</td>
        </tr>
        <tr>
          <td>a f</td>
          <td>
            <c:catch var="e"><mm:stringlist referid="list"><mm:import id="af"><mm:index /></mm:import></mm:stringlist></c:catch>
            <jsp:text>${empty e ? '' : 'an exception,'}</jsp:text>
            <mm:write referid="af" />
            <jsp:text>, ${af}</jsp:text>
          </td>
          <td>4, 4</td>
          <td>${e}</td>
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
          <mm:context id="contextb">
            <mm:import externid="ba" from="parent" required="true"/>
            <mm:import id="bb">BB</mm:import>
            <mm:import id="bc" reset="true">CC</mm:import>
            <mm:import id="bd" context="test">DD</mm:import>
            <mm:import id="be">EE</mm:import>
            <mm:import id="bf">EF</mm:import>
            <tr><td>b a</td><td><mm:write referid="ba" />, ${ba}</td><td>A, A</td></tr>
            <tr><td>b b</td><td><mm:write referid="bb" />, ${bb}</td><td>BB, BB</td></tr>
            <tr><td>b c</td><td><mm:write referid="bc" />, ${bc}</td><td>CC, CC</td></tr>
            <tr><td>b d</td><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
            <tr><td>b e</td><td><mm:write referid="be" />, ${be}</td><td>EE, EE</td></tr>
            <tr><td>b f</td><td><mm:write referid="bf" />, ${bf}</td><td>FF, FF</td></tr>
          </mm:context>

          <tr><td>b a</td><td><mm:write referid="ba" />, <mm:write referid="contextb.ba" />, ${ba}, ${contextb.ba}</td><td>A, A, A, A</td></tr>
          <tr><td>b b</td><td><mm:write referid="bb" />, <mm:write referid="contextb.bb" />, ${bb}, ${contextb.bb}</td><td>B, BB, B, BB</td></tr>
          <tr><td>b c</td><td><mm:write referid="bc" />, <mm:write referid="contextb.bc" />, ${bc}, ${contextb.bc}</td><td>C, CC, C, CC</td><td>A bit like <a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
          <tr><td>b d</td><td><mm:write referid="bd" />, ${bd}</td><td>DD, DD</td></tr>
          <tr>
            <td>b e</td>
            <td>
              <c:catch var="e"><mm:write referid="be" />,</c:catch>
              <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
              <mm:write referid="be" />
              <jsp:text>, (${be})</jsp:text>
            </td>
            <td>an exception, (EE)</td>
            <td> Fails in 1.9. ${e}</td>
          </tr>
          <tr>
            <td>b e</td>
            <td>
              <c:catch var="e"><mm:import id="be">EEE</mm:import></c:catch>
              <jsp:text>${empty e ? '' : 'an exception,'}</jsp:text>
              <mm:write referid="be" />
              <jsp:text>, ${be}</jsp:text>
            </td>
            <td>EEE, EEE</td>
            <td>${e}</td>
          </tr>
          <tr>
            <td>b f</td>
            <td>
              <c:catch var="e"><mm:stringlist referid="list"><mm:import id="bf"><mm:index /></mm:import></mm:stringlist></c:catch>
              <jsp:text>${empty e ? '' : 'an exception,'}</jsp:text>
              <mm:write referid="bf" />
              <jsp:text>, ${bf}</jsp:text>
            </td>
            <td>4, 4</td>
            <td>${e}</td>
          </tr>
        </table>
      </mm:context>

      <h2>(c) Lists</h2>
      <dl>
        <dt>a</dt><dd>Set before list, reset in list</dd>
        <dt>b</dt><dd>Not set before list, set in list</dd>
        <dt>c</dt><dd>Set before list, set in list (which should give excpetion)</dd>
        <dt>d</dt><dd>Like (c), but only set once in the list</dd>
        <dt>e</dt><dd>Using not mm:import but mm:write</dd>
        <dt>f</dt><dd>Set in list, remove in list</dd>
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
            <td><mm:write referid="ca" />, ${ca}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <mm:import id="cb"><mm:write /></mm:import>
          <tr>
            <td>c b</td>
            <td><mm:write referid="cb" />, ${cb}</td>
            <td><mm:index />, <mm:index /></td>
          </tr>
          <tr>
            <td>c c</td>
            <td>
              <c:catch var="e"><mm:import id="cc"><mm:write /></mm:import></c:catch>
              <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
              <mm:write referid="cc" />
              <jsp:text>, ${cc}</jsp:text>
            </td>
            <td>an exception, C, C</td>
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
                <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
                <mm:write referid="cd" />
                <jsp:text>, ${cd}</jsp:text>
              </td>
              <td>an exception, D, D</td>
            </tr>
          </mm:first>
          <tr>
            <td>c f</td>
            <td>
              <mm:import id="cf"><mm:write /></mm:import>
              <mm:write referid="cf" />
              <jsp:text>, ${cf}, </jsp:text>
              <mm:remove referid="cf" />
              <c:catch var="e"><mm:write referid="cf" /></c:catch>
              <jsp:text>${empty e ? '' : 'an exception'}</jsp:text>
              <jsp:text>, ${cf}</jsp:text>
            </td>
            <td><mm:index />, <mm:index />, an exception, </td>
          </tr>
        </mm:stringlist>
        <tr><td>c a</td><td><mm:write referid="ca" />, ${ca}</td><td>3, 3</td></tr>
        <tr><td>c b</td><td><mm:write referid="cb" />, ${cb}</td><td>3, 3</td><td><a href="http://www.mmbase.org/jira/browse/MMB-1702">MMB-1702</a></td></tr>
        <tr><td>c c</td><td><mm:write referid="cc" />, ${cc}</td><td>C, C</td><td>3,3 in MMBase 1.8 (See remarks about exception in first iteration)</td></tr>
        <tr><td>c d</td><td><mm:write referid="cd" />, ${cd}</td><td>D, D</td></tr>
        <tr>
          <td>c f</td>
          <td>
            <c:catch var="e"><mm:write referid="cf" /></c:catch>
            <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
            <jsp:text>${cf}</jsp:text>
          </td>
          <td>an exception, </td>
        </tr>
      </table>

      <h2>(d) List in context</h2>
      <p>Like (c), but in a context (named 'test2')</p>
      <mm:context id="test2">
        <mm:import id="da">A</mm:import>
        <mm:import id="dc">C</mm:import>
        <mm:import id="dd">D</mm:import>
        <mm:import id="de">E</mm:import>
        <table>
          <tr><th class="id">id</th><th>is</th><th>should be</th><th>remarks</th></tr>
          <mm:stringlist referid="list" max="3">
            <mm:import id="da" reset="true"><mm:write /></mm:import>
            <tr>
              <td>d a</td>
              <td><mm:write referid="da" />, ${da}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <mm:import id="db"><mm:write /></mm:import>
            <tr>
              <td>d b</td>
              <td><mm:write referid="db" />, ${db}</td>
              <td><mm:index />, <mm:index /></td>
            </tr>
            <tr>
              <td>d c</td>
              <td>
                <c:catch var="e"><mm:import id="dc"><mm:write /></mm:import></c:catch>
                <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
                <mm:write referid="dc" />
                <jsp:text>, ${dc}</jsp:text>
              </td>
              <td>an exception, C, C</td>
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
                  <jsp:text>${empty e ? '' : 'an exception, '}</jsp:text>
                  <mm:write referid="dd" />
                  <jsp:text>, ${dd}</jsp:text>
                </td>
                <td>an exception, D, D</td>
              </tr>
              <tr>
                <td>d e</td>
                <td>
                  <mm:write write="false" id="de" value="EE" reset="true" />
                  <mm:write referid="de" />
                  <jsp:text>, ${de}</jsp:text>
                </td>
                <td>EE, EE</td>
              </tr>
            </mm:first>
          </mm:stringlist>
          <tr><td>d a</td><td><mm:write referid="da" />, <mm:write referid="test2.da" />, ${da}</td><td>3, 3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d b</td><td><mm:write referid="db" />, ${db}</td><td>3, 3</td><td>3,1 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d c</td><td><mm:write referid="dc" />, ${dc}</td><td>C, C</td><td>3,2 in MMBase 1.8 (Fail)</td></tr>
          <tr><td>d d</td><td><mm:write referid="dd" />, ${dd}</td><td>D, D</td></tr>
          <tr><td>d e</td><td><mm:write referid="de" />, ${de}</td><td>EE, EE</td></tr>
        </table>
      </mm:context>


      <hr />
      <mm:escape escape="links">$URL$</mm:escape>


    </body>
  </html>
</jsp:root>
