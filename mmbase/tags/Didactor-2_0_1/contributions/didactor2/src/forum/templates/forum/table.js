// This javascript will create a dynamic sortable list
var headers = new Array();
function addHeader(name, width, align, valign) {
   if(typeof(valign) == "undefined")
      valign = "middle";
	headers[headers.length] = new Array(name, width, align, valign);
}

var data = new Array();
function addLine(fields, onclick) {
	data[data.length] = fields;
	fields[fields.length] = onclick;
}

var buildedRowBuffer = false;
function buildRowBuffer() {
   rowBuffer = new Array();
   var tdDef = new Array();
   for(var element = 0; element < headers.length; element++) {
      tdDef[element] = '<td class="listItem" align="'+headers[element][2]+'" valign="'+headers[element][3]+'">';
   }

	for(var line = 0; line < data.length; line++) {
      var buffer = '';
      if(typeof(data[line][headers.length]) != "undefined")
         buffer += ' onclick="'+data[line][headers.length]+'" style="cursor:pointer;">';
      else
         buffer += '>';

		for(var element = 0; element < headers.length; element++) {
			buffer += tdDef[element] + data[line][element] + '</td>';
		}
		buffer += '</tr>';
      data[line][data[line].length] = buffer;
   }
}

function buildTable(disableSort) {
	var table = '<form name="tableForm"><table cellspacing=0 width="100%" class="listTable">';
	table += '<tr>';
	for(var element = 0; element < headers.length; element++) {
		table += '<td width='+headers[element][1]+' class="listHeader">';
		if(sortCol == element && !disableSort) {
    		table += '<table cellspacing=0><tr><td>';
      }

      if(headers[element][1].indexOf('%') > 0 && !disableSort)
    		table += '<a href="javascript:sort('+element+')" class="listHeader">';
      table += headers[element][0];
      if(headers[element][1].indexOf('%') > 0 && !disableSort)
    		table += '</a>'

		if(sortCol == element && !disableSort) {
      table += '</td><td>&nbsp;&nbsp;<img src="/providers/educations/courses/gfx/';
			if(sortDir == 0)
				table += 'sort_down.gif';
			else
				table += 'sort_up.gif';
      table += '" width=7 height=4></td></tr></table>';
		}

		table += '</td>';
	}
	table += '</tr>';

	var bufferPos = headers.length+1;
	for(var line = 0; line < data.length; line++) {
    table += '<tr '+data[line][bufferPos];
	}
	table += '</form></table>';
	document.getElementById('tableDiv').innerHTML = table;
}

var sortDir = 0;
var sortCol = -1;
function sort(col, dir, disableSort) {

   var firstTime = !buildedRowBuffer
   if (firstTime) {
      buildRowBuffer();
      buildedRowBuffer = true;
   }

   if (!firstTime && typeof(beforeSort) != "undefined")
      beforeSort();

	if (sortCol != col)
		sortDir = 0;
	else
		sortDir = (sortDir + 1) % 2;
	sortCol = col;

   if (typeof(dir) != "undefined")
	   sortDir = dir;

	var sortingDone = false;
	while (!sortingDone) {
		sortingDone = true;
		for(var count = 0; count < data.length-1; count++) {
			if( (sortDir == 0 && data[count][col] > data[count+1][col]) ||
					(sortDir == 1 && data[count][col] < data[count+1][col]) ) {

				var swap = data[count];
				data[count] = data[count+1];
				data[count+1] = swap;

				sortingDone = false;
			}
		}
	}
	buildTable(disableSort);

   if (!firstTime && typeof(afterSort) != "undefined")
      afterSort();
}

function donotsort(col, dir) {
  sort(col,dir,true);
}