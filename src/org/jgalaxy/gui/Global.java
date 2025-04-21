package org.jgalaxy.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.javelinfx.log.LOG_Handler;
import org.javelinfx.units.EUDistance;
import org.javelinfx.units.IU_Unit;
import org.jgalaxy.IEntity;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.engine.IJG_Player;
import org.jgalaxy.orders.IJG_Orders;
import org.jgalaxy.orders.JG_Orders;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.server.SimpleClient;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_Unit;
import org.jgalaxy.utils.XML_Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class Global {

  static public EUDistance DISTANCEUNIT = EUDistance.LY;

  static public double INDPERSHIP = 10.0;

  static public long LASTTURNCHECK = 0L;

  static final public Map<String, Image> BANNERS = new HashMap<String, Image>();

  static final public BooleanProperty AUTOTURNLOAD = new SimpleBooleanProperty(false);

  static final public StringProperty CURRENTUSERNAME = new SimpleStringProperty("");
  static final public StringProperty CURRENTPASSWORD = new SimpleStringProperty("");

  static final public IntegerProperty CURRENTTURNNUMBER   = new SimpleIntegerProperty(-1);
  static final public StringProperty  CURRENTGAMEID       = new SimpleStringProperty("");
  static final public StringProperty  CURRENTPLAYERID     = new SimpleStringProperty("");
  static final public StringProperty  CURRENTSERVER       = new SimpleStringProperty("");

  static final public ObjectProperty<IJG_GameInfo>  CURRENTGAMEINFO         = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Game>      CURRENTGAME             = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Game>      CURRENTGAMECHANGED      = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Player>    CURRENTPLAYER           = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Player>    CURRENTPLAYERCHANGED    = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Faction>   CURRENTFACTION          = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Faction>   CURRENTFACTION_CHANGED  = new SimpleObjectProperty<>(null);

  static final public StringProperty CURRENTSENDNUMBER  = new SimpleStringProperty("");

  static final private ObservableList<IJG_Group> SELECTEDGROUPS = FXCollections.observableArrayList();

  static final private ObservableList<IEntity> SELECTEDENTITIES = FXCollections.observableArrayList();

  static final public ObjectProperty<IEntity> LASTSELECTEDENTITY = new SimpleObjectProperty<>(null);

  static public void clearSelections() {
    SELECTEDENTITIES.clear();
    LASTSELECTEDENTITY.set(null);
    return;
  }

  static public IEntity getLastSelectedEntity() {
    return LASTSELECTEDENTITY.get();
  }

  static public ObservableList<IEntity> getSelectedEntities() {
    return SELECTEDENTITIES;
  }

  static public ObservableList<IJG_Group> getSelectedGroups() {
    return SELECTEDGROUPS;
  }

  static public void removeSelectedIdentity( IEntity pEntity ) {
    if (pEntity!=null && IJG_Group.TYPE.equals(pEntity.entityType())) {
      SELECTEDGROUPS.remove((IJG_Group) pEntity );
    }
    SELECTEDENTITIES.remove(pEntity);
    if (LASTSELECTEDENTITY.get()!=pEntity) {
      LASTSELECTEDENTITY.set(null);
    }
    return;
  }

  static public void addSelectedIdentity( IEntity pEntity, boolean pClear ) {
    if (pClear) {
      SELECTEDGROUPS.clear();
    }
    if (pEntity!=null && IJG_Group.TYPE.equals(pEntity.entityType())) {
      if (!SELECTEDGROUPS.contains(pEntity)) {
        SELECTEDGROUPS.add((IJG_Group) pEntity);
      }
    }
    SELECTEDENTITIES.add( pEntity);
    LASTSELECTEDENTITY.set( pEntity );
    return;
  }

  static public IJG_Faction retrieveFactionByID( String pName ) {
    if (CURRENTFACTION_CHANGED.get()!=null && Objects.equals(CURRENTFACTION_CHANGED.get().id(), pName)) {
      return CURRENTFACTION_CHANGED.get();
    }
    if (CURRENTPLAYERCHANGED.get()!=null) {
      return CURRENTPLAYERCHANGED.get().getFactionByID(pName);
    }
    return null;
  }

  static public IJG_Faction resolveFaction( Object pFaction ) {
    if (pFaction instanceof IJG_Faction f) {
      return CURRENTFACTION_CHANGED.get().resolveFactionById(f.id());
    }
    if (pFaction instanceof String fid) {
      return CURRENTFACTION_CHANGED.get().resolveFactionById(fid);
    }
    return null;
  }

  static public void sendOrders() {
    IJG_Orders orders = JG_Orders.generateOf(Global.CURRENTTURNNUMBER.get(), Global.CURRENTFACTION.get(), Global.CURRENTFACTION_CHANGED.get());
    Document doc = XML_Utils.newXMLDocument();
    Node root = doc.createElement("root");
    doc.appendChild(root);
    orders.storeObject(null, root, "", "");
    try {
      String result = XML_Utils.documentToString(doc);
      System.out.println(result);

      String url =
        Global.CURRENTSERVER.get() + "/" +
          Global.CURRENTGAME.get().name() + "/" +
          Global.CURRENTTURNNUMBER.get() + "/" +
          Global.CURRENTPLAYERID.get() + "/" +
          Global.CURRENTFACTION.get().id() + "/" +
          "orders?alt=xml";
      HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .PUT(HttpRequest.BodyPublishers.ofString(result))
        .build();
      HttpResponse response = SimpleClient.createClient(Global.CURRENTUSERNAME.get(), Global.CURRENTPASSWORD.get()).send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Throwable e) {
      LOG_Handler.log("Global.sendOrders", Level.WARNING,e.getMessage(),e);
    }
    return;
  }

}
