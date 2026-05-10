INSERT INTO rank_privilege_type (id_privilege)
VALUES  ('MANAGE_RANKS'),
        ('MANAGE_SUBGROUPS'),
        ('MANAGE_ROSTERS'),
        ('MANAGE_POSITIONS'),
        ('MANAGE_ROLES'),
        ('INVITE');

INSERT INTO in_group_rank (rank_title, rank_notes, is_default_rank)
VALUES('Owner', 'Manage ranks, subgroups, rosters, positions, roles, and invites.', 1),
      ('Captain', 'Manage subgroups, rosters, positions, roles, and invites.', 1),
      ('Trusted', 'Send invites.', 1),
      ('Member', 'No privileges.', 1);

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

INSERT INTO crew_role_classification (role_category, role_title, is_generic_role)
VALUES(@ship, 'Pilot', 1),
      (@ship, 'Co-Pilot', 1),
      (@ship, 'Turret Operator', 1),
      (@ship, 'Navigator', 1),
      (@ship, 'Sensor Operator', 1),
      (@ship, 'Utility Operator', 1),
      (@ground, 'Marine', 1),
      (@ground, 'Sniper', 1),
      (@ground, 'Vehicle Driver', 1),
      (@support, 'Medic', 1),
      (@support, 'Repair Tech', 1),
      (@support, 'Engineer', 1),
      (@support, 'Cargo Tech', 1),
      (@support, 'Weapons Tech', 1),
      (@command, 'Fleet Commander', 1),
      (@command, 'Ship Captain', 1),
      (@command, 'Squad Leader', 1),
      (@command, 'Cargo Master', 1),
      (@command, 'Logistics Coordinator', 1);
