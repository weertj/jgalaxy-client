package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.convert.DoubleConvert;
import org.javelinfx.convert.IntegerConvert;
import org.javelinfx.engine.JPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.units.IJG_UnitDesign;
import org.jgalaxy.units.JG_UnitDesign;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ShipDesignerController extends JPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private Button      mCloseButton;

  @FXML private Label  mDriveTech;

  @FXML private TextField   mDesignName;

  @FXML private TextField   mDrive;
  @FXML private TextField   mWeapons;
  @FXML private TextField   mWeaponsNr;
  @FXML private TextField   mShields;
  @FXML private TextField   mCargo;
  @FXML private Button      mAddDesign;

  @FXML private Label mMass;
  @FXML private Label mSpeed;
  @FXML private Label mSpeedLoaded;
  @FXML private Label mShieldsValue;
  @FXML private Label mCargoValue;
  @FXML private Label mResistant;
  @FXML private Label mKillChance;

  @FXML private TextField   mAgainstWeapon;

  @FXML private ListView<IJG_UnitDesign> mOurDesigns;
  @FXML private ListView<IJG_UnitDesign> mOtherDesigns;


  private IJG_Faction mFaction;
  private boolean     mInRefresh = false;

  /**
   * initialize
   * @param location
   * The location used to resolve relative paths for the root object, or
   * {@code null} if the location is not known.
   *
   * @param resources
   * The resources used to localize the root object, or {@code null} if
   * the root object was not localized.
   */
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

    mOurDesigns.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
      setDesign(newValue);
      return;
    });
    mOtherDesigns.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
      setDesign(newValue);
      return;
    });

    SButtons.initButton(mAddDesign, _ -> {
      if (getDesign().mass()>0) {
        mFaction.addUnitDesign(getDesign());
        mFaction.newChange();
        refresh();
      }
    });
    SButtons.initButton(mCloseButton, _ -> {
      getThisStage().close();
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  private void setDesign( IJG_UnitDesign pDesign) {
    mDesignName.setText(pDesign.name());
    Effects.setValueDouble02(mDrive,pDesign.drive());
    Effects.setValueDouble02(mWeapons,pDesign.weapons());
    Effects.setValueInteger(mWeaponsNr, pDesign.nrweapons());
    Effects.setValueDouble02(mShields,pDesign.shields());
    Effects.setValueDouble02(mCargo,pDesign.cargo());
    return;
  }

  private IJG_UnitDesign getDesign() {
    IJG_UnitDesign unitDesign = JG_UnitDesign.of(mDesignName.getText(),
      mDesignName.getText(),
      DoubleConvert.convert(mDrive.getText(),0),
      DoubleConvert.convert(mWeapons.getText(),0),
      IntegerConvert.convert(mWeaponsNr.getText(),0),
      DoubleConvert.convert(mShields.getText(),0),
      DoubleConvert.convert(mCargo.getText(),0)
    );
    return unitDesign;
  }

  private void refreshUnitDesign() {
    IJG_UnitDesign unitDesign = getDesign();
    Effects.setValue(mMass, unitDesign.mass());
    Effects.setValue(mSpeed, unitDesign.speed(mFaction.tech(),0));
    Effects.setValue(mSpeedLoaded, unitDesign.speed(mFaction.tech(),unitDesign.canCarry(mFaction.tech())));
    Effects.setValue(mShieldsValue, unitDesign.effectiveShield(mFaction.tech()));
    Effects.setValue(mCargoValue, unitDesign.canCarry(mFaction.tech()));

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
