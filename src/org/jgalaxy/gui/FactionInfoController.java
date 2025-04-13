package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;

import java.net.URL;
import java.util.ResourceBundle;

public class FactionInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mFactionName;

  private IJG_Faction mFaction;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mFactionName.setOnAction( e -> {
      mFaction.setName(mFactionName.getText());
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
