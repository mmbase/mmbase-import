/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;


public class FieldData {
    
    private String name = null;
    
    private String type = null;
    
    private String value = null;
    
    private String oldValue = null;
    
    private BinaryData binary = null;
    
    private final String did  = DataUtils.getDataId();
    
    private BaseData mainObject = null;
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * load value form mmbase, this will reset cached backup value.
     * after load value, isChange() method will return false;
     * @param value The value to set.
     */
    public void loadValue(Object value) {
        if (value==null) {
            this.value = null;
            this.oldValue = this.value;
        } else if (value instanceof Date) {
            Date date = (Date)value;
            this.value = String.valueOf(date.getTime());
            this.oldValue = this.value;
        } else if (value instanceof byte[]) {
            this.binary = new BinaryData();
            this.binary.setData((byte[])value);
            this.oldValue = ""+binary.getVersion();
        } else {
            this.value = value.toString();
            this.oldValue = this.value;
        }
    }
    
    /**
     * set the value of the field
     * @param newValue
     */
    public void setValue(String newValue) {
        this.value = newValue;
    }
    
    /**
     * 
     * @return
     */
    public BinaryData getBinaryData() {
        return this.binary;
    }
    
    /**
     * @param binary The binary to set.
     */
    public void setBinary(BinaryData binary) {
        this.binary = binary;
    }
    
    /**
     * @return Returns the binary.
     */
    private byte[] getBinary() {
        return binary.getData();
    }
    
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 
     * @return
     */
    public String getStringValue() {
        return this.value;
    }
    
    /**
     * 
     * @return
     */
    public Date getDateValue() {
        try {
            long time = Long.parseLong(this.value);
            return new Date(time);
        } catch (Exception e) {
            //TODO: should add error log code
        }
        return null;
    }
    
    public int getIntValue() {
        try {
            int intValue = Integer.parseInt(this.value);
            return intValue;
        } catch (Exception e) {
            //TODO: should add error log code
        }
        return Integer.MIN_VALUE;
    }
    
    /**
     * @return Returns the oldValue.
     */
    public String getOldValue() {
        return oldValue;
    }

    
    /**
     * @return Returns the id.
     */
    public String getDid() {
        return did;
    }

    /**
     * @param mainObject The mainObject to set.
     */
    void setMainObject(BaseData mainObject) {
        this.mainObject = mainObject;
    }

    /**
     * @return Returns the mainObject.
     */
    public BaseData getMainObject() {
        return mainObject;
    }

    /**
     * indicate whether the value of the field was changed after load from mmbase
     * @return true, if the value was changed; false, otherwise.
     */
    public boolean isChanged() {
        switch (this.mainObject.getStatus()) {
            case BaseData.STATUS_NEW :
                return this.getStringValue()!=null || this.getBinary()!=null;
            case BaseData.STATUS_LOAD:
            case BaseData.STATUS_CHANGE:
            default:
                if ("binary".equals(this.type)){
                    // we can determined whether binary field be changed by binaryversion 
                    if (this.binary==null) {
                        return false;
                    }
                    return this.binary.getVersion().equals(this.oldValue)==false;
                }
                if (this.value!=null) {
                    //value != null, compare with old value.
                    return this.value.equals(this.oldValue)==false;
                } else if (this.oldValue!=null) {
                    //value = null; oldvalue!=null
                    return true;
                }
                // value=oldvalue=null
                return false;
        }
    }

    
}
