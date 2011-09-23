<!--

  A utility XSL, to format dates. Override 'formatteddate' when you
  want to customize your own date format.

  @todo: Perhaps we can simply use java.util.SimpleDateFormat here to
  calculate the dates.

  @author Michiel Meeuwissen   
  @version $Id: formatteddate.xslt,v 1.2 2005-11-01 23:44:53 michiel Exp $
  @since   MMBase-1.6


-->
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >

  <xsl:param name="formatter_language">en</xsl:param> 
  <!-- perhaps it should not only accept a language, but a full LOCALE setting -->

  <xsl:template name="formatteddate">
    <xsl:param name="year"     />
    <xsl:param name="monthname" />
    <xsl:param name="day"    />
    <xsl:choose><!-- try to do something for the dateformat based on the language setting -->
      <xsl:when test="$formatter_language='nl'">
        <xsl:value-of select="concat($day,' ',$monthname,' ',$year)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($year, ' ', $monthname,' ',$day)" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- generate the possible parameters -->
  <xsl:template name="date">
    <xsl:param name="datetime" />

    <xsl:variable name="month" select="number(substring($datetime, 6, 2))" />
    <xsl:call-template name="formatteddate">
      <xsl:with-param name="year"  select="number(substring($datetime, 1, 4))" />
      <xsl:with-param name="month" select="$month" />
      <xsl:with-param name="day"   select="number(substring($datetime, 9, 2))" />
      
      <xsl:with-param name="monthname"><xsl:call-template name="monthname">
        <xsl:with-param name="month" select="$month" />
      </xsl:call-template></xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!-- convert a month number to a month name -->
  <xsl:template name="monthname">
    <xsl:param name="month" />
    <xsl:choose>
      <xsl:when test="$formatter_language='nl'">
        <xsl:choose>
          <xsl:when test="$month=1">januari</xsl:when>
          <xsl:when test="$month=2">februari</xsl:when>
          <xsl:when test="$month=3">maart</xsl:when>
          <xsl:when test="$month=4">april</xsl:when>
          <xsl:when test="$month=5">mei</xsl:when>
          <xsl:when test="$month=6">juni</xsl:when>
          <xsl:when test="$month=7">juli</xsl:when>     
          <xsl:when test="$month=8">augustus</xsl:when>     
          <xsl:when test="$month=9">september</xsl:when>     
          <xsl:when test="$month=10">oktober</xsl:when>     
          <xsl:when test="$month=11">november</xsl:when>     
          <xsl:when test="$month=12">december</xsl:when>     
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$month=1">january</xsl:when>
          <xsl:when test="$month=2">february</xsl:when>
          <xsl:when test="$month=3">march</xsl:when>
          <xsl:when test="$month=4">april</xsl:when>
          <xsl:when test="$month=5">may</xsl:when>
          <xsl:when test="$month=6">june</xsl:when>
          <xsl:when test="$month=7">july</xsl:when>     
          <xsl:when test="$month=8">august</xsl:when>     
          <xsl:when test="$month=9">september</xsl:when>     
          <xsl:when test="$month=10">october</xsl:when>     
          <xsl:when test="$month=11">november</xsl:when>     
          <xsl:when test="$month=12">december</xsl:when>     
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
