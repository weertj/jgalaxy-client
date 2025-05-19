package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.javelinfx.canvas.CanvasUtils;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.colors.SColors;
import org.javelinfx.colors.SUX_Colors;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.spatial.SP_Line;
import org.jgalaxy.IEntity;
import org.jgalaxy.ai.AI_Defaults;
import org.jgalaxy.ai.NNetwork;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
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
            var faction = Global.GAMECONTEXT.resolveFaction(group.faction());
            // **** Draw range circle
            double xcenter = pCanvas.toPixelX(group.calcCurrentPosition().x(), Global.DISTANCEUNIT );
            double ycenter = pCanvas.toPixelY(group.calcCurrentPosition().y(), Global.DISTANCEUNIT );
//        IJG_UnitDesign design = faction.getUnitDesignById(group.unitDesign());
//        if (design.drive() > 0) {
            double range1turn = group.maxSpeed(Global.GAMECONTEXT.currentGameChanged(), faction);
            double radius = Math.abs(pCanvas.toPixelX(range1turn, Global.DISTANCEUNIT) - pCanvas.toPixelX(0, Global.DISTANCEUNIT));
            gc.setFill(Colors.TRAVELRANGE_FILL);
            gc.fillOval(xcenter-radius, ycenter-radius, radius * 2, radius * 2);
            gc.setStroke(Colors.TRAVELRANGE_STROKE);
            gc.setLineDashes(8, 8);
            gc.setLineWidth(0.5);
            gc.strokeOval(xcenter-radius, ycenter-radius, radius * 2, radius * 2);
            gc.setLineDashes(0);
            gc.setLineWidth(1);
//        }
          }
        }
      }
    }
    return;
  }

  public static List<Pair<String,Double>> getSortedOutputLabels(double[] outputs) {
    String[] OUTPUT_LABELS = {"RECON", "SG", "TRANSPORT"};

    List<Pair<String,Double>> results = new ArrayList<>();

    for (int i = 0; i < outputs.length; i++) {
      String label = (i < OUTPUT_LABELS.length) ? OUTPUT_LABELS[i] : "CLASS_" + i;
      results.add(new Pair(label, outputs[i]));
    }

    results.sort(Comparator.comparingDouble( value -> (Double)((Pair)value).getValue() ).reversed());
    return results;
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
        String loadType = group.loadType();
        if (loadType!=null) {
          if (loadType.equals("COL")) {
            gc.setFill(Colors.COLS);
            gc.fillOval(outline.getMaxX() + outline.getWidth() / 5, outline.getMinY(), outline.getWidth() / 3, outline.getHeight() / 3);
          } else if (loadType.equals("CAP")) {
            gc.setFill(Colors.CAPS);
            gc.fillOval(outline.getMaxX() + outline.getWidth() / 5, outline.getMinY(), outline.getWidth() / 3, outline.getHeight() / 3);
          } else if (loadType.equals("MAT")) {
            gc.setFill(Colors.MATS);
            gc.fillOval(outline.getMaxX() + outline.getWidth() / 5, outline.getMinY(), outline.getWidth() / 3, outline.getHeight() / 3);
          }
        }
        gc.setStroke(Color.BLACK);
        gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
      }
      if (pCanvas.getPixelZoom()>0.1) {
        gc.setFill(mainCol);
        renderText(gc, "MapUnitDataFont", outline.getX() + outline.getWidth(), outline.getY() + outline.getHeight(), group.name());
      }
      if (pCanvas.getPixelZoom()>2) {
        gc.setFill(Colors.LABEL_VALUE_VALUE);
        renderText(gc, "MapUnitDataFont", outline.getX() + outline.getWidth(), outline.getY(), group.getNumberOf() + "x");
      }

//      IJG_Faction faction = Global.GAMECONTEXT.retrieveFactionByID(group.faction() );
//      var ud = faction.getUnitDesignById(group.unitDesign());
//      if (ud!=null) {
//        double[] input = new double[5];
//        input[0] = ud.drive()/100.0;
//        input[1] = ud.weapons()/100.0;
//        input[2] = ud.nrweapons()/100.0;
//        input[3] = ud.shields()/100.0;
//        input[4] = ud.cargo()/100.0;
//        var outputs = AI_Defaults.SHIPCLASSNN.forward(input);
//        var result = getSortedOutputLabels(outputs);
//        if ("TRANSPORT".equals(result.getFirst().getKey())) {
//          gc.setFill(Color.BLACK);
//          double w = outline.getWidth();
//          double h = outline.getHeight();
//          gc.fillRect(outline.getX()+w*0.2, outline.getY()+h*0.2, w-w*0.4, h-h*0.4);
//        }
//      }
//

    }
    return;
  }

  protected void drawMovementPositionLines(IJavelinCanvas pCanvas) {
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();
    if (element() instanceof IJG_Group group) {
      // **** Movement line
      if (group.lastStaticPosition() != null && !Objects.equals(group.lastStaticPosition(), group.position())) {
        var fromplanet = Global.GAMECONTEXT.currentFactionChanged().planets().findPlanetByPosition(group.lastStaticPosition());
        double x = pCanvas.toPixelX(fromplanet.position().x(), Global.DISTANCEUNIT);
        double y = pCanvas.toPixelY(fromplanet.position().y(), Global.DISTANCEUNIT);
        CanvasUtils.drawGradientLine(gc, outline.getX() + outline.getWidth() / 2, outline.getY() + outline.getHeight() / 2, x, y, Colors.FRIEND_LIGHT);
      }
      if (group.to() != null) {
        var toplanet = Global.GAMECONTEXT.currentFactionChanged().planets().findPlanetById(group.to());
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
