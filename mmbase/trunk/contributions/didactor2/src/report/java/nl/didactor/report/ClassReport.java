package nl.didactor.report;
import org.mmbase.bridge.*;
import nl.didactor.util.*;
import nl.didactor.component.education.utils.*;
import java.util.*;

public class ClassReport {
	private Node klass;
	private Node education;
	private Cloud cloud;
	private List studentNodes;
	private List teacherNodes;
	private List testNodes;
	
	public ClassReport(Node klass, Node education) {
		this.klass = klass;
		this.education = education;
		this.studentNodes = ClassRoom.getStudents(klass);
		this.teacherNodes = ClassRoom.getTeachers(klass);
		this.testNodes = TestUtils.getAllTests(education);
	}

	public List getStudentNodes() {
		return studentNodes;
	}

	public List getTeacherNodes() {
		return teacherNodes;
	}
	
	public List getTestNodes() {
		return testNodes;
	}

	public Node getClassNode() {
		return klass;
	}
	
	public Node getEducationNode() {
		return education;
	}
}
