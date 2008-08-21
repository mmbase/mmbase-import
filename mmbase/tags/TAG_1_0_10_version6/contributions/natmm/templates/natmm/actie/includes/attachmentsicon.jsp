<%
	int indexOfPoint = dummy.lastIndexOf('.');
	if (indexOfPoint != -1) {
		String extension = dummy.substring(indexOfPoint);
		if (extension.indexOf("xls")!=-1) {
			imgName = "media/icexcel.gif";
			docType = "Excel bestand";
		} else if (extension.indexOf("pdf")!=-1) {
			imgName = "media/icpdf.gif";
			docType = "PDF file";
		} else if (extension.indexOf("ppt")!=-1) {
			imgName = "media/icppt.gif";
			docType = "Powerpoint bestand";
		} else if (extension.indexOf("doc")!=-1) {
			imgName = "media/icword.gif";
			docType = "Word bestand";
		} else {
			imgName = "media/ictxt.gif";
			docType = "Tekst file";
		}
	}
%>