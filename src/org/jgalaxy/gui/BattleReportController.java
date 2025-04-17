package org.jgalaxy.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.javelinfx.buttons.SButtons;
import org.javelinfx.engine.JUnitPanelInterface;
import org.jgalaxy.battle.IB_Shot;
import org.jgalaxy.battle.ISB_BattleReport;
import org.jgalaxy.engine.IJG_Faction;
import org.jgalaxy.planets.IJG_Planet;
import org.jgalaxy.units.IJG_Fleet;
import org.jgalaxy.units.IJG_Group;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class BattleReportController extends JUnitPanelInterface implements Initializable {

  static public class GroupLeftEntry {
    public IJG_Group group;
    public String groupname;
    public int start;
    public int left;

    public String getGroupName() { return groupname; }
    public int getStart() { return start; }
    public int getLeft() { return left; }
  }

  static public class ShotDetail {

    private IJG_Group group;
    private IB_Shot shot;

    protected ShotDetail( IJG_Group pGroup, IB_Shot pShot ) {
      group = pGroup;
      shot = pShot;
      return;
    }

    public IB_Shot.TYPE getType() { return shot.type(); }
    public IB_Shot.RESULT getResult() { return shot.result(); }
    public int getRound() { return shot.round(); }
    public String getSourceID() { return group.id(); }
    public String getSourceFaction() { return group.faction(); }
    public String getTargetID() { return shot.targetID(); }
    public String getTargetFaction() { return shot.targetFaction(); }
    public int getHits() { return shot.hits(); }

  }

  @FXML private AnchorPane  mRootPane;
  @FXML private AnchorPane  mGeneralPane;

  @FXML private Label mBattleTitle;

  @FXML private TableView<GroupLeftEntry>  mOwnResultTableView;
  @FXML private TableColumn<GroupLeftEntry,String> mGroupNameColumn;
  @FXML private TableColumn<GroupLeftEntry,Integer> mStartedColumn;
  @FXML private TableColumn<GroupLeftEntry,Integer> mLeftColumn;

  @FXML private TableView<GroupLeftEntry>  mOthersResultTableView;
  @FXML private TableColumn<GroupLeftEntry,String> mOtherGroupNameColumn;
  @FXML private TableColumn<GroupLeftEntry,Integer> mOtherStartedColumn;
  @FXML private TableColumn<GroupLeftEntry,Integer> mOtherLeftColumn;

  @FXML private TableView<ShotDetail>  mBattleDetail;
  @FXML private TableColumn<ShotDetail,Integer> mBattleDetailRoundColumn;
  @FXML private TableColumn<ShotDetail,IB_Shot.TYPE> mBattleDetailTypeColumn;
  @FXML private TableColumn<ShotDetail,IB_Shot.RESULT> mBattleDetailResultColumn;
  @FXML private TableColumn<ShotDetail,String> mBattleDetailSourceIDColumn;
  @FXML private TableColumn<ShotDetail,String> mBattleDetailTargetIDColumn;
  @FXML private TableColumn<ShotDetail,String> mBattleDetailTargetFactionColumn;
  @FXML private TableColumn<ShotDetail,Integer> mBattleDetailHitsColumn;

//  IB_Shot.TYPE type();
//  IB_Shot.RESULT result();
//  int round();
//  String targetID();
//  String targetFaction();
//  int hits();


  private IJG_Faction       mFaction;
  private ISB_BattleReport  mBattleReport;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mGroupNameColumn.setCellValueFactory( new PropertyValueFactory<>("groupName"));
    mStartedColumn.setCellValueFactory( new PropertyValueFactory<>("start"));
    mLeftColumn.setCellValueFactory( new PropertyValueFactory<>("left"));

    mOtherGroupNameColumn.setCellValueFactory( new PropertyValueFactory<>("groupName"));
    mOtherStartedColumn.setCellValueFactory( new PropertyValueFactory<>("start"));
    mOtherLeftColumn.setCellValueFactory( new PropertyValueFactory<>("left"));

    mBattleDetailRoundColumn.setCellValueFactory( new PropertyValueFactory<>("round"));
    mBattleDetailTypeColumn.setCellValueFactory( new PropertyValueFactory<>("type"));
    mBattleDetailResultColumn.setCellValueFactory( new PropertyValueFactory<>("result"));
    mBattleDetailSourceIDColumn.setCellValueFactory( new PropertyValueFactory<>("sourceID"));
    mBattleDetailTargetIDColumn.setCellValueFactory( new PropertyValueFactory<>("targetID"));
    mBattleDetailTargetFactionColumn.setCellValueFactory(new PropertyValueFactory<>("targetFaction"));
    mBattleDetailHitsColumn.setCellValueFactory( new PropertyValueFactory<>("hits"));

    return;
  }

  @Override
  public AnchorPane rootPane() {
    return mRootPane;
  }

  /**
   * refresh
   */
  public void refresh() {

    var planet = mFaction.planets().findPlanetByPosition(mBattleReport.position());
    if (planet==null) {
      Effects.setText(mBattleTitle,"-");
    } else {
      Effects.setText(mBattleTitle, "Battle at " + planet.name());
    }

    mOwnResultTableView.getItems().clear();
    mOthersResultTableView.getItems().clear();
    for(IJG_Group group : mBattleReport.groups()) {
      if (Objects.equals(group.faction(),mFaction.id())) {
        GroupLeftEntry gle = new GroupLeftEntry();
        gle.group = group;
        gle.groupname = group.name() + " (" + group.unitDesign() + ")";
        gle.start = mBattleReport.calcNumberOfBeforeBattle(group);
        gle.left = group.getNumberOf();
        mOwnResultTableView.getItems().add(gle);
      } else {
        GroupLeftEntry gle = new GroupLeftEntry();
        gle.group = group;
        gle.groupname = group.name() + " (" + group.unitDesign() + ")";
        gle.start = mBattleReport.calcNumberOfBeforeBattle(group);
        gle.left = group.getNumberOf();
        mOthersResultTableView.getItems().add(gle);
      }
    }

    mBattleDetail.getItems().clear();
    for( var bgroup : mBattleReport.groups() ) {
      for( var shot : bgroup.shotsMutable() ) {
        mBattleDetail.getItems().add(new ShotDetail(bgroup,shot));
      }
    }

//    var alldetails = mBattleReport.groups().stream().flatMap( g -> g.shotsMutable().stream() ).toList();
//    mBattleDetail.getItems().clear();
//    for( IB_Shot shot : alldetails ) {
//      mBattleDetail.getItems().add(new ShotDetail(shot));
//    }

    return;
  }

  public void setFaction(IJG_Faction pFaction) {
    mFaction = pFaction;
    return;
  }

  public void setBattleReport(ISB_BattleReport pBattleReport) {
    mBattleReport = pBattleReport;
    refresh();
    return;
  }

}
