<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->


  <xsl:template name="body"> 
  <body onload="doOnLoad_ew();init('{/wizard/curform}');" onunload="doOnUnLoad_ew();">
    <xsl:call-template name="bodycontent" />
    </body>
  </xsl:template>


  <xsl:template name="realposition">
  <span class="realpositionitem">
    <nobr><input type="text" name="{@fieldname}" value="{value}" class="input" onkeyaup="validate_validator(event);" onblur="validate_validator(event);">
    <xsl:apply-templates select="@*" />
    </input> ms
    <input type="button" value="get" onClick="document.forms['form'].elements['{@fieldname}'].value = parent.frames['player'].document.embeddedplayer.GetPosition();" />
    <input type="button" value="set" onClick="parent.frames['player'].document.embeddedplayer.setPosition(document.forms['form'].elements['{@fieldname}'].value);" />
    </nobr>
  </span>
  </xsl:template>

  
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title" />
      <xsl:call-template name="subtitle" />
      <tr>
        <td class="steps">
          <table width="100%">
            <xsl:apply-templates select="/*/steps-validator" />
          </table>
        </td>
        <td valign="top" width="100%">
          <table width="100%">
            <xsl:apply-templates select="form[@id=/wizard/curform]" />
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>


  <xsl:template name="steps">
    <tr>
      <td>
        <xsl:for-each select="step">
          <p class="step">
            <xsl:call-template name="steptemplate" />
          </p>
        </xsl:for-each>
      </td>
    </tr>
  </xsl:template>

 <!-- The appearance of one 'step' button -->
  <xsl:template name="step">
    <a>
      <xsl:call-template name="stepaattributes" />
      <xsl:call-template name="prompt_step" />
      </a><br />
      <xsl:call-template name="i18n"><xsl:with-param name="nodes" select="/*/form[@id=current()/@form-schema]/title" /></xsl:call-template>
  </xsl:template>


 <xsl:template name="buttons">
    <tr>
      <td colspan="2">
        <hr />
        <p>
          <xsl:call-template name="cancelbutton" />
        </p>
        <p>
          <xsl:call-template name="savebutton" />
        </p>
      </td>
    </tr>    
  </xsl:template>
  
</xsl:stylesheet>