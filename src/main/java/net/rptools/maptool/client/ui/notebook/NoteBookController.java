package net.rptools.maptool.client.ui.notebook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.notebook.NoteBookEntry;
import net.rptools.maptool.model.notebook.tabletreemodel.NoteBookEntryTreeItem;
import net.rptools.maptool.model.notebook.tabletreemodel.NoteBookGroupTreeItem;
import net.rptools.maptool.model.notebook.tabletreemodel.NoteBookZoneTreeItem;
import net.rptools.maptool.model.notebook.tabletreemodel.TableTreeItemHolder;

public class NoteBookController {

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private BorderPane noteBookPanel;

  @FXML
  private TreeTableView<TableTreeItemHolder> noteBookTreeTableView;

  @FXML
  private TreeTableColumn<TableTreeItemHolder, String> groupColumn;


  @FXML
  private Button addNoteButton;

  @FXML
  private StackPane mainViewStackPane;

  @FXML
  private AnchorPane notePane;

  @FXML
  private WebView noteWebView;

  @FXML
  private AnchorPane editorPane;

  @FXML
  private AnchorPane detailsAnchorPane;

  @FXML
  private HBox buttonHBox;

  @FXML
  private TextField nameTextField;

  @FXML
  private ComboBox<Zone> mapComboBox;

  @FXML
  private CheckBox mapCheckBox;

  @FXML
  private TextField referenceTextField;

  @FXML
  void initialize() {
    assert noteBookPanel != null : "fx:id=\"noteBookPanel\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert noteBookTreeTableView != null : "fx:id=\"noteBookTreeTableView\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert groupColumn != null : "fx:id=\"groupColumn\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert addNoteButton != null : "fx:id=\"addNoteButton\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert mainViewStackPane != null : "fx:id=\"mainViewStackPane\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert notePane != null : "fx:id=\"notePane\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert noteWebView != null : "fx:id=\"noteWebView\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert editorPane != null : "fx:id=\"editorPane\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert detailsAnchorPane != null : "fx:id=\"detailsAnchorPane\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert mapComboBox != null : "fx:id=\"mapComboBox\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert mapCheckBox != null : "fx:id=\"mapCheckBox\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert referenceTextField != null : "fx:id=\"referenceTextField\" was not injected: check your FXML file 'NoteBook.fxml'.";
    assert buttonHBox != null : "fx:id=\"buttonHBox\" was not injected: check your FXML file 'NoteBook.fxml'.";


    groupColumn.setCellValueFactory(
        cellDataFeatures -> {
          TableTreeItemHolder holder = cellDataFeatures.getValue().getValue();
          if (holder instanceof NoteBookGroupTreeItem) {
            var group = (NoteBookGroupTreeItem) holder;
            return new SimpleStringProperty(group.getName());
          } else if (holder instanceof NoteBookZoneTreeItem) {
            var zoneHolder = (NoteBookZoneTreeItem) holder;
            Zone zone = MapTool.getCampaign().getZone(zoneHolder.getId());
            return new SimpleStringProperty(zone.getName());
          } else if (holder instanceof NoteBookEntryTreeItem) {
            var entry = (NoteBookEntryTreeItem) holder;
            return new SimpleStringProperty(entry.getEntry().getName());
          }
          return new SimpleStringProperty("");
        });


    noteBookTreeTableView.setEditable(false);
    noteBookTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    noteBookTreeTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSel, newSel) -> {
              if (newSel != null && newSel.getValue() instanceof NoteBookEntryTreeItem) {
                showEntry(((NoteBookEntryTreeItem) newSel.getValue()).getEntry());
              } else {
                showEntry(null);
              }
            });
  }

  private void showEntry(NoteBookEntry entry) {
    if (entry == null) {
      noteWebView.getEngine().loadContent("");
      nameTextField.clear();
      mapCheckBox.setSelected(false);
      referenceTextField.clear();
    } else {
      noteWebView.getEngine().loadContent(entry.getNotes());
      nameTextField.setText(entry.getName());
      Zone currentZone;
      if (entry.getZoneId().isPresent()) {
        currentZone = MapTool.getCampaign().getZone(entry.getZoneId().get());
        mapCheckBox.setSelected(true);
      } else {
        currentZone = null;
        mapCheckBox.setSelected(false);
      }
      loadMapComboBox(currentZone);
      if (entry.getReference().isPresent()) {
        referenceTextField.setText(entry.getReference().get());
      } else {
        referenceTextField.clear();
      }
    }
    noteWebView.setDisable(true);
    nameTextField.setDisable(true);
    mapComboBox.setDisable(true);
    mapCheckBox.setDisable(true);
    referenceTextField.setDisable(true);
  }

  private void loadMapComboBox(Zone defaultZone) {
    mapComboBox.getItems().clear();
    for (Zone zone : MapTool.getCampaign().getZones()) {
      mapComboBox.getItems().add(zone);
    }
    mapComboBox.setValue(defaultZone);
  }


  void setTreeRoot(TreeItem<TableTreeItemHolder> root) {
    noteBookTreeTableView.setRoot(root);
    noteBookTreeTableView.refresh();
  }

  @FXML
  void addNoteAction(ActionEvent event) {

  }
}
