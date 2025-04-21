package org.jgalaxy.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.javelinfx.colors.SColors;
import org.javelinfx.system.JavelinSystem;
import org.jgalaxy.IEntity;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.utils.GEN_Math;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class Effects {

  static public DecimalFormat DOUBLE02;

  static public Paint createBackground( Color pColor, boolean pHorizontal ) {
    Color startColor = pColor;
    Color endColor = Color.TRANSPARENT;
    if (pHorizontal) {
      return new LinearGradient(0, 0, 1, 0, true, null, new Stop(0, startColor), new Stop(1, endColor));
    } else {
      return new LinearGradient(0, 0, 0, 1, true, null, new Stop(0, startColor), new Stop(1, endColor));
    }
  }

  static private void generateFolder( Pane pPane, IEntity pEntity, String pText ) {
    pPane.setBackground(new Background(
      new BackgroundFill(createBackground(Colors.planetUIColor(),true), new CornerRadii(10.0,false), null )));
    Label t = new Label(pEntity.name());
    t.setText(pText);
    t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
    pPane.getChildren().addAll(t);
    return;
  }

  /**
   * setTreePaneFolder
   * @param pPane
   * @param pEntity
   */
  static public void setTreePaneFolder(Pane pPane, IEntity pEntity ) {

    if (Objects.equals("Planets",pEntity.entityType())) {
      generateFolder(pPane,pEntity,"Planets" );
      return;

    } else if (Objects.equals("Planets/Uninhabited",pEntity.entityType())) {
      generateFolder(pPane,pEntity,"Uninhabited" );
      return;

    } else if (Objects.equals("Planets/Inhabited",pEntity.entityType())) {
      generateFolder(pPane,pEntity,"Inhabited" );
      return;

    } else if (Objects.equals("Planets/Unknown",pEntity.entityType())) {
      generateFolder(pPane,pEntity,"Unknown" );
      return;

    } else if (Objects.equals("Planets/Own",pEntity.entityType())) {
      generateFolder(pPane,pEntity,"Ours" );
      return;

    } else if (Objects.equals("Groups",pEntity.entityType())) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.groupUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(pEntity.name());
      t.setText("Groups");
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
      pPane.getChildren().addAll(t);
      return;

    } else if (Objects.equals("Fleets",pEntity.entityType())) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.fleetUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(pEntity.name());
      t.setText("Fleets");
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
      pPane.getChildren().addAll(t);
      return;

    } else if (Objects.equals("Battles",pEntity.entityType())) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.battlesUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(pEntity.name());
      t.setText("Battles");
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
      pPane.getChildren().addAll(t);
      return;

    } else if (Objects.equals("Faction",pEntity.entityType()) && (pEntity instanceof IJG_Faction faction)) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.factionUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(pEntity.name());
      t.setText(faction.name());
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
      pPane.getChildren().addAll(t);
      return;

    } else if (Objects.equals("Factions",pEntity.entityType())) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.factionUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(pEntity.name());
      t.setText("Factions");
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
      pPane.getChildren().addAll(t);
      return;

    } else if (pEntity instanceof IJG_Faction faction) {

      pPane.setBackground(new Background(
        new BackgroundFill(Colors.factionUIColor().deriveColor(0,0,0,0.5), null, null)));
//      pPane.setBorder( new Border(new BorderStroke(Colors.factionUIColor(),BorderStrokeStyle.SOLID,new CornerRadii(10.0,false),new BorderWidths(1))));
      Label t = new Label(faction.name() + " (" + GEN_Math.round02(faction.getReconTotalPop()) + ")");
      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(Colors.factionUIColor()));
      pPane.getChildren().addAll(t);


//      pPane.setBackground(new Background(
//        new BackgroundFill(Colors.factionUIColor().desaturate(), new CornerRadii(10.0, false), null)));
//      Label t = new Label(faction.name() + " (" + GEN_Math.round02(faction.getReconTotalPop()) + ")");
//      t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUNDLIGHT));
//      pPane.getChildren().addAll(t);
      return;

    } else if (pEntity instanceof IJG_Fleet fleet) {
      pPane.setBackground(new Background(
        new BackgroundFill(createBackground(Colors.fleetUIColor(),true), new CornerRadii(10.0,false), null )));
      Label t = new Label(fleet.name() );
      t.getStyleClass().add("label-value");
      Label v = new Label("" + fleet.getNumberOf() + "x ships");
      v.getStyleClass().add("label-value-value");
      v.setLayoutX(20);
      pPane.getChildren().addAll( t);


    } else if (pEntity instanceof IJG_Group group) {
//      pPane.setBackground(new Background(
//        new BackgroundFill(createBackground(Colors.groupUIColor(),true), new CornerRadii(10.0,false), null )));
      Rectangle c = new Rectangle(24,16);
      c.setLayoutX(0);
      c.setLayoutY(0);
      c.setFill(Colors.colorForMyFaction(group));
      Label t = new Label(group.name() );
      t.getStyleClass().add("label-value-dark");
      Label v = new Label("" + group.getNumberOf() + "x " + group.unitDesign());
      v.getStyleClass().add("label-value-value");
      v.setLayoutX(25);
      pPane.getChildren().addAll(c, t,v);


    }
    return;
  }

  static public void setText( Label pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setText(TextField pValueLabel, String text ) {
    pValueLabel.setText( text );
    return;
  }

  static public void setValueDouble02(Node pValueLabel, Double pValue) {
    if (DOUBLE02==null) {
      DOUBLE02 = (DecimalFormat)NumberFormat.getNumberInstance(JavelinSystem.getLocale());
      DOUBLE02.applyPattern("##0.00");
    }
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      if (pValue<0) {
        if (pValueLabel instanceof Label l) {
          l.setText("-");
        } else if (pValueLabel instanceof TextField tf) {
          tf.setText("-");
        }
      } else {
        String v = DOUBLE02.format(pValue);
        if (pValueLabel instanceof Label l) {
          l.setText(v);
        } else if (pValueLabel instanceof TextField tf) {
          tf.setText(v);
        }
      }
    }
    return;
  }

  static public void setValueInteger(Node pValueLabel, Integer pValue) {
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      if (pValue<0) {
        if (pValueLabel instanceof Label l) {
          l.setText("-");
        } else if (pValueLabel instanceof TextField tf) {
          tf.setText("-");
        }
      } else {
        if (pValueLabel instanceof Label l) {
          l.setText(""+pValue.intValue());
        } else if (pValueLabel instanceof TextField tf) {
          tf.setText(""+pValue.intValue());
        }
      }
    }
    return;
  }

  static public void setValue(Label pValueLabel, Number pValue) {
    if (pValue==null) {
      pValueLabel.setVisible(false);
    } else {
      pValueLabel.setVisible(true);
      if (pValue==null || pValue.intValue()<0) {
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
