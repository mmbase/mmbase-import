package nl.vpro.redactie.handlers;

import java.io.IOException;
import java.util.Map;

import nl.vpro.redactie.FieldError;
import nl.vpro.redactie.ResultContainer;
import nl.vpro.redactie.actions.Action;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.springframework.web.multipart.MultipartFile;

public abstract class Handler<T extends Action> {
	private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(Handler.class);
	protected Transaction transactionalCloud;
	protected ResultContainer resultContainer;
	protected Map<String,Node> idMap;
	protected T action;

	public Handler(Transaction transactionalCloud, ResultContainer resultContainer, Map<String,Node> idMap) {
		this.transactionalCloud = transactionalCloud;
		this.resultContainer = resultContainer;
		this.idMap = idMap;
	}

	public void process(T action) {
		this.action = action;
		log.debug("Processing: "+action);
		process();
	}

	abstract void process();

	protected String getNumber() {
		if(action.getNumber().size()==0) {
			return null;
		}
		return action.getNumber().toArray()[0].toString();
	}

	protected String getType() {
		if(action.getType().size()==0) {
			return null;
		}
		return action.getType().toArray()[0].toString();
	}

	protected void setId(Node node) {
		if(action.getId().size()==1) {
			String id = action.getId().toArray()[0].toString();
			if(log.isDebugEnabled()){
				log.debug("Register id "+id);
			}
			idMap.put(id,node);
		}
	}

    /**
     * This method sets the handle field of a given node if there is a file upload inside the
     * current action. It also tries to set a number of other fields on the node (if they exist):<br>
     * <ul>
     * <li>filename : the original file name (not the path)</li>
     * <li>filesize </li>
     * <li>size </li>
     * <li>mimetype </li>
     * </ul>
     * Current limitations are:
     * <ul>
     * <li>you can only set one file for an action
     * <li>the field name (handle) is hardcoded.
     * <ul>
     *
     * @param resultContainer errors are added here.
     * @param node the node to manipulate
     * @param changed the default value that will be returned when no changes occured
     * @return true if one or more fields were changed.
     */
    protected boolean setHandlerField(ResultContainer resultContainer, Node node, boolean changed) {
        try {
            MultipartFile file = action.getFile();
            if (action.getFile() != null && !action.getFile().isEmpty()) {
                NodeManager nodeManager = node.getNodeManager();

                node.setByteValue("handle", file.getBytes());
                changed = true;

                // see if we can set a derived filename
                if (nodeManager.hasField("filename")) {

                    String fileName = file.getOriginalFilename();
                    int pos1 = fileName.lastIndexOf("/");
                    int pos2 = fileName.lastIndexOf("\\");
                    int pos = pos1 > pos2 ? pos1 : pos2;
                    if (pos > 0) {
                        fileName = fileName.substring(pos + 1);
                        if ("".equals(fileName)) {
                            fileName = file.getOriginalFilename();
                        }
                    }
                    node.setStringValue("filename", fileName);
                }

                // see if we can set the mimetype
                if (nodeManager.hasField("mimetype")) {
                    String mimetype = file.getContentType();
                    node.setStringValue("mimetype", mimetype);
                }

                //the file size
                long filesize = file.getSize();
                if (nodeManager.hasField("filesize")) {
                    node.setLongValue("filesize", filesize);
                }
                if (nodeManager.hasField("size")) {
                    node.setLongValue("size", filesize);
                }

            }
        } catch (IOException e) {
            FieldError fielderror = new FieldError("file", "" + e);
            log.warn(fielderror);
            resultContainer.getErrors().add(fielderror);
        }
        return changed;
    }
}