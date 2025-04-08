package org.jgalaxy.gui;

import javafx.scene.paint.Color;
import org.jgalaxy.IFactionOwner;
import org.jgalaxy.planets.IJG_Planet;

public class Colors {

  static public Color colorForMyFaction(IFactionOwner pOwner) {
    if (pOwner.faction()==null) {
      if (pOwner instanceof IJG_Planet planet) {
        return UNKNOWN_DARK;
      }
      return UNKNOWN_MEDIUM;
    }
    String factionid = Global.CURRENTFACTION_CHANGED.get().id();
    if (factionid.equals( pOwner.faction())) {
      return FRIEND_MEDIUM;
    } else {
      if (Global.CURRENTFACTION_CHANGED.get().atWarWith().contains(pOwner.faction())) {
        return Colors.HOSTILE_MEDIUM;
      } else {
        return Colors.NEUTRAL_MEDIUM;
      }
    }
  }


//  String factionid = Global.CURRENTFACTION_CHANGED.get().id();

  static final public Color MAPBG           = Color.web( "#444444" );
  static final public Color MAPLINE         = Color.web( "#999999" );


  // **** MIL-STD-2525D
  static final public Color HOSTILE_DARK    = Color.web( "#C80000" );
  static final public Color HOSTILE_MEDIUM  = Color.web( "#EE3031" );
  static final public Color HOSTILE_LIGHT   = Color.web( "#EE8080" );
  static final public Color FRIEND_DARK     = Color.web( "#006B8C" );
  static final public Color FRIEND_MEDIUM   = Color.web( "#00A8DC" );
  static final public Color FRIEND_LIGHT    = Color.web( "#80E0FF" );
  static final public Color NEUTRAL_DARK    = Color.web( "#00A000" );
  static final public Color NEUTRAL_MEDIUM  = Color.web( "#00E200" );
  static final public Color NEUTRAL_LIGHT   = Color.web( "#AAFFAA" );
  static final public Color UNKNOWN_DARK    = Color.web( "#AAAA00" );
  static final public Color UNKNOWN_MEDIUM  = Color.web( "#FFFF00" );
  static final public Color UNKNOWN_LIGHT   = Color.web( "#FFFF80" );
  static final public Color CIVILIAN_DARK   = Color.web( "#500050" );
  static final public Color CIVILIAN_MEDIUM = Color.web( "#800080" );
  static final public Color CIVILIAN_LIGHT  = Color.web( "#FFFF80" );

  private Colors() {
  }

}
