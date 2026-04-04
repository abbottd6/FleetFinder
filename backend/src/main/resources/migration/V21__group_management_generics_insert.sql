INSERT INTO rank_privilege_type (id_privilege)
VALUES  ('MANAGE_RANKS'),
        ('MANAGE_SUBGROUPS'),
        ('MANAGE_ROSTERS'),
        ('MANAGE_POSITIONS'),
        ('MANAGE_ROLES'),
        ('INVITE');

INSERT INTO in_group_rank (rank_title, rank_notes)
VALUES('Owner', 'Manage ranks, subgroups, rosters, positions, roles, and invites.'),
      ('Captain', 'Manage subgroups, rosters, positions, roles, and invites.'),
      ('Trusted', 'Send invites.'),
      ('Member', 'No privileges.');

SET @owner = (SELECT id_rank FROM in_group_rank WHERE rank_title = 'Owner');
SET @capt = (SELECT id_rank FROM in_group_rank WHERE rank_title = 'Captain');
SET @trusted = (SELECT id_rank FROM in_group_rank WHERE rank_title = 'Trusted');
SET @member = (SELECT id_rank FROM in_group_rank WHERE rank_title = 'Member');

INSERT INTO group_rank_assigned_privilege (rank_id, privilege_id)
VALUES(@owner, 'MANAGE_RANKS'),
      (@owner, 'MANAGE_SUBGROUPS'),
      (@owner, 'MANAGE_ROSTERS'),
      (@owner, 'MANAGE_POSITIONS'),
      (@owner, 'MANAGE_ROLES'),
      (@owner, 'INVITE'),
      (@capt, 'MANAGE_SUBGROUPS'),
      (@capt, 'MANAGE_ROSTERS'),
      (@capt, 'MANAGE_POSITIONS'),
      (@capt, 'MANAGE_ROLES'),
      (@capt, 'INVITE'),
      (@trusted, 'INVITE');


SET @ship = 'Ship Crew';
SET @ground = 'Ground Crew';
SET @support = 'Support';
SET @command = 'Command';

INSERT INTO crew_role_classification (role_category, role_title)
VALUES(@ship, 'Pilot'),
      (@ship, 'Co-Pilot'),
      (@ship, 'Turret Operator'),
      (@ship, 'Engineer'),
      (@ship, 'Navigator'),
      (@ship, 'Sensor Operator'),
      (@ship, 'Utility Operator'),
      (@ship, 'Medic'),
      (@ground, 'Marine'),
      (@ground, 'Medic'),
      (@ground, 'Sniper'),
      (@ground, 'Vehicle Driver'),
      (@ground, 'Engineer'),
      (@support, 'Medic'),
      (@support, 'Repair Tech'),
      (@support, 'Engineer'),
      (@support, 'Cargo Tech'),
      (@command, 'Fleet Commander'),
      (@command, 'Wing Commander'),
      (@command, 'Squad Leader'),
      (@command, 'Cargo Master'),
      (@command, 'Logistics Coordinator');
