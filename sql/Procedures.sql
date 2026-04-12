--PL/SQL procedures
--Procedure to register students 
CREATE OR REPLACE PROCEDURE sp_register_student (
    p_reg_id     IN NUMBER,
    p_student_id IN NUMBER,
    p_sport_id   IN NUMBER,
    p_event      IN VARCHAR2
) AS
BEGIN
    INSERT INTO sp_registration
    VALUES (p_reg_id, p_student_id, p_sport_id, p_event);

    DBMS_OUTPUT.PUT_LINE('Student registered successfully');
END;
/
BEGIN
    sp_register_student(1300, 1009, 105, 'Badminton Singles');
END;
/
/*

--Procedure to list participants
CREATE OR REPLACE PROCEDURE sp_list_participants (
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT s.student_id, s.name, sp.sport_name
        FROM sp_student s
        JOIN sp_registration r ON s.student_id = r.student_id
        JOIN sp_sport sp ON r.sport_id = sp.sport_id;
END;
/
