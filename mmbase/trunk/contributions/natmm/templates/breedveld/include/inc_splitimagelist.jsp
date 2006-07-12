<%
if(imageId.indexOf(",")>-1) {
   		thisImage = imageId.substring(0,imageId.indexOf(","));
		otherImages = imageId.substring(imageId.indexOf(",")+1);
		if(otherImages.indexOf(",")>-1) {
			nextImage = otherImages.substring(0,otherImages.indexOf(",")); 
			otherImages = otherImages.substring(otherImages.indexOf(",")+1);
			if(otherImages.indexOf(",")>-1) { // four or more images
				String tPreviousImage = otherImages.substring(otherImages.lastIndexOf(",")+1); 
				otherImages = otherImages.substring(0,otherImages.lastIndexOf(","));
				previousImage = tPreviousImage + "," + thisImage + "," + nextImage + "," + otherImages;
				nextImage = nextImage + "," + otherImages + "," + tPreviousImage + "," + thisImage;
			} else { // three images
				previousImage = otherImages + "," + thisImage + "," + nextImage;
				nextImage = nextImage + "," + otherImages + "," + thisImage;
			}
		} else { // two images
			nextImage = otherImages + "," + thisImage;
			previousImage = nextImage;
		}
} else { // one image
			thisImage = imageId;
}
%>