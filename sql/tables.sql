--Table creation with constraints Commands
--SP_student
create table sp_student(student_id number primary key,name varchar(50) not null,department varchar(20) not null,year number check(year between 1 and 4));
--SP_Sport
create table sp_sport(sport_id number primary key, sport_name varchar(30) unique not null,sport_type varchar(15) check (sport_type in('TEAM','INDIVIDUAL')));
--SP_Coach
create table sp_coach(coach_id number,name varchar(50) not null,sport_id number, constraint sp_coach_pk primary key(coach_id),constraint sp_coach_fk foreign key (sport_id)references sp_sport(sport_id));
--SP_Team
create table sp_team(team_id number, team_name varchar(30) not null, sport_id number,coach_id number, constraint sp_team_pk primary key(team_id), constraint sp_team_name_uk unique (team_name),constraint sp_team_sport_fk foreign key(sport_id)references sp_sport(sport_id),constraint sp_team_coach_fk foreign key(coach_id)references sp_coach(coach_id));
--SP_Registration
create table sp_registration(reg_id number, student_id number,sport_id number, event_name varchar(30),constraint sp_reg_pk primary key(reg_id), constraint sp_reg_student_fk foreign key(student_id) references sp_student(student_id), constraint sp_reg_sport_fk foreign key(sport_id) references sp_sport(sport_id));
--SP_Participant
create table sp_participant(participant_id number, reg_id number, team_id number, constraint sp_participant_pk primary key(particiant_id), constraint sp_participant_reg_fk foreign key (reg_id) references sp_registration(reg_id),constraint sp_participant_team_fk foreign key (team_id) references sp_team(team_id));
--SP_Event
create table sp_event(event_id number, sport_id number, event_name varchar(30) not null, constraint sp_event_pk primary key (event_id), constraint sp_event_sport_fk foreign key (sport_id) references sp_sport(sport_id), constraint sp_event_uk unique(sport_id,event_name));
--SP_Match
create table sp_match(match_id number primary key, sport_id number,team1_id number, team2_id number,match_date date, venue varchar(30), constraint sp_match_sport_fk foreign key(sport_id) references sp_sport(sport_id), constraint sp_match_team1_fk foreign key (team1_id) references sp_team(team_id), constraint sp_match_team2_fk foreign key (team2_id)  references sp_team(team_id), constraint sp_match_team_ck check(team1_id<>team2_id));
--SP_result
create table sp_result (result_id number primary key, event_id number, participant_id number, position number, performance varchar(20), constraint sp_result_event_fk foreign key (event_id) references sp_event(event_id), constraint sp_result_participant_fk foreign key (participant_id) references sp_participant(participant_id), constraint sp_result_position_ck check (position>0));
