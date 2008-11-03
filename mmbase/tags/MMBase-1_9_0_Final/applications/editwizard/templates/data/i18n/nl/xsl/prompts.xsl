<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:import href="ew:xsl/prompts.xsl" />

  <!--
  prompts.xls
  Dutch version

  @since  MMBase-1.6
  @author Pierre van Rooden
  @version $Id: prompts.xsl,v 1.29 2008-10-29 13:04:06 michiel Exp $
  -->

<!-- prompts used in this editwizard. Override these prompts to change the view in your own versions -->
<!-- prompts for starting a editwizard -->
<xsl:variable name="tooltip_edit_wizard">Wijzigen...</xsl:variable>
<xsl:variable name="tooltip_add_wizard">Nieuw</xsl:variable>
<xsl:variable name="please_save">Eerst opslaan AUB</xsl:variable>
<!-- prompts for datefields -->
<xsl:variable name="date_january">januari</xsl:variable>
<xsl:variable name="date_february">februari</xsl:variable>
<xsl:variable name="date_march">maart</xsl:variable>
<xsl:variable name="date_april">april</xsl:variable>
<xsl:variable name="date_may">mei</xsl:variable>
<xsl:variable name="date_june">juni</xsl:variable>
<xsl:variable name="date_july">juli</xsl:variable>
<xsl:variable name="date_august">augustus</xsl:variable>
<xsl:variable name="date_september">september</xsl:variable>
<xsl:variable name="date_october">oktober</xsl:variable>
<xsl:variable name="date_november">november</xsl:variable>
<xsl:variable name="date_december">december</xsl:variable>

<xsl:variable name="day_sun">Sun</xsl:variable>
<xsl:variable name="day_mon">Mon</xsl:variable>
<xsl:variable name="day_tue">Tue</xsl:variable>
<xsl:variable name="day_wed">Wed</xsl:variable>
<xsl:variable name="day_thu">Thu</xsl:variable>
<xsl:variable name="day_fri">Fri</xsl:variable>
<xsl:variable name="day_sat">Sat</xsl:variable>

<xsl:variable name="datepicker_currentmonth">Ga naar huidige maand</xsl:variable>
<xsl:variable name="datepicker_today">Vandaag is</xsl:variable>
<xsl:variable name="datepicker_scrollleft">Klik hier om naar de voorgaande maand te scrollen. Houd de muisknop ingedrukt om automatisch te scrollen.</xsl:variable>
<xsl:variable name="datepicker_scrollright">Klik hier om naar de volgende maand te scrollen. Houd de muisknop ingedrukt om automatisch te scrollen.</xsl:variable>
<xsl:variable name="datepicker_selectmonth">Klik hier om een maand te selecteren.</xsl:variable>
<xsl:variable name="datepicker_selectyear">Klik hier om een jaar te selecteren.</xsl:variable>
<xsl:variable name="datepicker_selectdate">Selecteer [datum] als de datum.</xsl:variable>

<xsl:variable name="time_at">om</xsl:variable>
<!-- prompts for a binary field (upload/download) -->
<xsl:template name="prompt_file_upload">Bestand uploaden</xsl:template>
<xsl:template name="prompt_uploaded">Geüpload</xsl:template>
<xsl:template name="prompt_image_upload" >Upload nieuwe afbeelding</xsl:template>
<xsl:template name="prompt_image_replace">Vervang afbeelding</xsl:template>
<xsl:template name="prompt_do_download">Huidig bestand bekijken</xsl:template>
<xsl:template name="prompt_do_upload">Upload nieuw bestand</xsl:template>
<xsl:template name="prompt_no_file">Geen (nieuw) bestand.</xsl:template>
<xsl:template name="prompt_image_full">Volledige afbeelding</xsl:template>

<!-- prompts for a dropdown box -->
<xsl:template name="prompt_select">selecteer...</xsl:template>
<!-- up/down button prompts and tooltips -->

<xsl:variable name="tooltip_up">Verplaats dit item hoger in de lijst</xsl:variable>
<xsl:template name="prompt_up"><img src="{$mediadir}up.gif" border="0" alt="Omhoog" title="{$tooltip_up}" /></xsl:template>

<xsl:variable name="tooltip_down">Verplaats dit item lager in de lijst</xsl:variable>
<xsl:template name="prompt_down"><img src="{$mediadir}down.gif" border="0" alt="Omlaag" title="{$tooltip_down}" /></xsl:template>

<!-- new button prompts and tooltips -->
<xsl:template name="prompt_new"><img src="{$mediadir}new.gif" border="0" title="Nieuw" alt="Nieuw"/></xsl:template>
<xsl:variable name="tooltip_new">Voeg een nieuw item toe aan de lijst</xsl:variable>
<!-- remove button prompts and tooltips (for relations) -->
<xsl:template name="prompt_remove"><img src="{$mediadir}remove.gif" border="0"  alt="Verwijder"/></xsl:template>
<xsl:variable name="tooltip_remove">Verwijder dit item uit de lijst</xsl:variable>
<!-- delete button prompts and tooltips (for objects) -->
<xsl:template name="prompt_delete"><img src="{$mediadir}remove.gif" border="0"  alt="Verwijder"/></xsl:template>
<xsl:variable name="tooltip_delete">Verwijder dit item</xsl:variable>
<xsl:template name="prompt_delete_confirmation" >Weet u zeker dat u dit item wilt verwijderen?</xsl:template>
<xsl:template name="prompt_unlink_confirmation" >Weet u zeker dat u dit item wilt ontkoppelen?</xsl:template>
<!-- save button prompts and tooltips -->
<xsl:template name="prompt_save">Opslaan &amp; beëindigen</xsl:template>
<xsl:template name="prompt_save_only">Opslaan</xsl:template>
<xsl:variable name="tooltip_save">Bewaar alle wijzigingen en sluit deze taak.</xsl:variable>
<xsl:variable name="tooltip_save_only">Bewaar alle wijzigingen, maar ga door met editen.</xsl:variable>
<xsl:variable name="tooltip_no_save">De wijzigingen kunnen niet worden bewaard, sommige gegevens zijn niet correct ingevoerd.</xsl:variable>
<!-- cancel button prompts and tooltips -->
<xsl:template name="prompt_cancel">Annuleren</xsl:template>
<xsl:variable name="tooltip_cancel">Annuleer deze taak, wijzigingen (sinds de laatste maal 'Opslaan') worden niet bewaard.</xsl:variable>
<xsl:variable name="tooltip_no_cancel">Deze taak kan niet worden afgebroken.</xsl:variable>
<!-- step (form) button prompts and tooltips -->
<xsl:template name="prompt_step"><nobr>Stap <xsl:value-of select="position()" /></nobr></xsl:template>
<xsl:variable name="tooltip_step_not_valid" > is niet correct. Klik hier om te corrigeren.</xsl:variable>
<xsl:variable name="tooltip_valid" >Het huidige formulier is correct.</xsl:variable>
<xsl:variable name="tooltip_not_valid" >Het huidige formulier is niet correct. Corrigeer de rood gemarkeerde velden.</xsl:variable>
<!-- step forward and backward prompts and tooltips -->
<xsl:template name="prompt_previous" > &lt;&lt; </xsl:template>
<xsl:variable name="tooltip_previous" >Terug naar </xsl:variable>
<xsl:variable name="tooltip_no_previous" >Geen vorige stap</xsl:variable>
<xsl:template name="prompt_next" > &gt;&gt; </xsl:template>
<xsl:variable name="tooltip_next" >Verder naar </xsl:variable>
<xsl:variable name="tooltip_no_next" >Geen volgende stap</xsl:variable>
<xsl:variable name="tooltip_goto" >Ga naar </xsl:variable>
<!-- audio / video buttons prompts -->
<xsl:template name="prompt_audio" ><img src="{$mediadir}audio.gif" border="0" alt="Audio" /></xsl:template>
<xsl:variable name="tooltip_audio" >Luister naar de audio clip</xsl:variable>
<xsl:template name="prompt_video" ><img src="{$mediadir}video.gif" border="0" alt="Video" /></xsl:template>
<xsl:variable name="tooltip_video" >Bekijk de video clip</xsl:variable>
<!-- search : prompts for age filter -->
<xsl:template name="prompt_age" >Leeftijd</xsl:template>
<xsl:template name="age_now" >vandaag</xsl:template>
<xsl:template name="age_day" >1 dag</xsl:template>
<xsl:template name="age_week" >7 dagen</xsl:template>
<xsl:template name="age_month" >1 maand</xsl:template>
<xsl:template name="age_year" >1 jaar</xsl:template>
<xsl:template name="age_any" >alles</xsl:template>
<!-- search : other filters -->
<xsl:template name="prompt_search_list" >Zoek</xsl:template>
<xsl:template name="prompt_search_term" >Termen</xsl:template>
<xsl:template name="prompt_search" ><img src="{$mediadir}search.gif" border="0" alt="Zoek" /></xsl:template>
<xsl:variable name="tooltip_search" >Zoek een toe te voegen item</xsl:variable>
<xsl:variable name="tooltip_search_all">Zoek een toe te voegen item</xsl:variable>
<xsl:template name="prompt_search_title" >Bevat</xsl:template>
<xsl:template name="prompt_search_owner" >Eigenaar is</xsl:template>
<xsl:template name="prompt_search_number" >Nummer is</xsl:template>
<xsl:variable name="filter_required" >Het is verplicht een zoekterm in te vullen.</xsl:variable>
<!-- navigation -->
<xsl:template name="prompt_index">Start</xsl:template>
<xsl:variable name="tooltip_index" >Terug naar de startpagina</xsl:variable>
<xsl:template name="prompt_logout">Uitloggen</xsl:template>
<xsl:variable name="tooltip_logout" >Uitloggen en terug naar de startpagina</xsl:variable>
<!-- prompts and tooltips for lists -->
<xsl:template name="prompt_search_age">
  <xsl:param name="age" />
  <xsl:if test="$age=1"> van de laatste dag</xsl:if>
  <xsl:if test="$age=7"> van de laatste 7 dagen</xsl:if>
  <xsl:if test="$age=31"> van de laatste maand</xsl:if>
  <xsl:if test="$age=356"> van het afgelopen jaar</xsl:if>
  <xsl:if test="$age=-1"> alle</xsl:if>
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
    - zoek op <xsl:value-of select="$searchvalue" />
  </xsl:if>
  (items <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pagina <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
</xsl:template>
<xsl:variable name="tooltip_edit_list" >Dit zijn de items die u kan wijzigen.</xsl:variable>
<xsl:variable name="tooltip_sort_on">Sorteer op</xsl:variable>
<xsl:variable name="tooltip_sort_up">omhoog</xsl:variable>
<xsl:variable name="tooltip_sort_down">omlaag</xsl:variable>
<!-- searchlist prompts/tooltips -->
<xsl:variable name="tooltip_select_search">Selecteer een of meer items uit de lijst</xsl:variable>
<xsl:template name="prompt_no_results" >Geen items gevonden</xsl:template>
<xsl:template name="prompt_more_results" >(Meer items gevonden...)</xsl:template>
<xsl:template name="prompt_result_count" >(items <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pagina <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)</xsl:template>
<xsl:variable name="tooltip_cancel_search" >Annuleer</xsl:variable>
<xsl:variable name="tooltip_end_search" >Toevoegen</xsl:variable>
<!-- searchlist error messages for forms validation  -->
<xsl:variable name="message_pattern" >De waarde {0} volgt niet het vereiste patroon</xsl:variable>
<xsl:variable name="message_minlength" >Waarde moet minstens {0} karakters lang zijn</xsl:variable>
<xsl:variable name="message_maxlength" >Waarde kan hoogstens {0} karakters lang zijn</xsl:variable>
<xsl:variable name="message_min" >Waarde moet groter of gelijk zijn aan {0}</xsl:variable>
<xsl:variable name="message_max" >Waarde moet kleiner of gelijk zijn aan {0}</xsl:variable>
<xsl:variable name="message_mindate" >Datum moet groter of gelijk zijn aan{0}</xsl:variable>
<xsl:variable name="message_maxdate" >Datum moet kleiner of gelijk zijn aan {0}</xsl:variable>
<xsl:variable name="message_required" >Waarde is verplicht; Vul een waarde in</xsl:variable>
<xsl:variable name="message_dateformat" >Datum of tijd heeft fout formaat</xsl:variable>
<xsl:variable name="message_thisnotvalid" >Dit veld is incorrect</xsl:variable>
<xsl:variable name="message_notvalid" >{0} is incorrect</xsl:variable>

</xsl:stylesheet>
