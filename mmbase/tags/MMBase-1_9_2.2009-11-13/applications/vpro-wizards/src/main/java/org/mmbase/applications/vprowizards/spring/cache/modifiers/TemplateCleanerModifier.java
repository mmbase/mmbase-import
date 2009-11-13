package org.mmbase.applications.vprowizards.spring.cache.modifiers;

import org.mmbase.applications.vprowizards.spring.cache.Modifier;
import org.mmbase.applications.vprowizards.spring.cache.template.MultiTemplateParser;

public class TemplateCleanerModifier implements Modifier {

    public Modifier copy() {
        return new TemplateCleanerModifier();
    }

    public String modify(String input) {
        return MultiTemplateParser.stripTemplatesLeaveNodeNr(input);
    }

}
