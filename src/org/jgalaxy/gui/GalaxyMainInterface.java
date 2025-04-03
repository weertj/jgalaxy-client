package org.jgalaxy.gui;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.IJavelinUIElement;
import org.javelinfx.engine.JMainInterface;
import org.javelinfx.engine.startJavelin;
import org.javelinfx.fxml.FXMLLoad;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.player.JL_PlayerContext;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.window.S_Pane;
import org.jgalaxy.engine.*;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.server.SimpleClient;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_Incoming;
import org.jgalaxy.utils.XML_Utils;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GalaxyMainInterface extends JMainInterface {

//  private IJavelinCanvas mCanvas = new SimpleCanvas();

  private TurnInfoController    mTurnInfoController;
  private PlanetInfoController  mPlanetInfoController;
  private ShipDesignsController mShipDesignsController;
  private PlayerInfoController  mPlayerInfoController;
  private OOBController         mOOBController;

  private TabPane mTabControlPane;

  @Override
  public String title() {
    return "Galaxy";
  }

  @Override
  public void init() {
    super.init();

    mainPane().getStylesheets().add(getClass().getResource("/org/jgalaxy/gui/jgalaxy.css").toExternalForm());

    Global.CURRENTUSERNAME.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("username", ""));
    Global.CURRENTPASSWORD.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("password", ""));
    Global.CURRENTSERVER.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("server", ""));
    Global.CURRENTGAMEID.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("game", ""));
    Global.CURRENTPLAYERID.setValue(startJavelin.PARAMETERS.getNamed().getOrDefault("player", ""));

    IJavelinCanvas canvas = GalaxyCanvas.of();
    add( canvas );
    canvas.canvas().setLayoutX( 10 );
    canvas.canvas().setLayoutY( 30 );
    mainPane().widthProperty().addListener( (_,_,newValue) ->
      canvas.canvas().setWidth( newValue.doubleValue()-220 )
    );
    mainPane().heightProperty().addListener( (_,_,newValue) ->
      canvas.canvas().setHeight( newValue.doubleValue()-220 )
    );
    mainPane().getChildren().add( canvas.canvas() );

    mTabControlPane = new TabPane();
    mainPane().getChildren().add(mTabControlPane);
    mTabControlPane.setPrefWidth(200);
    S_Pane.setAnchors(mTabControlPane, null, 0.0, 100.0, 0.0);
    Tab planetTab = new Tab("Planet");
    mTabControlPane.getTabs().add(planetTab);
    Tab designTab = new Tab("Ship design");
    mTabControlPane.getTabs().add(designTab);


    try { // **** OOB
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/OOB.fxml", null);
      mOOBController = (OOBController)FXMLLoad.controller(contents);
      mainPane().getChildren().add(mOOBController.rootPane());
      S_Pane.setAnchors( mOOBController.rootPane(), 0.0, 0.0, null, 0.0);
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

    try { // **** TurnInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/TurnInfo.fxml", null);
      mTurnInfoController = (TurnInfoController)FXMLLoad.controller(contents);
      mainPane().getChildren().add(mTurnInfoController.rootPane());
      S_Pane.setAnchors( mTurnInfoController.rootPane(), null, 0.0, 0.0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** PlanetInfo
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/PlanetInfo.fxml", null);
      mPlanetInfoController = (PlanetInfoController)FXMLLoad.controller(contents);
      planetTab.setContent(mPlanetInfoController.rootPane());
      S_Pane.setAnchors( mPlanetInfoController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try { // **** ShipDesigner
      var contents = FXMLLoad.of().load(getClass().getClassLoader(), "/org/jgalaxy/gui/ShipDesigns.fxml", null);
      mShipDesignsController = (ShipDesignsController)FXMLLoad.controller(contents);
      designTab.setContent(mShipDesignsController.rootPane());
      S_Pane.setAnchors( mShipDesignsController.rootPane(), 0.0, 0.0, 0.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Global.CURRENTTURNNUMBER.addListener((observable, oldValue, newValue) -> {
      IJG_Faction faction = loadPlayer(Global.CURRENTSERVER.get(), Global.CURRENTGAMEID.get(), Global.CURRENTPLAYERID.get(), newValue.intValue() );
      loadFaction(faction);
    });

    Global.CURRENTTURNNUMBER.setValue(0);

//    IJG_Faction faction = loadPlayer("http://localhost:8080/jgalaxy/games", "test1", "player0", 5 );
//    loadFaction(faction);


    return;
  }

  private IJG_Faction loadPlayer( String pURL, String pGameName, String pPlayerName, int pTurnNumber ) {
    IJG_GameInfo gameinfo = null;
    String url = pURL;
    url += "/" + pGameName;
    HttpRequest request = HttpRequest.newBuilder(URI.create(url+ "?alt=xml"))
      .GET()
      .build();
    Node root = null;
    try {
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
      gameinfo = JG_GameInfo.of( XML_Utils.childNodeByPath(root,"game").get());
      Global.CURRENTGAMEINFO.set( gameinfo );
    } catch (Throwable e) {
      e.printStackTrace();
    }

    url += "/" + pTurnNumber;

    request = HttpRequest.newBuilder(URI.create(url + "?alt=xml"))
      .GET()
      .build();
    root = null;
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
    Global.CURRENTFACTION.set( faction );
    IJG_Faction factionchanged = JG_Faction.of(gamechanged,XML_Utils.childNodeByPath(root,"faction").orElse(null));
    Global.CURRENTFACTION_CHANGED.set( factionchanged );
    return factionchanged;
  }

  private void selectItem( IJavelinUIElement pItem ) {
    if (pItem instanceof PlanetRenderItem planetRI) {
      IJG_Planet planet = planetRI.element();
      mPlanetInfoController.setPlanet(planet);
      if (planet.faction()==null) {
        mPlanetInfoController.setFaction(null);
        mTurnInfoController.setFaction(null);
        mPlayerInfoController.setFaction(null);
      } else {
//        IJG_Faction faction = Global.CURRENTPLAYERCHANGED.get().getFactionByID(planet.owner());
        IJG_Faction faction = Global.retrieveFactionByID(planet.faction() );
        mPlanetInfoController.setFaction(faction);
        mTurnInfoController.setFaction(faction);
        mPlayerInfoController.setFaction(faction);
      }
    }
    return;
  }

  private void deselectItem( IJavelinUIElement pItem ) {

  }


  private void loadFaction( IJG_Faction pFaction ) {
    IJL_PlayerContext playerContext = JL_PlayerContext.of();
    setPlayerContext(playerContext);
    mPlanetInfoController.setFaction(pFaction);
    mTurnInfoController.setFaction(pFaction);
    mPlayerInfoController.setFaction(pFaction);
    mShipDesignsController.setFaction(pFaction);
    mOOBController.setFaction(pFaction);
    playerContext.selectedItems().selectedItems().addListener((ListChangeListener<IJavelinUIElement>) c -> {
      while(c.next()) {
        for( var item : c.getAddedSubList() ) {
          selectItem( item );
        }
        for( var item : c.getRemoved() ) {
          deselectItem( item );
        }
      }
      return;
    });

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
        playerContext.addRenderItem( 2,
          new GroupRenderItem(othergroup.id(),othergroup,
            SP_Position.of(othergroup.position().x(), othergroup.position().y(), Global.DISTANCEUNIT)));
      }
    }

    // **** Incoming groups
    for(IJG_Incoming incoming : pFaction.getIncomingMutable()) {
      playerContext.addRenderItem( 2,
        new IncomingGroupRenderItem(null,incoming, SP_Position.of(incoming.current().x(),incoming.current().y(), Global.DISTANCEUNIT)));
    }


    return;
  }

}
