package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.colors.SUX_Colors;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.shape.SRectangle;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.awt.geom.Rectangle2D;

public class PlanetRenderItem extends JavelinUIElement {

  public PlanetRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public IJG_Planet element() {
    return (IJG_Planet)super.element();
  }

  @Override
  public ZOOMTYPE zoomType() {
    return ZOOMTYPE.WITHLIMITS;
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();

//    if (grow()!=0.0) {
//      outline = SRectangle.shrink(outline, -grow()/2.0, -grow()/2.0, 1.0 + grow(), 1.0 + grow());
//    }

//    gc.setFill(Color.BLACK);
//    double grow = 0.4;
//    gc.fillArc(outline.getX()-outline.getWidth() * grow/2, outline.getY()-outline.getHeight() * grow/2, outline.getWidth() * (1.0+grow), outline.getHeight() * (1.0+grow), 0, 270, ArcType.ROUND);
//    gc.setFill(Colors.CIVILIAN_MEDIUM);
//    grow = 0.3;
//    gc.fillArc(outline.getX()-outline.getWidth() * grow/2, outline.getY()-outline.getHeight() * grow/2, outline.getWidth() * (1.0+grow), outline.getHeight() * (1.0+grow), 0, 270, ArcType.ROUND);

    if (element()==Global.getLastSelectedEntity()) {
      gc.setFill(SUX_Colors.SELECTION);
      gc.fillOval(outline.getX()-4, outline.getY()-4, outline.getWidth()+8, outline.getHeight()+8);
    }

    gc.setStroke(Colors.colorForMyFaction(element()));
    gc.setFill(Colors.colorForMyFaction(element()));
    if (element().faction()==null) {
      gc.strokeOval(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    } else {
      gc.fillOval(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      gc.setStroke(Color.BLACK);
      gc.strokeOval(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight() );
    }
    if (pCanvas.getPixelZoom()>0.1) {
      gc.fillText(element().name(), outline.getX(), outline.getY());
    }

//    gc.fillOval( 10*element().position().x(), 10*element().position().y(), 10, 10 );
    return;
  }

  @Override
  public void pointerPressed( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    if (pPointer==S_Pointer.POINTER.SECONDARY) {
      for(IJG_Group group : Global.getSelectedGroups()) {
        if (group instanceof IJG_Fleet fleet) {
          fleet.groups().stream().forEach( g -> sendGroupTo(g, element()));
        } else {
          Integer nr = -1;
          try {
            nr = Integer.parseInt(Global.CURRENTSENDNUMBER.get());
          } catch (NumberFormatException e) {
          }
          if (nr>0 && nr.intValue()<group.getNumberOf()) {
            IJG_Faction faction = Global.CURRENTFACTION_CHANGED.get();
            IJG_Group breakgroup = group.breakOffGroup(Global.CURRENTGAMECHANGED.get(), faction, group.id(), nr);
            faction.groups().addGroup(breakgroup);
            sendGroupTo(breakgroup, element());
            faction.newChange();
          } else {
            sendGroupTo(group, element());
          }
        }
      }
    }
    return;
  }

  private void sendGroupTo( IJG_Group pGroup, IJG_Planet pPlanet ) {
    pGroup.setTo(pPlanet.id());
    return;
  }

}
