<?xml version="1.0"?>
<xsl:stylesheet version="1.0"  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform">
<!--
  prompts.xls

  @since  MMBase-1.6
  @author Pierre van Rooden
  @version $Id: prompts.xsl,v 1.3 2004-06-29 16:31:35 michiel Exp $
-->

<!-- prompts used in this editwizard. Override these prompts to change the view in your own versions -->
<!-- prompts for starting a editwizard -->
<xsl:variable name="tooltip_edit_wizard">Change...</xsl:variable>
<xsl:template name="prompt_edit_wizard"><xsl:value-of select="$tooltip_edit_wizard" /></xsl:template><!-- to make the new 'i18n' xsl's work with this -->

<xsl:variable name="tooltip_add_wizard"><img src="{$mediadir}new.gif" border="0" width="20" height="20" alt="(start wizard)"/></xsl:variable>
<xsl:template name="prompt_add_wizard"><xsl:value-of select="$tooltip_add_wizard" /></xsl:template><!-- to make the new 'i18n' xsl's work with this -->

<!-- prompts for datefields -->
<xsl:template name="optionlist_months">
  <option value="1">january</option>
  <option value="2">february</option>
  <option value="3">march</option>
  <option value="4">april</option>
  <option value="5">may</option>
  <option value="6">june</option>
  <option value="7">july</option>
  <option value="8">august</option>
  <option value="9">september</option>
  <option value="10">october</option>
  <option value="11">november</option>
  <option value="12">december</option>
</xsl:template>
<xsl:variable name="time_daymonth"></xsl:variable><!-- Between day and month. Sadly, order cannot yet be adjusted -->
<xsl:variable name="time_at">at</xsl:variable><!-- Before the time -->
<!-- prompts for a binary field (upload/download) -->
<xsl:template name="prompt_file_upload">File Upload</xsl:template>
<xsl:template name="prompt_uploaded">Uploaded:</xsl:template>
<xsl:template name="prompt_image_upload" >Upload new image</xsl:template>
<xsl:template name="prompt_do_download">Download current</xsl:template>
<xsl:template name="prompt_do_upload">Upload new</xsl:template>
<xsl:template name="prompt_no_file">No (new) file.</xsl:template>
<!-- prompts for a dropdown box -->
<xsl:template name="prompt_select">select...</xsl:template>
<!-- up/down button prompts and tooltips -->

<xsl:variable name="tooltip_up">Move this item up in the list</xsl:variable>
<xsl:template name="prompt_up"><img src="{$mediadir}up.gif" border="0" width="20" height="20"  alt="(up)" title="{$tooltip_up}" /></xsl:template>
<xsl:template name="prompt_down"><img src="{$mediadir}down.gif" border="0" width="20" height="20"  alt="(down)"/></xsl:template>
<xsl:variable name="tooltip_down">Move this item down in the list</xsl:variable>
<!-- new button prompts and tooltips -->
<xsl:template name="prompt_new"><img src="{$mediadir}new.gif" border="0" width="20" height="20"  alt="(new)"/></xsl:template>
<xsl:variable name="tooltip_new">Add a new item to the list</xsl:variable>
<!-- remove button prompts and tooltips (for relations) -->
<xsl:template name="prompt_remove"><img src="{$mediadir}remove.gif" border="0" width="20" height="20"  alt="(remove)"/></xsl:template>
<xsl:variable name="tooltip_remove">Remove this item from the list</xsl:variable>
<!-- delete button prompts and tooltips (for objects) -->
<xsl:template name="prompt_delete"><img src="{$mediadir}remove.gif" border="0" width="20" height="20"  alt="(delete)"/></xsl:template>
<xsl:variable name="tooltip_delete">Delete this item</xsl:variable>
<xsl:template name="prompt_delete_confirmation" >Are you sure you want to delete this item?</xsl:template>
<!-- save button prompts and tooltips -->
<xsl:template name="prompt_save">save&amp;close</xsl:template>
<xsl:template name="prompt_save_only">save</xsl:template>
<xsl:variable name="tooltip_save">Store all changes.</xsl:variable>
<xsl:variable name="tooltip_save_only">Store all changes (but continue editing).</xsl:variable>
<xsl:variable name="tooltip_no_save">The changes cannot be saved, since some data is not filled in correctly.</xsl:variable>
<!-- cancel button prompts and tooltips -->
<xsl:template name="prompt_cancel">cancel</xsl:template>
<xsl:variable name="tooltip_cancel">Cancel this task, changes (made since last 'save') will NOT be saved.</xsl:variable>
<!-- step (form) button prompts and tooltips -->
<xsl:template name="prompt_step">step <xsl:value-of select="position()" /></xsl:template>
<xsl:variable name="tooltip_step_not_valid" > is NOT valid. Click here to correct the errors.</xsl:variable>
<xsl:variable name="tooltip_valid" >The current form is valid.</xsl:variable>
<xsl:variable name="tooltip_not_valid" >The current form is NOT valid. Correct red-marked fields and try again.</xsl:variable>
<!-- step forward and backward prompts and tooltips -->
<xsl:template name="prompt_previous" > &lt;&lt; </xsl:template>
<xsl:variable name="tooltip_previous" >Back to </xsl:variable>
<xsl:variable name="tooltip_no_previous" >No previous form</xsl:variable>
<xsl:template name="prompt_next" > &gt;&gt; </xsl:template>
<xsl:variable name="tooltip_next" >Forward to </xsl:variable>
<xsl:variable name="tooltip_no_next" >No next form</xsl:variable>
<xsl:variable name="tooltip_goto" >Go to </xsl:variable>
<!-- audio / video buttons prompts -->
<xsl:template name="prompt_audio" ><img src="{$mediadir}audio.gif" width="20" height="20" border="0" alt="(audio)" /></xsl:template>
<xsl:variable name="tooltip_audio" >Click here to hear the audio clip</xsl:variable>
<xsl:template name="prompt_video" ><img src="{$mediadir}video.gif" width="20" height="20" border="0" alt="(video)" /></xsl:template>
<xsl:variable name="tooltip_video" >Click here to view the video clip</xsl:variable>
<!-- search : prompts for age filter -->
<xsl:template name="prompt_age" >Age</xsl:template>
<xsl:template name="age_now" >today</xsl:template>
<xsl:template name="age_day" >1 day</xsl:template>
<xsl:template name="age_week" >7 days</xsl:template>
<xsl:template name="age_month" >1 month</xsl:template>
<xsl:template name="age_year" >1 year</xsl:template>
<xsl:template name="age_any" >any age</xsl:template>
<!-- search : other filters -->
<xsl:template name="prompt_search_list" >Search</xsl:template>
<xsl:template name="prompt_search_term" >Terms</xsl:template>
<xsl:template name="prompt_search" ><img src="{$mediadir}search.gif" border="0" width="20" height="20"/></xsl:template>
<xsl:variable name="tooltip_search" >Search and add an item</xsl:variable>
<xsl:template name="prompt_search_title" >Title contains</xsl:template>
<xsl:template name="prompt_search_number" >Number is</xsl:template>
<xsl:template name="prompt_search_owner" >Owner is</xsl:template>
<xsl:variable name="filter_required" >Entering a searchterm is required.</xsl:variable>
<!-- navigation -->
<xsl:template name="prompt_index">index</xsl:template>
<xsl:variable name="tooltip_index" >Return to the index page</xsl:variable>
<xsl:template name="prompt_logout">logout</xsl:template>
<xsl:variable name="tooltip_logout" >Logout and return to the index page</xsl:variable>
<!-- prompts and tooltips for lists -->
<xsl:template name="prompt_edit_list" ><xsl:value-of disable-output-escaping="yes" select="$title" /> (<xsl:value-of select="@count" /> items)</xsl:template>
<xsl:variable name="tooltip_edit_list" >These are the items that you can edit.</xsl:variable>
<!-- searchlist prompts/tooltips -->
<xsl:variable name="tooltip_select_search">please select one or more items from this list</xsl:variable>
<xsl:template name="prompt_no_results" >No entries found, try again...</xsl:template>
<xsl:template name="prompt_more_results" >(More items found...)</xsl:template>
<xsl:template name="prompt_result_count" ><xsl:value-of select="/list/@count" /> items found</xsl:template>
<xsl:variable name="tooltip_cancel_search" >Cancel</xsl:variable>
<xsl:variable name="tooltip_end_search" >OK</xsl:variable>
<!-- searchlist error messages for forms validation  -->
<xsl:variable name="message_pattern" >the value {0} does not match the required pattern</xsl:variable>
<xsl:variable name="message_minlength" >value must be at least {0} long</xsl:variable>
<xsl:variable name="message_maxlength" >value must be at most {0} long</xsl:variable>
<xsl:variable name="message_min" >value must be at least {0}</xsl:variable>
<xsl:variable name="message_max" >value must be at most {0}</xsl:variable>
<xsl:variable name="message_mindate" >date must be at least {0}</xsl:variable>
<xsl:variable name="message_maxdate" >date must be at most {0}</xsl:variable>
<xsl:variable name="message_required" >value is required; please select a value</xsl:variable>
<xsl:variable name="message_dateformat" >date/time format is invalid</xsl:variable>
<xsl:variable name="message_thisnotvalid" >This field is not valid</xsl:variable>
<xsl:variable name="message_notvalid" >{0} is not valid</xsl:variable>
<xsl:variable name="message_listtooshort" >List {0} has too few entries</xsl:variable>
</xsl:stylesheet>
