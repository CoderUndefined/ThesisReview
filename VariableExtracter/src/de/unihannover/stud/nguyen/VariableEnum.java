package de.unihannover.stud.nguyen;

/**
 * Enumerations for the categories of variables that use English words or not
 */
enum VariableEnum {
  singleLetterVariable, twoLetterVariable, threeLetterVariable, fourLetterVariable,
  fiveLetterVariable, sixLetterVariable, sevenLetterVariable, eightLetterVariable,
  nineLetterVariable, tenLetterVariable, moreLetterVariable,

  singleEnglishWord, twoEnglishWords, threeEnglishWords, fourEnglishWords,
  fiveEnglishWords, moreEnglishWords,

  singleEnglishWordPlus, twoEnglishWordsPlus, threeEnglishWordsPlus, fourEnglishWordsPlus,
  fiveEnglishWordsPlus, moreEnglishWordsPlus,

  singleEnglishWordWeak, twoEnglishWordsWeak, threeEnglishWordsWeak, fourEnglishWordsWeak,
  fiveEnglishWordsWeak, moreEnglishWordsWeak,

  singleEnglishWordAlmost, twoEnglishWordsAlmost, threeEnglishWordsAlmost, fourEnglishWordsAlmost,
  fiveEnglishWordsAlmost, moreEnglishWordsAlmost,


  singleAbrWord, twoAbrWords, threeAbrWords, fourAbrWords,
  fiveAbrWords, moreAbrWords,

  singleAbrWordPlus, twoAbrWordsPlus, threeAbrWordsPlus, fourAbrWordsPlus,
  fiveAbrWordsPlus, moreAbrWordsPlus,

  singleAbrWordWeak, twoAbrWordsWeak, threeAbrWordsWeak, fourAbrWordsWeak,
  fiveAbrWordsWeak, moreAbrWordsWeak,

  singleAbrWordAlmost, twoAbrWordsAlmost, threeAbrWordsAlmost, fourAbrWordsAlmost,
  fiveAbrWordsAlmost, moreAbrWordsAlmost,

  singleNonWord, twoNonWords, threeNonWords, fourNonWords, fiveNonWords, moreNonWords,

  singleUnderscore, doubleUnderscore, abr, system, other,

  pseudoSingleLetterVariable,

  wrongFormat
}
