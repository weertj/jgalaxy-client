package org.jgalaxy.gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import org.javelinfx.colors.SColors;
import org.jgalaxy.IFactionOwner;
import org.jgalaxy.battle.ISB_BattleReport;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Bombing;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.util.ArrayList;
import java.util.List;

public class Colors {

  static public Color canvasTextColor() {
    return SColors.ANTIQUE_WHITE;
  }


  static public Color planetUIColor() {
    return SColors.KASHMIR;
  }

  static public Color factionUIColor() {
    return SColors.DAMSON;
  }

  static public Color groupUIColor() {
    return SColors.DEJAVU_BLUE;
  }

  static public Color fleetUIColor() {
    return SColors.DEJAVU_BLUE.brighter();
  }

  static public Color battlesUIColor() {
    return SColors.WINTERBERRY;
  }

  static public List<Color> battleColorsFor(ISB_BattleReport pReport) {
    List<Color> colors = new ArrayList<>();
    colors.add(Color.YELLOW);
    colors.add(Color.ORANGE);
    colors.add(Color.RED);
    return colors;
  }

  static public List<Color> bombingColorsFor(IJG_Bombing pBombing) {
    List<Color> colors = new ArrayList<>();
    colors.add(Color.GREEN);
    colors.add(Color.DARKGRAY);
    colors.add(Color.LIGHTGRAY);
    return colors;
  }

  static public Color colorForMyFaction(IFactionOwner pOwner) {
    if (pOwner.faction()==null) {
      if (pOwner instanceof IJG_Planet planet) {
        if (planet.size()>=0) {
          return NEUTRAL_DARK;
        }
        return UNKNOWN_DARK;
      }
      return UNKNOWN_MEDIUM;
    }
    IJG_Faction faction = Global.GAMECONTEXT.currentFactionChanged();
    String factionid = faction.id();

    // **** Fleet?
    if (pOwner instanceof IJG_Fleet fleet) {
      if (factionid.equals( pOwner.faction())) {
        return FRIEND_FLEET;
      } else {
        if (faction.atWarWith().contains(pOwner.faction())) {
          return HOSTILE_FLEET;
        } else {
          return NEUTRAL_FLEET;
        }
      }
    }

    if (factionid.equals( pOwner.faction())) {
      return FRIEND_GROUP;
    } else {
      if (faction.atWarWith().contains(pOwner.faction())) {
        return Colors.HOSTILE_GROUP;
      } else {
        return Colors.NEUTRAL_GROUP;
      }
    }
  }


//  String factionid = Global.CURRENTFACTION_CHANGED.get().id();

  static final public Color LABEL_VALUE_VALUE = SColors.MISTED_MARIGOLD;

  static final public Color MAPBG           = Color.web( "#333333" );
  static final public Color MAPLINE         = Color.web( "#999999" );

  static final public Color PLANNED    = SColors.MISTED_MARIGOLD;

  static final public Color TRAVELRANGE_STROKE  = SColors.MISTED_MARIGOLD;
  static final public Color TRAVELRANGE_FILL    = SColors.transparent(TRAVELRANGE_STROKE, 0.1);

  static final public Color POPULATION      = SColors.KASHMIR;
  static final public Color COLS            = SColors.KASHMIR;
  static final public Color CAPS            = SColors.ORANGEADE;
  static final public Color MATS            = SColors.COCOON;
  static final public Color CARGO           = SColors.VAPOR_BLUE;

  // **** MIL-STD-2525D
  static final public Color HOSTILE_DARK    = Color.rgb( 200,0,0 );
  static final public Color HOSTILE_MEDIUM  = Color.rgb( 255,48,49 );
  static final public Color HOSTILE_LIGHT   = Color.rgb( 255,128,128 );
  static final public Color FRIEND_DARK     = Color.rgb( 0,107,140);
  static final public Color FRIEND_MEDIUM   = Color.rgb( 0,168,220);
  static final public Color FRIEND_LIGHT    = Color.rgb( 0,224,255);
  static final public Color NEUTRAL_DARK    = Color.rgb( 0,160,0);
  static final public Color NEUTRAL_MEDIUM  = Color.rgb( 0,226,0);
  static final public Color NEUTRAL_LIGHT   = Color.rgb( 40,255,40);
  static final public Color UNKNOWN_DARK    = Color.rgb( 225,220,0);
  static final public Color UNKNOWN_MEDIUM  = Color.rgb( 255,255,0);
  static final public Color UNKNOWN_LIGHT   = Color.rgb( 255,255,128);
  static final public Color CIVILIAN_DARK   = Color.web( "#500050" );
  static final public Color CIVILIAN_MEDIUM = Color.web( "#800080" );
  static final public Color CIVILIAN_LIGHT  = Color.web( "#FF80FF" );

  static       public Color FRIEND_GROUP  = FRIEND_MEDIUM;
  static       public Color FRIEND_FLEET  = FRIEND_LIGHT;
  static       public Color NEUTRAL_GROUP = NEUTRAL_MEDIUM;
  static       public Color NEUTRAL_FLEET = NEUTRAL_LIGHT;
  static       public Color HOSTILE_GROUP = HOSTILE_MEDIUM;
  static       public Color HOSTILE_FLEET = HOSTILE_LIGHT;

  private Colors() {
  }

}
