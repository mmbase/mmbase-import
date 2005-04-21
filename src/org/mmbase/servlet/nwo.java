/**
 * File 		: nwo.java
 * Date			: $Date: 2000-02-04 11:59:23 $
 * Description 	: Read, check, evaluate and mail form for the 'nationale wetenschapsquiz'.  
 *
 * Pages are at : 
 * 		http://www.vpro.nl/data/nationale-wetenschapsquiz/junior1998/vragen2.shtml and 
 * 		http://www.vpro.nl/data/nationale-wetenschapsquiz/
 *
 *		and files are in /export/home/wwwtech/nwo/[junior/senior]
 *
 * Update (marmaa) : 
 * 		- made the files with answers little more flexible 
 * 		  (will work even if number of questions not specified)
 *		- extensive logging
 * 		- used methods to implement different functions
 *		- will check if name exists in form and only mail it when it exists
 * 		- Updated it for using it with multiple answersfiles (junior and senior questions)
 *		- and plugged in some documentation ...
 *  
 * @author 	$Author: wwwtech $ 
 * @version $Revision: 1.1.1.1 $
 *
 * $Log: not supported by cvs2svn $
 * Revision 2.1  1999/12/02 14:51:00  wwwtech
 * - Changed email to marmaa for testing.
 *
 * Revision 2.0  1999/12/02 14:14:57  wwwtech
 * - This version is compilable under Orion
 *
 * Revision 1.5  1999/12/02 14:12:02  wwwtech
 * - Added Mail in import
 * - Added SendMail in import
 * - Extended from JamesServlet
 * - Removed writeheaders from HttpServletResponse
 * - Added method debug
 * - Inserted PrintStream in try{} catch(IOExceptio)
 *
 * Revision 1.4  1999/12/02 12:15:53  wwwtech
 * - removed packages from servlet
 *
 * Revision 1.3  1999/12/02 10:56:08  wwwtech
 * - Added extra comment tags.
 * - Added VPRO in description.
 *
 * Revision 1.2  1999/12/02 10:51:29  wwwtech
 * - Added extra tags for documetation.
 * - Commented the package tag.
 * 
 */

// package it belongs to only needed for javadoc
// ---------------------------------------------

//package vpro.james.servlets;
package org.mmbase.servlet;

// import the needed packages
// --------------------------
// import vpro.james.coreserver.*;
// import vpro.james.coremodules.*;
// import vpro.james.modules.*;
// import vpro.james.util.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.mmbase.servlet.JamesServlet;
import org.mmbase.util.HttpPost;
import org.mmbase.util.Mail;
import org.mmbase.module.SendMail;
import org.mmbase.module.SendMailInterface;

import java.io.*;
import java.util.*;

/**
 * 	Post Servlet a example of how to use different Post methods
 */

public class nwo extends JamesServlet 
{
	private String classname = getClass().getName();
	
	int aantalVragenJunior   = 10;
	int aantalVragenSenior 	 = 20;

	String antJuniorFilename = "/export/home/wwwtech/nwo/junior/nwoJunior.ant";
	String juniorFileName	 = "/export/home/wwwtech/nwo/junior/nwoJunior.log";
	String antSeniorFilename = "/export/home/wwwtech/nwo/senior/nwoSenior.ant";
	String seniorFileName	 = "/export/home/wwwtech/nwo/senior/nwoSenior.log";

	//String ant[] 			 = //new String[aantalVragen];
	String antJunior[] 		 = new String[aantalVragenJunior];
	String antSenior[] 	 	 = new String[aantalVragenSenior];

	String sendersEmail	 	 = "nwo@vpro.nl";
	String toEmail     	 	 = "nwo@vpro.nl";

	SendMailInterface sendmail;
	boolean first=true;
	
	public void init() 
	{
		sendmail=(SendMailInterface)getModule("sendmail");
	}

	/** 
	 * reload
	 */
	public void reload() 
	{
		sendmail=(SendMailInterface)getModule("sendmail");
	}

	/**
 	* service call will be called by the server when a request is done
	* by a user.
 	*/
	public synchronized void service(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException,IOException 
	{	

		boolean couldSend = false;
		String verzend = ""; 

		//Inlezen van antwoorden
		if (first) 
		{
			System.out.println("reading answers");
			antJunior = readAnswers(antJuniorFilename, "junior");
			antSenior = readAnswers(antSeniorFilename, "senior");
			first=false;
		}

		// Get name, form and emailaddie

		HttpPost hp = new HttpPost( req );

		String usersName  = hp.getPostParameter("name");
		String entryForm  = hp.getPostParameter("entry");
		String usersEmail = hp.getPostParameter("email");
	
		if (usersName == null || usersName.equals(""))
		{
			System.out.println("Nwo_Servlet: User has not specified a name for his entry!");
			displayErrorUsername(res);
		}

		else
		{
			if (usersEmail != null && !usersEmail.equals("")) 
				sendersEmail = usersEmail;

			if (entryForm == null || entryForm.equals(""))
			{
				System.out.println("Nwo_Servlet: User ["+usersName+", "+usersEmail+"] had no 'ENTRY' in form ...");
				System.out.println("Nwo_Servlet: Could not determine what questions user has answered. ");
				displayErrorEntry(res);
			} 
			else 
			{
				//
				// specify in form : <INPUT TYPE="hidden" NAME="entry" VALUE="JUNIOR"> or
				// ---------------   <INPUT TYPE="hidden" NAME="entry" VALUE="SENIOR">
				//

				String whatForm;

				String ant[];
				int aantalVragen = -1;

				if (entryForm.toUpperCase().equals("JUNIOR"))
				{
					ant      	 = antJunior;
					aantalVragen = aantalVragenJunior;
					whatForm 	 = "junior";
				}
				else
					if (entryForm.toUpperCase().equals("SENIOR"))
					{
						ant 	 	 = antSenior;
						aantalVragen = aantalVragenSenior;
						whatForm 	 = "senior";

					}
					else
					{
						System.out.println("Nwo_servlet: ERROR: ENTRY not specified in form");	
						ant = null;
						whatForm = null;
					}
				
				// now construct email, get answers and send email 

				if (ant != null) 
				{
					String email = constructEmail( hp, whatForm );
					email += getAnswers( hp, ant, aantalVragen );
	
					if ( !sendMail(email, usersName, sendersEmail, toEmail) )
					{
						System.out.println("Nwo_Servlet: Email ERROR!! -> [name="+usersName+", emailaddress="+sendersEmail+"]");
						writeToLog(email, whatForm, false);
						displayErrorMail(res);
					}
					else
					{
						System.out.println("Nwo_Servlet: Email success -> [name="+usersName+", emailaddress="+sendersEmail+"]");
						writeToLog(email, whatForm, true);
						displaySuccess(res);
					}
				}
			}
		}
	}
	
	private void displayResult( HttpServletResponse res, String title, String message )
	{
		String result = ""; 

		// Set the content type of this request
		try
		{
			PrintStream out = new PrintStream(res.getOutputStream());
			res.setContentType("text/html");
			//hp.writeHeaders();
		
		result += "<HTML>\n";
		result += "<HEAD>\n";
		result += "   <TITLE>"+title+"</TITLE>\n";
		result += "</HEAD>\n";
		result += "<BODY BGCOLOR=\"#000000\">\n";
		result += "\n";
		result += "<P><CENTER><TABLE BORDER=0 WIDTH=500 HEIGHT=\"95%\">\n";
		result += "   <TR>\n";
		result += "      <TD bgcolor=\"#FFFFFF\">\n";
		result += "         <P><CENTER><B><FONT SIZE=\"+1\" FACE=\"Arial\">";
		
		result += message;

		result += "         </FONT></B></CENTER></P>\n";
		result += "         <P><CENTER><B><FONT SIZE=\"+1\" FACE=\"Arial\">De antwoorden\n";
		result += "         staan eind december op de site.</FONT></B></CENTER>\n";
		result += "      </TD></TR>\n";
		result += "</TABLE></CENTER></P>\n";
		result += "</BODY>\n";
		result += "</HTML>\n";

		out.println( result );
		}
		catch( IOException e )
		{
			debug("display(): ERROR: " + e.toString());
		}
	}

	private void displaySuccess( HttpServletResponse res )
	{
		String titel = "Formulier is verstuurd";
		String body  = "Uw antwoorden zijn verstuurd naar het NWO.<BR>\n";
			   body += "Bedankt voor het meedoen aan deze quiz!";
		displayResult(res, titel, body);
	}
	
	private void displayErrorUsername ( HttpServletResponse res )
	{
		String titel = "Uw naam is niet ingevuld";
		String body  = "Uw naam is niet ingevuld in uw formulier. <BR>\n";
			   body += "<STRONG>Uw formulier is daarom NIET opgestuurt</STRONG>";
		displayResult(res, titel, body);
	}

	private void displayErrorEntry ( HttpServletResponse res )
	{
		String titel = "Entry niet gevonden in dokument";
		String body  = "Het systeem heeft een fout gedetecteerd tijdens het verwerken van uw formulier.<BR>\n";
			   body += "<STRONG>Uw formulier is daarom NIET opgestuurt</STRONG>";
		displayResult(res, titel, body);
	}

	private void displayErrorMail ( HttpServletResponse res )
	{
		String titel = "Fout tijdens versturen email";
		String body  = "Er is een fout opgetreden tijdens het versturen van uw formulier.<BR>\n";
			   body += "Er is melding van gemaakt aan de beheerder, maar u kunt het later nog eens opnieuw opsturen voor de zekerheid.";
		displayResult( res, titel, body);
	}

	/**
 	 * Construct email to user
	 */
	private String constructEmail( HttpPost hp, String whatForm )
	{
		if ( whatForm == null || whatForm.equals(""))
		{
			System.out.println("Nwo_Servlet: constructEmail: whatForm null");
			return null;
		}

		// MAKE E-MAIL 

		String verzend = "";
		String buffer="";
		
		// Set these names and values in the mail
		Vector items = new Vector();
		items.addElement("name");
		items.addElement("adres");
		items.addElement("postcode");
		items.addElement("plaats");
		items.addElement("telefoon");
		items.addElement("email");

		items.addElement("leeftijd");
		items.addElement("geslacht");
		if (whatForm.toLowerCase().equals("junior"))
			items.addElement("hobby");
		else
			if ( whatForm.toLowerCase().equals("senior"))
				items.addElement("beroep");
			else
				System.out.println("Nwo_Servlet: constructEmail: whatForm isn't junior nor senior!");

		items.addElement("krant");
		items.addElement("geattendeerd");

        for (Enumeration e = items.elements (); e.hasMoreElements ();) 
		{
			String item=(String)e.nextElement();	
			buffer = hp.getPostParameter(item);
			verzend += item;
			int i;
			int tabLength;
			if (buffer!=null && !buffer.equals("")) 
			{
				if (item.length() < 12 && item.length() > 0 )
				{
					tabLength = ((12 - item.length()+2) / 4) ;
					for (i =1; i< tabLength; i++)
						verzend += "\t";
				}
				verzend+=" = "+buffer+"\n";
			} 
			else 
			{
				if (item.length() < 12 && item.length() > 0)
				{
					tabLength = ((12 - item.length()+2) / 4 ) ;
					for (i =1; i< tabLength; i++)
						verzend += "\t";
				}	
				verzend +=" = Unknown\n";
			}
		}
		return verzend;
	}


	private String getAnswers( HttpPost hp, String[] antwoorden, int numberOfQuestions )
	{
		String verzend = "";
		String buffer;

		// Sort answers and count the good ones	
		String vragen="";	
		int goede = 0;	
		for(int i=1;i<=numberOfQuestions;i++) 
		{
			buffer = hp.getPostParameter("vraag"+i);
			if (buffer!=null) 
			{
				if (i<10)
					vragen+="vraag 0"+i+" = "+buffer.toUpperCase()+"\n";
				else
					vragen+="vraag "+i+" = "+buffer.toUpperCase()+"\n";
				if(buffer.toUpperCase().equals(antwoorden[i-1])) 
					goede++;	
			} 
			else 
			{
				if (i<10)
					vragen+="vraag 0"+i+" = Unknown\n";
				else
					vragen+="vraag "+i+" = Unknown\n";
	
			}
		}	
	
		// Set answers in mail	
		verzend+="\nAantal goede antwoorden = "+goede+"\n";
		if(goede==numberOfQuestions) 
		{
			verzend+="***********************************************\n";
			verzend+="*** Deze persoon heeft alle antwoorden goed ***\n";
			verzend+="***********************************************\n";
		}
		verzend+="\n"+vragen;

		return verzend;
	}

	/** 
	 * Send mail
	 *
	 * @returns true when send, false otherwise
	 */
	private boolean sendMail( String verzend, String name, String fromEmail, String toEmail )
	{
		boolean result = false;
		Mail mail = new Mail( toEmail, fromEmail);
		mail.setSubject("Wetenschapsquiz inzending ( "+name+" )");
		mail.setDate();
		mail.setReplyTo(sendersEmail);
		mail.setText(verzend);

		result = sendmail.sendMail(mail);

		return result;
	}

	/**
 	 * Writes mail to logbestand
	 */
	private void writeToLog( String verzend, String to, boolean succeeded ) {
   		try 
		{
			RandomAccessFile SecurityLog;
			if (to.toLowerCase().equals("junior"))
        		SecurityLog = new RandomAccessFile(juniorFileName,"rw"); 
			else
        		SecurityLog = new RandomAccessFile(seniorFileName,"rw"); 
	
			SecurityLog.seek(SecurityLog.length());
			
			SecurityLog.writeBytes("---------------------------------------------------------------------\n");
		
			if (!succeeded)
			{
				SecurityLog.writeBytes("*************************************************************\n");
				SecurityLog.writeBytes("*** THIS ENTRY COULD NOT BE MAILED, PLEASE VERIFY BY HAND ***\n");
				SecurityLog.writeBytes("*************************************************************\n");
				SecurityLog.writeBytes("\n");
			}
			SecurityLog.writeBytes(verzend);
			SecurityLog.close();

		}
		catch (IOException ioe)
		{
			 System.out.println("cannot open file" );
		}
	}
	
	private String[] readAnswers(String filename, String whatForm) 
	{
		//System.out.println("entering file "+filename+" from form "+ whatForm);
		String ant[] = new String [100];	
		Hashtable answers = new Hashtable();

		if (!whatForm.toLowerCase().equals("junior") && !whatForm.toLowerCase().equals("senior"))
		{
			System.out.println("Nwo_Servlet: readAnswers: ERROR: whatForm not specified");
		}
		else
		{
			if (filename == null)
				System.out.println("Nwo_Servlet: readAnswers: ERROR: filename is not specified.");
			else
			{

				//System.out.println("Nwo_Servlet: Opening file ...");

				String errorFile = "";
				errorFile += "Nwo_Servlet: *************\n";
				errorFile += "Nwo_Servlet: *** ERROR ***\n";
				errorFile += "Nwo_Servlet: *************\n";
				errorFile += "Nwo_Servlet: Kan voor de WetenschapsQuiz het bestand '"+filename+"' niet ";
	
				RandomAccessFile rafnwo = null;
				int index=0;
				String buffer ="";
	
				try 
				{
   		         	rafnwo = new RandomAccessFile(filename, "r");
		
					try
					{
						//System.out.println("reading entries...");
						StringTokenizer tok;
						while ( (buffer = rafnwo.readLine()) != null) 
						{
							//System.out.print("found entry ");
   		         			tok = new StringTokenizer(buffer,"\t\n:=- ");
   		         			while (tok.hasMoreTokens()) 	
							{
								String sIndex = tok.nextToken();
								try
								{
   		             				index=Integer.parseInt(sIndex)-1;
									//System.out.print(index + " (" + sIndex+ "): " );
									if (tok.hasMoreTokens())
									{
										sIndex = tok.nextToken();
										if (sIndex == null) 
											System.out.println("Nwo_Servlet: readAnswers: ERROR: in file ["+filename+"] entry ["+index+"] is null.");
										else
										{
											ant[index]=sIndex.toUpperCase();
											//System.out.println(ant[index] + ".");
										}
									}
									else
									{
										System.out.println("Nwo_Servlet: readAnswers: WARNING: while reading from file ["+filename+"] answer["+index+"], system didn't found an answer (unexpected EOF)!");
									}
   			     				}
								catch( Exception e ) // NumberFormatException ?
								{
									e.printStackTrace();
								}
							}
						}
	
						try
						{
							//System.out.println("Close");
							rafnwo.close();
   		     			} 
						catch (IOException e) 
						{
							System.out.println( errorFile + " sluiten !" );
							e.printStackTrace();
						}

						//System.out.println("checking form with index " + index);	
						if (whatForm.toLowerCase().equals("junior"))
						{
							if (index == 0)
								System.out.println("Nwo_Servlet: readAnswers: WARNING: while reading JUNIOR file '"+filename+"', 0 answers where found !!!");
							aantalVragenJunior = index+1;
						}
						else
						{
							if (index == 0) 
								System.out.println("Nwo_Servlet: readAnswers: WARNING: while reading SENIOR file '"+filename+"', 0 answers where found !!!");
							aantalVragenSenior = index+1;
						}
					}
					catch (IOException e)
					{
						System.out.println( errorFile + " lezen !");
						e.printStackTrace();
					}
				}
				catch (IOException e)
				{
					System.out.println( errorFile + " openen !");
					e.printStackTrace();
				}
	
				/*
				if ( (index+1) != numberOfAnswers)
				{
					String error = "";
					error += "Nwo_Servlet:                    ********************\n";
					error += "Nwo_Servlet:                    *** WAARSCHUWING ***\n";
					error += "Nwo_Servlet:                    ********************\n";
					error += "Nwo_Servlet:\n";
					error += "Nwo_Servlet: Tijdens het inlezen van het bestand '"+filename+"' voor de WetenschapsQuiz \n";
					error += "Nwo_Servlet: is gebleken dat het aantal ingelezen vragen ("+(index+1)+") NIET overeen komt\n";
					error += "Nwo_servlet: met de werkelijke hoeveelheid vragen ("+numberOfAnswers+").";
	
					System.out.println( error );
				}
				*/
			}
		}
		return ant;
	}


	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}	
	
	/**
	* Info method, provides the user/server with some basic info on
	* this Servlet
 	*/
	public String getServletInfo() 
	{
		return ("NWO Servlet - VPRO, Marcel Maatkamp");
	}
}
