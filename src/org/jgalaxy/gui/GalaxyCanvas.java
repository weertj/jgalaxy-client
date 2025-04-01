package org.jgalaxy.gui;

import org.javelinfx.canvas.JavelinCanvas;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.units.EUDistance;

public class GalaxyCanvas extends JavelinCanvas {

  static public GalaxyCanvas of() {
    GalaxyCanvas canvas = new GalaxyCanvas();
//    canvas.bgColor().setAndReport(FX_Colors.SILVER_BLUE);
    canvas.pixelModifier( 30.0, Global.DISTANCEUNIT );
    canvas.setPixelZoom(1);
    canvas.center().set(SP_Position.of( 5.0,5.0, Global.DISTANCEUNIT));
    return canvas;
  }

  private GalaxyCanvas() {
    return;
  }

}
