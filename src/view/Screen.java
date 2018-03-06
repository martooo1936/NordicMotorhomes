package view;

import controller.LoginController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ExtrasLineItem;
import model.Reservation;

import java.io.IOException;
import java.util.Collection;

public class Screen
{
    public void change(ActionEvent actionEvent, String fxml) throws IOException
    {
        Stage stage = (Stage)(((Node) actionEvent.getSource()).getScene().getWindow());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml)), 1200, 900));
    }

    // Rasmus
    public void changeToCustInfo(ActionEvent actionEvent,
                                 Reservation reservation,
                                 Collection<ExtrasLineItem> lineItems)
    {
        Stage stage = (Stage)(((Node) actionEvent.getSource()).getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("customerdetails.fxml"));

        Parent root = null;

        try
        {
            root = (Parent)fxmlLoader.load();
        } catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        CustomerDetailsView view = fxmlLoader.<CustomerDetailsView>getController();

        view.setResAndItems(reservation, lineItems);

        stage.setScene(new Scene(root));
    }

    // Rasmus
    public void changeToNewRes(ActionEvent actionEvent,
                                 Reservation reservation,
                                 Collection<ExtrasLineItem> lineItems)
    {
        Stage stage = (Stage)(((Node) actionEvent.getSource()).getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createRes.fxml"));

        Parent root = null;

        try
        {
            root = (Parent)fxmlLoader.load();

        } catch (IOException e)
        {
            e.printStackTrace();

            return;
        }

        CreateResView view = fxmlLoader.<CreateResView>getController();

        view.updateFields(reservation, lineItems);

        stage.setScene(new Scene(root, 1200, 900));
    }

    public void changeOnMouse(MouseEvent mouseEvent, String fxml) throws  IOException
    {
        Stage stage = (Stage)(((Node) mouseEvent.getSource()).getScene().getWindow());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
    }

    public void exitOrLogOut(MouseEvent mouseEvent, ChoiceBox exitOptions)
    {
        LoginController.setPersonId(-1);

        exitOptions.getSelectionModel().selectedItemProperty().addListener((v,oldValue, newValue) -> {
            if(exitOptions.getSelectionModel().getSelectedItem().equals("Exit")){
                System.exit(0);
            }

            if(exitOptions.getSelectionModel().getSelectedItem().equals("Log out")){
                Stage stage = (Stage)(((Node) mouseEvent.getSource()).getScene().getWindow());
                try {
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("login.fxml")),1200, 900));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void changeOnKeyEvent(KeyEvent keyEvent, String fxml) throws IOException
    {
        Stage stage = (Stage)(((Node) keyEvent.getSource()).getScene().getWindow());
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
    }

    public Boolean confirm(String titel, String message)
    {
        final Boolean[] sure = {false};

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(titel);
        window.setMinWidth(500);
        window.setMinHeight(150);

        Label label = new Label(message);
        Button yes = new Button("Yes");
        Button no = new Button("No");
        yes.setOnAction(e-> {
            window.close();
            sure[0] = true;
        });

        no.setOnAction(e-> window.close());

        HBox layout = new HBox(20);
        layout.getChildren().addAll(label, yes, no);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return sure[0];


    }

    public void warning(String titel, String message)
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(titel);
        window.setMinWidth(500);
        window.setMinHeight(150);

        Label label = new Label(message);
        Button yes = new Button("Ok");
        yes.setOnAction(e-> {
            window.close();
        });

        HBox layout = new HBox(20);
        layout.getChildren().addAll(label, yes);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }


    public static void restrictIntInput(TextField textField)
    {
        textField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.matches("\\d*"))
            {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }


    public static void restrictNumberInput(TextField textField)
    {
        textField.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+"))
                {
                    textField.setText(oldValue);
                }
            }
        });
    }
}
