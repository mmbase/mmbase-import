<!--
  This translates mmbase XML, normally containing an objects tag.

  author: Michiel Meeuwissen   
-->
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >
  <xsl:import href="mm:xslt/2xhtml.xslt" />  
  <xsl:output method="xml" omit-xml-declaration="yes"  /><!-- xhtml is a form of xml -->
    

  <xsl:template name="formatteddate">
	<xsl:param name="year"     />
	<xsl:param name="monthname" />
	<xsl:param name="day"    />
	-<xsl:value-of select="concat($day,' ',$monthname,' ',$year)" />-
  </xsl:template>

  <!-- how to present a news node -->
  <xsl:template match="object[@type='xmlnews' and @complete='true']">
	<xsl:apply-templates select="field[@name='title']"  />
	<h2><font color="green"><xsl:apply-templates select="field[@name='subtitle']" /></font></h2>
	<xsl:apply-templates select="field[@name='body']" />
	<p>
      <xsl:call-template name="date">     
	    <xsl:with-param name="datetime" select="field[@name='date']" />
      </xsl:call-template>
      </p>
  </xsl:template>

   <xsl:template match = "section" >
     <xsl:if test="count(ancestor::section)=0"><h3><font color="red"><xsl:value-of select="@title" /></font></h3></xsl:if>
     <xsl:if test="count(ancestor::section)=1"><p><b><xsl:value-of select="@title" /></b></p></xsl:if>
     <xsl:if test="count(ancestor::section)=2"><p><xsl:value-of select="@title" /></p></xsl:if>
     <xsl:if test="count(ancestor::section)>2"><xsl:value-of select="@title" /><br /></xsl:if>
 	 <xsl:apply-templates select = "section|p" />
   </xsl:template>

</xsl:stylesheet>
