/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.module.irc.communication.*;
import org.mmbase.module.irc.communication.irc.*;
import org.mmbase.module.core.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author vpro
 * @version $Id: IrcModule.java,v 1.15 2002-03-04 14:07:45 pierre Exp $
 */

public class IrcModule extends ProcessorModule implements CommunicationUserInterface, Runnable {
    private static Logger log = Logging.getLoggerInstance(IrcModule.class.getName());

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

    /**
     * @javadoc
     */
    public IrcModule() {
    }

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
    public void receive( String msg ) {
        if (log.isDebugEnabled()) {
            log.debug("#" + msg);
        }

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
                    matchQuestion(question);
                }

                if(secondToken.equals("help")) {
                    com.sendPrivate(im.getFromNick(),"Syntax of MMBase commands are:\n"+
                                    "mmbase say [text]\n"+
                                    "mmbase question [text]\n"+
                                    "mmbase answer to question [number] is [text]\n"+
                                    "mmbase tell [person|all] about [subject]\n");
                }

                if(secondToken.equals("tell")) {
                    String person = "";
                    String subject = "";
                    String result="";

                    person=st.nextToken();
                    st.nextToken(); //about
                    subject=st.nextToken();

                    Enumeration g = questions.search("WHERE title like '%"+subject+"%'");
                    if (g.hasMoreElements()) { //just take the first match
                        MMObjectNode questionNode=(MMObjectNode)g.nextElement();
                        Vector a = questionNode.getRelatedNodes("answers");
                        Enumeration aa = a.elements();
                        while (aa.hasMoreElements()) {
                            MMObjectNode answerNode=(MMObjectNode)aa.nextElement();
                            result+=answerNode.getStringValue("title")+"\n";
                            System.out.println(answerNode.getStringValue("title"));
                        }
                    }
                    if(person.equals("all") || person.equals("everybody")) {
                        com.sendPublic(result);
                    } else {
                        com.sendPrivate(person,result);
                    }
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
                        com.sendPublic(im.getFromNick() +" thank you for your answer");
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
        int rnumber = mmbase.getRelDef().getNumberByName("related");
        MMObjectNode question = (MMObjectNode)number2question.get(number);
        MMObjectNode relnode=mmbase.getInsRel().getNewNode("irc");
        relnode.setValue("snumber",answer.getNumber());
        relnode.setValue("snumber",question.getNumber());
        relnode.setValue("snumber",rnumber);
        relnode.insert("irc");
    }

    /**
     * @javadoc
     */
    private MMObjectNode matchQuestion (String question) {
        MMObjectNode matchedquestion = null;
        float value = 0;
        float highestValue = 0;

        Enumeration g = questions.search("WHERE title ='*'");
        while (g.hasMoreElements()) {
            MMObjectNode questionNode=(MMObjectNode)g.nextElement();
            System.out.print("MATCHING => "+questionNode.getStringValue("title")+" "+question);
            value = Matcher.match(questionNode.getStringValue("title"),question);
            System.out.print(" value = "+value);
            if (highestValue<=value) {
                matchedquestion=questionNode;
            }
        }
        if (highestValue>0.75) {
            return matchedquestion;
        } else {
            return null;
        }
    }

    /**
     * @javadoc
     */
    public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
        return(null);
    }

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"IrcModule");
            kicker.start();
        }
    }

    /**
     * @javadoc
     */
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        // kicker.suspend();
        // kicker.stop();
        kicker = null;
    }

    /**
     * @javadoc
     */
    public void run () {
        com = (CommunicationInterface) new IrcUser( this );

        if(com.connect(ircServer, nickname, channel, channel)) {
            String l = "test";
            BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
            try {
                while((kicker!=null) && !l.equals("STOP")) {
                    if(com.isconnected()) {
                        if((l=dis.readLine()) != null && !l.equals("STOP")) {
                            com.sendPublic( l );
                            if(l.equals("STOP"))
                                com.stopit();
                        }
                    } else {
                        log.debug("calling reconned()!");
                        //com.reconnect();
                    }
                }
            } catch( IOException e ) {
                log.error( e.toString() );
                com.stopit();
            }
        } else {
            log.error("Could not connect!");
            com.stopit();
        }
    }

    /**
     * @javadoc
     */
    class IrcMessage {
        private String server = null;
        private String to = null;
        private String from = null;
        private String fromNick = null;		// loeki
        private String message = null;

        /**
         * @javadoc
         */
        public IrcMessage(String message) {
            encodeMessage(message);
        }

        /**
         * @javadoc
         */
        private void encodeMessage(String message) {
            try {
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
            } catch (Exception e) {
                log.error("Failed to encode message " + message);
            }
        }

        /**
         * @javadoc
         */
        public String getServer() {
            return server;
        }

        /**
         * @javadoc
         */
        public String getTo() {
            return to;
        }

        /**
         * @javadoc
         */
        public String getFrom() {
            return from;
        }

        /**
         * @javadoc
         */
        public String getFromNick() {
            return fromNick;
        }

        /**
         * @javadoc
         */
        public String getMessage() {
            return message;
        }
    }
}
