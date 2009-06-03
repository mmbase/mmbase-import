/* -*- css -*- */
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content  expires="0" type="text/css">

.searchable h1 {
  background-color: #eee;
  font-weight: bold;
  margin-bottom: 0px;
  border: solid 1px #000;
}
.searchable .searchform {
  width: 100%;
  border: solid 1px #000;
}

table.searchform {
  margin-bottom: 0px;

}

input.searching
{
	background-image: url("${mm:link('/mmbase/style/ajax-loader-16.gif')}");
	background-color: #fff;
	background-position: right 1px;
	background-repeat: no-repeat;
}

.searchresult table {
  border: solid 1px #000;
}
.searchresult table caption  {
  border-left: solid 1px #000;
  border-right: solid 1px #000;
}

.searchresult table caption.emptysearch.pagingnotneeded  {
  display: none;
}

.searchresult a {
  color: #00f;
}
.searchresult caption,
.searchresult thead tr,
.searchresult tfoot tr {
  color: #000;
  background-color: #ffb;
}

.searchresult table tbody tr.even {
  color: #000;
  background-color: #ddd;
}
.searchresult table tbody tr.odd {
  color: #000;
  background-color: #fff;
}

.searchresult table tbody tr.relation {
  color: #333;
  background-color: #FFD;
  font-size: 0.8em;
}

.searchresult tbody tr.even.new {
  background-color: #ded;
}
.searchresult tbody tr.odd.new {
  background-color: #efe;
}

.searchresult tbody tr.even.removed {
  background-color: #edd;
}
.searchresult tbody tr.odd.removed {
  background-color: #fee;
}

.searchresult tbody tr.click:hover {
  cursor: pointer;
}
.searchresult tbody tr.odd:hover {
  background-color: #e0f0e0;
}
.searchresult tbody tr.even:hover {
  background-color: #d8e8d8;
}
.searchresult tbody tr.odd.readonly:hover {
  background-color: #f0e0e0;
}
.searchresult tbody tr.even.readonly:hover {
  background-color: #e8d8d8;
}

.searchresult.delete tbody tr.selected.odd {
 background-color: #ffffaa;
}
.searchresult.delete tbody tr.selected.even {
 background-color: #ffff00;
}

.searchresult tbody tr.selected.odd {
  background-color: #f0f0e0;
}
.searchresult tbody tr.selected.even {
  background-color: #e8e8d8;
}

.searchresult tbody tr.selected.odd:hover {
  background-color: #e0f0e0;
}
.searchresult tbody tr.selected.even:hover {
  background-color: #d8e8d8;
}

.searchresult tbody tr.selected.odd.readonly:hover {
  background-color: #f0e0e0;
}
.searchresult tbody tr.selected.even.readonly:hover {
  background-color: #e8d8d8;
}

.mm_related .searchable table tfoot.notneeded, 
.mm_related .searchable table thead.notneeded {
  display: none;
}


.failed {
 background-color: red;
}
.submitting {
 background-color: green;
}
.succeeded {
 background-color: yellow;
}

</mm:content>
