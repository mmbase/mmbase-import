<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard list.xsl 

  @version   $Id: list.xsl,v 1.3 2002-07-11 08:29:34 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/list.xsl" /> <!-- extend from standard  editwizard xslt -->
 
  <xsl:template name="superhead"><!-- The first row of the the body's table -->
    <tr>
      <td />
      <td>
        <table width="100%">
          <tr>
            <td class="superhead"><nobr>[[ <xsl:value-of select="$title" /> ]]</nobr></td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>   
</xsl:stylesheet>