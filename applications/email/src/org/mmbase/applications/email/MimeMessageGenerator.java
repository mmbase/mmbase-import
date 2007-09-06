/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.util.*;

import java.io.*;
import org.mmbase.module.core.*;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.activation.*;


import org.mmbase.bridge.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @javadoc
 * @author Daniel Ockeloen
 *
 */
public class MimeMessageGenerator {

    private static final Logger log = Logging.getLoggerInstance(MimeMessageGenerator.class);

    /**
     * @javadoc
     */
    public static MimeMultipart getMimeMultipart(String text, Node node) {

        Map<String, MimeBodyTag> nodes = new HashMap<String, MimeBodyTag>();
        List<MimeBodyTag> rootnodes = new ArrayList<MimeBodyTag>();


        for (MimeBodyTag tag : MimeBodyTagger.getMimeBodyParts(text, node)) {
            try {
		// get all the needed fields
		String type    = tag.getType();
		String id      = tag.getId();
		String related = tag.getRelated();
		String alt     = tag.getAlt();

		// add it to the id cache
		nodes.put(id, tag);

		// is it a root node ?
		if (alt == null && related == null) {
                    rootnodes.add(tag);
		} else if (alt != null) {
                    MimeBodyTag oldpart = nodes.get(alt);
                    if (oldpart != null) {
                        oldpart.addAlt(tag);
                    }
		} else if (related != null) {
                    MimeBodyTag oldpart = nodes.get(related);
                    if (oldpart != null) {
                        oldpart.addRelated(tag);
                    }
		}

            } catch(Exception e) {
		log.error("Mime mail error " + e.getMessage());
            }
	}

	if (rootnodes.size() == 1) {
            MimeBodyTag t = rootnodes.get(0);
            MimeMultipart mmp = t.getMimeMultipart();
            if (mmp != null) {
                return mmp;
            }
	} else {
            if (rootnodes.size()>1) {
                try {
                    MimeMultipart root = new MimeMultipart();
                    root.setSubType("mixed");
                    for (MimeBodyTag t : rootnodes) {
                        MimeMultipart mmp = t.getMimeMultipart();
                        if (mmp != null) {
                            log.info("setting parent info : " + t.getId());
                            MimeBodyPart wrapper = new MimeBodyPart();
                            wrapper.setContent(mmp);
                            root.addBodyPart(wrapper);
                        } else {
                            log.info("adding info : " + t.getId());
                            root.addBodyPart(t.getMimeBodyPart());
                        }
                    }
                    return root;
                } catch (MessagingException e) {
                    log.error("Root generation error" + e.getMessage());
                }
            } else {
                log.error("Don't have a root node");
            }
        }
	return null;
    }
    private static class MimeBodyTagger {


        /**
         * @javadoc
         */
        static List<MimeBodyTag> getMimeBodyParts(String body, Node node) {
            String startkey="<multipart ";
            String endkey="</multipart>";

            List<MimeBodyTag> results = new ArrayList<MimeBodyTag>();

            int pos = body.indexOf(startkey);
            while (pos != -1) {
                String part = body.substring(pos);
                int endpos  = part.indexOf(endkey);
                part        = part.substring(startkey.length(), endpos);
                String atr  = part.substring(0, part.indexOf(">"));
                part = part.substring(part.indexOf(">")+1);
                StringTagger atrtagger = new StringTagger(atr);

                MimeBodyTag tag = new MimeBodyTag();

                String type = atrtagger.Value("type");
                if (type != null) tag.setType(type);

                String encoding = atrtagger.Value("encoding");
                if (encoding != null) tag.setEncoding(encoding);

                String number = atrtagger.Value("number");
                if (number != null) tag.setNumber(number);

                String field = atrtagger.Value("field");
                if (field != null) tag.setNumber(field);

                String formatter = atrtagger.Value("formatter");
                if (formatter != null) tag.setFormatter(formatter);

                String alt = atrtagger.Value("alt");
                if (alt != null) tag.setAlt(alt);

                String id = atrtagger.Value("id");
                if (id != null) tag.setId(id);

                String related = atrtagger.Value("related");
                if (related != null) tag.setRelated(related);

                String file = atrtagger.Value("file");
                if (file != null) tag.setFile(file);

                String filename = atrtagger.Value("filename");
                if (filename != null) tag.setFileName(filename);

                String attachment = atrtagger.Value("attachment");
                if (attachment != null) tag.setAttachment(attachment);

                tag.setText(part);

                results.add(tag);

                // set body ready for the new part
                endpos = body.indexOf(endkey);
                body = body.substring(endpos+endkey.length());
                pos = body.indexOf(startkey);
            }
            return results;
        }

    }


    /**
     * @todo I don't see the point of wrapping a body part in this things first.
     *       Why don't we parse directly to MultiPart's, and avoid about 300 lines of code...
     *
     * @author Daniel Ockeloen
     */
    private static  class MimeBodyTag {

        private String type="text/plain";
        private String encoding="ISO-8859-1";
        private String text="";
        private String id="default";
        private String related;
        private String alt;
        private String formatter;
        private String filepath;
        private String filename;
        private List<MimeBodyTag> altnodes;
        private MimeMultipart relatednodes;
        private String number;
        private String field;

        private static final Logger log = Logging.getLoggerInstance(MimeBodyTag.class);


        public void setFormatter(String formatter) {
            this.formatter = formatter;
        }


        /**
         * @javadoc
         */

        public void addAlt(MimeBodyTag sub) {
            if (altnodes == null) {
                //altnodes=new MimeMultipart("alternative");
                //altnodes.addBodyPart(getMimeBodyPart());
                altnodes = new ArrayList<MimeBodyTag>();
            }
            //altnodes.addBodyPart(sub.getMimeBodyPart());
            altnodes.add(sub);
        }



        /**
         * @javadoc
         */

        public void addRelated(MimeBodyTag sub) {
            try {
                if (relatednodes == null) {
                    relatednodes = new MimeMultipart("related");
                    relatednodes.addBodyPart(getMimeBodyPart());
                }
                relatednodes.addBodyPart(sub.getMimeBodyPart());
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public void setRelated(String related) {
            this.related = related;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setFile(String filepath) {
            this.filepath = filepath;
        }

        public void setFileName(String filename) {
            this.filename = filename;
        }

        public String getFileName() {
            if (filename == null) {
                // needs to be better, create a guessed name on getFile
                return "unknown";
            }
            return filename;
        }

        public void setAttachment(String attachmentid) {
        }

        public String getFormatter() {
            return formatter;
        }

        public String getFile() {
            return filepath;
        }

        public String getType() {
            return type;
        }

        public String getEncoding() {
            return encoding;
        }

        public String getNumber() {
            return number;
        }

        public String getField() {
            return field;
        }

        public String getId() {
            return id;
        }

        public String getRelated() {
            return related;
        }

        public String getAlt() {
            return alt;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setNumber(String number) {
            this.number = number;
        }
        public void setField(String field) {
            this.field = field;
        }
        public void setText(String text) {
            this.text = text;
        }
        public String getText() {
            // is there a formatter requested ??
            if (formatter != null) {
                // wtf
                if (formatter.equals("html2plain")) {
                    return html2plain(text);
                }
            }
            return text;
        }


        /**
         * convert 'html' to 'plain' text
         * this removes the br and p tags and converts them
         * to returns and dubble returns for email use.

         // WTF WTF WTF
         */
        private static String html2plain(String input) {
            // define the result string
            String result="";

            // setup a tokenizer on all returns and linefeeds so
            // we can remove them
            StringTokenizer tok = new StringTokenizer(input,"\n\r");
            while (tok.hasMoreTokens()) {
                // add the content part stripped of its return/linefeed
                result += tok.nextToken();
            }

            // now use the html br and p tags to insert
            // the wanted returns
            StringObject obj = new StringObject(result);
            obj.replace("<br/>", "\n");
            obj.replace("<br />", "\n");
            obj.replace("<BR/>", "\n");
            obj.replace("<BR />", "\n");
            obj.replace("<br>", "\n");
            obj.replace("<BR>", "\n");
            obj.replace("<p>"," \n\n");
            obj.replace("<p/>", "\n\n");
            obj.replace("<p />", "\n\n");
            obj.replace("<P>"," \n\n");
            result=obj.toString();

            // return the coverted body
            return result;
        }

        /**
         * @javadoc
         */
        public MimeMultipart getMimeMultipart() {
            try {
                if (altnodes!=null) {
                    MimeMultipart result = new MimeMultipart("alternative");

                    MimeMultipart r = getRelatedpart();
                    if (r == null) {
                        result.addBodyPart(getMimeBodyPart());
                    } else {
                        MimeBodyPart wrapper = new MimeBodyPart();
                        wrapper.setContent(r);
                        result.addBodyPart(wrapper);
                    }

                    for (MimeBodyTag t : altnodes) {
                        r = t.getRelatedpart();
                        if (r == null) {
                            result.addBodyPart(t.getMimeBodyPart());
                        } else {
                            MimeBodyPart wrapper = new MimeBodyPart();
                            wrapper.setContent(r);
                            result.addBodyPart(wrapper);
                        }

                    }
                    return result;
                }
                if (relatednodes != null) return relatednodes;
            } catch (MessagingException e) {
                log.debug("Failed to get Multipart" + e.getMessage());
            }
            return null;
        }

        /**
         * @javadoc
         */
        public MimeMultipart getRelatedpart() {
            return relatednodes;
        }

        /**
         * @javadoc
         */
        public MimeBodyPart getMimeBodyPart() {
            MimeBodyPart mmbp = new MimeBodyPart();
            try {
                DataHandler d = null;
                if (number != null && !number.equals("")) {
                    if (field!=null) {
                        d = getMMBaseObject(number, field);
                    } else {
                        d = getMMBaseObject(number);
                    }
                } else  if (type.equals("text/plain")) {
                    d = new DataHandler(text, type + ";charset=\"" + encoding + "\"");
                    mmbp.setDataHandler(d);
                } else if (type.equals("text/html")) {
                    d = new DataHandler(text, type + ";charset=\"" + encoding + "\"");
                    mmbp.setDataHandler(d);
                } else if (type.equals("application/octet-stream")) {
                    // WTF WTF WTF
                    String filepath = MMBaseContext.getHtmlRoot() + File.separator + getFile();
                    if (filepath.indexOf("..") == -1 && filepath.indexOf("WEB-INF") == -1) {
                        FileDataSource fds = new FileDataSource(filepath);
                        d = new DataHandler(fds);
                        mmbp.setDataHandler(d);
                        mmbp.setFileName(getFileName());
                    } else {
                        log.error("file from there not allowed");
                    }
                } else if (type.equals("image/gif") || type.equals("image/jpeg")) {
                    // more WTF WTF
                    String filepath = MMBaseContext.getHtmlRoot() + File.separator + getFile();
                    if (filepath.indexOf("..") == -1 && filepath.indexOf("WEB-INF") == -1) {
                        FileDataSource fds = new FileDataSource(filepath);
                        d = new DataHandler(fds);
                        mmbp.setDataHandler(d);
                        mmbp.setHeader("Content-ID","<"+id+">");
                        mmbp.setHeader("Content-Disposition","inline");
                    } else {
                        log.error("file from there not allowed");
                    }
                }

            } catch(Exception e){
                log.error(e.getMessage());
            }

            return mmbp;
        }


        /**
         * @javadoc
         */

        private DataHandler getMMBaseObject(String number) {
            return getMMBaseObject(number,"");
        }



        /**
         * @javadoc
         */

        private DataHandler getMMBaseObject(String number,String field) {

            // TODO should use user cloud, to avoid sending nodes which are unreadable.
            Cloud cloud = LocalContext.getCloudContext().getCloud("mmbase");
            Node node = cloud.getNode(number);
            log.info("attached node=" + node);
            return null;
        }

    }

}
