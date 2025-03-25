package org.jgalaxy.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.engine.IJG_Player;

public class Global {

  static final public IntegerProperty CURRENTTURNNUMBER = new SimpleIntegerProperty(0);
  static final public ObjectProperty<IJG_GameInfo>  CURRENTGAMEINFO = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Game>      CURRENTGAME = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Game>      CURRENTGAMECHANGED = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Player>    CURRENTPLAYER = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Player>    CURRENTPLAYERCHANGED = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Faction>   CURRENTFACTION = new SimpleObjectProperty<>(null);
  static final public ObjectProperty<IJG_Faction>   CURRENTFACTION_CHANGED = new SimpleObjectProperty<>(null);


}
