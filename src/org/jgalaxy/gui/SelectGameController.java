package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JPanelInterface;
import org.jgalaxy.engine.IJG_GameInfo;
import org.jgalaxy.engine.IJG_Games;
import org.jgalaxy.engine.IJG_Player;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectGameController extends JPanelInterface implements Initializable {

  @FXML private AnchorPane  mRootPane;

  @FXML private TableView<IJG_GameInfo> mGameList;
  @FXML private TableColumn<IJG_GameInfo,String> mGameNameColumn;
  @FXML private TableColumn<IJG_GameInfo,String> mGameTurnColumn;

  @FXML private Button mSelectButton;

  private Runnable mSelectGameRunnable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mGameNameColumn.setCellValueFactory( new PropertyValueFactory<>("name"));

    SButtons.initButton(mSelectButton, _ -> {
      var gameInfo = mGameList.getSelectionModel().getSelectedItem();
      Global.GAMECONTEXT.setGameName(gameInfo.getName());
      if (Global.GAMECONTEXT.playerName().isBlank()) {
        Global.GAMECONTEXT.setPlayerName("player0");
      }

      mSelectGameRunnable.run();
      getThisStage().close();
    });

    return;
  }

  public void setSelectGameRunnable(Runnable runnable) {
    mSelectGameRunnable = runnable;
    return;
  }

  public AnchorPane rootPane() {
    return mRootPane;
  }

  public void refresh() {
    Global.GAMECONTEXT.loadGames();
    IJG_Games games = Global.GAMECONTEXT.currentGamesProperty().get();
    for(IJG_GameInfo gameInfo : games.games()) {
      System.out.println(gameInfo);
      IJG_Player player =  gameInfo.players().stream().filter(p -> p.getUsername().equals(Global.GAMECONTEXT.userName())).findFirst().orElse(null);
      if (player!=null) {
        mGameList.getItems().add(gameInfo);
      }
    }
    return;
  }

}
