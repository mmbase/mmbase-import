<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:node="org.mmbase.bridge.util.xml.NodeFunction" xmlns:date="org.mmbase.bridge.util.xml.DateFormat" extension-element-prefixes="node date">
  <!--
      Some Didactor-specific overrides
    @version $Id: wizard.xsl,v 1.9 2007-11-06 11:00:56 michiel Exp $

  -->
  <xsl:import href="ew:xsl/wizard.xsl"/>



  <xsl:template name="extrajavascript">
    <!--
         is the used directory ok? When it this used?
    -->
    <script type="text/javascript" src="../../education/wizards/mtmtrack.js"></script>
  </xsl:template>


  <xsl:template name="ftype-unknown">
    <xsl:choose>
      <xsl:when test="(@ftype='dateoffset')">
        <xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-other"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-datetime">
    <xsl:choose>
      <xsl:when test="@maywrite!='false'">
        <div>
          <!-- BEGIN DIDACTOR CHANGE -->
          <xsl:if test="@ftype='dateoffset'">
            <input type="hidden" name="{@fieldname}" value="date" id="{@fieldname}">
              <xsl:attribute name="new"><xsl:value-of select="value = ''"/></xsl:attribute>
              <xsl:apply-templates select="@*"/>
            </input>
          </xsl:if>
          <xsl:if test="@ftype!='dateoffset'">
            <input type="hidden" name="{@fieldname}" value="{@ftype}" id="{@fieldname}">
              <xsl:attribute name="new"><xsl:value-of select="value = ''"/></xsl:attribute>
              <xsl:apply-templates select="@*"/>
            </input>
          </xsl:if>
          <!-- END DIDACTOR CHANGE -->

          <xsl:if test="(@ftype='datetime') or (@ftype='date')">
            <xsl:call-template name="ftype-datetime-date"/>
          </xsl:if>

          <xsl:if test="@ftype='datetime'">
            <span class="time_at">
              <xsl:value-of select="$time_at"/>
            </span>
          </xsl:if>

          <xsl:if test="(@ftype='datetime') or (@ftype='time') or (@ftype='duration')">
            <xsl:call-template name="ftype-datetime-time"/>
          </xsl:if>

          <!-- BEGIN DIDACTOR CHANGE -->
          <xsl:if test="(@ftype='dateoffset')">
            <xsl:call-template name="ftype-datetime-dateoffset"/>
          </xsl:if>
          <!-- END DIDACTOR CHANGE -->

        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-data"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- BEGIN DIDACTOR CHANGE -->
  <xsl:template name="ftype-datetime-dateoffset">
    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_seconds" value="0" />
    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_minutes" value="0" />
    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_hours" value="0" />

    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_day" value="1" />
    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_month" value="0" />
    <input type="hidden" super="{@fieldname}" name="internal_{@fieldname}_year" value="1970" />

    <select name="internal_{@fieldname}_weeks" super="{@fieldname}">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="selected" select="date:format(string(value), 'w', $timezone, $language, $country) - 1" />
        <xsl:with-param name="end" select="52" />
      </xsl:call-template>
    </select> weken,
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    <select name="internal_{@fieldname}_days" super="{@fieldname}">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="selected" select="date:format(string(value), 'F', $timezone, $language, $country) - 1" />
        <xsl:with-param name="end" select="6" />
      </xsl:call-template>
    </select> dagen
  </xsl:template>
  <!-- END DIDACTOR CHANGE -->

  <xsl:template name="listsearch-fields-default">
    <!-- always search on owner and number too -->
    <xsl:choose>
       <xsl:when test="(@nodepath!='audiotapes') and
                       (@nodepath!='providers') and
                       (@nodepath!='images') and
                       (@nodepath!='attachments') and
                       (@nodepath!='videotapes') and
                       (@nodepath!='classes') and
                       (@nodepath!='tests') and
                       (@nodepath!='people') and
                       (@nodepath!='learnblocks') and
                       (@nodepath!='urls') and
                       (@nodepath!='competencetypes,competencies') and
                       (@nodepath!='questions') and
                       (@nodepath!='metadata,questions') and
                       (@nodepath!='mcanswers,feedback') and
                       (@nodepath!='feedback') and
                       (@nodepath!='learnobjects,learnblocks') and
                       (@nodepath!='news')">
         <option value="number" searchtype="equals">
            <xsl:call-template name="prompt_search_number"/>
         </option>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
       <xsl:when test="(@nodepath!='audiotapes') and
                       (@nodepath!='providers') and
                       (@nodepath!='images') and
                       (@nodepath!='attachments') and
                       (@nodepath!='videotapes') and
                       (@nodepath!='classes') and
                       (@nodepath!='tests') and
                       (@nodepath!='people') and
                       (@nodepath!='learnblocks') and
                       (@nodepath!='urls') and
                       (@nodepath!='competencetypes,competencies') and
                       (@nodepath!='questions') and
                       (@nodepath!='metadata,questions') and
                       (@nodepath!='mcanswers,feedback') and
                       (@nodepath!='feedback') and
                       (@nodepath!='learnobjects,learnblocks') and
                       (@nodepath!='news')">
          <option value="owner" searchtype="like">
              <xsl:call-template name="prompt_search_owner"/>
          </option>
       </xsl:when>
    </xsl:choose>
  </xsl:template>



</xsl:stylesheet>
