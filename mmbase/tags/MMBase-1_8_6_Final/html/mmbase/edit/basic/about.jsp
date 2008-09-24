<%@include file="page_base.jsp"
%><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<mm:cloud sessionname="$config.session">
  <mm:write referid="style" escape="none" />
  <title>About generic mmbase taglib editors</title>
</head>
<body class="basic">
  <table summary="taglib editors">
    <tr><th>The MMBase taglib editors</th></tr>
    <tr><td class="data">
    <p>
      These are the <a href="http://www.mmbase.org/" target="_blank">MMBase</a> generic editors, based on 
      <a href="http://www.mmbase.org/docs/mmci/api/index.html" target="_blank">MMCI</a> (version 1.2) with usage of 
      <a href="http://www.mmbase.org/docs/reference/taglib/toc.html" target="_blank">MMBase Taglib</a> (version 1.1.0)
        under the 
        <a href="<mm:url page="../../mpl-1.0.jsp" />" target="_blank">Mozilla License 1.0</a>
      </p>
      <p>
        version of the editors: 2005-2
      </p>
      <p>
        The MMBase 'basic' editors are the oldest and 'generic' editors implemented in JSP/Taglib,
        which are feature-rich and well-tested. In the mean time several alternatives were deviced like 
        <a href="../my_editors">`My editors'</a>, and `preditors'.
      <p>
        Features:
      </p>
      <ul>
        <li>Generic editing of MMBase content, using MMBase taglib and little JSP.</li>
        <li>Relations (with directionality).</li>
        <li>Image upload.</li>
        <li>Aliases.</li>
        <li>Searching on fields, alias and age.</li>
        <li>Configurable (a.o. language and the aspect).</li>
        <li>Can follow relations to 'navigate' through cloud.</li>
        <li>'Tree-view' of an object (use this with caution though).</li>
        <li>Advanced paging mechanism.</li>
      </ul>
      <p>
        Known bugs:
      </p>
      <ul>
        <li>Source code is a little too chaotic</li>
        <li>Heavy use of CSS to define the looks hinder functionallity in some buggy browsers</li>
      </ul>
      </td></tr>
    </table>
    <%@ include file="foot.jsp"  %>
  </mm:cloud>
</mm:content>