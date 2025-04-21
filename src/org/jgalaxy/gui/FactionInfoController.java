package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.common.IC_Message;
import org.jgalaxy.engine.IJG_Faction;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FactionInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private AnchorPane  mUIPane;
  @FXML private TextField   mFactionName;

  @FXML private ImageView   mFactionBanner;

  @FXML private Button mDeclareWar;
  @FXML private Button mDeclarePeace;

  @FXML private TextArea mMessagesReceived;

  private IJG_Faction mFaction;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mUIPane.setBackground(new Background(
      new BackgroundFill(Effects.createBackground(Colors.factionUIColor().darker(),false), new CornerRadii(10.0,false), null )));
    mFactionName.setOnAction( e -> {
      mFaction.setName(mFactionName.getText());
      mFaction.newChange();
    });
    SButtons.initButton(mDeclareWar, _ -> {
      Global.CURRENTFACTION_CHANGED.get().addWarWith(mFaction.id());
      mFaction.newChange();
    });
    SButtons.initButton(mDeclarePeace, _ -> {
      Global.CURRENTFACTION_CHANGED.get().removeWarWith(mFaction.id());
      mFaction.newChange();
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

      Image banner = Global.BANNERS.get(mFaction.id());
      mFactionBanner.setImage(banner);

      IJG_Faction myFaction = Global.CURRENTFACTION_CHANGED.get();

      boolean sameFaction = Objects.equals(mFaction.id(),myFaction.id());
      mFactionName.setEditable(sameFaction);
      mDeclareWar.setVisible(!sameFaction);
      mDeclarePeace.setVisible(!sameFaction);

      mDeclarePeace.disableProperty().bind(mDeclareWar.disableProperty().not());
      mDeclareWar.setDisable(myFaction.atWarWith().contains(mFaction.id()));


      String messages = "";
      for(IC_Message message : mFaction.getMessagesMutable()) {
        messages += message.message() + '\n';
      }
      mMessagesReceived.setText(messages);

    } finally {
      mInRefresh = false;
    }

    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    if (pFaction==null) {
      pFaction = Global.CURRENTFACTION_CHANGED.get();
    }
    mFaction = pFaction;
    refresh();
    return;
  }

}
