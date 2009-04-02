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

public class DerivedDateProcessorFactory implements ParameterizedCommitProcessorFactory {

   private static final String FIELD = "field";
   private static final String OFFSET = "offset";


   public CommitProcessor createProcessor(Parameters parameters) {
      String field = (String) parameters.get(FIELD);
      String offset = (String) parameters.get(OFFSET);
      return new DerivedDateProcessor(field, offset);
   }


   public Parameters createParameters() {
      Parameter[] PARAMS = new Parameter[] { new Parameter(FIELD, String.class, true),
            new Parameter(OFFSET, String.class, true) };
      return new Parameters(PARAMS);
   }

}
