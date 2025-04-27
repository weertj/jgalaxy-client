package org.jgalaxy.gui;

import org.jgalaxy.engine.IJG_Game;
import org.jgalaxy.planets.EProduceType;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Group;
import org.jgalaxy.units.IJG_UnitDesign;

import java.util.Objects;

public class SOrders {

  static public void renamePlanet(IJG_Planet pPlanet, String pNewName ) {
    pPlanet.setName(pNewName);
    Global.GAMECONTEXT.currentGameChanged().galaxy().map().planets().findPlanetById(pPlanet.id()).setName(pNewName);
    Global.GAMECONTEXT.currentFactionChanged().newChange();
    return;
  }


  static public void setPlanetProduction( IJG_Planet pPlanet, String pProduction ) {
    if (Objects.equals(pProduction, EProduceType.PR_CAP.order())) {
      pPlanet.setProduceType(EProduceType.PR_CAP, null);
    } else if (Objects.equals(pProduction,EProduceType.PR_MAT.order())) {
      pPlanet.setProduceType(EProduceType.PR_MAT, null);
    } else if (Objects.equals(pProduction,EProduceType.PR_DRIVE.order())) {
      pPlanet.setProduceType(EProduceType.PR_DRIVE, null);
    } else if (Objects.equals(pProduction,EProduceType.PR_WEAPONS.order())) {
      pPlanet.setProduceType(EProduceType.PR_WEAPONS, null);
    } else if (Objects.equals(pProduction,EProduceType.PR_SHIELDS.order())) {
      pPlanet.setProduceType(EProduceType.PR_SHIELDS, null);
    } else if (Objects.equals(pProduction,EProduceType.PR_CARGO.order())) {
      pPlanet.setProduceType(EProduceType.PR_CARGO, null);
    } else {
      pPlanet.setProduceType(EProduceType.PR_SHIP,pProduction);
    }
    Global.GAMECONTEXT.currentGameChanged().galaxy().map().planets()
      .findPlanetById(pPlanet.id())
      .setProduceType(pPlanet.produceType(),pPlanet.produceUnitDesign());
    Global.GAMECONTEXT.currentFactionChanged().newChange();
    return;
  }

  static public void addUnitDesign(IJG_UnitDesign pUnitDesign) {
    Global.GAMECONTEXT.currentFactionChanged().addUnitDesign(pUnitDesign);
    Global.GAMECONTEXT.currentFactionChanged().newChange();
    return;
  }

  static public void sendGroup(IJG_Group pGroup, IJG_Planet pPlanet ) {
    pGroup.setTo(pPlanet.id());
    pGroup.toPosition().copyOf(pPlanet.position());
    return;
  }

  static public double unloadOrder(IJG_Game pGame, IJG_Group pGroup, IJG_Planet pPlanet, double pAmountToUnLoad) {
    double unloaded = 0.0;
    String type = pGroup.loadType();
    double unload = Math.min(pGroup.load(),pAmountToUnLoad);
    var gamePlanet = pGame.galaxy().map().planets().findPlanetById(pPlanet.id());
    if ("COL".equals(type)) {
      pPlanet.setCols(pPlanet.cols() + unload);
      unloaded = unload;
      if (pPlanet.faction()==null) {
        pPlanet.setFaction(pGroup.faction());
      }
      gamePlanet.copyFrom(pPlanet);
      pGroup.setLoad(pGroup.load() - unload);
      if (pGroup.load() <= 0.0) {
        pGroup.setLoadType(null);
      }
    } else if ("CAP".equals(type)) {
      pPlanet.setCapitals(pPlanet.capitals() + unload);
      unloaded = unload;
      gamePlanet.copyFrom(pPlanet);
      pGroup.setLoad(pGroup.load() - unload);
      if (pGroup.load()<=0.0) {
        pGroup.setLoadType(null);
      }
    } else if ("MAT".equals(type)) {
      pPlanet.setMaterials(pPlanet.materials() + unload);
      unloaded = unload;
      gamePlanet.copyFrom(pPlanet);
      pGroup.setLoad(pGroup.load() - unload);
      if (pGroup.load()<=0.0) {
        pGroup.setLoadType(null);
      }
    }
    return unloaded;
  }

}
