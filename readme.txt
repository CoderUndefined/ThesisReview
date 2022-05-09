Before all of that:

0. Check the system requirements for creating these resources. Here a quick reminder:

   Linux-based 64-bit OS. It was tested on Debian.
   8-core CPU with SMT. Desktop examples are a modern 10th gen. Core i7 or any Ryzen 7 CPU
   At least 2TB of storage
   
   
   Extract all archives on this directory. This example will be used onward.
   Your directory will probably be different
   
   /home/pc/Desktop/folder
  
   Do not run ANY of the steps on an external drive, because external drive names are very long.
  
   Make sure that pylint is installed. If not, run
   sudo pip install pylint
   [or]
   sudo apt install pylint
   [or your equivalent package manager. 
   It MUST be installed globally so it can be run from the command line (bash) like this:
   
   pylint
   
   (Run this in the command line, it should display a help message on how to use pylint)
   
   or else it won't work]
   
   Also make sure that jshint is installed, If not, run
   sudo npm install -g jshint

   This will require pip and npm respectively.
   This also means that Java 11 and Python 3 must be installed, via local package manager
  
   Also extract any archives.
   
1. Pylint must be installed via pip install pylint. It was tested on version 2.13.5
2. Clone all repositories using a cloning script. There are a total of five cloning scripts
   which are located in the 0_CloningScripts folder, which was used to recreate these cloning scripts.
   
   clone_gitDepth1.sh           Creates generic Python code dataset. Used as basis for create resources	
   clone_gitDSDatasetDepth1.sh  Creates data science dataset.
   clone_noDSFolder.sh          Creates non data science dataset. Based of the generic Python code dataset.
   clone_controlFolder.sh       Creates control dataset, for checking only.
   clone_jsCorpus.sh            Creates JavaScript dataset.
   
3. Create a directory for each dataset. It is important to keep them separate. Some example names:

   gitDepth1                 generic Python code dataset. Used as basis for create resources	
   gitDSDatasetDepth1        data science dataset.
   noDSFolder                non data science dataset. Based of the generic Python code dataset.
   controlFolder             control dataset, for checking only.
   jsCorpus                  JavaScript dataset.

   And their corresponding full paths, as an example.
   THESE ARE JUST EXAMPLES, DO USE SOMETHING DIFFERENT PLEASE OR ELSE SOMETHING MAY BREAK
   IF CREATING RESOURCES FROM SCRATCH, DO NOT REUSE ANY EXISTING RESOURCES
   DO NOT MIX UP EXISTING RESOURCES WITH NEW ONES

   /home/pc/Desktop/gitDepth1           // Expect to see this a lot as an example
   /home/pc/Desktop/gitDSDatasetDepth1
   /home/pc/Desktop/noDSFolder
   /home/pc/Desktop/controlFolder
   /home/pc/Desktop/jsCorpus  // this is the odd one out, do not process this further until section 7.8.1
   
4. Copy and paste each corresponding cloning script to the corresponding directory
5. Allow them to execute. This is done with "chmod 700 clone_gitDepth1.sh". Repeat this with every script.
6. Run one script at a time at each directory. Depending in internet bandwidth, this can take a LONG time.
7. Delete all non-Python files named "pylintrc", "pyproject.toml" and "setup.cfg" in each Python code dataset.
   This should not be required because Pylint always uses its own configuration file, but this is to be safe.
8. For the jsCorpus JavaScript corpus, delete all "jshint" files that are not JavaScript 
   The reason is the same even though this should not be required. Use the search function to find them.
   
9. For the Python datasets, all comments regarding Pylint have to be removed
   This step is VERY important or else the number of Pylint warnings is lower due to them being suppressed

   Run RemovePylintComment.jar located in:
   /home/pc/Desktop/folder/RemovePylintComment
   
   Run:
   RemovePylintComment.jar /home/pc/Desktop/gitDepth1
   
   This will remove all Pylint comments inside each Python code file.
   
   Repeat this for each Python dataset.
   RemovePylintComment.jar /home/pc/Desktop/gitDSDatasetDepth1
   RemovePylintComment.jar /home/pc/Desktop/noDSFolder
   RemovePylintComment.jar /home/pc/Desktop/controlFolder


10 The same must be applied to the JS dataset
   This step is VERY important or else the number of JSHint warnings is lower due to them being suppressed

   Run RemoveJSHintComment.jar located in:
   /home/pc/Desktop/folder/RemoveJSHintComment
   
   Run:
   RemoveJSHintComment.jar /home/pc/Desktop/jsCorpus
   
   This will remove all JSHint comments inside each JS code file.


   The datasets are now ready to be processed.
   
   On a note, DO NOT USE VERY LONG FILE PATHS. This might break any program due to name limits.
   Also avoid using relative paths as arguments. Always specify the full path when using it as argument
   
   Do not run it off from an external drive. Use an internal drive if possible, due to performance.
   If an external drive is used, at least ensure it has a proper label like "HardDrive".
   not something like "abcdefab-1234-5678-9012-abcdefabcdef", which lengthens the path
   
======================================================================================================


Reproduce section 7.4 and section 7.5

1. Pylint must be installed via 

   pip install pylint. 

   It was tested on version 2.13.5
   
2. Run PylintChecker.jar located in:
   /home/pc/Desktop/folder/PylintChecker
   
   Ensure that the terminal is currently located in that exact directory.
   Also ensure that the custom .pylintrc and .pylintrcZero configuration file is inside the directory.
   These files are hidden due to the dot. File managers have an option to show these hidden files.

   Run:
   PylintChecker.jar --standardPylint /home/pc/Desktop/gitDepth1
   
   This will generate two folders with all Pylint results in the same directory as PylintChecker.jar
   Name will be "std1"+[Name of directory of dataset]
   
   Assuming that this was run on /home/pc/Desktop/, it should look like this
   
   Output folder. This is the one we need. Using gitDepth1 directory as example:
   /home/pc/Desktop/folder/PylintChecker/std1gitDepth1
   
   Error output folder. This can be ignored. Using gitDepth1 directory as example:
   /home/pc/Desktop/folder/PylintChecker/err1gitDepth1
   
   This must be repeated for each dataset.
   

3. Run PylintWarningCounter.jar located in:
   /home/pc/Desktop/folder/PylintWarningCounter
   
   Ensure that the terminal is currently located in that exact directory.
   
   Run:
   PylintWarningCounter.jar --generatePylintStats /home/pc/Desktop/gitDepth1 ./../PylintChecker/std1gitDepth1 0
   gitDepth1combined.txt
   
   This will generate an output file named gitDepth1combined.txt that will contain the results necessary to fill the Table 7.9 in section 7.5.
   
   The number "0" as fourth argument is important because each number corresponds to the operation
   "0" takes all Pylint warnings into account.
   "1" only takes Pylint warnings into account in modules that have at least 1 "Non-compliant name" warning.
   "2" only takes Pylint warnings into account in modules that have 0 "Non-compliant name" warnings.
   
   Therefore, this needs to be run again, but with a different fourth argument each time:
   
   Run:
   PylintWarningCounter.jar --generatePylintStats /home/pc/Desktop/gitDepth1 ./../PylintChecker/std1gitDepth1 1
   gitDepth1withC0103.txt
  
   This will generate the information needed for Table 7.10 in subsection 7.5.2
   
   Run:
   PylintWarningCounter.jar --generatePylintStats /home/pc/Desktop/gitDepth1 ./../PylintChecker/std1gitDepth1 2 
   gitDepth1withC0103.txt
  
   This will generate the information needed for Table 7.11 in subsection 7.5.2
   
   This must be repeated for the DS and the Non-DS dataset.
  

4. For the non compliant name breakdown in section 7.4 on Table 7.8, run ProjectA.jar which is in
   /home/pc/Desktop/folder/ProjectA

   Ensure that the terminal is currently located in that exact directory.

   Run:
   ProjectA.jar --processPylintResultDirectory ./../PylintChecker/std1gitDpeth1
   
   The last argument is the Pylint result directory.
   The output is printed on the console. This will generate the information on Table 7.8. 
   
   This must be repeated for the DS and the Non-DS dataset.


======================================================================================================


Most popular variable names (section 7.6)
Assuming at all repositories are cloned


1. Run VariableExtracter.jar located in:
   /home/pc/Desktop/folder/VariableExtracter
  
   Ensure that the terminal is currently located in that exact directory.
   
   Run:
   VariableExtracter.jar --firstStepOnly englishDictionary.txt /home/pc/Desktop/gitDSDatasetDepth1
   
   There are three arguments. 
   --firstStepOnly refers to the fact that it only executes the first steps of the abbreviation tree creation
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDSDatasetDepth1 is the location of a different dataset, here the DS dataset.
   
   The output of this program is a new directory named after the directory of the dataset.
   Inside the directory there are these files
   - temp.txt are all variables that are found in the dataset, with their number of occurrences and is sorted
   - percentages.txt is almost the exact same as temp.txt except the numbers are replaced by percentages
   - ThreeOnly.txt are all variables with at least three alphabetic letters, sorted by popularity
   
   The file that matters is percentages.txt. This contains the information necessary to create Table 7.12
   
   This must be done for the DS and the Non-DS dataset.

======================================================================================================

Section 7.7.2

1. Start Type4Py if this has not been launched yet.

   To get the Type4Py (by Mir et al.) local model, use this command. Requires sudo privileges.
   It only needs to be run once in a lifetime.
   sudo docker pull ghcr.io/saltudelft/type4py:latest;
    
   To start the local model, use this command. Requires sudo privileges. 
   Must be started once after every time the computer has started. 
   sudo docker run -d -p 5001:5010 -it ghcr.io/saltudelft/type4py:latest
   
   This is technically optional and not running it can speed it up but it may affect the results

2. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   This time, the abbreviation tree data structure must be created and exported.
   
   Run:
   VariableExtracter.jar --generateAbbreviationMap englishDictionary.txt /home/pc/Desktop/gitDepth1 
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt
   
   There are four arguments. 
   --generateAbbreviationMap refers to the creation of that abbreviation tree data structure
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDepth1 is the location of the dataset
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt 
   is the output file of the exported tree.
   
   This may take several hours to run, especially for the first time.
   The console will indicate the progress. 
   Very rarely Type4Py may fail to work, in which case an exception is thrown.
   This does not affect the overall operation too significantly.
   
   
   
3. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   The accuracy of the algorithm will be measured, using three manually created files.
   
   Run:
   VariableExtracter.jar --testAccuracy 
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt
   
   There are two arguments. 
   --testAccuracy refers to the option that the accuracy will be tested
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt
    is the output file of the exported tree from the previous step.
   
   The console will output the accuracy figures required for Table 7.14
   
  
====================================================================================================

Section 7.7.3

1. Start Type4Py if this has not been launched yet.

   To get the Type4Py (by Mir et al.) local model, use this command. Requires sudo privileges.
   It only needs to be run once in a lifetime.
   sudo docker pull ghcr.io/saltudelft/type4py:latest;
    
   To start the local model, use this command. Requires sudo privileges. 
   Must be started once after every time the computer has started. 
   sudo docker run -d -p 5001:5010 -it ghcr.io/saltudelft/type4py:latest
   
   This is technically optional and not running it can speed it up but it may affect the results

2. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   This time, the abbreviation tree data structure must be created and exported, with documentation
   
   Run:
   VariableExtracter.jar --generateAbbreviationMapWithDocs englishDictionary.txt 
   /home/pc/Desktop/gitDepth1  genericPythonCodeDatasetWithDocs.txt
   
   There are four arguments. 
   --generateAbbreviationMapWithDocs refers the same creation, but now with documentation taken into account
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDepth1 is the location of the dataset
   genericPythonCodeDatasetWithDocs.txt is the output file of the exported tree.
   
   This may take more than a day to run, especially for the first time.
   The console will indicate the progress. 
   Very rarely Type4Py may fail to work, in which case an exception is thrown.
   This does not affect the overall operation too significantly.
   
3. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   The accuracy of the algorithm will be measured, using three manually created files.
   
   Run:
   VariableExtracter.jar --testAccuracy genericPythonCodeDatasetWithDocs.txt
   
   There are two arguments. 
   --testAccuracy refers to the option that the accuracy will be tested
   genericPythonCodeDatasetWithDocs.txt is the output file of the exported tree from the previous step.
   
   The console will output the accuracy figures required for Table 7.15
   

====================================================================================================

Section 7.7.4

1. Start Type4Py if this has not been launched yet.

   To get the Type4Py (by Mir et al.) local model, use this command. Requires sudo privileges.
   It only needs to be run once in a lifetime.
   sudo docker pull ghcr.io/saltudelft/type4py:latest;
    
   To start the local model, use this command. Requires sudo privileges. 
   Must be started once after every time the computer has started. 
   sudo docker run -d -p 5001:5010 -it ghcr.io/saltudelft/type4py:latest
   
   This is technically optional and not running it can speed it up but it may affect the results
   
   
2. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   This time, the abbreviation tree data structure must be created and exported, with documentation
   
   Run:
   VariableExtracter.jar --generateAbbreviationMapHalf englishDictionary.txt 
   /home/pc/Desktop/gitDepth1 genericPythonCodeDatasetHalf.txt
   
   There are four arguments. 
   --generateAbbreviationMapHalf refers the same creation, but with only 50% of the dataset size
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDepth1 is the location of the dataset
   genericPythonCodeDatasetHalf.txt is the output file of the exported tree.
   
   This may take more than a day to run, especially for the first time.
   The console will indicate the progress. 
   Very rarely Type4Py may fail to work, in which case an exception is thrown.
   This does not affect the overall operation too significantly.
   
3. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   The accuracy of the algorithm will be measured, using three manually created files.
   
   Run:
   VariableExtracter.jar --testAccuracy genericPythonCodeDatasetHalf.txt
   
   There are two arguments. 
   --testAccuracy refers to the option that the accuracy will be tested
   genericPythonCodeDatasetHalf.txt is the output file of the exported tree from the previous step.
   
   
   The console will output the accuracy figures required for Table 7.15
   
   DO NOT CANCEL THIS OR ELSE SOME DIRECTORIES WILL REMAIN MARKED AS UNREADABLE
   This can be manually undone by setting these as readable or by running this step fully.
   
4. Repeat step 2, then step 3 but step 2 has a different first argument
   --generateAbbreviationMapQuarter
   --generateAbbreviationMapEighth
   
   And a different fourth argument, accordingly
   genericPythonCodeDatasetQuarter.txt
   genericPythonCodeDatasetEighth.txt


====================================================================================================

Section 7.8

May require to run java with more memory. This is done with this option
java -Xmx20G (or whatever number you need)
   
1. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The relation lists must be created in its raw form.
   
   Run:
   editor.jar --createFullFolder englishDictionary.txt 
   /home/pc/Desktop/CacheFolder/fullFolder
   /home/pc/Desktop/gitDepth1
   
   It needs four arguments
   --createFullFolder creates the raw full folder that would normally be exported
   englishDictionary.txt is the dictionary file
   /home/pc/Desktop/CacheFolder/fullFolder is the output directory of this program.
   /home/pc/Desktop/gitDepth1 is the location of the generic Python code dataset.
   
2. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The output from the previous step must be exported properly
   
   Run:
   editor.jar --export /home/pc/Desktop/CacheFolder/fullFolder
   /home/pc/Desktop/CacheFolder/exportedFolder
   
   It needs three arguments
   --export exports the raw data into a proper directory structure
   /home/pc/Desktop/CacheFolder/fullFolder is the output from the previous step; This acts as input now.
   /home/pc/Desktop/CacheFolder/exportedFolder are the exported lists which is the output.
   
   
3. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   This one measures the accuracy required for section 7.8
   
   Run:
   editor.jar --experimental englishDictionary.txt 
   /home/pc/Desktop/CacheFolder/fullFolder
   /home/pc/Desktop/controlFolder
   /home/pc/Desktop/CacheFolder/exportedFolder
   
   It needs four arguments
   --experimental Tests accuracy of that single letter variable replacer algorithm
   englishDictionary.txt is the dictionary file
   /home/pc/Desktop/CacheFolder/fullFolder is the output from the previous step; This acts as input now.
   /home/pc/Desktop/control is the a different dataset, the previous one must NOT be used.
   /home/pc/Desktop/CacheFolder/exportedFolder are the exported lists.
   
   
4. (REQUIRED FOR LATER) Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The cache needs to be created for the plugin
   
   Run:
   editor.jar --createCacheFolder englishDictionary.txt 
   /home/pc/Desktop/CacheFolder/cacheFolder
   /home/pc/Desktop/gitDepth1
   
   It needs four arguments
   --createCacheFolder creates a cache folder
   englishDictionary.txt is the dictionary file
   /home/pc/Desktop/CacheFolder/cacheFolder is the output directory of this program (cache).
   /home/pc/Desktop/gitDepth1 is the location of the generic Python code dataset.
   
   

====================================================================================================

Section 7.8.1


1. Ensure JSHint is installed
   If this is not the case, install it via
   
   npm install -g jshint
   

2. Run JSCounter.jar located in:
   /home/pc/Desktop/folder/JSCounter
   
   Ensure that the terminal is currently located in that exact directory.
   Also ensure that the custom .jshintrc configuration file is inside the directory.

   Run:
   JSCounter.jar --processJSDataset /home/pc/Desktop/jsCorpus
      
   There are two arguments
   --processJSDataset tells the program to process that dataset with JSHint.
   The second argument is the directory of the JavaScript corpus
   
   This will take a very long time to complete. The output is displayed on the console for JavaScript
   
   
   Alternatively if this was already done once:
   
   Run:
   JSCounter.jar --processOutputFolder
   
   This alternative method does not work for the first time when the output folder is not present.
   
   
3. Run PylintChecker.jar located in:
   /home/pc/Desktop/folder/PylintChecker
   
   Ensure that the terminal is currently located in that exact directory.
   Also ensure that the custom .pylintrc and .pylintrcZero configuration file is inside the directory.

   Run:
   PylintChecker.jar --zeroPylint /home/pc/Desktop/gitDepth1
   
   There are two arguments
   --zeroPylint uses the .pylintrcZero configuration file, so it always triggers due to zero thresholds
   The second argument is the directory of the generic Python code dataset

   This will generate two folders with all Pylint results in the same directory as PylintChecker.jar
   Name will be "stdZERO"+[Name of directory of dataset]
   
   Assuming that this was run on /home/pc/Desktop/, it should look like this
   
   Output folder. This is the one we need. Using gitDepth1 directory as example:
   /home/pc/Desktop/folder/PylintChecker/stdZEROgitDepth1
   
   This must be repeated for each dataset.
   
4. Run PylintFunctionCounter.jar located in
   /home/pc/Desktop/folder/PylintFunctionCounter
   
   Ensure that the terminal is currently located in that exact directory.
 
   Run:
   PylintFunctionCounter.jar --procssOutputFolder /home/pc/Desktop/folder/PylintChecker/stdZEROgitDepth1
   
   There are two arguments
   --processOutputFolder tells the program to process that output folder containing Pylint results
   The second argument is the directory of the output folder created from the previous step
   
   It must(!) be from a "zero-Pylint" search that flags at every occurrence from the previous step.
   A regular Pylint search will not work here.

   Output results are on the console

====================================================================================================


Section 7.9

1. Run VariableExtracter.jar located in:
   /home/pc/Desktop/folder/VariableExtracter
  
   Ensure that the terminal is currently located in that exact directory.
   
   Run:
   VariableExtracter.jar --firstStepOnly englishDictionary.txt /home/pc/Desktop/gitDSDatasetDepth1
   
   There are three arguments. 
   --firstStepOnly refers to the fact that it only executes the first steps of the abbreviation tree creation
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDSDatasetDepth1 is the location of a different dataset, here the DS dataset.
   
   The output of this program is a new directory named after the directory of the dataset.
   In this example it would be at /home/pc/Desktop/folder/VariableExtracter/gitDSDatasetDepth1
  
   The console output matters, which contribute to Table 7.18
   
   This must be done for the DS and the Non-DS dataset.
   

2. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   The existing abbreviation tree must be evaluated
   
   Run:
   VariableExtracter.jar --generateOriginalFiles englishDictionary.txt /home/pc/Desktop/gitDSDatasetDepth1 
   genericPythonCodeDataset.txt
   
   There are four arguments. 
   --generateOriginalFiles refers to the creation of the original files for Table 7.19
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDSDatasetDepth1 is the location of a different dataset, here the DS dataset.
   genericPythonCodeDataset.txt is the output file of the exported tree.
   
   The console output matters, which contribute to Table 7.19
   
   This must be done for the DS and the Non-DS dataset.
   
   Table 7.20 must be filled manually, using the formulas from Listing 7.21 and Listing 7.22.
  
  
====================================================================================================

Section 7.10

Measure times in seconds. May greatly differ based on internet bandwidth, CPU, RAM and so on.

Generating resources for plugin


In the event that the results fluctuate, which should not happen, try these out
- Disable multithreading, which is already the case.

If nothing is left
- Do 5 runs
- Take either the median or mean result.

=================================================================================================

Installation guide plugin

1. English dictionary file is already in "0_PluginResources".
2. AbbreviationTree file is already in "0_PluginResources".
3. The "cacheFolder.7z" archive must be extracted first.
4. The "exportFolder.7z" archive must be extracted first.
5. The actual plugin is named "editor-0.0.1.zip" and has to manually installed in PyCharm.
6. Then change the settings to point all the directories for the resources.
7. Set Memory Settings in PyCharm to at least 8192MB.

For more details see the thesis in section 6.6.

If you want to create these resources, then things might get complicated. The steps are similar.
The english dictionary file cannot be generated.



1. Start Type4Py if this has not been launched yet.

   To get the Type4Py (by Mir et al.) local model, use this command. Requires sudo privileges.
   It only needs to be run once in a lifetime.
   sudo docker pull ghcr.io/saltudelft/type4py:latest;
    
   To start the local model, use this command. Requires sudo privileges. 
   Must be started once after every time the computer has started. 
   sudo docker run -d -p 5001:5010 -it ghcr.io/saltudelft/type4py:latest
   
   This is technically optional and not running it can speed it up but it may affect the results

2. Run VariableExtracter.jar located in
   /home/pc/Desktop/folder/VariableExtracter
   
   This time, the abbreviation tree data structure must be created and exported.
   
   Run:
   VariableExtracter.jar --generateAbbreviationMap englishDictionary.txt /home/pc/Desktop/gitDepth1 
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt
   
   There are four arguments. 
   --generateAbbreviationMap refers to the creation of that abbreviation tree data structure
   englishDictionary.txt is the English dictionary file that should already be there.
   /home/pc/Desktop/gitDepth1 is the location of the dataset
   /home/pc/Desktop/folder/VariableExtracter/genericPythonCodeDataset.txt 
   is the output file of the exported tree.
   
   This may take several hours to run, especially for the first time.
   The console will indicate the progress. 
   Very rarely Type4Py may fail to work, in which case an exception is thrown.
   This does not affect the overall operation too significantly.

3. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The cache needs to be created for the plugin
   
   Run:
   editor.jar --createCacheFolder englishDictionary.txt 
   /home/pc/Desktop/CacheFolder/cacheFolder
   /home/pc/Desktop/gitDepth1
   
   It needs four arguments
   --createCacheFolder creates a cache folder
   englishDictionary.txt is the dictionary file
   /home/pc/Desktop/CacheFolder/cacheFolder is the output directory of this program (cache).
   /home/pc/Desktop/gitDepth1 is the location of the generic Python code dataset.
   
   
4. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The relation lists must be created in its raw form.
   
   Run:
   editor.jar --createFullFolder englishDictionary.txt 
   /home/pc/Desktop/CacheFolder/fullFolder
   /home/pc/Desktop/gitDepth1
   
   It needs four arguments
   --createFullFolder creates the raw full folder that would normally be exported
   englishDictionary.txt is the dictionary file
   /home/pc/Desktop/CacheFolder/fullFolder is the output directory of this program.
   /home/pc/Desktop/gitDepth1 is the location of the generic Python code dataset.
   
5. Run editor.jar located in
   /home/pc/Desktop/folder/editor_basics
   
   The output from the previous step must be exported properly
   
   Run:
   editor.jar --export /home/pc/Desktop/CacheFolder/fullFolder
   /home/pc/Desktop/CacheFolder/exportedFolder
   
   It needs three arguments
   --export exports the raw data into a proper directory structure
   /home/pc/Desktop/CacheFolder/fullFolder is the output from the previous step; This acts as input now.
   /home/pc/Desktop/CacheFolder/exportedFolder are the exported lists which is the output.
   
6. Now these resources are created. Proceed with the usual installation steps as seen in section 6.6.

================================================================================================
Used jars
  RemovePylintComment.jar         
  RemoveJSHintComment.jar         
  PylintChecker.jar               
  PylintWarningCounter.jar        
  ProjectA.jar                   
  VariableExtracter.jar
  editor.jar
  JSCounter.jar                   
  PylintFunctionCounter.jar       



