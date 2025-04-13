package org.jgalaxy.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.javelinfx.colors.SColors;
import org.javelinfx.engine.JUnitPanelInterface;
import org.javelinfx.system.FX_Platform;
import org.javelinfx.system.JavelinSystem;
import org.javelinfx.tree.ITreeEntity;
import org.javelinfx.tree.STreeView;
import org.jgalaxy.IEntity;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class ContentTreeController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TreeView<IEntity>  mContentTreeView;

  private IJG_Faction mFaction;
  private TreeItem mRoot;
  private TreeItem mRootPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String entityType() { return "Planets"; }
    @Override public String name() { return "Planets"; }
  });
  private TreeItem mRootOwnPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return mFaction.name(); }
  });
  private TreeItem mRootUnknownPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Unknown"; }
  });
  private TreeItem mRootUnknownInhabitedPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Inhabited"; }
  });
  private TreeItem mRootGroups = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Groups"; }
  });
  private TreeItem mRootOwnGroups = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return mFaction.name(); }
  });
  private TreeItem mRootFleets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Fleets"; }
  });
  private TreeItem mRootOwnFleets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return mFaction.name(); }
  });
  private Map<String,TreeItem> mFactionNodes = new HashMap<>(16);

  private boolean mInRefresh;

//  private TreeItem mRootOwnFleetsGroups = new TreeItem<>("Own fleets/groups");

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRoot = new TreeItem<>();
    mContentTreeView.setRoot(mRoot);
    mContentTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    mRoot.getChildren().add(mRootPlanets);
    mRoot.getChildren().add(mRootGroups);
    mRoot.getChildren().add(mRootFleets);

    mContentTreeView.setCellFactory(param -> new TreeCell() {
      @Override
      protected void updateItem( Object tent, boolean empty) {
        super.updateItem(tent, empty);

        if (tent==null) {
          setText(null);
          setGraphic(null);
        } else {
          if (tent instanceof IEntity ent) {
            AnchorPane pane = new AnchorPane();
            if ("Planets".equals(ent.entityType())) {
              Label t = new Label(ent.name());
              t.setText("Planets");
              pane.getChildren().addAll(t);
            } else if (ent instanceof IJG_Planet planet) {
              Label t = new Label(ent.name());
              t.setStyle("-fx-text-fill: " + SColors.toRGBCode(SColors.DEFAULT_TEXTFOREGROUND));
              t.setLayoutX(24);
              Circle circle = new Circle(8);
              circle.setLayoutX(0);
              circle.setLayoutY(6);
              circle.setFill(Colors.colorForMyFaction(planet));
              pane.getChildren().addAll(circle, t);
            } else {
              pane.getChildren().add(new Label(ent.name()));
            }
            setGraphic(pane);
          }
        }
//        setText(item == null ? "" : item.name());
      }
    });

    // **** LASTSELECTEDENTITY
    Global.LASTSELECTEDENTITY.addListener( (_, _, newValue) -> {
      if (!mInRefresh) {
        FX_Platform.runLater(() -> {
          var selModel = mContentTreeView.getSelectionModel();
//        try {
//          selModel.clearSelection();
//        } catch (Throwable e) {
//          System.out.println("asddassda");
//        }
          var ti = STreeView.findTreeItemBy(mContentTreeView, null, newValue, true);
          if (ti != null) {
            selModel.select(ti);
          }
        });
      }
    });
    mContentTreeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<IEntity>>) c -> {
      mInRefresh = true;
      try {
        while (c.next()) {
          for (var add : c.getAddedSubList()) {
            if (add!=null) {
              Global.addSelectedIdentity(add.getValue(), false);
            }
          }
          for (var rem : c.getRemoved()) {
            if (rem!=null) {
              Global.removeSelectedIdentity(rem.getValue());
            }
          }
        }
      } finally {
        mInRefresh = false;
      }
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {

    // **** Planets
    mRootPlanets.getChildren().clear();
    mRootOwnPlanets.getChildren().clear();
    mRootUnknownPlanets.getChildren().clear();
    mRootUnknownInhabitedPlanets.getChildren().clear();
    mRootPlanets.getChildren().add(mRootOwnPlanets);
    mRootPlanets.getChildren().add(mRootUnknownPlanets);
    mRootPlanets.getChildren().add(mRootUnknownInhabitedPlanets);
    mRootGroups.getChildren().clear();
    mRootOwnGroups.getChildren().clear();
    mRootGroups.getChildren().add(mRootOwnGroups);
    mRootFleets.getChildren().clear();
    mRootOwnFleets.getChildren().clear();
    mRootFleets.getChildren().add(mRootOwnFleets);
    if (mFaction!=null) {

      for(IJG_Planet planet : mFaction.planets().planetsOwnedBy(mFaction)) {
        mRootOwnPlanets.getChildren().add(new TreeItem<>(planet));
      }
      for(IJG_Planet planet : mFaction.planets().planets()) {
        if (planet.faction()==null) {
          mRootUnknownPlanets.getChildren().add(new TreeItem<>(planet));
        } else if (Objects.equals(planet.faction(),"")) {
          mRootUnknownInhabitedPlanets.getChildren().add(new TreeItem<>(planet));
        }
      }

//      for(IJG_Fleet fleet : mFaction.groups().fleets()) {
//        var fleetitem = new TreeItem(fleet);
//        mRootOwnPlanets.getChildren().add(fleetitem);
//        for(IJG_Group group : fleet.groups()) {
//          fleetitem.getChildren().add(new TreeItem(group));
//        }
//      }
      for(IJG_Group group : mFaction.groups().getGroups()){
        if (group.getFleet()==null) {
          mRootOwnGroups.getChildren().add(new TreeItem<>(group));
        }
      }
//      for( IJG_Faction faction : mFaction.getOtherFactionsMutable()) {
//        TreeItem ti = mFactionNodes.get(faction.id());
//        for( var group : faction.groups().getGroups() ) {
//          ti.getChildren().add(new TreeItem<>(group));
//        }
//      }

      for(IJG_Fleet fleet : mFaction.groups().fleets()){
        TreeItem ti = new TreeItem<>(fleet);
        mRootOwnFleets.getChildren().add(ti);
        for( IJG_Group group : fleet.groups()) {
          ti.getChildren().add(new TreeItem<>(group));
        }
      }


    }
    mContentTreeView.refresh();
    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
//    for( IJG_Faction faction : mFaction.getOtherFactionsMutable()) {
//      mFactionNodes.put(faction.id(),new TreeItem<>(faction));
//      mRoot.getChildren().add(mFactionNodes.get(faction.id()));
//    }
    refresh();
    return;
  }

}
