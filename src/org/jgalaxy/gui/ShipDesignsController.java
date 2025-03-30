package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.EProduceType;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;
import org.jgalaxy.units.JG_UnitDesign;

import java.net.URL;
import java.util.ResourceBundle;

public class ShipDesignsController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;

  @FXML private TextField mName;
  @FXML private Spinner<Double> mDrive;
  @FXML private Spinner<Double> mWeapons;
  @FXML private Spinner<Integer> mNrWeapons;
  @FXML private Spinner<Double> mShields;
  @FXML private Spinner<Double> mCargo;

  @FXML private Button mBuild;

  @FXML private TextField mNewFleet;
  @FXML private Button mCreateFleet;

  private IJG_Faction mFaction;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    mDrive.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,9999,0));
    mWeapons.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,9999,0));
    mNrWeapons.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,9999,0));
    mShields.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,9999,0));
    mCargo.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0,9999,0));

    mBuild.setOnAction(e -> {
      IJG_UnitDesign design = JG_UnitDesign.of(
        mName.getText(),
        mName.getText(),
        mDrive.getValue(),
        mWeapons.getValue(),
        mNrWeapons.getValue(),
        mShields.getValue(),
        mCargo.getValue()
      );
      Global.CURRENTFACTION_CHANGED.get().addUnitDesign(design);
      return;
    });

    mCreateFleet.setOnAction(e -> {
       mFaction.groups().addFleet( mNewFleet.getText(), mNewFleet.getText() );
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void setFaction( IJG_Faction pFaction ) {
    mFaction = pFaction;
    refresh();
    return;
  }

  public void refresh() {
    return;
  }


}
