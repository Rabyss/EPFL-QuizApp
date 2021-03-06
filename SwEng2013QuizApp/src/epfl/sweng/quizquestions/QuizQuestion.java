package epfl.sweng.quizquestions;

import static epfl.sweng.util.StringHelper.containsNonWhitespaceCharacters;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question of the quiz.
 */
public class QuizQuestion implements Serializable {

    private static final long serialVersionUID = 1678050024179601632L;

    // Uses Integer instead of int to allow them to be nullable.
    private final long mId;
    private final String mQuestion;
    private final AnswersList mAnswers;
    private final int mSolutionIndex;
    private final TagsSet mTags;
    private final String mOwner;

    private static final int MAX_QUESTION_LENGTH = 500;
    private static final int MIN_ANSWERS_NUM = 2;
    private static final int MAX_ANSWERS_NUM = 10;
    private static final int MIN_TAGS_NUM = 1;
    private static final int MAX_TAGS_NUM = 20;
    private static final int MAX_TAG_LEN = 20;
    private static final int SHIFT_32 = 32;
    private static final String ANSWERS = "answers";
    private static final String QUESTION = "question";
    private static final String OWNER = "owner";
    private static final String SOLUTION_INDEX = "solutionIndex";

    public QuizQuestion(final String question, final List<String> answers,
            final int solutionIndex, final Set<String> tags, final long id,
            final String owner) {
        mId = id;
        mQuestion = question;

        mAnswers = new AnswersList();
        if (answers != null) {
            mAnswers.addAll(answers);
        }
        mSolutionIndex = solutionIndex;

        mTags = new TagsSet();
        if (tags != null) {
            mTags.addAll(tags);
        }
        mOwner = owner;
    }

    /**
     * Constructs the class from a JSONObject
     * 
     * @param jsonText
     * @throws MalformedQuestionException
     *             if the JSONObject is malformed
     */
    public QuizQuestion(final String jsonText) throws JSONException {
        JSONObject jsonModel = new JSONObject(jsonText);
        mId = jsonModel.getLong("id");
        mQuestion = jsonModel.getString(QUESTION);

        mAnswers = new AnswersList();
        mAnswers.addAll(extractCollectionFromJSONArray(jsonModel
                .getJSONArray(ANSWERS)));

        mSolutionIndex = jsonModel.getInt(SOLUTION_INDEX);

        mTags = new TagsSet();
        mTags.addAll(extractCollectionFromJSONArray(jsonModel
                .getJSONArray("tags")));

        mOwner = jsonModel.getString(OWNER);
    }

    public long getId() {
        return mId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public List<String> getAnswers() {
        return Collections.unmodifiableList(mAnswers);
    }

    /**
     * Checks whether the given index is the index of the solution.
     */
    public boolean isSolution(int index) {
        return mSolutionIndex == index;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(mTags);
    }

    public String getOwner() {
        return mOwner;
    }

    public void addTag(String tag) {
        mTags.add(tag);
    }

    public void addAnswer(String answer) {
        mAnswers.add(answer);
    }

    public int getSolution() {
        return mSolutionIndex;
    }

    /**
     * Converts the object to its JSON description.
     * 
     * @return a string containing the JSON description.
     * @throws MalformedQuestionException
     *             if the question is malformed
     */
    public String toJSON() throws MalformedQuestionException {
        if (this.auditErrors() != 0) {
            throw new MalformedQuestionException("Question that must be "
                    + " converted to JSON is malformed");
        }
        Map<String, Object> questionMap = new HashMap<String, Object>();

        if (mId != -1) {
            questionMap.put("id", mId);
        }

        questionMap.put(QUESTION, mQuestion);
        questionMap.put(ANSWERS, new JSONArray(mAnswers));
        questionMap.put(SOLUTION_INDEX, mSolutionIndex);
        questionMap.put("tags", new JSONArray(mTags));

        if (mOwner != null) {
            questionMap.put(OWNER, mOwner);
        }

        return new JSONObject(questionMap).toString();
    }

    /**
     * Audit method for QuizQuestion
     * 
     * @return the number of errors in this instance representation
     */
    public int auditErrors() {
        int errors = 0;

        if (!containsNonWhitespaceCharacters(mQuestion)
                || mQuestion.length() > MAX_QUESTION_LENGTH) {
            errors++;
        }

        errors += mAnswers.auditErrors();

        errors += mTags.auditErrors();

        return errors;
    }

    private static Collection<String> extractCollectionFromJSONArray(
            JSONArray jsonArray) throws JSONException {
        final List<String> sourceList = new LinkedList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            sourceList.add(jsonArray.getString(i));
        }

        return new AbstractCollection<String>() {

            private final List<String> strEntries = sourceList;

            @Override
            public Iterator<String> iterator() {
                return strEntries.iterator();
            }

            @Override
            public int size() {
                return strEntries.size();
            }
        };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((mAnswers == null) ? 0 : mAnswers.hashCode());
        result = prime * result + (int) (mId ^ (mId >>> SHIFT_32));
        result = prime * result + ((mOwner == null) ? 0 : mOwner.hashCode());
        result = prime * result
                + ((mQuestion == null) ? 0 : mQuestion.hashCode());
        result = prime * result + mSolutionIndex;
        result = prime * result + ((mTags == null) ? 0 : mTags.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        QuizQuestion other = (QuizQuestion) obj;
        if (mAnswers == null) {
            if (other.mAnswers != null) {
                return false;
            }
        } else if (!mAnswers.equals(other.mAnswers)) {
            return false;
        }
        if (mId != other.mId) {
            return false;
        }
        if (mOwner == null) {
            if (other.mOwner != null) {
                return false;
            }
        } else if (!mOwner.equals(other.mOwner)) {
            return false;
        }
        if (mQuestion == null) {
            if (other.mQuestion != null) {
                return false;
            }
        } else if (!mQuestion.equals(other.mQuestion)) {
            return false;
        }
        if (mSolutionIndex != other.mSolutionIndex) {
            return false;
        }
        if (mTags == null) {
            if (other.mTags != null) {
                return false;
            }
        } else if (!mTags.equals(other.mTags)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id : " + mId + "\nquestion : " + mQuestion + "\nanswers : "
                + mAnswers + "\nsolution index : " + mSolutionIndex
                + "\ntags : " + mTags + "\nowner : " + mOwner;
    }

    private class AnswersList extends LinkedList<String> {

        private static final long serialVersionUID = 3821236061355668378L;

        public int auditErrors() {
            int errors = 0;
            int numAnswers = size();

            if (numAnswers < MIN_ANSWERS_NUM || numAnswers > MAX_ANSWERS_NUM) {
                errors++;
            }

            if (mSolutionIndex < 0 || mSolutionIndex >= numAnswers) {
                errors++;
            }

            for (String answer : this) {
                if (!containsNonWhitespaceCharacters(answer)
                        || answer.length() > MAX_QUESTION_LENGTH) {
                    errors++;
                }
            }

            return errors;
        }

    }

    private class TagsSet extends HashSet<String> {

        private static final long serialVersionUID = -3692725125179875656L;

        public int auditErrors() {
            int errors = 0;
            int numTags = size();

            if (numTags < MIN_TAGS_NUM || numTags > MAX_TAGS_NUM) {
                errors++;
            }

            for (String tag : this) {
                if (!containsNonWhitespaceCharacters(tag)
                        || tag.length() > MAX_TAG_LEN) {
                    errors++;
                }
            }

            return errors;
        }

    }
}
