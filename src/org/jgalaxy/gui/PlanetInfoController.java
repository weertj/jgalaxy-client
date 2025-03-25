package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.planets.IJG_Planet;

import java.net.URL;
import java.util.ResourceBundle;

public class PlanetInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mPlanetName;

  @FXML private Label   mPopulation;
  @FXML private Label   mCol;

  private IJG_Planet mPlanet;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    if (mPlanet==null) {
      mPlanetName.setText("");
    } else {
      mPlanetName.setText(mPlanet.name());
    }
    mPopulation.setText(String.valueOf(mPlanet.population()));
    mCol.setText(String.valueOf(mPlanet.cols()));
    return;
  }

  public void setPlanet(IJG_Planet pPlanet ) {
    mPlanet = pPlanet;
    refresh();
    return;
  }

}
