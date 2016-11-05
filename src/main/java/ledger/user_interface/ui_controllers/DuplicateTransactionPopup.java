package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 11/2/2016.
 */
public class DuplicateTransactionPopup extends GridPane implements Initializable, IUIController {

    @FXML
    private Button importButton;
    @FXML
    private Button skipButton;
    @FXML
    private Text transactionText;

    private static final String pageLoc = "/fxml_files/DuplicateTransactionPopup.fxml";

    private List<Transaction> trans;
    private Iterator<Transaction> iter;
    private Transaction currentTrans;


    public DuplicateTransactionPopup() {
    }

    public DuplicateTransactionPopup(List<Transaction> trans) {
        this.trans = trans;
        this.initController(pageLoc, this, "Unable to load Duplicate Transaction Popup");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.iter = this.trans.iterator();
        this.skipButton.setOnAction(this::next);
        this.importButton.setOnAction(this::importTrans);
        next(null);
    }

    private void importTrans(ActionEvent actionEvent) {
        TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(currentTrans);

        task.RegisterSuccessEvent(() -> this.next(null));
        task.RegisterFailureEvent((e) -> this.next(null));
        task.startTask();
    }

    private void next(ActionEvent actionEvent) {
        if (iter.hasNext()) {
            currentTrans = iter.next();
            transactionText.setText(currentTrans.toString());
        } else {
            closeWindow();
        }
    }


    private void closeWindow() {
        ((Stage) this.getScene().getWindow()).close();
    }
}
