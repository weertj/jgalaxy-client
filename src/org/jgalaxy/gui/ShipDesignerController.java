package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.convert.DoubleConvert;
import org.javelinfx.convert.IntegerConvert;
import org.javelinfx.engine.JPanelInterface;
import org.javelinfx.textfield.SNumberField;
import org.javelinfx.textfield.STextField;
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
  @FXML private Label mWeaponsValue;
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

    STextField.initTextField(mDesignName, this::update );
    SNumberField.makeTextfieldPositiveDoubleOnly(mDrive);
    STextField.initTextField(mDrive, this::update );
    SNumberField.makeTextfieldPositiveDoubleOnly(mWeapons);
    STextField.initTextField(mWeapons, this::update );
    SNumberField.makeTextfieldPositiveIntegerOnly(mWeaponsNr);
    STextField.initTextField(mWeaponsNr, this::update );
    SNumberField.makeTextfieldPositiveDoubleOnly(mShields);
    STextField.initTextField(mShields, this::update );
    SNumberField.makeTextfieldPositiveDoubleOnly(mCargo);
    STextField.initTextField(mCargo, this::update );

    SNumberField.makeTextfieldPositiveDoubleOnly(mAgainstWeapon);
    STextField.initTextField(mAgainstWeapon, this::update );

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
        SOrders.addUnitDesign(getDesign());
//        mFaction.addUnitDesign(getDesign());
//        mFaction.newChange();
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
    if (pDesign!=null) {
      mDesignName.setText(pDesign.name());
      Effects.setValueDouble02(mDrive, pDesign.drive());
      Effects.setValueDouble02(mWeapons, pDesign.weapons());
      Effects.setValueInteger(mWeaponsNr, pDesign.nrweapons());
      Effects.setValueDouble02(mShields, pDesign.shields());
      Effects.setValueDouble02(mCargo, pDesign.cargo());
    }
    update();
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
    Effects.setValue(mWeaponsValue, unitDesign.effectiveWeapon(mFaction.tech()));
    Effects.setValue(mShieldsValue, unitDesign.effectiveShield(mFaction.tech()));
    Effects.setValue(mCargoValue, unitDesign.canCarry(mFaction.tech()));

    Effects.setValue( mKillChance, JG_UnitDesign.killChance( Double.parseDouble(mAgainstWeapon.getText()), unitDesign.effectiveShield(mFaction.tech()) ) );

    return;
  }

  public void update() {
    refreshUnitDesign();

    String designName = mDesignName.getText();
    if (designName.isBlank()) {
      mAddDesign.setDisable(true);
    } else {
      if (mFaction.getUnitDesignById(designName)==null) {
        mAddDesign.setDisable(false);
      } else {
        mAddDesign.setDisable(true);
      }
    }

    // **** Weapons check
    if (getDesign().weapons()>0 && getDesign().nrweapons()==0) {
      mWeaponsNr.setText("1");
      update();
    }
    if (getDesign().weapons()==0 && getDesign().nrweapons()!=0) {
      mWeaponsNr.setText("0");
      update();
    }

    Effects.setValue(mResistant, JG_UnitDesign.shieldsImmuneForWeapons(getDesign().effectiveShield(mFaction.tech())));

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
    update();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
