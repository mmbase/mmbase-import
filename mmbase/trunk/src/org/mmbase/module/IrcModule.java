package org.mmbase.module;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.*;
import org.mmbase.module.core.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.*;

public class IrcModule extends ProcessorModule implements CommunicationUserInterface, Runnable
{
	private String classname = getClass().getName();

	private	CommunicationInterface	com;
    private Thread kicker = null;
	private MMObjectBuilder answers = null;
	private MMObjectBuilder questions = null;
	private MMBase mmbase;
	private Hashtable user2number = new Hashtable();
	private Hashtable number2question = new Hashtable();
	private Hashtable number2answer = new Hashtable();
	private int number=0;
	
	private String nickname = "";
	private String channel = "";
	private String refreshUrl = "";
	private String refreshTime = "";
	private String ircServer = "";

	public IrcModule() {
	}

	public void init() {
		super.init();
  		mmbase=(MMBase)getModule("MMBASEROOT");
		answers=(MMObjectBuilder)mmbase.getMMObject("answers");
		questions=(MMObjectBuilder)mmbase.getMMObject("questions");

		nickname = getInitParameter("nickname");
		channel = getInitParameter("channel");
		refreshUrl = getInitParameter("refreshUrl");
		refreshTime = getInitParameter("refreshTime");
		ircServer = getInitParameter("ircServer");

		start();
	}

	public void receive( String msg ) {
		debug("#"+msg);

		IrcMessage im = new IrcMessage(msg);
		msg=im.getMessage();
		StringTokenizer st = new StringTokenizer(msg);

		if(st.hasMoreTokens()) {
			String firstToken=st.nextToken();

			if(firstToken.equals("mmbase")) {
				String secondToken=st.nextToken();

				if(secondToken.equals("say")) {
					String say = "";
					while (st.hasMoreTokens()) {
						say+=st.nextToken()+" ";
					}
					com.sendPublic(say);
				}

				if(secondToken.equals("question")) {
					String question = "";
					while (st.hasMoreTokens()) {
						question+=st.nextToken()+" ";
					}
					addQuestion(im.getFromNick(),question);
				}

				if(secondToken.equals("help")) {
					com.sendPrivate(im.getFromNick(),"Syntax of MMBase commands are:\n"+
									"mmbase say [text]\n"+
									"mmbase question [text]\n"+
									"mmbase answer to question [number] is [text]\n"+
									"mmbase tell [person] about [subject] (not implemented yet)\n");
				}

				if(secondToken.equals("tell")) {
					com.sendPrivate(im.getFromNick(),"the tell command isn't implemented yet");
				}

				if(secondToken.equals("answer")) {
					try { 
						st.nextToken(); //to
						st.nextToken(); //question
						// resolve number
						String number = st.nextToken();
						st.nextToken(); //is
						// resolve answer
						String antwoord = "";
						while (st.hasMoreTokens()) {
							antwoord+=st.nextToken()+" ";
						}
						addAnswerToQuestion(number, antwoord);
					} catch (Exception e) {
						com.sendPrivate(im.getFromNick(),"Syntax of message is wrong, it should be mmbase answer to question x is ...");
					}
				}
			}

			if(msg.toLowerCase().indexOf("mmbeest")!=-1) { 
				com.sendPublic("Ahum, my name is MMBase not MMBeest");
			}
		
			if(msg.toLowerCase().indexOf("hi mmbase")!=-1) { 
				com.sendPublic("Hi "+im.getFromNick());
			}
		}
	}

	/**
	 * add answer to question
	 */
	private void addAnswerToQuestion(String number, String antwoord) {
		// create answer
		MMObjectNode answer = answers.getNewNode("irc");
		answer.setValue("title",antwoord);
		answer.setValue("body",antwoord);
		answer.insert("irc");
		number2answer.put(number,answer);

		// make relation between question and answer
		int rnumber = mmbase.InsRel.getGuessedNumber("related");
		MMObjectNode question = (MMObjectNode)number2question.get(number);
		mmbase.InsRel.insert("irc",answer.getIntValue("number"),question.getIntValue("number"),rnumber);
	}

	private void debug( String msg ) {
		System.out.println( classname +":"+ msg );
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}

	public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
		return(null);
	}

	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		System.out.println("CMDS="+cmds);
		System.out.println("VARS="+vars);
	
		if(cmds.containsKey("QUESTION")) {
			String vraag = ""+vars.get("vraag");
			String koekie = sp.session.getCookie();
			addQuestion(koekie,vraag);
		}
		return(false);
	}

	private void addQuestion(String user, String question) {
		MMObjectNode vraag = questions.getNewNode("irc");
		vraag.setValue("title",question);
		vraag.setValue("body",question);
		vraag.insert("irc");
		String n = ""+new Integer(number++);
		user2number.put(user,n);
		number2question.put(n,vraag);
		com.sendPublic(n+": "+question);
	}

	public String replace(scanpage sp, String cmds) {
		//System.out.println("replace "+cmds);
		if(cmds.equals("ANSWER")) {
			try {
				String number = (String)user2number.get(sp.session.getCookie());
				MMObjectNode mmon = (MMObjectNode)number2answer.get(number);
				String body = null;
				body = (String)mmon.getValue("body");
				return body;
			} catch (Exception e) {
			}
		}
		if(cmds.equals("QUESTION")) {
			try {
				String number = (String)user2number.get(sp.session.getCookie());
				MMObjectNode mmon = (MMObjectNode)number2question.get(number);
				String body = null;
				body = (String)mmon.getValue("body");
				return body;
			} catch (Exception e) {
			}
		}
		if(cmds.equals("REFRESH")) {
			try {
				String number = (String)user2number.get(sp.session.getCookie());
				if(number2question.containsKey(number)) {
					if(number2answer.containsKey(number)) {
						return "";
					}
					return "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""+refreshTime+"; URL=http://"+refreshUrl+"\">";
				}
				return "";
			} catch (Exception e) {
			}
		}
		return "";
	}

    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"IrcModule");
            kicker.start();
        }
    }

    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }

 	public void run () {
        com = (CommunicationInterface) new IrcUser( this );

        if( com.connect(ircServer, nickname, channel, channel))
        {
            String l = "test";
            DataInputStream dis = new DataInputStream( System.in );

            try
            {
                while( !l.equals("STOP") )
                {
                    if( com.isconnected() )
                    {
                        if( (l=dis.readLine()) != null && !l.equals("STOP"))
                        {
                            com.sendPublic( l );
                            if( l.equals("STOP") )
                                com.stopit();
                        }
                    }
                    else
                    {
                        debug("calling reconned()!");
                        //com.reconnect();
                    }
                }
            }
            catch( IOException e )
            {
                debug( e.toString() );
                com.stopit();
            }
        }
        else
        {
            debug("Could not connect!");
            com.stopit();
        }
    }

	class IrcMessage {
		private String server = null;
		private String to = null;
		private String from = null; 		// loeki!~wwwtech@www.mmbase.org
		private String fromNick = null;		// loeki
		private String message = null;

	
		public IrcMessage(String message) {
			encodeMessage(message);
		}

		private void encodeMessage(String message) {
			StringTokenizer st = new StringTokenizer(message,"[],",true);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if(token.indexOf("server")==0) {
					server=token.substring(token.indexOf('\'')+1,token.lastIndexOf('\''));
				} else
				if(token.indexOf("to")==1) {
					to=token.substring(token.indexOf('\'')+1,token.lastIndexOf('\''));
				} else
				if(token.indexOf("from")==1) {
					from=token.substring(token.indexOf('\'')+1,token.lastIndexOf('\''));
					try {
						fromNick=from.substring(0,from.indexOf('!'));
					} catch (Exception e) {
					}
				} else
				if(token.indexOf("]")==0) {
					if(st.hasMoreTokens()) {
						this.message = st.nextToken();	
					}
				}
			} 
		}

		public String getServer() {
			return server;
		}

		public String getTo() {
			return to;	
		}

		public String getFrom() {
			return from;	
		}

		public String getFromNick() {
			return fromNick;
		}

		public String getMessage() {
			return message;	
		}
	}		
}
