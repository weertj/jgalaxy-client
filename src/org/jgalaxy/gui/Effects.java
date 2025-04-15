package org.jgalaxy.gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.javelinfx.system.JavelinSystem;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Effects {

  static public DecimalFormat DOUBLE02;

  static public void setText( Label pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setText(TextField pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setValueDouble02(Label pValueLabel, Double pValue) {
    if (DOUBLE02==null) {
      DOUBLE02 = (DecimalFormat)NumberFormat.getNumberInstance(JavelinSystem.getLocale());
      DOUBLE02.applyPattern("##0.00");
    }
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      if (pValue<0) {
        pValueLabel.setText("-");
      } else {
        String v = DOUBLE02.format(pValue);
        pValueLabel.setText(v);
      }
    }
    return;
  }


  static public void setValue(Label pValueLabel, Double pValue) {
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      if (pValue<0) {
        pValueLabel.setText("-");
      } else {
        String v = String.format(JavelinSystem.getLocale(), "%.2f", pValue.doubleValue());
        pValueLabel.setText(v);
      }
    }
    return;
  }

  private Effects() {
    return;
  }

}
