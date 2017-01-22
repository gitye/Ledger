package ledger.user_interface.ui_controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_models.TransactionModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/22/2017.
 */
public class PayeeTableView extends TableView implements IUIController, Initializable {
    public static final String pageLoc = "";

    @FXML
    public TableColumn<Payee,String> nameColumn;
    @FXML
    public TableColumn<Payee,String> descriptionColumn;
    @FXML
    public TableColumn<Payee,List<Tag>> tagColumn;


    public PayeeTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<Payee, String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Payee, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Payee, String> event) {
                String name = event.getNewValue();
                Payee payee = event.getRowValue();
                payee.setName(name);

                TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
                task.startTask();
                task.waitForComplete();
            }
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Payee, String>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Payee, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Payee, String> event) {
                String description = event.getNewValue();
                Payee payee = event.getRowValue();
                payee.setDescription(description);

                TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
                task.startTask();
                task.waitForComplete();
            }
        });
    }
}
