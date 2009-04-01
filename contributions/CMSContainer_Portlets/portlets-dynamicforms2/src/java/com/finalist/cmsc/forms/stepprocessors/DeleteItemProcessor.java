package com.finalist.cmsc.forms.stepprocessors;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.value.*;

public class DeleteItemProcessor extends StepProcessor {

   private final static Log Log = LogFactory.getLog(DeleteItemProcessor.class);

   /**
    * @see com.finalist.cmsc.forms.stepprocessors.StepProcessor#processStep(ValueObject, Map, ValueForm)
    */
   @Override
   public ProcessorResult processStep(ValueObject valueObject, Map<String, List<String>> parameters, ValueForm valueform) {
      List<String> deleteitems = parameters.get("deleteitems");
      List<String> remove = parameters.get("remove");

      if (remove != null && remove.size() > 0 && remove.get(0).equals("true")) {
         if (deleteitems != null && deleteitems.size() > 0) {

            // The deleteitems are path in the valuestructure. Objects will be on a different
            // position when
            // objects earlier in the list are deleted. Looping forward instead of backwards will
            // fail.
            Collections.sort(deleteitems, String.CASE_INSENSITIVE_ORDER);
            for (int i = deleteitems.size() - 1; i >= 0; i--) {
               Log.debug(deleteitems.get(i));
               ValuePathUtil.deleteObjectFromPath(valueObject, deleteitems.get(i));
            }
         }
      }
      return null;
   }

}
