<%-- a div to include with related pages
--%><mm:present referid="page">
<div class="relatedpages">
<mm:list nodes="$page" path="pages1,posrel,pages2" searchdir="source">
  <mm:node element="pages2"><%-- <a href="<mm:url>
      <mm:param name="page"><mm:field name="number" /></mm:param>
      <mm:present referid="portal"><mm:param name="portal" value="$portal" /></mm:present>
  </mm:url>"><mm:field name="title" /></a> --%>
  <mm:first><mm:related path="posrel,pages" searchdir="destination"
    fields="posrel.pos,pages.number,pages.title"
    orderby="posrel.pos">
    <mm:context><mm:field name="pages.number" id="p" write="false" />
    <mm:compare referid="page" value="$p"><strong></mm:compare>
    <a href="<mm:url>
      <mm:param name="page"><mm:field name="pages.number" /></mm:param>
      <mm:present referid="portal"><mm:param name="portal" value="$portal" /></mm:present>
    </mm:url>"><mm:field name="pages.title" /></a><mm:compare referid="page" value="$p"></strong></mm:compare>
    <mm:last inverse="true">&nbsp;|&nbsp;</mm:last>
    </mm:context>
  </mm:related></mm:first>
  </mm:node>
</mm:list>
</div>
</mm:present>
