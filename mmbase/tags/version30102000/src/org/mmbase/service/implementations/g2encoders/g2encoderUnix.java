/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
--------------------------------
How to Use the RealProducer Plus
--------------------------------

To begin encoding files with the RealProducer command
line, you need to become familiar with the RealProducer
Plus options.

Type the following command in a terminal window:

realproducer --Help

You will see the options displayed on your screen.

Usage: realproducer [ options ]
Example: realproducer -i foo.wav  -b "My American Journey" -o journey.rm
     Options: ( defaults in parentheses )

     -i    infile name             Input File
     -y    encode audio            1 - Encode audio
                                   0 - Do not encode audio
     -z    encode video            1 - Encode video
                                   0 - Do not encode video
                                       Note - by default RealProducer
                                       encodes all media streams present
     -o    outfile name            Output File              (output.rm)
     -s    server[:port]/resource  Server URL
     -u    username                Username for Live Connection
     -p    password                Server Password for Live Connection
     -l    audio device index      Live Audio Input Device / Port
                                           (e.g: 3:0)
     -n    video device index      Live Video Input Device / Port
                                           (e.g: 4:0)
     -t    target audience         Comma Delimited Target Audience 0-5 (0)
                                       0 - 28 Kbps Modems
                                       1 - 56 Kbps Modems
                                       2 - Single ISDN
                                       3 - Dual ISDN
                                       4 - Corporate LAN
                                       5 - LAN/T1 - High
     -a    audio format            Audio Format 0-3
                                       0 - Voice Only            (default)
                                       1 - Voice with Background Music
                                       2 - Music
                                       3 - Stereo Music
     -v    video quality           Video Quality 0-3
                                       0 - Normal Motion Video   (default)
                                       1 - Smoothest Motion Video
                                       2 - Sharpest Image Video
                                       3 - Slide Show
     -f    file type               Single Rate or SureStream 0-1 (1)
                                       0 - Single Rate
                                       0 - Single Rate
                                       1 - SureStream
     -b    title                   Clip Title
     -h    author                  Clip Author
     -c    copyright               Clip Copyright
     -k    boolean (0 or 1)        Enable Mobile Play       (0)
     -r    boolean (0 or 1)        Enable Selective Record  (0)
     -g    player version          Player Compatibility 5-6 (6)
                                       5 - RealPlayer 5.0 or Greater
                                       6 - RealPlayer G2
     -w    a/v emphasis            SureStream Emphasize Audio/Video 0-1
                                       0 - Emphasize Audio
                                       1 - Emphasize Video
     -m    config file             Target Audience Configuration File
     -j    left,top,width,height   Set Image Cropping       (0,0,0,0)
     -x    duration                Maximum Encoding Duration (hh:mm:ss)
     -e    <columns>x<rows>        Image Size to be Encoded (160x120)
                                       (columns must be a multiple of 4)
     --help                        Display This Message.
     --version                     Display Version.
     --force-overwrite             Force overwriting of existing output file.
     --temp-dir=dir                Set temporary directory to dir.

Capture devices:

Available devices formats: (9 total)
    0.  File format Audio File Reader   (extensions *.wav, *.au)
    1.  File format Quicktime File Reader       (extensions *.mov)
    2.  V4L1 Video capture plug-in (/dev/video0) Real Networks, Inc.
        Port 0: Television               (2:0)
        Port 1: Composite1               (2:1)
        Port 2: S-Video          (2:2)
    3.  V4L1 Video capture plug-in (/dev/video1) Real Networks, Inc.
        Port 0: Television               (3:0)
        Port 1: Composite1               (3:1)
        Port 2: S-Video          (3:2)
    4.  V4L1 Video capture plug-in (/dev/video2) Real Networks, Inc.
    5.  V4L1 Video capture plug-in (/dev/video3) Real Networks, Inc.
    6.  Linux/OSS audio capture plug-in (/dev/dsp) RealNetworks, Inc.
        Port 0: Microphone               (6:0)
        Port 1: Line In          (6:1)
        Port 2: CD               (6:2)
    7.  Linux/OSS audio capture plug-in (/dev/dsp1) RealNetworks, Inc.
        Port 0: Microphone               (7:0)
        Port 1: Line In          (7:1)
        Port 2: CD               (7:2)
    8.  X11 window capture plug-in. Real Networks, Inc.
        Port 0: Window capture           (8:0)
*/
package org.mmbase.service.implementations.g2encoders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.applet.*;

import org.mmbase.service.*;
import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;


/**
 */
public class g2encoderUnix implements g2encoderInterface {

	private	String 	classname 	= getClass().getName();
	private boolean	debug		= true;
	private void	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	String command = "/usr/bin/nice /usr/local/rprod/bin/realproducer ";

	public void startUp() {
	}

	public void shutDown() {
	}

	public String getVersion() {		
		try {
			Thread.sleep(10000);
		} catch(Exception e) {}
		return("12.0.0.1");
	}

	public String getCommand()
	{
		return null;
	}


 	 /**
	  * executes the given command
	  * @return standard output
	  */
	private String execute (String command) {
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;

		try {
			p = (Runtime.getRuntime()).exec(command,null);
			p.waitFor();
		} catch (Exception e) {
			s+=e.toString();
			return s;
		}
		dip = new DataInputStream(p.getInputStream());
        try {
            while ((tmp = dip.readLine()) != null) {
               	s+=tmp+"\n"; 
		   }
        } catch (Exception e) {
			//s+=e.toString();
			return s;
	   }

		DataInputStream dep = new DataInputStream(p.getErrorStream());
		try {
			while( (tmp=dep.readLine()) != null ) {
				s += tmp + "\n";	
			}	
		} catch( Exception e ) { 
			return s;
		}
		
	   return s;
	}



	/**
	 * cmds - String containing options
	 * 
 	 * Values are:
	 *
 	 * 	inputname		= "<name>"					- input filename
	 * 	outputname		= "<name>"					- output filename
	 *	encodeAudio		= "false/true"				- should audio be encoded
	 * 	encodeVideo		= "false/true"				- should video be encoded
	 * 	server			= "<name>:port/resource"	- name of server
	 *  username		= "<name>"					- username on server
	 *	password		= "<password>"				- password on server
	 *
	 *  ------------------------------------------------------------------------------------------------------------
	 *	audioDevice		= "<num:num>"				- start encoder by hand w/ help, encoder will tell the avail. ports
	 *	videoDevice 	= "<num:num>"				- same here
	 *  
	 * OR use 
	 * 
	 *	caputureAudioDevice = "audioFile, quicktimeFile, audiodevice[1..2] [microphone/linein/cd], or windowCapture"
	 *	captureVideoDevice  = "quicktimeFile, videodevice[1..4] [television/composite1/s-video], or windowCapture"
	 *  ------------------------------------------------------------------------------------------------------------
	 * 
	 *	targetAudience	= "28k,56k,singleISDN,dualISDN,lan,cable" (comma-sep if sureStream)
	 *	audioFormat		= "0..3"
	 *	videoFormat		= "0..3"
	 *	sureStream		= "false/true"			- singleRate or SureStream
	 *	title			= "<name>"
	 *	author			= "<name>"
	 * 	copyright		= "<name>"
	 *	enableMobile	= "false/true"
	 * 	enableRecord	= "false/true"
	 *	playerVersion	= "[5/6]"
	 *	emphasize		= "false/true"
	 * 	cropping		= "xt,yt,xd,yd"
	 *	duration		= "hh:mm:ss.m"
	 *	imageSize		= "<num>x<num>"
	 *	forceOverwrite	= "false/true"
	 */

	public String doEncode( String cmds )
	{
		String result = "";

		//cmds+=" targetAudience=28k,56k,singleISDN,dualISDN,cable";
		debug("doEncode("+cmds+")");

		StringTagger tagger = new StringTagger( cmds );

		if (tagger.containsKey("inputname")) 		result += "-i "+ 		tagger.Value("inputname") 		+" ";
		if (tagger.containsKey("outputname")) 		result += "-o "+ 		tagger.Value("outputname") 		+" ";
		if (tagger.containsKey("encodeAudio"))		result += "-y "+ 	c(	tagger.Value("encodeAudio")) 	+" ";
		if (tagger.containsKey("encodeVideo")) 		result += "-z "+ 	c(	tagger.Value("encodeVideo"))	+" ";
		if (tagger.containsKey("server"))			result += "-s "+		tagger.Value("server")			+" ";
		if (tagger.containsKey("username"))			result += "-u "+		tagger.Value("username")		+" ";
		if (tagger.containsKey("password"))			result += "-p "+		tagger.Value("password")		+" ";

		if (tagger.containsKey("audioDevice"))		result += "-l "+		tagger.Value("audioDevice")		+" ";
		if (tagger.containsKey("videoDevice"))		result += "-n "+		tagger.Value("videoDevice")		+" ";
		if (tagger.containsKey("captureAudioDevice"))result += "-l "+ captureDevice( tagger.Value("captureAudioDevice")) + " " ;
		if (tagger.containsKey("captureVideoDevice"))result += "-n "+ captureDevice( tagger.Value("captureAudioDevice")) + " " ;


		if (tagger.containsKey("targetAudience"))	result += "-t "+	t(	tagger.Value("targetAudience"))	+" ";
		if (tagger.containsKey("audioFormat"))		result += "-a "+audioFormat(tagger.Value("audioFormat"))+" ";
		if (tagger.containsKey("videoFormat"))		result += "-v "+videoFormat(tagger.Value("videoFormat"))+" ";
		if (tagger.containsKey("sureStream"))		result += "-f "+	c(	tagger.Value("sureStream"))		+" ";
		if (tagger.containsKey("title"))			result += "-t \""+		tagger.Value("title")			+"\" ";
		if (tagger.containsKey("author"))			result += "-h \""+		tagger.Value("author")			+"\" ";
		if (tagger.containsKey("copyright"))		result += "-c "+		tagger.Value("copyright")		+"\" ";
		if (tagger.containsKey("enableMobile"))		result += "-k "+	c(	tagger.Value("enableMobile"))	+" ";
		if (tagger.containsKey("enableRecord"))		result += "-r "+	c(	tagger.Value("enableRecord"))	+" ";
		if (tagger.containsKey("playerVersion"))	result += "-g "+		tagger.Value("playerVersion")	+" ";
		if (tagger.containsKey("emphasize"))		result += "-w "+	c(	tagger.Value("emphasize"))		+" ";
		if (tagger.containsKey("cropping"))			result += "-j "+		tagger.Value("cropping")		+" ";
		if (tagger.containsKey("duration"))			result += "-x "+		tagger.Value("duration")		+" ";
		if (tagger.containsKey("imageSize"))		result += "-e "+		tagger.Value("imageSize")		+" ";
		if (tagger.containsKey("forceOverwrite"))	result += "--force-overwrite ";

		

		result+=" -t 2,3,4";
		debug("doEncode(): exec("+command+result+")");
		result=execute(command+result);
		debug("doEncode(): encoding done, result("+result+")");
		//return result;
		return("oke");
	}

	private	String c( String bool )
	{
		if(bool.equalsIgnoreCase("true"))
			return "1";
		else
			return "0";
	} 

	private String t( String params )
	{
		String result = "";
		StringTokenizer tok = new StringTokenizer( params );

		while( tok.hasMoreTokens() ) {
			if( result.equals("")) {
				result = toNum( tok.nextToken() );
			} else {
				result += "," + toNum( tok.nextToken() );
			}
		}

		return result;
	}
	

	/**
 	 * @params 28k, 56k, singleISDN, dualISDN, lan, cable - implemented with ignoreCase
	 */
	private String toNum( String params )
	{
		String result;

		debug("tonum="+params);
		if (params != null)
		{
			if( !params.equals(""))
			{
				if(	params.equalsIgnoreCase("28k"))
					result = "0";
				else
				if( params.equalsIgnoreCase("56k"))
					result = "1";
				else
				if( params.equalsIgnoreCase("singleISDN"))
					result = "2";
				else
				if( params.equalsIgnoreCase("dualISDN"))
					result = "3";
				else
				if( params.equalsIgnoreCase("lan"))
					result = "4";
				else
				if( params.equalsIgnoreCase("cable"))
					result = "5";
				else
				{
					// signal error and return lowest quality 
					// --------------------------------------
		
					debug("toNum("+params+"): ERROR: This is not a valid option! - Valid options are: 28k,56k,singleISDN,dualISDN,lan and cable.");
					result = "0";
				}
			}
			else
			{
				// signal error and return lowest quality
                // --------------------------------------
	
				debug("toNum("+params+"): ERROR: params is empty ! - Valid options are: 28k,56k,singleISDN,dualISDN,lan and cable.");
				result = "0";
			}
		}
		else
		{
			// signal error and return lowest quality
            // --------------------------------------
			debug("toNum("+params+"): ERROR: params is null! - Valid options are: 28k,56k,singleISDN,dualISDN,lan and cable.");
			result = "0";
		}
		return result;
	}

	private String audioFormat( String format ) 
	{
		String result = null;
		debug("FORMAT="+format);

		if( format != null )
		{
			if( !format.equals("") )
			{
				if( format.equalsIgnoreCase("voice") )
				{
					result = "0";
				}
				else
				if( format.equalsIgnoreCase("voice with music") )
				{
					result = "1";
				}
				else
				if( format.equalsIgnoreCase("mono music") )
				{
					result = "2";
				}
				else
				if( format.equalsIgnoreCase("stereo music") )
				{
					result = "3";
				}
				else
				{
					debug("audioFormat("+format+"): ERROR: parameter format is not legal! Valid options are: voice, voice with music, mono music, stereo music.");
					result = "0";
				}
			}
			else
			{
				debug("audioFormat("+format+"): ERROR: parameter format is empty! Valid options are: voice, voice with music, mono music, stereo music.");
				result = "0";
			}
		}
		else
		{
			debug("audioFormat("+format+"): ERROR: parameter format is null! Valid options are: voice, voice with music, mono music, stereo music.");
			result = "0";
		}

		return result;
	}

	private String videoFormat( String format )
	{
		String result;
		if ( format != null )
		{
			if ( !format.equals("") )
			{
				if( format.equalsIgnoreCase("normal") )
				{
					result = "0";
				}
				else
				if( format.equalsIgnoreCase("smooth") )
				{
					result = "1";
				}
				else
				if( format.equalsIgnoreCase("sharp") )
				{
					result = "2";
				}
				else
				if( format.equalsIgnoreCase("slideshow") )
				{
					result = "3";
				}
				else
				{
					debug("videoFormat("+format+"): ERROR: parameter format is not legal! Valid options are: normal, smooth, sharp, slideshow.");
					result = "0";
				}	
			}
			else
			{
				debug("videoFormat("+format+"): ERROR: parameter format is empty! Valid options are: normal, smooth, sharp, slideshow.");
				result = "0";	
			}
		}
		else
		{
			debug("videoFormat("+format+"): ERROR: parameter format is null! Valid options are: normal, smooth, sharp, slideshow.");
			result = "0";
		}

		return result;
	}


	/**
	 * @param device 
	 * 	Valid options are : 
	 * 		- audioFile, 
	 * 		- quicktimeFile,
	 * 		- videoCapture[1..4] with device
	 *			+ television
	 *			+ composite1 or
	 *			+ s-video
	 *		- audioCapture[1..2] with device
	 *			+ microphone
	 *			+ linein or
	 * 			+ cd
	 */

	private String captureDevice( String device )
	{
		String result;

		device = device.trim();

		if( device != null )
		{
			if( !device.equals( "" ) )
			{
				if( device.equalsIgnoreCase( "audioFile" ))
				{
					result = "0:0";
				}
				else
				if( device.equalsIgnoreCase( "quicktimeFile" ))
				{
					result = "1:0";
				}
				else
				if( device.toLowerCase().startsWith( "videodevice" ))
				{
					// -------------------------------------------------
					// videoCapture[1..4] [television/compsite1/s-video]
					// -------------------------------------------------

					String inputdevice = device.substring( 11 );
					String sdeviceNr = inputdevice.substring( 0,1 );
					try
					{
						int deviceNr = Integer.parseInt( sdeviceNr );
						if( deviceNr > 0 && deviceNr < 5 ) 	// 1 .. 4 are valid numbers 
						{
							deviceNr = deviceNr + 1; 		// but rprod wants numbers from 2..5 
							String format = inputdevice.substring( 2 ).trim();
						
							if( format.equalsIgnoreCase( "television" ))
							{
								result = "" + deviceNr + ":0";
							}
							else	
							if( format.equalsIgnoreCase( "composite1" ))
							{
								result = "" + deviceNr + ":1";
							}
							else	
							if( format.equalsIgnoreCase( "s-video" ))
							{
								result = "" + deviceNr + ":2";
							}
							else	
							{
								debug( "captureDevice("+device+"): ERROR: No valid format("+format+") specified for videoCapture("+(deviceNr-1)+")! (Valid options are televsion/composite1/s-video).");
								result = "" + deviceNr + ":0";
							}
						}
						else
						{
							debug( "captureDevice("+device+"): ERROR: videoCapture has to be between 1..4 (but specified number("+deviceNr+"). Using standard 2:0 (/dev/video0 - television) as device.");
							result = "2:0";
						}
					}
					catch( NumberFormatException e )
					{
						debug( "captureDevice("+device+"): ERROR: While determining video deviceNumber: This is NOT a number("+sdeviceNr+")! Using standard 2:0 (/dev/video0 - television) as device.");
						result = "2:0";
					}
				}
				else
				if( device.toLowerCase().startsWith( "audiodevice" ))
				{
					// -----------------------------------------
					// audioDevice[1..2] [microphone/linein/cd]
					// -----------------------------------------

					String inputdevice = device.substring( 11 );
					String sdeviceNr = inputdevice.substring( 0,1 );	
					try
					{
						int deviceNr = Integer.parseInt( sdeviceNr );
						if( deviceNr > 0 && deviceNr < 3 )		// check between 1..2
						{
							deviceNr = deviceNr + 5;			// but rprod wants 6..7
							String inputDevice = inputdevice.substring( 2 ).trim();
							
							if( inputDevice.equalsIgnoreCase( "microphone" ))
							{
								result = "" + deviceNr + ":0";
							}
							else
							if( inputDevice.equalsIgnoreCase( "linein" ))
							{
								result = "" + deviceNr + ":1";
							}
							else
							if( inputDevice.equalsIgnoreCase( "cd" ))
							{
								result = "" + deviceNr + ":2";
							}
							else
							{
								debug( "captureDevice("+device+"): ERROR: Found intputDevice("+inputDevice+") is not a valid inputdevice! Using standard device( audio0/microphone )." );	
								result = "" + deviceNr + ":0";
							}
						}
						else
						{
							debug( "captureDevice("+device+"): ERROR: Found inputdeviceNr("+deviceNr+") not in range(1..2)! Using standard device( audio0/microphone ) ");
							result = "6:0";
						}
					}
					catch( NumberFormatException e )
					{	
						debug("captureDevice("+device+"): ERROR: While determining audio deviceNumber: This is NOT a number("+sdeviceNr+")! Using standard device( audio0/microphone ).");
						result = "6:0";
					}
				}
				else
				if( device.toLowerCase().startsWith( "windowCapture" ))
				{
					result = "8:0";
				}
				else
				{
					debug("captureDevice("+device+"): ERROR: Found device("+device+") is not a valid device! Valid devices are audioFile, quicktimeFile, videoDevice[1..4], audioDevice[1..2] or windowCapture. Returning device 0:0.");
					result = "0:0";
				}
			}
			else
			{
				debug( "captureDevice("+device+"): ERROR: captureDevice is empty! Returning device 0:0.");
				result = "0:0";
			}
		}
		else
		{
			debug( "captureDevice("+device+"): ERROR: captureDevice is null! Returning device 0:0. ");
			result = "0:0";
		}

		return result;
	}

	public static void main( String args[] )
	{
		String params = "";
		
		params += "inputname=\"boe.mpg\", ";
		params += "outputname=\"boe.rm\", ";
		params += "encodeAudio=true, ";
		params += "encodeVideo=true, ";
		params += "captureAudioDevice=audioDevice1 linein, ";
		params += "targetAudience=28k,56k,cable, ";
		params += "audioFormat=stereo music, ";
		params += "videoFormat=smooth, ";
		params += "sureStream=true, ";
		params += "title=\"Boe the sequel, part 1\", ";
		params += "author=\"Boe 'boe' Boe\", ";
		params += "copyright=\"Boe inc.\", ";
		params += "enableMobile=false, ";
		params += "enableRecord=false, ";
		params += "playerVersion=6, ";
		params += "emphasize=true, ";
		params += "duration=60:00:0.0, ";
		params += "imageSize=1280x1024";

		g2encoderUnix g2 = new g2encoderUnix();
		g2.debug( g2.doEncode ( params ) );
	}

	public boolean checkstring( String method, String varname, String tocheck )
	{
		boolean result = false;	

				if( method == null 	  ) debug("checkstring("+method+","+varname+","+tocheck+"): ERROR: method("+method+") is null!");
		else	if( method.equals("") ) debug("checkstring("+method+","+varname+","+tocheck+"): ERROR: method("+method+") is empty!");
		else	if( varname == null	  ) debug("checkstring("+method+","+varname+","+tocheck+"): ERROR: varname("+varname+") is null!");
		else	if( varname.equals("")) debug("checkstring("+method+","+varname+","+tocheck+"): ERROR: varname("+varname+") is null!");
		else	if( tocheck == null   ) debug( method+"(): "+varname+"("+tocheck+") is null!");
		else	if( tocheck.equals("")) debug( method+"(): "+varname+"("+tocheck+") is empty!");
		else 	result = true;

		return result;
	}
}
