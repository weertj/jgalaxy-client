package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.image.SImages;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;
import org.jgalaxy.units.JG_Fleet;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ShipDesignInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TextField   mDesignName;

  @FXML private Label  mDriveTech;

  private IJG_Faction mFaction;
  private IJG_Group   mGroup;
  private boolean     mInRefresh = false;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
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
    if (mGroup==null) {
      mRootPane.setVisible(false);
    } else {
      IJG_UnitDesign design = mFaction.getUnitDesignById(mGroup.unitDesign());
      mRootPane.setVisible(true);
      Effects.setText(mDesignName,mGroup.name());
      Effects.setValue( mDriveTech,mGroup.tech().drive());
    }
    return;
  }

  public void setGroup(IJG_Group pGroup) {
    refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    refresh();
    return;
  }

}
