/*
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<mm:content type="text/javascript" language="${param.locale}">
<fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
*/
/**
 * Generic mmbase search & relate tool. Javascript part.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Searcher.js.jsp 41486 2010-03-17 22:35:11Z michiel $
 */



$(document).ready(
    function() {
        MMBasePathSearcher.prototype.init($("body")[0]);
    }
);

$("div.lazy").
    live("mmsr_lazyloaded",
         function(e) {
             var div = e.target;
             MMBasePathSearcher.prototype.init(this);
         }
        );

$("div.mm_related.pathsearcher_ontheway").
    live("mmsrRelate",
         function (e, tr, relater) {
             var number = relater.getNumber(tr);
             var pathSearcher = $(e.target).closest("table.mmsr_pathsearch")[0].searcher;
             var index = $(e.target).closest("td").index() + 1;
             $(tr).addClass("selected");
             $(tr).siblings().removeClass("selected");
             pathSearcher.fillStep(index + 1, number);
         });

$("div.mm_related.pathsearcher_end").
    live("mmsrRelate",
         function (e, tr, relater) {
             var number = relater.getNumber(tr);
             relater.repository.searcher.dec();
             $(tr).addClass("removed");
             relater.repository.searcher.resetTrClasses();
             $(e.target).trigger("mmsrAddToList", [number]);
         });

function MMBasePathSearcher(table) {
    this.table = table;
    this.table.searcher = this;
    this.pid = $(this.table).attr("id");
    this.searcher = $(this.table).find("> tr > td > div");
}

MMBasePathSearcher.prototype.fillStep = function(step, node) {
    var td = $(this.table).find("> tr > td")[step - 1];
    var params = {};
    params.step = (step - 1) * 2;
    params.nodeNumber = node;
    params.pid = this.pid;
    var url = "${mm:link('/mmbase/searchrelate/path/searcher.jspx')}";
    var loader = $("<img src='${mm:link('/mmbase/style/ajax-loader.gif')}' height='32' width='32' alt='loading' class='searching' />");
    $(td).empty();
    $(td).append(loader);

    $(td).load(url, params,
               function() {
                   $(td).find("div.mm_related").each(
                       function() {
                           if (this.relater == null) {
                               this.relater = new MMBaseRelater(this);
                           }
                       });
                   $(td).find("~ td").empty();
               });

}

MMBasePathSearcher.prototype.init = function(el) {
    $(el).find("table.mmsr_pathsearch").each(
        function() {
            if (this.pathSearch == null) {
                this.pathSearcher = new MMBasePathSearcher(this);
            }
        });
}
/*
</fmt:bundle>
</mm:content>
*/