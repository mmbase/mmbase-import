<%@page session="false" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mn" 
%><mn:content type="text/html" language="en" expires="600" postprocessor="none">
<html>
<head>
  <title><mn:write id="title" value="MMBase Taglib 1.1" /></title>
  <link href="../style.css" rel="stylesheet" type="text/css"/>
  <style>
    table.toc {
    position: absolute;
    top: 0px;
    left: 0px;
    height: 50px;
    overflow: auto;
    background-color: #eee;
}
 div.content {
    position: absolute;
    margin-left: auto;
    margin-right: auto;
    background: #eee;
    visibility: hidden;
    top: 50px;
    height: 100%;
}
 div.toc span {
   margin-left: 3ex;
   }
  </style>
</head>
<body>
  <mn:formatter  escape="none">
    <mn:xslt>
      <xsl:import href="xslt/slides.xslt" />  
      <!-- support blink for the sake of the argument -->
      <xsl:template match="blink|pre"><xsl:copy-of select="." /></xsl:template>

    </mn:xslt>

    <mmxf>
      <section>
        <h><mn:write referid="title" /></h>
        <p>
          What was MMBase taglib?
        </p>
        <ul>
          <li>Access to MMBase data
<pre>
<![CDATA[
   <mm:cloud>
     <mm:listnodes type="news">
         <h1><mm:field name="title" /></h1>
     </mm:listnodes>
   </mm:cloud>
]]>
</pre>
          </li>        
          <li>It also gives some means to deal with this data        
<pre>
<![CDATA[
   <mm:field name="title">
     <mm:isempty>
        <mm:field name="subtitle" />
     </mm:isempty>
   </mm:field>
]]>
</pre>
          </li>
          <li>Pass stuff around
<pre>
<![CDATA[
  <mm:import externid="node" required="true" />
  <mm:node number="$node">
     <mm:field name="title" />
  </mm:node>
]]>
</pre>
          </li>
        <li>And other more or less to MMBase related things
<pre>
<![CDATA[
  <mm:field name="begin"><mm:time format=":FULL" /></mm:field>
  <mm:formatter>....</mm:formatter>
  ...
]]>
</pre>
          </li>
        </ul>

        <p>
          What is MMBase Taglib 1.1? --&gt; It is still this.
        </p>

        <p>
          Number of features in 1.6: 444
        </p>
        <p>
          Number of features in 1.7: 745
        </p>
        <p>
          Great number of bug-fixes. (over 60 according to bugtracker). Resin-fix.
        </p>
        <p>
          <a href="http://www.mmbase.org/docs/reference/taglib/toc.html">TOC</a>
        </p>

        <section>
          <h>Re: <blink><em>Escapers</em></blink> postprocessor</h>
          <p>
            It was cumbersome to `escape' data-values. It was easy to forget it, which could easily lead to the effect in the title.
<pre>
<![CDATA[
  <h1><mm:field name="html(title)" /></h1>
  <p><mm:field name="html(body)" /></p>
]]>
</pre>
          </p>
          <p>
            More importantly, it did <em>not</em> generate valid HTML. And it also cannot, because
            e.g. inside inline-html elements that should be done differently.
          </p>
          <p>
            Solution: the content-tag, and 'escape' attributes to override its effect.
<pre>
<![CDATA[
<mm:content escaper="inline">
  <h1><mm:field name="title" /></h1>
  <mm:field name="body" escape="p" />
</mm:content>
]]>
</pre>
          </p>
          <p>
            Defaults are coupled to the content-type.
<pre>
<![CDATA[
<mm:content type="text/html" language="nl">
...
</mm:content>
]]>
</pre>
          </p>
          <p>
            Escaping the complete body? That is called 'postprocessor'.
<pre>
<![CDATA[
<mm:content type="text/plain" postprocessor="figlet">
hoi!
</mm:content>
]]>
</pre>
<pre> _           _ _ 
| |__   ___ (_) |
| '_ \ / _ \| | |
| | | | (_) | |_|
|_| |_|\___/|_(_)</pre>

          </p>
          <p>
            See  <a href="taglibcontent.jsp">org/mmbase/bridge/jsp/taglib/resources/taglibcontent.xml</a>
          </p>
          
          <h>"GET /index.jsp HTTP/1.1" 200</h>
          <p>
            Another issue was that people tend to forget that their page might be cacheable in front proxy.
            It was possible though:
<pre>
long ct = System.currentTimeMillis();
response.setDateHeader("Expires", ct + EXPIRES); 
response.setDateHeader("Last-Modified", ct);
</pre>
            "GET /index.jsp HTTP/1.1" 304
          </p>
          <p>
            Content-tag encourages this, by requiring the <em>inverse</em>. If you use it, you must <em>explicitely</em> state that this page should <em>not</em>
            be cached by proxies/browsers.
<pre>
<![CDATA[
<mm:content expires="0">
  non-cacheable content.
</mm:content>
]]>
</pre>
          </p>
          <p>
            What perhaps still might be needed:
<pre>
&lt;%@page session="false" contentType="text/html;charset=UTF-8"  %&gt;
</pre>

          </p>
        </section>
        <section>
          <h>Number of results found: 0</h>
          <p>
            A trouble of the taglib of mmbase-1.6
<pre>
<![CDATA[
<mm:listnodes type="news">
 <mm:first><mm:size id="found" write="false"/></mm:first>
</mm:listnodes>
<mm:present referid="found">
 Number of result found : <mm:write referid="found">
</mm:present>
<mm:notpresent referid="found">
 Number of result found : 0
</mm:notpresent>
]]>
</pre>
          Actually there are two problems here:
          <pre>
            <li>This is cumbersome to code, and hard to understand.</li>
            <li>The size is a java-determined size. If you also wanted to apply max things got really uncomfortable.</li>
          </pre>
          </p>
          <p>
            Solution: Every list tag has its `container' counter-part --&gt; SearchQuery!
<pre>
<![CDATA[
<mm:listnodescontainer type="news">
  <mm:listnodes>
     ...
  </mm:listnodes>
 Number of result found : <mm:size />
</mm:listnodescontainer>
]]>
</pre>
           Also easy to apply `max'
<pre>
<![CDATA[
<mm:listnodescontainer type="news">
  <mm:size id="totalsize" write="false" />
  <mm:maxnumber value="10" />
  <mm:listnodes>
     ...
  </mm:listnodes>
 Number of result found : <mm:write referid="totalsize" />
</mm:listnodescontainer>
]]>
</pre>
          </p>
          <p>
            More SearchQuery stuff that can be done with taglib: constructing constraints.
<pre>
<![CDATA[
<mm:relatednodescontainer type="news">
   <mm:constraint field="begin" operator="LESS" value="$now" />
   <mm:constraint field="end"   operator="GREATER"    value="$now" />
   <mm:sortorder field="begin" direction="down" />
   <mm:maxnumber value="$numberofarticles" />
   <mm:relatednodes>
      ....
   </mm:relatednodes>
</mm:relatednodescontainer>  
]]>
</pre>
         In 1.6 this could in many cases be very cumbersome (where must be the AND's...)
          </p>
          <p>
            So, the list-container tags are an alternative way to define the list, and can be seen
            as `query' tags. They serve as way to get meta-information about the list (mm:size), and to set
            information in it (mm:offset)
          </p>
          <p>
            When using a tag with its container, the tag itself becomes merely the 'loop' tag.
          </p>
            <h>Functions</h>
            <p>
              Function tags work in a similar way.
<pre>
<![CDATA[
<mm:node ...>
<mm:functioncontainer>
   <mm:param name="bla">bloe</mm:param>
   <mm:booleanfunction name="isitso">
     It is true!
   </mm:booleanfunction>
   ..
   <mm:nodelistfunction name="listthosenodes">
      <mm:field name="blabla" /><br />
   </mm:nodelistfunction>
</mm:relatednodescontainer>  
]]>
</pre>
            </p>
        </section>
        <section>
          <h>Object with id X was already registered in the root context.</h>
          <p>
            Other improvements.
          </p>
          <p>
            Perhaps you remember this:
            <pre>
<![CDATA[
<mm:list>
  <mm:context>
   .... id="a"
  </mm:context>  
</mm:list>

<mm:list>
  <mm:remove referid="a" />
   .... id="a"
</mm:list>


<mm:list>
  ...
  <mm:last> ... id="a" </mm:last>>
</mm:list>
]]>
</pre>
          </p>
          <p>
            In the new taglib you can do this:
            <pre>
<![CDATA[
<mm:list>
   .... id="a"
</mm:list>
<mm:write referid="a" />
]]>
</pre>
            and this still goes wrong:
            <pre>
<![CDATA[
<mm:list>
   .... id="a"

  ... id="a"
</mm:list>
]]>
</pre>
          </p>
          <h>comparator</h>
          <p>
            <pre>
<![CDATA[
<mm:listnodes comparator="SHUFFLE">

</mm:list>
]]>
</pre>
          </p>
          <h>re-use context</h>
          <p>
            <pre>
<![CDATA[
<mm:context id="config">

</mm:context>
<mm:context referid="config">
  <mm:import externid="bla" from="parameters,session,this" />
</mm:context>
]]>
</pre>
            See for example the configuration pages of the jsp-editors. 
          </p>
          <h>mm:url trickery</h>
          <p>
                  <pre>
<![CDATA[
 <mm:field id="a" name="number" />

 <mm:field name="field1">
 <a href="<mm:url referid="baseurl" referids="a,b@bb,c?,d?@dd,_@e">
       <mm:param name="f">FF</mm:param>
     </mm:url>">bar</a>
 </mm:field>
]]>
</pre>      
         --&gt;  foo.jsp?z=z&amp;a=123&amp;bb=xxx&amp;e=yyy&amp;f=FF
          </p>
          <h>value set</h>
          <p>
            <pre>
<![CDATA[
 <mm:write referid="foo">
    <mm:compare valueset="a,b,c">
      foo is a or b or c
    </mm:compare>
  </mm:write>
]]>
</pre>      
</p>
         <h>precision="minutes"</h>
          <p>
            <pre>
<![CDATA[
<mm:time id="now" write="false" precision="minutes" />
]]>
</pre>      
</p>

        </section>
        <section>
          <h>And..</h>
          <ul>
            <li>Tree-tag (experimental)</li>
            <li>More query-container-referrers like mm:ageconstraint, mm:previousbatches etc.</li>            
	    <li>mmbase-taglib.jar</li>
            <li>....</li>
          </ul>
          <p>
            Questions?
          </p>
        </section>

      </section>
    </mmxf>
  </mn:formatter>
</body>
</html>
</mn:content>
