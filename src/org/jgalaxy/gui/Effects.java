package org.jgalaxy.gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.javelinfx.system.JavelinSystem;

public class Effects {

  static public void setText( Label pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setText(TextField pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setValue(Label pValueLabel, Double pValue) {
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      String v = String.format(JavelinSystem.getLocale(),"%.2f",pValue.doubleValue());
      pValueLabel.setText(v);
    }
    return;
  }

  private Effects() {
    return;
  }

}
