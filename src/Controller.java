import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {
    //Følgende kode er lavet ud fra fremgangsmåden i videoen "ControllerRejseplan" fra moodle

    JDBCmodel model;
    View view;

    public Controller(JDBCmodel model) {
        this.model = model;
        try {
            model.connect(); //caller connection gennem vores model
            model.CreateStatement();
        }
        catch (SQLException e) {
            e.printStackTrace(); //Printer vores callstack
            System.out.println(e.getMessage());
        }
    }

    //vi kan ikke klikke på vores knap inden vi har vores view, derfor:
    public void setView(View view) {
        this.view = view;
        view.exitBtn.setOnAction(e-> Platform.exit());


        //aktiverer hvad der sker når man trykker på "se info om kurset
        EventHandler<ActionEvent> PrintKursusInfo =
                e-> HandlePrintkursusBookinger(view.KurserComB.getValue(), view.infoTekstOmråde);
        view.kursusInfo.setOnAction(PrintKursusInfo);

        //aktivererr hvad der sker når du trykker på book knappen
        EventHandler<ActionEvent> LavEnBooking = e-> HandleBooking(view.infoTekstOmråde);
        view.bookBtn.setOnAction(LavEnBooking);



    }

    //------------------------- til combo boxes ------------------
    //(for at vi kan indsætte i combo boxes under view, skal vores arraylists gøres til ObservableLists)
    //Kurser
    public ObservableList<String> getKursusnavne() { //til brug i combo boxes
        ArrayList<String> navne = model.SQLQueryKursusNavne();
        ObservableList<String> navneObservable = FXCollections.observableList(navne);
        return navneObservable;
    }

    //Lokaler
    public ObservableList<String> getLokaler() { //til brug i combo boxes
        ArrayList<String> navne = model.SQLQueryLokaler();
        ObservableList<String> navneObservable = FXCollections.observableList(navne);
        return navneObservable;
    }

    //Timeslots
    public ObservableList<String> getTimeslots() { //til brug i combo boxes
        ArrayList<String> navne = model.SQLQueryTimeslots();
        ObservableList<String> navneObservable = FXCollections.observableList(navne);
        return navneObservable;
    }

    //undervisere
    public ObservableList<String> getUnderviserNavne() { //til brug i combo boxes
        ArrayList<String> navne = model.SQLQueryUndervisereNavne();
        ObservableList<String> navneObservable = FXCollections.observableList(navne);
        return navneObservable;
    }


    public void HandlePrintkursusBookinger (String kursus, TextArea TxtArea){
        TxtArea.clear();
        if (model.SQLQUERYinfoForKursus(kursus).isEmpty()) {
            TxtArea.appendText("Der er ingen bookinger for kurset i øjeblikket.");
        }
        else{
            TxtArea.appendText(model.SQLQUERYinfoForKursus(kursus).toString());
        }

    }

    public void HandleBooking(TextArea TxtArea){
        // HUSK AT MAN KAN BOOKE FLERE LOKALER, FORDI MAN IKKE BEHØVER VÆLGE ANDET END LOKALE + TIDSSLOT FOR AT BOOKE
        //tag data fra UI og kør dem ind i LavEnBooking()
        model.Prepairedstmtlokalebookinger();
        int antalLokaleBookinger = model.LokaleBookingerKopi(view.TidComB.getValue(), view.LokalerComB.getValue()).get(0).antalBookinger;
        int antalUnderviserBookinger = model.UnderviserBookinger(view.TidComB.getValue(), view.UndervisereComB.getValue(), view.Undervisere2ComB.getValue()).get(0);
        int kursusKapacitet = model.SQLQueryKursusAdvarsel(view.KurserComB.getValue()).get(0);
        int lokaleKapacitet = model.SQLQueryLokaleAdvarsel(view.LokalerComB.getValue()).get(0);

        if (kursusKapacitet>lokaleKapacitet) {
            System.out.println("ADVARSEL: antallet af kursusdeltagere overstiger lokalekapaciteten");
            //EventHandler<ActionEvent> LavEnBooking = e-> HandleBooking(view.infoTekstOmråde);
            //view.bookBtn.setOnAction(LavEnBooking);
            //PopUpHandler();

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ADVARSEL");
                alert.setHeaderText("ADVARSEL: antallet af kursusdeltagere overstiger lokalekapaciteten");
                alert.setContentText("Det frarådes at afholde dette kursus i det valgte lokale, \n" +
                        "grundet det forventede fremmøde fra kurset overstiger lokalets \n" +
                        "anbefalede kapacitet.");

                Optional<ButtonType> result = alert.showAndWait(); //returner hvilken knap brugern trykker på
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    System.out.println("OK Button Clicked");

                }
        }

        if (antalLokaleBookinger>0) {
            //Skriv "lokalet er optaget i denne tidsperiode"

            TxtArea.clear();
            TxtArea.appendText("Lokalet er allerede booket i denne tidsperiode. \nVælg venligst en anden tidsperiode");
        }
        if (antalUnderviserBookinger>0){
            //skriv "underviseren er booket i denne tidsperiode
            TxtArea.clear();
            TxtArea.appendText("Underviseren er allerede booket i denne tidsperiode. \nVælg venligst en anden tidsperiode");
            System.out.println(model.UnderviserBookinger(view.TidComB.getValue(), view.UndervisereComB.getValue(),
                    view.Undervisere2ComB.getValue()).get(0)>0);
        }
        else if (antalLokaleBookinger < 1 && antalUnderviserBookinger < 1){
           model.SQLQUERYLavEnBooking(view.TidComB.getValue(), view.LokalerComB.getValue(), view.UndervisereComB.getValue(),
                    view.KurserComB.getValue(), view.Undervisere2ComB.getValue());

            TxtArea.clear();
            TxtArea.appendText("Booking gennemført!");
            System.out.println("booking gennemført");
        }



    }

    //Koden til at lave en pop-up box er lavet ud fra fremgangsmåden i følgende video: https://www.youtube.com/watch?v=KzxE3ZcSIvQ
    /*public void PopUpHandler() {
        view.bookBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("ADVARSEL");
                alert.setHeaderText("ADVARSEL: antallet af kursusdeltagere overstiger lokalekapaciteten");
                alert.setContentText("Det frarådes at afholde dette kursus i det valgte lokale, \n" +
                        "grundet det forventede fremmøde fra kurset overstiger lokalets \n" +
                        "anbefalede kapacitet.");

                Optional<ButtonType> result = alert.showAndWait(); //returner hvilken knap brugern trykker på
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    System.out.println("OK Button Clicked");
                }

            }
        });
    }


    /*Skal muligvis bruges senere
    public void HandlePrintkursusBookinger (String kursus, TextArea TxtArea){
        TxtArea.clear();
        TxtArea.appendText("TEXT HER");

    }

     */


}
