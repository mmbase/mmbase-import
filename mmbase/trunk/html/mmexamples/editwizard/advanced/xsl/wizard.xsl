<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard wizard.xsl 

  @version   $Id: wizard.xsl,v 1.2 2002-07-19 14:19:20 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->

  <!-- The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one -->
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title" />
      <xsl:call-template name="subtitle" />
      <tr>
        <td class="mysteps">
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

  <xsl:template name="step">
    <a>
      <xsl:call-template name="stepaattributes" />
      <xsl:call-template name="prompt_step" />
      </a><br />
    <xsl:value-of select="/*/form[@id=current()/@form-schema]/title" />
  </xsl:template>

  <xsl:template name="buttons">
    <tr>
      <td class="mybuttons">
        <xsl:call-template name="cancelbutton" /><br />
        <xsl:call-template name="savebutton" />
      </td>
    </tr>    
  </xsl:template>


</xsl:stylesheet>