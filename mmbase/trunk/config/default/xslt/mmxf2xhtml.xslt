<!--
  This translates a mmbase XML field to XHTML. So if you have MMXF
  fields in your datbase, this describes how they are presented as XHTML.
 
  $Id:
  
  Michiel Meeuwissen
-->
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >
   <xsl:output method="xml" omit-xml-declaration="yes"  />

   <xsl:param name="formatter_xsltpath">/mmbase/config/xslt</xsl:param>

   <xsl:template match = "mmxf" >
      <xsl:apply-templates select = "p|section" />
   </xsl:template>

   <xsl:template match = "p">
      <xsl:copy-of select = "." />
   </xsl:template>

   <xsl:template match = "em"> 
     <xsl:copy>
   	  <xsl:apply-templates />
     </xsl:copy>
   </xsl:template>

   <xsl:template match = "section" >
     <xsl:if test="count(ancestor::section)=0"><h3><xsl:value-of select="@title" /></h3></xsl:if>
     <xsl:if test="count(ancestor::section)=1"><p><b><xsl:value-of select="@title" /></b></p></xsl:if>
     <xsl:if test="count(ancestor::section)=2"><p><xsl:value-of select="@title" /></p></xsl:if>
     <xsl:if test="count(ancestor::section)>2"><xsl:value-of select="@title" /><br /></xsl:if>
 	 <xsl:apply-templates select = "section|p" />
   </xsl:template>

</xsl:stylesheet>
