<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard wizard.xsl 

  @version   $Id: wizard.xsl,v 1.1 2008-08-06 16:17:13 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$templatedir}/style/color/wizard.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$templatedir}/style/extra/wizard.css" />
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script language="javascript" src="{$templatedir}/{$javascriptdir}override.js"><xsl:comment>help IE</xsl:comment></script>
  </xsl:template>

 <!-- The first row of the the body's table -->
  <xsl:template name="title">
      <td>        
        <span class="title"><nobr><a href="{$referrerdir}" class="tools">Redactietools</a>: 
        <span class="titleprompt" ><xsl:value-of select="$title" /></span>
         </nobr></span>
      </td>
     <td>
        <xsl:if test="$debug='true'"><a href="debug.jsp{$sessionid}?sessionkey={$sessionkey}&amp;popupid={$popupid}" target="_blank" class="step">[debug]</a></xsl:if>
     </td>
  </xsl:template>
  
  <!-- The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one -->
  <xsl:template name="formcontent">
    <table class="advancedform">
      <tr>
        <td>
    <div id="editform" class="editform">
      <table class="formcontent">
        <xsl:apply-templates select="form[@id=/wizard/curform]"/>
        <xsl:choose>
          <xsl:when test="not(/wizard/form[@id=/wizard/nextform])">
            <xsl:call-template name="publish_step" />
          </xsl:when>
         </xsl:choose>
      </table>
    </div>
        </td>
        <td style="width: 1%; background-color: #000000; color: #FFFFFF;">
    <div id="stepsbar" class="stepscontent" style="height: 100%">
      <xsl:apply-templates select="/*/steps-validator"/>
    </div>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="form">
    <xsl:for-each select="fieldset|field">
      <xsl:choose>
        <xsl:when test="name()='field'">
          <tr class="fieldcanvas">
            <xsl:apply-templates select="." />
          </tr>
        </xsl:when>
        <xsl:when test="name()='fieldset'">
          <tr class="fieldsetcanvas">
            <xsl:apply-templates select="." />
          </tr>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="." />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:for-each select="list">
        <xsl:apply-templates select="." />
    </xsl:for-each>    
  </xsl:template>

  <xsl:template name="publish_step">
     <xsl:choose>
      <xsl:when test="title='title of the editwizard to add some info'">
        <tr>
          <td class="fieldprompt">Some info</td>
          <td class="field">Some text</td>
        </tr>
        <tr>
          <td class="fieldprompt">More info</td>
          <td class="field">More text</td>
        </tr>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

 <xsl:template match="steps-validator">
   <!-- when multiple steps, otherwise do nothing -->
    <table class="stepscontent">
      <tr>
        <td>
          <xsl:variable name="stepcount" select="count(step)" />
          <xsl:for-each select="step">
            <!-- p -->
              <!-- xsl:call-template name="stepsattributes" / -->
              <!-- xsl:variable name="schemaid" select="@form-schema" / -->
              <xsl:choose>         
                <xsl:when test="@form-schema=/wizard/curform">
                  <xsl:call-template name="prompt_step" />&#160;van&#160;<xsl:value-of select="$stepcount" />
                  <br />
                  <xsl:value-of select="/*/form[@id=current()/@form-schema]/title" />
                </xsl:when>
              </xsl:choose>
            <!-- /p -->
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <td>
          <ul>
            <xsl:if test="count(step) &gt; 1">
              <xsl:call-template name="nav-buttons" />
            </xsl:if>
            <xsl:call-template name="buttons" />
          </ul>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="buttons">
    <li><xsl:call-template name="cancelbutton" /></li>
    <li><xsl:call-template name="savebutton" /></li>
  </xsl:template>
 
  <xsl:template name="nav-buttons">
    <xsl:call-template name="previousbutton" />
    <xsl:call-template name="nextbutton" />
  </xsl:template>

  <xsl:template name="previousbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/prevform]">
        <li><a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/prevform}')" title="{$tooltip_previous} '{/wizard/form[@id=/wizard/prevform]/title}'"><xsl:call-template name="prompt_previous" /></a></li>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="nextbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/nextform]">
        <li><a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/nextform}')" title="{$tooltip_next} '{/wizard/form[@id=/wizard/nextform]/title}'"><xsl:call-template name="prompt_next" /></a></li>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="list">
      <tr class="itemcanvas">
        <td  class="listprompt">
          <xsl:call-template name="listprompt"/>
        </td>
        <td>
          <xsl:call-template name="listitems"/><br/>
          <xsl:call-template name="listsearch"/><br/>
          <xsl:call-template name="listnewbuttons"/>
        </td>
      </tr>
  </xsl:template>
  
  <xsl:template match="value" mode="line">
       <xsl:param name="val" select="." />
       <span style="width: 400;">
         <xsl:apply-templates select="../prefix" />
         <xsl:value-of select="$val" disable-output-escaping="yes" />
         <xsl:apply-templates select="../postfix" />
       </span>
   </xsl:template>

  <xsl:template name="prompt_add_wizard">
    <img src="{$mediadir}new.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_add_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>

</xsl:stylesheet>