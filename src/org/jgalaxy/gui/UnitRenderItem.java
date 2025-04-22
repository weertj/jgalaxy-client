package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.CanvasUtils;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.colors.SColors;
import org.javelinfx.colors.SUX_Colors;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.spatial.SP_Line;
import org.jgalaxy.IEntity;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

public class UnitRenderItem extends JavelinUIElement {

  public UnitRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  public ZOOMTYPE zoomType() {
    return ZOOMTYPE.WITHLIMITS;
  }

  protected void beforeDrawUnit( IJavelinCanvas pCanvas ) {
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();
    if (Global.getLastSelectedEntity()!=null) {
      if (Global.getLastSelectedEntity() instanceof IEntity entity) {
        boolean selected = element().getClass().equals(entity.getClass()) && (Objects.equals(((IEntity)element()).id(), entity.id()));
        if (selected) {
          gc.setFill(SUX_Colors.SELECTION);
          gc.fillRect(outline.getX() - 4, outline.getY() - 4, outline.getWidth() + 8, outline.getHeight() + 8);
          if (element() instanceof IJG_Group group) {
            var faction = Global.resolveFaction(group.faction());
            // **** Draw range circle
//        IJG_UnitDesign design = faction.getUnitDesignById(group.unitDesign());
//        if (design.drive() > 0) {
            double range1turn = group.maxSpeed(Global.CURRENTGAMECHANGED.get(), faction);
            double radius = Math.abs(pCanvas.toPixelX(range1turn, Global.DISTANCEUNIT) - pCanvas.toPixelX(0, Global.DISTANCEUNIT));
            gc.setFill(Colors.TRAVELRANGE_FILL);
            gc.fillOval(outline.getCenterX() - radius, outline.getCenterY() - radius, radius * 2, radius * 2);
            gc.setStroke(Colors.TRAVELRANGE_STROKE);
            gc.setLineDashes(8, 8);
            gc.setLineWidth(0.5);
            gc.strokeOval(outline.getCenterX() - radius, outline.getCenterY() - radius, radius * 2, radius * 2);
            gc.setLineDashes(0);
            gc.setLineWidth(1);
//        }
          }
        }
      }
    }
    return;
  }

  protected void drawUnit(IJavelinCanvas pCanvas ) {
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();

    if (element() instanceof IJG_Group group) {
      Color mainCol = Colors.colorForMyFaction(group);
      gc.setFill(mainCol);
      gc.setStroke(mainCol);
      if (group.getNumberOf() <= 0) {
        gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      } else {
        gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
        if ("COL".equals(group.loadType())) {
          gc.setFill(Colors.COLS);
          gc.fillOval(outline.getMaxX()+outline.getWidth()/5, outline.getMinY(), outline.getWidth()/3, outline.getHeight()/3);
        } else if ("CAP".equals(group.loadType())) {
          gc.setFill(Colors.CAPS);
          gc.fillOval(outline.getMaxX()+outline.getWidth()/5, outline.getMinY(), outline.getWidth()/3, outline.getHeight()/3);
        } else if ("MAT".equals(group.loadType())) {
          gc.setFill(Colors.MATS);
          gc.fillOval(outline.getMaxX()+outline.getWidth()/5, outline.getMinY(), outline.getWidth()/3, outline.getHeight()/3);
        }
        gc.setStroke(Color.BLACK);
        gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      }
      if (pCanvas.getPixelZoom()>0.1) {
        gc.setFill(mainCol);
        gc.setStroke(mainCol);
        renderText(gc, "MapUnitDataFont", outline.getX() + outline.getWidth(), outline.getY() + outline.getHeight(), group.name());
      }
    }
    return;
  }

  protected void drawMovementPositionLines(IJavelinCanvas pCanvas) {
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();
    if (element() instanceof IJG_Group group) {
      // **** Movement line
      if (group.lastStaticPosition() != null && !Objects.equals(group.lastStaticPosition(), group.position())) {
        var fromplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetByPosition(group.lastStaticPosition());
        double x = pCanvas.toPixelX(fromplanet.position().x(), Global.DISTANCEUNIT);
        double y = pCanvas.toPixelY(fromplanet.position().y(), Global.DISTANCEUNIT);
        CanvasUtils.drawGradientLine(gc, outline.getX() + outline.getWidth() / 2, outline.getY() + outline.getHeight() / 2, x, y, Colors.FRIEND_LIGHT);
      }
      if (group.to() != null) {
        var toplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetById(group.to());
        double x = pCanvas.toPixelX(toplanet.position().x(), Global.DISTANCEUNIT);
        double y = pCanvas.toPixelY(toplanet.position().y(), Global.DISTANCEUNIT);
        gc.setLineWidth(3);
        gc.setLineDashes(8, 8);
        CanvasUtils.drawGradientLine(gc, x, y, outline.getX() + outline.getWidth() / 2, outline.getY() + outline.getHeight() / 2, Colors.FRIEND_DARK);
        gc.setLineDashes(0);
        gc.setLineWidth(1);

      } else if (!group.toPosition().equals(group.position())) {
        double x = pCanvas.toPixelX(group.toPosition().x(), Global.DISTANCEUNIT);
        double y = pCanvas.toPixelY(group.toPosition().y(), Global.DISTANCEUNIT);
        gc.setStroke(Colors.FRIEND_LIGHT);
        gc.setLineDashes(4, 4);
        gc.strokeLine(outline.getX() + outline.getWidth() / 2, outline.getY() + outline.getHeight() / 2, x, y);
        gc.stroke();
        gc.setLineDashes(0);
      }
    }
    return;
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

}
