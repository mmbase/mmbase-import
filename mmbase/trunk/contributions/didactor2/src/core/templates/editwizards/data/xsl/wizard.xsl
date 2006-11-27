<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:node="org.mmbase.bridge.util.xml.NodeFunction" xmlns:date="org.mmbase.bridge.util.xml.DateFormat" extension-element-prefixes="node date">
  <!--
      Some Didactor-specific overrides
    @version $Id: wizard.xsl,v 1.7 2006-11-27 12:15:48 mmeeuwissen Exp $

  -->
  <xsl:import href="ew:xsl/wizard.xsl"/>


  
  <xsl:template name="extrajavascript">
    <!-- 
         is the used directory ok? When it this used?
    -->
    <script type="text/javascript" src="../../education/wizards/mtmtrack.js"></script>
  </xsl:template>

  <!--
    fieldintern is called to draw the values
  -->
  <xsl:template name="fieldintern">
    <xsl:apply-templates select="prefix"/>

    <xsl:choose>
      <xsl:when test="@ftype=&apos;startwizard&apos;">
        <xsl:call-template name="ftype-startwizard"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;function&apos;">
        <xsl:call-template name="ftype-function"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;data&apos;">
        <xsl:call-template name="ftype-data"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;line&apos;">
        <xsl:call-template name="ftype-line"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;text&apos;">
        <xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;html&apos;">
        <xsl:call-template name="ftype-html"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;relation&apos;">
        <xsl:call-template name="ftype-relation"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;enum&apos;">
        <xsl:call-template name="ftype-enum"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;enumdata&apos;">
        <xsl:call-template name="ftype-enumdata"/>
      </xsl:when>
      <xsl:when test="(@ftype=&apos;datetime&apos;) or (@ftype=&apos;date&apos;) or (@ftype=&apos;time&apos;) or (@ftype=&apos;duration&apos;)">
        <xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <!-- BEGIN DIDACTOR CHANGE -->
      <xsl:when test="(@ftype=&apos;dateoffset&apos;)">
        <xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <!-- END DIDACTOR CHANGE -->
      <xsl:when test="@ftype=&apos;image&apos;">
        <xsl:call-template name="ftype-image"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;file&apos;">
        <xsl:call-template name="ftype-file"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;realposition&apos;">
        <xsl:call-template name="ftype-realposition"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-unknown"/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates select="postfix"/>
  </xsl:template>


  <xsl:template name="ftype-datetime">
    <xsl:choose>
      <xsl:when test="@maywrite!=&apos;false&apos;">
        <div>
          <!-- BEGIN DIDACTOR CHANGE -->
          <xsl:if test="@ftype=&apos;dateoffset&apos;">
            <input type="hidden" name="{@fieldname}" value="date" id="{@fieldname}">
              <xsl:attribute name="new"><xsl:value-of select="value = ''"/></xsl:attribute>
              <xsl:apply-templates select="@*"/>
            </input>
          </xsl:if>
          <xsl:if test="@ftype!=&apos;dateoffset&apos;">
            <input type="hidden" name="{@fieldname}" value="{@ftype}" id="{@fieldname}">
              <xsl:attribute name="new"><xsl:value-of select="value = ''"/></xsl:attribute>
              <xsl:apply-templates select="@*"/>
            </input>
          </xsl:if>
          <!-- END DIDACTOR CHANGE -->

          <xsl:if test="(@ftype=&apos;datetime&apos;) or (@ftype=&apos;date&apos;)">
            <xsl:call-template name="ftype-datetime-date"/>
          </xsl:if>

          <xsl:if test="@ftype=&apos;datetime&apos;">
            <span class="time_at">
              <xsl:value-of select="$time_at"/>
            </span>
          </xsl:if>

          <xsl:if test="(@ftype=&apos;datetime&apos;) or (@ftype=&apos;time&apos;) or (@ftype=&apos;duration&apos;)">
            <xsl:call-template name="ftype-datetime-time"/>
          </xsl:if>

          <!-- BEGIN DIDACTOR CHANGE -->
          <xsl:if test="(@ftype=&apos;dateoffset&apos;)">
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
       <xsl:when test="(@nodepath!=&apos;audiotapes&apos;)=
                       (@nodepath!=&apos;providers&apos;)=
                       (@nodepath!=&apos;images&apos;)=
                       (@nodepath!=&apos;attachments&apos;)=
                       (@nodepath!=&apos;videotapes&apos;)=
                       (@nodepath!=&apos;classes&apos;)=
                       (@nodepath!=&apos;tests&apos;)=
                       (@nodepath!=&apos;people&apos;)=
                       (@nodepath!=&apos;learnblocks&apos;)=
                       (@nodepath!=&apos;urls&apos;)=                      
                       (@nodepath!=&apos;competencetypes,competencies&apos;)=
                       (@nodepath!=&apos;questions&apos;)=
                       (@nodepath!=&apos;metadata,questions&apos;)= 
                       (@nodepath!=&apos;mcanswers,feedback&apos;)=
                       (@nodepath!=&apos;feedback&apos;)=
                       (@nodepath!=&apos;learnobjects,learnblocks&apos;)=
                       (@nodepath!=&apos;news&apos;)">      
         <option value="number" searchtype="equals">
            <xsl:call-template name="prompt_search_number"/>
         </option>
      </xsl:when>
    </xsl:choose>   
    <xsl:choose>
       <xsl:when test="(@nodepath!=&apos;audiotapes&apos;)=
                       (@nodepath!=&apos;providers&apos;)=
                       (@nodepath!=&apos;images&apos;)=
                       (@nodepath!=&apos;attachments&apos;)=
                       (@nodepath!=&apos;videotapes&apos;)=
                       (@nodepath!=&apos;classes&apos;)=
                       (@nodepath!=&apos;tests&apos;)=
                       (@nodepath!=&apos;people&apos;)=
                       (@nodepath!=&apos;learnblocks&apos;)=
                       (@nodepath!=&apos;urls&apos;)=
                       (@nodepath!=&apos;competencetypes,competencies&apos;)=
                       (@nodepath!=&apos;questions&apos;)=
                       (@nodepath!=&apos;metadata,questions&apos;)=
                       (@nodepath!=&apos;mcanswers,feedback&apos;)=   
                       (@nodepath!=&apos;feedback&apos;)=   
                       (@nodepath!=&apos;learnobjects,learnblocks&apos;)=       
                       (@nodepath!=&apos;news&apos;)"> 
          <option value="owner" searchtype="like">
              <xsl:call-template name="prompt_search_owner"/>
          </option>
       </xsl:when>
    </xsl:choose> 
  </xsl:template>



</xsl:stylesheet>
