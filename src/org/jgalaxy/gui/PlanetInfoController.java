package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.EProduceType;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_UnitDesign;

import java.net.URL;
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

  private IJG_Faction mFaction;
  private IJG_Planet  mPlanet;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mProduce.valueProperty().addListener((observable, oldValue, newValue) -> {
      mPlanet.setProduceType(null, newValue );
    });
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
    mCap.setText(String.valueOf(mPlanet.capitals()));
    mMat.setText(String.valueOf(mPlanet.materials()));
    mInd.setText(String.valueOf(mPlanet.industry()));
    mProduce.setValue(mPlanet.produceUnitDesign());
    mProduce.getItems().clear();
    if (mFaction!=null) {
      for( EProduceType produceType : EProduceType.values()) {
        if (produceType!=EProduceType.PR_SHIP) {
          mProduce.getItems().add(produceType.order());
        }
      }
      for(IJG_UnitDesign ud : mFaction.unitDesigns()) {
        mProduce.getItems().add(ud.name());
      }
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
