<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><mm:content type="text/css">

<mm:import id="myeditors">../../my_editors/img</mm:import>
@import url(base.css);

span.select:before   { content: url(<mm:url page="$myeditors/mmbase-right.png" />);  }
span.create:before   { content: url(<mm:url page="$myeditors/mmbase-new.png" />);    }
span.change:before   { content: url(<mm:url page="$myeditors/mmbase-edit.png" />);   }
span.delete:before   { content: url(<mm:url page="$myeditors/mmbase-delete.png" />); }
span.previous:before { content: url(<mm:url page="$myeditors/mmbase-left.png" />);   }
span.next:before     { content: url(<mm:url page="$myeditors/mmbase-right.png" />);  }
span.search:before   { content: url(<mm:url page="$myeditors/mmbase-search.png" />);  }
span.tree:before   { content: url(<mm:url page="$myeditors/mmbase-search.png" />);  }


body.basic { 
	margin: 0px;
	font-family: "Lucida Grande", Helvetica, sans-serif;
	font-size: 12pt;
  background-color: #EFEFEF;
}

body.basic:before {
	font-family: "Lucida Grande", Helvetica, sans-serif;
	font-size: 18px;
	font-weight: bold;
	color: #660000;
  width: 100%;
  content: url(<mm:url page="$myeditors/mmbase-edit-40.gif" />) "   jsp-editors";
}

body.navigationbar {
  background-color: #00425A;  
}

input.search:after  { content: url(<mm:url page="$myeditors/mmbase-search.png" />);  }

a:link
{
	color: #900;
	text-decoration: underline;
}

a:visited { color: #00C; }

a:hover
{
	color: #FF0000;
	text-decoration: none;
}

span {
  text-align: right;
}

span.alt {
  display: none;
}


table   { 
    background-color: #fff;
		width : 100%; 
}


td {
    color: black;
    border-width: 1px;		
}	

table.list td {
}
table.edit td {
}
table.edit td.navigate {

}
table.list td.navigate {
}
th {
   background-color: #ccc;
	 color: black;
   font-weight:bold
}

textarea.small {
     height: 6em;
}
textarea.big   {
	 height: 15em;
}

.data {
	font-weight: bold;
	color: #660033;
  text-align: right;
}
td.data:after {
  content: ":";
}
.navigate {
    text-align: right;
}

.edit {

     color: white;
}
.search {
     background-color: #efefef;
     color: black;
}

.linkdata {

     color: white;
     text-align: center
}

.listdata {
     background-color: #efefef;
     text-align: left
}

.multidata  {
     padding-top: 1em;
}

</mm:content>