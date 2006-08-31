<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->


  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); startHtmlArea();init('<xsl:value-of select="/wizard/curform" />');</xsl:variable>


  <xsl:template name="realposition">
  <span class="realpositionitem">
    <xsl:variable name="thisprompt"><!--<xsl:call-template name="i18n"><xsl:with-param name="nodes" select="prompt" /></xsl:call-template>--></xsl:variable>
    <nobr>
	<xsl:if test="@dttype != 'string'"> <!-- e.g. field ftype="realposition" name="title" (just to get the 'view' link ) -->

    <input type="button" value="{$button_current}{$thisprompt}"  title="{$tooltip_current}"  onClick="document.forms['form'].elements['{@fieldname}'].value = getPosition();" />

    <xsl:if test="not(preceding-sibling::field[@ftype='realposition' and @dttype='long'])">
      <input type="button" value="{$button_start}{$thisprompt}"  title="{$tooltip_start}"    onClick="document.forms['form'].elements['{@fieldname}'].value = 0;" />
    </xsl:if>
    <xsl:if test="not(following-sibling::field[@ftype='realposition'])">
      <input type="button" value="{$button_end}{$thisprompt}"    title="{$tooltip_end}" onClick="document.forms['form'].elements['{@fieldname}'].value = getLength();" />
    </xsl:if>


     <xsl:if test="following::field[@ftype='realposition']">
       <xsl:if test="not(following-sibling::field[@ftype='realposition'])">
       <input type="button" value="{$button_next}{$thisprompt}"  title="{$tooltip_next}"     onClick="document.forms['form'].elements['{@fieldname}'].value = document.forms['form'].elements['{following::field[@ftype='realposition']/@fieldname}'].value;" />        
       </xsl:if>
     </xsl:if>
     <xsl:if test="preceding::field[@ftype='realposition' and @dttype='long']">
       <xsl:if test="not(preceding-sibling::field[@ftype='realposition' and @dttype='long'])">
       <input type="button" value="{$button_previous}{$thisprompt}"  title="{$tooltip_previous}"    onClick="document.forms['form'].elements['{@fieldname}'].value = document.forms['form'].elements['{preceding::field[@ftype='realposition'][1]/@fieldname}'].value;" />        
       </xsl:if>
     </xsl:if>

     <img height="10" src="{$referrerdir}media/a_right.gif" alt="-&gt;" />
    <input type="text" name="{@fieldname}" value="{value}" class="input" onBlur="validate_validator(event);">
     <xsl:apply-templates select="@*" />
    </input> ms
     <img height="10" src="{$referrerdir}media/a_right.gif" alt="-&gt;" />      
     <input class="check" type="button" value="{$button_check}{$thisprompt}" title="{$tooltip_check}" onClick="setPosition(document.forms['form'].elements['{@fieldname}'].value);" />

     </xsl:if>
	 <xsl:if test="@dttype='string'">
	 <span class="normal">
        <xsl:if test="@maywrite!='false'">
          <xsl:apply-templates select="value" mode="inputline"/>
        </xsl:if>
        <xsl:if test="@maywrite='false'">
          <span class="readonly">
            <xsl:apply-templates select="value" mode="value"/>
          </span>
        </xsl:if>
		</span>	
	 </xsl:if>

     <xsl:if test="not(following-sibling::field[@ftype='realposition'])">
       <xsl:if test="substring(@number, 1, 1) != 'n'"><!-- cannot view if not commited -->
         <br />
         <a target="_new" href="{$referrerdir}view/showurls.jsp?fragment={@number}">view</a>         
       </xsl:if>
    </xsl:if>


    </nobr>
  </span>
  </xsl:template>

      
  <!-- Media-items must be overridable, because there is no good generic sollution forewards compatible yet -->  
  <xsl:template name="mediaitembuttons">
    <xsl:if test="@displaytype='audio'">
      <a target="left" href="{$referrerdir}player.jsp?fragment={field/@number}&amp;language={$language}" title="{$tooltip_audio}"><xsl:call-template name="prompt_audio" /></a>
    </xsl:if>
    <xsl:if test="@displaytype='video'">
      <a target="left" href="{$referrerdir}player.jsp?fragment={field/@number}&amp;language={$language}" title="{$tooltip_video}"><xsl:call-template name="prompt_video" /></a>
    </xsl:if>
  </xsl:template>

  
</xsl:stylesheet>
