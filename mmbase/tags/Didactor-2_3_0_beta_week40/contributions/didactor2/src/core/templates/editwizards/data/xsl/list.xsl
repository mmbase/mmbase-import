<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--

    @version $Id: list.xsl,v 1.3 2006-11-27 12:22:36 mmeeuwissen Exp $
  -->

  <xsl:import href="ew:xsl/list.xsl" />

  <xsl:template match="list">
    <xsl:call-template name="searchbox" />
    <tr class="listcanvas">
      <td>
        <xsl:call-template name="dolist" />
      </td>
    </tr>
    <xsl:if test="count(/*/pages/page) &gt; 1">
      <tr class="pagescanvas">
        <td>
          <div>
            <xsl:apply-templates select="/*/pages" />
            <br />
            <br />
          </div>
        </td>
      </tr>
    </xsl:if>
    <tr class="buttoncanvas">
      <td>
        <xsl:if test="$searchfields=&apos;&apos; and $creatable=&apos;true&apos;">
          <br />
          <div width="100%" align="left">
            <a
              href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}"
              title="{$tooltip_new}">
              <xsl:call-template name="prompt_new" />
            </a>
          </div>
        </xsl:if>
      </td>
    </tr>
    <!-- linkcanvas was removed here  (bug '141')-->
  </xsl:template>

</xsl:stylesheet>