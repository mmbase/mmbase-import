package nl.didactor.component.education.utils;

import java.util.*;
import org.mmbase.bridge.*;
import nl.didactor.util.*;

public class TestUtils {
	
	public static List getAllTests(Node root) {
		List list = new ArrayList();
		return getAllTests(root,list,0);
	}
	private static List getAllTests(Node root,List list,int depth) {
		if (depth < 10) {
			NodeIterator children = root.getRelatedNodes("learnblocks","posrel","destination").nodeIterator();
			while (children.hasNext()) {
				getAllTests(children.nextNode(),list,depth+1);
			}
		}
		NodeIterator tests = root.getRelatedNodes("tests","posrel","destination").nodeIterator();
		while (tests.hasNext()) {
			list.add(tests.nextNode());
		}
		return list;
	}
	
	public static Node getCopyBook(Node student, Node klass) {
		return ClassRoom.getClassRel(klass,student).getRelatedNodes("copybooks").getNode(0);
	}
	
	public static final int INCOMPLETE = -1;
	public static final int NOTRATED = -4;
	
	public static int testScore(Node student, Node test, Node klass) {
		Node copyBook = getCopyBook(student,klass);
/*		int questionAmount = test.getIntValue("questionamount");
		if (questionAmount == -1) { // all questions
			questionAmount = test.countRelatedNodes("questions");
		} */
		
		NodeIterator madetests = test.getCloud().getList(
				test.getStringValue("number"),
				"tests,related,madetests,related,copybooks",
				"madetests.number,copybooks.number,madetests.score",
				"copybooks.number="+copyBook.getNumber()+" and madetests.score !="+INCOMPLETE,
				null,
				null,
				null,
				true
		).nodeIterator();
		int finalScore = INCOMPLETE;
		while (madetests.hasNext()) {
			Node made = madetests.nextNode();
			int score = made.getIntValue("score");
			if (score == NOTRATED && finalScore == INCOMPLETE) {
				finalScore = NOTRATED;
				continue;
			}
			if (score > finalScore) {
				finalScore = score;
			}
		}
		return finalScore;
	}
	
}
