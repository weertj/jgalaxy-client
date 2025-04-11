package org.jgalaxy.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.javelinfx.canvas.IJavelinCanvas;
import org.javelinfx.canvas.JavelinUIElement;
import org.javelinfx.image.SImages;
import org.javelinfx.player.IJL_PlayerContext;
import org.javelinfx.spatial.ISP_Position;
import org.javelinfx.spatial.SP_Position;
import org.javelinfx.window.S_Pointer;
import org.jgalaxy.map.IMAP_Map;

import java.awt.geom.Rectangle2D;


public class BackgroundItem extends JavelinUIElement {

  public BackgroundItem(String id, Object element, ISP_Position position) {
    super(id, element, position);
    return;
  }


  @Override
  public void render(IJavelinCanvas pCanvas, IJL_PlayerContext pContext) {
    super.render(pCanvas, pContext);
    GraphicsContext gc = pCanvas.context();
    Rectangle2D outline = getOutline();
    Image im = SImages.getImage("data/gfx/mapelements/bg.jpg");
    double ratio = im.getWidth() / im.getHeight();
    gc.drawImage(im, 0, 0, pCanvas.canvas().getWidth(), pCanvas.canvas().getHeight()*ratio );
    return;
  }

}
