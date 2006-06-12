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
/* 

 */

package com.finalist.pluto.portalImpl.om.common.impl;

import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class DescriptionImpl implements Description, java.io.Serializable, Support {

	private String description;

	private Locale locale;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	// digester methods
	public void setLanguage(String lang) {
		this.locale = new Locale(lang);
	}
    
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer(50);
        StringUtils.newLine(buffer, indent);
        buffer.append(getClass().toString());
        buffer.append(": description='");
        buffer.append(description);
        buffer.append("', locale='");
        buffer.append(locale);
        buffer.append("'");
        return buffer.toString();
    }

    public void postLoad(Object parameter) throws Exception {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
    }
}
