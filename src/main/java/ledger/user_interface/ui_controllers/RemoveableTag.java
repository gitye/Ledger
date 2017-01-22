package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import ledger.database.entity.Tag;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/21/2017.
 */
public class RemoveableTag extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TagButton.fxml";
    private final Tag tag;

    @FXML
    public Text tagText;

    @FXML
    public Button removeButton;

    public RemoveableTag(Tag tag) {
        this.tag = tag;
        this.initController(pageLoc, this, "Unable to load Removeable Tag Button");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagText.setText(tag.toString());
        removeButton.setOnAction(this::removeTag);
    }

    private void removeTag(ActionEvent actionEvent) {

    }
}
