<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="asis">
<div
  class="component mm_c_core mm_c_b_welcome ${requestScope.className}"
  id="${requestScope.componentId}">
  <h2>Welcome to MMBase Content management system.</h2>
  <h3>You are running version: <mm:cloudinfo type="mmbaseversion" /></h3>
  <p>
    If you are running on the binary distribution: It is shipped with a database that is
    written in Java: <a href="http://www.hsqldb.org">Hsqldb</a>, configured for <strong>memory
    only</strong>.  MMBase does support both commercial and opensource databases and you must
    make a decision on which database you want to run an actual site. You can use HSQL for
    that, but you must configure it to write it's data to disk then. HSQL is useable for small
    and simple sites, for bigger sites consider something like MySQL or Postgresql.
  </p>
  <p>
    If you wish to build your own MMBase version from the sourcecode, you are encouraged to
    download the <em>source distribution</em>.
  </p>
  <p>If you run into problems you can get help from the following sources :</p>
  <ul>
    <li>Shipped documentation - the readme, releasenotes and installation docs. 
      These documents are in the root of the binary distro.</li>
    <li>The MMBase website - <a href="http://www.mmbase.org">http://www.mmbase.org</a>.</li>
    <li>Contact information -
      <a href="http://www.mmbase.org/communication">http://www.mmbase.org/communication</a>.</li>
    <li>The mailinglists - <a href="http://lists.mmbase.org">http://lists.mmbase.org</a>.</li>
    <li>The developers irc channel - see <a href="http://www.mmbase.org/irc">irc page for mmbase</a>.</li>
    <li>The MMBase bugtracker - <a href="http://www.mmbase.org/bug">http://www.mmbase.org/bug</a>.</li>
  </ul>
  <p><em> -- The MMBase Release Team.</em></p>
</div>
</mm:cloud>
