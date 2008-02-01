var panelOpen = false;
var total = 0;
var ignoreStart = false;
var maxAttachments = ${maxAttachments?default(0)};
var counter = 0;

<#if attachmentsEnabled>
	var template = "<table width='100%'><tr><td><span class='gen'><b>${I18n.getMessage("Attachments.filename")}</b></span></td>";
	template += "<td><input type='file' size='50' name='file_#counter#'></td></tr>";
	template += "<tr><td><span class='gen'><b>${I18n.getMessage("Attachments.description")}</b></span></td>";
	template += "<td><textarea rows='4' cols='40' name='comment_#counter#'></textarea>";
	template += "&nbsp;&nbsp;<a href='javascript:removeAttach(#counter#)' class='gensmall'>${I18n.getMessage("Attachments.remove")}</a></td></tr>";
	template += "<tr><td colspan='2' width='100%' class='row3'></td></tr></table>";

	function openAttachmentPanel() 
	{
		if (total == 0 && !ignoreStart) {
			addAttachmentFields();
		}

		document.getElementById("tdAttachPanel").style.display = panelOpen ? 'none' : '';
		panelOpen = !panelOpen;
	}

	function addAttachmentFields()
	{
		if (counter < maxAttachments) {
			var s = template.replace(/#counter#/g, total);
			s += "<div id='attach_#counter#'></div>";
			s = s.replace(/#counter#/, total + 1);

			
			document.getElementById("attach_" + total).innerHTML = s;
			document.getElementById("total_files").value = ++total;

			counter++;
			setAddAttachButtonStatus();
		}
	}

	function removeAttach(index)
	{
		document.getElementById("attach_" + index).innerHTML = "<div id='attach_" + total + "'></div>";
		//Avoid HTML Validation error by not using the minusminus shortcut
		counter = counter-1;
		setAddAttachButtonStatus();
	}

	function setAddAttachButtonStatus()
	{
		var disabled = !(counter < maxAttachments);
		document.post.add_attach.disabled = disabled;
		document.post.add_attach.style.color = disabled ? "#cccccc" : "#000000";
	}
</#if>

<#if attachments?exists>
	var templateEdit = "<table width='100%'><tr><td class='row2'><span class='gen'><b>${I18n.getMessage("Attachments.filename")}</b></span></td>";
	templateEdit += "<td class='row2'><span class='gen'>#name#</td></tr>";
	templateEdit += "<tr><td class='row2'><span class='gen'><b>${I18n.getMessage("Attachments.description")}</b></span></td>";
	templateEdit += "<td class='row2'><textarea rows='4' cols='40' name='edit_comment_#id#'>#value#</textarea>";
	templateEdit += "&nbsp;&nbsp;<span class='gensmall'><input type='checkbox' onclick='configureAttachDeletion(#id#, this);'>${I18n.getMessage("Attachments.remove")}</span></td></tr>";
	templateEdit += "<tr><td colspan='2' width='100%' class='row3'></td></tr></table>";
	
	function editAttachments()
	{
		var data = new Array();
		<#list attachments as a>
			var attach_${a.id} = new Array();

			attach_${a.id}["filename"] = "${a.info.realFilename}";
			attach_${a.id}["description"] = "${a.info.comment}";
			attach_${a.id}["id"] = "${a.id}";

			data.push(attach_${a.id});
		</#list>

		counter = data.length;
		<#if attachmentsEnabled>setAddAttachButtonStatus();</#if>
		
		for (var i = 0; i < data.length; i++) {
			var a = data[i];
			var s = templateEdit.replace(/#value#/, a["description"]);
			s = s.replace(/#name#/, a["filename"]);
			s = s.replace(/#id#/g, a["id"]);

			var v = document.getElementById("edit_attach").innerHTML;
			v += s;
			document.getElementById("edit_attach").innerHTML = v;
			document.post.edit_attach_ids.value += a["id"] + ",";
		}
	}

	function configureAttachDeletion(id, f)
	{
		if (f.checked) {
			document.post.delete_attach.value += id + ",";
		}
		else {
			var p = document.post.delete_attach.value.split(",");
			document.post.delete_attach.value = "";
			for (var i = 0; i < p.length; i++) {
				if (p[i] != id) {
					document.post.delete_attach.value += p[i] + ",";
				}
			}
		}
	}
</#if>