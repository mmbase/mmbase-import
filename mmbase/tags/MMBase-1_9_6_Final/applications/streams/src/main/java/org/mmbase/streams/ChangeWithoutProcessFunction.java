/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009-2011 André van Toly, Michiel Meeuwissen

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

package org.mmbase.streams;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.mmbase.security.ActionRepository;
import org.mmbase.util.logging.*;

/**
 * Function on streamsources nodes to enable editing of the url field without processing (transcoding) the stream.
 * Normally an url is only made once when a stream is uploaded, it gets (newly) transcoded when the url
 * field changes. This makes for a way to change that field without uploading and transcoding.
 * TODO: make this work for streamcaches too.
 * 
 * @author Andr&eacute; van Toly
 * @version $Id$
 */

public class ChangeWithoutProcessFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(ChangeWithoutProcessFunction.class);
    private ChainedLogger log = new ChainedLogger(LOG);

    public final static Parameter[] PARAMETERS = { 
        new Parameter("url", java.lang.String.class)
    };
    public ChangeWithoutProcessFunction() {
        super("changesource", PARAMETERS);
    }

    @Override
    protected Boolean getFunctionValue(final Node source, final Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }
        if (source.getNumber() > 0 
                && source.getCloud().may(ActionRepository.getInstance().get("streams", "update_sources"), null)) {
        
            Cloud cloud = source.getCloud();
            cloud.setProperty(org.mmbase.streams.createcaches.Processor.NOT, "no implicit processing please");

            String url = (String) parameters.get("url");
            source.setValueWithoutProcess("url", url);
            source.commit();
            
            if (log.isDebugEnabled()) {
                LOG.debug("url changed to: " + url);
            }

            return true;

        } else {
            return false;
        }
    } 

}
