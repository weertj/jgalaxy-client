package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.system.JavelinSystem;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Player;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusBarController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private Label       mMousePosition;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    return;
  }

  public void setMouseMovePosition(ISP_Position pPosition) {
    mMousePosition.setText( String.format( JavelinSystem.getLocale(),  "(%.2f,%.2f)", pPosition.x(), pPosition.y() ));
    return;
  }

  public Label getMousePosition() {
    return mMousePosition;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    return;
  }


}
