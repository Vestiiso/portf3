import java.sql.*;
import java.util.ArrayList;

import static java.sql.DriverManager.getConnection;

public class JDBCmodel {
    //Den følgende kode er lavet ud fra vejledningen i videoerne:
    //   "JDBC 3 (SQL Statement)"
    // + "JDBC 5 (PreparedStatement)"
    // + "JDBC 4 (SQL Injection)" fra moodle.
    Connection conn = null;
    String url;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    // url til vores database: "jdbc:sqlite:E:/dokumenter/RUC/4. semester/Software development/portfolio3/port3db.db"
    //opsætning og basig funktioner
    JDBCmodel (String url) {
        this.url = url;
    }
    public void connect() throws SQLException {
            conn = getConnection(url);
    }
    public void close() throws SQLException {
            if (conn != null) { //tjek om der er en connection, inden du prøver at lukke den
                conn.close();
            }
    }

    // -------------------- queries ------------------------------

    public void CreateStatement() throws SQLException {
        this.stmt = conn.createStatement();
    }

    //få en liste af undervisernes navne
    public ArrayList<String> SQLQueryUndervisereNavne () {
        ArrayList<String> navne = new ArrayList<>();
        String sql = "SELECT navn FROM Undervisere;";
        try {
            rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) { //tjekker om der er noget i rs, og går videre til næste item i rs settet
                String navn = rs.getString(1);
                navne.add(navn);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        rs = null;
        return navne;
    }

    //få en liste af lokaler
    public ArrayList<String> SQLQueryLokaler () {
        ArrayList<String> navne = new ArrayList<>();
        String sql = "SELECT lokale_navn FROM Lokaler;";
        try {
            rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) { //tjekker om der er noget i rs, og går videre til næste item i rs settet
                String navn = rs.getString(1);
                navne.add(navn);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        rs = null;
        return navne;
    }

    //få en liste af Timeslots
    public ArrayList<String> SQLQueryTimeslots () {
        ArrayList<String> navne = new ArrayList<>();
        String sql = "SELECT tid FROM Timeslots;";
        try {
            rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) { //tjekker om der er noget i rs, og går videre til næste item i rs settet
                String navn = rs.getString(1);
                navne.add(navn);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        rs = null;
        return navne;
    }

    //få en liste af Kursernes navne
    public ArrayList<String> SQLQueryKursusNavne () {
        ArrayList<String> navne = new ArrayList<>();
        String sql = "SELECT kurs_navn FROM Kurser;";
        try {
            rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) { //tjekker om der er noget i rs, og går videre til næste item i rs settet
                String navn = rs.getString(1);
                navne.add(navn);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        rs = null;
        return navne;
    }

    //få kapaciteten for et givet kursus
    public int kursKapacitet () {
        String kursus = "Sådan træner du din drage"; //det ønskede kursus skal indsættes som en string her
        String sql = "SELECT kurs_kapacitet FROM Kurser WHERE kurs_navn = '"+kursus+"';";
        int subject = 0;

        try {
            rs = stmt.executeQuery(sql);
            subject = rs.getInt("kurs_kapacitet");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        rs = null;

        return subject;
    }

    //Join query, giver bookinger + lokale_kapacitet, hvor de har navnet til fælles
    //den her metode kan VIRKELIG godt optimeres, den måde vi viser informationerne på er langt fra optimal
    public ArrayList<ArrayList<String>> SQLQUERYinfoForKursus (String kursusTilInfo) {
        ArrayList<String> info1 = new ArrayList<>();
        ArrayList<String> info2 = new ArrayList<>();
        ArrayList<String> info3 = new ArrayList<>();
        ArrayList<String> info4 = new ArrayList<>();
        ArrayList<String> info5 = new ArrayList<>();
        ArrayList<String> info6 = new ArrayList<>();
        ArrayList<ArrayList<String>> alInfo = new ArrayList<>();


        String sql = "SELECT timeslot_tid, lokale_id, underviser_navn, kurs_navn, " +
                "underviserAnden_navn, lokale_kapacitet FROM Bookinger INNER JOIN " +
                "Lokaler ON Lokaler.lokale_navn = Bookinger.lokale_id WHERE kurs_navn = '" + kursusTilInfo + "';";

        try {
            rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) { //tjekker om der er noget i rs, og går videre til næste item i rs settet
                //String navn = rs.getString(1);
                info1.add(rs.getString(1));
                info2.add(rs.getString(2));
                info3.add(rs.getString(3));
                info4.add(rs.getString(4));
                info5.add(rs.getString(5));
                info6.add(rs.getString(6));

                alInfo.add(info1);
                alInfo.add(info2);
                alInfo.add(info3);
                alInfo.add(info4);
                alInfo.add(info5);
                alInfo.add(info6);

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        rs = null;
        System.out.println(alInfo.toString());
        return alInfo;
    }

    //Indsæt en booking i vores Bookinger table
    public void SQLQUERYLavEnBooking(String timeslot_tid, String lokale_id, String underviser_navn,
                                     String kurs_navn, String underviserAnden_navn) {


        String sql = "INSERT INTO Bookinger (timeslot_tid, lokale_id, underviser_navn, kurs_navn, underviserAnden_navn)" +
        "VALUES ('"+ timeslot_tid +"', '"+lokale_id+"', '" +underviser_navn+"', '"+kurs_navn+"', '"+underviserAnden_navn+"');";

        try {
            stmt.executeUpdate(sql); //ligesom executes, men bare til når man sætter noget ind frem for at trække ud

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    public ArrayList<Integer> UnderviserBookinger(String timeslot_tid, String underviser_navn, String underviserAnden_navn) { //ikke en prepaired statement
        //Hvis der allerede eksisterer en booking med det givne underviser i det givne tidsrum, vil denne metode svare "1"
        //hvis der ikke er en booking, vil den svare 0.

        String sql = "SELECT COUNT (timeslot_tid) FROM Bookinger " +
                "WHERE timeslot_tid = '"+timeslot_tid+"' AND underviser_navn = '"+underviser_navn+"' " +
                "OR underviserAnden_navn = '"+underviserAnden_navn+"';";
        ArrayList<Integer> ArrayTilReturn = new ArrayList<>();
        try {
            rs = stmt.executeQuery(sql);
            if (rs == null)
                System.out.println("UnderviserBookinger metoden: fejl. kunne ikke fetche noget.");
            while (rs != null && rs.next()) {

                ArrayTilReturn.add(rs.getInt(1));

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return ArrayTilReturn;
    }

    public ArrayList<Integer> SQLQueryLokaleAdvarsel(String lokale_id) { //ikke en prepaired statement

        String sql = "SELECT lokale_kapacitet FROM Lokaler WHERE lokale_navn = '"+lokale_id+"';";

        ArrayList<Integer> ArrayTilReturn = new ArrayList<>();
        try {
            rs = stmt.executeQuery(sql);
            if (rs == null)
                System.out.println("SQLQueryLokaleAdvarsel metoden: fejl. kunne ikke fetche noget.");
            while (rs != null && rs.next()) {
                ArrayTilReturn.add(rs.getInt(1));

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return ArrayTilReturn;
    }

    public ArrayList<Integer> SQLQueryKursusAdvarsel(String kurs_navn) { //ikke en prepaired statement

        String sql = "SELECT kurs_kapacitet FROM Kurser WHERE kurs_navn = '"+kurs_navn+"';";

        ArrayList<Integer> ArrayTilReturn = new ArrayList<>();
        try {
            rs = stmt.executeQuery(sql);
            if (rs == null)
                System.out.println("SQLQueryKursusAdvarsel metoden: fejl. kunne ikke fetche noget.");
            while (rs != null && rs.next()) {
                ArrayTilReturn.add(rs.getInt(1));

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return ArrayTilReturn;
    }


    //printer resultater fra queries der giver arraylists
    public void PrintArrayResultat(ArrayList<String> subjects) {
        for(int i = 0; i < subjects.size(); i++) {
            System.out.println(subjects.get(i));
        }
    }

    //printer resultater fra queries der giver ints
    public void PrintTal (int subject) {
        System.out.println(subject);
    }

    // ---------------------- Herunder ser vi en version af LokaleBookingerInt() lavet som en prepaired statement

    public void Prepairedstmtlokalebookinger () {
        String sql = "SELECT COUNT(timeslot_tid) FROM Bookinger WHERE timeslot_tid = ? AND lokale_id = ?;";
        try {
            pstmt = conn.prepareStatement(sql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Bookinglokaler> LokaleBookingerKopi(String timeslot_tid, String lokale_id) {
        //arraylisten skal indeholde den class du også har lavet i skabelonen
        ArrayList<Bookinglokaler> bookinglokalers = new ArrayList<>();
        String sql = "SELECT COUNT(timeslot_tid) FROM Bookinger WHERE timeslot_tid = '?' AND lokale_id = '?';";
        try {

            pstmt.setString(1, timeslot_tid);
            pstmt.setString(2, lokale_id);
            rs = pstmt.executeQuery();

            if (rs == null)
                System.out.println("lokalebookinger metoden: fejl. kunne ikke fetche noget.");
            while (rs != null && rs.next()) {
                bookinglokalers.add(new Bookinglokaler(rs.getInt(1)));

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return bookinglokalers;
    }


}

class Bookinglokaler {
    // fra videoen: https://www.youtube.com/watch?v=9teEzuzaJjE
    int antalBookinger;

    public Bookinglokaler(int antalBookinger) {
        this.antalBookinger = antalBookinger;
    }

}



