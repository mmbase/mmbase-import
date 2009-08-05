<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:import externid="main">bundles</mm:import>
<!-- first the selection part -->
<table cellpadding="0" cellspacing="0" class="list" 
	style="margin-top: 30px; margin-bottom: 30px;" width="95%">
<tr>
 <th>MMBase Packages</th>
</tr><tr>
  <td>
  <h5>Package overview</h5>
  MMBase packages can be downloaded from an number of places.<br/>
  <ul>
  	<li>applications: these are packages which can be found in the MMBase CVS and are maintained by the community.
	See the page for downloading MMBase releases for the list of applications.
	E.g. <a href="/development/download/build_page.jsp?dir=head/MMBase-1.8.0.final">the 1.8 release</a>
	</li>
	<li>contributions: these are packages which are maintained by third parties and can be found in the MMBase CVS.
	See <a href="http://cvs.mmbase.org/viewcvs/contributions/">the contributions directory in the MMBase CVS</a>
	</li>
	<li>tools on sourceforge: some MMBase packages and tools have been placed on the MMBase project on sourceforge. See
	<a href="http://mmapps.sourceforge.net/">mmapps.sourceforge.net</a>
	</li>
	<li>tools that are only available as downloadable zips: some MMBase applications developed by third parties are available as downloadable zips.
	Good examples are the MMBase FlashChat Jircs and EGEMMAIL. See the list below and contact the developers in case you have questions, if you would like to know
	whether there is a new version or when they are going to put their package in CVS.
	</li>
	<li>tools in the speeltuin (= playground, en; kindergarten, de): some of the committers maintain some utility tools
	in <a href="http://cvs.mmbase.org/viewcvs/speeltuin/">the MMBase speeltuin</a>.
	</li>
  </ul>
  If your package is not on this page and you feel it should be or you have extra information on your package mail me 
  at <a href="mailto:hangyi@xs4all.nl">hangyi@xs4all.nl</a> and I will update this list.
  <h5>Hot deployable and peer-to-peer</h5>
  In 2004/2005 some work has been done to create a package framework by which packages would be hot-deployable and could be distributed in a peer-to-peer network.
  This apps2 project succeeded and created some fancy deliverables, like a thememanager. However we came to the conclusion that hot-deployable packages does not fit the 
  actual requirements of most MMBase hosting situations. The apps2 project is therefore put on hold. If you like to have a look at the packagemanager you
  can find it at <a href="http://packages.mmbase.org/mmbase/packagemanager/public">preview of the new packaging system</a>.
  </td>
</tr>
</table>
<%-- <mm:compare referid="main" value="bundles"><%@ include file="bundles.jsp" %></mm:compare>
<mm:compare referid="main" value="bundle"><%@ include file="bundle.jsp" %></mm:compare> --%>
<%@ include file="packages.jsp" %>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
