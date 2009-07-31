package org.mmbase.applications.vprowizards.spring.cache.template;

/**
 * A template is a String where certain placeholders can be prepended with (node) numbers.
 * A template parser can perform those operations on a template.
 * @author ebunders
 *
 */
public interface TemplateParser {
    public void insertNodeNumber();
    public void removeNodeNumber();
    public void stripTemplateLeaveNodenr();
    public String getTemplate();
}
