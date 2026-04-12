--PL/SQL triggers
--Trigger to check invalid position in SP_result
CREATE OR REPLACE TRIGGER sp_result_check
BEFORE INSERT ON sp_result
FOR EACH ROW
BEGIN
    IF :NEW.position <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Position must be positive');
    END IF;
END;
/



