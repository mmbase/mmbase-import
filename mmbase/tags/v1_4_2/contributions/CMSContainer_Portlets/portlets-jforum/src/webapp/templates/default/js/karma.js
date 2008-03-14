<#if karmaEnabled>
	function karmaVote(s, postId) {
		if (s.selectedIndex == 0) {
			return;
		}

		if (confirm("${I18n.getMessage("Karma.confirmVote")}")) {
			document.location = "${contextPath}/karma/insert/${start}/" + postId + "/" + s.value + "${extension}";
		}
		else {
			s.selectedIndex = 0;
		}
	}

	function karmaPointsCombo(postId)
	{
		document.write('<select name="karma" onchange="karmaVote(this,' + postId + ')">');
		document.write('	<option value="">${I18n.getMessage("Karma.rateMessage")}</option>');
		document.write('	<option value="1">1</option>');
		document.write('	<option value="2">2</option>');
		document.write('	<option value="3">3</option>');
		document.write('	<option value="4">4</option>');
		document.write('	<option value="5">5</option>');
		document.write('</select>');
	}
</#if>