package com.finalist.cmsc.tasks.forms;

import com.finalist.cmsc.struts.MMBaseForm;

public class ShowTaskForm extends MMBaseForm {

	private String taskShowType ="";

	public String getTaskShowType() {
		return taskShowType;
	}

	public void setTaskShowType(String taskShowType) {
		this.taskShowType = taskShowType;
	}
	
}
