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
  <span style="width:128;">
    <nobr><input type="text" name="{@fieldname}" value="{value}" class="input" onkeyaup="validate_validator(event);" onblur="validate_validator(event);">
    <xsl:apply-templates select="@*" />
    </input><input type="button" value="get" onClick="document.forms['form'].elements['{@fieldname}'].value = parent.frames['player'].document.embeddedplayer.GetPosition();" /></nobr>
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


  
</xsl:stylesheet>