function massDelete(form, confirmmessage) {
    if (confirmmessage) {
        if (confirm(confirmmessage)) {
                 var checkboxs = document.getElementsByTagName("input");
                 var objectnumbers = '';
                 for(i = 0; i < checkboxs.length; i++) {
                    if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
                       objectnumbers += checkboxs[i].value+",";
                    }
                 }
                 if(objectnumbers == ''){
                    return ;
                 }
                 objectnumbers = objectnumbers.substr(0,objectnumbers.length - 1);
                 form.deleteRequest.value = objectnumbers;
                 form.submit();
        }
    }   
}