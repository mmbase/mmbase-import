<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction">

  <xsl:import href="xsl/searchlist.xsl" />

  <xsl:param name="relationOriginNode">-1</xsl:param>
  <xsl:param name="relationRole"></xsl:param>
  <xsl:param name="relationCreateDir"></xsl:param>
  
  <xsl:template name="javascript">
    <script type="text/javascript" src="{$javascriptdir}searchlist.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$javascriptdir}tools.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript">
      window.status = "<xsl:value-of select="tooltip_select_search" />";
      var listpage = "<xsl:value-of disable-output-escaping="yes" select="$listpage" />";
      var searchtype = getParameter_general("type", "objects");
      var searchterm = getParameter_general("searchterm", "nothing");
      var cmd = getParameter_general("cmd", "");
      var selected = getParameter_general("selected", "");
      var relationRole = getParameter_general("relationRole", "");
      var relationOriginNode = getParameter_general("relationOriginNode", "");
      var relationCreateDir = getParameter_general("relationCreateDir", "");
    </script>

    <script type="text/javascript" src="{$javascriptdir}searchwindow.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
  </xsl:template>

  <xsl:template name="searchnavigation">
    <div id="searchnavigation" class="searchnavigation">
      <table class="searchnavigation">
        <tr>
          <td colspan="2">
            <xsl:apply-templates select="pages" />

            <xsl:if test="/list/@showing">
              <xsl:text></xsl:text>
              <span class="pagenav">
                <xsl:call-template name="prompt_more_results" />
              </span>
            </xsl:if>
            <xsl:if test="not(/list/@showing)">
              <xsl:text></xsl:text>
              <span class="pagenav">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <xsl:call-template name="prompt_result_count" />
              </span>
            </xsl:if>
          </td>
        </tr>
        <tr>
          <td>
            <input
              type="button"
              name="cancel"
              value="{$tooltip_cancel_search}"
              onclick="closeSearch();"
              class="button" />
          </td>
          <td align="right" valign="top">
            <input
              type="button"
              name="ok"
              value="{$tooltip_end_search}"
              onclick="doAddSubmit('{$relationOriginNode}', '{$relationRole}', '{$relationCreateDir}');"
              class="button" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

  
</xsl:stylesheet>
