<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->


  <xsl:template name="body"> 
    <body onload="doOnLoad_ew();init('{/wizard/curform}');" onunload="doOnUnLoad_ew();">
      <xsl:call-template name="bodycontent" />
    </body>
  </xsl:template>


  <xsl:template name="realposition">    
  <span style="width:128;">
    <nobr><input type="text" name="{@fieldname}" value="{value}" class="input" onkeyaup="validate_validator(event);" onblur="validate_validator(event);">
    <xsl:apply-templates select="@*" />
    </input><input type="button" value="get" onClick="document.forms['form'].elements['{@fieldname}'].value = parent.frames['player'].document.embeddedplayer.GetPosition();" /></nobr>
  </span>
  </xsl:template>


    <!-- forms with 'itemize' will contain an embedded stream viewer -->
    
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title" />
        <xsl:call-template name="subtitle" />
          <tr>
            <td class="steps">
              <xsl:if test="/wizard/curform = 'itemize'">    
              <!-- embed 
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
                name="embeddedplayer"></embed -->
            </xsl:if>
            <xsl:if test="/wizard/curform = 'itemize-popup'">

              <a href="javascript:void(0)" onClick="javascript:detach();">detach</a>
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