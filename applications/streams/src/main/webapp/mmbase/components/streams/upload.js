$(document).ready(function() {
	var progressUrl = $("head meta[name=ContextRoot]").attr("content") + "mmbase/components/streams/upload.progress.jspx";
	$("form.mm_form").each(
	    function() {
		var pInfo = $(this).find(".progressInfo").first();
		$(this).submit(
		    function() {
			var form = this;
			var result = "<div>Uploading...</div>";
			$(pInfo).html(result);
			var i = 0;
			var progress = null;
			clearInterval(progress);
			progress = setInterval(
			    function() {
				$.ajax(
				    { url: progressUrl,
				      async: false,
				      cache: false,
				      error: function(xhr, status, err) {
					  result = '<div>Error: ' + status + " : " + err + '</div>';
				      },
				      complete: function(data) {
					  result = data.responseText;
					  //console.log('complete');
					  if (result.indexOf('100%') > -1 && i == 0) {
					      result = "<div>Uploading...</div>";
					  }
					  i++;
				      },
				      success: function(data) {
					  result = data.responseText;
					  //console.log('success');
				      }
				    });
				$(pInfo).html(result);
				//console.log('uploading: ' + i);
			    }, 1000);
			    return true;
		    });
	    });

	// Just some stuff to guess a nicer title
	$("input[type=file]").change(function(ev) {
		var file = ev.target.value;
		var title = $(ev.target.form).find(".mm_validate.mm_f_title")[0];
		if (title.originalValue == $(title).val()) {
		    var li = file.lastIndexOf('\\');
		    if (li > 0) {
			file = file.substring(li + 1);
		    }
		    li = file.lastIndexOf('.');
		    if (li > 0) {
			file = file.substring(0, li);
		    }
		    if (file.length >= 3) {
			$(title).val(file);
		    }
		}
	    });
    });
