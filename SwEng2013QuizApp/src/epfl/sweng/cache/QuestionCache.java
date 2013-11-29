package epfl.sweng.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Cache for questions
 * 
 * for each question q cached, there is a file questions/q.bin where the
 * question is stored; and for each tag t in q, there is a file tags/t.bin where
 * the id of q is stored (together with all other question's id with same tag t)
 * 
 */
public final class QuestionCache  {
//
//	/**
//	 * Singleton instance of QuestionCache
//	 */
//	private static QuestionCache instance = null;
//
//	/**
//	 * Directory where tag file are stored
//	 */
//	private File tagDir;
//
//	/**
//	 * Directory where question files are stored
//	 */
//	private File questionDir;
//
//	private QuestionCache(Context context) {
//		File mainDir = context.getFilesDir();
//		tagDir = new File(mainDir, "tags");
//		tagDir.mkdir(); // return false if already exists
//		questionDir = new File(mainDir, "questions");
//		questionDir.mkdir();
//	}
//
//	public static synchronized QuestionCache getInstance(Context context) {
//		if (instance == null) {
//			instance = new QuestionCache(context);
//		}
//		return instance;
//	}
//
//	public void cacheQuestion(QuizQuestion question) {
//
//		// TODO Handle 1GB storage limit
//		// TODO append using writeObject may not work
//
//		Integer id = question.hashCode();
//		File questionFile = new File(questionDir, id + ".bin");
//
//		// if question already cached, stop
//		if (questionFile.exists()) {
//			return;
//		}
//
//		// store question if questionDir/id.bin
//		ObjectOutputStream os = null;
//		try {
//			questionFile.createNewFile();
//			os = new ObjectOutputStream(new FileOutputStream(questionFile));
//			os.writeObject(question);
//			os.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// for each tag t in question add id in tagDir/t.bin
//		File tagFile = null;
//		for (String t : question.getTags()) {
//			tagFile = new File(tagDir, t + ".bin");
//			try {
//				tagFile.createNewFile(); // return false if already exists
//				os = new ObjectOutputStream(new FileOutputStream(tagFile, true));
//				os.writeObject(id);
//				os.close();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public Set<Integer> getQuestionSetByTag(String tag) {
//		
//		Set<Integer> ids = new HashSet<Integer>();
//		File tagFile = new File(tagDir, tag + ".bin");
//		
//		if (!tagFile.exists()) {
//			return ids; // empty set
//		}
//		
//		try {
//			ObjectInputStream is = new ObjectInputStream(new FileInputStream(tagFile));
//			while (is.available() != 0) { // TODO replace available with something that works
//				ids.add((Integer) is.readObject());
//			}
//			is.close();
//		} catch (StreamCorruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return ids;
//	}
//
//	public QuizQuestion getQuestionById(Integer id) {
//		File questionFile = new File(questionDir, id + ".bin");
//		if (!questionFile.exists()) {
//			return null; // TODO Handle bad id (ok for Sacha)
//		}
//
//		QuizQuestion question = null;
//		try {
//			ObjectInputStream is = new ObjectInputStream(new FileInputStream(
//					questionFile));
//			question = (QuizQuestion) is.readObject();
//			is.close();
//		} catch (StreamCorruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return question;
//	}
//	
//	//TODO : implements clearCache (clean the cache)
//	// certainly usefull for testing
//	public void clearCache() {
//		
//	}
}
