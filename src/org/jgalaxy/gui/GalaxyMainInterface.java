package org.jgalaxy.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.IJavelinUIElement;
import org.javelinfx.engine.JMainInterface;
import org.javelinfx.engine.startJavelin;
import org.javelinfx.fxml.FXMLLoad;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.player.JL_PlayerContext;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.window.S_Pane;
import org.jgalaxy.IEntity;
import org.jgalaxy.battle.ISB_BattleReport;
import org.jgalaxy.battle.SB_BattleReport;
import org.jgalaxy.engine.*;
import org.jgalaxy.map.IMAP_Map;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.server.SimpleClient;
import org.jgalaxy.server.SimpleServer;
import org.jgalaxy.units.IJG_Bombing;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_Incoming;
import org.jgalaxy.utils.XML_Utils;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GalaxyMainInterface extends JMainInterface {

//  private IJavelinCanvas mCanvas = new SimpleCanvas();

  private TurnInfoController      mTurnInfoController;
  private FactionInfoController   mFactionInfoController;
  private PlanetInfoController    mPlanetInfoController;
  private GroupInfoController     mGroupInfoController;
  private FleetInfoController     mFleetInfoController;
  private BattleReportController  mBattleReportController;
  private PlayerInfoController    mPlayerInfoController;
  private StatusBarController     mStatusBarController;
  private ContentTreeController   mContentTreeController;

  private TabPane mTabControlPane;
  private Tab mFactionTab;
  private Tab mFleetTab;
  private Tab mGroupTab;
  private Tab mPlanetTab;
  private Tab mBattleTab;

  private MapRenderItem mMapRenderItem;

  private IJavelinCanvas mCanvas;

  private boolean mFirstRun = true;

  private final ChangeListener<Number> mFactionChanged = (observable, oldValue, newValue) -> {
    Global.sendOrders();
    refresh();
    return;
  };


  @Override
  public String title() {
    return "Galaxy Reloaded";
  }

  /**
   * canvasCallback
   */
  private void canvasCallback() {
    if (Global.AUTOTURNLOAD.get()) {
      if ((System.currentTimeMillis()-Global.LASTTURNCHECK)>4000) {
        try {
          loadGameInfo(Global.CURRENTSERVER.get(), Global.CURRENTGAMEID.get());
          Global.CURRENTTURNNUMBER.setValue(Global.CURRENTGAMEINFO.get().currentTurnNumber());
        } finally {
          Global.LASTTURNCHECK = System.currentTimeMillis();
        }
      }
    }
    return;
  }

  @Override
  public void init() {
    super.init();

//    mainPane().getStylesheets().add(getClass().getResource("/org/jgalaxy/gui/jgalaxy.css").toExternalForm());

    Global.CURRENTUSERNAME.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("username", ""));
    Global.CURRENTPASSWORD.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("password", ""));
    Global.CURRENTSERVER.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("server", ""));
    Global.CURRENTGAMEID.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("game", ""));
    Global.CURRENTPLAYERID.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("player", ""));

    loadGameInfo(Global.CURRENTSERVER.get(), Global.CURRENTGAMEID.get());
    loadBanners(Global.CURRENTSERVER.get(), Global.CURRENTGAMEID.get());

    mCanvas = GalaxyCanvas.of();
    mCanvas.addCanvasRunnable( () -> canvasCallback() );
    add( mCanvas );
    mCanvas.canvas().setLayoutX( 200 );
    mCanvas.canvas().setLayoutY( 32 );
    mainPane().widthProperty().addListener( (_,_,newValue) ->
      mCanvas.canvas().setWidth( mainPane().getWidth()-420 )
    );
    mainPane().heightProperty().addListener( (_,_,newValue) -> {
      mCanvas.canvas().setHeight(mainPane().getHeight() - 64);
      }
    );
    mainPane().getChildren().add( mCanvas.canvas() );

    mTabControlPane = new TabPane();
//    mTabControlPane.getSelectionModel().selectedItemProperty().addListener((_,_, newValue) -> {
//      if (newValue==mPlanetTab) {
//        refresh();
//      }
//    });
    mTabControlPane.setSide(Side.TOP);
    mainPane().getChildren().add(mTabControlPane);
    mTabControlPane.setPrefWidth(220);
    S_Pane.setAnchors(mTabControlPane, null, 0.0, 110.0, null);
    mFactionTab = new Tab("Faction");
    mFactionTab.setClosable(false);
    mTabControlPane.getTabs().add(mFactionTab);
    mPlanetTab = new Tab("Planet");
    mPlanetTab.setClosable(false);
    mTabControlPane.getTabs().add(mPlanetTab);
    mFleetTab = new Tab("Fleet");
    mFleetTab.setClosable(false);
    mTabControlPane.getTabs().add(mFleetTab);
    mGroupTab = new Tab("Group");
    mGroupTab.setClosable(false);
    mTabControlPane.getTabs().add(mGroupTab);
    mBattleTab = new Tab("Battle");
    mBattleTab.setClosable(false);
    mTabControlPane.getTabs().add(mBattleTab);

    try { // **** Content Tree
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/ContentTree.fxml", null);
      mContentTreeController = (ContentTreeController)FXMLLoad.controller(contents);
      mainPane().getChildren().add(mContentTreeController.rootPane());
      S_Pane.setAnchors( mContentTreeController.rootPane(), 0.0, null, 32.0, 32.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** Statusbar
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/StatusBar.fxml", null);
      mStatusBarController = (StatusBarController)FXMLLoad.controller(contents);
      mainPane().getChildren().add(mStatusBarController.rootPane());
      S_Pane.setAnchors( mStatusBarController.rootPane(), 0.0, 0.0, null, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** Player info
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/PlayerInfo.fxml", null);
      mPlayerInfoController = (PlayerInfoController) FXMLLoad.controller(contents);
      mainPane().getChildren().add(mPlayerInfoController.rootPane());
      S_Pane.setAnchors( mPlayerInfoController.rootPane(), 0.0, 0.0, 0.0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** Faction info
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/FactionInfo.fxml", null);
      mFactionInfoController = (FactionInfoController)FXMLLoad.controller(contents);
      mFactionTab.setContent(mFactionInfoController.rootPane());
      mainPane().getChildren().add(mFactionInfoController.rootPane());
      S_Pane.setAnchors( mFactionInfoController.rootPane(), 0.0, 0.0, 0.0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** TurnInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/TurnInfo.fxml", null);
      mTurnInfoController = (TurnInfoController)FXMLLoad.controller(contents);
      mainPane().getChildren().add(mTurnInfoController.rootPane());
      S_Pane.setAnchors( mTurnInfoController.rootPane(), null, 0.0, 32.0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** PlanetInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/PlanetInfo.fxml", null);
      mPlanetInfoController = (PlanetInfoController)FXMLLoad.controller(contents);
      mPlanetTab.setContent(mPlanetInfoController.rootPane());
      S_Pane.setAnchors( mPlanetInfoController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** FleetInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/FleetInfo.fxml", null);
      mFleetInfoController = (FleetInfoController)FXMLLoad.controller(contents);
      mFleetTab.setContent(mFleetInfoController.rootPane());
      S_Pane.setAnchors( mFleetInfoController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** BattleReport
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/BattleReport.fxml", null);
      mBattleReportController = (BattleReportController)FXMLLoad.controller(contents);
      mBattleTab.setContent(mBattleReportController.rootPane());
      S_Pane.setAnchors( mBattleReportController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** GroupInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/GroupInfo.fxml", null);
      mGroupInfoController = (GroupInfoController)FXMLLoad.controller(contents);
      mGroupTab.setContent(mGroupInfoController.rootPane());
      S_Pane.setAnchors( mGroupInfoController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Global.getSelectedEntities().addListener((ListChangeListener<IEntity>) c -> {
      while(c.next()) {
        for(IEntity entity : c.getAddedSubList()) {
          if (entity instanceof IJG_Planet planet) {
            selectPlanet(planet);
          } else if (entity instanceof IJG_Fleet fleet) {
            selectFleet(fleet);
          } else if (entity instanceof IJG_Group group) {
            selectGroup(group);
          } else if (entity instanceof IJG_Faction faction) {
            faction = Global.resolveFaction(faction);
            selectFaction(faction);
          } else if (entity instanceof ISB_BattleReport battleReport) {
            selectBattleReport(battleReport);
          }
        }
      }
    });

    Global.CURRENTTURNNUMBER.addListener((observable, oldValue, newValue) -> {
      IJG_Faction faction = loadPlayer(Global.CURRENTSERVER.get(), Global.CURRENTGAMEID.get(), Global.CURRENTPLAYERID.get(), newValue.intValue() );
      loadFaction(faction);
      return;
    });

    Global.CURRENTTURNNUMBER.setValue(Global.CURRENTGAMEINFO.get().currentTurnNumber());

    setUIFaction(Global.CURRENTFACTION_CHANGED.get());

    return;
  }

  private void loadBanners(String pURL, String pGameName) {
    Global.BANNERS.clear();
    for( String faction : Global.CURRENTGAMEINFO.get().factions()) {
      Image banner = loadBanner(pURL, pGameName, faction);
      if (banner!=null) {
        Global.BANNERS.put(faction, banner);
      }
    }
    return;
  }

  private Image loadBanner(String pURL, String pGameName, String pFaction) {
    String url = pURL;
    url += "/" + pGameName + "/banners/" + pFaction + ".png";
    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .GET()
      .build();
    try {
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofByteArray() );
      if (response.statusCode()==200) {
        var bais = new ByteArrayInputStream((byte[]) response.body());
        return new Image(bais);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  private IJG_GameInfo loadGameInfo(String pURL, String pGameName) {
    IJG_GameInfo gameinfo = null;
    String url = pURL;
    url += "/" + pGameName;
    HttpRequest request = HttpRequest.newBuilder(URI.create(url+ "?alt=xml"))
      .GET()
      .build();
    Node root;
    try {
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
      gameinfo = JG_GameInfo.of( XML_Utils.childNodeByPath(root,"game").get());
      Global.CURRENTGAMEINFO.set( gameinfo );
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return gameinfo;
  }

  private IJG_Faction loadPlayer( String pURL, String pGameName, String pPlayerName, int pTurnNumber ) {
//    IJG_GameInfo gameinfo = null;
    String url = pURL;
    url += "/" + pGameName;
    if (Global.CURRENTGAMECHANGED.get()!=null && Global.CURRENTGAMECHANGED.get().isRealTime()) {
      url += "/current";
    } else {
      url += "/" + pTurnNumber;
    }

    HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?alt=xml"))
      .GET()
      .build();
    Node root = null;
    IJG_Game game = null;
    IJG_Game gamechanged = null;
    try {
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
      game = JG_Game.of( null, root, pTurnNumber);
      Global.CURRENTGAME.set( game );
      gamechanged = JG_Game.of( null, root, pTurnNumber);
      Global.CURRENTGAMECHANGED.set( gamechanged );
    } catch (Throwable e) {
      e.printStackTrace();
    }

    IJG_Player player = game.getPlayerByID(pPlayerName);
    Global.CURRENTPLAYER.set( player );
    IJG_Player playerchanged = gamechanged.getPlayerByID(pPlayerName);
    Global.CURRENTPLAYERCHANGED.set( playerchanged );

    url += "/" + player.id() + "/" + player.factions().getFirst().id();

    request = HttpRequest.newBuilder(URI.create(url + "?alt=xml"))
      .GET()
      .build();
    root = null;
    try {
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    IJG_Faction faction = JG_Faction.of(game,XML_Utils.childNodeByPath(root,"faction").orElse(null));
    if (Global.CURRENTFACTION.get() != null) {
      Global.CURRENTFACTION.get().close();
    }
    Global.CURRENTFACTION.set( faction );
    IJG_Faction factionchanged = JG_Faction.of(gamechanged,XML_Utils.childNodeByPath(root,"faction").orElse(null));
    if (Global.CURRENTFACTION_CHANGED.get() != null) {
      Global.CURRENTFACTION_CHANGED.get().changeCounterProperty().removeListener(mFactionChanged);
      Global.CURRENTFACTION_CHANGED.get().close();
    }
    Global.CURRENTFACTION_CHANGED.set( factionchanged );

    factionchanged.changeCounterProperty().addListener(mFactionChanged);

    return factionchanged;
  }

  private void setUIFaction(IJG_Faction pFaction) {
    mPlanetInfoController.setFaction(pFaction);
    mPlayerInfoController.setFaction(pFaction);
    mFactionInfoController.setFaction(pFaction);
    mFleetInfoController.setFaction(pFaction);
    mGroupInfoController.setFaction(pFaction);
//    mContentTreeController.setFaction(pFaction);
    return;
  }


  private void selectPlanet( IJG_Planet pPlanet ) {
    mPlanetInfoController.setPlanet(pPlanet);
    if (pPlanet==null || pPlanet.faction()==null) {
      setUIFaction(null);
    } else {
      IJG_Faction faction = Global.retrieveFactionByID(pPlanet.faction() );
      setUIFaction(faction);
    }
    mMapRenderItem.middleMoveToCanvasPositionProperty().set(SP_Position.of(pPlanet.position().x(),pPlanet.position().y(),Global.DISTANCEUNIT));
    mTabControlPane.getSelectionModel().select(mPlanetTab);
    return;
  }

  private void selectGroup( IJG_Group pGroup ) {
    mGroupInfoController.setFaction(Global.CURRENTFACTION_CHANGED.get().resolveFactionById(pGroup.faction()));
    mGroupInfoController.setGroup(pGroup);
    mFleetInfoController.setFleet( Global.CURRENTFACTION_CHANGED.get().groups().getFleetByName(pGroup.getFleet()) );
    mMapRenderItem.middleMoveToCanvasPositionProperty().set(SP_Position.of(pGroup.position().x(),pGroup.position().y(),Global.DISTANCEUNIT));
    mTabControlPane.getSelectionModel().select(mGroupTab);
    return;
  }

  private void selectFleet( IJG_Fleet pFleet ) {
    mFleetInfoController.setFaction(Global.CURRENTFACTION_CHANGED.get().resolveFactionById(pFleet.faction()));
    mFleetInfoController.setFleet(pFleet);
    mMapRenderItem.middleMoveToCanvasPositionProperty().set(SP_Position.of(pFleet.position().x(),pFleet.position().y(),Global.DISTANCEUNIT));
    mTabControlPane.getSelectionModel().select(mFleetTab);
    return;
  }

  private void selectFaction( IJG_Faction pFaction ) {
    mFactionInfoController.setFaction(pFaction);
    mTabControlPane.getSelectionModel().select(mFactionTab);
    return;
  }

  private void selectBattleReport( ISB_BattleReport pBattleReport ) {
    mBattleReportController.setFaction(Global.CURRENTFACTION_CHANGED.get());
    mBattleReportController.setBattleReport(pBattleReport);
    mMapRenderItem.middleMoveToCanvasPositionProperty().set(SP_Position.of(pBattleReport.position().x(),pBattleReport.position().y(),Global.DISTANCEUNIT));
    mTabControlPane.getSelectionModel().select(mBattleTab);
    return;
  }

  private void selectItem( IJavelinUIElement pItem ) {
    if (pItem.element() instanceof IEntity entity) {
      Global.addSelectedIdentity(entity,true);
    }

//    setUIFaction(Global.CURRENTFACTION_CHANGED.get());

    if (pItem instanceof PlanetRenderItem planetRI) {
      selectPlanet(planetRI.element());
      Global.resolveFaction(planetRI.element().faction());
    } else if (pItem instanceof GroupRenderItem groupRI) {
      selectGroup(groupRI.element());
      Global.resolveFaction(groupRI.element().faction());
    } else if (pItem instanceof FleetRenderItem fleetRI) {
      selectFleet(fleetRI.element());
      Global.resolveFaction(fleetRI.element().faction());
    }
    return;
  }

  private void deselectItem( IJavelinUIElement pItem ) {

  }


  private void loadFaction( IJG_Faction pFaction ) {
    IJL_PlayerContext playerContext = JL_PlayerContext.of();
    setPlayerContext(playerContext);
    setUIFaction(pFaction);
//    mPlanetInfoController.setFaction(pFaction);
    mTurnInfoController.setFaction(pFaction);
    mPlayerInfoController.setFaction(pFaction);
    mFactionInfoController.setFaction(pFaction);
    mContentTreeController.setPlayerContext( playerContext );
    mContentTreeController.setFaction(pFaction);
    playerContext.selectedItems().selectedItems().addListener((ListChangeListener<IJavelinUIElement>) c -> {
      while(c.next()) {
//        Global.clearSelections();
        if (c.wasAdded()) {
          for (var item : c.getAddedSubList()) {
            selectItem(item);
          }
        }
        for( var item : c.getRemoved() ) {
          deselectItem( item );
        }
      }
      return;
    });

    addRenderItems( pFaction );

//    if (oldPlayerContext!=null) {
//      for( IEntity entity : Global.getSelectedEntities()) {
//        System.out.println(entity);
//      }
//      for( IJavelinUIElement item : oldPlayerContext.selectedItems().selectedItems()) {
//        if (item.element() instanceof IEntity entity) {
//          Global.addSelectedIdentity(entity,true);
//        }
//      }
//    }

    return;
  }

  /**
   * addRenderItems
   * @param pFaction
   */
  private void addRenderItems( IJG_Faction pFaction ) {

    IJL_PlayerContext playerContext = getPlayerContext();
    playerContext.clearRenderItems();

    IMAP_Map map = Global.CURRENTGAMECHANGED.get().galaxy().map();

    BackgroundItem bgi = new BackgroundItem("map", map, SP_Position.of(map.xStart(), map.yStart(), Global.DISTANCEUNIT));
    playerContext.addRenderItem(0, bgi );


    mMapRenderItem = new MapRenderItem("map",
      map,
      SP_Position.of(map.xStart(), map.yStart(), Global.DISTANCEUNIT),
      mFirstRun);
    mFirstRun = false;
    mMapRenderItem.mouseOverMapPositionProperty().addListener((_, _, newValue) -> {
      mStatusBarController.setMouseMovePosition(newValue);
      return;
    });
    mMapRenderItem.middleCanvasPositionProperty().addListener((_, _, newValue) -> {
      mStatusBarController.setCenterPosition(newValue);
      return;
    });
    playerContext.addRenderItem(1, mMapRenderItem );

    for( IJG_Planet planet : pFaction.planets().planets() ) {
      ISB_BattleReport report = SB_BattleReport.of(pFaction, planet.position());
      if (report.isBattle()) {
        playerContext.addRenderItem(2,
          new BattleRenderItem(report.id(), report, SP_Position.of(report.position().x(), report.position().y(), Global.DISTANCEUNIT)));
      }
    }
    for(IJG_Bombing bombing : pFaction.getBombingsMutable()) {
      playerContext.addRenderItem(2,
        new BombingRenderItem(bombing.whichGroup(), bombing, SP_Position.of(bombing.position().x(), bombing.position().y(), Global.DISTANCEUNIT)));
    }

    for(IJG_Planet planet : pFaction.planets().planets()) {
      playerContext.addRenderItem(2,
        new PlanetRenderItem(planet.id(),planet,
          SP_Position.of(planet.position().x(),planet.position().y(), Global.DISTANCEUNIT)));
    }

    for( IJG_Group group : pFaction.groups().getGroups()) {
      if (group.getFleet()==null) {
        playerContext.addRenderItem(3,
          new GroupRenderItem(group.id(), group,
            SP_Position.of(group.position().x(), group.position().y(), Global.DISTANCEUNIT)));
      }
    }
    for(IJG_Fleet fleet : pFaction.groups().fleets()) {
      if (!fleet.groups().isEmpty()) {
        var group = fleet.groups().getFirst();
        playerContext.addRenderItem(3,
          new FleetRenderItem(fleet.id(), fleet,
            SP_Position.of(group.position().x(), group.position().y(), Global.DISTANCEUNIT)));
      }
    }

    // **** Other faction groups
    for( IJG_Faction otherFaction : pFaction.getOtherFactionsMutable()) {
      for( IJG_Group othergroup : otherFaction.groups().getGroups()) {
        if (othergroup.getFleet()==null) {
          playerContext.addRenderItem(2,
            new GroupRenderItem(othergroup.id(), othergroup,
              SP_Position.of(othergroup.position().x(), othergroup.position().y(), Global.DISTANCEUNIT)));
        }
      }
    }
    for( IJG_Faction otherFaction : pFaction.getOtherFactionsMutable()) {
      for( IJG_Fleet otherfleet : otherFaction.groups().fleets()) {
        if (!otherfleet.groups().isEmpty()) {
          var group = otherfleet.groups().getFirst();
          playerContext.addRenderItem(3,
            new FleetRenderItem(otherfleet.id(), otherfleet,
              SP_Position.of(group.position().x(), group.position().y(), Global.DISTANCEUNIT)));
        }
      }
    }

    // **** Incoming groups
    for(IJG_Incoming incoming : pFaction.getIncomingMutable()) {
      playerContext.addRenderItem( 2,
        new IncomingGroupRenderItem(null,incoming, SP_Position.of(incoming.current().x(),incoming.current().y(), Global.DISTANCEUNIT)));
    }
    return;
  }

  private void refresh() {
    mContentTreeController.refresh();
    mPlanetInfoController.refresh();
    addRenderItems( Global.CURRENTFACTION_CHANGED.get() );
    return;
  }

}
