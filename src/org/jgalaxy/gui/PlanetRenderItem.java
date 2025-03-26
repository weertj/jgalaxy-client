package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinRenderItem;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.planets.IJG_Planet;
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
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    if (element().owner()==null) {
      gc.setFill(Color.LIGHTGRAY);
    } else {
      gc.setFill(Color.GREEN);
    }
    Rectangle2D outline = getOutline();
    gc.fillOval(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight() );
    gc.fillText( element().name(), outline.getX(), outline.getY() );
//    gc.fillOval( 10*element().position().x(), 10*element().position().y(), 10, 10 );
    return;
  }

  @Override
  public void pointerPressed(IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    if (pPointer==S_Pointer.POINTER.SECONDARY) {
      System.out.println(" snensnd " + element().name());
      for(IJG_Group group : Global.SELECTEDGROUPS) {
        group.setTo(element().id());
      }
    }
    return;
  }

  @Override
  public void pointerEntered(IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    return;
  }

  @Override
  public void pointerLeft(IJL_PlayerContext pContext, ISP_Position pPosition) {
    return;
  }
}
