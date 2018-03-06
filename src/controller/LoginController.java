package controller;

import javafx.event.ActionEvent;
import model.Depot;
import model.Person;
import view.LoginView;
import view.Screen;

import java.io.IOException;

public class LoginController
{
    public static int personId;

    private Screen screen = new Screen();

    private Depot mhDepot = new Depot();

    public boolean validateUser(String eMail, String pass, ActionEvent event) throws IOException
    {
        Person signedInPerson = mhDepot.validateUser(eMail, pass);

        if (signedInPerson != null)
        {
            personId = signedInPerson.getId();
            switch (signedInPerson.getStatus())
            {
                case "accountant" :
                    screen.change(event, "orders.fxml");
                    return true;

                case "employee" :
                    screen.change(event, "orders.fxml");
                    return true;

                case "admin" :
                    screen.change(event, "employees.fxml");
                    return true;

                case "customer" :
                    screen.change(event, "orderedit.fxml");
                    return true;

                case "assistant" :
                    screen.change(event, "orders.fxml");
                    return true;

                case "mechanic" :
                    screen.change(event, "service.fxml");
                    return true;

                case "cleaner" :
                    screen.change(event, "service.fxml");
                    System.out.println(signedInPerson.getStatus());
                    return true;

            }
        }
        return false;
    }

    public void changeScreen()
    {

    }

    //region Login countdown (Rasmus)
    public void countDown (LoginView view, int attemptNo)
    {
        class CountDownTimer implements Runnable
        {
            private LoginView view;
            private int waitTime;

            private CountDownTimer(LoginView view, int attemptNo)
            {
                this.view = view;
                this.waitTime = (attemptNo - 2) * 5000;
            }

            @Override
            public void run()
            {

                view.setCountdown(true, false);

                try
                {
                    Thread.sleep(waitTime);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                view.setCountdown(false, true);
            }
        }

        CountDownTimer timer = new CountDownTimer(view, attemptNo);

        Thread thread = new Thread(timer);
        thread.start();
    }

    public static int getPersonId()
    {
        return personId;
    }

    public static void setPersonId(int personId)
    {
        LoginController.personId = personId;
    }

    //endregion
}