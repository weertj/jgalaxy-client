package org.jgalaxy.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.colors.SUX_Colors;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.jgalaxy.battle.ISB_BattleReport;
import org.jgalaxy.units.IJG_Bombing;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class BombingRenderItem extends JavelinUIElement {

  public BombingRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
  }

  @Override
  protected double defaultUnitSize() {
    return 40;
  }

  @Override
  public IJG_Bombing element() {
    return (IJG_Bombing) super.element();
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();

    if (element()==Global.getLastSelectedEntity()) {
      gc.setFill(SUX_Colors.SELECTION);
      gc.fillRect(outline.getX()-4, outline.getY()-4, outline.getWidth()+8, outline.getHeight()+8);
    }

    List<Color> colors = Colors.bombingColorsFor(element());

    double opacity = 0.5;
    int points = 8;
    double radiusInner = 0.8*(outline.getWidth()/2);
    double radiusOuter = radiusInner*1.6;
    double centerX = outline.getCenterX();
    double centerY = outline.getCenterY();

    gc.save();
    gc.setGlobalAlpha(opacity);

    double[] xPoints = new double[(points<<1)];
    double[] yPoints = new double[(points<<1)];
    double angleStep = Math.PI / points;
    for( Color col : colors ) {
      for (int i = 0; i < points*2; i++) {
        double r = (i % 2 == 0) ? radiusOuter : radiusInner;
        double angle = i * angleStep;
        xPoints[i] = centerX + Math.cos(angle) * r;
        yPoints[i] = centerY + Math.sin(angle) * r;
      }
      gc.setFill(col);
      gc.fillPolygon(xPoints, yPoints, xPoints.length);
      radiusOuter *= 0.8;
      radiusInner *= 0.8;
    }
    gc.restore();


    return;
  }

}
