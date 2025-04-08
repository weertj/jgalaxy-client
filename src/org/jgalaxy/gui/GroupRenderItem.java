package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;

import java.awt.geom.Rectangle2D;

public class GroupRenderItem extends JavelinUIElement {

  public GroupRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public IJG_Group element() {
    return (IJG_Group)super.element();
  }

//  @Override
//  public ZOOMTYPE zoomType() {
//    return ZOOMTYPE.WITHLIMITS;
//  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    Color mainCol = Colors.colorForMyFaction(element());
    gc.setFill(mainCol);
    gc.setStroke(mainCol);
    Rectangle2D outline = getOutline();
    if (element().getNumberOf()<=0) {
      gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    } else {
      gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      gc.setStroke(Color.BLACK);
      gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    }
    gc.fillText( element().name(), outline.getX(), outline.getY() );

    var group = element();
    if (group.to()!=null) {
      var toplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetById(group.to());
      double x = pCanvas.toPixelX( toplanet.position().x(), Global.DISTANCEUNIT );
      double y = pCanvas.toPixelY( toplanet.position().y(), Global.DISTANCEUNIT );
      gc.setStroke(Colors.FRIEND_LIGHT);
      gc.strokeLine(outline.getX(), outline.getY(),x, y);
      gc.stroke();
    } else if (group.toPosition()!=null) {
      double x = pCanvas.toPixelX( group.toPosition().x(), Global.DISTANCEUNIT );
      double y = pCanvas.toPixelY( group.toPosition().y(), Global.DISTANCEUNIT );
      gc.setStroke(Colors.FRIEND_LIGHT);
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
    System.out.println(" --- GROUP " + element().id());
    return;
  }

  @Override
  public void pointerLeft( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, ISP_Position pPosition) {
    return;
  }
}
