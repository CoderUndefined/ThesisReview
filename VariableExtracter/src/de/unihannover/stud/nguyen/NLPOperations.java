package de.unihannover.stud.nguyen;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NLPOperations {

  /**
   * Uses the Porter stemmer to stem a long word
   *
   * @param input Input string
   * @return base form of string
   */
  static String stemming(String input) {
    PorterStemmer stemmer = new PorterStemmer();
    String output = stemmer.stem(input);
    return output;
  }

}
