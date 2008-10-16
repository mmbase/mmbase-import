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
		// count (1) number of images between offsetIDand thisImage and (2) the total number of images
		int comma_pos = imageId.indexOf(",",0);
		while(comma_pos>-1) {
			totalNumberOfImages++;
			if(imageId.indexOf(offsetID,comma_pos+1)==-1) {
				thisImageNumber++;
			}
			comma_pos = imageId.indexOf(",",comma_pos+1);
		}
		if(thisImageNumber==totalNumberOfImages){
			thisImageNumber = 0;
		}
		thisImageNumber++;
} else { // one image
		thisImage = imageId;
}%>
