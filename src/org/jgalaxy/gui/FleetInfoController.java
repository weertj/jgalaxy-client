package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.image.SImages;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FleetInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mFleetName;

  @FXML private AnchorPane  mGeneralPane;

  @FXML private AnchorPane  mNewFleetPane;
  @FXML private TextField   mNewFleetName;
  @FXML private Button      mCreateNewFleet;

  private IJG_Faction mFaction;
  private IJG_Fleet   mFleet;
  private IJG_Planet  mHoverPlanet;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRootPane.setBackground(new Background(
      new BackgroundFill(Effects.createBackground(Colors.fleetUIColor().darker(),false), new CornerRadii(10.0,false), null )));

    mCreateNewFleet.disableProperty().bind(mNewFleetName.textProperty().isEmpty());
    SButtons.initButton(mCreateNewFleet, event -> {
      mFaction.groups().addFleet( mNewFleetName.getText(), mNewFleetName.getText() );
      mFaction.newChange();
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

    if (mFleet==null) {
      mGeneralPane.setVisible(false);
      return;
    }

    if (mFaction!=null) {
      mHoverPlanet = mFaction.planets().findPlanetByPosition(mFleet.position());
    }

    try {
      mInRefresh = true;
      Effects.setText(mFleetName,mFleet.name());

    } finally {
      mInRefresh = false;
    }

    return;
  }

  public void setFleet(IJG_Fleet pFleet) {
    mFleet = pFleet;
    refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
