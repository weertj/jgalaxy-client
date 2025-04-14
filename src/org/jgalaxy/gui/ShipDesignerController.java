package org.jgalaxy.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JPanelInterface;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.system.JavelinSystem;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;
import org.jgalaxy.units.JG_UnitDesign;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ShipDesignerController extends JPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;

  @FXML private Label  mDriveTech;

  @FXML private TextField   mDrive;
  @FXML private TextField   mWeapons;
  @FXML private TextField   mWeaponsNr;
  @FXML private TextField   mShields;
  @FXML private TextField   mCargo;

  @FXML private Label mMass;
  @FXML private Label mSpeed;
  @FXML private Label mShieldsValue;
  @FXML private Label mResistant;
  @FXML private Label mKillChance;

  @FXML private TextField   mAgainstWeapon;

  @FXML private ListView<IJG_UnitDesign> mOurDesigns;
  @FXML private ListView<IJG_UnitDesign> mOtherDesigns;


  private IJG_Faction mFaction;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
      String newText = change.getControlNewText();
      if (newText.matches("\\d*(\\.\\d*)?")) {
        return change;
      }
      return null;
    };
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
      String newText = change.getControlNewText();
      if (newText.matches("\\d*")) {
        return change;
      }
      return null;
    };
    mDrive.setTextFormatter(new TextFormatter<>(doubleFilter));
    mDrive.setOnKeyTyped( _ -> refreshUnitDesign());
    mWeapons.setTextFormatter(new TextFormatter<>(doubleFilter));
    mWeapons.setOnKeyTyped( _ -> refreshUnitDesign());
    mWeaponsNr.setTextFormatter(new TextFormatter<>(integerFilter));
    mWeaponsNr.setOnKeyTyped( _ -> refreshUnitDesign());
    mShields.setTextFormatter(new TextFormatter<>(doubleFilter));
    mShields.setOnKeyTyped( _ -> refreshUnitDesign());
    mCargo.setTextFormatter(new TextFormatter<>(doubleFilter));
    mCargo.setOnKeyTyped( _ -> refreshUnitDesign());

    mAgainstWeapon.setTextFormatter(new TextFormatter<>(doubleFilter));
    mAgainstWeapon.setOnKeyTyped( _ -> refreshUnitDesign());
    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  private void refreshUnitDesign() {
    IJG_UnitDesign unitDesign = JG_UnitDesign.of("test",
      "test",
      Double.parseDouble(mDrive.getText()),
      Double.parseDouble(mWeapons.getText()),
      Integer.parseInt(mWeaponsNr.getText()),
      Double.parseDouble(mShields.getText()),
      Double.parseDouble(mCargo.getText())
    );
    Effects.setValue(mMass, unitDesign.mass());
    Effects.setValue(mSpeed, unitDesign.speed(mFaction.tech(),0));
    Effects.setValue(mShieldsValue, unitDesign.effectiveShield(mFaction.tech()));

    Effects.setValue( mKillChance, JG_UnitDesign.killChance( Double.parseDouble(mAgainstWeapon.getText()), unitDesign.effectiveShield(mFaction.tech()) ) );

    return;
  }

  /**
   * refresh
   */
  public void refresh() {
    mOurDesigns.getItems().clear();
    mOurDesigns.getItems().addAll(mFaction.unitDesigns());
    mOtherDesigns.getItems().clear();
    for( var other : mFaction.getOtherFactionsMutable() ) {
      for( var ud : other.unitDesigns() ) {
        mOtherDesigns.getItems().add(ud);
      }
    }
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
