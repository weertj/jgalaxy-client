package org.jgalaxy.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.engine.IJG_Player;
import org.jgalaxy.orders.IJG_Orders;
import org.jgalaxy.orders.JG_Orders;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.server.SimpleClient;
import org.jgalaxy.utils.XML_Utils;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class TurnInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane        mRootPane;
  @FXML private Label             mTurnNumber;
  @FXML private Button            mMinTurn;
  @FXML private Button            mPlusTurn;
  @FXML private Button            mSendOrders;
  @FXML private Button            mNextTurn;

  @FXML private CheckBox          mAILoadTurn;

  private final IntegerProperty mTurnNumberProperty = new SimpleIntegerProperty(1);

  private IJG_GameInfo      mGameInfo;
  private IJG_Player        mPlayer;
  private IJG_Faction       mFaction;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Bindings.bindBidirectional(Global.AUTOTURNLOAD, mAILoadTurn.selectedProperty() );

//    Global.CURRENTTURNNUMBER.addListener((observable, oldValue, newValue) -> {
    Global.GAMECONTEXT.turnNumberProperty().addListener((observable, oldValue, newValue) -> {
      mTurnNumber.setText(newValue.toString());
    });
    mMinTurn.setOnAction(e -> {
      Global.GAMECONTEXT.previousTurnNumber();
//      Global.CURRENTTURNNUMBER.setValue(Global.CURRENTTURNNUMBER.get()-1);
    });
    mPlusTurn.setOnAction(e -> {
      Global.GAMECONTEXT.nextTurnNumber();
//      Global.CURRENTTURNNUMBER.setValue(Global.CURRENTTURNNUMBER.get()+1);
    });

    // **** SEND ORDERS
    mSendOrders.setOnAction(event -> {
      Global.GAMECONTEXT.sendCurrentOrders();
//      Global.sendOrders();
//      IJG_Orders orders = JG_Orders.generateOf(Global.CURRENTTURNNUMBER.get(), Global.CURRENTFACTION.get(), Global.CURRENTFACTION_CHANGED.get());
//      Document doc = XML_Utils.newXMLDocument();
//      Node root = doc.createElement("root" );
//      doc.appendChild(root);
//      orders.storeObject(null, root, "", "");
//      try {
//        String result = XML_Utils.documentToString(doc);
//        System.out.println(result);
//
//        String url =
//              Global.CURRENTSERVER.get() + "/" +
////              Global.CURRENTGAMEINFO.get().name() + "/" +
//              Global.CURRENTGAME.get().name() + "/" +
//              Global.CURRENTTURNNUMBER.get() + "/" +
//              Global.CURRENTPLAYERID.get() + "/" +
//              Global.CURRENTFACTION.get().id() + "/" +
//              "orders?alt=xml";
//        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
//          .PUT(HttpRequest.BodyPublishers.ofString(result))
//          .build();
//          HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
//      } catch (Throwable e) {
//        e.printStackTrace();
//      }
    });

    mNextTurn.setOnAction(event -> {
      try {
        Global.GAMECONTEXT.nextTurn();
//        String url = Global.CURRENTSERVER.get() + "/" + Global.CURRENTGAME.get().name() + "/" + Global.CURRENTTURNNUMBER.get() + "?nextTurn&alt=xml";
//        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
//          .PUT(HttpRequest.BodyPublishers.ofString(""))
//          .build();
//        HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString() );
      } catch (Throwable e) {
        e.printStackTrace();
      }
    });

    return;
  }

  public void setGameInfo(IJG_GameInfo gameInfo) {
    mGameInfo = gameInfo;
    refresh();
    return;
  }

  public void setPlayer(IJG_Player player) {
    mPlayer = player;
    refresh();
    return;
  }

  public void setFaction(IJG_Faction faction) {
    mFaction = faction;
    refresh();
    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
//    Global.CURRENTTURNNUMBER.setValue(Global.CURRENTGAMEINFO.get().currentTurnNumber());
    return;
  }


}
