package org.jgalaxy.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.map.IMAP_Map;

import java.awt.geom.Rectangle2D;


public class MapRenderItem extends JavelinUIElement {

  private final IMAP_Map mMap;
  private final ObjectProperty<ISP_Position> mMouseOverMapPosition = new SimpleObjectProperty<>(null);
  private final ObjectProperty<ISP_Position> mMiddleCanvasPosition = new SimpleObjectProperty<>(null);
  private final ObjectProperty<ISP_Position> mMiddleMoveToCanvasPosition = new SimpleObjectProperty<>(null);

  public MapRenderItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
    mMap = (IMAP_Map) element;
    return;
  }

  public ObjectProperty<ISP_Position> mouseOverMapPositionProperty() {
    return mMouseOverMapPosition;
  }

  public ObjectProperty<ISP_Position> middleCanvasPositionProperty() {
    return mMiddleCanvasPosition;
  }

  public ObjectProperty<ISP_Position> middleMoveToCanvasPositionProperty() {
    return mMiddleMoveToCanvasPosition;
  }

  @Override
  public void calculateRenderPositions(IJavelinCanvas pCanvas, double pRingMod, int pItemSeqNr, int pTotItems, double pSmoothFactor) {
    double xstart = pCanvas.toPixelX(mMap.xStart(), Global.DISTANCEUNIT );
    double ystart = pCanvas.toPixelY(mMap.yStart(), Global.DISTANCEUNIT );
    double xend   = pCanvas.toPixelX(mMap.xEnd(), Global.DISTANCEUNIT );
    double yend   = pCanvas.toPixelY(mMap.yEnd(), Global.DISTANCEUNIT );
    setOutline( new Rectangle2D.Double(xstart, yend, xend - xstart, ystart - yend) );
    return;
  }

  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();

    Rectangle2D outline = getOutline();
//    if (pCanvas.getPixelZoom()<2) {
//      Image im = SImages.getImage("data/gfx/mapelements/bg.jpg");
//      gc.drawImage(im, outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
//    } else {
//      gc.setFill(Colors.MAPBG);
//      gc.fillRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());
//    }
    gc.setStroke(Colors.MAPLINE);
    gc.strokeRect(outline.getX(), outline.getY(), outline.getWidth(), outline.getHeight());

    if (mMiddleMoveToCanvasPosition.get()!=null) {
      var mmcp = mMiddleMoveToCanvasPosition.get();
      var curcen = pCanvas.center().get();
      double offx = (mmcp.x()-curcen.x())/10.0;
      curcen = curcen.x(curcen.x() + offx );
      curcen = curcen.y(curcen.y() + (mmcp.y()-curcen.y())/10.0 );
      pCanvas.center().set(curcen);
      if (Math.abs(offx)<0.001) {
        mMiddleMoveToCanvasPosition.set(null);
      }
    }

    double w = pCanvas.canvas().getWidth();
    double h = pCanvas.canvas().getHeight();
    double x = pCanvas.fromPixelX(w/2,Global.DISTANCEUNIT );
    double y = pCanvas.fromPixelY(h/2,Global.DISTANCEUNIT );
    middleCanvasPositionProperty().set(SP_Position.of(x,y,Global.DISTANCEUNIT ));
    return;
  }

  @Override
  public void pointerMoved( IJavelinCanvas pCanvas, IJL_PlayerContext pContext, S_Pointer.POINTER pPointer, ISP_Position pPosition) {
    super.pointerMoved(pCanvas, pContext, pPointer, pPosition);
    double x = pCanvas.fromPixelX(pPosition.x(),Global.DISTANCEUNIT );
    double y = pCanvas.fromPixelY(pPosition.y(),Global.DISTANCEUNIT );
    mouseOverMapPositionProperty().set(SP_Position.of(x,y,Global.DISTANCEUNIT));
    return;
  }
}
