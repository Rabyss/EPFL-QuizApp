package epfl.sweng.cache;

import java.util.Set;

import android.content.Context;
import android.util.SparseArray;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Non persistent Cache use for more performance.
 * Implements Proxy Pattern with QuestionCache
 * RAMCache is a proxy of QuestionCache
 * RAMCache has the set of HashQuestion => Question
 *
 */
public class RAMCache implements CacheInterface {
	
	private static RAMCache instance;

	//apparemment sparseArray est plus efficace que hashmap
	//pour maper des integers avec des objets
	private SparseArray<QuizQuestion> cacheMap ;

	private QuestionCache persistentCache;
	
	private RAMCache(Context context) {
		cacheMap = new SparseArray<QuizQuestion>();
		persistentCache = QuestionCache.getInstance(context);
	}

	public static synchronized RAMCache getInstance(Context context) {
		if (instance == null) {
			instance = new RAMCache(context);
		}
		return instance;
	}
	@Override
	public void cacheQuestion(QuizQuestion question) {
		Integer id = question.hashCode();
		// TODO : controler que la hashMap ne dépasse pas 50 MB

		cacheMap.append(id, question);
		persistentCache.cacheQuestion(question);
		
	}

	@Override
	public Set<Integer> getQuestionSetByTag(String tag) {
		return persistentCache.getQuestionSetByTag(tag);
	}

	@Override
	public QuizQuestion getQuestionById(Integer id) {
		QuizQuestion question = cacheMap.get(id);
		if(question != null){
			return question;
		} else {
			return persistentCache.getQuestionById(id);
		}
	}
	
}
