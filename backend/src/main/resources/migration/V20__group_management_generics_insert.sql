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
