<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp" session="false"%>
<mm:content type="text/html">
<mm:cloud>
<html>
<head>
  <title>Taglib Tips & Tricks</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<%@ include file="menu.jsp"%>

<h1>Tips and Tricks</h1>

<ul>
  <li><a name="listcolumn" /><em>How can I divide my list result in columns?</em>
     <p>
       You need some simple escaping to jsp and some basic arithmetic for this.
     </p>
     <table>
      <tr valign="top">
      <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/listcolumns.jsp" /></mm:formatter></pre></td>
      <td width="50%"><%@include file="codesamples/listcolumns.jsp" %></td>
      </tr>
      </table>
  </li>
  <li><a name="iterate" /><em>How can I iterate?</em>
     <p>
       You can iterate over MMBase lists by use of the
       several nodelists. For other iterations you could consider also 
       external taglibs or inlined java.
     </p>
     <p>
       But there is a trick to do it with the MMBase taglib as
       well. This example shows how:
     </p>
     <table>
      <tr valign="top">
      <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/iterate.jsp" /></mm:formatter></pre></td>
      <td width="50%"><%@include file="codesamples/iterate.jsp" %></td>
      </tr>
      </table>
      <p>
        See also <a href="jstl.jsp">the section about JSTL</a>.
      </p>
  </li>
  <li><a name="listempty" /><em>How do I find out if a list is empty?</em>
     <p>
      Define a variable in the mm:first tag. After the list you can put in the body of mm:notpresent 
      the things which should happen if the list is empty.
     </p>
     <p>
       Since MMBase 1.7.0 you can also use the mm:size tag inside 'container' tags. Currently only the node list tags 
       do have a 'container' counterpart. See <a href="query.jsp"> Query </a> example.
     </p>
  </li>
  <li><a name="isempty" /><em>How can I write a field only if another field is empty?</em>
       <p>
       Use the 'mm:empty' tag in combination with
       the 'write="true"' attribute. This is valid for all 'writer' tags. For example 
       <pre><mm:formatter format="escapexml"><mm:include page="codesamples/empty.jsp" /></mm:formatter></pre>
   You need the 'write' attribute because it is false on default if the tag has a body.
   </p>
  </li>
  <!--
  <li><a name="idsinlist" /><em>I get an exception if I use the 'id' attribute in a tag in
     the body of a list.</em><p>This can be solved in several
     ways.</p><p> The first solution is to make sure that the tag with
     the id attribute is evaluated only once (e.g. by using an
     mm:first tag). Then it is clear which value exactly must be
     written to the context, and you will not see an exception.</p>
     <p>The second way is to add an 'anonymous' (without 'id'
     attribute) mm:context tag inside your list. In that way every
     evaluation of the list-body has its own context, and variables
     cannot interfere. The drawback is of course that these variables
     cannot be accessed outside the list, because they are in an
     anonymous context. This is only logical.</p>
    <p>The last, and perhaps ugliest solution, is to 'remove' the id concerned at the end of the
     body of the list, by using the 'mm:remove' tag.</p>
  </li>
  -->
  <li><a name="contextsession" /><em>How to make personalized pages (taking a collection of info from page to page)?</em>
  <p>
   It is possible to write a whole `context' to the session. Give a context an id, and with the
   write-tag write it to the session. With the 'import' tag you can pick it up in another page.
  </p>
  <p>
   It is not (yet?) possible to write a whole context to a cookie. Write only a user id to the
   cookie, and store the info in MMBase objects.
  </p>
  <p>
    If there are only a few parameters, you can also pass them from page to page using
    the 'referids' attribute of the URL-tag (they must however be imported one-by-one).
  </p>
  </li>
  <li><em>Use the 'image' tag for mmbase images. Use url-tag to refer to other pages</em>
   <p>Using the 'image' tag, your pages can also be ported to e.g. a MMBase running in another
   servlet context easily. The image tag determines the url needed to access the image servlet
   (which need not always be /mg.db anymore). This tag does the tricks for you.
   It also protects you against a possible change in the way the image
   database should be called, because then of course also the implementation of this tag will change. </p>
   <p>Using the url-tag, you can easily add parameters (using the mm:param tag), and your url's are
   automaticly URL-escaped and encoded (putting the session in the URL if cookies are disabled).
   </p>
  </li>
  <li><a name="dividelist" /><em>How do I divide my list in pages?</em> 
   <p>
     You could use the mm:previousbatches and mm:nextbatches tags. See documentation.
   </p>

     <!--
<pre>
      offset="$[+$page*$config.page_size]"  max="$config.page_size"
 </pre>
  and in the body something like this:
  <pre>
 &lt;mm:last&gt;
   &lt;mm:index&gt;
      &lt;mm:compare referid2="config.page_size"&gt;
         &lt;mm:import id="next_page"&gt;yes&lt;/mm:import&gt;
      &lt;/mm:compare&gt;
   &lt;/mm:index&gt;
 &lt;/mm:last&gt;
</pre>
 After the list the 'next' page and 'previous' page links can be made by something like this:
<pre>
    &lt;mm:isgreaterthan referid="page" value="0.5"&gt;
            &lt;a href='&lt;mm:url&gt;&lt;mm:param name="page" value="$[+$page-1]" /&gt;&lt;/mm:url&gt;' &gt;
              previous page
      &lt;/a&gt;
    &lt;/mm:isgreaterthan&gt;
    &lt;mm:present referid="next_page"&gt;
            &lt;a href='&lt;mm:url&gt;&lt;mm:param name="page"  value="$[+$page+1]" /&gt;&lt;/mm:url&gt;' &gt;
               next page
            &lt;/a&gt;
  </pre>
  -->
  </li>
  <li><a name="notpossible" /><em>If something is not possible with the MMBase taglib, you can alway use jsp (or other taglibs)!</em>
   <p>
    E.g. basic looping constructions are not really available in the MMBase taglib. You can use
    another taglib for this, or use JSP. For this the 'jspvar' (and 'vartype') attributes are available.
   </p>
   <p>
     A lot is possible though using MMBase taglib only. We discourage you to 'flee' to JSP too quickly, as it makes your pages harder to read.
     See also <a href="jstl.jsp">the section about JSTL</a>.
   </p>
   </li>
   <li><a name="form" /><em>Can I put the form, and the code to handle the form easily in one JSP page?</em>
    <p>
      Yes. Use the 'mm:present' and 'mm:notpresent' tags to separate the two pieces of code. Check
      on the submit value of the form or on some hidden value.
    </p>
   </li>
   <li><a name="reuse" />
    <em>How do I reuse pieces of taglib/jsp code?</em>
    <p>
      See <a href="http://www.mmbase.org/docs/applications/taglib/frontenddevelopers/taglib/include.html">MMBase documentation about include mechanisms in mmbase taglib</a>.
    </p>
  </li>
  <li><a name="listvslistnodes" />
   <em>What's the difference between listnodes and list, relatednodes and  related?</em>
    <p>
      The difference is that 'listnodes' and 'relatednodes' return real nodes, and the fields you
      can refer to simply by their name.  'list' and 'related' return 'cluster' nodes, which are
      nodes combined of several types. Fields must be prefixed by their element in the 'path'. See
      documentation of <a href="<mm:url page="$taglibdoc/reference/list.jsp" />">mm:list</a>.
    </p>
    <p>
      It is possible to get the 'real' nodes from the 'cluster' nodes of the 'list' and 'related'
      tag by use of a node tag with the element attribute. But be careful to use this with large resultsets, 
      because there is a good chance that it will do a new query for each node.
    </p>
    <p>
      If your page is too slow and you are using relatednodes in relatednodes in relatednodes, you
      might consider replacing it by one 'related'. This may be faster.
    </p>
    <p>
     The other big advantage of 'related' above 'relatednodes' is that you can indicate the 'role'
     of the relation.
    </p>
  </li>
</ul>

</body>

</html>

</mm:cloud>
</mm:content>