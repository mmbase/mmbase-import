package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import nl.didactor.component.Component;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.module.core.*;
import nl.didactor.education.builders.QuestionBuilder;

/**
 * ScoreTag: retrieve a score for a given answer
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ScoreTag extends CloudReferrerTag { 
    private Attribute answer = Attribute.NULL;
    private Attribute question = Attribute.NULL;

    public void setQuestion(String question) throws JspTagException {
        this.question = getAttribute(question);
    }

    public void setAnswer(String answer) throws JspTagException {
        this.answer = getAttribute(answer);
    }

    public int doStartTag() throws JspTagException {
        Cloud cloud = getCloudVar();
        if (cloud == null) {
            throw new JspTagException("No parent <mm:cloud> tag found");
        }

        Node answerNode = cloud.getNode(answer.getString(this));
        if (answerNode == null) {
            throw new JspTagException("Cannot get node with number '" + answer.getString(this) + "'");
        }

        Node questionNode = cloud.getNode(question.getString(this));
        if (questionNode == null) {
            throw new JspTagException("Cannot get node with number '" + answer.getString(this) + "'");
        }

        MMObjectBuilder questionBuilder = MMBase.getMMBase().getBuilder(questionNode.getNodeManager().getName());
        if (questionBuilder == null) {
            throw new JspTagException("questionBuilder is null");
        }
        if (!(questionBuilder instanceof QuestionBuilder)) {
            throw new JspTagException("Builder [" + questionBuilder + "] is not a QuestionBuilder builder");
        }

        MMObjectBuilder answerBuilder = MMBase.getMMBase().getBuilder(answerNode.getNodeManager().getName());
        if (answerBuilder == null) {
            throw new JspTagException("answerBuilder is null");
        }

        String value = "" + ((QuestionBuilder)questionBuilder).getScore(
            questionBuilder.getNode(questionNode.getNumber()), 
            answerBuilder.getNode(answerNode.getNumber()));
    
        try {
            pageContext.getOut().print(value);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }
        return SKIP_BODY;
    }
}
