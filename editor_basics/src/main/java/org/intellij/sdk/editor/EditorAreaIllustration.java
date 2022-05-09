// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import de.unihannover.stud.nguyen.App;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * If conditions support it, makes a menu visible to display information about the caret.
 *
 * @see AnAction
 */
public class EditorAreaIllustration extends AnAction {

  private String[] doubleUnderscoreArray(String[] strings) {

    List<String> list = new ArrayList<>();
    for (int i = 0; i < strings.length; i++) {
      String str = strings[i];

      if(str == null) {
//        newArray[i] = "ERROR";
        System.out.println("Line 40: Error at array index = " + i);
      }
      else {
        StringBuilder stringBuilder = new StringBuilder();
        for(int j = 0; j < str.length(); j++) {
          if(str.charAt(j) == '_') {
            stringBuilder.append("__");
          }
          else {
            stringBuilder.append(str.charAt(j));
          }
        }
//        newArray[i] = stringBuilder.toString();
        list.add(stringBuilder.toString());
      }

    }
    return list.toArray(new String[0]);
  }


  private String getType(PsiElement psiElement) {

    String returnValue = null;

    if(psiElement.getNavigationElement().toString().startsWith("PyTargetExpression")) {

      Query<PsiReference> query = ReferencesSearch.search(psiElement);
      boolean found = false;
      for(PsiReference psiReference : query) {
        System.out.println(psiReference.getElement());
        PsiElement ref = psiReference.getElement();

        for (PsiElement element : ref.getParent().getChildren()) {
          if (element.toString().startsWith("PyAnnotation")) {
            String typeHint = element.getText();
            typeHint = typeHint.substring(typeHint.indexOf(":") + 1);
            while (typeHint.startsWith(" ")) {
              typeHint = typeHint.substring(1);
            }
            System.out.println(typeHint);
            if(!found) {
              found = true;
              returnValue = typeHint;
            }
            else {
              System.out.println("WARNING: DUPLICATE FOUND");
              returnValue = null;

            }
          }
        }
        // queue it properly! from bottom to top
      }
    }
    else if(psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
      String text = psiElement.getText();
      if(text.contains(":")) {
        text = text.substring(text.indexOf(":") + 1);
        if(text.contains(":")) {
          text = text.substring(0, text.indexOf(":"));
        }
        while(text.startsWith(" ")) {
          text = text.substring(1);
        }
        while(text.endsWith(" ")) {
          text = text.substring(0, text.length()-1);
        }


        returnValue = text;
      }
    }
    if(returnValue != null) {
      if(returnValue.contains("[")) {
        returnValue = returnValue.substring(0, returnValue.indexOf("["));
      }

      returnValue = returnValue.toLowerCase();

      if(returnValue.equals("int") || returnValue.equals("float")) {
        returnValue = "[NUMBER]";
      }
      return returnValue;
    }
    else return null;
  }

  private String getVariableName(PsiElement psiElement) {
    if(psiElement.getNavigationElement().toString().startsWith("PyTargetExpression") ||
            psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
      String text = psiElement.getText();

      if(psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
        System.out.println(text);
      }

      // search for ALL instances of that variabble
      // sea


      if(psiElement.getNavigationElement().toString().startsWith("PyTargetExpression")) {

        Query<PsiReference> query = ReferencesSearch.search(psiElement);
        boolean found = false;
        for(PsiReference psiReference : query) {
          System.out.println(psiReference.getElement());
          PsiElement ref = psiReference.getElement();

          for (PsiElement element : ref.getParent().getChildren()) {
            if (element.toString().startsWith("PyAnnotation")) {
              String typeHint = element.getText();
              typeHint = typeHint.substring(typeHint.indexOf(":") + 1);
              while (typeHint.startsWith(" ")) {
                typeHint = typeHint.substring(1);
              }
              System.out.println(typeHint);
              if(!found) {
                found = true;
              }
              else {
                System.out.println("WARNING: DUPLICATE FOUND");
              }
            }
          }
          // queue it properly! from bottom to top
        }
      }

//      System.out.println(text);
      if(text.contains(".")) {
        text = text.substring(text.lastIndexOf(".")+1);
      }
      if(text.contains(":")) {
        text = text.substring(0,text.indexOf(":"));
      }
      if(text.contains("=")) {
        text = text.substring(0,text.indexOf("="));
      }
      return text;
    }
    else {
      return null;
    }
  }

  private boolean hasFunction(PsiElement psiElement) {

    while(true) {
      psiElement = psiElement.getParent();

      if (psiElement == null) {
        return false;
      }

      if(psiElement.toString().startsWith("PyFunction('")) {
        return true;
      }

      if (psiElement.toString().startsWith("PsiDirectory:")) {
        return false;
      }
    }
  }

  private String getFunction(PsiElement psiElement) {
    if(hasFunction(psiElement)) {

      while(true) {
        psiElement = psiElement.getParent();

        if (psiElement == null) {
          return null;
        }

        if(psiElement.toString().startsWith("PyFunction('")) {
          return psiElement.toString().substring(12, psiElement.toString().length()-2);
        }

        if(psiElement.toString().startsWith("PsiDirectory:")) {
          return null;
        }
      }
    }
    else if(isInMainFunction(psiElement)) {
      return "[global]";
    }
    else {
      return "[global]";
    }

  }

  private String getDirectory(Document document) {
    return document.toString().substring(
            document.toString().indexOf("[")+8,           // [file://
            document.toString().lastIndexOf("]"));
  }

  // after checking if it has a function ->
  // true: is in main
  // false: is global
  private boolean isInMainFunction(PsiElement psiElement) {
    if(!psiElement.getNavigationElement().toString().startsWith("PyTargetExpression")) {
      return false;
    }

    while(true) {
      psiElement = psiElement.getParent();

      if(psiElement == null) {
        return false;
      }

      if(psiElement.toString().startsWith("PyIfPartIf")) {
        if(psiElement.getText().startsWith("if __name__ == '__main__'") ||
                psiElement.getText().startsWith("if __name__ == \"__main__\"")) {
          return true;
        }
      }

      if(psiElement.toString().startsWith("PsiDirectory:")) {
        return false;
      }
    }
  }

  private int getActualTextLength(PsiElement psiElement) {
    String text = psiElement.getText();
    if(text.contains(".")) {
      text = text.substring(text.lastIndexOf(".")+1);
    }
    if(text.contains(":")) {
      text = text.substring(0,text.indexOf(":"));
    }

    return text.length();
  }

  private void giveTheUnderscoresBack(String[] strings,
                                      boolean doubleUnderscoreStart,
                                      boolean singleUnderscoreStart,
                                      boolean doubleUnderscoreEnd,
                                      boolean singleUnderscoreEnd) {
    if(doubleUnderscoreStart) {
      for(int i = 0; i < strings.length; i++) {
        strings[i] = "__" + strings[i];
      }
    }
    else if(singleUnderscoreStart) {
      for(int i = 0; i < strings.length; i++) {
        strings[i] = "_" + strings[i];
      }
    }

    if(doubleUnderscoreEnd) {
      for(int i = 0; i < strings.length; i++) {
        strings[i] = strings[i] + "__";
      }
    }
    else if(singleUnderscoreEnd) {
      for(int i = 0; i < strings.length; i++) {
        strings[i] = strings[i] + "_";
      }
    }

  }

  private int findIdenticalName(PsiElement psiElement, String name, int value) {
    if(psiElement.toString().startsWith("PyTargetExpression") ||
       psiElement.toString().startsWith("PyReferenceExpression")) {
      String variable = psiElement.getText();

      if(variable.equals(name)) {
        System.out.println(variable + " == " + name);
        return 1;
      }
      else {
        System.out.println(variable + " != " + name);
        return 0;
      }
    }
    else if(psiElement.getChildren().length > 0) {
      for(PsiElement child : psiElement.getChildren()) {
        value = value + findIdenticalName(child, name, value);
      }
    }
    return value;
  }

  private void captializeStringArray(String[] strings) {
    for (int i = 0; i < strings.length; i++) {
      strings[i] = strings[i].toUpperCase();
    }
  }

  private boolean hasDuplicateName(PsiElement psiElement, String name) {
    PsiElement currentPsiElement = psiElement;
    while(true) {
      currentPsiElement = currentPsiElement.getParent();
      System.out.println(currentPsiElement.toString());

      if(currentPsiElement.toString().startsWith("PyFile")) {
        System.out.println(currentPsiElement);
        break;
      }
    }
    return findIdenticalName(currentPsiElement, name, 0) > 0;
  }

  /**
   * Displays a message with information about the current caret.
   *
   * @param e Event related to this action
   */
  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
    Project project = e.getProject();



    if(e.getData(CommonDataKeys.PSI_ELEMENT) != null) {
      Document document = editor.getDocument();
      PsiElement psiElement = e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
      if(psiElement.getNavigationElement().toString().startsWith("PyTargetExpression") ||
         psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
        Query<PsiReference> query = ReferencesSearch.search(psiElement);


        int maxLength;
        // TODO: Handle it on the other side!! Optimize if necessary. Should return a string array
        // ENTRYPOINT IS HERE

        boolean doubleUnderscoreStart = false;
        boolean singleUnderscoreStart = false;
        boolean doubleUnderscoreEnd = false;
        boolean singleUnderscoreEnd = false;


        String variableName = psiElement.getText();
        if(variableName.startsWith("__")) {
          doubleUnderscoreStart = true;
        }
        else if(variableName.startsWith("_")) {
          singleUnderscoreStart = true;
        }

        if(variableName.endsWith("__")) {
          doubleUnderscoreEnd = true;
        }
        else if(variableName.endsWith("_")) {
          singleUnderscoreEnd = true;
        }

        boolean isAllCapitalized = true;
        for(int i = 0; i < variableName.length(); i++) {
          if (variableName.charAt(i) >= 'a' && variableName.charAt(i) <= 'z') {
            isAllCapitalized = false;
            break;
          }
        }

        String varName = getVariableName(psiElement);

//        System.out.println("Final result: " + getType(psiElement));
        String type = getType(psiElement);

        String[] strings = App.getSuggestedVariableNames(
                new File(Global.fullFolderPath), Global.cacheMap,
             varName, getFunction(psiElement), getDirectory(document), type);


        if(strings == null || strings.length == 0) {
          Messages.showInfoMessage("Nothing found", "Warning");
        }
        else if(strings.length == 1 && strings[0].equals(Global.ALREADY_EXISTS)) {
          Messages.showInfoMessage("String is already in full English", "Info");
        }
        else if(strings.length == 1 && strings[0].equals(Global.ALREADY_EXISTS+"s")) {
          Messages.showInfoMessage("String is already in full English", "Info");
        }
        else {
          List<String> fixList = new ArrayList<>();
          for(String string : strings) {
            if(string != null) {
              fixList.add(string);
            }
          }
          strings = fixList.toArray(new String[0]);

          // leading and trailing underscores are removed internally in the backend
          // they must be restored here.
          giveTheUnderscoresBack(strings, doubleUnderscoreStart,
                  singleUnderscoreStart, doubleUnderscoreEnd, singleUnderscoreEnd);

          // visual glitch: needs to be a__b to show as a_b
          if(strings.length > 3) {
            String[] temporaryArray = new String[3];
            System.arraycopy(strings, 0, temporaryArray, 0, 3);
            strings = temporaryArray;
          }
          String[] showed_strings = doubleUnderscoreArray(strings);
          if(isAllCapitalized) {
            captializeStringArray(showed_strings);
          }


          int x = Messages.showDialog(project,"Replace the variable name\n" +
                          "with one of the options below:\n"+
                  "(may require multiple steps with multi-word variables)",
                  "Replacing the Variable",
                  showed_strings,0,null); // the one you want
          if(x >= 0) {
            PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Comparator.reverseOrder());
            boolean hasQuery = false;
            for(PsiReference psiReference : query) {
//              System.out.println(psiReference.getElement());
              // queue it properly! from bottom to top
              int extraOffset = 0;
              if(psiReference.getElement().getNavigationElement().toString().startsWith(
                      "PyReferenceExpression")) {
                if(psiReference.getElement().getText().contains(".")) {
                  String string = psiReference.getElement().getText().substring(
                          0,psiReference.getElement().getText().lastIndexOf(".")+1);
                  extraOffset = string.length();
                }
              }
              priorityQueue.add(psiReference.getElement().getTextOffset()+extraOffset);
              hasQuery = true;
            }
            if(!hasQuery) {
              Messages.showInfoMessage(
                      "IDE does not support renaming on external documents\n" +
                              "Consider to move the external file to the project", "Info");
              return;
            }


            boolean hasSameName = hasDuplicateName(psiElement, strings[x]);
            // handle case if queue is empty
            int yes = 0; // 0 means yes in the default yes/no dialog
            if(hasSameName) {
              yes = Messages.showYesNoDialog("Are you sure about this? " +
                              "Another variable name has the same name.",
                      "Warning", null);
            }

            if(yes == 0) {
              if(psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
                priorityQueue.add(psiElement.getTextOffset());
              }

              maxLength = getActualTextLength(psiElement);

              while(!priorityQueue.isEmpty()) {
                int offset = priorityQueue.poll();

                String replacementWord = strings[x];
                if(isAllCapitalized) {
                  replacementWord = replacementWord.toUpperCase();
                }
                String finalReplacementWord = replacementWord;
                WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(
                        offset, offset + maxLength, finalReplacementWord));
              }
            }

          }
        }
      }
    }
  }

  /**
   * Sets visibility and enables this action menu item if:
   * <ul>
   *   <li>a project is open</li>
   *   <li>an editor is active</li>
   * </ul>
   *
   * @param e Event related to this action
   */
  @Override
  public void update(@NotNull final AnActionEvent e) {
    // TODO: Do not show it if certain keywords are detected (self, cls, kwargs, args..)
    // Get required data keys
    if(!PersistentData.getInstance().firstStart) {
      e.getPresentation().setEnabledAndVisible(false);
    }
    else {
      final Project project = e.getProject();
      final Editor editor = e.getData(CommonDataKeys.EDITOR);
      //Set visibility only in case of existing project and editor
      if(e.getData(CommonDataKeys.PSI_ELEMENT) != null && Global.loadingFinished) {
        PsiElement psiElement = e.getRequiredData(CommonDataKeys.PSI_ELEMENT);
        if(psiElement.getNavigationElement().toString().startsWith("PyTargetExpression") ||
                psiElement.getNavigationElement().toString().startsWith("PyNamedParameter")) {
          String variableName = getVariableName(psiElement);
          if(variableName == null) {
            e.getPresentation().setEnabledAndVisible(false);
          }
          else if(variableName.equals("self")     || variableName.startsWith("self.") ||
                  variableName.equals("cls")      || variableName.startsWith("cls.")  ||
                  variableName.equals("kwargs")   || variableName.equals("*kwargs") ||
                  variableName.equals("**kwargs") || variableName.equals("_") ||
                  variableName.equals("args")     || variableName.equals("*args") ||
                  variableName.equals("**args")) {
            e.getPresentation().setEnabledAndVisible(false);
          }
          else {
            e.getPresentation().setEnabledAndVisible(
                    project != null &&
                            editor != null &&
                            !editor.getSelectionModel().hasSelection());
          }
        }
        else {
          e.getPresentation().setEnabledAndVisible(false);
        }
      }
      else {
        e.getPresentation().setEnabledAndVisible(false);
      }
    }

  }

}



// ignore intelligent word selection now.
//