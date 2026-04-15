package com.example.collegesportsmanagementdbs;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class HelloApplication extends Application {

    // ---------------- DB CONNECTION ----------------
    static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "username",
                "password"
        );
    }

    // ---------------- ADMIN AUTH ----------------
    boolean authenticateAdmin(String u, String p) {
        return u.equals("admin") && p.equals("admin123");
    }

    @Override
    public void start(Stage stage) {

        TabPane tabPane = new TabPane();

        Tab studentTab = new Tab("Student Panel");
        studentTab.setClosable(false);
        studentTab.setContent(studentPanel());

        Tab adminTab = new Tab("Admin Panel");
        adminTab.setClosable(false);
        adminTab.setContent(adminLogin());

        tabPane.getTabs().addAll(studentTab, adminTab);

        Scene scene = new Scene(tabPane, 800, 500);
        stage.setTitle("College Sports Management System");
        stage.setScene(scene);
        stage.show();
    }

    // ---------------- STUDENT PANEL ----------------
    VBox studentPanel() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefHeight(300);

        Button b1 = new Button("View Students");
        Button b2 = new Button("Register Student");
        Button b3 = new Button("View Results");
        Button b4 = new Button("View Sports");
        Button b5 = new Button("View Student Participation");
        Button b6 = new Button("View Winners");
        Button b7 = new Button("Count Participants");

        // ---------- View Students ----------
        b1.setOnAction(e -> {
            try (Connection con = StudentService.getConnection()) {
                ResultSet rs = StudentService.viewStudents(con);
                output.clear();
                while (rs.next()) {
                    output.appendText(
                            rs.getInt(1) + "  " +
                                    rs.getString(2) + "  " +
                                    rs.getString(3) + "  Year:" +
                                    rs.getInt(4) + "\n"
                    );
                }
            } catch (Exception ex) {
                output.setText(ex.getMessage());
            }
        });

        // ---------- Register Student ----------
        b2.setOnAction(e -> {
            Dialog<Void> d = new Dialog<>();
            d.setTitle("Register Student");

            TextField reg = new TextField();
            reg.setPromptText("Reg ID");

            TextField sid = new TextField();
            sid.setPromptText("Student ID");

            TextField sport = new TextField();
            sport.setPromptText("Sport ID");

            TextField event = new TextField();
            event.setPromptText("Event (optional)");

            VBox box = new VBox(8, reg, sid, sport, event);
            d.getDialogPane().setContent(box);
            d.getDialogPane().getButtonTypes().add(ButtonType.OK);

            d.setResultConverter(bt -> {
                try {
                    StudentService.registerStudent(
                            Integer.parseInt(reg.getText()),
                            Integer.parseInt(sid.getText()),
                            Integer.parseInt(sport.getText()),
                            event.getText()
                    );
                    output.setText("Registration Successful");
                } catch (Exception ex) {
                    output.setText(ex.getMessage());
                }
                return null;
            });

            d.showAndWait();
        });

        // ---------- View Results ----------
        b3.setOnAction(e -> {
            try (Connection con = StudentService.getConnection()) {
                ResultSet rs = StudentService.viewResults(con);
                output.clear();
                while (rs.next()) {
                    output.appendText(
                            rs.getString(1) + " | " +
                                    rs.getString(2) + " | Pos " +
                                    rs.getInt(3) + " | " +
                                    rs.getString(4) + "\n"
                    );
                }
            } catch (Exception ex) {
                output.setText(ex.getMessage());
            }
        });

        // ---------- View Sports ----------
        b4.setOnAction(e -> {
            try (Connection con = StudentService.getConnection()) {
                ResultSet rs = StudentService.viewSports(con);
                output.clear();
                while (rs.next()) {
                    output.appendText(
                            rs.getInt(1) + "  " +
                                    rs.getString(2) + " (" +
                                    rs.getString(3) + ")\n"
                    );
                }
            } catch (Exception ex) {
                output.setText(ex.getMessage());
            }
        });

        // ---------- Student Participation ----------
        b5.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("Enter Student ID");
            d.showAndWait().ifPresent(val -> {
                try (Connection con = StudentService.getConnection()) {
                    ResultSet rs = StudentService.viewStudentParticipation(con, Integer.parseInt(val));
                    output.clear();
                    while (rs.next()) {
                        output.appendText(
                                rs.getString(1) + " - " +
                                        rs.getString(2) + "\n"
                        );
                    }
                } catch (Exception ex) {
                    output.setText(ex.getMessage());
                }
            });
        });

        // ---------- Winners ----------
        b6.setOnAction(e -> {
            try (Connection con = StudentService.getConnection()) {
                ResultSet rs = StudentService.viewWinners(con);
                output.clear();
                while (rs.next()) {
                    output.appendText(
                            rs.getString(1) + " Winner: " +
                                    rs.getString(2) + "\n"
                    );
                }
            } catch (Exception ex) {
                output.setText(ex.getMessage());
            }
        });

        // ---------- Count Participants ----------
        b7.setOnAction(e -> {
            try (Connection con = StudentService.getConnection()) {
                ResultSet rs = StudentService.countParticipants(con);
                output.clear();
                while (rs.next()) {
                    output.appendText(
                            rs.getString(1) + " : " +
                                    rs.getInt(2) + "\n"
                    );
                }
            } catch (Exception ex) {
                output.setText(ex.getMessage());
            }
        });

        FlowPane buttons = new FlowPane(10, 10,
                b1, b2, b3, b4, b5, b6, b7);

        root.getChildren().addAll(
                new Label("Student Panel"),
                buttons,
                output
        );

        return root;
    }

    // ---------------- ADMIN LOGIN ----------------
    VBox adminLogin() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        TextField user = new TextField();
        user.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        Button login = new Button("Login");
        Label msg = new Label();

        login.setOnAction(e -> {
            if (authenticateAdmin(user.getText(), pass.getText())) {
                box.getChildren().clear();
                ScrollPane sp = new ScrollPane(adminPanel());
                sp.setFitToWidth(true);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                box.getChildren().add(sp);
            } else {
                msg.setText("Invalid Admin Credentials");
            }
        });

        box.getChildren().addAll(
                new Label("Admin Login"),
                user, pass, login, msg
        );
        return box;
    }

    // ---------------- ADMIN PANEL ----------------
    VBox adminPanel() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(750);

        /* ================= ADD SPORT ================= */
        Label addSportLabel = new Label("Add Sport");

        TextField sportId = new TextField();
        sportId.setPromptText("Sport ID");

        TextField sportName = new TextField();
        sportName.setPromptText("Sport Name");

        TextField sportType = new TextField();
        sportType.setPromptText("TEAM / INDIVIDUAL");

        Button addSport = new Button("Add Sport");
        addSport.setOnAction(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps =
                        con.prepareStatement("INSERT INTO sp_sport VALUES (?,?,?)");
                ps.setInt(1, Integer.parseInt(sportId.getText()));
                ps.setString(2, sportName.getText());
                ps.setString(3, sportType.getText());
                ps.executeUpdate();
                alert("Sport added successfully");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });

        /* ================= ADD COACH ================= */
        Label addCoachLabel = new Label("Add Coach");

        TextField coachId = new TextField();
        coachId.setPromptText("Coach ID");

        TextField coachName = new TextField();
        coachName.setPromptText("Coach Name");

        TextField coachSport = new TextField();
        coachSport.setPromptText("Sport ID");

        Button addCoach = new Button("Add Coach");
        addCoach.setOnAction(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps =
                        con.prepareStatement("INSERT INTO sp_coach VALUES (?,?,?)");
                ps.setInt(1, Integer.parseInt(coachId.getText()));
                ps.setString(2, coachName.getText());
                ps.setInt(3, Integer.parseInt(coachSport.getText()));
                ps.executeUpdate();
                alert("Coach added successfully");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });

        /* ================= SCHEDULE MATCH ================= */
        Label scheduleMatchLabel = new Label("Schedule Match");

        TextField matchId = new TextField();
        matchId.setPromptText("Match ID");

        TextField matchSport = new TextField();
        matchSport.setPromptText("Sport ID");

        TextField team1 = new TextField();
        team1.setPromptText("Team 1 ID");

        TextField team2 = new TextField();
        team2.setPromptText("Team 2 ID");

        TextField venue = new TextField();
        venue.setPromptText("Venue");

        Button addMatch = new Button("Schedule Match");
        addMatch.setOnAction(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps =
                        con.prepareStatement(
                                "INSERT INTO sp_match VALUES (?,?,?,?,SYSDATE,?)");
                ps.setInt(1, Integer.parseInt(matchId.getText()));
                ps.setInt(2, Integer.parseInt(matchSport.getText()));
                ps.setInt(3, Integer.parseInt(team1.getText()));
                ps.setInt(4, Integer.parseInt(team2.getText()));
                ps.setString(5, venue.getText());
                ps.executeUpdate();
                alert("Match scheduled");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });

        /* ================= UPDATE RESULT ================= */
        Label updateResultLabel = new Label("Update Result");

        TextField resultId = new TextField();
        resultId.setPromptText("Result ID");

        TextField eventId = new TextField();
        eventId.setPromptText("Event ID");

        TextField participantId = new TextField();
        participantId.setPromptText("Participant ID");

        TextField position = new TextField();
        position.setPromptText("Position");

        TextField performance = new TextField();
        performance.setPromptText("Performance");

        Button addResult = new Button("Update Result");
        addResult.setOnAction(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps =
                        con.prepareStatement(
                                "INSERT INTO sp_result VALUES (?,?,?,?,?)");
                ps.setInt(1, Integer.parseInt(resultId.getText()));
                ps.setInt(2, Integer.parseInt(eventId.getText()));
                ps.setInt(3, Integer.parseInt(participantId.getText()));
                ps.setInt(4, Integer.parseInt(position.getText()));
                ps.setString(5, performance.getText());
                ps.executeUpdate();
                alert("Result updated");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });

        /* ================= ADD EVERYTHING ================= */
        root.getChildren().addAll(
                addSportLabel, sportId, sportName, sportType, addSport,
                new Separator(),
                addCoachLabel, coachId, coachName, coachSport, addCoach,
                new Separator(),
                scheduleMatchLabel, matchId, matchSport, team1, team2, venue, addMatch,
                new Separator(),
                updateResultLabel, resultId, eventId, participantId, position, performance, addResult
        );

        return root;
    }

    void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
