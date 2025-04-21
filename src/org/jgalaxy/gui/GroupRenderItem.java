package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.javelinfx.canvas.CanvasUtils;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.colors.SUX_Colors;
import org.javelinfx.fonts.F_Fonts;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.shape.SRectangle;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.spatial.SP_Line;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

public class GroupRenderItem extends UnitRenderItem {

  public GroupRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public IJG_Group element() {
    return (IJG_Group)super.element();
  }

  @Override
  public ZOOMTYPE zoomType() {
    return ZOOMTYPE.WITHLIMITS;
  }

  private void drawArrowLine(IJavelinCanvas pCanvas, ISP_Position pFrom, ISP_Position pTo) {
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();
//    var pos1 = SP_Position.of(outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,Global.DISTANCEUNIT);
//    var pos2 = SP_Position.of(x,y,Global.DISTANCEUNIT);
    var polylines = SP_Line.lineToPolygon(List.of(pFrom,pTo), 8, true, 0.5);
    gc.beginPath();
    gc.moveTo(polylines.getFirst().x(), polylines.getFirst().y());
    for( ISP_Position pos : polylines ) {
      gc.lineTo(pos.x(), pos.y());
    }
    gc.closePath();
    gc.fill();
    return;
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    gc.save();

    beforeDrawUnit(pCanvas);
    drawUnit(pCanvas);
    drawMovementPositionLines(pCanvas);

//    var group = element();
//    // **** Movement line
//    if (group.lastStaticPosition()!=null && !Objects.equals(group.lastStaticPosition(), group.position())) {
//      var fromplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetByPosition(group.lastStaticPosition());
//      double x = pCanvas.toPixelX( fromplanet.position().x(), Global.DISTANCEUNIT );
//      double y = pCanvas.toPixelY( fromplanet.position().y(), Global.DISTANCEUNIT );
//      CanvasUtils.drawGradientLine(gc,outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y,Colors.FRIEND_LIGHT);
//    }
//    if (group.to()!=null) {
//      var toplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetById(group.to());
//      double x = pCanvas.toPixelX( toplanet.position().x(), Global.DISTANCEUNIT );
//      double y = pCanvas.toPixelY( toplanet.position().y(), Global.DISTANCEUNIT );
//      gc.setLineWidth(3);
//      gc.setLineDashes(8,8);
//      CanvasUtils.drawGradientLine(gc, x,y,outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,Colors.FRIEND_DARK);
//      gc.setLineDashes(0);
//      gc.setLineWidth(1);
//
//    } else if (!group.toPosition().equals(group.position())) {
//      double x = pCanvas.toPixelX( group.toPosition().x(), Global.DISTANCEUNIT );
//      double y = pCanvas.toPixelY( group.toPosition().y(), Global.DISTANCEUNIT );
//      gc.setStroke(Colors.FRIEND_LIGHT);
//      gc.setLineDashes(4,4);
//      gc.strokeLine(outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y);
//      gc.stroke();
//      gc.setLineDashes(0);
//    }
    gc.restore();
    return;
  }
}
