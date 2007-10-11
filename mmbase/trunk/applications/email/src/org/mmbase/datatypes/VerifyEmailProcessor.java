/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.datatypes;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Base64;
import org.mmbase.datatypes.processors.*;
import java.util.*;
import java.text.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Using this class a Processor and as CommitProcessor on a certain field, adds 'email verification'
 * functionality for another field.
 *
 * If this other field is changed, then this change will be ignored but in stead be stored on this
 * 'verify email' field, together with a verification key.
 *
 * The verification can then be done by setting this key in this verify field (so, over the
 * implicitely stored value). If the key is then the same as already stored, then the new email
 * value is stored in the actual email field, and the verify field is emptied.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: VerifyEmailProcessor.java,v 1.1 2007-10-11 17:47:50 michiel Exp $

 */

public class VerifyEmailProcessor implements CommitProcessor, Processor, java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(VerifyEmailProcessor.class);
    private static final long serialVersionUID = 1L;

    private static final String SEP = ":";

    private static final String ENCRYPT_IMPL = "PBEWithMD5AndDES";
    private static final PBEParameterSpec PBE_PARAM_SPEC;
    private static SecretKey pbeKey = null;
    static {
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        int iterationCount = 20;


        // Create PBE parameter set
        PBE_PARAM_SPEC = new PBEParameterSpec(salt, iterationCount);
    }

    private static long startTime = System.currentTimeMillis();

    private static int key = 0;

    private String emailField   = "email";
    private String statusField  = null;
    private String emailTextBundle = "org.mmbase.datatypes.resources.verifyemailtemplate";


    public void setEmailField(String ef) {
        emailField = ef;
    }
    public void setStatusField(String sf) {
        statusField = sf;
    }
    public void setTextBundle(String b) {
        emailTextBundle = b;
    }

    public void setReplyTo(String b) {
        emailTextBundle = b;
    }


    public static void setupEncryption() {
        if (pbeKey == null) {
            String password = null;
            try {
                password = ContextProvider.getDefaultCloudContext().getModule("sendmail").getProperty("verify_email.password");
            } catch (Exception e) {
                log.error(e);
            }
            if (password == null) password = "secret";
            try {
                PBEKeySpec pbeKeySpec   = new PBEKeySpec(password.toCharArray());
                SecretKeyFactory keyFac = SecretKeyFactory.getInstance(ENCRYPT_IMPL);
                pbeKey = keyFac.generateSecret(pbeKeySpec);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
    public static String encrypt(String string) {
        setupEncryption();
        try {
            Cipher pbeCipher = Cipher.getInstance(ENCRYPT_IMPL);
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, PBE_PARAM_SPEC);
            Base64 base64 = new Base64();
            return base64.transform(pbeCipher.doFinal(string.getBytes()));
        } catch (Exception e) {
            log.error(e);
            return string;
        }
    }
    public static String decrypt(String string) {
        setupEncryption();
        try {
            Cipher pbeCipher = Cipher.getInstance(ENCRYPT_IMPL);
            pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, PBE_PARAM_SPEC);
            Base64 base64 = new Base64();
            return new String(pbeCipher.doFinal(base64.transformBack(string)));
        } catch (Exception e) {
            log.error(e);
            return string;
        }
    }


    protected String generateKey() {
        //todo:  perhaps something less obvious
        return "" + (startTime + key++);
    }
    public Object process(Node node, Field field, Object value) {
        String checkValue = node.getStringValue(field.getName());
        if (checkValue != null) {
            int pos = checkValue.indexOf(SEP);
            if (pos > 0) {
                String key   = checkValue.substring(0, pos);
                String email = checkValue.substring(pos + 1);
                if (value.equals(key)) {
                    node.setStringValue(emailField, email);
                    log.service("Verification of " + email + " succeeded");
                    return null;
                } else {
                    log.service("Verification of " + email + " failed (" + value + "!=" + key + ")");
                }
            }
        }
        return checkValue;
    }

    protected String encryptKey(Node node, Field field, String key) {
        return encrypt(node.getNodeManager().getName() + SEP + field.getName() + SEP + key);
    }
    public static boolean validate(Cloud cloud, String encryptedKey) {
        String keyChain = decrypt(encryptedKey);
        int pos1 = keyChain.indexOf(SEP);
        String nodeManager = keyChain.substring(0, pos1);
        int pos2 = keyChain.indexOf(SEP, pos1 + 1);
        String field = keyChain.substring(pos1 + 1, pos2);
        String fieldValue = keyChain.substring(pos2 + 1);
        int pos3 = keyChain.indexOf(SEP, pos2 + 1);
        String key = keyChain.substring(pos2 + 1, pos3);
        Node node = SearchUtil.findNode(cloud, nodeManager, field, key);
        if (node != null) {
            node.setStringValue(field, key);
            node.commit();
            return true;
        }
        return false;
    }

    public void commit(Node node, Field field) {
        log.info("Commit for " + node + " " + emailField + " " + node.getChanged());
        if ((node.getChanged().contains(emailField) && ! node.getChanged().contains(field.getName())) || node.isNew()) {
            String email = node.getStringValue(emailField);
            log.service("Sending confirmation email to '" + email + "'");
            String key = generateKey();
            String encryptedKey = encryptKey(node, field, key);

            node.setValueWithoutProcess(field.getName(), key + SEP + email);
            Object originalValue = node.isNew() ? null :
                org.mmbase.module.core.MMBase.getMMBase().getBuilder("object").getNode(node.getNumber()).getValues().get(emailField);
            log.debug("Found original value " + originalValue);
            if (originalValue != null) {
                node.setValueWithoutProcess(emailField, originalValue);
            }
;
            log.debug("Setting " + key + SEP + email + " in " + field);


            Cloud cloud = node.getCloud();

            // Send an email.
            Locale locale = cloud.getLocale();

            ResourceBundle emailTemplate = ResourceBundle.getBundle(emailTextBundle, locale);

            Module emailModule   = cloud.getCloudContext().getModule("sendmail");


            NodeManager emailBuilder = cloud.getNodeManager(emailModule.getProperty("emailbuilder"));

            Node emailNode = emailBuilder.createNode();



            String toField       = emailModule.getProperty("emailbuilder.tofield");
            String subjectField  = emailModule.getProperty("emailbuilder.subjectfield");
            String bodyField     = emailModule.getProperty("emailbuilder.bodyfield");
            String fromField     = emailModule.getProperty("emailbuilder.fromfield");

            emailNode.setStringValue(toField, email);
            emailNode.setStringValue(bodyField, MessageFormat.format(emailTemplate.getString("body"), encryptedKey));

            emailNode.setStringValue(subjectField, MessageFormat.format(emailTemplate.getString("subject"), encryptedKey));

            String from = emailTemplate.getString("from");
            emailNode.setStringValue(fromField, from);

            emailNode.commit();
            try {
                Function mailFunction = emailNode.getFunction("startmail");
                mailFunction.getFunctionValue(null);
            } catch (NotFoundException nfe) {
                log.debug("No function 'startmail', assuming that the mail builder mailed on commit");
            }
        }

    }


    public static void main(String[] argv) throws Exception {

        String encrypted = encrypt(argv[0]);
        System.out.println(encrypted);
        System.out.println(decrypt(encrypted));

    }

}
