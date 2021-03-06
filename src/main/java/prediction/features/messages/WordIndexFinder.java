package prediction.features.messages;

import java.util.Set;

/**
 * Class for determining whether the index of a word in a feature vector
 */
public interface WordIndexFinder {

	/**
	 * Get the index of the word in the feature vector
	 * @param word
	 * 			The word to obtain the index of
	 * @return the index of the word
	 */
	public Integer indexOf(String word);
	
	/**
	 * The number of words that can be indexed
	 * @return The number of words
	 */
	public int numWords();
	
	/**
	 * The set of stop words that are not counted because they give no syntactic value
	 * @return The set of stop words
	 */
	public Set<String> stopWords();
}
