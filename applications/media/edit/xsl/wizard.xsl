<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:output
    method="xml"
    version="1.0"
    encoding="utf-8"
    omit-xml-declaration="no"
    standalone="yes"
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//"
    indent="yes"
    />


  <xsl:template name="extrajavascript">
    <script language="javascript" src="{$referrerdir}style/streammanager.js.jsp?dir={$referrerdir}&amp;fragment={$objectnumber}"><xsl:comment>help IE</xsl:comment></script>
  </xsl:template>


    <!-- forms with 'itemize' will contain an embedded stream viewer -->
    
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title" />
        <xsl:call-template name="subtitle" />
          <tr>
            <td class="steps">
              <xsl:if test="/wizard/curform = 'itemize-embeded'">    
              <embed 
                id="player"
                src="{$referrerdir}display.ram.jsp?fragment={$objectnumber}" 
                width="200" 
                height="165"   
                type="audio/x-pn-realaudio-plugin"
                nojava="false" 
                controls="ImageWindow" 
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                name="embeddedplayer"></embed>
              <a href="javascript:void(0)" onClick="javascript:detach();">detach</a>
            </xsl:if>
            <xsl:if test="/wizard/curform = 'itemize-popup'">    

            </xsl:if>
          <table width="100%">
            <xsl:apply-templates select="/*/steps-validator" />
          </table>
        </td>
        <td valign="top" width="100%">
          <table width="100%">
            <xsl:apply-templates select="form[@id=/wizard/curform]" />
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>


  <xsl:template name="steps">
    <tr>
      <td>
        <xsl:for-each select="step">
          <p class="step">
            <xsl:call-template name="steptemplate" />
          </p>
        </xsl:for-each>
      </td>
    </tr>
  </xsl:template>


  
</xsl:stylesheet>