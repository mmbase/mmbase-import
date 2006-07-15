<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/searchlist.xsl" /> <!-- extend from standard  editwizard xslt -->

  <xsl:template name="style">
    <!-- remove old style -->
  </xsl:template>

  <xsl:param name="age" />
	<!-- searchvalue: vereist voor correcte werking aanpassing in javascript/editwizard.jsp, nog niet ingecheckt in 1.7  - 8/4/2005 -->
  <xsl:param name="searchvalue" />

  <xsl:template name="body">
	  <tr>
		  <td>
			  <div class="searchresult_title">
				  U zocht op: '<xsl:value-of select="$searchvalue" disable-output-escaping="yes"  />' in <xsl:value-of select="$title" disable-output-escaping="yes"  /> (
					<xsl:call-template name="prompt_age" >
						<xsl:with-param name="age" select="$age" />
					</xsl:call-template>
					)
			  </div>
			</td>
		</tr>
    <tr>
  	  <td>
    <xsl:apply-templates select="list" />
      </td>
    </tr>
  </xsl:template>

	<!-- wijziging, hoort in de 1.7 branche, maar nog niet ingecheckt - 8/4/2005 -->
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
					  <xsl:call-template name="searchcancelbutton" />
          </td>
          <td align="right" valign="top" class="button">
					  <xsl:call-template name="searchokbutton" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

	<!-- wijziging, hoort in de 1.7 branche, maar nog niet ingecheckt 8/4/2005 -->
  <xsl:template name="searchcancelbutton">
		<input
			type="button"
			name="cancel"
			value="{$tooltip_cancel_search}"
			onclick="closeSearch();"
			class="button" />
	</xsl:template>

  <xsl:template name="searchokbutton">
		<xsl:value-of select="$tooltip_end_search" disable-output-escaping="yes"  />
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<input class="button" onclick="dosubmit();" value="Toevoegen" name="ok" type="image" src="{$mediadir}relations_new.gif" />
	</xsl:template>

</xsl:stylesheet>