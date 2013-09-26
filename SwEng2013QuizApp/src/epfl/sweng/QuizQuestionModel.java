package epfl.sweng;

/** Represents a question of the quiz.
 *  @author Philémon Favrod (philemon.favrod@epfl.ch)
 */
public class QuizQuestionModel {
    private final int mId;
    private final String mQuestion;
    private final String[] mAnswers;
    private final int mSolutionIndex;
    private final String[] mTags;
    private final String mOwner;
    
    public QuizQuestionModel(int id, String question, String[] answers, int solutionIndex, String[] tags, String owner) {
	mId = id;
	mQuestion = question;
	mAnswers = answers;
	mSolutionIndex = solutionIndex;
	mTags = tags;
	mOwner = owner;
    }

    public int getId() {
	return mId;
    }

    public String getQuestion() {
	return mQuestion;
    }

    public String[] getAnsers() {
	return mAnswers;
    }

    /** Checks whether the given index is the index of the solution.
    */
    public boolean isSolution(int index) {
	return (mSolutionIndex == index);
    }

    public String[] getTags() {
	return mTags;
    }

    
}
