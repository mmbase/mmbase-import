<!--
  This translates mmbase XML, normally containing an objects tag.

  author: Michiel Meeuwissen   
-->
<xsl:stylesheet  version = "1.0" 
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction" 
>
  <xsl:import href="mmxf2xhtml.xslt" /><!-- dealing with mmxf is done there -->
  <xsl:import href="formatteddate.xslt" /><!-- dealing with dates is done there -->
  
  <xsl:param name="formatter_imgdb" /> <!-- this information is needed to correctly construct img.db urls -->
  
  <xsl:output method="xml" omit-xml-declaration="yes"  /><!-- xhtml is a form of xml -->

  <xsl:variable name="newstype" select="news" />
  
   <!-- If objects is the entrance to this XML, then only handly the root child of it -->
  <xsl:template match="objects"> 
    <xsl:apply-templates select="object[@id=current()/@root]" />
  </xsl:template>
  
   <!-- how to present a node -->
   <xsl:template match="object[@complete='true']">
    <xsl:for-each select="field">
      <xsl:apply-templates /><br />
    </xsl:for-each>
  </xsl:template>
  
  
   <!-- how to present a news node -->
  <xsl:template match="object[@type=$newstype and @complete='true']">
    <xsl:apply-templates select="field[@name='title']"    />
    <xsl:apply-templates select="field[@name='subtitle']" />
    <xsl:apply-templates select="field[@name='body']" />
  </xsl:template>
  

  <xsl:template match="object[@type=$newstype]/field[@name='title']" >
    <h1><xsl:value-of select="." /></h1>
  </xsl:template>
  
  <xsl:template match="object[@type=$newstype]/field[@name='subtitle']" >
    <h2><xsl:value-of select="." /></h2>
  </xsl:template>
  
  
  <!-- how to present a nodes that are related to paragraphs. -->
  <xsl:template match="object" mode="concise">
    <xsl:choose>
      <xsl:when test="@type='images'">
        <img src="{$formatter_imgdb}{node:function(., 'cache(s(100x100))')}" alt="{./field[@name='description']}" align="right" />          
      </xsl:when>
      <xsl:when test="@type='urls'">
        <br /><a href="{field[@name='url']}"><xsl:value-of select="field[@name='description']" /></a>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  

  <!--how to present nodes that are related to words -->
  <xsl:template match="object" mode="inline">
    <xsl:choose>
      <xsl:when test="@type='images'">
        <a href="{$formatter_imgdb}{./field[@name='number']}" alt="{./field[@name='description']}">plaatje</a>
      </xsl:when>
      <xsl:when test="@type='urls'">
        <a href="{field[@name='url']}"><xsl:value-of select="position()" /></a>
        <xsl:if test="position() &lt; last()"><xsl:text>, </xsl:text></xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <!-- An anchor can handle only urls links -->
  <xsl:template match="a" mode="sub">
    <xsl:param name="relatednodes" />
    <xsl:variable name="urls" select="$relatednodes[@type='urls']" />
    <xsl:choose>
      <xsl:when test="not($urls)"> 
        <!-- no relations found, simply ignore the anchor -->
        <xsl:apply-templates />
      </xsl:when>
      <xsl:when test="count($urls)=1">
        <!-- only one url is related, it is simple to make the body clickable -->
        <a href="{$urls/field[@name='url']}"><xsl:apply-templates  /></a>
      </xsl:when>
      <xsl:otherwise> 
        <!-- more than one url related to this anchor, we add between parentheses a list of links -->
        <xsl:apply-templates /> (<xsl:apply-templates select="$urls" mode="inline" />)
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  
  <!-- template to override mmxf tags with an 'id', we support links to it here -->
  <xsl:template match="p|a"> 
    <xsl:copy>	 	 
      <!-- find the nodes which are related to this node (by means of a 'descrel' -->
      <xsl:variable name="relatednodes" select="//objects/object[@id=//objects/object[@type='descrel' and 
        field[@name='name']=current()/@id and
        source/@object=current()/ancestor::object/@id]/destination/@object]" />
      
      <xsl:apply-templates select="." mode="sub">
        <xsl:with-param name="relatednodes" select="$relatednodes" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <!-- A paragraph can handle images and urls links -->
  <xsl:template match="p" mode="sub">
    <xsl:param name="relatednodes" />
    <xsl:apply-templates  select="$relatednodes[@type='images']"  mode="concise" />	
    <xsl:apply-templates />
    <xsl:if test="count($relatednodes[@type='urls']) &gt; 0">
      <br />---<xsl:apply-templates  select="$relatednodes[@type='urls']"  mode="concise" />
    </xsl:if>
  </xsl:template>
  


</xsl:stylesheet>
