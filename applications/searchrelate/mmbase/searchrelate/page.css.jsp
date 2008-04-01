// -*- css -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content  expires="0" type="text/css">

.searchresult {
  border: solid 1px #000;
}
.searchresult thead tr,
.searchresult tfoot tr {
  background-color: #ffb;
}

.searchresult tbody tr.even {
  background-color: #ddd;
}
.searchresult tbody tr.odd {
  background-color: #fff;
}
.searchresult tbody tr:hover {
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
