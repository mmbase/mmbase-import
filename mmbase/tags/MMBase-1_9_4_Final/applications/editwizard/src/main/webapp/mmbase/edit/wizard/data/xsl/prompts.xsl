<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    prompts.xls

    @since  MMBase-1.6
    @author Pierre van Rooden
    @author Nico Klasens
    @version $Id: prompts.xsl,v 1.38 2008-02-14 17:19:40 nklasens Exp $

    prompts used in this editwizard.
    Override these prompts to change the view in your own versions.
  -->

  <!-- prompts for starting a editwizard -->
  <xsl:variable name="tooltip_edit_wizard">Change...</xsl:variable>
  <xsl:variable name="please_save">Please save first</xsl:variable>
  <xsl:template name="prompt_edit_wizard">
    <xsl:choose>
      <xsl:when test="prompt">
        <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="prompt"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$tooltip_edit_wizard" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:variable name="tooltip_add_wizard">New</xsl:variable>
  <xsl:template name="prompt_add_wizard">
    <xsl:if test="prompt">
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="prompt"/>
      </xsl:call-template>
    </xsl:if>
    <img src="{$mediadir}new.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt">
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="prompt"/>
            </xsl:call-template>
          </xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_add_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>

  <xsl:variable name="date_january">january</xsl:variable>
  <xsl:variable name="date_february">february</xsl:variable>
  <xsl:variable name="date_march">march</xsl:variable>
  <xsl:variable name="date_april">april</xsl:variable>
  <xsl:variable name="date_may">may</xsl:variable>
  <xsl:variable name="date_june">june</xsl:variable>
  <xsl:variable name="date_july">july</xsl:variable>
  <xsl:variable name="date_august">august</xsl:variable>
  <xsl:variable name="date_september">september</xsl:variable>
  <xsl:variable name="date_october">october</xsl:variable>
  <xsl:variable name="date_november">november</xsl:variable>
  <xsl:variable name="date_december">december</xsl:variable>

  <xsl:variable name="day_sun">Sun</xsl:variable>
  <xsl:variable name="day_mon">Mon</xsl:variable>
  <xsl:variable name="day_tue">Tue</xsl:variable>
  <xsl:variable name="day_wed">Wed</xsl:variable>
  <xsl:variable name="day_thu">Thu</xsl:variable>
  <xsl:variable name="day_fri">Fri</xsl:variable>
  <xsl:variable name="day_sat">Sat</xsl:variable>

  <xsl:variable name="datepicker_currentmonth">Go to current month</xsl:variable>
  <xsl:variable name="datepicker_today">Today is</xsl:variable>
  <xsl:variable name="datepicker_scrollleft">Click here to scroll to the previous month. Press the mousebutton to scroll automatically.</xsl:variable>
  <xsl:variable name="datepicker_scrollright">Click here to scroll to the next month. Press the mousebutton to scroll automatically.</xsl:variable>
  <xsl:variable name="datepicker_selectmonth">Click here to select a month.</xsl:variable>
  <xsl:variable name="datepicker_selectyear">Click here to select a year.</xsl:variable>
  <xsl:variable name="datepicker_selectdate">Select [datum] as the date.</xsl:variable>

  <xsl:variable name="time_daymonth" />
  <!-- Between day and month. Sadly, order cannot yet be adjusted -->
  <xsl:variable name="time_at">at</xsl:variable>
  <!-- Before the time -->

  <!-- prompts for a binary field (upload/download) -->
  <xsl:template name="prompt_file_upload">File Upload</xsl:template>
  <xsl:template name="prompt_uploaded">Uploaded:</xsl:template>
  <xsl:template name="prompt_image_upload">Upload new image</xsl:template>
  <xsl:template name="prompt_image_replace">Replace new image</xsl:template>
  <xsl:template name="prompt_do_download">Download current</xsl:template>
  <xsl:template name="prompt_do_upload">Upload new</xsl:template>
  <xsl:template name="prompt_no_file">No (new) file.</xsl:template>
  <xsl:template name="prompt_image_full">Full image</xsl:template>

  <!-- prompts for a dropdown box -->
  <xsl:template name="prompt_select">select...</xsl:template>

  <!-- up/down button prompts and tooltips -->
  <xsl:variable name="tooltip_up">Move this item up in the list</xsl:variable>
  <xsl:variable name="tooltip_down">Move this item down in the list</xsl:variable>
  <xsl:template name="prompt_up">
    <img src="{$mediadir}up.gif" alt="{$tooltip_up}" />
  </xsl:template>
  <xsl:template name="prompt_down">
    <img src="{$mediadir}down.gif" alt="{$tooltip_down}" />
  </xsl:template>

  <!-- new button prompts and tooltips -->
  <xsl:variable name="tooltip_new">Add a new item to the list</xsl:variable>
  <xsl:template name="prompt_new">
    <img src="{$mediadir}new.gif"
         alt="{$tooltip_new}"
         title="{$tooltip_new}"
         class="imgbutton"/>
  </xsl:template>

  <!-- remove button prompts and tooltips (for relations) -->
  <xsl:variable name="tooltip_remove">Remove this item from the list. Removed items are still present in MMBase.</xsl:variable>
  <xsl:template name="prompt_remove">
    <img src="{$mediadir}remove.gif" alt="{$tooltip_remove}" class="imgbutton"/>
  </xsl:template>

  <!-- delete button prompts and tooltips (for objects) -->
  <xsl:variable name="tooltip_delete">Delete this item</xsl:variable>
  <xsl:template name="prompt_delete">
    <img src="{$mediadir}remove.gif" alt="{$tooltip_delete}" />
  </xsl:template>
  <xsl:template name="prompt_unlink">
    <img src="{$mediadir}unlink.gif" alt="{$tooltip_remove}" />
  </xsl:template>
  <xsl:template name="prompt_delete_confirmation">Are you sure you want to delete this item?</xsl:template>
  <xsl:template name="prompt_unlink_confirmation">Are you sure you want to unlink this item?</xsl:template>

  <!-- help button prompts and tooltips -->
  <xsl:variable name="tooltip_help">Help</xsl:variable>
  <xsl:template name="prompt_help">
    <img src="{$mediadir}help.gif" alt="{$tooltip_help}" class="imgbutton"/>
  </xsl:template>

  <!-- save button prompts and tooltips -->
  <xsl:template name="prompt_save">save &amp; close</xsl:template>
  <xsl:template name="prompt_save_only">save</xsl:template>
  <xsl:variable name="tooltip_save">Store all changes.</xsl:variable>
  <xsl:variable name="tooltip_save_only">Store all changes (but continue editing).</xsl:variable>
  <xsl:variable name="tooltip_no_save">The changes cannot be saved, since some data is not filled in correctly.</xsl:variable>

  <!-- cancel button prompts and tooltips -->
  <xsl:template name="prompt_cancel">cancel</xsl:template>
  <xsl:variable name="tooltip_cancel">Cancel this task, changes will NOT be saved.</xsl:variable>

  <!-- step (form) button prompts and tooltips -->
  <xsl:template name="prompt_step">step <xsl:value-of select="position()" /></xsl:template>
  <xsl:variable name="tooltip_step_not_valid">is NOT valid. Click here to correct the errors.</xsl:variable>
  <xsl:variable name="tooltip_valid">The current form is valid.</xsl:variable>
  <xsl:variable name="tooltip_not_valid">The current form is NOT valid. Correct red-marked fields and try again.</xsl:variable>

  <!-- step forward and backward prompts and tooltips -->
  <xsl:template name="prompt_previous">&lt;&lt;</xsl:template>
  <xsl:variable name="tooltip_previous">Back to</xsl:variable>
  <xsl:variable name="tooltip_no_previous">No previous form</xsl:variable>
  <xsl:template name="prompt_next">>></xsl:template>
  <xsl:variable name="tooltip_next">Forward to</xsl:variable>
  <xsl:variable name="tooltip_no_next">No next form</xsl:variable>
  <xsl:variable name="tooltip_goto">Go to</xsl:variable>

  <!-- audio / video buttons prompts -->
  <xsl:variable name="tooltip_audio">Click here to hear the audio clip</xsl:variable>
  <xsl:template name="prompt_audio">
    <img src="{$mediadir}audio.gif" alt="($tooltip_audio)" class="imgbutton"/>
  </xsl:template>
  <xsl:variable name="tooltip_video">Click here to view the video clip</xsl:variable>
  <xsl:template name="prompt_video">
    <img src="{$mediadir}video.gif" alt="($tooltip_video)" class="imgbutton"/>
  </xsl:template>

  <!-- search : prompts for age filter -->
  <xsl:template name="prompt_age">Age</xsl:template>
  <xsl:template name="age_now">today</xsl:template>
  <xsl:template name="age_day">1 day</xsl:template>
  <xsl:template name="age_week">7 days</xsl:template>
  <xsl:template name="age_month">1 month</xsl:template>
  <xsl:template name="age_year">1 year</xsl:template>
  <xsl:template name="age_any">any age</xsl:template>

  <!-- search : other filters -->
  <xsl:template name="prompt_search_list">Search</xsl:template>
  <xsl:template name="prompt_search_term">Terms</xsl:template>
  <xsl:template name="prompt_search_page" >
    <img src="{$mediadir}search_page.gif" border="0" alt="Search in cloud" />
  </xsl:template>

  <xsl:template name="prompt_search">
    <img src="{$mediadir}search.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt!=''">
          <xsl:attribute name="alt">
            <xsl:value-of select="prompt" />
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="prompt" />
          </xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt">
            <xsl:value-of select="$tooltip_search" />
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="$tooltip_search" />
          </xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>
  <xsl:template name="prompt_search_all">
    <img src="{$mediadir}search_all.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt!=''">
          <xsl:attribute name="alt">
            <xsl:value-of select="prompt" />
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="prompt" />
          </xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt">
            <xsl:value-of select="$tooltip_search_all" />
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="$tooltip_search_all" />
          </xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>
  <xsl:variable name="tooltip_search">Search</xsl:variable>
  <xsl:variable name="tooltip_search_all">Search and add an item</xsl:variable>
  <xsl:template name="prompt_search_title">Contains</xsl:template>
  <xsl:template name="prompt_search_number">Number is</xsl:template>
  <xsl:template name="prompt_search_owner">Owner is</xsl:template>
  <xsl:variable name="filter_required">Entering a searchterm is required.</xsl:variable>

  <!-- navigation -->
  <xsl:template name="prompt_index">index</xsl:template>
  <xsl:variable name="tooltip_index">Return to the index page</xsl:variable>
  <xsl:template name="prompt_logout">logout</xsl:template>
  <xsl:variable name="tooltip_logout">Logout and return to the index page</xsl:variable>

  <xsl:template name="prompt_search_age">
    <xsl:param name="age" />
    <xsl:if test="$age=1"> from the previous day</xsl:if>
    <xsl:if test="$age=7"> from the previous 7 days</xsl:if>
    <xsl:if test="$age=31"> from the previous month</xsl:if>
    <xsl:if test="$age=356"> from the previous year</xsl:if>
    <xsl:if test="$age=-1"> all</xsl:if>
  </xsl:template>

  <!-- prompts and tooltips for lists -->
  <xsl:template name="prompt_edit_list">
      <xsl:param name="age" />
      <xsl:param name="searchvalue" />
      <xsl:call-template name="prompt_search_age" >
        <xsl:with-param name="age" select="$age" />
      </xsl:call-template>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      <xsl:value-of select="$title" disable-output-escaping="yes"  />
      <xsl:if test="$searchvalue" >
        - search for <xsl:value-of select="$searchvalue" />
      </xsl:if>
      (items <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pages <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
  </xsl:template>
  <xsl:variable name="tooltip_edit_list">These are the items that you can edit.</xsl:variable>
  <xsl:variable name="tooltip_sort_on">Sort on</xsl:variable>
  <xsl:variable name="tooltip_sort_up">up</xsl:variable>
  <xsl:variable name="tooltip_sort_down">down</xsl:variable>
  <xsl:template name="prompt_sort_up">
    <img src="{$mediadir}sortup.png" alt="{$tooltip_sort_up}" height="15" width="15" />
  </xsl:template>
  <xsl:template name="prompt_sort_down">
    <img src="{$mediadir}sortdown.png" alt="{$tooltip_sort_down}" height="15" width="15" />
  </xsl:template>

  <!-- searchlist prompts/tooltips -->
  <xsl:variable name="searchpage_title">Search Results</xsl:variable>
  <xsl:variable name="tooltip_select_search">please select one or more items from this list</xsl:variable>
  <xsl:template name="prompt_no_results">No entries found, try again...</xsl:template>
  <xsl:template name="prompt_more_results">(More items found...)</xsl:template>
  <xsl:template name="prompt_result_count" >(items <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pages <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)</xsl:template>
  <xsl:variable name="tooltip_cancel_search">Cancel</xsl:variable>
  <xsl:variable name="tooltip_end_search">OK</xsl:variable>

  <!-- searchlist error messages for forms validation  -->
  <xsl:variable name="message_pattern">the value {0} does not match the required pattern</xsl:variable>
  <xsl:variable name="message_minlength">value must be at least {0} long</xsl:variable>
  <xsl:variable name="message_maxlength">value must be at most {0} long</xsl:variable>
  <xsl:variable name="message_min">value must be at least {0}</xsl:variable>
  <xsl:variable name="message_max">value must be at most {0}</xsl:variable>
  <xsl:variable name="message_mindate">date must be at least {0}</xsl:variable>
  <xsl:variable name="message_maxdate">date must be at most {0}</xsl:variable>
  <xsl:variable name="message_required">value is required; please select a value</xsl:variable>
  <xsl:variable name="message_dateformat">date/time format is invalid</xsl:variable>
  <xsl:variable name="message_thisnotvalid">This field is not valid</xsl:variable>
  <xsl:variable name="message_notvalid">{0} is not valid</xsl:variable>
  <xsl:variable name="message_listtooshort">List {0} has too few entries</xsl:variable>

  <!-- prompt for debug link -->
  <xsl:template name="prompt_debug">[debug]</xsl:template>

</xsl:stylesheet>
