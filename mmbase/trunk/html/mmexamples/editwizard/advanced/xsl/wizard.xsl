<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard wizard.xsl 

  @version   $Id: wizard.xsl,v 1.1 2002-07-15 12:23:29 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->

  <!-- The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one -->
  <xsl:template name="formcontent">
    <table>
      <xsl:call-template name="superhead" />       
      <tr>
        <td />
        <td>
          <span class="head">
            <xsl:value-of select="form[@id=/wizard/curform]/subtitle" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </span>
        </td>
      </tr>
      <tr>
        <td valign="top">
          <table>
          <xsl:apply-templates select="/*/steps-validator" />
          </table>
        </td>
        <td valign="top">
          <table>
            <xsl:apply-templates select="form[@id=/wizard/curform]" />
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="steps">
    <tr>
      <td>
        <!-- all steps -->
        <xsl:for-each select="step">
          <p>
            <xsl:call-template name="stepbutton" />
          </p>
        </xsl:for-each>
      </td>
    </tr>
  </xsl:template>



</xsl:stylesheet>