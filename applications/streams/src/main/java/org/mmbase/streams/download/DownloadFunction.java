/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2011 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams.download;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.bridge.*;
import org.mmbase.security.Action;
import org.mmbase.security.ActionRepository;
import org.mmbase.streams.CreateSourcesWithoutProcessFunction;
import org.mmbase.util.ThreadPools;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.NodeFunction;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Downloads a media stream from an url for a media item (mediafragments node) puts it in the files
 * directory and returns it's filename when finished. A timeout parameter let's you set number of
 * seconds you wish to wait for the download to finish. When you specify an email address as parameter
 * you get an email when it's really finished.
 * The function starts a thread and calls {@link Downloader} to do the actual work. The media file itself is
 * saved in a mediasources node and transcoded by the streams application when the download finishes.
 * Url and information about success or failure of the download are saved as properties 
 * on the mediafragments node.
 *
 * @author Michiel Meeuwissen
 * @author Andr&eacute; van Toly
 * @version $Id$
 */
public final class DownloadFunction extends NodeFunction<String> {
    private static final long serialVersionUID = 0L;
    private static final Logger log = Logging.getLoggerInstance(DownloadFunction.class);

    /* url to get */
    private static final Parameter<String> URL = new Parameter<String>("url", String.class);
    /* timeout: how long you are prepaired to wait for a result, default is 5 seconds */
    private static final Parameter<Integer> TIMEOUT = new Parameter<Integer>("timeout", Integer.class);
    /* email address to send ready to */
    private static final Parameter<String> EMAIL = new Parameter<String>("email", String.class);
    public final static Parameter[] PARAMETERS = { URL, EMAIL, TIMEOUT, Parameter.LOCALE };

    private final static String URL_KEY    = DownloadFunction.class.getName() + ".url";
    private final static String STATUS_KEY = DownloadFunction.class.getName() + ".status";

    private static final Map<Integer, Future<?>> runningJobs = new ConcurrentHashMap<Integer, Future<?>>();

    public DownloadFunction() {
        super("download", PARAMETERS);
    }

    protected void setProperty(Node node, String key, String value) {
        NodeManager properties = node.getCloud().getNodeManager("properties");
        Function set = properties.getFunction("set");
        Parameters params = set.createParameters();
        params.set("node", node);
        params.set("key", key);
        if (value.length() > 255) {
            value = value.substring(0, 255);
        }
        params.set("value", value);
        set.getFunctionValue(params);
    }
    protected String getProperty(Node node, String key) {
        NodeManager properties = node.getCloud().getNodeManager("properties");
        Function get = properties.getFunction("get");
        Parameters params = get.createParameters();
        params.set("node", node);
        params.set("key", key);
        return (String) get.getFunctionValue(params);
    }
    protected void setDownloadUrl(Node node, String link) {
        setProperty(node, URL_KEY, link);
    }
    protected void setDownloadStatus(Node node, String status) {
        log.info("Setting status of " + node.getNumber() + " to " + status);
        setProperty(node, STATUS_KEY, status);
    }
    protected String getDownloadStatus(Node node) {
        return getProperty(node, STATUS_KEY);
    }

    private Boolean sendMail(Node node, String email) {
         boolean send = false;

         Cloud cloud = node.getCloud();
         String emailbuilder = "email";
         try {
             Module sendmail = cloud.getCloudContext().getModule("sendmail");
             emailbuilder = sendmail.getProperty("emailbuilder");
         } catch (NotFoundException nfe) {
             log.warn("No email module " + nfe);
         }

         if (cloud.hasNodeManager(emailbuilder)) {

             NodeManager nm = cloud.getNodeManager(emailbuilder);
             Node message = nm.createNode();

             String from = "downloader@mmbase.org";
             try {
                 from = "downloader@" + java.net.InetAddress.getLocalHost().getHostName();
                 // do a quick check if we've got something more or less valid
                 Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                 Matcher m = p.matcher(from);
                 if (!m.matches()) {
                    from = "downloader@mmbase.org";
                 }
             } catch (UnknownHostException uhe) {
                 log.warn("No host: " + uhe);
             }

             String mediaTitle = node.getStringValue("title");
             String mediaUrl = getProperty(node, URL_KEY);
             StringBuilder body = new StringBuilder();
             body.append("Your media file for '").append(mediaTitle);
             body.append("' (#").append(node.getNumber()).append(") has finished downloading from: ").append(mediaUrl);
             body.append(" \n\nKind regards, your friendly downloader");

             message.setValue("from", from);
             message.setValue("to", email);
             message.setValue("subject", "Media download complete");
             message.setValue("body", body.toString());
             message.commit();

             Function mail = message.getFunction("mail");
             Parameters mail_params = mail.createParameters();
             mail_params.set("type", "oneshot");
             mail.getFunctionValue(mail_params);

             if (log.isDebugEnabled()) {
                log.debug("Message download ready send to: " + email);
             }
             send = true;
         } else {
             log.warn("Can not send message - no emailbuilder installed.");
         }

         return send;
     }


    protected Future<?> submit(final Node node, final Parameters parameters) {
        return ThreadPools.jobsExecutor.submit(new Callable() {
            public String call() {
                String result = "";
                Node source = null;
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("media : " + node);
                        log.debug("params: " + parameters);
                    }
                    URL url = new URL(parameters.get(URL));
                    DownloadFunction.this.setDownloadUrl(node, parameters.get(URL));

                    // get or create streamsource node
                    source = CreateSourcesWithoutProcessFunction.getMediaSource(node);

                    Downloader downloader = new Downloader();
                    downloader.setUrl(url);
                    downloader.setNode(source);
                    log.info("Now calling: " + downloader);
                    result = downloader.download();

                    // download is ready
                    DownloadFunction.this.setDownloadStatus(node, "ok");
                    source = CreateSourcesWithoutProcessFunction.getMediaSource(node);  // forces 'reload' of node?
                    source.commit();

                    // send mail?
                    String email = parameters.get(EMAIL);
                    if (email != null && !"".equals(email)) {
                        sendMail(node, email);
                    }

                    log.info("Result: " + result + ", calling transcoders for #" + source.getNumber());
                    source.getFunctionValue("triggerCaches",
                            new Parameters(org.mmbase.streams.CreateCachesFunction.PARAMETERS).set("all", true));

                } catch (IllegalArgumentException iae) {
                    log.error(iae.getMessage(), iae);
                    DownloadFunction.this.setDownloadStatus(node, "NONHTTP " + iae.getMessage());
                } catch (MalformedURLException ue) {
                    log.error(ue.getMessage(), ue);
                    DownloadFunction.this.setDownloadStatus(node, "BADURL " + ue.getMessage());
                } catch (IOException ioe) {
                    log.error(ioe.getMessage(), ioe);
                    DownloadFunction.this.setDownloadStatus(node, "IOERROR " + ioe.getMessage());
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    DownloadFunction.this.setDownloadStatus(node, "UNEXPECTED " + t.getMessage());
                } finally {
                    DownloadFunction.this.runningJobs.remove(node.getNumber());
                    log.info("Running jobs: " + DownloadFunction.this.runningJobs);
                }
                return result;
            }
        });
            
    }

    @Override
    public String getFunctionValue(final Node node, final Parameters parameters) {
        if (log.isDebugEnabled()) {
            log.debug("node #" + node.getNumber());
            log.debug("params: " + parameters);
        }
        String status = getDownloadStatus(node);

        int timeout = 5;
        if (parameters.get(TIMEOUT) != null) {
            timeout = parameters.get(TIMEOUT);
        }

        if (status == null) {
            Action action = ActionRepository.getInstance().get("streams", "download_media");
            if (action == null) {
                throw new IllegalStateException("Action could not be found");
            }
            if (node.getCloud().may(action, null)) {
                synchronized(runningJobs) {
                    Future<?> future = runningJobs.get(node.getNumber());
                    if (future == null) {
                        setDownloadStatus(node, "busy: " + System.currentTimeMillis());
                        future = submit(node, parameters);

                        ThreadPools.identify(future, DownloadFunction.class.getName() + " downloading... for #"  + node.getNumber()  + " - status: " + getDownloadStatus(node) );
                        String fname = ThreadPools.getString(future);
                        log.info("Future name: " + fname);
                        try {
                            status = (String) future.get(timeout, TimeUnit.SECONDS);
                            log.info("status: " + status);
                        } catch (TimeoutException te) {
                            status = ThreadPools.getString(future);
                            log.info("TimeoutException: " + status);
                        } catch (Exception e) {
                            log.error(e);
                        }

                    } else {
                        status = ThreadPools.getString(future);
                    }

                }
                log.info("status: " + status);
                return status;
            } else {
                throw new org.mmbase.security.SecurityException("Not allowed");
            }
        }
        return status;
    }
}
