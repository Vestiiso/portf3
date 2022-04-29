import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class View {
    //denne kode er lavet ud fra fremgangsmåden i videoen "ViewTrain" fra moodle

    Controller control;

    int GridPaneMinSizeV = 500;
    int GridPaneMinSizeV1 = 500;
    int PaddingInsetsV = 10;
    int PaddingInsetsV1 = 10;
    int PaddingInsetsV2 = 10;
    int PaddingInsetsV3 = 10;
    int VgapSize = 5;
    int HgapSize = 1;

    private GridPane StartView;
    Button exitBtn = new Button("exit");
    Button bookBtn = new Button("Book");
    Button kursusInfo = new Button("Se info om kurset");
    Label tekstKurser = new Label("Vælg kursus: ");
    Label tekstLokaler = new Label("Vælg lokale: ");
    Label tekstTid = new Label("Vælg tidspunkt: ");
    Label tekstUndervisere = new Label("Vælg underviser: ");
    Label tekstUndervisere2 = new Label("Vælg evt. anden underviser: \nønskes der kun 1 underviser, \nså vælg den samme");

    TextArea infoTekstOmråde = new TextArea("Vælg et kursus for at se info om det her.");

    ComboBox<String> KurserComB = new ComboBox<>();
    ComboBox<String> LokalerComB = new ComboBox<>();
    ComboBox<String> TidComB = new ComboBox<>();
    ComboBox<String> UndervisereComB = new ComboBox<>();
    ComboBox<String> Undervisere2ComB = new ComboBox<>();

    public View(Controller control) {
        this.control = control;
        CreateAndConfigure();

    }

    private void CreateAndConfigure() {
        StartView = new GridPane();
        StartView.setMinSize(GridPaneMinSizeV, GridPaneMinSizeV1);
        StartView.setPadding(new Insets(PaddingInsetsV, PaddingInsetsV1, PaddingInsetsV2, PaddingInsetsV3));
        StartView.setVgap(VgapSize);
        StartView.setHgap(HgapSize);

        StartView.add(tekstKurser, 1, 1);
        StartView.add(KurserComB,15,1);

        StartView.add(tekstLokaler, 1, 3);
        StartView.add(LokalerComB, 15,3);

        StartView.add(tekstTid,1,5);
        StartView.add(TidComB, 15,5);

        StartView.add(tekstUndervisere,1,7);
        StartView.add(UndervisereComB, 15, 7);

        StartView.add(tekstUndervisere2,1,9);
        StartView.add(Undervisere2ComB, 15, 9);

        StartView.add(bookBtn, 15, 12);
        StartView.add(infoTekstOmråde,1,14,15,7);
        infoTekstOmråde.setEditable(false);

        StartView.add(kursusInfo, 1, 12);

        StartView.add(exitBtn, 1, 30);

        //-------------------------udfyld combo boxes-----------------------------

        //Kurser
        ObservableList<String> kurserListe = control.getKursusnavne();
        KurserComB.setItems(kurserListe);
        KurserComB.getSelectionModel().selectFirst();
        //så et af navnene står i boksen inden man klikker på den

        //Lokaler
        ObservableList<String> lokaleListe = control.getLokaler();
        LokalerComB.setItems(lokaleListe);
        LokalerComB.getSelectionModel().selectFirst();
        //så et af navnene står i boksen inden man klikker på den

        //Timeslots
        ObservableList<String> tidListe = control.getTimeslots();
        TidComB.setItems(tidListe);
        TidComB.getSelectionModel().selectFirst();
        //så et af navnene står i boksen inden man klikker på den

        //Undervisere
        ObservableList<String> underviserListe = control.getUnderviserNavne();
        UndervisereComB.setItems(underviserListe);
        UndervisereComB.getSelectionModel().selectFirst();
        //man kunne istedet også bare skrive UndervisereComB.setItems(control.getUnderviserNavne());
        //- i stedet for at oprette en ny Observable list
        //så et af navnene står i boksen inden man klikker på den

        //Undervisere2
        ObservableList<String> underviser2Liste = control.getUnderviserNavne();
        Undervisere2ComB.setItems(underviser2Liste);
        Undervisere2ComB.getSelectionModel().selectFirst();
    }

    public Parent asParent(){
        return StartView;
    }
}
