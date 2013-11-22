package epfl.sweng.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

import android.content.Context;

import epfl.sweng.quizquestions.QuizQuestion;

public final class QuestionCache {

	/**
	 * Singleton instance of QuestionCache
	 */
	private static QuestionCache instance = null;

	/**
	 * Directory where tag file are stored
	 */
	private File tagDir;

	/**
	 * Directory where question files are stored
	 */
	private File questionDir;

	private QuestionCache(Context context) {
		File mainDir = context.getFilesDir();
		tagDir = new File(mainDir, "tags");
		tagDir.mkdir(); // return false if already exists
		questionDir = new File(mainDir, "questions");
		questionDir.mkdir();
	}

	public static synchronized QuestionCache getInstance(Context context) {
		if (instance == null) {
			instance = new QuestionCache(context);
		}
		return instance;
	}

	public void cacheQuestion(QuizQuestion question) {
		
		// TODO Handle 1GB storage limit
		// TODO append using writeObject may not work
		
		Integer id = question.hashCode();
		File questionFile = new File(questionDir, id + ".bin");

		// if question already cached, stop
		if (questionFile.exists()) {
			return;
		}

		// store question if questionDir/id.bin
		ObjectOutputStream os = null;
		try {
			questionFile.createNewFile();
			os = new ObjectOutputStream(new FileOutputStream(questionFile));
			os.writeObject(question);
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for each tag t in question add id in tagDir/t.bin
		File tagFile = null;
		for (String t : question.getTags()) {
			tagFile = new File(tagDir, t + ".bin");
			try {
				tagFile.createNewFile(); // return false if already exists
				os = new ObjectOutputStream(new FileOutputStream(tagFile, true));
				os.writeObject(id);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Set<Integer> getQuestionSetByTag(String tag) {
		// TODO implement this
		return null;
	}

	public QuizQuestion getQuestionById(Integer id) {
		// TODO implement this
		return null;
	}

}
