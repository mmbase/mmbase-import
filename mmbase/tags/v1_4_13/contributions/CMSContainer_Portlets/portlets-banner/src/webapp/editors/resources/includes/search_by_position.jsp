<script type="text/javascript">

function selectPage(page, path, positions) {
    document.forms['bannersearchform'].page.value = page;
    document.forms['bannersearchform'].pagepath.value = path;

    var selectWindow = document.forms['bannersearchform'].position;
    for (var i = selectWindow.options.length -1 ; i >=0 ; i--) {
        selectWindow.options[i] = null;
    }
   
    selectWindow.options[0] = new Option(" - ", "");
    for (var i = 0 ; i < positions.length ; i++) {
        var position = positions[i];
        selectWindow.options[selectWindow.options.length] = new Option(position, position);
    }
    var allPositions = '';
    for (var i = 0 ; i < positions.length ; i++) {
        allPositions = allPositions + ';' + positions[i];
    }
    document.forms['bannersearchform'].allPositions.value = allPositions;
 }
function erase(field) {
    document.forms['bannersearchform'][field].value = '';
}
function eraseList(field) {
    document.forms['bannersearchform'][field].selectedIndex = -1;
}

var repositoryUrl = "<cmsc:staticurl page='/editors/repository/index.jsp'/>";
function openRepositoryWithChannel() {
    contentchannel = document.forms['<portlet:namespace />form'].contentchannel.value;
    if(contentchannel == undefined || contentchannel == '') {
        alert('<fmt:message key="edit_defaults.preview.noChannel"/>');
    }
    else {
        if(confirm('<fmt:message key="edit_defaults.preview.loseChanges"/>')) {
            window.top.bottompane.location = repositoryUrl + '?channel=' + contentchannel;
        }
    }
}
</script>
        <div class="body">
        <html:form action="/editors/resources/SearchBannerAction" method="post" styleId="bannersearchform">
            <html:hidden property="action" value="search" />
            <html:hidden property="offset" />
            <html:hidden property="order" />
            <html:hidden property="direction" />

            <mm:import id="contenttypes" jspvar="contenttypes">customer</mm:import>
            <%@include file="positionform.jsp"%>
        </html:form>
        </div>
