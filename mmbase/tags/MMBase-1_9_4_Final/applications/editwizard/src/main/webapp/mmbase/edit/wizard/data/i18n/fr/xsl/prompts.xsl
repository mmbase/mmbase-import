<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:import href="ew:xsl/prompts.xsl" />

  <!--
    prompts.xls
    French version
    
    @since  MMBase-1.8.7
    @author Ahlonko
    @version $Id: prompts.xsl, v1.0 
  -->

  <!-- prompts used in this editwizard. Override these prompts to change the view in your own versions -->
  <!-- prompts for starting a editwizard -->
  <xsl:variable name="tooltip_edit_wizard">Changer...</xsl:variable>
  <xsl:variable name="please_save">SVP sauvegarder en premier</xsl:variable>
  <xsl:variable name="tooltip_add_wizard">Nouveau</xsl:variable>
  <!-- prompts for datefields -->
  <xsl:variable name="date_january">janvier</xsl:variable>
  <xsl:variable name="date_february">février</xsl:variable>
  <xsl:variable name="date_march">mars</xsl:variable>
  <xsl:variable name="date_april">avril</xsl:variable>
  <xsl:variable name="date_may">mai</xsl:variable>
  <xsl:variable name="date_june">juin</xsl:variable>
  <xsl:variable name="date_july">juillet</xsl:variable>
  <xsl:variable name="date_august">août</xsl:variable>
  <xsl:variable name="date_september">septembre</xsl:variable>
  <xsl:variable name="date_october">octobre</xsl:variable>
  <xsl:variable name="date_november">novembre</xsl:variable>
  <xsl:variable name="date_december">décembre</xsl:variable>

  <xsl:variable name="day_sun">Dim</xsl:variable>
  <xsl:variable name="day_mon">Lun</xsl:variable>
  <xsl:variable name="day_tue">Mar</xsl:variable>
  <xsl:variable name="day_wed">Mer</xsl:variable>
  <xsl:variable name="day_thu">Jeu</xsl:variable>
  <xsl:variable name="day_fri">Ven</xsl:variable>
  <xsl:variable name="day_sat">Sam</xsl:variable>

  <xsl:variable name="datepicker_currentmonth">Aller au mois actuel</xsl:variable>
  <xsl:variable name="datepicker_today">Aujourd'hui est</xsl:variable>
  <xsl:variable name="datepicker_scrollleft">Cliquez ici pour aller au mois précédent.Pressez la bouton de la souris pour défiler automatiquement.</xsl:variable>
  <xsl:variable name="datepicker_scrollright">Cliquez ici pour aller au mois prochain.Pressez la bouton de la souris pour défiler automatiquement.</xsl:variable>
  <xsl:variable name="datepicker_selectmonth">Cliquez ici pour sélectionner un mois.</xsl:variable>
  <xsl:variable name="datepicker_selectyear">Cliquez ici pour sélectionner une année.</xsl:variable>
  <xsl:variable name="datepicker_selectdate">Sélectionnez [datum] comme la date.</xsl:variable>

  <xsl:variable name="time_at">à</xsl:variable>
  <!-- prompts for a binary field (upload/download) -->
  <xsl:template name="prompt_file_upload">Fichier à charger</xsl:template>
  <xsl:template name="prompt_uploaded">Chargé:</xsl:template>
  <xsl:template name="prompt_image_upload">Charger une nouvelle image</xsl:template>
  <xsl:template name="prompt_image_replace">Remplacer une nouvelle image</xsl:template>
  <xsl:template name="prompt_do_download">Téléchargement actuel</xsl:template>
  <xsl:template name="prompt_do_upload">Nouveau chargement</xsl:template>
  <xsl:template name="prompt_no_file">Aucun (nouveau) ficher.</xsl:template>
  <xsl:template name="prompt_image_full">Image complète</xsl:template>

  <!-- prompts for a dropdown box -->
  <xsl:template name="prompt_select">sélectionnez...</xsl:template>
  <!-- up/down button prompts and tooltips -->

  <xsl:variable name="tooltip_up">Glisser cet élément en haut de la liste</xsl:variable>
  <xsl:template name="prompt_up"><img src="{$mediadir}up.gif" alt="{$tooltip_up}" /></xsl:template>

  <xsl:variable name="tooltip_down">Glisser cet élément en bas de la liste</xsl:variable>
  <xsl:template name="prompt_down"><img src="{$mediadir}down.gif" alt="{$tooltip_down}" /></xsl:template>

  <!-- new button prompts and tooltips -->
  <xsl:variable name="tooltip_new">Ajouter un nouvel élément à la liste</xsl:variable>

  <!-- remove button prompts and tooltips (for relations) -->
  <xsl:variable name="tooltip_remove">Supprimer cet élément de la liste. Les éléments supprimés sont toujours présents dans MMBase.</xsl:variable>
  <xsl:template name="prompt_remove"><img src="{$mediadir}remove.gif" alt="{$tooltip_remove}" class="imgbutton"/></xsl:template>

  <!-- delete button prompts and tooltips (for objects) -->
  <xsl:variable name="tooltip_delete">Supprimer cet élément</xsl:variable>
  <xsl:template name="prompt_delete"><img src="{$mediadir}remove.gif" alt="{$tooltip_delete}" /></xsl:template>
  <xsl:template name="prompt_unlink"><img src="{$mediadir}unlink.gif" alt="{$tooltip_remove}" /></xsl:template>
  <xsl:template name="prompt_delete_confirmation">Êtes vous sûr(e) de vouloir supprimer cet élément?</xsl:template>
  <xsl:template name="prompt_unlink_confirmation">Êtes vous sûr(e) de vouloir supprimer ce lien?</xsl:template>

  <!-- help button prompts and tooltips -->
  <xsl:variable name="tooltip_help">Aide</xsl:variable>
  <xsl:template name="prompt_help"><img src="{$mediadir}help.gif" alt="{$tooltip_help}" class="imgbutton"/></xsl:template>

  <!-- save button prompts and tooltips -->
  <xsl:template name="prompt_save">enregistrer et sortir</xsl:template>
  <xsl:template name="prompt_save_only">enregistrer</xsl:template>
  <xsl:variable name="tooltip_save">Enregistrer toutes les modifications.</xsl:variable>
  <xsl:variable name="tooltip_save_only">Enregistrer toutes les modifications (mais continuer l'édition).</xsl:variable>
  <xsl:variable name="tooltip_no_save">Les modifications ne pourront être enregistrées tant que certaines données restent non renseignées correctement.</xsl:variable>

  <!-- cancel button prompts and tooltips -->
  <xsl:template name="prompt_cancel">annuler</xsl:template>
  <xsl:variable name="tooltip_cancel">Annuler cette tâche, les modifications NE seront pas enregistrées.</xsl:variable>

  <!-- step (form) button prompts and tooltips -->
  <xsl:template name="prompt_step">étape <xsl:value-of select="position()" /></xsl:template>
  <xsl:variable name="tooltip_step_not_valid">N'est PAS valide. Cliquez ici pour corriger les erreurs.</xsl:variable>
  <xsl:variable name="tooltip_valid">L'actuel formulaire est valide.</xsl:variable>
  <xsl:variable name="tooltip_not_valid">Le présent formulaire n'est pas valide. Corrigez les champs marqués de rouge et réessayez.</xsl:variable>

  <!-- step forward and backward prompts and tooltips -->
  <xsl:template name="prompt_previous">&lt;&lt;</xsl:template>
  <xsl:variable name="tooltip_previous">Retour à</xsl:variable>
  <xsl:variable name="tooltip_no_previous">Aucun formulaire précédent</xsl:variable>
  <xsl:template name="prompt_next" > &gt;&gt; </xsl:template>
  <xsl:variable name="tooltip_next">Transférer à</xsl:variable>
  <xsl:variable name="tooltip_no_next">Aucun prochain formulaire</xsl:variable>
  <xsl:variable name="tooltip_goto">Aller à</xsl:variable>

  <!-- audio / video buttons prompts -->
  <xsl:variable name="tooltip_audio">Cliquez ici pour écouter le clip audio</xsl:variable>
  <xsl:template name="prompt_audio"><img src="{$mediadir}audio.gif" alt="($tooltip_audio)" class="imgbutton"/></xsl:template>
  <xsl:variable name="tooltip_video">Cliquez ici pour reagrder le clip vidéo</xsl:variable>
  <xsl:template name="prompt_video"><img src="{$mediadir}video.gif" alt="($tooltip_video)" class="imgbutton"/></xsl:template>

  <!-- search : prompts for age filter -->
  <xsl:template name="prompt_age">Age</xsl:template>
  <xsl:template name="age_now">aujourd'hui</xsl:template>
  <xsl:template name="age_day">1 jour</xsl:template>
  <xsl:template name="age_week">7 jours</xsl:template>
  <xsl:template name="age_month">1 mois</xsl:template>
  <xsl:template name="age_year">1 an</xsl:template>
  <xsl:template name="age_any">Tout</xsl:template>

  <!-- search : other filters -->
  <xsl:template name="prompt_search_list">Recherche</xsl:template>
  <xsl:template name="prompt_search_term">Thèmes</xsl:template>

  <xsl:template name="prompt_search" ><img src="{$mediadir}search.gif" border="0" alt="Search" /></xsl:template>
<xsl:variable name="tooltip_search_all">Chercher un élément à ajouter</xsl:variable>
  <xsl:variable name="tooltip_search">Chercher</xsl:variable>
  <xsl:variable name="tooltip_search_all">Chercher et ajouter un élémént</xsl:variable>
  <xsl:template name="prompt_search_title">Contient</xsl:template>
  <xsl:template name="prompt_search_number">Le numéro est</xsl:template>
  <xsl:template name="prompt_search_owner">Le proprétaire est </xsl:template>
  <xsl:variable name="filter_required">La saisie d'un élément recherché est requise.</xsl:variable>

  <!-- navigation -->
  <xsl:template name="prompt_index">index</xsl:template>
  <xsl:variable name="tooltip_index">Retour à la page de départ</xsl:variable>
  <xsl:template name="prompt_logout">logout</xsl:template>
  <xsl:variable name="tooltip_logout">Déconnexion et retour à la page de départ</xsl:variable>
<!-- prompts and tooltips for lists -->
  <xsl:template name="prompt_search_age">
    <xsl:param name="age" />
    <xsl:if test="$age=1"> du jour précédent</xsl:if>
    <xsl:if test="$age=7"> des 7 jours précédents</xsl:if>
    <xsl:if test="$age=31">du mois précédent</xsl:if>
    <xsl:if test="$age=356">de l'année précédente</xsl:if>
    <xsl:if test="$age=-1"> Tout</xsl:if>
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
        - recherche de <xsl:value-of select="$searchvalue" />
      </xsl:if>
      (éléments <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pages <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
  </xsl:template>
  <xsl:variable name="tooltip_edit_list">Vous pouvez éditer ces éléments.</xsl:variable>
  <xsl:variable name="tooltip_sort_on">Tri sur</xsl:variable>
  <xsl:variable name="tooltip_sort_up">haut</xsl:variable>
  <xsl:variable name="tooltip_sort_down">bas</xsl:variable>

  <!-- searchlist prompts/tooltips -->
  <xsl:variable name="searchpage_title">Résultats de la recherche</xsl:variable>
  <xsl:variable name="tooltip_select_search">SVP sélectionnez une ou plusieurs élémens dans cette liste</xsl:variable>
  <xsl:template name="prompt_no_results">Aucune correspondance trouvée, essayez encore...</xsl:template>
  <xsl:template name="prompt_more_results">(Plus d'éléments trouvés...)</xsl:template>
  <xsl:template name="prompt_result_count" >(éléments <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pages <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)</xsl:template>
  <xsl:variable name="tooltip_cancel_search">Annuler</xsl:variable>
  <xsl:variable name="tooltip_end_search">OK</xsl:variable>

  <!-- searchlist error messages for forms validation  -->
  <xsl:variable name="message_pattern">la valeur {0} ne coorespond pas au format exigé</xsl:variable>
  <xsl:variable name="message_minlength">la valeur doit être au moins {0} long</xsl:variable>
  <xsl:variable name="message_maxlength">la valeur doit être au plus {0} long</xsl:variable>
  <xsl:variable name="message_min">la valeur doit être au moins {0}</xsl:variable>
  <xsl:variable name="message_max">la valeur doit être au plus {0}</xsl:variable>
  <xsl:variable name="message_mindate">la date doit être au moins {0}</xsl:variable>
  <xsl:variable name="message_maxdate">la date doit être au plus {0}</xsl:variable>
  <xsl:variable name="message_required">une valeur est requise; SVP sélectioner une valeur</xsl:variable>
  <xsl:variable name="message_dateformat">le format date/heure n'est pas valide</xsl:variable>
  <xsl:variable name="message_thisnotvalid">ce champs n'est pas valide</xsl:variable>
  <xsl:variable name="message_notvalid">{0} n'est pas valide</xsl:variable>
  <xsl:variable name="message_listtooshort">liste {0} a très peu d'entrées</xsl:variable>

</xsl:stylesheet>
