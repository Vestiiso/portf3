import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.sql.DriverManager.getConnection;

public class Main extends Application{

    int setScenestrX = 600;
    int setScenestrY = 475;

    //denne kode er lavet ud fra fremgangsmåden i videoen "ViewTrain" fra moodle
    @Override
    public void start(Stage primarystage) throws Exception {
        String url = "jdbc:sqlite:E:/dokumenter/RUC/4. semester/Software development/portfolio3/port3db.db";
        JDBCmodel model = new JDBCmodel(url);
        Controller control = new Controller(model);
        View view = new View(control);

        control.setView(view);

        primarystage.setTitle("Portfolio 3");
        primarystage.setScene(new Scene(view.asParent(), setScenestrX, setScenestrY));
        primarystage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }










 /*   public static void main(String[] args) {
        //denne kode er delvist lavet efter fremgangsmåden
        // præsenteret i videoen "JDBC 2 (JavaDataBaseConnection)" fra moodle

        String url = "jdbc:sqlite:E:/dokumenter/RUC/4. semester/Software development/portfolio3/port3db.db";
        JDBCmodel model = new JDBCmodel(url);

        try { //foretager en masse queries her
            model.connect();
            model.CreateStatement();
            ArrayList<String> navne = model.SQLQueryUndervisereNavne();
            model.PrintArrayResultat(navne);
            model.kursKapacitet();
            model.PrintTal(model.kursKapacitet());
            model.Prepairedstmtlokalebookinger();
            model.LokaleBookingerKopi("4", "7"); //prepaired statement versionen
            System.out.println(model.LokaleBookingerInt("4", "8"));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                model.close();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }


    }

  */
}
