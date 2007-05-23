package nl.didactor.component.metadata.autofill.handlers;


import java.util.ArrayList;
import java.util.Iterator;

import java.net.URL;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.format.*;

import javax.servlet.ServletContext;
import java.io.*;


import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


import nl.didactor.component.metadata.autofill.HandlerInterface;
import nl.didactor.metadata.util.MetaHelper;
import nl.didactor.metadata.util.MetaLangStringHelper;


public class StreamingHandler implements HandlerInterface, ControllerListener{

    private static Logger log = Logging.getLoggerInstance(StreamingHandler.class);

    private Processor p;
    private Object waitSync = new Object();
    private boolean stateTransitionOK = true;
    private Player player;

    private ServletContext servletContext;


    public StreamingHandler(ServletContext servletContext){
        this.servletContext = servletContext;
    }


    /**
     * Sets the correct values. It overwrites old values if there any.
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     */
    public void addMetaData(Node nodeMetaDefinition, Node nodeObject){
        log.info("StreamingHandler:addMetaData()");

        NodeList nlMetaDataNodes = nodeObject.getCloud().getList("" + nodeMetaDefinition.getNumber(),
           "metadefinition,metadata,object",
           "metadata.number",
           "object.number='" + nodeObject.getNumber() + "'",
           null,null,null,false);


        Node nodeMetaData = null;
        try {
            nodeMetaData = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metadata.number"));
        }
        catch (Exception e){
            //There is no metadata node yet, let's create it
            nodeMetaData = MetaHelper.createMetaDataNode(nodeObject.getCloud(), nodeObject, nodeMetaDefinition);
        }


        try{
            String fileName = "didactor_temp_media_file_" + nodeMetaData.getNumber() + "_";
            String fileExtension =  "." + nodeObject.getStringValue("filename");
            log.debug("StreamingHandler:Writing file to filename=" + fileName + "  extension=" + fileExtension);
            File fileTemp = File.createTempFile(fileName, fileExtension);
            RandomAccessFile raTemp = new RandomAccessFile(fileTemp, "rw");
            raTemp.write(nodeObject.getByteValue("handle"));
            raTemp.close();
            raTemp = null;

            String tempFile = "file:///" + fileTemp.getAbsolutePath().replaceAll("\\\\", "/");
            log.debug("Creating processor from " + tempFile);
            p = this.createProcessor(new URL(tempFile));
            fileTemp = null;

            Node nodeResultLangString = MetaLangStringHelper.doOneLangString(nodeMetaData);
            try{
                ArrayList arliFormats = this.getMediaFormats(p);
                for(Iterator it = arliFormats.iterator(); it.hasNext(); ){
                    MetaLangStringHelper.addNewLangString(nodeMetaData, (String) it.next(), "", -1);
                }
            }
            catch(Exception e){
                log.error(e);
            }

            try{
                nodeResultLangString.setValue("value", this.getDuration(p));
                nodeResultLangString.commit();
            }
            catch(Exception e){
                log.error(e);
            }

            try{
                p.deallocate();
            }
            catch(Exception e){
                log.error(e);
            }
        }
        catch(Exception e){
            log.error(e);
        }
    }




    /**
     * Checks the correct metadata value
     * @param nodeMetaDefinition Node
     * @param nodeObject Node
     * @return boolean
     */
    public boolean checkMetaData(Node nodeMetaDefinition, Node nodeObject){
        NodeList nlMetaDataNodes = nodeObject.getCloud().getList("" + nodeMetaDefinition.getNumber(),
           "metadefinition,metadata,metalangstring,metadata,object",
           "metalangstring.number",
           "object.number='" + nodeObject.getNumber() + "'",
           null,null,null,false);


        try{
            Node nodeLangString = nodeMetaDefinition.getCloud().getNode(nlMetaDataNodes.getNode(0).getStringValue("metalangstring.number"));
            String sDate = nodeLangString.getStringValue("value");

            if (false) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }




    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This
     * function will be called whenever there is a media event
     */
    public synchronized void controllerUpdate(ControllerEvent event) {
        // If we're getting messages from a dead player,
        // just leave
        if (p == null) {
            return;
        }

        if (event instanceof CachingControlEvent) {
            if (p.getState() > Controller.Realizing) {
                return;
            }

        }
        else if (event instanceof EndOfMediaEvent) {
            // We've reached the end of the media; rewind and
            // start over
            p.setMediaTime(new Time(0));
            p.start();

        }
        else if (event instanceof ControllerErrorEvent) {
            p = null;
            Fatal( ( (ControllerErrorEvent) event).getMessage());
        }
    }

    void Fatal (String s){
        // Applications will make various choices about what
        // to do here. We print a message
        //System.err.println("FATAL ERROR: " + s);
        throw new Error(s); // Invoke the uncaught exception
        // handler System.exit() is another
        // choice.
    }


    private boolean waitForState(int state) {
        synchronized (waitSync) {
            try {
                while (p.getState() != state){
                    this.waitSync.wait(100);
                }
            }
            catch (Exception e) {
            }
        }
        return stateTransitionOK;
    }



    /**
     * Create a list of all supported formats in readableformat.
     * @param p Processor
     * @return ArrayList
     */
    private ArrayList getMediaFormats(Processor p) throws Exception{
        log.info("getMediaFormats()");
        ArrayList arliResult = new ArrayList();

        // Obtain the track controls.
        TrackControl tc[] = p.getTrackControls();

        if (tc == null) {
            throw new Exception("Failed to obtain track controls from the processor");
        }

        // Search for the track control for the video track.
        TrackControl videoTrack = null;
        TrackControl audioTrack = null;

        for (int i = 0; i < tc.length; i++) {
            if (tc[i].getFormat() instanceof VideoFormat) {
               videoTrack = tc[i];
               VideoFormat vf = (VideoFormat) videoTrack.getFormat();

                log.info("VideoTrack Format: " + videoTrack.getFormat() );
                arliResult.add("codec=" + vf.getEncoding());
                arliResult.add("dimension=" + new Double(vf.getSize().getWidth()).intValue() +
                    "x" + new Double(vf.getSize().getHeight()).intValue());
                arliResult.add("framerate=" + vf.getFrameRate());
            }
            if (tc[i].getFormat() instanceof AudioFormat) {
                audioTrack = tc[i];
                log.info("AudioTrack Format: " + audioTrack.getFormat() );
                AudioFormat af = (AudioFormat) audioTrack.getFormat();
                if(af.getChannels() > 1){
                    arliResult.add("stereo/mono=stereo(" + af.getChannels() + " channels)");
                }
                else{
                    arliResult.add("stereo/mono=mono");
                }
            }
        }
        return arliResult;
    }



    /**
     * Calculates mediaDuration and converts it to the human readable format
     * @param p Processor
     * @throws Exception
     * @return String
     */
    private String getDuration(Processor p) throws Exception{
        log.info("getDuration()");
        String sResult;
        p.start();
        if(p.getDuration() == p.DURATION_UNKNOWN){
            sResult = "playtime=unsupported";
        }
        else{
            double dPlayTime = p.getDuration().getSeconds();

            int iMinutes = (new Double(dPlayTime / 60)).intValue();
            int iSeconds = new Double(dPlayTime - iMinutes * 60).intValue();
            String sPlayTime = "" + iMinutes + ":";
            if(iSeconds < 10){
               sPlayTime += "0";
            }
            sPlayTime += iSeconds;
            sResult = "playtime=" + sPlayTime;
        }
        p.stop();

        return sResult;
    }




    /**
     * Creates a new processor for the specific URL
     * @param url URL Media file
     * @throws Exception something wrong with processor
     * @return Processor
     */
    private Processor createProcessor(URL url) throws Exception{
        MediaLocator ml = new MediaLocator(url);

        p = Manager.createProcessor(ml);
        p.configure();

        if (!waitForState(p.Configured)) {
            throw new Exception("Failed to configure the media processor.");
        }

        p.setContentDescriptor(null);
        p.addControllerListener(this);
        return p;
    }
}
