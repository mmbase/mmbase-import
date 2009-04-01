package com.finalist.cmsc.forms.stepprocessors;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.forms.value.ValueForm;
import com.finalist.cmsc.forms.value.ValueObject;

public class ItemCompletedProcessor extends StepProcessor {

   @Override
   public ProcessorResult processStep(ValueObject valueObject, Map<String, List<String>> parameters,
         ValueForm valueForm) {
      ValueObject item = valueObject;

      if (!item.isCompleted()) {
         item.setCompleted(true);
      }
      parameters.put("editpath", null);
      return null;

   }

}
