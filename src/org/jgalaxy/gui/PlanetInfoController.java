package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.IEntity;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.EProduceType;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlanetInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mPlanetName;

  @FXML private Label   mPopulation;
  @FXML private Label   mCol;
  @FXML private Label   mCap;
  @FXML private Label   mMat;
  @FXML private Label   mInd;
  @FXML private ComboBox<String> mProduce;

  @FXML private ListView<IJG_Group> mGroupsInOrbit;
  @FXML private ListView<IJG_Group> mOtherGroupsInOrbit;

  @FXML private Button mAddToFleet;
  @FXML private ComboBox<String> mFleetNames;

  private IJG_Faction mFaction;
  private IJG_Planet  mPlanet;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mProduce.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (mInRefresh) return;
      if (Objects.equals(newValue,EProduceType.PR_CAP.order())) {
        mPlanet.setProduceType(EProduceType.PR_CAP, null);
      } else if (Objects.equals(newValue,EProduceType.PR_MAT.order())) {
        mPlanet.setProduceType(EProduceType.PR_MAT, null);
      } else if (Objects.equals(newValue,EProduceType.PR_DRIVE.order())) {
        mPlanet.setProduceType(EProduceType.PR_DRIVE, null);
      } else if (Objects.equals(newValue,EProduceType.PR_WEAPONS.order())) {
        mPlanet.setProduceType(EProduceType.PR_WEAPONS, null);
      } else if (Objects.equals(newValue,EProduceType.PR_SHIELDS.order())) {
        mPlanet.setProduceType(EProduceType.PR_SHIELDS, null);
      } else if (Objects.equals(newValue,EProduceType.PR_CARGO.order())) {
        mPlanet.setProduceType(EProduceType.PR_CARGO, null);
      } else {
        mPlanet.setProduceType(EProduceType.PR_SHIP,newValue);
      }
    });
    mGroupsInOrbit.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    mGroupsInOrbit.getSelectionModel().getSelectedItems().addListener( (ListChangeListener)c -> {
      Global.SELECTEDGROUPS.clear();
      Global.SELECTEDGROUPS.addAll(mGroupsInOrbit.getSelectionModel().getSelectedItems());
    });
    mPlanetName.setOnAction( e -> mPlanet.setName(mPlanetName.getText()));

    mOtherGroupsInOrbit.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    mOtherGroupsInOrbit.getSelectionModel().getSelectedItems().addListener( (ListChangeListener)c -> {
      Global.SELECTEDGROUPS.clear();
      Global.SELECTEDGROUPS.addAll(mOtherGroupsInOrbit.getSelectionModel().getSelectedItems());
    });

    mAddToFleet.setOnAction(event -> {
      Global.SELECTEDGROUPS.stream().forEach( g -> g.setFleet(mFleetNames.getSelectionModel().getSelectedItem()) );
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  /**
   * refresh
   */
  public void refresh() {
    try {
      mInRefresh = true;
      if (mPlanet == null) {
        mPlanetName.setText("");
        mPopulation.setText("");
        mCol.setText("");
        mCap.setText("");
        mMat.setText("");
        mInd.setText("");
      } else {
        mPlanetName.setText(mPlanet.name());
        mPopulation.setText(String.valueOf(mPlanet.population()));
        mCol.setText(String.valueOf(mPlanet.cols()));
        mCap.setText(String.valueOf(mPlanet.capitals()));
        mMat.setText(String.valueOf(mPlanet.materials()));
        mInd.setText(String.valueOf(mPlanet.industry()));
        mProduce.getItems().clear();

        mGroupsInOrbit.getItems().clear();
        mOtherGroupsInOrbit.getItems().clear();
        mFleetNames.getItems().clear();
        if (mFaction != null) {
          for (EProduceType produceType : EProduceType.values()) {
            if (produceType != EProduceType.PR_SHIP) {
              mProduce.getItems().add(produceType.order());
            }
          }
          for (IJG_UnitDesign ud : mFaction.unitDesigns()) {
            mProduce.getItems().add(ud.name());
          }
          // **** Add fleets
          for (IJG_Fleet fleet : mFaction.groups().fleets()) {
            if (Objects.equals(fleet.position(), mPlanet.position())) {
              mGroupsInOrbit.getItems().add(fleet);
            }
          }
          // **** Add single groups
          for (IJG_Group group : mFaction.groups().groupsByPosition(mPlanet.position()).getGroups()) {
            if (group.getFleet() == null) {
              mGroupsInOrbit.getItems().add(group);
            }
          }
          for (IJG_Faction other : mFaction.getOtherFactionsMutable()) {
            for (IJG_Group group : other.groups().groupsByPosition(mPlanet.position()).getGroups()) {
              mOtherGroupsInOrbit.getItems().add(group);
            }
          }
          mFaction.groups().fleets().forEach(g -> mFleetNames.getItems().add(g.name()));
        }
        if (mPlanet.produceUnitDesign() == null) {
          if (mPlanet.produceType() == null) {
          } else {
            mProduce.setValue(mPlanet.produceType().order());
          }
        } else {
          mProduce.setValue(mPlanet.produceUnitDesign());
        }
      }
    } finally {
      mInRefresh = false;
    }
    return;
  }

  public void setPlanet(IJG_Planet pPlanet ) {
    mPlanet = pPlanet;
    refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
