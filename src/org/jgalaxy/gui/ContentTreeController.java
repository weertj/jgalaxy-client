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
import org.javelinfx.events.EH_Select;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.system.FX_Platform;
import org.javelinfx.system.JavelinSystem;
import org.javelinfx.tree.ITreeEntity;
import org.javelinfx.tree.STreeView;
import org.jgalaxy.IEntity;
import org.jgalaxy.battle.*;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.engine.IJG_Player;
import org.jgalaxy.orders.SJG_LoadOrder;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.utils.GEN_Math;

import java.net.URL;
import java.util.*;

public class ContentTreeController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;
  @FXML private TreeView<IEntity>  mContentTreeView;

  private IJL_PlayerContext mPlayerContext;
  private IJG_Faction mFaction;
  private TreeItem mRoot;
  private TreeItem mRootPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String entityType() { return "Planets"; }
    @Override public String name() { return "Planets"; }
  });
  private TreeItem mRootOwnPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return Global.CURRENTFACTION_CHANGED.get().name(); }
  });
  private TreeItem mRootUnknownPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Unknown"; }
  });
  private TreeItem mRootUninhabitedPlanets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Uninhabited"; }
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
    @Override public String name() { return Global.CURRENTFACTION_CHANGED.get().name(); }
  });
  private TreeItem mRootOwnCargoGroups = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Cargo ships"; }
  });
  private TreeItem mRootFleets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Fleets"; }
  });
  private TreeItem mRootOwnFleets = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return Global.CURRENTFACTION_CHANGED.get().name(); }
  });

  private TreeItem mRootBattles = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Battles"; }
  });
  private TreeItem mRootOwnBattles = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return Global.CURRENTFACTION_CHANGED.get().name(); }
  });

  private TreeItem mRootFactions = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Factions"; }
  });
  private TreeItem mRootFactionsTop = new TreeItem<>(new IEntity() {
    @Override public String id() { return ""; }
    @Override public String name() { return "Top factions"; }
  });

//  private Map<String,TreeItem> mFactionNodes = new HashMap<>(16);

  private boolean mInRefresh;

//  private TreeItem mRootOwnFleetsGroups = new TreeItem<>("Own fleets/groups");

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mRoot = new TreeItem<>();
    mContentTreeView.setRoot(mRoot);
    mContentTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    mRoot.getChildren().add(mRootPlanets);
    mRoot.getChildren().add(mRootGroups);
    mRoot.getChildren().add(mRootFleets);
    mRoot.getChildren().add(mRootBattles);
    mRoot.getChildren().add(mRootFactions);
    mRoot.setExpanded(true);

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
            } else if (ent instanceof IJG_Group group) {
              Label t = new Label(group.name() + " " + group.getNumberOf() + "x " + group.unitDesign());
              pane.getChildren().addAll( t);
            } else if (ent instanceof IJG_Faction faction) {
              Label t = new Label(faction.name() + " (" + GEN_Math.round02(faction.getReconTotalPop()) + ")");
              pane.getChildren().addAll( t);
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
          for (var rem : c.getRemoved()) {
            if (rem!=null) {
              Global.removeSelectedIdentity(rem.getValue());
            }
          }
          if (c.wasAdded()) {
            Global.clearSelections();
            for (var add : c.getAddedSubList()) {
              if (add != null) {
                Global.addSelectedIdentity(add.getValue(), false);
              }
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
    mRootUninhabitedPlanets.getChildren().clear();
    mRootUnknownInhabitedPlanets.getChildren().clear();
    mRootPlanets.getChildren().add(mRootOwnPlanets);
    mRootPlanets.getChildren().add(mRootUninhabitedPlanets);
    mRootPlanets.getChildren().add(mRootUnknownInhabitedPlanets);
    mRootPlanets.getChildren().add(mRootUnknownPlanets);
    mRootGroups.getChildren().clear();
    mRootOwnGroups.getChildren().clear();
    mRootGroups.getChildren().add(mRootOwnGroups);
    mRootOwnCargoGroups.getChildren().clear();
    mRootOwnGroups.getChildren().add(mRootOwnCargoGroups);
    mRootFleets.getChildren().clear();
    mRootOwnFleets.getChildren().clear();
    mRootFleets.getChildren().add(mRootOwnFleets);

    mRootBattles.getChildren().clear();
    mRootOwnBattles.getChildren().clear();
    mRootBattles.getChildren().add(mRootOwnBattles);

    mRootFactions.getChildren().clear();
    mRootFactionsTop.getChildren().clear();
    mRootFactions.getChildren().add(mRootFactionsTop);

    if (mFaction!=null) {

      for(IJG_Planet planet : mFaction.planets().planetsOwnedBy(mFaction)) {
        mRootOwnPlanets.getChildren().add(new TreeItem<>(planet));
      }
      for(IJG_Planet planet : mFaction.planets().planets()) {
        if (planet.faction()==null) {
          if (planet.size() >= 0) {
            mRootUninhabitedPlanets.getChildren().add(new TreeItem<>(planet));
          } else {
            mRootUnknownPlanets.getChildren().add(new TreeItem<>(planet));
          }
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
      for(IJG_Group group : mFaction.groups().getGroups()){
        if (group.getFleet()==null) {
          if (mFaction.getUnitDesignById(group.unitDesign()).cargo()>0) {
            mRootOwnCargoGroups.getChildren().add(new TreeItem<>(group));
          }
        }
      }
//      for( IJG_Faction faction : mFaction.getOtherFactionsMutable()) {
//        TreeItem ti = mFactionNodes.get(faction.id());
//        for( var group : faction.groups().getGroups() ) {
//          ti.getChildren().add(new TreeItem<>(group));
//        }
//      }

      // **** Fleets
      for(IJG_Fleet fleet : mFaction.groups().fleets()){
        TreeItem ti = new TreeItem<>(fleet);
        mRootOwnFleets.getChildren().add(ti);
        for( IJG_Group group : fleet.groups()) {
          ti.getChildren().add(new TreeItem<>(group));
        }
      }

      // **** Battles
      IJG_Game game = Global.CURRENTGAMECHANGED.get();

      for( IJG_Planet planet : mFaction.planets().planets() ) {
        ISB_BattleReport report = SB_BattleReport.of(mFaction, planet.position() );
        if (report.isInvolved(mFaction)) {
          mRootOwnBattles.getChildren().add(new TreeItem<>(report));
        }
      }

      // **** Factions
      for( IJG_Faction faction : mFaction.getOtherFactionsMutable() ) {
        mRootFactions.getChildren().add(new TreeItem<>(faction));
      }
      // **** TopFactions
      var topfactions = Global.CURRENTGAME.get().topFactions();
      for(IJG_Faction faction : topfactions) {
        mRootFactionsTop.getChildren().add(new TreeItem<>(faction));
      }



//      List<ISB_BattleField> battles = new ArrayList<>(8);
//      for(IJG_Group group : mFaction.groups().getGroups()) {
//        var gfaction = game.getFactionById(group.faction());
//        for(IB_Shot shot : group.shotsMutable()) {
//          if (shot.type()==IB_Shot.TYPE.SHIP_SHIP) {
//            ISB_BattleField battleField = SB_BattleField.of(game);
//            battleField.addEntry(gfaction,group);
//            battles.add(battleField);
//          }
//        }
//      }
//      mRootBattles.getChildren().addAll(battles);


    }
    mContentTreeView.refresh();
    return;
  }

  public void setPlayerContext( IJL_PlayerContext pPlayerContext ) {
    mPlayerContext = pPlayerContext;
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
