package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.fxml.FXMLLoad;
import org.javelinfx.image.SImages;
import org.javelinfx.window.S_Pane;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.planets.EProduceType;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;
import org.jgalaxy.units.JG_Fleet;
import org.jgalaxy.utils.GEN_Math;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class GroupInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mGroupName;
  @FXML private Label       mConfiguration;

  @FXML private AnchorPane mFleetPane;
  @FXML private ComboBox<IJG_Fleet> mFleets;

  @FXML private AnchorPane mShipDesignPane;

  @FXML private AnchorPane mCargoPane;
  @FXML private ImageView  mCargoHeaderImage;
  @FXML private Label      mCargoAmountLoaded;

  @FXML private AnchorPane mColsPane;
  @FXML private ImageView  mColsHeaderImage;
  @FXML private Label      mColsAvailableLabel;
  @FXML private TextField  mNumberOfColsToBeLoaded;
  @FXML private Button     mLoadColsButton;

  @FXML private Button     mUnloadButton;

  private IJG_Faction mFaction;
  private IJG_Group   mGroup;
  private IJG_Planet  mHoverPlanet;
  private boolean     mInRefresh = false;

  private ShipDesignInfoController mShipDesignInfoController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    try { // **** ShipDesignInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/ShipDesignInfo.fxml", null);
      mShipDesignInfoController = (ShipDesignInfoController)FXMLLoad.controller(contents);
      mShipDesignPane.getChildren().add(mShipDesignInfoController.rootPane());
    } catch (Throwable e) {
      e.printStackTrace();
    }

    // **** Fleet
    mFleets.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) -> {
      if (newValue==null) {

      } else {
        mGroup.setFleet(newValue.id());
        mFaction.newChange();
      }
    });

    Image im = SImages.getImage("data/icons/cols.png");
    mColsHeaderImage.setImage(im);

    mNumberOfColsToBeLoaded.setOnKeyTyped(event -> {
      try {
        Double.parseDouble(mNumberOfColsToBeLoaded.getText());
        mLoadColsButton.setDisable(false);
      } catch (Throwable e) {
        mLoadColsButton.setDisable(true);
      }
    });

    SButtons.initButton(mLoadColsButton,  _ -> {
      var design = mFaction.getUnitDesignById(mGroup.unitDesign());
      SJG_LoadOrder.loadOrder(mGroup, design, "COL", mHoverPlanet, Double.parseDouble(mNumberOfColsToBeLoaded.getText()));
      refresh();
      return;
    });

    SButtons.initButton(mUnloadButton,  _ -> {
      SJG_LoadOrder.unloadOrder( Global.CURRENTGAMECHANGED.get(), mGroup, mHoverPlanet, 999999.0 );
      refresh();
      return;
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

    if (mGroup==null || mFaction==null) {
      mRootPane.setVisible(false);
      return;
    }
    mRootPane.setVisible(true);

    try {
      mInRefresh = true;
      Effects.setText(mGroupName,mGroup.name());
      Effects.setText(mConfiguration, "" + mGroup.getNumberOf() + "x " + mGroup.unitDesign());

      // **** Fleets
      if (mGroup==null) {
        mFleetPane.setVisible(false);
      } else {
        mFleetPane.setVisible(true);
        mFleets.getItems().clear();
        mFleets.getItems().add(JG_Fleet.of("<no fleet>","<no fleet>", List.of()));
        for( var fleet : mFaction.groups().fleets()) {
          if (fleet.position()==null) {
            mFleets.getItems().add(fleet);
          }
        }
        for( var fleet : mFaction.groups().fleetsByPosition(mGroup.position())) {
          mFleets.getItems().add(fleet);
        }
        if (mGroup.getFleet()==null) {

        } else {
          mFleets.getSelectionModel().select(mFaction.groups().getFleetByName(mGroup.getFleet()));
        }
      }

      // **** Cargo
      if (mGroup!=null && mGroup.totalCargoMass()>0.0) {
        mCargoPane.setVisible(true);
        Effects.setValue(mCargoAmountLoaded, mGroup.totalCargoMass());
      } else {
        mCargoPane.setVisible(false);
      }

      // **** Cols
      if (mHoverPlanet==null || !Objects.equals(mHoverPlanet.faction(),mFaction.id())) {
        mColsPane.setVisible(false);
      } else {
        mColsPane.setVisible(true);
        Effects.setValue(mColsAvailableLabel,mHoverPlanet.cols());
      }
    } finally {
      mInRefresh = false;
    }

    return;
  }

  public void setGroup(IJG_Group pGroup) {
    mGroup = pGroup;
    mShipDesignInfoController.setGroup(pGroup);
    mHoverPlanet = mFaction.planets().findPlanetByPosition(mGroup.position());
    refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    mShipDesignInfoController.setFaction(pFaction);
    refresh();
    return;
  }

}
