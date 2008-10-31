/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.pluto.portalImpl.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.util.StringUtils;

public class PortalControlParameter {

   static public final String ACTION = "ac";

   static public final String MODE = "md";

   static public final String PORTLET_ID = "pid";

   static public final String PREFIX = "_";

   static public final String PREV_MODE = "pm";

   static public final String PREV_STATE = "ps";

   static public final String RENDER_PARAM = "rp";

   static public final String STATE = "st";

   private Map<String, String[]> requestParameter = new HashMap<String, String[]>();

   /**
    * The map containing the encoded statefull control parameters. They are
    * encoded in the sense, that names and values of <i>render parameters</i>
    * and render parameters only are encoded using {{@link #encodeRenderParamName(PortletWindow, String)}}
    * and {{@link #encodeRenderParamValues(String[])}}.
    */
   private Map<String, Object> encodedStateFullControlParameter;

   private Map<String, String> stateLessControlParameter;

   private PortalURL url;


   public PortalControlParameter(PortalURL url) {
      this.url = url;
      encodedStateFullControlParameter = this.url.getClonedEncodedStateFullControlParameter();
      stateLessControlParameter = this.url.getClonedStateLessControlParameter();
   }


   /**
    * Encodes the given String. Encoding means that all characters that might
    * interfere with the algorithm used to prefix parameters to associate them
    * with the correct portal window will be encoded. The reverse method is {{@link #decodeString(String)}}.
    * 
    * @param value
    *           The String to be encoded.
    * @return The encoded String.
    * @see #decodeParameterName(String)
    */
   private static String encodeString(String value) {
      value = StringUtils.replace(value, "0x", "0xx");
      value = StringUtils.replace(value, "_", "0x1");
      value = StringUtils.replace(value, ".", "0x2");
      value = StringUtils.replace(value, "/", "0x3");
      value = StringUtils.replace(value, "\r", "0x4");
      value = StringUtils.replace(value, "\n", "0x5");
      value = StringUtils.replace(value, "<", "0x6");
      value = StringUtils.replace(value, ">", "0x7");
      value = StringUtils.replace(value, " ", "0x8");
      return value;
   }


   /**
    * Decodes the given String. This is the reverse method to {{@link #encodeString(String)}}.
    * 
    * @param value
    *           The string to be decoded.
    * @return The decoded String.
    */
   private static String decodeString(String value) {
      value = StringUtils.replace(value, "0x1", "_");
      value = StringUtils.replace(value, "0x2", ".");
      value = StringUtils.replace(value, "0x3", "/");
      value = StringUtils.replace(value, "0x4", "\r");
      value = StringUtils.replace(value, "0x5", "\n");
      value = StringUtils.replace(value, "0x6", "<");
      value = StringUtils.replace(value, "0x7", ">");
      value = StringUtils.replace(value, "0x8", " ");
      value = StringUtils.replace(value, "0xx", "0x");
      return value;
   }


   /**
    * Each parameter is encoded by prefixing it with the String {@link #PREFIX}.
    * The reverse method is {{@link #decodeParameterName(String)}}. Don't
    * mistake this method for {{@link #encodeRenderParamName(PortletWindow, String)}}
    * or {{@link #encodeRenderParamValues(String[])}}.
    * 
    * @param param
    *           The parameter to be encoded / prefixed.
    * @return The encoded parameter.
    */
   public static String encodeParameterName(String param) {
      return PREFIX + param;
   }


   /**
    * Decodes a parameter by deleting the prefix, if the parameter was prefixed.
    * Reverse method to {{@link #encodeParameterName(String)}}. Don't mistake
    * this method for {{@link #decodeRenderParameterName(String)}} or {{@link #decodeRenderParamValues(String)}}.
    * 
    * @param param
    *           The parameter to be decoded.
    * @return The decoded parameter.
    */
   public static String decodeParameterName(String param) {
      if (param.startsWith(PREFIX)) {
         return param.substring(PREFIX.length());
      }
      else {
         return param;
      }
   }


   /**
    * Dummy method. Does nothing!
    */
   public static String decodeParameterValue(String paramName, String paramValue) {
      return paramValue;
   }


   /**
    * Encodes the given render parameter name. The name will be encoded using
    * the {{@link #encodeValue(String)}} method, meaning that characters that
    * will interfere with plutos internal url encoding and decoding mechanisms,
    * like "/" or "_" will be encoded. The parameter name will then be prefixed
    * with a string that encodes the portlet window the parameter belongs to.
    * This prefix contains the characters ("/", "_", ...) that had to be encoded
    * in the parameter name to later allow for a safe parsing of the prefix.
    * 
    * @return A string encoding the given render parameter name to be used in
    *         portal urls.
    */
   public static String encodeRenderParamName(PortletWindow window, String paramName) {
      String renderParamKey = getRenderParamKey(window);
      return encodeRenderParamNameInternal(renderParamKey, paramName);
   }


   public static String encodeRenderParamName(String windowid, String paramName) {
      String renderParamKey = getRenderParamKey(windowid);
      return encodeRenderParamNameInternal(renderParamKey, paramName);

   }


   private static String encodeRenderParamNameInternal(String renderParamKey, String paramName) {
      String encodedParamName = encodeString(paramName);
      StringBuffer returnvalue = new StringBuffer(50);
      returnvalue.append(renderParamKey);
      returnvalue.append("_");
      returnvalue.append(encodedParamName);
      return returnvalue.toString();
   }


   /**
    * Reverse method for method {{@link #encodeRenderParamName(PortletWindow, String)}}.
    */
   public static String decodeRenderParamName(PortletWindow window, String encodedRenderParamName) {
      String prefix = getRenderParamKey(window);
      String unprefixedRenderParamName = null;
      if (encodedRenderParamName.startsWith(prefix)) {
         unprefixedRenderParamName = encodedRenderParamName.substring(prefix.length());
      }
      else {
         unprefixedRenderParamName = encodedRenderParamName;
      }
      return decodeString(unprefixedRenderParamName);
   }


   /**
    * Encodes the given render parameter values. The values are encoded in one
    * single string that will be used in portal urls.
    * 
    * @param paramValues
    *           The render parameter values to be encoded.
    * @return A string containing the encoded render parameter values.
    */
   public static String encodeRenderParamValues(String[] paramValues) {
      StringBuffer returnvalue = new StringBuffer(100);
      returnvalue.append(paramValues.length);
      for (String element : paramValues) {
         returnvalue.append("_");
         if (element != null) {
            returnvalue.append(encodeString(element));
         }
      }
      return returnvalue.toString();
   }


   /**
    * Reverse method for the method {{@link #encodeRenderParamValues(String[])}}.
    */
   private static String[] decodeRenderParamValues(String encodedParamValues) {
      StringTokenizer tokenizer = new StringTokenizer(encodedParamValues, "_");
      if (!tokenizer.hasMoreTokens()) {
         return null;
      }
      String _count = tokenizer.nextToken();
      int count = Integer.valueOf(_count).intValue();
      String[] values = new String[count];
      for (int i = 0; i < count; i++) {
         if (!tokenizer.hasMoreTokens()) {
            return null;
         }
         values[i] = decodeString(tokenizer.nextToken());
      }
      return values;
   }


   /**
    * Retrieve the key to use to prefix render parameters of the given portlet
    * window.
    * 
    * @param window
    * @return
    */
   public static String getRenderParamKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getRenderParamKey(windowid);
   }


   private static String getRenderParamKey(String windowid) {
      return RENDER_PARAM + "_" + windowid;
   }


   /**
    * Check whether the given string encodes a control parameter.
    */
   public static boolean isControlParameter(String param) {
      return param.startsWith(PREFIX);
   }


   /**
    * Check whether the given string encodes a stateful parameter, i.e. mode,
    * previous mode, window state, previous window state or render parameter.
    */
   public static boolean isStateFullParameter(String param) {
      if (isControlParameter(param)) {
         if ((param.startsWith(PREFIX + MODE)) || (param.startsWith(PREFIX + PREV_MODE))
               || (param.startsWith(PREFIX + STATE)) || (param.startsWith(PREFIX + PREV_STATE))
               || (param.startsWith(PREFIX + RENDER_PARAM))) {
            return true;
         }
      }
      return false;
   }


   /**
    * Deletes all render parameter that belong to the given window.
    */
   public void clearRenderParameters(PortletWindow portletWindow) {
      String prefix = getRenderParamKey(portletWindow);
      Iterator<String> keyIterator = encodedStateFullControlParameter.keySet().iterator();
      while (keyIterator.hasNext()) {
         String encodedName = keyIterator.next();
         if (encodedName.startsWith(prefix)) {
            keyIterator.remove();
         }
      }
   }


   private String getActionKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getActionKey(windowid);
   }


   private String getActionKey(String windowid) {
      return ACTION + "_" + windowid;
   }


   public String[] getActionParameter(PortletWindow window, String paramName) {
      String encodedValues = (String) encodedStateFullControlParameter.get(encodeRenderParamName(window, paramName));
      String[] decodedValues = decodeRenderParamValues(encodedValues);
      return decodedValues;
   }


   public PortletMode getMode(PortletWindow window) {
      String mode = (String) encodedStateFullControlParameter.get(getModeKey(window));
      if (mode != null) {
         return new PortletMode(mode);
      }
      else {
         return PortletMode.VIEW;
      }
   }


   private String getModeKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getModeKey(windowid);
   }


   private String getModeKey(String windowid) {
      return MODE + "_" + windowid;
   }


   public String getPIDValue() {
      String value = stateLessControlParameter.get(getPortletIdKey());
      return value == null ? "" : value;
   }


   private String getPortletIdKey() {
      return PORTLET_ID;
   }


   public String getPortletWindowOfAction() {
      String id = null;
      Iterator<String> iterator = getStateLessControlParameter().keySet().iterator();
      while (iterator.hasNext()) {
         String name = iterator.next();
         if (name.startsWith(ACTION)) {
            id = name.substring(ACTION.length() + 1);
         }
      }
      return id;
   }


   public PortletMode getPrevMode(PortletWindow window) {
      String mode = (String) encodedStateFullControlParameter.get(getPrevModeKey(window));
      if (mode != null) {
         return new PortletMode(mode);
      }
      else {
         return null;
      }
   }


   private String getPrevModeKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getPrevModeKey(windowid);
   }


   private String getPrevModeKey(String windowid) {
      return PREV_MODE + "_" + windowid;
   }


   public WindowState getPrevState(PortletWindow window) {
      String state = (String) encodedStateFullControlParameter.get(getPrevStateKey(window));
      if (state != null)
         return new WindowState(state);
      else
         return null;
   }


   private String getPrevStateKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getPrevStateKey(windowid);
   }


   private String getPrevStateKey(String windowid) {
      return PREV_STATE + "_" + windowid;
   }


   public Iterator<String> getRenderParamNames(PortletWindow window) {
      ArrayList<String> returnvalue = new ArrayList<String>();
      String prefix = getRenderParamKey(window);
      Iterator<String> keyIterator = encodedStateFullControlParameter.keySet().iterator();
      while (keyIterator.hasNext()) {
         String encodedName = keyIterator.next();
         if (encodedName.startsWith(prefix)) {
            // remove specific render parameter name encoding
            String decodedName = decodeRenderParamName(window, encodedName);
            // remove general parameter encoding
            String unprefixedName = decodeParameterName(decodedName);
            returnvalue.add(unprefixedName);
         }
      }
      return returnvalue.iterator();
   }


   public String[] getRenderParamValues(PortletWindow window, String paramName) {
      String encodedValues = (String) encodedStateFullControlParameter.get(encodeRenderParamName(window, paramName));
      String[] decodedValues = decodeRenderParamValues(encodedValues);
      return decodedValues;
   }


   public Map<String, String[]> getRequestParameter() {
      return requestParameter;
   }


   public WindowState getState(PortletWindow window) {
      String state = (String) encodedStateFullControlParameter.get(getStateKey(window));
      if (state != null)
         return new WindowState(state);
      else
         return WindowState.NORMAL;
   }


   public Map<String, Object> getEncodedStateFullControlParameter() {
      return encodedStateFullControlParameter;
   }


   private String getStateKey(PortletWindow window) {
      String windowid = window.getId().toString();
      return getStateKey(windowid);
   }


   private String getStateKey(String windowid) {
      return STATE + "_" + windowid;
   }


   public Map<String, String> getStateLessControlParameter() {
      return stateLessControlParameter;
   }


   public boolean isOnePortletWindowMaximized() {
      Iterator<String> iterator = encodedStateFullControlParameter.keySet().iterator();
      while (iterator.hasNext()) {
         String encodedName = iterator.next();
         if (encodedName.startsWith(STATE)) {
            if (encodedStateFullControlParameter.get(encodedName).equals(WindowState.MAXIMIZED.toString())) {
               return true;
            }
         }
      }
      return false;
   }


   public void setAction(PortletWindow window) {
      String actionKey = getActionKey(window);
      setActionInternal(actionKey);
   }


   public void setAction(String window) {
      String actionKey = getActionKey(window);
      setActionInternal(actionKey);
   }


   private void setActionInternal(String actionKey) {
      getEncodedStateFullControlParameter().put(actionKey, ACTION.toUpperCase());
   }


   public void setMode(PortletWindow window, PortletMode mode) {
      String modeKey = getModeKey(window);
      String prevModeKey = getPrevModeKey(window);
      setModeInternal(mode, modeKey, prevModeKey);
   }


   public void setMode(String window, PortletMode mode) {
      String modeKey = getModeKey(window);
      String prevModeKey = getPrevModeKey(window);
      setModeInternal(mode, modeKey, prevModeKey);
   }


   private void setModeInternal(PortletMode mode, String modeKey, String prevModeKey) {
      Object prevMode = encodedStateFullControlParameter.get(modeKey);
      if (prevMode != null)
         encodedStateFullControlParameter.put(prevModeKey, prevMode);
      // set current mode
      encodedStateFullControlParameter.put(modeKey, mode.toString());
   }


   public void setPortletId(PortletWindow window) {
      String windowid = window.getId().toString();
      setPortletId(windowid);
   }


   public void setPortletId(String windowid) {
      getEncodedStateFullControlParameter().put(getPortletIdKey(), windowid);
      // getStateLessControlParameter().put(getPortletIdKey(),window.getId().toString());
   }


   /**
    * Sets the given render parameter. Note that its name as well as its values
    * will be encoded for storage using {{@link #encodeRenderParamName(PortletWindow, String)}}
    * and {{@link #encodeRenderParamValues(String[])}.
    */
   public void setRenderParam(PortletWindow window, String name, String[] values) {
      encodedStateFullControlParameter.put(encodeRenderParamName(window, name), encodeRenderParamValues(values));
   }


   public void setRenderParam(String window, String name, String[] values) {
      encodedStateFullControlParameter.put(encodeRenderParamName(window, name), encodeRenderParamValues(values));
   }


   public void setRequestParam(String name, String[] values) {
      requestParameter.put(name, values);
   }


   public void setState(PortletWindow window, WindowState state) {
      String stateKey = getStateKey(window);
      String prevStateKey = getPrevStateKey(window);
      setStateInternal(state, stateKey, prevStateKey);
   }


   public void setState(String window, WindowState state) {
      String stateKey = getStateKey(window);
      String prevStateKey = getPrevStateKey(window);
      setStateInternal(state, stateKey, prevStateKey);
   }


   private void setStateInternal(WindowState state, String stateKey, String prevStateKey) {
      Object prevState = encodedStateFullControlParameter.get(stateKey);
      if (prevState != null) {
         encodedStateFullControlParameter.put(prevStateKey, prevState);
      }
      encodedStateFullControlParameter.put(stateKey, state.toString());
   }
}
