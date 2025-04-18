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
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

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
    gc.save();
    Rectangle2D outline = getOutline();
//    if (grow()!=0.0) {
//      outline = SRectangle.shrink(outline, -grow()/2.0, -grow()/2.0, 1.0 + grow(), 1.0 + grow());
//    }
    boolean selected = element()==Global.getLastSelectedEntity();
    if (selected) {
      var faction = Global.CURRENTFACTION_CHANGED.get().resolveFactionById(element().faction());
      // **** Draw range circle
      IJG_UnitDesign design = faction.getUnitDesignById(element().unitDesign());
      if (design.drive()>0) {
        double range1turn = element().maxSpeed(faction);
        double radius = Math.abs(pCanvas.toPixelX(range1turn,Global.DISTANCEUNIT) - pCanvas.toPixelX(0,Global.DISTANCEUNIT));
        gc.setFill(Colors.TRAVELRANGE_FILL);
        gc.fillOval(outline.getCenterX()-radius, outline.getCenterY()-radius,radius*2, radius*2);
        gc.setStroke(Colors.TRAVELRANGE_STROKE);
        gc.setLineDashes(8, 8);
        gc.setLineWidth(2);
        gc.strokeOval(outline.getCenterX()-radius, outline.getCenterY()-radius,radius*2, radius*2);
        gc.setLineDashes(0);
        gc.setLineWidth(1);
      }

      gc.setFill(SUX_Colors.SELECTION);
      gc.fillRect(outline.getX()-4, outline.getY()-4, outline.getWidth()+8, outline.getHeight()+8);
    }
    Color mainCol = Colors.colorForMyFaction(element());
    gc.setFill(mainCol);
    gc.setStroke(mainCol);

    if (element().getNumberOf()<=0) {
      gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    } else {

      gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      if ("COL".equals(element().loadType())) {
        gc.setStroke(Colors.COLS);
      } else {
        gc.setStroke(Color.BLACK);
      }
      gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
    }

    if (pCanvas.getPixelZoom()>0.1) {
      renderText(gc, "MapUnitDataFont", outline.getX(), outline.getY(), element().name());
    }

    var group = element();
    if (group.to()!=null) {
      var toplanet = Global.CURRENTFACTION_CHANGED.get().planets().findPlanetById(group.to());
      double x = pCanvas.toPixelX( toplanet.position().x(), Global.DISTANCEUNIT );
      double y = pCanvas.toPixelY( toplanet.position().y(), Global.DISTANCEUNIT );
//      gc.setStroke(Colors.FRIEND_LIGHT);
//      gc.strokeLine(outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y);
//      gc.stroke();
      CanvasUtils.drawGradientLine(gc,outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y,Colors.FRIEND_LIGHT);
    } else if (group.toPosition()!=null) {
      double x = pCanvas.toPixelX( group.toPosition().x(), Global.DISTANCEUNIT );
      double y = pCanvas.toPixelY( group.toPosition().y(), Global.DISTANCEUNIT );
//      gc.setStroke(Colors.FRIEND_LIGHT);
//      gc.strokeLine(outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y);
//      gc.stroke();
      CanvasUtils.drawGradientLine(gc,outline.getX() + outline.getWidth()/2, outline.getY() + outline.getHeight()/2,x, y,Colors.FRIEND_LIGHT);

    }
    gc.restore();
    return;
  }
}
