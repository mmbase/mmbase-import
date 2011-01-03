/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009-2010 André van Toly, Michiel Meeuwissen

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

import java.io.File;

import org.mmbase.streams.transcoders.*;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.security.ActionRepository;
import org.mmbase.util.logging.*;

/**
 * 'Pings' the physical file of a streamssources (video- or audiostream) with 
 * {@link org.mmbase.streams.transcoders.FFMpegRecognizer} to update current information
 * in its node.
 * 
 * @author Andr&eacute; van Toly
 * @version $Id: UpdateSourcesFunction.java 43186 2010-08-28 14:48:27Z andre $
 */

public class UpdateSourcesFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(UpdateSourcesFunction.class);
    private ChainedLogger log = new ChainedLogger(LOG);

    public final static Parameter[] PARAMETERS = { 
        new Parameter("cache", org.mmbase.bridge.Node.class)
    };
    public UpdateSourcesFunction() {
        super("updatesources", PARAMETERS);
    }

    @Override
    protected Boolean getFunctionValue(final Node source, final Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }
        if (source.getNumber() > 0 
                && source.getCloud().may(ActionRepository.getInstance().get("streams", "update_sources"), null)) {
        
            Node cache = (Node) parameters.get("cache");
            
            final Field urlField = source.getNodeManager().getField("url");
            final org.mmbase.streams.createcaches.Processor cc = CreateCachesFunction.getCacheCreator(urlField);
            
            String url = source.getStringValue("url");
            if (cache == null || !source.getCloud().hasNode(cache.getNumber())) {
                cache = null;
            } else {
                url = cache.getStringValue("url");
            }
            
            File f = new File(cc.getDirectory(), url);
            
            try {
                Recognizer recognizer = new FFMpegRecognizer().clone();
                
                FFMpegAnalyzer a = new FFMpegAnalyzer();
                if (cache != null) {
                    a.setUpdateDestination(true);
                } else {
                    a.setUpdateSource(true);
                }
                
                ChainedLogger chain = new ChainedLogger(log);
                chain.addLogger(new AnalyzerLogger(a, source, cache));
                
                recognizer.analyze(f.toURI(), chain);
                a.ready(source, cache);
                
                if (log.isDebugEnabled()) {
                    log.debug("source: " + source);
                    log.debug(" cache: " + cache);
                }
                
                return true;
                
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return false;
            }
        } else {
            return false;
        }
    } 

}
