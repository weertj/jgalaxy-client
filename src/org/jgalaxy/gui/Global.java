package org.jgalaxy.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.javelinfx.units.EUDistance;
import org.javelinfx.units.IU_Unit;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.engine.IJG_Player;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_Unit;

import java.util.Objects;

public class Global {

  static public EUDistance DISTANCEUNIT = EUDistance.LY;

  static final public StringProperty CURRENTUSERNAME = new SimpleStringProperty("");
  static final public StringProperty CURRENTPASSWORD = new SimpleStringProperty("");

  static final public IntegerProperty CURRENTTURNNUMBER   = new SimpleIntegerProperty(0);
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

  static final public ObservableList<IJG_Group> SELECTEDGROUPS = FXCollections.observableArrayList();

  static public IJG_Faction retrieveFactionByID( String pName ) {
    if (CURRENTFACTION_CHANGED.get()!=null && Objects.equals(CURRENTFACTION_CHANGED.get().id(), pName)) {
      return CURRENTFACTION_CHANGED.get();
    }
    if (CURRENTPLAYERCHANGED.get()!=null) {
      return CURRENTPLAYERCHANGED.get().getFactionByID(pName);
    }
    return null;
  }

}
