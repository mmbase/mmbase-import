package com.finalist.cmsc.forms.dataproviders;

import java.util.Map;

import com.finalist.cmsc.forms.value.ValueObject;


public interface DataProvider {

   void createData(ValueObject valueObject, Map<String, String[]> params);

}
