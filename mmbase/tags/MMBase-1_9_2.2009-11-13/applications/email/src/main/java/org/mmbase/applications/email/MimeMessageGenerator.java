/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.util.*;

import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.*;
import javax.activation.*;


import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * This class contains the functionality to contain 'multipart xml', which can be used as the body
 * of email nodes (of the type {@link EmailBuilder}.

 * @author Daniel Ockeloen
 *
 */
class MimeMessageGenerator {

    private static final Logger log = Logging.getLoggerInstance(MimeMessageGenerator.class);

    /**
     * @javadoc
     */
    public static MimeMultipart getMimeMultipart(String text, Node node) {

        Map<String, MimeBodyTag> nodes = new HashMap<String, MimeBodyTag>();
        List<MimeBodyTag> rootNodes = new ArrayList<MimeBodyTag>();


        for (MimeBodyTag tag : getMimeBodyParts(text, node)) {
            try {
		// get all the needed fields
		String type    = tag.getType();
		String related = tag.getRelated();
		String alt     = tag.getAlt();
                log.debug("" + tag);
		// add it to the id cache
                if (tag.getId() != null) {
                    nodes.put(tag.getId(), tag);
                }

		// is it a root node ?
		if (alt == null && related == null) {
                    rootNodes.add(tag);
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

	if (rootNodes.size() == 1) {
            MimeBodyTag t = rootNodes.get(0);
            MimeMultipart mmp = t.getMimeMultipart();
            if (mmp != null) {
                return mmp;
            } else {
                try {
                    MimeMultipart root = new MimeMultipart();
                    root.addBodyPart(t.getMimeBodyPart());
                    log.debug("Returing " + root);
                    return root;
                } catch (MessagingException e) {
                    log.error("Root generation error" + e.getMessage());
                }
            }
	} else {
            if (rootNodes.size() > 1) {
                try {
                    MimeMultipart root = new MimeMultipart();
                    root.setSubType("mixed");
                    for (MimeBodyTag t : rootNodes) {
                        MimeMultipart mmp = t.getMimeMultipart();
                        if (mmp != null) {
                            log.debug("setting parent info : " + t.getId());
                            MimeBodyPart wrapper = new MimeBodyPart();
                            wrapper.setContent(mmp);
                            root.addBodyPart(wrapper);
                        } else {
                            log.debug("adding info : " + t.getId());
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

    /**
     * @javadoc
     */
    static List<MimeBodyTag> getMimeBodyParts(String body, Node node) {
        // MM.
        // This is all quite horrible
        // because an XML syntax is parsed here, in a very particular way.
        // This leads to all kind of issues like e.g.
        // http://www.mmbase.org/jira/browse/MMB-982
        //
        // This may be a bit hard to solve, because it is not garanteed that the body is actually
        // XML. and it may lead to backwards incompatibility issues.
        // So I'm not feeling like such a redo of this ***t right now.
        // But I may feel so soon!

        log.debug("Get parts for " + body);

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


            // StringTagger is deprecated.
            StringTagger atrtagger = new StringTagger(atr);

            MimeBodyTag tag = new MimeBodyTag(node.getCloud());

            String type = atrtagger.Value("type");
            if (type != null) tag.setType(type);

            String encoding = atrtagger.Value("encoding");
            if (encoding != null) tag.setEncoding(encoding);

            String number = atrtagger.Value("number");
            if (number != null) tag.setNumber(number);

            String field = atrtagger.Value("field");
            if (field != null) tag.setNumber(field);

            tag.setFormatter(atrtagger.Value("formatter"));

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

            tag.setText(part);

            results.add(tag);

            // set body ready for the new part
            endpos = body.indexOf(endkey);
            body = body.substring(endpos + endkey.length());
            pos = body.indexOf(startkey);
        }
        log.debug("Found " + results);
        return results;
    }


    /**
     * @todo I don't see the point of wrapping a body part in this things first.
     *       Why don't we parse directly to MultiPart's, and avoid about 300 lines of code...
     *
     */
    private static  class MimeBodyTag {

        private String type     = "text/plain";
        private String encoding = "ISO-8859-1";
        private String text     = "";
        private String id       = "default";

        private String related;
        private String alt;
        private String formatter;
        private String filePath;
        private String fileName;
        private List<MimeBodyTag> altNodes;
        private MimeMultipart relatedNodes;
        private String number;
        private String field;

        private final Cloud cloud;

        public String toString() {
            return type + (altNodes == null ? "" : (" alt: " + altNodes )) + (relatedNodes == null ? "" : (" rel: "+ relatedNodes)) + (number != null ? " " + number + "." + getField() : "");
        }


        MimeBodyTag(Cloud c) {
            cloud = c;
        }


        /**
         * @deprecated
         */
        public void setFormatter(String formatter) {
            this.formatter = formatter;
        }

        /**
         * @javadoc
         */

        public void addAlt(MimeBodyTag sub) {
            if (altNodes == null) {
                //altNodes=new MimeMultipart("alternative");
                //altNodes.addBodyPart(getMimeBodyPart());
                altNodes = new ArrayList<MimeBodyTag>();
            }
            //altNodes.addBodyPart(sub.getMimeBodyPart());
            altNodes.add(sub);
        }



        /**
         * @javadoc
         */

        public void addRelated(MimeBodyTag sub) {
            try {
                if (relatedNodes == null) {
                    relatedNodes = new MimeMultipart("related");
                    relatedNodes.addBodyPart(getMimeBodyPart());
                }
                relatedNodes.addBodyPart(sub.getMimeBodyPart());
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

        public void setFile(String filePath) {
            this.filePath = filePath;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            if (fileName == null) {
                return ResourceLoader.getName(getFile());
            }
            return fileName;
        }

        /**
         * @deprecated
         */
        public String getFormatter() {
            return formatter;
        }

        public String getFile() {
            return filePath;
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
            return field == null ? "handle" : field;
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
                // deprecated
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
         * @deprecated
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
            // WTF
            obj.replace("<br/>", "\n");
            obj.replace("<br />", "\n");
            obj.replace("<BR/>", "\n");
            obj.replace("<BR />", "\n");
            obj.replace("<br>", "\n");
            obj.replace("<BR>", "\n");
            obj.replace("<p>"," \n\n");   // WTF
            obj.replace("<p/>", "\n\n");  // WTF
            obj.replace("<p />", "\n\n"); // WTF
            obj.replace("<P>"," \n\n");   // WTF
            result = obj.toString();

            // return the coverted body
            return result;
        }

        /**
         * @javadoc
         */
        public MimeMultipart getMimeMultipart() {
            try {
                if (altNodes != null) {
                    MimeMultipart result = new MimeMultipart("alternative");

                    MimeMultipart r = getRelatedpart();
                    if (r == null) {
                        result.addBodyPart(getMimeBodyPart());
                    } else {
                        MimeBodyPart wrapper = new MimeBodyPart();
                        wrapper.setContent(r);
                        result.addBodyPart(wrapper);
                    }

                    for (MimeBodyTag t : altNodes) {
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
                if (relatedNodes != null) {
                    return relatedNodes;
                } else {
                    log.debug("No multipart for " + this);
                }
            } catch (MessagingException e) {
                log.error("Failed to get Multipart" + e.getMessage());
            }
            return null;
        }

        /**
         * @javadoc
         */
        public MimeMultipart getRelatedpart() {
            return relatedNodes;
        }

        /**
         * @javadoc
         */
        public MimeBodyPart getMimeBodyPart() {
            log.debug("Creating mimebody part for " + this);
            MimeBodyPart mmbp = new MimeBodyPart();
            try {
                if (number != null && !number.equals("")) {
                    log.service("attachment");
                    addNode(mmbp, number, getField());
                } else  if (type.equals("text/plain")) {
                    DataHandler d = new DataHandler(text, type + ";charset=\"" + encoding + "\"");
                    mmbp.setDataHandler(d);
                } else if (type.equals("text/html")) {
                    DataHandler d = new DataHandler(text, type + ";charset=\"" + encoding + "\"");
                    mmbp.setDataHandler(d);
                             } else if (type.startsWith("application/")) {
                    if (filePath.indexOf("..") == -1 && filePath.indexOf("WEB-INF") == -1) {
                        DataSource ds = new ByteArrayDataSource(ResourceLoader.getWebRoot().getResourceAsStream(filePath), type);
                        DataHandler d = new DataHandler(ds);
                        mmbp.setDataHandler(d);
                        mmbp.setDisposition("attachment");
                        mmbp.setFileName(getFileName());
                    } else {
                        log.error("file from '" + filePath + "' not allowed");
                    }
                } else if (type.startsWith("image/")) {
                    if (filePath.indexOf("..") == -1 && filePath.indexOf("WEB-INF") == -1) {
                        DataSource ds = new ByteArrayDataSource(ResourceLoader.getWebRoot().getResourceAsStream(filePath), type);
                        DataHandler d = new DataHandler(ds);
                        mmbp.setDataHandler(d);
                        mmbp.setDisposition("inline");
                    } else {
                        log.error("file from '" + filePath + "' not allowed");
                    }
                } else {
                    log.warn("don't know how to handle " + this);
                }
                if (id != null) {
                    mmbp.setHeader("Content-ID","<"+id+">");
                }
            } catch(Exception e){
                log.error(e.getMessage(), e);
            }


            return mmbp;
        }

        private CharTransformer getUnhtml() {
            TagStripperFactory factory = new TagStripperFactory();
            Parameters params = factory.createParameters();
            params.set("tags", "NONE");
            params.set("addbrs", false);
            params.set("escapeamps", false);
            CharTransformer transformer = (CharTransformer) factory.createTransformer(params);
            return transformer;
        }


        private void addNode(MimeBodyPart mmbp, String number, String field) throws MessagingException {
            Node node = cloud.getNode(number);
            String mimeType;
            try {
                Function f = node.getFunction("mimetype");
                Parameters params = f.createParameters();
                params.setIfDefined("field", field);
                mimeType = f.getFunctionValue(params).toString();
                if (mimeType == null || mimeType.startsWith("Failed ")) {
                    mimeType = "application/octet-stream";
                }
            } catch (NotFoundException nfe) {
                mimeType = "appliction/octet-stream";
            }
            String fileName = "bla";
            if (node.getNodeManager().hasField("filename")) {
                fileName = node.getStringValue("filename");
            } else {
                CharTransformer unhtml = getUnhtml();
                fileName = unhtml.transform("" + node.getFunctionValue("gui", null));
            }
            if (fileName != null) {
                mmbp.setFileName(fileName);
            } else {
                mmbp.setDisposition("attachment");
            }



            byte[] b = node.getByteValue(field);
            if (b == null || b.length == 0) {
                log.warn("No binary data found for node " + node.getNumber() + "." + field);
                b = ("No binary data found for node " + node.getNumber() + "." + field).getBytes();
                mimeType = "text/plain";
            }
            DataHandler d = new DataHandler(new ByteArrayDataSource(b, mimeType));
            mmbp.setDataHandler(d);

            if (log.isDebugEnabled()) {
                log.debug("attached node=" + node + " " + mimeType + " -> " + mmbp);
            }

        }

    }

}
