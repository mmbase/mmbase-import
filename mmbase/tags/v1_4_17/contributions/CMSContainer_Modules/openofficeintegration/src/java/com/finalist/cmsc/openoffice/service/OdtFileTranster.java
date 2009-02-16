package com.finalist.cmsc.openoffice.service;

import com.finalist.cmsc.openoffice.model.OdtDocument;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OdtFileTranster {

    private static Log log = LogFactory.getLog(OdtFileTranster.class);

    public static String WORKINGFOLDER;

    public static OdtDocument process(File file, String requestContext) {

        String middelFileLocation = WORKINGFOLDER + File.separator + file.getName() + ".xml";

        ChangeContentXml ccx = new ChangeContentXml();
        ChangeHtml ch = new ChangeHtml();
        OutFinishHtml ofh = new OutFinishHtml(file.getPath(), middelFileLocation);

        HashMap styleMap = ccx.getStyleMap(ccx.getContentStyle(file.getPath()));
        StringBuffer buffer = new StringBuffer();

        try {
            File workingfolder = new File(WORKINGFOLDER);
            if (!workingfolder.exists()) {
                workingfolder.mkdir();
            }

            ofh.getFirstHtml();
            Map rowAndSavedImageMap = saveAllImageToCMSC(new FileInputStream(file), requestContext);
            ch.change(middelFileLocation, styleMap, rowAndSavedImageMap);
            FileInputStream fileInputStream = new FileInputStream(middelFileLocation + ".html");
            InputStreamReader inputStramReader = new InputStreamReader(fileInputStream, "utf-8");

            BufferedReader in = new BufferedReader(inputStramReader);
            String str;
            while ((str = in.readLine()) != null) {
            	String newStr = str.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
            	buffer.append(newStr);			  
            }
            in.close();
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
            log.error(e);
        } catch (SAXException e) {
            e.printStackTrace();
            log.error(e);
        }


        OdtDocument doc = new OdtDocument();
        doc.setTitle(file.getName());
        doc.setBody(buffer.toString());
        return doc;
    }

    public static Map saveAllImageToCMSC(InputStream fis, String requestContext) throws IOException {

        Map<String, String> rawImgeToSavedImgeMaping = new HashMap();

        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            String entryName = ze.getName();
            if (entryName.startsWith("Pictures/")) {
                String numbersOfSavedImage = saveAImage(zis, entryName);
                rawImgeToSavedImgeMaping.put(entryName, requestContext + numbersOfSavedImage);
            }
            zis.closeEntry();
        }

        zis.close();
        return rawImgeToSavedImgeMaping;
    }

    private static String saveAImage(InputStream is, String entryName) throws IOException {
        byte imgdata[] = inputStreamToByte(is);
        CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
        Cloud cloud = cloudProvider.getCloud();
        Node node = cloud.getNodeManager("images").createNode();
        node.setStringValue("title", entryName);
        node.setStringValue("filename", entryName);
        node.setByteValue("handle", imgdata);
        node.commit();

        return Integer.toString(node.getNumber());
    }

    private static byte[] inputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = iStrm.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }
}
