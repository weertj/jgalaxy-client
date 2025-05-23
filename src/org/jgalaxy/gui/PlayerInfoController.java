package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.fxml.FXMLLoad;
import org.javelinfx.system.JavelinSystem;
import org.javelinfx.window.S_Pane;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Player;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;

  @FXML private ImageView mBanner;
  @FXML private Label mFactionName;

  @FXML private Label mDriveTech;
  @FXML private Label mWeaponsTech;
  @FXML private Label mShieldsTech;
  @FXML private Label mCargoTech;

  @FXML private Button mShipDesignerButton;
  @FXML private TextField mRightMouseSendNumber;

  @FXML private Button mUserButton;
  @FXML private ImageView mUserIcon;

//  private IJG_Player mPlayer;
  private IJG_Faction mFaction;

  private ShipDesignerController mController;

  private Stage mStage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Global.CURRENTSENDNUMBER.bindBidirectional(mRightMouseSendNumber.textProperty());

    SButtons.initButton(mShipDesignerButton, e -> {
      try { // **** ShipDesigner
        var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/ShipDesigner.fxml", null);
        mController = (ShipDesignerController)FXMLLoad.controller(contents);
        mStage = new Stage();
        mStage.setTitle("Ship Designer");
        Scene scene = new Scene(mController.rootPane());
        scene.getStylesheets().add(JavelinSystem.stylesheet().file().toURI().toString());
        mStage.setScene(scene);
        mController.setThisStage(mStage);
        mController.setFaction(Global.GAMECONTEXT.currentFactionChanged());
        mStage.show();
      } catch (Throwable t) {
        t.printStackTrace();
      }

    });

    SButtons.initButton(mUserButton, e -> {

    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    if (mFaction==null) {
      mFactionName.setText("-");
      mDriveTech.setText("-");
      mWeaponsTech.setText("-");
      mShieldsTech.setText("-");
      mCargoTech.setText("-");
    } else {
      mBanner.setImage( Global.GAMECONTEXT.imageForFaction(mFaction));
      mFactionName.setText(mFaction.name());
      mDriveTech.setText("" + mFaction.tech().drive());
      mWeaponsTech.setText("" + mFaction.tech().weapons());
      mShieldsTech.setText("" + mFaction.tech().shields());
      mCargoTech.setText("" + mFaction.tech().cargo());
    }
    if (Global.GAMECONTEXT.currentPlayerChanged()!=null) {
      mUserButton.setText(Global.GAMECONTEXT.currentPlayerChanged().getUsername());
    }
    return;
  }

//  public void setPlayer( IJG_Player pPlayer ) {
//    mPlayer = pPlayer;
//    refresh();
//    return;
//  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
