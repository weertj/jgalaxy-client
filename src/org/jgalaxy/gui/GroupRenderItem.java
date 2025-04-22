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

    gc.restore();
    return;
  }
}
