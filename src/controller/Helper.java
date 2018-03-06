package controller;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Helper
{
    public static view.Screen screen = new view.Screen();

    // Rasmus
    public int intFromString(String txt)
    {
        int number;

        try
        {
            number = Integer.parseInt(txt);
        }
        catch (NumberFormatException e)
        {
            return -12345;
        }

        return number;
    }

    // Rasmus
    public double doubleFromTxt (String txt)
    {
        double number;

        try
        {
            number = Double.parseDouble(txt);
        }
        catch (NumberFormatException e)
        {
            return -12345;
        }

        return number;
    }

    // Rasmus
    public boolean hasEmptyTxt(String[] txts)
    {
        for (String txt : txts)
        {
            if (txt.isEmpty() || txt == null)
            {
                return true;
            }
        }

        return false;
    }

    // Converts LocalDate to String
    public String txtFromDate(LocalDate date)
    {
        try
        {
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(dtf);
        }
        catch (DateTimeException e)
        {
            return "00/00/0000";
        }
    }

    // Converts String to LocalDate
    public LocalDate dateFromTxt(String date)
    {
        try
        {
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(date, dtf);
        }
        catch (DateTimeParseException e)
        {
            return null;
        }
    }
    //Jakub
    public static void doubleClick(MouseEvent mouseEvent, TableView table, String name) {
        table.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2)
                {
                    try {

                        screen.changeOnMouse(mouseEvent, name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }


    public boolean getDouble(String text)
    {
        boolean bul = false;

        try
        {

            int i = Integer.parseInt(text);

            return true;

        }catch (Exception e)
        {
            try
            {

                double d = Double.parseDouble(text);

                return true;

            }catch (Exception ex)
            {

                return false;
            }

        }

    }


    //this method checks what season are we in, returns price of period
    //represents camper price rise in % converted into Double

    //we need to figure out how to check every day
    //Jakub
    public static Double seasonalPriceChange(LocalDate startDate, LocalDate endDate, Double priceOfMotorhomePerDay)
    {

        double price;
        price = priceOfMotorhomePerDay;  //just for naming purposes
        double priceTotal = 0;

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {

            Month currentMonth = date.getMonth();

            if (currentMonth.getValue() <= 3 || currentMonth.getValue() >= 11) {
                priceTotal += price;
            } else if (currentMonth.getValue() >= 3 && currentMonth.getValue() <= 6) {
                priceTotal += price*1.5;
            } else if (currentMonth.getValue() >= 6 && currentMonth.getValue() <= 11) {
                priceTotal += price*1.7;
            } else {
                System.out.println("FAILED DATE VALIDATION");
                priceTotal += price;
            }
        }

        return priceTotal;
    }
    //Martin
    public static void displayError (String title,String header,String content)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
    //Martin
    public static void dispplayConfirmation(String title,String header,String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();

    }



    // Rasmus
    public static double sumOfGUI(Control[] controls)
    {
        Helper helper = new Helper();
        double finalPrice = 0;

        for (Control control : controls)
        {
            double addAmount = 0;

            if (control instanceof Label)
            {
                Label label = (Label) control;
                addAmount = helper.doubleFromTxt(label.getText());
            }

            if (control instanceof TextField)
            {
                TextField textField = (TextField) control;
                addAmount = helper.doubleFromTxt(textField.getText());
            }

            if (addAmount != -12345)
            {
                finalPrice += addAmount;
            }
        }

        return finalPrice;
    }


}
