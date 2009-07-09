/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.streams.urlcomposers;


import org.mmbase.module.core.*;
import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.*;
import java.util.*;


/**
 *
 * @author Michiel Meeuwissen
 */
public class ImagesURLComposer extends FragmentURLComposer {
    private static final Logger log = Logging.getLoggerInstance(ImagesURLComposer.class);

    String format = "s(100)";
    public void setFormat(String f) {
        format = f ;
    }


    @Override
    protected StringBuilder getURLBuffer() {
        /*
        FileReceiver receiver = new FileReceiver(file);
        //white pixel
        ImageConversionRequest req =
            Factory.getImageConversionRequest(input, "gif", receiver,
                                              "gravity(west)", "s(80x22!)", "fill(" + fillColor + ")", "pointsize(20)", "draw(text 10x10 \'" + captchaKey + "')", "f(png)", "swirl(10)");
        // should also be possible to use 'text', but that is broken with newer image-magicks.

        req.waitForConversion();
        deleter.schedule(new TimerTask() { public void run() {file.delete();} }, 60000);
        */
        // TODO
        return new StringBuilder("http://" + format);

    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            ImagesURLComposer other = (ImagesURLComposer) o;
            return other.format.equals(format);
        }
        return false;
    }


    @Override
    public boolean canCompose() {
        return source.getBuilder().getTableName().equals("imagesources");
    }


    @Override
    public String toString() {
        return super.toString() + " " + format;
    }

}
