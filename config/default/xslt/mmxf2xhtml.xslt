<!--
  This translates a mmbase XML field to XHTML. So if you have MMXF
  fields in your datbase, this describes how they are presented as XHTML.
 
  @version $Id: mmxf2xhtml.xslt,v 1.4 2002-06-24 13:55:12 michiel Exp $  
  @author Michiel Meeuwissen
-->
<xsl:stylesheet 
  version = "1.0" 
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" 
  xmlns:mmxf="http://www.mmbase.org/mmxf"
  exclude-result-prefixes="mmxf" 

>
  <xsl:output 
    method="xml" 
    omit-xml-declaration="yes"  
    />
   

    <xsl:template match = "mmxf:mmxf" >
      <xsl:apply-templates select = "mmxf:p|mmxf:section" />
    </xsl:template>
      
    <xsl:template match = "mmxf:li|mmxf:ul|mmxf:em|mmxf:p"> 
      <xsl:element name="{local-name()}">
        <xsl:apply-templates />
      </xsl:element>
    </xsl:template>
  
  <xsl:template match = "mmxf:section" >
    <xsl:if test="count(ancestor::mmxf:section)=0"><h3><xsl:value-of select="@mmxf:title" /></h3></xsl:if>
    <xsl:if test="count(ancestor::mmxf:section)=1"><p><b><xsl:value-of select="@mmxf:title" /></b></p></xsl:if>
    <xsl:if test="count(ancestor::mmxf:section)=2"><p><xsl:value-of select="@mmxf:title" /></p></xsl:if>
    <xsl:if test="count(ancestor::mmxf:section)>2"><xsl:value-of select="@mmxf:title" /><br /></xsl:if>
    <xsl:apply-templates select = "mmxf:section|mmxf:p" />
  </xsl:template>
  
</xsl:stylesheet>
