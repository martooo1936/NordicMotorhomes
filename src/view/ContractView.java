package view;

import controller.COController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContractView implements Initializable
{
    private Screen screen = new Screen();

    @FXML
    private TextArea textArea;


    public void goBack(ActionEvent event) throws IOException
    {
       screen.change(event, "rental.fxml");
    }


    public void print(ActionEvent event) throws IOException
    {
        screen.change(event, "rental.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        textArea.setText(COController.getSelectedRental().getContract());
    }
}
