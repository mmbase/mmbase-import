/**
 * Function checks the contentelementtype checboxes
 * according to the values that are found in the input parameter.
 * This is supposed to be a semi-colon delimited string of types.
 * @param ;-separated string of ce - types.
 */
function setCheckBoxes(selectedTypes, modus) {
   //alert('modus: ' + modus);
   var types = new Array();
   types = selectedTypes.split(",");
   for (var i=0;i<types.length;i++) {
      try {
         document.getElementById(types[i]).checked = true;
      } catch (er) {}
   }
   xableCheckBoxes();
   if (modus == "true") {
      disableAllCheckBoxes();
   }
}
/**
  * disables all check boxes
  */
function disableAllCheckBoxes() {
   with(document.forms[0]) {
      for (var i = 0; i<elements.length; i++) {
         if(elements[i].type == "checkbox") {
            elements[i].disabled = true;
         }
      }
   }
}

/**
 * Function disables or enables the checkboxes
 *
 */
function xableCheckBoxes() {
   with(document.forms[0]) {
      var checked = elements[0].checked;
      for (var i = 1; i<elements.length; i++) {   //skip the first checkbox
         if(elements[i].type == "checkbox") {
            //elements[i].checked = true;
            elements[i].disabled = checked;
         }
      }
   }
}

 /**
 * Function updates the hiddenvalue in which
 * the selected ce-types are stored.
 *
 */
function updateHiddenValue() {
   var hiddenValue = "";
   with(document.forms[0]) {
     for (var i = 1; i<elements.length; i++) {   //skip the first checkbox
       if(elements[i].type == "checkbox" && elements[i].checked == true) {
       	  if(hiddenValue!="") { hiddenValue += ","; }
          hiddenValue += elements[i].id;
       }
     }
   }
   document.getElementById("selectedTypes").value = hiddenValue;
   //alert("selected types: " + document.getElementById("selectedTypes").value);
}

/**
 * Function disables the type checkboxes, and sets them to checked.
 *
 */
function xableAllCheckBoxes() {
   with(document.forms[0]) {
      var checked = elements[0].checked;
      for (var i = 0; i<elements.length; i++) {
         if(elements[i].type == "checkbox") {
            if (elements[i].name != 'allTypesSelected' && elements[i].name != 'popupEditWizards') {
               elements[i].checked = checked;
               elements[i].disabled = checked;
            }
         }
      }
   }
   document.getElementById("selectedTypes").value = "";
}

/**
 * Function changes the column used for sorting.
 *
 * @param the column whereupon should be sorted
 */
function changeOrder(orderColumn) {
   document.getElementById("orderColumn").value = orderColumn;
   document.forms[0].submit();
   return true;
}

/**
 *  Function submits the form
 *  for navigation to a different
 *  page.
 *  Adjusted search criteria will
 *  be reset.
 */
 function pageIterate(toPage) {
     with(document.forms[0]) {
       reset();
       curPage.value=toPage;
       submit();
     }
 }

 /**
  * Sets the current page
  * back to 1 and submits.
  */
  function doFilter() {
    with(document.forms[0]) {
       curPage.value="1";
       submit();
     }
 }

function popupPaginaSelector() {
    var newWindow;
    var urlstring = "../paginamanagement/selector/pagina_selector.jsp?fieldname=paginanaam&fieldnumber=pageno&targetframe=bottompane";    
    newWindow = window.open(urlstring,'_popUpPage','height=500,width=400,left=100,top=100,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=no');
}

var modus ='<%=modus %>';
function toggleAdvanced() {
   if (modus=='advanced') {
      modus='simple';
      hideTr("button_standaard");
      showTr("button_geavanceerd");
      document.forms[0].modus.value="simple";
      document.forms[0].auteur.value="<%=OPTION_ALLE %>";
      document.forms[0].metatag.value="";
      document.forms[0].titel.value="";
      document.forms[0].changeAge.value="0";
      document.forms[0].paginanaam.value="- alle pagina's -";
      document.forms[0].pageno.value="0";
   } else {
      modus='advanced';
      hideTr("button_geavanceerd");
      showTr("button_standaard");
      document.forms[0].modus.value= "advanced";
   }
   showTrModus("ad_1", modus);
   showTrModus("ad_2", modus);
   showTrModus("ad_3", modus);
   showTrModus("ad_4", modus);
   return false;
}

/**
 *  Provide a certain Table Row with
 *  a certain style.
 *  @param name of the Table Row
 *  @param style of the Table Row
 */
function showTrModus(name, style) {
   //alert('name: ' + name);
   //alert('style: ' + style);
   if(style == 'simple') {
      document.getElementById(name).style.display = 'none';
   } else {
      document.getElementById(name).style.display = 'block';
   }
}

/** show table row */
function showTr(name) {
   document.getElementById(name).style.display = 'block';
}
/** hide table row */
function hideTr(name) {
   document.getElementById(name).style.display = 'none';
}

/**
 * Function forwards to either callEditWizard or
 * to closeWindowPassbackParams, depending on value of popup
 */
var popup = <%=popup %>;
function doForward(ce_id,ce_type) {
   if (popup==true) {
      closeWindowPassbackParams(ce_id);
   } else {
      callEditWizard(ce_id,ce_type);
   }
}

function callEditWizard(ce_id,ce_type) {
   if (document.forms[0].popupEditWizards.checked) {
      var url = '../../editors/WizardInitAction.eb?objectnumber=' + ce_id;
      url += '&closewindow=true';
      url += '&returnurl=/editors/beheerbibliotheek/index.jsp';
    window.open(url,"callEditwizardForm", "width=800,height=600,scrollbars=auto,toolbar=no,status=no,resizable=no");
   }
   else {
    document.callEditwizardForm.objectnumber.value=ce_id;
      document.callEditwizardForm.submit();
 }
}

function closeWindowPassbackParams(ce_id) {
    //set some parameter in the calling window
    opener.beheerbibliotheekCallback(ce_id);
    //
    window.close();
}

function closeWindow() {
   if (popup==true) {
      opener.refreshPage();
   } 
   window.close();
}

function doDelete(ce_id) {
 var url = 'deleteitem.jsp?objectnumber=' + ce_id;
 window.open(url,"biebDelete", "width=400,height=400,scrollbars=no,toolbar=no,status=no,resizable=no");
}
