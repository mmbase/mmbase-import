<?xml version="1.0"?>
<xsl:stylesheet version="1.0"  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="ew:xsl/prompts.xsl" />

<!--
  prompts.xls

  @since  MMBase-1.6
  @author Pierre van Rooden
  @version $Id: prompts.xsl,v 1.10 2007-11-23 10:07:41 michiel Exp $
-->

<!-- prompts used in this editwizard. Override these prompts to change the view in your own versions -->
<!-- prompts for starting a editwizard -->
<xsl:variable name="tooltip_edit_wizard">Change...</xsl:variable>
<xsl:variable name="tooltip_add_wizard">New</xsl:variable>
<!-- prompts for datefields -->
<xsl:variable name="date_january">gennaio</xsl:variable>
<xsl:variable name="date_february">febbraio</xsl:variable>
<xsl:variable name="date_march">marzo</xsl:variable>
<xsl:variable name="date_april">aprile</xsl:variable>
<xsl:variable name="date_may">maggio</xsl:variable>
<xsl:variable name="date_june">giugno</xsl:variable>
<xsl:variable name="date_july">luglio</xsl:variable>
<xsl:variable name="date_august">agosto</xsl:variable>
<xsl:variable name="date_september">settembre</xsl:variable>
<xsl:variable name="date_october">ottobre</xsl:variable>
<xsl:variable name="date_november">novembre</xsl:variable>
<xsl:variable name="date_december">dicembre</xsl:variable>

<xsl:variable name="time_daymonth"></xsl:variable><!-- Between day and month. Sadly, order cannot yet be adjusted -->
<xsl:variable name="time_at">at</xsl:variable><!-- Before the time -->
<!-- prompts for a binary field (upload/download) -->
<xsl:template name="prompt_file_upload">Carica File</xsl:template>
<xsl:template name="prompt_uploaded">Caricato:</xsl:template>
<xsl:template name="prompt_image_upload" >Carica un'altra Immagine</xsl:template>
<xsl:template name="prompt_do_download">Sacrica l'attuale</xsl:template>
<xsl:template name="prompt_do_upload">Carica una nuova</xsl:template>
<xsl:template name="prompt_no_file">Nessun (nuovo) file.</xsl:template>
<!-- prompts for a dropdown box -->
<xsl:template name="prompt_select">seleziona...</xsl:template>
<!-- up/down button prompts and tooltips -->

<xsl:template name="prompt_up"><img src="{$mediadir}up.gif" border="0" width="20" height="20"  alt="(up)"/></xsl:template>
<xsl:variable name="tooltip_up">Sposta questo item in alto nella lista</xsl:variable>
<xsl:template name="prompt_down"><img src="{$mediadir}down.gif" border="0" width="20" height="20"  alt="(down)"/></xsl:template>
<xsl:variable name="tooltip_down">Sposta questo item in basso nella lista</xsl:variable>
<!-- new button prompts and tooltips -->
<xsl:template name="prompt_new"><img src="{$mediadir}new.gif" border="0" width="20" height="20"  alt="(new)"/></xsl:template>
<xsl:variable name="tooltip_new">Aggiungi un nuovo item alla lista</xsl:variable>
<!-- remove button prompts and tooltips (for relations) -->
<xsl:template name="prompt_remove"><img src="{$mediadir}remove.gif" border="0" width="20" height="20"  alt="(remove)"/></xsl:template>
<xsl:variable name="tooltip_remove">Rimuovi questo item alla lista</xsl:variable>
<!-- delete button prompts and tooltips (for objects) -->
<xsl:template name="prompt_delete"><img src="{$mediadir}remove.gif" border="0" width="20" height="20"  alt="(delete)"/></xsl:template>
<xsl:variable name="tooltip_delete">Cancella questo item</xsl:variable>
<xsl:template name="prompt_delete_confirmation" >Siete sicuro di voler cancellare questo item?</xsl:template>
<!-- save button prompts and tooltips -->
<xsl:template name="prompt_save">salva</xsl:template>
<xsl:variable name="tooltip_save">Salva tutte le modifiche.</xsl:variable>
<xsl:variable name="tooltip_no_save">Le modifiche non possono essere salvate, poiche' alcuni dati sono stati inseriti in modo incorretto.</xsl:variable>
<!-- cancel button prompts and tooltips -->
<xsl:template name="prompt_cancel">annulla</xsl:template>
<xsl:variable name="tooltip_cancel">Annulla questo incarico, le modifiche NON verranno salvate.</xsl:variable>
<!-- step (form) button prompts and tooltips -->
<xsl:template name="prompt_step">passo <xsl:value-of select="position()" /></xsl:template>
<xsl:variable name="tooltip_step_not_valid" > NON e' valido. Clicca qui per correggere gli errori.</xsl:variable>
<xsl:variable name="tooltip_valid" >Il corrente modulo e' valido.</xsl:variable>
<xsl:variable name="tooltip_not_valid" >Il corrente modulo NON e' valido. Correggi i campi segnati in rosso e prova di nuovo.</xsl:variable>
<!-- step forward and backward prompts and tooltips -->
<xsl:template name="prompt_previous" > &lt;&lt; </xsl:template>
<xsl:variable name="tooltip_previous" >Indietro a </xsl:variable>
<xsl:variable name="tooltip_no_previous" >Nessun precedente modulo</xsl:variable>
<xsl:template name="prompt_next" > &gt;&gt; </xsl:template>
<xsl:variable name="tooltip_next" >Avanti a </xsl:variable>
<xsl:variable name="tooltip_no_next" >Nessun modulo seguente</xsl:variable>
<xsl:variable name="tooltip_goto" >Vai a </xsl:variable>
<!-- audio / video buttons prompts -->
<xsl:template name="prompt_audio" ><img src="{$mediadir}audio.gif" width="20" height="20" border="0" alt="(audio)" /></xsl:template>
<xsl:variable name="tooltip_audio" >Clicca qui per ascoltare l'audio-clip</xsl:variable>
<xsl:template name="prompt_video" ><img src="{$mediadir}video.gif" width="20" height="20" border="0" alt="(video)" /></xsl:template>
<xsl:variable name="tooltip_video" >Clicca qui per visionare il video-clip</xsl:variable>
<!-- search : prompts for age filter -->
<xsl:template name="age_now" >oggi</xsl:template>
<xsl:template name="age_day" >1 giorno</xsl:template>
<xsl:template name="age_week" >7 giorni</xsl:template>
<xsl:template name="age_month" >1 mese</xsl:template>
<xsl:template name="age_year" >1 anno</xsl:template>
<xsl:template name="age_any" >qualunque eta'</xsl:template>
<!-- search : other filters -->
<xsl:template name="prompt_search" ><img src="{$mediadir}search.gif" border="0" width="20" height="20"/></xsl:template>
<xsl:variable name="tooltip_search" >Cerca e aggiungi un item</xsl:variable>
<xsl:template name="prompt_search_title" >Contiene</xsl:template>
<xsl:template name="prompt_search_owner" >Il proprietario e'</xsl:template>
<xsl:variable name="filter_required" >E' richiesto l'inserimento di un termine da ricercare.</xsl:variable>
<!-- navigation -->
<xsl:template name="prompt_index">indice</xsl:template>
<xsl:variable name="tooltip_index" >Ritorna alla pagina dell'indice</xsl:variable>
<xsl:template name="prompt_logout">logout</xsl:template>
<xsl:variable name="tooltip_logout" >Logout e ritorna alla pagina dell'indice</xsl:variable>
<!-- prompts and tooltips for lists -->
<xsl:template name="prompt_edit_list" >
  <xsl:param name="age" />
  <xsl:param name="searchvalue" />
  <xsl:value-of select="$title" disable-output-escaping="yes"  />(voci <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pagina <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
</xsl:template>
<xsl:variable name="tooltip_edit_list" >Questi sono gli items che puo' inserire.</xsl:variable>
<!-- searchlist prompts/tooltips -->
<xsl:variable name="tooltip_select_search">prego selezioni uno o piu' items di questa lista</xsl:variable>
<xsl:template name="prompt_no_results" >Nessun resultato, provi ancora...</xsl:template>
<xsl:template name="prompt_more_results" >(Ulteriori risultati...)</xsl:template>
<xsl:template name="prompt_result_count" >(voci <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pagina <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)</xsl:template>
<xsl:variable name="tooltip_cancel_search" >Annulla</xsl:variable>
<xsl:variable name="tooltip_end_search" >OK</xsl:variable>
<!-- searchlist error messages for forms validation  -->
<xsl:variable name="message_pattern" >il valore {0} non si accorda con il modello richiesto</xsl:variable>
<xsl:variable name="message_minlength" >il valore deve essre al minimo {0} lungo</xsl:variable>
<xsl:variable name="message_maxlength" >il valore deve essre almassimo {0} lungo</xsl:variable>
<xsl:variable name="message_min" >il valore deve essre almeno {0}</xsl:variable>
<xsl:variable name="message_max" >il valore deve essre almassimo {0}</xsl:variable>
<xsl:variable name="message_mindate" >la data deve essre almeno {0}</xsl:variable>
<xsl:variable name="message_maxdate" >la data deve essre almassimo {0}</xsl:variable>
<xsl:variable name="message_required" >value is required; please select a value</xsl:variable>
<xsl:variable name="message_dateformat" >il formato di data/tempo e' invalido</xsl:variable>
<xsl:variable name="message_thisnotvalid" >Questo campo non e' valido</xsl:variable>
<xsl:variable name="message_notvalid" >{0} non e' valido</xsl:variable>
<xsl:variable name="message_listtooshort" >La lista {0} ha insufficienti voci</xsl:variable>
</xsl:stylesheet>
