<!--
  This translates a mmbase XML field to XHTML. So if you have MMXF
  fields in your datbase, this describes how they are presented as XHTML.
 
  @version $Id: mmxf2xhtml.xslt,v 1.5 2002-06-25 15:41:10 michiel Exp $  
  @author Michiel Meeuwissen
-->
<xsl:stylesheet 
  version = "1.0" 
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" 

>
  <xsl:output 
    method="xml" 
    omit-xml-declaration="yes"  
    />
   

    <xsl:template match = "mmxf" >
      <xsl:apply-templates select = "p|section" />
    </xsl:template>
        

    <xsl:template match ="p|section|ul|li|em">
      <xsl:copy-of select="." />
    </xsl:template>

  <xsl:template match = "section" >
    <xsl:if test="count(ancestor::section)=0"><h3><xsl:value-of select="@title" /></h3></xsl:if>
    <xsl:if test="count(ancestor::section)=1"><p><b><xsl:value-of select="@title" /></b></p></xsl:if>
    <xsl:if test="count(ancestor::section)=2"><p><xsl:value-of select="@title" /></p></xsl:if>
    <xsl:if test="count(ancestor::section)>2"><xsl:value-of select="@title" /><br /></xsl:if>
    <xsl:apply-templates select = "section|p" />
  </xsl:template>
  
</xsl:stylesheet>
