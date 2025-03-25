package org.jgalaxy.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.IJavelinUIElement;
import org.javelinfx.engine.JMainInterface;
import org.javelinfx.example.SimpleCanvas;
import org.javelinfx.fxml.FXMLLoad;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.player.JL_PlayerContext;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.units.EUDistance;
import org.javelinfx.window.S_Pane;
import org.jgalaxy.engine.*;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;
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

  @Override
  public String title() {
    return "Galaxy";
  }

  @Override
  public void init() {
    super.init();
    IJavelinCanvas canvas = GalaxyCanvas.of();
    add( canvas );
    canvas.canvas().setLayoutX( 10 );
    canvas.canvas().setLayoutY( 10 );
    mainPane().widthProperty().addListener( (_,_,newValue) ->
      canvas.canvas().setWidth( newValue.doubleValue()-220 )
    );
    mainPane().heightProperty().addListener( (_,_,newValue) ->
      canvas.canvas().setHeight( newValue.doubleValue()-20 )
    );
    mainPane().getChildren().add( canvas.canvas() );

    try { // **** PlanetInfo
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
      mainPane().getChildren().add(mPlanetInfoController.rootPane());
      S_Pane.setAnchors( mPlanetInfoController.rootPane(), null, 0.0, 80.0, 0.0);
    } catch (Exception e) {
      e.printStackTrace();
    }

//    IJG_GameInfo gameinfo = null;
//    HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/jgalaxy/games/test1?alt=xml"))
//      .GET()
//      .build();
//    Node root = null;
//    try {
//      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
//      String result = response.body().toString();
//      root = XML_Utils.rootNodeBy(result);
//      gameinfo = JG_GameInfo.of( root);
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
//
//    request = HttpRequest.newBuilder(URI.create("http://localhost:8080/jgalaxy/games/test1/5?alt=xml"))
//      .GET()
//      .build();
//    root = null;
//    IJG_Game game = null;
//    try {
//      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
//      String result = response.body().toString();
//      root = XML_Utils.rootNodeBy(result);
//      game = JG_Game.of( null, root, 2);
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
//
//
//    request = HttpRequest.newBuilder(URI.create("http://localhost:8080/jgalaxy/games/test1/5/player0/faction0?alt=xml"))
//      .GET()
//      .build();
//    root = null;
//    try {
//      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
//      String result = response.body().toString();
//      root = XML_Utils.rootNodeBy(result);
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
//    IJG_Faction faction = JG_Faction.of(game,XML_Utils.childElementsByName(root,"faction").get(0));
//
//    loadFaction(faction);

    Global.CURRENTTURNNUMBER.addListener((observable, oldValue, newValue) -> {
      IJG_Faction faction = loadPlayer("http://localhost:8080/jgalaxy/games", "test1", "player0", newValue.intValue() );
      loadFaction(faction);
    });

    Global.CURRENTTURNNUMBER.setValue(1);

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
      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
      gameinfo = JG_GameInfo.of( root);
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
    try {
      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
      game = JG_Game.of( null, root, 2);
      Global.CURRENTGAME.set( game );
    } catch (Throwable e) {
      e.printStackTrace();
    }

    IJG_Player player = game.getPlayerByID(pPlayerName);
    Global.CURRENTPLAYER.set( player );

    url += "/" + player.id() + "/" + player.factions().getFirst().id();

    request = HttpRequest.newBuilder(URI.create(url + "?alt=xml"))
      .GET()
      .build();
    root = null;
    try {
      HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
      String result = response.body().toString();
      root = XML_Utils.rootNodeBy(result);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    IJG_Faction faction = JG_Faction.of(game,XML_Utils.childNodeByPath(root,"faction").orElse(null));
    Global.CURRENTFACTION.set( faction );
    Global.CURRENTFACTION_CHANGED.set( JG_Faction.of(game,XML_Utils.childNodeByPath(root,"faction").orElse(null)));
    return faction;
  }

  private void selectItem( IJavelinUIElement pItem ) {
    if (pItem instanceof PlanetRenderItem planet) {
      mPlanetInfoController.setPlanet(planet.element());
    }
    return;
  }

  private void deselectItem( IJavelinUIElement pItem ) {

  }


  private void loadFaction( IJG_Faction pFaction ) {
    IJL_PlayerContext playerContext = JL_PlayerContext.of();
    setPlayerContext(playerContext);
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
          SP_Position.of(planet.position().x(),planet.position().y(), EUDistance.KM)));
    }
    for( IJG_Group group : pFaction.groups().getGroups()) {
      playerContext.addRenderItem(3,
        new GroupRenderItem(group.id(),group,
          SP_Position.of(group.position().x(),group.position().y(), EUDistance.KM)));
    }

    return;
  }

}
