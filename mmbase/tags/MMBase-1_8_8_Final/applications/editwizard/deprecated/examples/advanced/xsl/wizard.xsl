<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!--
  Demonstrating overriding of standard wizard.xsl

  @version   $Id: wizard.xsl,v 1.1 2003-12-19 11:10:07 nico Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->
  <xsl:variable name="searchagetype">edit</xsl:variable>

  <!-- The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one -->
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title" />
      <tr>
        <td valign="top" width="100%">
          <table width="100%">
                <xsl:apply-templates select="form[@id=/wizard/curform]" />
                <xsl:choose>
                        <xsl:when test="not(/wizard/form[@id=/wizard/nextform])">
                                <xsl:call-template name="publish_step" />
                        </xsl:when>
                </xsl:choose>
          </table>
        </td>
        <td class="mysteps_top">
          <table width="100%">
            <xsl:apply-templates select="/*/steps-validator" />
          </table>
        </td>
      </tr>
    </table>
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

 <!-- The first row of the the body's table -->
  <xsl:template name="title">
    <tr>
      <td class="mysteps_top">
        <span class="title"><nobr><a href="{$referrer}" class="step">Redactietools</a>:
        <span class="titleprompt" ><xsl:value-of select="title" /></span>
                                 </nobr></span>
      </td>
                 <td class="mysteps_top">
        <xsl:if test="$debug='true'"><a href="debug.jsp{$sessionid}?sessionkey={$sessionkey}&amp;popupid={$popupid}" target="_blank" class="step">[debug]</a></xsl:if>
                 </td>
                </tr>
  </xsl:template>

 <xsl:template match="steps-validator">
   <!-- when multiple steps, otherwise do nothing -->
   <tr>
       <td>
           <xsl:variable name="stepcount" select="count(step)" />
           <xsl:for-each select="step">
           <p>
                <xsl:call-template name="stepaattributes" />
                <xsl:variable name="schemaid" select="@form-schema" />
                <xsl:choose>
                  <xsl:when test="@form-schema=/wizard/curform">
                        <xsl:call-template name="prompt_step" />&#160;van&#160;<xsl:value-of select="$stepcount" />
                     <br />
                    <xsl:value-of select="/*/form[@id=current()/@form-schema]/title" />
                  </xsl:when>
                </xsl:choose>
           </p>
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

 <!--
       What to do with 'lists'.
       @bad-constant: styles should be in the style-sheet, and are then configurable.
       -->
  <xsl:template match="list">
        <td class="fieldprompt">
                  <span>
                                <xsl:choose>
                                        <xsl:when test="@status='invalid'">
                                                <xsl:attribute name="class">notvalid</xsl:attribute>
                                                                </xsl:when>
                                </xsl:choose>
                                <xsl:value-of select="title" />
                        </span>
                </td>
                <td class="listcanvas">
                        <xsl:call-template name="listitems" />
                </td>
  </xsl:template><!-- list -->

        <xsl:template match="value" mode="line">
             <xsl:param name="val" select="." />
             <span>
               <xsl:attribute name="style">width: 400;</xsl:attribute>
               <xsl:apply-templates select="../prefix" />
               <xsl:value-of select="$val" disable-output-escaping="yes" />
               <xsl:apply-templates select="../postfix" />
             </span>
   </xsl:template>

</xsl:stylesheet>
