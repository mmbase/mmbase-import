<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard list.xsl 

  @version   $Id: list.xsl,v 1.4 2003-01-17 17:27:16 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6
  -->

  <xsl:import href="ew:xsl/list.xsl" /> <!-- extend from standard  editwizard xslt -->
 
  <xsl:template name="title"><!-- The first row of the the body's table -->
    <tr>
      <th colspan="2">
        <span class="title"><nobr>
          [[ <xsl:value-of select="$title" /> ]]</nobr>
        </span>
      </th>
    </tr>
  </xsl:template>   
</xsl:stylesheet>