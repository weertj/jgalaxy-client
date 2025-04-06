package org.jgalaxy.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.system.JavelinSystem;
import org.jgalaxy.IEntity;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class OOBController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TreeTableView mOOBTree;
  @FXML private TreeTableColumn<IEntity,String> mNumberOf;
  @FXML private TreeTableColumn<IEntity,String> mFGName;
  @FXML private TreeTableColumn<IEntity,String> mShipDesign;
  @FXML private TreeTableColumn<IEntity,String> mPosition;
  @FXML private TreeTableColumn<IEntity,String> mCargo;
  @FXML private TreeTableColumn<IEntity,String> mPlanet;
  @FXML private TreeTableColumn<IEntity,Node> mLoadCOL;
  @FXML private TreeTableColumn<IEntity,Node> mUnload;
  @FXML private TreeTableColumn<IEntity,Node> mWarPeace;

  private IJG_Faction mFaction;
  private TreeItem mRoot;
  private TreeItem mRootOwnFleetsGroups = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Own fleets/groups"; }
  });
  private Map<String,TreeItem> mFactionNodes = new HashMap<>(16);

//  private TreeItem mRootOwnFleetsGroups = new TreeItem<>("Own fleets/groups");

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRoot = new TreeItem<>();
    mOOBTree.setRoot(mRoot);
    mOOBTree.setShowRoot(false);
    mOOBTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    mRoot.getChildren().add(mRootOwnFleetsGroups);
    mNumberOf.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Fleet fleet) {
        return new ReadOnlyStringWrapper("FLEET");
      } else if (df.getValue().getValue() instanceof IJG_Group group) {
        return new ReadOnlyStringWrapper(group.getNumberOf()+"x");
      }
      return new ReadOnlyStringWrapper("");
    });
    mFGName.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IEntity ent) {
        return new ReadOnlyStringWrapper(ent.name());
      }
      return new ReadOnlyStringWrapper("");
    });
    mShipDesign.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Group group) {
        return new ReadOnlyStringWrapper(group.unitDesign());
      }
      return new ReadOnlyStringWrapper("");
    });
    mPosition.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Fleet fleet) {
        if (fleet.containsGroups()) {
          return new ReadOnlyStringWrapper(String.format(JavelinSystem.getLocale(), "(%.2f,%.2f)", fleet.position().x(), fleet.position().y()));
        } else {
          return new ReadOnlyStringWrapper( "-" );
        }
      } else if (df.getValue().getValue() instanceof IJG_Group group) {
        return new ReadOnlyStringWrapper(String.format(JavelinSystem.getLocale(),"(%.2f,%.2f)",group.position().x(),group.position().y()));
      }
      return new ReadOnlyStringWrapper("");
    });
    mCargo.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Group group) {
        return new ReadOnlyStringWrapper(group.loadType());
      }
      return new ReadOnlyStringWrapper("");
    });
    mPlanet.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Group group) {
        var planet = mFaction.planets().findPlanetByPosition(group.position());
        if (planet==null) {
          return new ReadOnlyStringWrapper("");
        } else {
          return new ReadOnlyStringWrapper(planet.name());
        }
      }
      return new ReadOnlyStringWrapper("");
    });
    mLoadCOL.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Group group) {
        var planet = mFaction.planets().findPlanetByPosition(group.position());
        if (planet!=null) {
          Button button = new Button("load");
          button.setOnAction(e -> {
            var design = mFaction.getUnitDesignById(group.unitDesign());
            SJG_LoadOrder.loadOrder(group, design, "COL", planet, 9999999);
            mOOBTree.refresh();
          });
          return new ReadOnlyObjectWrapper<>(button);
        }
      }
      return null;
    });
    mUnload.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Group group) {
        var planet = mFaction.planets().findPlanetByPosition(group.position());
        if (planet!=null) {
          Button button = new Button("unload");
          button.setOnAction(e -> {
            SJG_LoadOrder.unloadOrder(group, planet,9999999);
            mOOBTree.refresh();
          });
          return new ReadOnlyObjectWrapper<>(button);
        }
      }
      return null;
    });
    mWarPeace.setCellValueFactory( df -> {
      if (df.getValue().getValue() instanceof IJG_Faction faction) {
        if (mFaction.atWarWith().contains(faction.id())) {
          Button button = new Button("peace");
          button.setOnAction(e -> {
            mFaction.removeWarWith(faction.id());
            mOOBTree.refresh();
          });
          return new ReadOnlyObjectWrapper<>(button);
        } else {
          Button button = new Button("war");
          button.setOnAction(e -> {
            mFaction.addWarWith(faction.id());
            mOOBTree.refresh();
          });
          return new ReadOnlyObjectWrapper<>(button);
        }
      }
      return null;
    });
    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    mRootOwnFleetsGroups.getChildren().clear();
    if (mFaction != null) {
      for(IJG_Fleet fleet : mFaction.groups().fleets()) {
        var fleetitem = new TreeItem(fleet);
        mRootOwnFleetsGroups.getChildren().add(fleetitem);
        for(IJG_Group group : fleet.groups()) {
          fleetitem.getChildren().add(new TreeItem(group));
        }
      }
      for(IJG_Group group : mFaction.groups().getGroups()){
        if (group.getFleet()==null) {
          mRootOwnFleetsGroups.getChildren().add(new TreeItem<>(group));
        }
      }
      for( IJG_Faction faction : mFaction.getOtherFactionsMutable()) {
        TreeItem ti = mFactionNodes.get(faction.id());
        for( var group : faction.groups().getGroups() ) {
          ti.getChildren().add(new TreeItem<>(group));
        }
      }
    }
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    mRoot.getChildren().clear();
    mRoot.getChildren().add(mRootOwnFleetsGroups);
    for( IJG_Faction faction : mFaction.getOtherFactionsMutable()) {
      mFactionNodes.put(faction.id(),new TreeItem<>(faction));
      mRoot.getChildren().add(mFactionNodes.get(faction.id()));
    }
    refresh();
    return;
  }

}
