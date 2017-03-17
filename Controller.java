package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {
    @FXML
    TableView<FileTask> tableView;

    @FXML
    VBox root;
    

    private ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @FXML protected void chooseFiles(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose files to upload");
        List<File> l = fc.showOpenMultipleDialog(root.getScene().getWindow());

        if(l != null) {
            for(File f: l) {
                FileTask ft = new FileTask(f);
                tableView.getItems().add(ft);
                executor.execute(ft);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileTask, String> nameCol = new TableColumn("File");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        nameCol.setPrefWidth(150);

        TableColumn<FileTask, String> statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        statusCol.setPrefWidth(75);

        TableColumn<FileTask, Double> progressCol = new TableColumn("Progress");
        progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressCol
                .setCellFactory(ProgressBarTableCell.forTableColumn());

        tableView.getColumns().addAll(nameCol, statusCol, progressCol);
    }
}
