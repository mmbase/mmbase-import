<xsl:stylesheet 
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" 
  version = "1.0" 
  xmlns:mmxf="http://www.mmbase.org/mmxf"
  exclude-result-prefixes="mmxf" 
>
  <xsl:output method="xml" 
    indent="yes"
    omit-xml-declaration="yes"  /><!-- xhtml is a form of xml -->
    

  <xsl:param name="formatter_counter"></xsl:param>

  <xsl:template match="mmxf">
    <xsl:apply-templates select="section" mode="entrance" />
  </xsl:template>

  

  <xsl:template match="section" mode="entrance">

    <xsl:apply-templates select="." mode="thetoc" />
    <div class="content" style="visibility: visible;">
      <xsl:attribute name="id"><xsl:value-of select="generate-id(.)" /></xsl:attribute>
      <xsl:apply-templates select="h|p|ul" />
    </div>
    <xsl:apply-templates select="section" />
  </xsl:template>

  <xsl:template match="section" mode="thetoc">
    <!-- generate a table of content -->    
    <table class="toc">
      <tr>
        <td>
          <xsl:apply-templates select="." mode="link" />
        </td>
      <xsl:for-each select="section">
        <td>
          <xsl:apply-templates select="."       mode="link" />
          <xsl:apply-templates select="section" mode="toc" />
        </td>
        </xsl:for-each>
    
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="section" mode="link">
    <a>
      <xsl:attribute name="href">#</xsl:attribute>
      <xsl:attribute name="onClick">
        var collection  = document.getElementsByTagName('div'); 
        for(var i = 0; i &lt; collection.length; i++) { 
        if (collection[i].className  == 'content') collection[i].style.visibility = 'hidden'; 
        } 
        document.getElementById('<xsl:value-of select="generate-id(.)" />').style.visibility = 'visible';
      </xsl:attribute>
      <xsl:apply-templates select="h[1]" mode="simple"/>
    </a>
  </xsl:template>

  <xsl:template match="section" mode="toc">
    <ul>
      <li>
        <xsl:apply-templates select="."       mode="link" />
        <xsl:apply-templates select="section" mode="toc" />
      </li>
    </ul>
  </xsl:template>

  <xsl:template match="section">
    <div class="content">
      <xsl:attribute name="id"><xsl:value-of select="generate-id(.)" /></xsl:attribute>
      <xsl:apply-templates select="h|p|ul" />
    </div>
    <xsl:apply-templates select="section" />
  </xsl:template>

  <xsl:template match="*" mode="simple">   
    <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="h" mode="simple">    
    <xsl:apply-templates select="node()" mode="simple" />
  </xsl:template>

   <xsl:template match = "h" mode="h1">
     <h3 style="color:red;">
       <xsl:apply-templates select="node()" />
     </h3>
   </xsl:template>

   <xsl:template match = "h" mode="h2"><xsl:apply-templates select="." mode="h1" /></xsl:template>
     
</xsl:stylesheet>
