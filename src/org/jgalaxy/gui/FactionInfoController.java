package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FactionInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mFactionName;

  @FXML private Button mDeclareWar;
  @FXML private Button mDeclarePeace;

  private IJG_Faction mFaction;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRootPane.setBackground(new Background(
      new BackgroundFill(Effects.createBackground(Colors.factionUIColor().darker(),false), new CornerRadii(10.0,false), null )));
    mFactionName.setOnAction( e -> {
      mFaction.setName(mFactionName.getText());
      mFaction.newChange();
    });
    SButtons.initButton(mDeclareWar, _ -> {
      Global.CURRENTFACTION_CHANGED.get().addWarWith(mFaction.id());
    });
    SButtons.initButton(mDeclarePeace, _ -> {
      Global.CURRENTFACTION_CHANGED.get().removeWarWith(mFaction.id());
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

    if (mFaction==null) {
      mRootPane.setVisible(false);
      return;
    }
    mRootPane.setVisible(true);

    try {
      mInRefresh = true;
      Effects.setText(mFactionName,mFaction.name());

      IJG_Faction myFaction = Global.CURRENTFACTION_CHANGED.get();

      boolean sameFaction = Objects.equals(mFaction.id(),myFaction.id());
      mFactionName.setEditable(sameFaction);
      mDeclareWar.setVisible(!sameFaction);
      mDeclarePeace.setVisible(!sameFaction);

      mDeclarePeace.disableProperty().bind(mDeclareWar.disableProperty().not());
      mDeclareWar.setDisable(myFaction.atWarWith().contains(mFaction.id()));


    } finally {
      mInRefresh = false;
    }

    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
