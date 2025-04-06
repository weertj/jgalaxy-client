package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.awt.geom.Rectangle2D;

public class FleetRenderItem extends JavelinUIElement {

  public FleetRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public IJG_Fleet element() {
    return (IJG_Fleet) super.element();
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    gc.setFill(Color.CYAN);
    Rectangle2D outline = getOutline();
    gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight() );
    gc.fillText( element().name(), outline.getX(), outline.getY() );
//    gc.fillOval( 10*element().position().x(), 10*element().position().y(), 10, 10 );

    var fleet = element();
    var group = fleet.groups().getFirst();
    if (group.to()!=null) {
      var toplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetById(group.to());
      double x = pCanvas.toPixelX( toplanet.position().x(), Global.DISTANCEUNIT );
      double y = pCanvas.toPixelY( toplanet.position().y(), Global.DISTANCEUNIT );
      gc.setStroke(Color.WHITE);
      gc.strokeLine(outline.getX(), outline.getY(),x, y);
      gc.stroke();
    }

    return;
  }

  @Override
  public void pointerPressed( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    return;
  }

  @Override
  public void pointerEntered( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    System.out.println(" --- FLEET " + element().id());
    return;
  }

  @Override
  public void pointerLeft( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, ISP_Position pPosition) {
    return;
  }
}
