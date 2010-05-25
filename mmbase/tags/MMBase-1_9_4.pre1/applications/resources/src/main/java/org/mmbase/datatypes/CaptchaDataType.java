/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.images.*;


/**

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.3
 */

public class CaptchaDataType extends StringDataType {

    private static final Logger LOG = Logging.getLoggerInstance(CaptchaDataType.class);

    public static final String KEY = CaptchaDataType.class.getName() + ".KEY";

    public static final String CLEAR = CaptchaDataType.class.getName() + ".CLEAR";

    public static final String BASE = "temporary_images";

    protected CaptchaRestriction  captchaRestriction =  new CaptchaRestriction();

    private static final byte[] GIFBYTES = new byte[]{
             0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
             (byte)0x80, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff,
             0x00, 0x00, 0x00, 0x21, (byte)0xf9, 0x04, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00,
             0x01, 0x00, 0x01, 0x00, 0x40, 0x02, 0x02, 0x44, 0x01, 0x00,
             0x3b, 0x0a};

    private static final Timer DELETER = new Timer(true);

    public static class CaptchaImage {
        public File file;
        public final String text;
        public int height;
        public int width;
        public String background = "white";
        public String fillColor = "black";
        public int swirl = 30;
        public String font = null;

        public CaptchaImage(String text) {
            this.text = text;
        }
        // Silly stupid getters are needed in EL. EL sucks.

        public File getFile() {
            return file;
        }
        public String getPath() {
            return org.mmbase.servlet.MMBaseServlet.getBasePath("files") + BASE + "/" + getFile().getName();
        }
        public int getHeight() {
            return height;
        }
        public int getWidth() {
            return width;
        }
        public String getText() {
            return text;
        }

    }
    private static final String[] CHARSET = {"3","4","5","6","7","8", "a","b","c","d","e","f","h","i","j","k","m","n","p","q","r","s","t","u","v","w","x","y"};    // 29
    private static final Random RAND = new Random();
    public static String createString(int length) {
        StringBuilder sb = new StringBuilder();
        for(int n = 0; n < length; n++) {
            sb = sb.append(CHARSET[RAND.nextInt(CHARSET.length)]);
        }
        return sb.toString();
    }


    public static void createCaptchaImage(InputStream input, final CaptchaImage image) throws IOException {
        if (input == null) {
            input = new ByteArrayInputStream(GIFBYTES);
        }
        final File directory =  org.mmbase.servlet.FileServlet.getFile(BASE, null);
        directory.mkdir();
        image.file = File.createTempFile("captcha", ".png", directory);
        image.file.deleteOnExit();
        FileReceiver receiver = new FileReceiver(image.file);
        List<String> commands = new ArrayList<String>();
        if (image.background != null && !image.background.equals("white") && !image.background.equals("#fff")) {
            if ("transparent".equals(image.background)) {
                commands.add("transparent(white)");
            } else {
                commands.add("fill(" + image.background + ")");
                commands.add("draw(color 0,0 reset)");
            }
        }
        commands.add("gravity(west)");
        commands.add("s(80x22!)");
        if (image.font != null) {
            commands.add("font(" + image.font + ")");
        }
        commands.add("fill(" + image.fillColor + ")");
        commands.add("pointsize(20)");
        commands.add("text(0,0,\'" + image.text + "')");
        commands.add("f(png)");
        if (image.swirl != 0) {
            commands.add("swirl(" + image.swirl + ")");
        }
        ImageConversionRequest req =
            Factory.getImageConversionRequest(input, "gif", receiver, commands);

        req.waitForConversion();
        DELETER.schedule(new TimerTask() { public void run() {image.file.delete();} }, 60000);
        Dimension dim = receiver.getDimension();
        image.width  = dim.getWidth();
        image.height = dim.getHeight();
    }

    public CaptchaDataType(String name) {
        super(name);
    }

    @Override
    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node node, Field field) {
        LOG.debug("Validating " + castValue);
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        errors = captchaRestriction.validate(errors, castValue, node, field);
        return errors;
    }


    protected class CaptchaRestriction extends AbstractRestriction<String> {
        private static final long serialVersionUID = 0L;
        CaptchaRestriction(CaptchaRestriction source) {
            super(source);
        }
        CaptchaRestriction() {
            super("captcha", null);
        }
        @Override
        protected String getErrorDescriptionBundle() {
            return "org.mmbase.datatypes.resources.resourcesdatatypes";
        }
        @Override
        protected boolean simpleValid(final Object v, final Node node, final Field field) {
            Cloud cloud = CloudThreadLocal.currentCloud();
            if (cloud == null) {
                LOG.debug("" + v + " is valid because no cloud found");
                return true;
            } else {
                HttpServletRequest req = (HttpServletRequest) cloud.getProperty(Cloud.PROP_REQUEST);
                if (req == null) {
                    LOG.debug("" + v + " is valid because no request found in cloud");
                    return true;
                }
                HttpSession session = req.getSession(false);
                if (session == null) {
                    LOG.debug("" + v + " is invalid because no session found in cloud");
                    return false;
                }
                String mustbe = (String) session.getAttribute(KEY);
                if (mustbe == null) {
                    LOG.debug("" + v + " is invalid because " + KEY + " not found in session");
                    return false;
                }
                LOG.debug("Found " + KEY + " is " + value + " (user provided  '" + v + "')");
                return mustbe.equalsIgnoreCase(Casting.toString(v));
            }
        }
    }


    public static class Handler extends org.mmbase.datatypes.handlers.html.StringHandler {

        private int length = 5;
        private int swirl = 30;
        private String background = "white";
        private String font = null;


        public void setLength(int l) {
            length = l;
        }
        public void setSwirl(int s) {
            swirl = s;
        }

        public void setBackground(String bg) {
            background = bg;
        }

        public void setFont(String f) {
            font = f;
        }

        @Override
        protected void setValue(Request request, Node node, String fieldName, Object value) {
            super.setValue(request, node, fieldName, value);
            if (request.isPost()) {
                HttpServletRequest req = (HttpServletRequest) request.getCloud().getProperty(Cloud.PROP_REQUEST);
                HttpSession session = req.getSession(true);
                session.removeAttribute(CaptchaDataType.KEY);
                LOG.debug("Set value now cleaning from session");
            }
        }

        @Override
        public String input(Request request, Node node, Field field, boolean search)  {
            if (search) {
                return super.input(request, node, field, search);
            } else {
                HttpServletRequest req = (HttpServletRequest) request.getCloud().getProperty(Cloud.PROP_REQUEST);
                HttpSession session = req.getSession(true);
                String t = (String) session.getAttribute(CaptchaDataType.KEY);
                if (t == null ||  request.getCloud().getProperty(CLEAR) != null) {
                    t = createString(length);
                    session.setAttribute(CaptchaDataType.KEY, t);
                    LOG.debug("Created key now setting in session");

                }
                CaptchaImage image = new CaptchaDataType.CaptchaImage(t);
                image.swirl = swirl;
                image.background = background;
                image.font = font;
                StringBuilder show =  new StringBuilder();
                try {
                    CaptchaDataType.createCaptchaImage(null, image);
                    show.append("<img src='");
                    show.append(req.getContextPath());
                    show.append(image.getPath());
                    show.append("' alt='captcha' width='");
                    show.append(image.getWidth());
                    show.append("' height='");
                    show.append(image.getHeight());
                    show.append("' />");
                } catch (Exception e) {
                    LOG.error(e);
                    show.append(e.getMessage());
                }
                show.append(super.input(request, node, field, search));
                return show.toString();
            }
        }

    }

}
