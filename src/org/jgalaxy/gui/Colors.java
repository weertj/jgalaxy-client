package org.jgalaxy.gui;

import javafx.scene.paint.Color;
import org.javelinfx.colors.SColors;
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

  static final public Color MAPBG           = Color.web( "#333333" );
  static final public Color MAPLINE         = Color.web( "#999999" );

  static final public Color POPULATION      = SColors.KASHMIR;
  static final public Color COLS            = SColors.WHITE_GRAPE;
  static final public Color CARGO           = SColors.VAPOR_BLUE;

  // **** MIL-STD-2525D
  static final public Color HOSTILE_DARK    = Color.rgb( 200,0,0 );
  static final public Color HOSTILE_MEDIUM  = Color.rgb( 255,48,49 );
  static final public Color HOSTILE_LIGHT   = Color.rgb( 255,128,128 );
  static final public Color FRIEND_DARK     = Color.rgb( 0,107,140);
  static final public Color FRIEND_MEDIUM   = Color.rgb( 0,168,220);
  static final public Color FRIEND_LIGHT    = Color.rgb( 128,224,255);
  static final public Color NEUTRAL_DARK    = Color.rgb( 0,160,0);
  static final public Color NEUTRAL_MEDIUM  = Color.rgb( 0,226,0);
  static final public Color NEUTRAL_LIGHT   = Color.rgb( 170,255,170);
  static final public Color UNKNOWN_DARK    = Color.rgb( 225,220,0);
  static final public Color UNKNOWN_MEDIUM  = Color.rgb( 255,255,0);
  static final public Color UNKNOWN_LIGHT   = Color.rgb( 255,255,128);
  static final public Color CIVILIAN_DARK   = Color.web( "#500050" );
  static final public Color CIVILIAN_MEDIUM = Color.web( "#800080" );
  static final public Color CIVILIAN_LIGHT  = Color.web( "#FF80FF" );
//  static final public Color HOSTILE_DARK    = Color.web( "#C80000" );
//  static final public Color HOSTILE_MEDIUM  = SColors.POPPY_RED;
//  static final public Color HOSTILE_LIGHT   = Color.web( "#EE8080" );
//  static final public Color FRIEND_DARK     = SColors.LIMPET_SHELL.darker().darker();
//  static final public Color FRIEND_MEDIUM   = SColors.LIMPET_SHELL.darker();
//  static final public Color FRIEND_LIGHT    = SColors.LIMPET_SHELL;
//  static final public Color NEUTRAL_DARK    = Color.web( "#00A000" );
//  static final public Color NEUTRAL_MEDIUM  = Color.web( "#006B54" );
//  static final public Color NEUTRAL_LIGHT   = Color.web( "#AAFFAA" );
//  static final public Color UNKNOWN_DARK    = SColors.MISTED_MARIGOLD;
//  static final public Color UNKNOWN_MEDIUM  = SColors.MISTED_MARIGOLD.brighter();
//  static final public Color UNKNOWN_LIGHT   = SColors.MISTED_MARIGOLD.brighter().brighter();
//  static final public Color CIVILIAN_DARK   = Color.web( "#500050" );
//  static final public Color CIVILIAN_MEDIUM = Color.web( "#800080" );
//  static final public Color CIVILIAN_LIGHT  = Color.web( "#FFFF80" );

  private Colors() {
  }

}
