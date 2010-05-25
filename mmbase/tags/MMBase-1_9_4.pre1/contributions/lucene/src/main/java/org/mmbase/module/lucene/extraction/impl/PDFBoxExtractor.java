/*
 * MMBase Lucene module
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.module.lucene.extraction.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.mmbase.module.lucene.extraction.Extractor;
import org.mmbase.util.logging.*;

import org.pdfbox.pdmodel.encryption.*;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

/**
 * @author Wouter Heijke
 * @version $Id$
 */
public class PDFBoxExtractor implements Extractor {

    private static final Logger log = Logging.getLoggerInstance(PDFBoxExtractor.class);

    private String mimetype = "application/pdf";

    public PDFBoxExtractor() {
        log.debug("PDFBoxExtractor instance");
    }

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public String extract(InputStream input) throws Exception {
        log.debug("extract stream");
        String result = null;
        PDDocument pdfDocument = null;
        PDFParser parser = null;
        try {
            parser = new PDFParser(input);
            parser.parse();
            pdfDocument = parser.getPDDocument();
            if (pdfDocument.isEncrypted()) {
                StandardSecurityHandler decryptor = new StandardSecurityHandler();
                StandardDecryptionMaterial dm = new StandardDecryptionMaterial("");
                // TODO: password must be configurable
                decryptor.decryptDocument(pdfDocument, dm);
            }

            StringWriter writer = new StringWriter();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(pdfDocument, writer);

            result = writer.getBuffer().toString();

            log.debug("extracted: '" + result.length() + "' bytes");

            writer.close();

            boolean doInfo = false;
            if (doInfo) {
                PDDocumentInformation info = pdfDocument.getDocumentInformation();
                if (info.getAuthor() != null) {
                    // document.add(Field.Text( "Author", info.getAuthor() ) );
                }

                // if (info.getCreationDate() != null) {
                // Date date = info.getCreationDate().getTime();
                // // for some reason lucene cannot handle dates before the
                // // epoch
                // // and throws a nasty RuntimeException, so we will check and
                // // verify that this does not happen
                // if (date.getTime() >= 0) {
                // // document.add(Field.Text("CreationDate",
                // // DateField.dateToString( date ) ) );
                // log.debug("CreationDate:" + date);
                // }
                // }
                if (info.getCreator() != null) {
                    // document.add( Field.Text( "Creator", info.getCreator() )
                    // );
                }
                if (info.getKeywords() != null) {
                    // document.add( Field.Text( "Keywords", info.getKeywords()
                    // ) );
                }
                // if (info.getModificationDate() != null) {
                // Date date = info.getModificationDate().getTime();
                // // for some reason lucene cannot handle dates before the
                // // epoch
                // // and throws a nasty RuntimeException, so we will check and
                // // verify that this does not happen
                // if (date.getTime() >= 0) {
                // // document.add(Field.Text("ModificationDate",
                // // DateField.dateToString( date ) ) );
                // log.debug("ModificationDate:" + date);
                // }
                // }
                if (info.getProducer() != null) {
                    // document.add( Field.Text( "Producer", info.getProducer()
                    // ) );
                }
                if (info.getSubject() != null) {
                    // document.add( Field.Text( "Subject", info.getSubject() )
                    // );
                }
                if (info.getTitle() != null) {
                    // document.add( Field.Text( "Title", info.getTitle() ) );
                }
                if (info.getTrapped() != null) {
                    // document.add( Field.Text( "Trapped", info.getTrapped() )
                    // );
                }
            }
        } catch (IOException e) {
            throw new Exception("PDFBoxExtractor, Error reading document: " + e.getMessage(), e);
        } catch (CryptographyException e) {
            throw new Exception("PDFBoxExtractor, Error decrypting document: " + e.getMessage(), e);
        } finally {
            // cleanup to return clean
            if (pdfDocument != null) {
                // out.close();
                pdfDocument.close();
            }
        }

        return result;
    }

    public static void main(String [] args) throws Exception {
        Extractor e = new PDFBoxExtractor();
        java.io.FileInputStream file = new java.io.FileInputStream(args[0]);
        System.out.println(e.extract(file));
    }

}
