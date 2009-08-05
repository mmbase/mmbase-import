

function WizardTree() {
    //this.mode = $("body").attr("class").replace(/editwizards/, '').trim();
    this.education = $("html head meta[name='Didactor-Education']").attr("content");
    this.setupTree();
}

WizardTree.prototype.reloadMode = function () {
    var url = $("#mode_url")[0].href;
    var self = this;
    var data = [];
    data.expires = 0;
    $('#mode-' + this.mode).load(url, data, function() {
        self.setupTree();
    });
    return false;
}

WizardTree.prototype.afterReload = function () {
}

WizardTree.prototype.setupTree = function() {
    var self = this;
    $(document).ready(function() {
        $('ul.treeview').treeview({
            collapsed: true,
            persist: "cookie",
            cookieId: "treeview_" + self.education + "_" + self.mode,
            cookieOptions: {expires: 100}
        }
                                 );
        $('ul.treeview li a').click(function() {
            window.text.location.href= this.href;
            window.text.focus();
            return false;
        });
    });
}


var wizardtree = new WizardTree();



function reloadMode() {
    wizardtree.reloadMode();
}
