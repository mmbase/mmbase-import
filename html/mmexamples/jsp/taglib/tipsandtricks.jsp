<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>


<mm:cloud>

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Tips and Tricks</h1>

<ul>
  <li><em>How do I find out if a list is empty?</em>
     <p>
      Define a variable in the mm:first tag. After the list you can put in the body of mm:notpresent 
	  the things which should happen if the list is empty.
     </p>
  </li>
  <li><em>How can I write a field only if another field is
       empty?</em><p>Use the 'mm:empty' tag in combination with
       the 'write="true"' attribute. This is valid for all 'writer'
       tags. For example 
       <pre>
&lt;mm:field name="subtitle" write="true" &gt;
     &lt;mm:isempty&gt;
         &lt;mm:field name="title" /&gt;
    &lt;/mm:isempty&gt;
&lt;/mm:field&gt;
</pre>
   You need the 'write' attribute because it is false on default if the tag has a body.
   </p>
  </li>
  <li><em>I get an exception if I use the 'id' attribute in a tag in
     the body of a list.</em><p>This can be solved in several
     ways.</p><p> The first solution is to make sure that the tag with
     the id attribute is evaluated only once (e.g. by using an
     mm:first tag). Then it is clear which value exactly must be
     written to the context, and you will not see an exception.</p>
     <p>The second way is to add an 'anonymous' (without 'id'
     attribute) mm:context text inside your list. In that way every
     evaluation of the list-body has its own context, and variables
     cannot interfere. The drawback is of course that these variables
     cannot be accessed outside the list, because they are in an
     anonymous context. This is only logical.</p>
    <p>The last, and perhaps ugliest solution, is to 'remove' the id in concern at the end of the
     body of the list, by using the 'mm:remove' tag.</p>
  </li>
  <li><em>How to make personalized pages (taking a collection of info from page to page)?</em>
  <p>
   It is possible to write a whole `context' to the session. Give a context an id, and with the
   write-tag write it to the session. With the 'import' tag you can pick it up in another page.
  </p>
  <p>
   It is not (yet?) possible to write a whole context to a cookie. Write only a user id to the
   cookie, and store the info in mmbase objects.
  </p>
  <p>
    If it are only a few parameters you can also pass them relatively easily from page to page using
    the 'referids' attribute of the URL-tag (they must however be imported one-by-one).
  </p>
  </li>
  <li><em>Use the 'image' tag for mmbase images. Use url-tag to refer to other pages</em>
   <p>Using the 'image' tag, you pages can also be ported to e.g. an mmbase running in another
   servlet context easily, because then the url should not be simply /img.db anymore. This tag does
   the tricks for you. It also will protect you against a possible change in the way the image
   database should be called, because then of course also the implementation of this tag will change. </p>
   <p>Using the url-tag, you can easily add parameters (using the mm:param tag), and your url's are
   automaticly URL-escaped and encoded (putting the session in the URL if cookies are disabled).
   </p>
  </li>
  <li><em>How do I divide my list in pages?</em> 
   <p>There is no prefabricated solution for this, but using the 'undocumented' ${+ } feature it can be
   done relatively nicely in a taglib-only way. With for example the following list attributes:
<pre>
      offset="${+$page*$config.page_size}"  max="$config.page_size"
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
            &lt;a href='&lt;mm:url&gt;&lt;mm:param name="page" value="${+$page-1}" /&gt;&lt;/mm:url&gt;' &gt;
              previous page
      &lt;/a&gt;
    &lt;/mm:isgreaterthan&gt;
    &lt;mm:present referid="next_page"&gt;
            &lt;a href='&lt;mm:url&gt;&lt;mm:param name="page"  value="${+$page+1}" /&gt;&lt;/mm:url&gt;' &gt;
               next page
            &lt;/a&gt;
  </pre>
  </li>
  <li><em>If something is not possible with the MMBase taglib, you can alway use jsp (or other taglibs)!</em>
   <p>
    E.g. basic looping constructions are not really available in the MMBase taglib. You can use
    another taglib for this, or use JSP. For this the 'jspvar' (and 'vartype') attributes are available.
   </p>
   <p>
    A remarkable lot is possible though using MMBase taglib only. So we would like to discourage a
    'flee' to JSP too quickly.
   </p>
   </li>
   <li><em>Can I put the form, and the code to handle the form easily in one JSP page?</em>
    <p>
      Yes. Use the 'mm:present' and 'mm:notpresent' tags to separate the two pieces of code. Check
      on the submit value of the form or on some hidden value.
    </p>
   </li>
   <li><em>How do i reuse pieces of taglib/jsp code?</em>
    <p>
    The standard jsp &lt;%@ include %&gt; directive can be very useful to include small pieces of
    reusable taglib code. If for example you have lists of urls on several places in your site, you
    can put the taglib code to lay out such a list in a separate file, and &lt;%@-include it where
    you need it.
    </p>
    <p>
     Another possibility is to use the '<a href="../../../mmdocs/mmbase-taglib.html#include">mm:include</a>' tag. The such included page must be a stand alone
     taglib page (with its own &lt;@taglib directive and so on), and the result is simply included
     in your page. You can feed the mm:include-d page with the mm;param tag.
    </p>
    <p>
     If you prefer to cut and copy your way, that's fine too, of course :-)
    </p>
  </li>
  <li><em>What's the difference between listnodes and list, relatednodes and  related?</em>
    <p>
      The difference is that 'listnodes' and 'relatednodes' return real nodes, and the fields you
      can refer to simply by their name.  'list' and 'related' return 'cluster' nodes, which are
      nodes combined of several types. Fields must be prefixed by their element in the 'path'. See
      documentation of <a href="../../../mmdocs/mmbase-taglib.html#list">mm:list</a>.
    </p>
    <p>
      It is possible to get the 'real' nodes from the 'cluster' nodes of the 'list' and 'related'
      tag by use of a node tag with the element attribute. But be careful with this, because there
      is a good chance that it will do a new SQL query for each node then.
    </p>
    <p>
      If your page is too slow and your are using relatednodes in relatednodes in relatednodes, you
      might consider replacing it by one 'related'. Perhaps it is faster (NOTE: We should benchmark
      this!)  It won't be very good for the readability of your code though.
    </p>
    <p>
     The other big advantage of 'related' above 'relatednodes' is that you can indicate the 'role'
     of the relation easily.
    </p>
  </li>
</ul>

</body>

</html>

</mm:cloud>
