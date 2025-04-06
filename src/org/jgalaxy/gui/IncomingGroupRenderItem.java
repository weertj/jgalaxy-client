package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.IFactionOwner;
import org.jgalaxy.units.IJG_Incoming;

import java.awt.geom.Rectangle2D;

public class IncomingGroupRenderItem extends JavelinUIElement {

  public IncomingGroupRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public IJG_Incoming element() {
    return (IJG_Incoming)super.element();
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    IJG_Incoming incoming = element();
    Color mainCol = Colors.colorForMyFaction(new IFactionOwner() {
      @Override public String faction() { return null; }
      @Override public void setFaction(String faction) {}
    });
    gc.setFill(mainCol);
    gc.setStroke(mainCol);
    Rectangle2D outline = getOutline();
    gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    gc.fillText( "mass="+element().mass(), outline.getX(), outline.getY() );

    double fromx = pCanvas.toPixelX( incoming.from().x(), Global.DISTANCEUNIT );
    double fromy = pCanvas.toPixelY( incoming.from().y(), Global.DISTANCEUNIT );
    double tox   = pCanvas.toPixelX( incoming.to().x(), Global.DISTANCEUNIT );
    double toy   = pCanvas.toPixelY( incoming.to().y(), Global.DISTANCEUNIT );
    gc.setStroke(Colors.UNKNOWN_DARK);
    gc.strokeLine(fromx, fromy, tox,toy);
    gc.stroke();

    return;
  }

  @Override
  public void pointerPressed( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    return;
  }

  @Override
  public void pointerEntered( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    return;
  }

  @Override
  public void pointerLeft( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, ISP_Position pPosition) {
    return;
  }
}
