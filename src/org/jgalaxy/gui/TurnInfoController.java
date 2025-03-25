package org.jgalaxy.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.orders.IJG_Orders;
import org.jgalaxy.orders.JG_Orders;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.utils.XML_Utils;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class TurnInfoController extends JUnitPanelInterface implements Initializable {

  @FXML private AnchorPane        mRootPane;
  @FXML private Spinner<Integer>  mTurnNumber;
  @FXML private Button            mSendOrders;
  @FXML private Button            mNextTurn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mTurnNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,9999,1));
    mTurnNumber.getValueFactory().setValue(1);
    mTurnNumber.valueProperty().addListener((observable, oldValue, newValue) -> {
      Global.CURRENTTURNNUMBER.setValue(newValue);
    });

    mSendOrders.setOnAction(event -> {
      IJG_Orders orders = JG_Orders.generateOf(Global.CURRENTTURNNUMBER.get(), Global.CURRENTFACTION.get(), Global.CURRENTFACTION_CHANGED.get());
      Document doc = XML_Utils.newXMLDocument();
      Node root = doc.createElement("root" );
      doc.appendChild(root);
      orders.storeObject(null, root, "", "");
      try {
        String result = XML_Utils.documentToString(doc);
        System.out.println(result);

        String url = "http://localhost:8080/jgalaxy/games/" + Global.CURRENTGAMEINFO.get().name() + "/" + Global.CURRENTTURNNUMBER.get() + "/player0/faction0/orders?alt=xml";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
          .PUT(HttpRequest.BodyPublishers.ofString(result))
          .build();
          HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
      } catch (Throwable e) {
        e.printStackTrace();
      }
    });

    mNextTurn.setOnAction(event -> {
      try {
        String url = "http://localhost:8080/jgalaxy/games/" + Global.CURRENTGAMEINFO.get().name() + "/" + Global.CURRENTTURNNUMBER.get() + "?nextTurn&alt=xml";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
          .PUT(HttpRequest.BodyPublishers.ofString(""))
          .build();
        HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString() );
      } catch (Throwable e) {
        e.printStackTrace();
      }
    });

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    mTurnNumber.getValueFactory().setValue(Global.CURRENTGAMEINFO.get().currentTurnNumber());
    return;
  }


}
