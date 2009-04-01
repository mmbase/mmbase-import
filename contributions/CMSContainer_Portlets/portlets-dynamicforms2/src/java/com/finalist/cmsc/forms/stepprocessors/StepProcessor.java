package com.finalist.cmsc.forms.stepprocessors;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.forms.value.ValueForm;
import com.finalist.cmsc.forms.value.ValueObject;


public abstract class StepProcessor {

   public abstract ProcessorResult processStep(ValueObject valueObject, Map<String, List<String>> parameters,
         ValueForm valueForm);

}
