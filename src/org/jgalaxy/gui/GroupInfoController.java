package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
  @FXML private Label       mGroupName;

  @FXML private Label       mTransitStatus;
  @FXML private Button      mTargetPlanet;

  @FXML private Label       mConfiguration;

  @FXML private AnchorPane mFleetPane;
  @FXML private TextField  mAmountToFleet;
  @FXML private ComboBox<IJG_Fleet> mFleets;

  @FXML private AnchorPane mShipDesignPane;

  @FXML private AnchorPane mCargoPane;
  @FXML private ImageView  mCargoHeaderImage;
  @FXML private Label      mCargoAmountLoaded;
  @FXML private Label      mCargoType;

  @FXML private AnchorPane mPlanetCargoPane;
  @FXML private ImageView  mColsHeaderImage;
  @FXML private Label      mColsAvailableLabel;
  @FXML private TextField  mNumberOfColsToBeLoaded;
  @FXML private Button     mLoadColsButton;
  @FXML private Label      mCapsAvailableLabel;
  @FXML private TextField  mNumberOfCapsToBeLoaded;
  @FXML private Button     mLoadCapsButton;
  @FXML private Label      mMatsAvailableLabel;
  @FXML private TextField  mNumberOfMatsToBeLoaded;
  @FXML private Button     mLoadMatsButton;

  @FXML private Label      mDesignDrive;
  @FXML private Label      mDesignWeapons;
  @FXML private Label      mDesignShields;
  @FXML private Label      mDesignCargo;

  @FXML private Button     mUnloadButton;

  private IJG_Faction mFaction;
  private IJG_Group   mGroup;
  private IJG_Planet  mHoverPlanet;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRootPane.setBackground(new Background(
      new BackgroundFill(Effects.createBackground(Colors.groupUIColor().darker(),false), new CornerRadii(10.0,false), null )));

    // **** Fleet
    mFleets.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) -> {
      if (newValue==null) {

      } else {
        try {
          int amount = Integer.parseInt(mAmountToFleet.getText());
          var newgroup = mGroup.breakOffGroup(Global.CURRENTGAMECHANGED.get(), mFaction, mGroup.id(), amount);
          mFaction.groups().addGroupAlways(newgroup);
          newgroup.setFleet(newValue.id());
        } catch (NumberFormatException ne) {
          mGroup.setFleet(newValue.id());
        }
        mFaction.newChange();
      }
    });

//    Image im = SImages.getImage("data/icons/cols.png");
//    mColsHeaderImage.setImage(im);

    mNumberOfColsToBeLoaded.setOnKeyTyped(event -> {
      try {
        Double.parseDouble(mNumberOfColsToBeLoaded.getText());
        mLoadColsButton.setDisable(false);
      } catch (Throwable e) {
        mLoadColsButton.setDisable(true);
      }
    });
    mNumberOfCapsToBeLoaded.setOnKeyTyped(event -> {
      try {
        Double.parseDouble(mNumberOfCapsToBeLoaded.getText());
        mLoadCapsButton.setDisable(false);
      } catch (Throwable e) {
        mLoadCapsButton.setDisable(true);
      }
    });
    mNumberOfMatsToBeLoaded.setOnKeyTyped(event -> {
      try {
        Double.parseDouble(mNumberOfMatsToBeLoaded.getText());
        mLoadMatsButton.setDisable(false);
      } catch (Throwable e) {
        mLoadMatsButton.setDisable(true);
      }
    });

    SButtons.initButton(mLoadColsButton,  _ -> {
      var design = mFaction.getUnitDesignById(mGroup.unitDesign());
      SJG_LoadOrder.loadOrder(mGroup, design, "COL", mHoverPlanet, Double.parseDouble(mNumberOfColsToBeLoaded.getText()));
      mFaction.newChange();
      refresh();
      return;
    });
    SButtons.initButton(mLoadCapsButton,  _ -> {
      var design = mFaction.getUnitDesignById(mGroup.unitDesign());
      SJG_LoadOrder.loadOrder(mGroup, design, "CAP", mHoverPlanet, Double.parseDouble(mNumberOfCapsToBeLoaded.getText()));
      mFaction.newChange();
      refresh();
      return;
    });
    SButtons.initButton(mLoadMatsButton,  _ -> {
      var design = mFaction.getUnitDesignById(mGroup.unitDesign());
      SJG_LoadOrder.loadOrder(mGroup, design, "MAT", mHoverPlanet, Double.parseDouble(mNumberOfMatsToBeLoaded.getText()));
      mFaction.newChange();
      refresh();
      return;
    });

    SButtons.initButton(mUnloadButton,  _ -> {
      SJG_LoadOrder.unloadOrder( Global.CURRENTGAMECHANGED.get(), mGroup, mHoverPlanet, 999999.0 );
      mFaction.newChange();
      refresh();
      return;
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  private void refreshDesign() {
    IJG_UnitDesign design = mFaction.getUnitDesignById(mGroup.unitDesign());
    if (design!=null) {
      Effects.setValueDouble02(mDesignDrive, design.drive());
      if (design.nrweapons() > 0) {
        Effects.setText(mDesignWeapons, design.nrweapons() + "x " + design.weapons());
      } else {
        Effects.setText(mDesignWeapons, "");
      }
      Effects.setValueDouble02(mDesignShields, design.shields());
      Effects.setValueDouble02(mDesignCargo, design.cargo());
    }
    return;
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

      refreshDesign();

      // **** Transit
      if (mHoverPlanet==null) {
        mTransitStatus.setText( "In space at " + mGroup.position().x() + " " + mGroup.position().y() );
        mTargetPlanet.setVisible(false);
      } else {
        mTransitStatus.setText( "In orbit at" );
        mTargetPlanet.setText(mHoverPlanet.name());
        mTargetPlanet.setVisible(true);
      }

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
//          mFleets.getSelectionModel().select(mFaction.groups().getFleetByName(mGroup.getFleet()));
        }
      }

      // **** Cargo
      if (mGroup!=null && mGroup.totalCargoMass()>0.0) {
        mCargoPane.setVisible(true);
        mUnloadButton.setDisable(mHoverPlanet==null);
        refreshGroupCargoPane();
      } else {
        mCargoPane.setVisible(false);
      }

      // **** Cols
      if (mHoverPlanet==null || !Objects.equals(mHoverPlanet.faction(),mFaction.id())) {
        mPlanetCargoPane.setVisible(false);
      } else {
        mPlanetCargoPane.setVisible(true);
        refreshPlanetCargoPane();
      }

    } finally {
      mInRefresh = false;
    }

    return;
  }

  private void refreshGroupCargoPane() {
    Effects.setValueDouble02(mCargoAmountLoaded, mGroup.load());
    Effects.setText(mCargoType, mGroup.loadType());
    return;
  }

  private void refreshPlanetCargoPane() {
    IJG_UnitDesign design = mFaction.getUnitDesignById(mGroup.unitDesign());
    Effects.setValueDouble02(mColsAvailableLabel,mHoverPlanet.cols());
    Effects.setValueDouble02(mCapsAvailableLabel,mHoverPlanet.capitals());
    Effects.setValueDouble02(mMatsAvailableLabel,mHoverPlanet.materials());
    mLoadColsButton.setDisable(mHoverPlanet.cols()<=0);
    mLoadCapsButton.setDisable(mHoverPlanet.capitals()<=0);
    mLoadMatsButton.setDisable(mHoverPlanet.materials()<=0);
    if (design!=null) {
      Effects.setValueDouble02(mNumberOfColsToBeLoaded, Math.min(mHoverPlanet.cols(), mGroup.getNumberOf() * design.canCarry(mGroup.tech())));
      Effects.setValueDouble02(mNumberOfCapsToBeLoaded, Math.min(mHoverPlanet.capitals(), mGroup.getNumberOf() * design.canCarry(mGroup.tech())));
      Effects.setValueDouble02(mNumberOfMatsToBeLoaded, Math.min(mHoverPlanet.materials(), mGroup.getNumberOf() * design.canCarry(mGroup.tech())));
    }
    return;
  }

  public void setGroup(IJG_Group pGroup) {
    mGroup = pGroup;
    mHoverPlanet = mFaction.planets().findPlanetByPosition(mGroup.position());
    refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
