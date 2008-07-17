/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository;

import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.datatypes.processors.ParameterizedCommitProcessorFactory;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

public class ValueBetweenProcessorFactory implements ParameterizedCommitProcessorFactory {

   private static final String FROM = "from";
   private static final String TO = "to";


   public CommitProcessor createProcessor(Parameters parameters) {
      String from = (String) parameters.get(FROM);
      String to = (String) parameters.get(TO);
      return new ValueBetweenProcessor(from, to);
   }


   public Parameters createParameters() {
      Parameter[] PARAMS = new Parameter[] { new Parameter(FROM, String.class, true),
            new Parameter(TO, String.class, true) };
      return new Parameters(PARAMS);
   }

}
