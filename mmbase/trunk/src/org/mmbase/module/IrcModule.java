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

	public IrcModule() {}

	public void receive( String msg ) {
		StringBuffer sb = new StringBuffer(msg);
		//debug( classname +":"+ msg );
		msg=sb.substring(msg.indexOf(']')+1,msg.length());
		debug( classname +":"+ msg );
		if(msg.toLowerCase().indexOf("mmbeest")!=-1) com.sendPublic("Ahum, my name is MMBase not MMBeest");

		try {
		StringTokenizer st = new StringTokenizer(msg);
		String name = st.nextToken(); 
		if(name.equals("mmbase")) {
			String number = st.nextToken();
			String antwoord = "";
			while (st.hasMoreTokens()) {
				antwoord+=st.nextToken()+" ";
			}
			MMObjectNode answer = answers.getNewNode("irc");
			answer.setValue("body",antwoord);
			answer.insert("irc");
			number2answer.put(number,answer);

			//create relations
			debug(""+(answer.getValue("number")));
			int rnumber = mmbase.InsRel.getGuessedNumber("related");
			debug("rnumber"+new Integer(rnumber));
			debug(""+answer);
			MMObjectNode question = (MMObjectNode)number2question.get(number);
			debug(""+new Integer(question.getIntValue("number")));

			mmbase.InsRel.insert("irc",answer.getIntValue("number"),question.getIntValue("number"),rnumber);
			
		}
		} catch (Exception e) {
			System.out.println(e);
 			e.printStackTrace();

		}
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}

	public void init() {
		super.init();
  		mmbase=(MMBase)getModule("MMBASEROOT");
		answers=(MMObjectBuilder)mmbase.getMMObject("answers");
		questions=(MMObjectBuilder)mmbase.getMMObject("questions");
		start();
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
			System.out.println("vraag="+vars.get("vraag"));
			MMObjectNode vraag = questions.getNewNode("irc");
			vraag.setValue("body",vars.get("vraag"));
			vraag.insert("irc");
			String koekie = sp.session.getCookie();
			String n = ""+new Integer(number++);
			user2number.put(koekie,n);
			number2question.put(n,vraag);
			com.sendPublic(n+": " +vars.get("vraag"));
		}
		return(false);
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
					return "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"4; URL=http://www.mmbase.org:8080/from.shtml\">";
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

		if( com.connect( "irc.xs4all.nl", "mmbase", "#mmbase", "#mmbase") )
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
}
