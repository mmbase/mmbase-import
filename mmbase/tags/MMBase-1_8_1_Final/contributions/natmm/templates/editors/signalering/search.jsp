<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">

<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>

   <title>Nieuwe taak</title>
   <script language="JavaScript" src="../../calendar/calendar.js"></script>
   <script>
      function popUpCalendarVan() {
         if (document.forms[0].datumVanDag.value != "" && document.forms[0].datumVanMaand.value != "" && document.forms[0].datumVanJaar.value != "") 
               document.forms[0]._hiddenDateVan.value = document.forms[0].datumVanDag.value + '-' + document.forms[0].datumVanMaand.value + '-' + document.forms[0].datumVanJaar.value;
        popup_calendar_year("document.forms[0]._hiddenDateVan");
      }
        
      function popUpCalendarTot() {
         if(document.forms[0].datumTotDag.value != "" && document.forms[0].datumTotMaand.value != "" && document.forms[0].datumTotJaar.value != "")
               document.forms[0]._hiddenDateTot.value = document.forms[0].datumTotDag.value + '-' + document.forms[0].datumTotMaand.value + '-' + document.forms[0].datumTotJaar.value;
            popup_calendar_year("document.forms[0]._hiddenDateTot");
      }
        
      function popupDone(resultfield) {
            if(resultfield == document.forms[0]._hiddenDateVan){
            var splitData = document.forms[0]._hiddenDateVan.value.split('-');
               if (splitData.length == 3) {
                   document.forms[0].datumVanDag.value = splitData[0];
                   document.forms[0].datumVanMaand.value = splitData[1];
                   document.forms[0].datumVanJaar.value = splitData[2];
               }
            }
            
            if (resultfield == document.forms[0]._hiddenDateTot){
               var splitData = document.forms[0]._hiddenDateTot.value.split('-');
               if (splitData.length == 3) {
                   document.forms[0].datumTotDag.value = splitData[0];
                   document.forms[0].datumTotMaand.value = splitData[1];
                   document.forms[0].datumTotJaar.value = splitData[2];
               }
            }
      }
   </script>
</head>

<body>
    
<form action="overzicht.jsp" method="post">

<table>
   <tr>
      <td>
         <table cellpadding="2" cellspacing="1">
            <tr>
               <td colspan="2"><h2>Zoek taak</h2></td>
            </tr>
            <tr>
               <td>Content type:</td>
               <td align="right">
                  <select name="contentelement">
                     <option value="-1">Alle</option>
                     <option value="images">Afbeelding</option>
                     <option value="artikel">Artikel</option>
                     <option value="attachments">Bijlage</option>
                     <option value="evenement">Evenement</option>
                     <option value="flash">Flash</option>
                     <option value="link">Link</option>
                     <option value="poll">Opinie</option>
                     <option value="organisatie">Organisatie</option>
                     <option value="persoon">Persoon</option>
                     <option value="vacature">Vacature</option>
                     <option value="vgv">Veel Gestelde Vraag</option>
                  <select>
               </td>
            </tr>
            <tr>
               <td>Dead line:</td>
               <td nowrap align="right">
                  <b>van</b>&nbsp;&nbsp; <input type="text" name="datumVanDag" maxlength="2" size="2" tabindex="2">-
                  <input type="text" name="datumVanMaand" maxlength="2" size="2" tabindex="3">-
                  <input type="text" name="datumVanJaar" maxlength="4" size="4" tabindex="4">
                   <a href="javascript:popUpCalendarVan()"><img src="../../calendar/show-calendar-on-button.gif" width="24" height="24" align="absmiddle" border="0"></a>
                  <input type="hidden" name="_hiddenDateVan">
                </td>
           </tr>
           <tr>
               <td>&nbsp;</td>
                <td nowrap align="right">
                  <b>tot</b>&nbsp;&nbsp; <input type="text" name="datumTotDag" maxlength="2" size="2" tabindex="2">-
                  <input type="text" name="datumTotMaand" maxlength="2" size="2" tabindex="3">-
                  <input type="text" name="datumTotJaar" maxlength="4" size="4" tabindex="4">
                   <a href="javascript:popUpCalendarTot()"><img src="../../calendar/show-calendar-on-button.gif" width="24" height="24" align="absmiddle" border="0"></a>
                  <input type="hidden" name="_hiddenDateTot">
                </td>
           </tr>
            <tr>
               <td>Gebruikers:</td>
               <td align="right">
                  <select name="usernumber">
                     <option value="-1">Alle</option>
                     <mm:list path="rubriek,rolerel,users" distinct="true" fields="users.number,users.voornaam,users.tussenvoegsel,users.achternaam" orderby="users.achternaam,users.voornaam" constraints="rolerel.rol >= 1">
                        <option value="<mm:field name="users.number"/>"><mm:field name="users.voornaam"/> <mm:field name="users.tussenvoegsel"/> <mm:field name="users.achternaam"/></option>
                     </mm:list>
                  </select>
               </td>
            </tr>
            <tr>
               <td>&nbsp;</td>
               <td align="right"><input type="submit" name="search" value="Zoek"></td>
            </tr>
         </table>
      </td>
   </tr>
</table>

</form>

</body>
</html>

</mm:cloud>