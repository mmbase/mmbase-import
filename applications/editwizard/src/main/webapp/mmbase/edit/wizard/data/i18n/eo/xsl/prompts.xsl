<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:import href="ew:xsl/prompts.xsl" />

  <!--
  prompts.xls
  Esperanto version

  @since  MMBase-1.6
  @author Michiel Meeuwissen
  @version $Id: prompts.xsl,v 1.13 2007-11-23 10:07:10 michiel Exp $
  -->

<!-- prompts used in this editwizard. Override these prompts to change the view in your own versions -->
<!-- prompts for starting a editwizard -->
<xsl:variable name="tooltip_edit_wizard">Ŝanĝi...</xsl:variable>
<xsl:variable name="tooltip_add_wizard">Nova</xsl:variable>
<!-- prompts for datefields -->
<xsl:variable name="date_january">januaro</xsl:variable>
<xsl:variable name="date_february">februaro</xsl:variable>
<xsl:variable name="date_march">marto</xsl:variable>
<xsl:variable name="date_april">aprilo</xsl:variable>
<xsl:variable name="date_may">majo</xsl:variable>
<xsl:variable name="date_june">junio</xsl:variable>
<xsl:variable name="date_july">julio</xsl:variable>
<xsl:variable name="date_august">aŭgusto</xsl:variable>
<xsl:variable name="date_september">septembro</xsl:variable>
<xsl:variable name="date_october">oktobro</xsl:variable>
<xsl:variable name="date_november">novembro</xsl:variable>
<xsl:variable name="date_december">decembro</xsl:variable>

<xsl:variable name="time_daymonth">-a de</xsl:variable>
<xsl:variable name="time_at">je</xsl:variable>
<!-- prompts for a binary field (upload/download) -->
<xsl:template name="prompt_file_upload">Sendu dosieron</xsl:template>
<xsl:template name="prompt_uploaded">sendita:</xsl:template>
<xsl:template name="prompt_image_upload" >Sendu novan bildon</xsl:template>
<xsl:template name="prompt_do_download">Rigardi ĉi-dosieron</xsl:template>
<xsl:template name="prompt_do_upload">Sendu novan dosieron</xsl:template>
<xsl:template name="prompt_no_file">Neniu (nova) dosiero</xsl:template>
<!-- prompts for a dropdown box -->
<xsl:template name="prompt_select">elektu...</xsl:template>
<!-- up/down button prompts and tooltips -->

<xsl:template name="prompt_up"><img src="{$mediadir}up.gif" border="0" alt="Supren"/></xsl:template>
<xsl:variable name="tooltip_up">Suprenigu ĉi tiun eron en la listo</xsl:variable>
<xsl:template name="prompt_down"><img src="{$mediadir}down.gif" border="0" alt="Malsupren"/></xsl:template>
<xsl:variable name="tooltip_down">Malsuprenigu ĉi tiun eron en la listo</xsl:variable>
<!-- new button prompts and tooltips -->
<xsl:template name="prompt_new"><img src="{$mediadir}new.gif" border="0" alt="Nova"/></xsl:template>
<xsl:variable name="tooltip_new">Aldonu eron en la liston</xsl:variable>
<!-- remove button prompts and tooltips (for relations) -->
<xsl:template name="prompt_remove"><img src="{$mediadir}remove.gif" border="0"  alt="Forigu"/></xsl:template>
<xsl:variable name="tooltip_remove">Forigu ĉi tiun eron el la listo</xsl:variable>
<!-- delete button prompts and tooltips (for objects) -->
<xsl:template name="prompt_delete"><img src="{$mediadir}remove.gif" border="0"  alt="Forigu"/></xsl:template>
<xsl:variable name="tooltip_delete">Forigu ĉi tiun eron</xsl:variable>
<xsl:template name="prompt_delete_confirmation" >Ĉu vi certas ke vi volas forigi ĉi tiun eron?</xsl:template>
<!-- save button prompts and tooltips -->
<xsl:template name="prompt_save">Konservu &amp; eliru</xsl:template>
<xsl:template name="prompt_save_only">Konservu</xsl:template>
<xsl:variable name="tooltip_save">Konservu ĉiujn ŝanĝojn.</xsl:variable>
<xsl:variable name="tooltip_no_save">Ne eblas konservi la ŝanĝojn, kelkaj informeroj ne estas korekte plenumitaj.</xsl:variable>
<!-- cancel button prompts and tooltips -->
<xsl:template name="prompt_cancel">Nefaru</xsl:template>
<xsl:variable name="tooltip_cancel">Nefaru ĉi tiun taskon, ŝanĝoj ne estos konservitaj.</xsl:variable>
<xsl:variable name="tooltip_no_cancel">Ne eblas nefari ĉi tiun taskon.</xsl:variable>
<!-- step (form) button prompts and tooltips -->
<xsl:template name="prompt_step">Paŝo <xsl:value-of select="position()" /></xsl:template>
<xsl:variable name="tooltip_step_not_valid" > ne estas korekta. Klaku jene por korekti.</xsl:variable>
<xsl:variable name="tooltip_valid" >Jena formularo estas korekta.</xsl:variable>
<xsl:variable name="tooltip_not_valid" >Jena formula ne estas korekta. Korektu la ruĝe markitajn kampojn.</xsl:variable>
<!-- step forward and backward prompts and tooltips -->
<xsl:template name="prompt_previous" > &lt;&lt; </xsl:template>
<xsl:variable name="tooltip_previous" >Reen al </xsl:variable>
<xsl:variable name="tooltip_no_previous" >Mankas antaŭa paŝo</xsl:variable>
<xsl:template name="prompt_next" > &gt;&gt; </xsl:template>
<xsl:variable name="tooltip_next" >Pluen al </xsl:variable>
<xsl:variable name="tooltip_no_next" >Mankas sekva paŝo</xsl:variable>
<xsl:variable name="tooltip_goto" >Iru al </xsl:variable>
<!-- audio / video buttons prompts -->
<xsl:template name="prompt_audio" ><img src="{$mediadir}audio.gif" border="0" alt="Aŭdo" /></xsl:template>
<xsl:variable name="tooltip_audio" >Aŭskultu la aŭd-fragmenton</xsl:variable>
<xsl:template name="prompt_video" ><img src="{$mediadir}video.gif" border="0" alt="Filmo" /></xsl:template>
<xsl:variable name="tooltip_video" >Rigardu la film-fragmenton</xsl:variable>
<!-- search : prompts for age filter -->
<xsl:template name="age_now" >hodiaŭ</xsl:template>
<xsl:template name="age_day" >1 tago</xsl:template>
<xsl:template name="age_week" >7 tagoj</xsl:template>
<xsl:template name="age_month" >1 monato</xsl:template>
<xsl:template name="age_year" >1 jaro</xsl:template>
<xsl:template name="age_any" >ĉio</xsl:template>
<!-- search : other filters -->
<xsl:template name="prompt_search" ><img src="{$mediadir}search.gif" border="0" alt="Serĉu" /></xsl:template>
<xsl:variable name="tooltip_search" >Serĉu aldonotan eron</xsl:variable>
<xsl:template name="prompt_search_title" >Enhavas</xsl:template>
<xsl:template name="prompt_search_owner" >Posedas</xsl:template>
<!-- navigation -->
<xsl:template name="prompt_index">Enirejo</xsl:template>
<xsl:variable name="tooltip_index" >Reen al enirejo</xsl:variable>
<xsl:template name="prompt_logout">Malsaluti</xsl:template>
<xsl:variable name="tooltip_logout" >Malsaluti enirejen</xsl:variable>
<xsl:variable name="filter_required" >Estas devige enigi serĉeron.</xsl:variable>
<!-- prompts and tooltips for lists -->
<xsl:template name="prompt_edit_list" >
  <xsl:param name="age" />
  <xsl:param name="searchvalue" />
  <xsl:value-of select="$title" disable-output-escaping="yes"  />(eroj <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pa&#x11D;o <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
</xsl:template>
<xsl:variable name="tooltip_edit_list" >Jen la ŝanĝeblaj eroj.</xsl:variable>
<!-- searchlist prompts/tooltips -->
<xsl:variable name="tooltip_select_search">Elektu unu aŭ pli da eroj el la listo</xsl:variable>
<xsl:template name="prompt_no_results" >Neniu ero troviĝis</xsl:template>
<xsl:template name="prompt_more_results" >(troviĝis pli da eroj...)</xsl:template>
<xsl:template name="prompt_result_count" >(eroj <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pa&#x11D;o <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)</xsl:template>
<xsl:variable name="tooltip_cancel_search" >Nefaru</xsl:variable>
<xsl:variable name="tooltip_end_search" >Aldoni</xsl:variable>
<!-- searchlist error messages for forms validation  -->
<xsl:variable name="message_pattern" >La valoro {0} ne estas ĝustaforma</xsl:variable>
<xsl:variable name="message_minlength" >Valoro longu minimume {0} signojn</xsl:variable>
<xsl:variable name="message_maxlength" >Valoru longu maksimume {0} signojn</xsl:variable>
<xsl:variable name="message_min" >Valoru pli grandu ol aŭ samu al {0}</xsl:variable>
<xsl:variable name="message_max" >Valoru malpli grandu ol aŭ samu al {0}</xsl:variable>
<xsl:variable name="message_mindate" >Dato pli grandu ol aŭ samu al {0}</xsl:variable>
<xsl:variable name="message_maxdate" >Dato malpli grandu ol aŭ samu al {0}</xsl:variable>
<xsl:variable name="message_required" >Valoro estas devigita; elektu ion</xsl:variable>
<xsl:variable name="message_dateformat" >Dato aŭ tempo malĝusteformas</xsl:variable>
<xsl:variable name="message_thisnotvalid" >Ĉi tiu kampo estas malkorekta</xsl:variable>
<xsl:variable name="message_notvalid" >{0} estas malkorekta</xsl:variable>

</xsl:stylesheet>
