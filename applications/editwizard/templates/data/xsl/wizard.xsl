<xsl:stylesheet version="1.0"
  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
>
  <!--
  wizard.xsl

  @since  MMBase-1.6
  @author Kars Veling
  @author Michiel Meeuwissen
  @author Pierre van Rooden
  @version $Id: wizard.xsl,v 1.49 2002-07-18 15:21:13 michiel Exp $
  -->

  <xsl:import href="xsl/base.xsl" />

  <xsl:variable name="defaultsearchage">7</xsl:variable>
  <xsl:param name="objectnumber"></xsl:param>
  <xsl:param name="wizardtitle"><xsl:value-of select="list/object/@type" /></xsl:param>
  <xsl:param name="title"><xsl:value-of select="$wizardtitle" /></xsl:param>

  <!-- ================================================================================
       The following things can be overriden to customize the appearance of wizard
       ================================================================================ -->

  <!-- It can be usefull to add a style, change the title -->
  <xsl:template name="style"> 
     <title><xsl:value-of select="title" /></title>
     <link rel="stylesheet" type="text/css" href="../style/wizard.css" />
     <xsl:call-template name="extrastyle" /> <!-- see base.xsl -->
  </xsl:template>

  <!-- You can put stuff before and after then, or change the attributes of body itself. Don't forget to call 'bodycontent' 
       and to add the on(un)load attributes.
       It is probably handier to override beforeform of formcontent.       
       -->
  <xsl:template name="body"> 
    <body onload="doOnLoad_ew();" onunload="doOnUnLoad_ew();">
      <xsl:call-template name="bodycontent" />
    </body>
  </xsl:template>

  <!-- The first row of the the body's table -->
  <xsl:template name="superhead">
    <tr>
      <td colspan="2" align="center" valign="bottom">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
            <td class="head"><nobr><xsl:value-of select="title" /></nobr></td>
            <td class="superhead" align="right">
              <nobr><xsl:if test="$debug='true'"><a href="debug.jsp{$sessionid}" target="_blank">[debug]</a><br /></xsl:if><xsl:value-of select="form[@id=/wizard/curform]/title" /></nobr>
            </td>
          </tr>
        </table>
      </td>
    </tr>   
  </xsl:template>

  <!-- can be handy to add something to the top of your page -->
  <xsl:template name="beforeform" /> 

  <!-- The body itself, probably no need overriding this -->
  <xsl:template name="bodycontent">
    <xsl:call-template name="javascript" />
    <xsl:call-template name="beforeform" />
    <xsl:call-template name="bodyform" />
  </xsl:template>


  <!-- The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one,
       especially /*/steps-validator and form[..] 
       -->
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="superhead" />
        <tr>
          <th colspan="2" class="divider">
            <xsl:value-of select="form[@id=/wizard/curform]/subtitle" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </th>
        </tr>        
        <xsl:apply-templates select="form[@id=/wizard/curform]" /><!-- produces <tr />'s -->
        <xsl:apply-templates select="/*/steps-validator" />       <!-- produces <tr />'s -->
    </table>
  </xsl:template>

  <!-- Buttons are (always) called in the steps-validator 
       A <tr> is expected.
       -->
  <xsl:template name="buttons">
    <!-- our buttons -->
    <tr>
      <td colspan="2" align="center">
        <hr color="#005A4A" size="1" noshade="true" />
        <p>
          <!-- cancel -->
          <xsl:call-template name="cancelbutton" />
            -
          <!-- commit  -->
          <xsl:call-template name="savebutton" />
        </p>
        <hr color="#005A4A" size="1" noshade="true" />
      </td>
    </tr>    
  </xsl:template>


  <!-- The navigation buttons are only created (in steps-validator) if there is more than one step -->
  <xsl:template name="nav-buttons">
    <tr>
      <td colspan="2" align="center">
        <!-- previous -->
        <xsl:call-template name="previousbutton" />
          - -
        <!-- next -->
        <xsl:call-template name="nextbutton" />
      </td>
    </tr>    
  </xsl:template>

  <!-- The steps buttons are only created (in steps-validator) if there is more than one step -->
  <xsl:template name="steps">
    <tr>
      <td colspan="2" align="center">
        <hr color="#005A4A" size="1" noshade="true" />
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <!-- all steps -->
        <xsl:for-each select="step">
          <xsl:call-template name="stepbutton" />
          <br />
        </xsl:for-each>
      </td>
    </tr>
  </xsl:template>

  
  <xsl:template name="javascript"><!-- you probably don't want to override this. -->
    <script language="javascript" src="{$javascriptdir}tools.js"><xsl:comment>help IE</xsl:comment></script>
    <script language="javascript" src="{$javascriptdir}validator.js"><xsl:comment>help IE</xsl:comment></script>
    <script language="javascript" src="{$javascriptdir}editwizard.jsp{$sessionid}?referrer={$referrer}"><xsl:comment>help IE</xsl:comment></script>
    <script language="javascript" src="{$javascriptdir}wysiwyg.js"><xsl:comment>help IE</xsl:comment></script>
    <script language="javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[<!--
          window.history.forward(1);
          -->
        ]]> 
      </xsl:text>
    </script>
    <xsl:if test="//*[@ftype='html']"><!-- no need to add the wysiwyg button bar if there are not fields of this type -->
      <script language="javascript">
        <xsl:text disable-output-escaping="yes">
          <![CDATA[<!--
            if (browserutils.ie5560win) {
            window.attachEvent("onload",start_wysiwyg);
            }
            -->
          ]]> 
        </xsl:text>
      </script>      
    </xsl:if>  
  </xsl:template>


  <!-- ================================================================================
       The following is functionality. You probably don't want to override it.
       ================================================================================ -->

  <!-- Every wizard is based on one 'form', with a bunch of attributes and a few hidden entries.
       Those are set here -->
  <xsl:template name="bodyform">   
    <form name="form" method="post" action="{$wizardpage}" id="{/wizard/curform}"
      message_pattern="{$message_pattern}"
      message_required="{$message_required}"
      message_minlength="{$message_minlength}" message_maxlength="{$message_maxlength}"
      message_min="{$message_min}"  message_max="{$message_max}"
      message_mindate="{$message_mindate}" message_maxdate="{$message_maxdate}"
      message_dateformat="{$message_dateformat}"
      message_thisnotvalid="{$message_thisnotvalid}" message_notvalid="{$message_notvalid}"
      message_listtooshort="{$message_listtooshort}"
      invalidlist="{/wizard/form[@id=/wizard/curform]/@invalidlist}"
      >
      <input type="hidden" name="curform" value="{/wizard/curform}" />
      <input type="hidden" name="cmd" value="" id="hiddencmdfield" />
      <xsl:call-template name="formcontent" />
    </form>
  </xsl:template>


  <!-- On default.  All attributes must be copied -->
  <xsl:template match="@*">
    <xsl:copy><xsl:value-of select="." /></xsl:copy>
  </xsl:template>

  <xsl:template match="@name"></xsl:template>

  <!-- Wizard is the entry template-->
  <xsl:template match="/">
    <xsl:apply-templates select="wizard" />
  </xsl:template>

  <!-- we produce HTML, this could be overriden, but it does not make sense -->
  <xsl:template match="wizard">
    <html>
      <head>
        <xsl:call-template name="style" />
      </head>
      <xsl:call-template name="body" />
    </html>
  </xsl:template>

  <xsl:template match="form">
    <xsl:for-each select="fieldset|field|list">
      <tr>
        <xsl:apply-templates select="." />
      </tr>
    </xsl:for-each>
  </xsl:template>

  <!-- produces 2 or 3 td's -->
  <xsl:template match="field">
    <xsl:param name="colspan">1</xsl:param>
    <td class="fieldprompt">
      <xsl:call-template name="prompt" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td class="field" colspan="{$colspan}">
      <xsl:call-template name="fieldintern" />
    </td>
  </xsl:template>

  <!-- produces 2 or 3 td's -->
  <xsl:template match="fieldset">
    <xsl:param name="colspan">1</xsl:param>
    <td class="fieldprompt">
      <xsl:call-template name="prompt" />
    </td>
    <td class="field" colspan="{$colspan}">
      <xsl:for-each select="field">
        <xsl:call-template name="fieldintern" />
        <xsl:text disable-output-escaping="yes"> </xsl:text>
      </xsl:for-each>
    </td>
  </xsl:template>


  <!-- 
       Prefix and postfix are subtags of 'field', and are put respectively before and after the presentation of the field.
       Useful in fieldsets. 
       -->
  <xsl:template name="prefix|postfix">
    <xsl:value-of select="name()" /><xsl:value-of select="." />
  </xsl:template>

  <xsl:template name="prompt">
      <span id="prompt_{@fieldname}" class="valid" prompt="{prompt}">
        <xsl:choose>
          <xsl:when test="description">
            <xsl:attribute name="title"><xsl:value-of select="description" /></xsl:attribute>
            <xsl:attribute name="description"><xsl:value-of select="description" /></xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="title"><xsl:value-of select="prompt" /></xsl:attribute>
            <xsl:attribute name="description"><xsl:value-of select="prompt" /></xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="prompt" />
      </span>
   </xsl:template>

   <xsl:template match="value" mode="line">
     <xsl:param name="val" select="." />
     <span>
       <xsl:attribute name="style">width: 400;</xsl:attribute>
       <xsl:apply-templates select="../prefix" />
       <xsl:value-of select="$val" disable-output-escaping="yes" />
       <xsl:apply-templates select="../postfix" />
     </span>
   </xsl:template>

   <xsl:template match="value" mode="inputline">
     <xsl:param name="val" select="." />
       <xsl:apply-templates select="../prefix" />
       <input type="text" size="80" name="{../@fieldname}" value="{$val}" class="input" onkeyup="validate_validator(event);" onblur="validate_validator(event);">
         <xsl:apply-templates select="../@*" />
        </input>
        <xsl:apply-templates select="../postfix" />
   </xsl:template>



   <!-- ================================================================================
        fieldintern is called from fields and fieldsets
        
        -->
   <xsl:template name="fieldintern">
      <xsl:choose>
        <xsl:when test="@ftype='function'">
          <xsl:if test="not(string(number(@number)) = 'NaN')">
            <xsl:apply-templates select="value" mode="line">              
              <xsl:with-param name="val"><xsl:value-of select="node:function(string(@number), string(value))" disable-output-escaping="yes" /></xsl:with-param>
            </xsl:apply-templates>
          </xsl:if>
        </xsl:when>
        <xsl:when test="@ftype='data'">
          <xsl:apply-templates select="value" mode="line" />
        </xsl:when>
        <xsl:when test="@ftype='line'">
          <xsl:apply-templates select="value" mode="inputline" />
        </xsl:when>
        <xsl:when test="@ftype='text' or @ftype='html'">
          <span>
           <xsl:apply-templates select="prefix" />
          <xsl:text disable-output-escaping="yes">&lt;textarea
                name="</xsl:text><xsl:value-of select="@fieldname" /><xsl:text>"
                dttype="</xsl:text><xsl:value-of select="@dttype" /><xsl:text>"
                ftype="</xsl:text><xsl:value-of select="@ftype" /><xsl:text>"
                dtminlength="</xsl:text><xsl:value-of select="@dtminlength" /><xsl:text>"
                dtmaxlength="</xsl:text><xsl:value-of select="@dtmaxlength" /><xsl:text>"
                class="input" wrap="soft"
                onkeyup="validate_validator(event);"
                onblur="validate_validator(event);"</xsl:text>
          <xsl:choose>
            <xsl:when test="@cols">
              <xsl:text>cols="</xsl:text><xsl:value-of select="@cols" /><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>cols="80"</xsl:text>
            </xsl:otherwise></xsl:choose>
          <xsl:choose>
            <xsl:when test="@rows">
              <xsl:text>rows="</xsl:text><xsl:value-of select="@rows" /><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>rows="10"</xsl:text>
            </xsl:otherwise></xsl:choose>
          <xsl:apply-templates select="@*" />
          <xsl:text disable-output-escaping="yes">&gt;</xsl:text><xsl:value-of disable-output-escaping="yes" select="value" />
          <xsl:text disable-output-escaping="yes">&lt;/textarea&gt;</xsl:text>
            <xsl:apply-templates select="postfix" />
          </span>
        </xsl:when>
        <xsl:when test="@ftype='relation' or @ftype='enum'">
          <select name="{@fieldname}" class="input" onchange="validate_validator(event);" onblur="validate_validator(event);">
            <xsl:apply-templates select="@*" />
            <xsl:choose>
              <xsl:when test="optionlist/option[@selected='true']"></xsl:when>
              <xsl:otherwise>
                <option value="-"><xsl:call-template name="prompt_select" /></option>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:for-each select="optionlist/option">
              <option value="{@id}">
                <xsl:if test="@selected='true'">
                  <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="." />
              </option>
            </xsl:for-each>
          </select>
        </xsl:when>
        <xsl:when test="@ftype='enumdata'">
          <xsl:if test="optionlist/option[@id=current()/value]">
            <xsl:apply-templates select="value" mode="line">              
               <xsl:with-param name="val"><xsl:value-of select="optionlist/option[@id=current()/value]" /></xsl:with-param>
            </xsl:apply-templates>            
          </xsl:if>
        </xsl:when>
        <xsl:when test="(@ftype='datetime') or (@ftype='date') or (@ftype='time')">
          <div>
            <input type="hidden" name="{@fieldname}" value="{value}" id="{@fieldname}">
              <xsl:apply-templates select="@*" />
            </input>

            <xsl:if test="(@ftype='datetime') or (@ftype='date')">
              <select name="internal_{@fieldname}_day" onchange="validate_validator(event);" onblur="validate_validator(event);">
                <option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option><option value="29">29</option><option value="30">30</option><option value="31">31</option>
              </select><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              <select name="internal_{@fieldname}_month" onchange="validate_validator(event);" onblur="validate_validator(event);">
                <xsl:call-template name="optionlist_months" />
              </select><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              <input name="internal_{@fieldname}_year" type="text" value="" size="5" maxlength="4" onkeyup="validate_validator(event);" onblur="validate_validator(event);" />
            </xsl:if>

            <xsl:if test="@ftype='datetime'">
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>at<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:if>

            <xsl:if test="(@ftype='datetime') or (@ftype='time')">
              <input name="internal_{@fieldname}_hours" type="text" value="" size="2" maxlength="2" onkeyup="validate_validator(event);" onblur="validate_validator(event);" />
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>:<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              <input name="internal_{@fieldname}_minutes" type="text" value="" size="2" maxlength="2" onkeyup="validate_validator(event);" onblur="validate_validator(event);" />
            </xsl:if>
          </div>
        </xsl:when>
        <xsl:when test="@ftype='startwizard'">
          <nobr>
           <xsl:if test="@inline='true'">
                <a href="javascript:doStartWizard('{../../@fid}','{../../command[@name='add-item']/@value}','{@wizardname}','{@objectnumber}');">
                <xsl:call-template name="prompt_edit_wizard" />
                </a>
           </xsl:if>
           <xsl:if test="not(@inline='true')">
                <a href="{$popuppage}&amp;fid={../../@fid}&amp;did={../../command[@name='add-item']/@value}&amp;sessionkey={@wizardname}|popup_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={$objectnumber}"
                   target="_blank">
                <xsl:call-template name="prompt_edit_wizard" />
                </a>
           </xsl:if>
          </nobr>
        </xsl:when>
        <xsl:when test="@ftype='image'">
          <xsl:choose>
            <xsl:when test="@dttype='binary' and not(upload)">
              <div class="imageupload">
                <div><input type="hidden" name="{@fieldname}" value="YES" />
                  <img src="{node:function(string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" /><br />
                  <a href="{$uploadpage}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                  <xsl:call-template name="prompt_image_upload" />
                  </a>
                </div>
              </div>
            </xsl:when>
            <xsl:when test="@dttype='binary' and upload">
              <div class="imageupload"><input type="hidden" name="{@fieldname}" value="YES" />
                <img src="{upload/path}" hspace="0" vspace="0" border="0" width="128" height="128" />
                <br />
                <span>
                  <xsl:value-of select="upload/@name" /><xsl:text disable-output-escaping="yes" >&amp;nbsp;</xsl:text> (<xsl:value-of select="round((upload/@size) div 100) div 10" />K)
                </span>
                <br />
                <a href="{$uploadpage}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);"><xsl:call-template name="prompt_image_upload" /></a>
              </div>
            </xsl:when>
            <xsl:otherwise>
          <span>
            <img src="{node:function(string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}" /><br />
          </span>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="@ftype='file'">
          <xsl:choose>
            <xsl:when test="@dttype='data'">
            <a href="{node:function(string(@number), concat('servletpath(', $cloudkey, ',', string(@number), ')'))}"><xsl:call-template name="prompt_do_download" /></a>
            </xsl:when>
            <xsl:otherwise>
              <nobr><input type="hidden" name="{@fieldname}" value="YES" />
                <xsl:choose>
                  <xsl:when test="not(upload)"><xsl:call-template name="prompt_no_file" /><br />
                    <a href="{node:function(string(@number), concat('servletpath(', $cloudkey, ',', string(@number), ')'))}"><xsl:call-template name="prompt_do_download" /></a><br />
                  </xsl:when>
                  <xsl:otherwise><xsl:call-template name="prompt_uploaded" /><xsl:value-of select="upload/@name" /><xsl:text disable-output-escaping="yes" >&amp;nbsp;</xsl:text>(<xsl:value-of select="round((upload/@size) div 100) div 10" />K)<br />
                  </xsl:otherwise>
                </xsl:choose>
                <a href="{$uploadpage}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);"><xsl:call-template name="prompt_do_upload" /></a>
              </nobr>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>

        <xsl:when test="@ftype='realposition'">
       <span style="width:128" >
          <input type="text" name="{@fieldname}" value="{value}" class="input" onkeyaup="validate_validator(event);" onblur="validate_validator(event);">
              <xsl:apply-templates select="@*" />
            </input><input type="button" value="get" onClick="document.forms['form'].{@fieldname}.value = document.embeddedplayer.GetPosition();" />
          </span>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="value" mode="inputline" />
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>


  <!-- produces the <tr> (two columns) for a bunch of fields (used in item) -->
  <xsl:template name="itemfields">
    <xsl:for-each select="field|fieldset">
      <tr>
        <xsl:apply-templates select="." />
       </tr>
    </xsl:for-each>     
  </xsl:template>

  <!-- 
       ================================================================================
       item, produces 3 column-tr's ( prompt (or image) -   item itself  -  (buttons)
       -->
  <xsl:template match="item">
      <!-- here we figure out how to draw this repeated item. It depends on the displaytype -->
      <xsl:choose>
        <xsl:when test="@displaytype='link'"><!-- simply make the link, there must be a field name and number -->
          <tr><td colspan="3">
            <a href="{$wizardpage}&amp;wizard={@wizardname}&amp;objectnumber={field[@name='number']/value}">- <xsl:value-of select="field[@name='title']/value" /></a>
          </td></tr>
        </xsl:when>
        <xsl:when test="@displaytype='image'"> <!-- first column is the image, show the fields in the second column -->
          <tr>
            <td colspan="3">
              <xsl:call-template name="itembuttons" />
            </td>
          </tr>
          <tr>
            <td>          
              <!-- the image -->
              <img src="{node:function(string(@destination), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}" />
            </td>
            <td colspan="2">
              <xsl:if test="field|fieldset">
                <table>
                  <xsl:call-template name="itemfields" />
                </table>                
              </xsl:if>
            </td>
          </tr>
        </xsl:when>
        <xsl:when test="count(field|fieldset) &lt; 2"><!-- only one field ?, itembutton right from the item. -->
          <tr>           
            <xsl:for-each select="field|fieldset">
              <xsl:apply-templates select="." /> <!-- two td's -->
            </xsl:for-each>
            <xsl:if test="not(field|fieldset)">
              <td colspan="2">[]</td>
            </xsl:if>          
            <td align="right" valign="top">
              <nobr>
                <xsl:call-template name="itembuttons" />
              </nobr>
            </td>
          </tr>
        </xsl:when>
        <xsl:otherwise><!-- more fields -->
          <tr>            
            <td colspan="3">
              <xsl:call-template name="itembuttons" />
             </td>
          </tr>
          <!-- draw all fields, if there are any for this item -->
          <xsl:for-each select="field|fieldset">
            <tbody class="fieldset">
            <tr>
              <xsl:choose>
                <xsl:when test="position()=1">
                  <xsl:attribute name="class">first</xsl:attribute>
                </xsl:when>
                <xsl:when test="position()=last()">
                  <xsl:attribute name="class">last</xsl:attribute>
                </xsl:when>
              </xsl:choose>
              <xsl:apply-templates select=".">
                <xsl:with-param name="colspan">2</xsl:with-param>
              </xsl:apply-templates>
            </tr>
          </tbody>
          </xsl:for-each>          
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template><!-- item -->

  <!-- produces a bunch of links -->
  <xsl:template name="itembuttons">
    <xsl:if test="@displaytype='audio'">
        <a href="{$ew_context}/rastreams.db?{@destination}" title="{$tooltip_audio}"><xsl:call-template name="prompt_audio" /></a>
    </xsl:if>
    <xsl:if test="@displaytype='video'">
        <a href="{$ew_context}/rmstreams.db?{@destination}" title="{$tooltip_video}"><xsl:call-template name="prompt_video" /></a>
    </xsl:if>
    <xsl:if test="command[@name='delete-item']">
        <span class="imagebutton" title="{$tooltip_remove}" onclick="doSendCommand('{command[@name='delete-item']/@cmd}');">
          <xsl:call-template name="prompt_remove" />
        </span>
    </xsl:if>

    <xsl:if test="command[@name='move-up']">
          <span class="imagebutton" title="{$tooltip_up}" onclick="doSendCommand('{command[@name='move-up']/@cmd}');"><xsl:call-template name="prompt_up" /></span>
    </xsl:if>
    <xsl:if test="command[@name='move-down']">
          <span class="imagebutton" title="{$tooltip_down}" onclick="doSendCommand('{command[@name='move-down']/@cmd}');"><xsl:call-template name="prompt_down" /></span>
    </xsl:if>
  </xsl:template>


  <!-- The age search options of a search item -->
  <xsl:template name="searchoptions">  
    <option value="0"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'0'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_now" /></option>
    <option value="1"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'1'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_day" /></option>
    <option value="7"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'7'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_week" /></option>
    <option value="31"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'31'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_month" /></option>
    <option value="365"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'365'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_year" /></option>
    <option value="-1"><xsl:call-template name="searchage"><xsl:with-param name="real" select="'-1'" /><xsl:with-param name="pref" select="@age" /></xsl:call-template><xsl:call-template name="age_any" /></option>
  </xsl:template>

  <!--
       What to do with 'lists'.
       @bad-constant: styles should be in the style-sheet, and are then configurable.       
       -->
  <xsl:template match="list">
    <td colspan="2" class="listcanvas">
      
      <div class="subhead" title="{description}">
        <nobr><xsl:value-of select="title" /></nobr>
      </div>

      <!-- show the item's of the list like that -->
      <xsl:if test="item">
        <table class="itemlist"><!-- three columns -->       
          <xsl:apply-templates select="item" />          
        </table>
      </xsl:if>


      <!-- if 'add-item' command and a search, then make a search util-box -->
      <xsl:if test="command[@name='add-item']">
        <xsl:for-each select="command[@name='search']">
          <table class="itemadd">
            <tr>
              <td>
                  <xsl:value-of select="prompt" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                  <nobr><!-- the search-tools must not be seperated -->
                  <!-- alway make searching on age possible -->
                  <select name="searchage_{../command[@name='add-item']/@cmd}" class="age">
                    <xsl:call-template name="searchoptions" />
                  </select>
                  <!-- other search-possibilities are given in the xml -->
                  <select 
                    name="searchfields_{../command[@name='add-item']/@cmd}"   class="searchpossibilities"
                    onChange="form['searchterm_{../command[@name='add-item']/@cmd}'].value = this[this.selectedIndex].getAttribute('default'); ">
                    >
                    <xsl:choose>
                      <xsl:when test="search-filter">
                        <xsl:for-each select="search-filter">
                          <option value="{search-fields}" default="{default}"><xsl:value-of select="name" /></option>
                        </xsl:for-each>
                      </xsl:when>
                      <!-- if nothing, then search on title -->
                      <xsl:otherwise>
                        <option value="title"><xsl:call-template name="prompt_search_title" /></option>
                      </xsl:otherwise>
                    </xsl:choose>
                    <!-- always search on owner too -->
                    <option value="owner"><xsl:call-template name="prompt_search_owner" /></option>
                  </select>
                  <input type="text" name="searchterm_{../command[@name='add-item']/@cmd}" value="{search-filter[1]/default}" class="search" />
                  <span title="{$tooltip_search}" class="imagebutton" 
                    onClick="doSearch(this,'{../command[@name='add-item']/@cmd}','{$sessionkey}');">
                    <xsl:for-each select="@*"><xsl:copy /></xsl:for-each>
                    <xsl:call-template name="prompt_search" />
                 </span>
                </nobr>
              </td>
            </tr>
          </table>
        </xsl:for-each>
      </xsl:if><!-- if add-item -->


      <!-- 
           Create the '*' (add) buttons.
           -->
      <xsl:for-each select="command[@name='add-item']">
        <xsl:if test="not(../command[@name='startwizard'] and not(../command[@name='search' or @name='insert']))">
          <!-- if there is a start-wizard command and not 'search' or 'insert' command then do not add an extra 'add' button (will be added by 'startwizard' -->
          <table class="itemadd">
          <xsl:for-each select="../command[@name='search' or @name='insert']">
            <xsl:choose>
              <xsl:when test="@name='search'">
                <xsl:attribute name="style">display:inline; visibility:hidden; position:absolute; top:0; left:0;</xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="style">display:inline;</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <tr>
            <td>
              <nobr>
                <span class="imagebutton" onclick="doSendCommand('{@cmd}');"><xsl:call-template name="prompt_new" /></span>                
               </nobr>
              </td>
            </tr>
          </table>
        </xsl:if>
      </xsl:for-each>

      <!-- 
           Create the add-buttons for the startwizard commands.
           -->
      <xsl:for-each select="command[@name='startwizard']">
        <!-- only if less then maxoccurs -->
        <xsl:if test="not(ancestor::list/@maxoccurs) or (ancestor::list/@maxoccurs = '*') or count(ancestor::list/item) &lt; ancestor::list/@maxoccurs">
        <table class="itemadd">
          <tr>
            <td>
              <nobr>
                <xsl:if test="@inline='true'">
                  <a href="javascript:doStartWizard('{../@fid}','{../command[@name='add-item']/@value}','{@wizardname}','{@objectnumber}');">
                    <xsl:call-template name="prompt_add_wizard" />
                  </a>
                </xsl:if>
                <xsl:if test="not(@inline='true')">
                  <a href="{$popuppage}&amp;fid={../@fid}&amp;did={../command[@name='add-item']/@value}&amp;sessionkey={@wizardname}|popup_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={$objectnumber}"
                    target="_blank">
                    <xsl:call-template name="prompt_add_wizard" />
                  </a>
                </xsl:if>                
                </nobr>
              </td>
            </tr>
          </table>
        </xsl:if>
        </xsl:for-each>
      </td>

    </xsl:template><!-- list -->
    
    <xsl:template match="steps-validator">
      <!-- when multiple steps, otherwise do nothing -->
      <xsl:if test="count(step) &gt; 1">
        <xsl:call-template name="steps" />
        <xsl:call-template name="nav-buttons" />
      </xsl:if>
      <xsl:call-template name="buttons" />
    </xsl:template>
          
          
    <xsl:template name="savebutton">
    <a href="javascript:doSave();" id="bottombutton-save" unselectable="on"
      titlesave="{$tooltip_save}" titlenosave="{$tooltip_no_save}" >
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_save" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save" /></xsl:attribute>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="step[@valid='false'][not(@form-schema=/wizard/curform)]">
          <xsl:attribute name="otherforms">invalid</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="otherforms">valid</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="prompt_save" />
      </a>
  </xsl:template>

  <xsl:template name="cancelbutton">
    <a href="javascript:doCancel();"><span id="bottombutton-cancel" class="bottombutton" title="{$tooltip_cancel}">
    <xsl:call-template name="prompt_cancel" />
    </span></a>
  </xsl:template>

  <xsl:template name="previousbutton">
          <xsl:choose>
            <xsl:when test="/wizard/form[@id=/wizard/prevform]">
              <a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/prevform}')" title="{$tooltip_previous} '{/wizard/form[@id=/wizard/prevform]/title}'"><xsl:call-template name="prompt_previous" /></a>
            </xsl:when>
            <xsl:otherwise>
              <span class="step-disabled" align="left" width="100%" title="{$tooltip_no_previous}"><xsl:call-template name="prompt_previous" /></span>
            </xsl:otherwise>
          </xsl:choose>
  </xsl:template>

  <xsl:template name="nextbutton">
          <xsl:choose>
            <xsl:when test="/wizard/form[@id=/wizard/nextform]">
              <a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/nextform}')" title="{$tooltip_next} '{/wizard/form[@id=/wizard/nextform]/title}'"><xsl:call-template name="prompt_next" /></a>
            </xsl:when>
            <xsl:otherwise>
              <span class="step-disabled" align="left" width="100%" title="{$tooltip_no_next}"><xsl:call-template name="prompt_next" /></span>
            </xsl:otherwise>
          </xsl:choose>
  </xsl:template>


  <xsl:template name="stepbutton">
    a:<xsl:variable name="schemaid" select="@form-schema" />      
      <a href="javascript:doGotoForm('{@form-schema}');" id="bottombutton-step-{$schemaid}" class="stepicon"
        titlevalid="{$tooltip_valid}" titlenotvalid="{$tooltip_not_valid}"> 
      <xsl:attribute name="class"><xsl:if test="$schemaid=/wizard/curform">current</xsl:if>stepicon<xsl:if test="@valid='true'">-valid</xsl:if></xsl:attribute>
      <xsl:attribute name="title"><xsl:value-of select="/*/form[@id=$schemaid]/title" /><xsl:if test="@valid='false'"><xsl:value-of select="$tooltip_step_not_valid" /></xsl:if></xsl:attribute>
      <xsl:call-template name="prompt_step" />
      </a>
      <span class="step-info" ><xsl:value-of select="/*/form[@id=$schemaid]/title" /></span>
  </xsl:template>

  <xsl:template name="searchage">
    <xsl:param name="real" />
    <xsl:param name="pref" />
    <xsl:if test="($real=$pref) or (not($pref) and $defaultsearchage=$real)"><xsl:attribute name="selected">true</xsl:attribute></xsl:if>
  </xsl:template>

</xsl:stylesheet>
