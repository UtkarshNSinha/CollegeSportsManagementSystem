/*Code for student panel*/
package com.example.collegesportsmanagementdbs;
import java.sql.*;

public class StudentService {
    public static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "username",
                "password"
        );
    }

    // 1. View Students (PL/SQL / SQL)
    public static ResultSet viewStudents(Connection con) throws Exception {
        Statement st = con.createStatement();
        return st.executeQuery(
                "SELECT student_id, name, department, year FROM sp_student"
        );
    }

    // 2. Register Student (PL/SQL procedure)
    public static void registerStudent(
            int regId, int studentId, int sportId, String eventName) throws Exception {

        Connection con = getConnection();
        CallableStatement cs =
                con.prepareCall("{call sp_register_student(?,?,?,?)}");

        cs.setInt(1, regId);
        cs.setInt(2, studentId);
        cs.setInt(3, sportId);

        if (eventName == null || eventName.isEmpty())
            cs.setNull(4, Types.VARCHAR);
        else
            cs.setString(4, eventName);

        cs.execute();
        con.close();
    }

    // 3. View Sports
    public static ResultSet viewSports(Connection con) throws Exception {
        Statement st = con.createStatement();
        return st.executeQuery(
                "SELECT sport_id, sport_name, sport_type FROM sp_sport"
        );
    }

    // 4. View Results
    public static ResultSet viewResults(Connection con) throws Exception {
        Statement st = con.createStatement();
        return st.executeQuery(
                """
                SELECT e.event_name, s.name, r.position, r.performance
                FROM sp_result r
                JOIN sp_event e ON r.event_id = e.event_id
                JOIN sp_participant p ON r.participant_id = p.participant_id
                JOIN sp_registration reg ON p.reg_id = reg.reg_id
                JOIN sp_student s ON reg.student_id = s.student_id
                """
        );
    }

    // 5. View Student Participation
    public static ResultSet viewStudentParticipation(Connection con, int sid) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                """
                SELECT sp.sport_name, r.event_name
                FROM sp_registration r
                JOIN sp_sport sp ON r.sport_id = sp.sport_id
                WHERE r.student_id = ?
                """
        );
        ps.setInt(1, sid);
        return ps.executeQuery();
    }

    // 6. View Winners
    public static ResultSet viewWinners(Connection con) throws Exception {
        Statement st = con.createStatement();
        return st.executeQuery(
                """
                SELECT e.event_name, s.name
                FROM sp_result r
                JOIN sp_event e ON r.event_id = e.event_id
                JOIN sp_participant p ON r.participant_id = p.participant_id
                JOIN sp_registration reg ON p.reg_id = reg.reg_id
                JOIN sp_student s ON reg.student_id = s.student_id
                WHERE r.position = 1
                """
        );
    }

    // 7. Count Participants per Sport (procedure OR query)
    public static ResultSet countParticipants(Connection con) throws Exception {
        Statement st = con.createStatement();
        return st.executeQuery(
                """
                SELECT sp.sport_name, COUNT(*) AS total
                FROM sp_registration r
                JOIN sp_sport sp ON r.sport_id = sp.sport_id
                GROUP BY sp.sport_name
                """
        );
    }
}
