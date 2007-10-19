/**
 * Copyright 2001-2003 Antonio W. Lagnada.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The name "Antonio W. Lagnada" must not be used to endorse or 
 *    promote products derived from this software without prior
 *    written permission. For written permission,
 *    please contact tony@lagnada.com
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ANTONIO W. LAGNADA OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 */
package nl.leocms.evenementen.forms;

import java.io.Serializable;

import nl.leocms.evenementen.forms.HtmlButton;

/**
 * SubscribeButtons
 */
public class EvenementButtons implements Serializable {

    protected HtmlButton showPastDates = new HtmlButton();
    
    public EvenementButtons() { }

    public HtmlButton getShowPastDates() { return showPastDates; }
    public void setShowPastDates(HtmlButton showPastDates) {  this.showPastDates = showPastDates; }

}


