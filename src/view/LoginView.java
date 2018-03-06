package view;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginView implements Initializable
{
    LoginController loginController = new LoginController();

    @FXML
    public Label waitLabel;

    @FXML
    private Label redLabel, tip;

    @FXML
    private JFXTextField eMailField;

    @FXML
    private JFXPasswordField passField;

    @FXML
    private Button signInButton;

    private int attempt = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Tooltip tooltip = new Tooltip();

        tooltip.setText(
                "To login as an 'Admin' use:\" +\n" +
                        "mechanic - ras@yahoo.com  - raspass\" +\n" +
                        "assistant - nik@yahoo.com  - nikpass\" +\n" +
                        "admin - jak@yahoo.com  - jakpass\" +\n" +
                        "accountant - mar@yahoo.com  - marpass\" +\n" +
                        "cleaner - las@yahoo.com  - laspass");

        tip.setTooltip(tooltip);
        waitLabel.setVisible(false);
    }
//Nikolay
    public void checkEmailAndPass(ActionEvent actionEvent) throws IOException
    {
        redLabel.setVisible(false);

        boolean userExist = false;



        if(loginController.validateUser(eMailField.getText(), passField.getText(), actionEvent))
        {
            userExist = true;
            attempt = 1;
        }

        if (!userExist)
        {
            if (attempt > 2)
            {
                loginController.countDown(this, attempt);
            }/*
            if (attempt == 3)
            {
                loginController.countDown(this, attempt);
            }*/

            redLabel.setVisible(true);
            attempt++;
            return;
        }

        loginController.changeScreen();

    }

    // Rasmus
    public void setCountdown(
            boolean showWaitMsg, boolean showButton)
    {
        //attempt  = 3;

        if (showButton)
        {
            redLabel.setVisible(true);
        }
        else
        {
            redLabel.setVisible(false);
        }

        waitLabel.setVisible(showWaitMsg);
        signInButton.setVisible(showButton);
    }
}
