package nl.vpro.dvt.communities.domain;

//mmvpro-1.7.4/src/nl/vpro/mmbase/preditor/optionlists/mediasources_format.properties

public enum MediaFragmentFormat { 
	UNKNOWN(-1),
	
	MP3(1), 			// mp3
	REALAUDIO(2), 		// Real Audio (ra)
	WAV(3),				// wav
						// 
	MP2(5),				// mp2
	REALMEDIA(6),		// Real Media (rm)
						// 
	AVI(8),				// Avi
	MPEG(9), 			// Mpeg
	MP4(10),			// mp4
	MPG(11),			// mpg
	WINDOWSMEDIA(12),	// Windows media
	MOV(13),			// QuickTime (mov)
						// 
	OGG(15),			// Ogg
	OGM(16),			// Ogm
	RAM(17),			// Real Media (ram)
	WMP(18),			// Windows Media (wmp)
						// 
	SMIL(20),			// Synchronized Multimedia (SMIL)
	QUICKTIME(21),		//
	 
	M4A(60),			//	
	M4V(61),			//
	
	DRIEGPP(70);		// 

	int index;

	private MediaFragmentFormat(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static MediaFragmentFormat getByIndex(int index){
		MediaFragmentFormat[] classes = MediaFragmentFormat.values();
		for(MediaFragmentFormat clazz : classes){
			if(clazz.getIndex() == index){
				return clazz;
			}
		}
		return null;
	}
	
	public String toClassString() {
		return
			(this == MediaFragmentFormat.UNKNOWN ? 		"(onbekend)" : "") + 
			
			(this == MediaFragmentFormat.MP3 ? 			"Mp3 (webradio)" : "") + 
			(this == MediaFragmentFormat.REALAUDIO ? 	"Realaudio (ra)" : "") +
			(this == MediaFragmentFormat.WAV ? 			"Wav" : "") +
			
			(this == MediaFragmentFormat.MP2 ? 			"Mp2" : "") +
			(this == MediaFragmentFormat.REALMEDIA ? 	"Realmedia (rm)" : "") +
			
			(this == MediaFragmentFormat.AVI ? 			"Avi" : "") +
			(this == MediaFragmentFormat.MPEG ? 		"Mpeg" : "") +
			(this == MediaFragmentFormat.MP4 ? 			"Mp4" : "") +
			(this == MediaFragmentFormat.MPG ? 			"mpg" : "") +
			(this == MediaFragmentFormat.WINDOWSMEDIA ?	"Windows Media (wm)" : "") +
			(this == MediaFragmentFormat.MOV ? 			"Mov" : "") +
			
			(this == MediaFragmentFormat.OGG ? 			"Ogg" : "") +
			(this == MediaFragmentFormat.OGM ? 			"Ogm" : "") +
			(this == MediaFragmentFormat.RAM ? 			"Ram" : "") +
			(this == MediaFragmentFormat.WMP ? 			"Wmp" : "") +
			
			(this == MediaFragmentFormat.SMIL ? 		"Smil" : "") +
			(this == MediaFragmentFormat.QUICKTIME ? 	"Quicktime" : "") +
			(this == MediaFragmentFormat.M4A ? 			"Podcast (m4a)" : "") +
			(this == MediaFragmentFormat.M4V ? 			"Vodcast (m4v)" : "") +
			
			(this == MediaFragmentFormat.DRIEGPP ? 		"3GPP" : "");
	}
	
}