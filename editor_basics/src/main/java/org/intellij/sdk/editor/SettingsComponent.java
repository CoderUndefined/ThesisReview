// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.io.File;

/**
 * Class that contains Swing UI components to be used in the Settings panel
 */
public class SettingsComponent {

  private final JPanel jPanel;
  private final JBTextField cacheFolderField = new JBTextField();
  private final JBTextField fullFolderField = new JBTextField();
  private final JBTextField dictionaryField = new JBTextField();
  private final JBTextField abbreviationField = new JBTextField();

  /**
   * Generate the Swing UI. The UI is simple, consisting of two text fields.
   * For each text field there is a file chooser, which can select a directory
   */
  public SettingsComponent() {
    JButton cacheButton = new JButton("Set cache folder location");
    cacheButton.addActionListener(actionEvent -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File("."));
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int x = fileChooser.showOpenDialog(getJPanel());
      if(x == JFileChooser.APPROVE_OPTION) {
        setCacheFolderField(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    JButton fullButton = new JButton("Set full folder location");
    fullButton.addActionListener(actionEvent -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File("."));
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int x = fileChooser.showOpenDialog(getJPanel());
      if(x == JFileChooser.APPROVE_OPTION) {
        setFullFolderField(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    JButton dictionaryButton = new JButton("Set dictionary location");
    dictionaryButton.addActionListener(actionEvent -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File("."));
//      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int x = fileChooser.showOpenDialog(getJPanel());
      if(x == JFileChooser.APPROVE_OPTION) {
        setDictionaryField(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    JButton abbreviationButton = new JButton("Set abbreviation location");
    abbreviationButton.addActionListener(actionEvent -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File("."));
//      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int x = fileChooser.showOpenDialog(getJPanel());
      if(x == JFileChooser.APPROVE_OPTION) {
        setAbbreviationField(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    jPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Cache folder location: "), cacheFolderField, 1, false)
            .addComponent(cacheButton)

            .addLabeledComponent(new JBLabel("Full folder location: "), fullFolderField, 1, false)
            .addComponent(fullButton)

            .addLabeledComponent(new JBLabel("Dictionary location: "), dictionaryField, 1, false)
            .addComponent(dictionaryButton)

            .addLabeledComponent(new JBLabel("Abbreviation list location: "), abbreviationField, 1, false)
            .addComponent(abbreviationButton)

            .addComponent(new JLabel("Effects take place after restarting the IDE"))
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();


  }

  /**
   * Get the parent JPanel
   *
   * @return jPanel from SettingsComponent
   */
  public JPanel getJPanel() {
    return jPanel;
  }

  /**
   * Get the string from the text field "Cache folder location: "
   *
   * @return string from the text field "Cache folder location: "
   */
  public String getCacheFolderField() {
    return cacheFolderField.getText();
  }

  /**
   * Sets the text field "Cache folder location: "
   *
   * @param string string to fill the text field "Cache folder location: "
   */
  public void setCacheFolderField(String string) {
    cacheFolderField.setText(string);
  }

  /**
   * Get the string from the text field "Full folder location: "
   *
   * @return string from the text field "Full folder location: "
   */
  public String getFullFolderField() {
    return fullFolderField.getText();
  }

  /**
   * Sets the text field "Full folder location: "
   *
   * @param string string to fill the text field "Full folder location: "
   */
  public void setFullFolderField(String string) {
    fullFolderField.setText(string);
  }

  public String getDictionaryField() {
    return dictionaryField.getText();
  }

  public void setDictionaryField(String string) {
    dictionaryField.setText(string);
  }

  public String getAbbreviationField() {
    return abbreviationField.getText();
  }

  public void setAbbreviationField(String string) {
    abbreviationField.setText(string);
  }
}
