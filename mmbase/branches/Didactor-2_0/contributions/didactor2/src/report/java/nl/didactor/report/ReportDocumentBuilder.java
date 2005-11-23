package nl.didactor.report;
import com.lowagie.text.*;
import org.mmbase.bridge.*;
import java.util.*;
import nl.didactor.util.*;
import nl.didactor.component.education.utils.*;
import java.text.*;
public class ReportDocumentBuilder {
	private Document doc;
	private ClassReport report;
	private Font font;
	private DateFormat dateFormat;
	
	public ReportDocumentBuilder(Document doc, ClassReport report) {
		this.doc = doc;
		this.report = report;
		font = FontFactory.getFont(FontFactory.HELVETICA,(float)8.0);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	public Document buildDocument() throws DocumentException {
		Node klass = report.getClassNode();
		StringBuffer teacherNames = new StringBuffer();
		Iterator teachers = report.getTeacherNodes().iterator();
		while (teachers.hasNext()) {
			Node teacher = (Node) teachers.next();
			if (teacherNames.length() > 0) {
				teacherNames.append(", ");
			}
			teacherNames.append(teacher.getStringValue("firstname")+" "+teacher.getStringValue("lastname"));
		}
		doc.add(new Paragraph(
				"Klas: "+klass.getStringValue("name")+": "+
				dateFormat.format(ClassRoom.getStartDate(klass))+" - "+
				dateFormat.format(ClassRoom.getEndDate(klass))+".",font));

		doc.add(new Paragraph("Opleiding: "+report.getEducationNode().getStringValue("name"),font));
		doc.add(new Paragraph("Docent(en): "+teacherNames.toString(),font));
		
		java.util.List tests = report.getTestNodes();
		
		
		Table table = new Table(4+tests.size());
		table.addCell(new Phrase(9f,"login",font));
		table.addCell(new Phrase(9f,"naam",font));
		table.addCell(new Phrase(9f,"onlinetijd",font));
		table.addCell(new Phrase(9f,"tijd/bezoek",font));
		Iterator testIterator = tests.iterator();
		while (testIterator.hasNext()) {
			Node test = (Node) testIterator.next();
			table.addCell(new Phrase(9f,test.getStringValue("name"),font));
		}
		
		Iterator studentIterator = report.getStudentNodes().iterator();
		while (studentIterator.hasNext()) {
			Node student = (Node) studentIterator.next();
			Node classRel = (Node) ClassRoom.getClassRel(klass,student);
			table.addCell(new Phrase(9f,student.getStringValue("username"),font));
			table.addCell(new Phrase(9f,student.getStringValue("firstname")+" "+student.getStringValue("lastname"),font));
			
			int onlinetime = classRel.getIntValue("onlinetime");
            table.addCell(new Phrase(9f,""+(onlinetime / 3600)+":"+((onlinetime % 3600) / 60),font));
            
			int logincount = classRel.getIntValue("logincount");
			int avgtime = 0;
			if (logincount > 0) {
				avgtime = onlinetime / logincount;
			}
            table.addCell(new Phrase(9f,""+(avgtime / 3600)+":"+((avgtime % 3600) / 60),font));
    		testIterator = tests.iterator();
    		while (testIterator.hasNext()) {
    			Node test = (Node) testIterator.next();
    			int required = test.getIntValue("requiredscore");
    			int scored = TestUtils.testScore(student,test,klass);
    			if (scored == TestUtils.INCOMPLETE) {
    				table.addCell(new Phrase(9f,"",font));
    			}
    			else if (scored == TestUtils.NOTRATED) {
    				table.addCell(new Phrase(9f,"Nog niet nagekeken",font));	
    			}
    			else {
    				if (scored >= required) {
    					table.addCell(new Phrase(9f,"Geslaagd ("+scored+"/"+required+")",font));
    				}
    				else {
    					table.addCell(new Phrase(9f,"Niet gesl. ("+scored+"/"+required+")",font));
    				}
    				
    			}
    		}


		}
		doc.add(table);
		return doc;
	}
}
