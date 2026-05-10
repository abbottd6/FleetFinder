# template_category references

SET @cap = 'Capital';
SET @lrg = 'Large';
SET @md = 'Medium';
SET @sml = 'Small';

# crew_template inserts (parent reference entity for each template) ---------------------------------------

INSERT INTO crew_template (template_label, template_category)
VALUES('Idris', @cap),
      ('Polaris', @cap),
      ('Generic Capital', @cap),
      ('Hammerhead', @lrg),
      ('A2 Hercules', @lrg),
      ('Starfarer/Gemini', @lrg),
      ('Starlancer TAC', @lrg),
      ('890 Jump', @lrg),
      ('Retaliator', @lrg),
      ('Perseus', @lrg),
      ('Carrack', @lrg),
      ('Reclaimer', @lrg),
      ('Generic Large', @lrg),
      ('Valkyrie', @md),
      ('600i', @md),
      ('Mercury Star Runner', @md),
      ('Redeemer', @md),
      ('MOLE', @md),
      ('Generic Medium', @md),
      ('Generic Small', @sml);

# template id references --------------------------------------------------------------------------------

SET @idris = (SELECT id_template FROM crew_template WHERE template_label = 'Idris');
SET @polaris = (SELECT id_template FROM crew_template WHERE template_label = 'Polaris');
SET @hh = (SELECT id_template FROM crew_template WHERE template_label = 'Hammerhead');
SET @a2 = (SELECT id_template FROM crew_template WHERE template_label = 'A2 Hercules');
SET @gemini = (SELECT id_template FROM crew_template WHERE template_label = 'Starfarer/Gemini');
SET @tac = (SELECT id_template FROM crew_template WHERE template_label = 'Starlancer TAC');
SET @890 = (SELECT id_template FROM crew_template WHERE template_label = '890 Jump');
SET @ret = (SELECT id_template FROM crew_template WHERE template_label = 'Retaliator');
SET @seus = (SELECT id_template FROM crew_template WHERE template_label = 'Perseus');
SET @carr = (SELECT id_template FROM crew_template WHERE template_label = 'Carrack');
SET @claimer = (SELECT id_template FROM crew_template WHERE template_label = 'Reclaimer');
SET @valk = (SELECT id_template FROM crew_template WHERE template_label = 'Valkyrie');
SET @600 = (SELECT id_template FROM crew_template WHERE template_label = '600i');
SET @gencap = (SELECT id_template FROM crew_template WHERE template_label = 'Generic Capital');
SET @genl = (SELECT id_template FROM crew_template WHERE template_label = 'Generic Large');
SET @genm = (SELECT id_template FROM crew_template WHERE template_label = 'Generic Medium');
SET @msr = (SELECT id_template FROM crew_template WHERE template_label = 'Mercury Star Runner');
SET @redeemer = (SELECT id_template FROM crew_template WHERE template_label = 'Redeemer');
SET @mole = (SELECT id_template FROM crew_template WHERE template_label = 'MOLE');
SET @gens = (SELECT id_template FROM crew_template WHERE template_label = 'Generic Small');

# template subgroup definitions -----------------------------------------------------------------------------

INSERT INTO crew_subgroup_template (template_id, parent_subgroup_id, subgroup_label, sort_order, intended_subgroup_size)
VALUES
    #IDRIS
    (@idris, NULL, 'Idris', 0, 28),
    (@idris, NULL, 'Bridge', 1, 5),
    (@idris, NULL, 'Gunnery', 2, 6),
    (@idris, NULL, 'Engineering', 3, 3),
    (@idris, NULL, 'Medical', 4, 2),
    (@idris, NULL, 'Fighter Bay', 5, 6),
    (@idris, NULL, 'Marine Detachment', 6, 6),
    #POLARIS
    (@polaris, NULL, 'Polaris', 0, 14),
    (@polaris, NULL, 'Bridge', 1, 5),
    (@polaris, NULL, 'Gunnery', 2, 4),
    (@polaris, NULL, 'Engineering', 3, 2),
    (@polaris, NULL, 'Weapons Station', 4, 1),
    (@polaris, NULL, 'Medical', 5, 1),
    (@polaris, NULL, 'Fighter Bay', 6, 1),
    #Generic Capital
    (@gencap, NULL, 'Generic Capital', 0, 18),
    (@gencap, NULL, 'Bridge', 1, 2),
    (@gencap, NULL, 'Gunnery', 2, 4),
    (@gencap, NULL, 'Engineering', 3, 2),
    (@gencap, NULL, 'Medical', 4, 1),
    (@gencap, NULL, 'Utility', 5, 1),
    (@gencap, NULL, 'Fighter Bay', 6, 2),
    (@gencap, NULL, 'Marine Detachment', 7, 6),
    #HAMMERHEAD
    (@hh, NULL, 'Hammerhead', 0, 11),
    (@hh, NULL, 'Bridge', 1, 3),
    (@hh, NULL, 'Gunnery', 2, 6),
    (@hh, NULL, 'Engineering', 3, 2),
    #A2 HERCULES
    (@a2, NULL, 'A2 Hercules', 0, 8),
    (@a2, NULL, 'Bridge', 1, 4),
    (@a2, NULL, 'Engineering',2, 1),
    (@a2, NULL, 'Ground Assault', 3, 3),
    #Starfarer/Gemini
    (@gemini, NULL, 'Starfarer/Gemini', 0, 8),
    (@gemini, NULL, 'Bridge', 1, 3),
    (@gemini, NULL, 'Gunnery', 2, 2),
    (@gemini, NULL, 'Fuel Operations', 3, 2),
    (@gemini, NULL, 'Engineering', 4, 1),
    #Starlancer TAC
    (@tac, NULL, 'Starlancer TAC', 0, 8),
    (@tac, NULL, 'Bridge', 1, 2),
    (@tac, NULL, 'Gunnery', 2, 4),
    (@tac, NULL, 'Medical', 3, 1),
    (@tac, NULL, 'Engineering', 4, 1),
    #890 Jump
    (@890, NULL, '890 Jump', 0, 8),
    (@890, NULL, 'Bridge', 1, 2),
    (@890, NULL, 'Gunnery', 2, 2),
    (@890, NULL, 'Medical', 3, 1),
    (@890, NULL, 'Engineering', 4, 1),
    (@890, NULL, 'Security', 5, 2),
    #Retaliator
    (@ret, NULL, 'Retaliator', 0, 9),
    (@ret, NULL, 'Bridge', 1, 2),
    (@ret, NULL, 'Gunnery', 2,5),
    (@ret, NULL, 'Engineering', 3,1),
    (@ret, NULL, 'Weapons Station', 4, 1),
    #Perseus
    (@seus, NULL, 'Perseus', 0, 7),
    (@seus, NULL, 'Bridge', 1, 3),
    (@seus, NULL, 'Gunnery', 2, 2),
    (@seus, NULL, 'Engineering', 3, 1),
    (@seus, NULL, 'Weapons Station', 4, 1),
    #Carrack
    (@carr, NULL, 'Carrack', 0, 8),
    (@carr, NULL, 'Bridge', 1, 3),
    (@carr, NULL, 'Gunnery', 2, 3),
    (@carr, NULL, 'Science & Exploration', 3, 1),
    (@carr, NULL, 'Engineering', 4, 1),
    #Reclaimer
    (@claimer, NULL, 'Reclaimer', 0, 5),
    (@claimer, NULL, 'Bridge', 1, 1),
    (@claimer, NULL, 'Claw Operator', 2, 1),
    (@claimer, NULL, 'Salvage Operator', 3, 2),
    (@claimer, NULL, 'Cargo', 4, 1),
    #Generic Large
    (@genl, NULL, 'Generic Large', 0, 8),
    (@genl, NULL, 'Bridge', 1, 3),
    (@genl, NULL, 'Gunnery', 2, 2),
    (@genl, NULL, 'Engineering', 3, 1),
    (@genl, NULL, 'Utility', 4, 2),
    #Valkyrie
    (@valk, NULL, 'Valkyrie', 0, 15),
    (@valk, NULL, 'Bridge', 1, 1),
    (@valk, NULL, 'Gunnery', 2, 2),
    (@valk, NULL, 'Engineering', 3, 1),
    (@valk, NULL,'Ground Assault', 4, 11),
    (@valk, NULL, 'Medical', 5, 1),
    #600i
    (@600, NULL, '600i', 0, 5),
    (@600, NULL, 'Bridge', 1, 2),
    (@600, NULL, 'Scanning/Navigation', 2, 2),
    (@600, NULL, 'Security', 3, 1),
    #Generic Medium
    (@genm, NULL, 'Generic Medium', 0, 5),
    (@genm, NULL, 'Bridge', 1, 2),
    (@genm, NULL, 'Gunnery', 2, 2),
    (@genm, NULL, 'Utility', 3, 1),
    #MSR
    (@msr, NULL, 'Mercury Star Runner', 0, 5),
    (@msr, NULL, 'Bridge', 1, 2),
    (@msr, NULL, 'Scanning/Data', 2, 1),
    (@msr, NULL, 'Gunnery', 3, 2),
    #Redeemer
    (@redeemer, NULL, 'Redeemer', 0, 4),
    (@redeemer, NULL, 'Bridge', 1, 2),
    (@redeemer, NULL, 'Gunnery', 2, 2),
    #MOLE
    (@mole, NULL, 'MOLE', 0, 5),
    (@mole, NULL, 'Bridge', 1, 1),
    (@mole, NULL, 'Mining Operators', 2, 3),
    (@mole, NULL, 'Utility', 3, 1),
    #GENERIC SMALL
    (@gens, NULL, 'Generic Small', 0, 1);

SET @idrisParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Idris');
SET @polarisParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Polaris');
SET @gencapParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Generic Capital');
SET @hhParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Hammerhead');
SET @a2Parent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'A2 Hercules');
SET @geminiParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Starfarer/Gemini');
SET @tacParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Starlancer TAC');
SET @890Parent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = '890 Jump');
SET @retParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Retaliator');
SET @persParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Perseus');
SET @carrParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Carrack');
SET @recParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Reclaimer');
SET @lParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Generic Large');
SET @valkParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Valkyrie');
SET @600Parent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = '600i');
SET @mParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Generic Medium');
SET @msrParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Mercury Star Runner');
SET @redeemerParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Redeemer');
SET @moleParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'MOLE');
SET @sParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Generic Small');

# subgroup parent pointer updates --------------------------------------------------------------------------

UPDATE crew_subgroup_template
SET parent_subgroup_id = @idrisParent
WHERE template_id = @idris AND subgroup_label <> 'Idris';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @polarisParent
WHERE template_id = @polaris AND subgroup_label <> 'Polaris';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @gencapParent
WHERE template_id = @gencap AND subgroup_label <> 'Generic Capital';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @hhParent
WHERE template_id = @hh AND subgroup_label <> 'Hammerhead';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @a2Parent
WHERE template_id = @a2 AND subgroup_label <> 'A2 Hercules';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @geminiParent
WHERE template_id = @gemini AND subgroup_label <> 'Starfarer/Gemini';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @tacParent
WHERE template_id = @tac AND subgroup_label <> 'Starlancer TAC';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @890Parent
WHERE template_id = @890 AND subgroup_label <> '890 Jump';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @retParent
WHERE template_id = @ret AND subgroup_label <> 'Retaliator';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @persParent
WHERE template_id = @seus AND subgroup_label <> 'Perseus';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @carrParent
WHERE template_id = @carr AND subgroup_label <> 'Carrack';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @recParent
WHERE template_id = @claimer AND subgroup_label <> 'Reclaimer';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @lParent
WHERE template_id = @genl AND subgroup_label <> 'Generic Large';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @valkParent
WHERE template_id = @valk AND subgroup_label <> 'Valkyrie';

SET @ga = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Ground Assault' AND template_id = @valk);
UPDATE crew_subgroup_template
SET parent_subgroup_id = @ga
WHERE template_id = @valk AND subgroup_label = 'Medical';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @600Parent
WHERE template_id = @600 AND subgroup_label <> '600i';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @mParent
WHERE template_id = @genm AND subgroup_label <> 'Generic Medium';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @msrParent
WHERE template_id = @msr AND subgroup_label <> 'Mercury Star Runner';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @redeemerParent
WHERE template_id = @redeemer AND subgroup_label <> 'Redeemer';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @moleParent
WHERE template_id = @mole AND subgroup_label <> 'MOLE';

# Role id mappings ----------------------------------------------------------------------------------------

SET @pilot = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Pilot');
SET @Copilot = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Co-Pilot');
SET @turretOp = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Turret Operator');
SET @nav = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Navigator');
SET @senseOp = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Sensor Operator');
SET @utilOp = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Utility Operator');
SET @marine = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Marine');
SET @sniper = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Sniper');
SET @grndDriver = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Vehicle Driver');
SET @medic = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Medic');
SET @repairTech = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Repair Tech');
SET @engTech = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Engineer');
SET @cargoTech = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Cargo Tech');
SET @weapsTech = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Weapons Tech');
SET @fleetC = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Fleet Commander');
SET @shipCpt = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Ship Captain');
SET @squadL = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Squad Leader');
SET @cargoM = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Cargo Master');
SET @logisticsM = (SELECT id_role FROM crew_role_classification WHERE role_title = 'Logistics Coordinator');

#Idris positions and roles --------------------------------------------------------------------------------
# (@idris, NULL, 'Idris', 28),
# (@idris, NULL, 'Bridge', 5),
# (@idris, NULL, 'Gunnery', 6),
# (@idris, NULL, 'Engineering', 3),
# (@idris, NULL, 'Medical', 2),
# (@idris, NULL, 'Fighter Bay', 6),
# (@idris, NULL, 'Marine Detachment', 6),

# @idris is id_template from crew template OR template_id from crew_subgroup_template
SET @idrisBridge = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @idris AND subgroup_label = 'Bridge');
SET @idrisGun = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris AND subgroup_label = 'Gunnery');
SET @idrisEng = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris AND subgroup_label = 'Engineering');
SET @idrisMed = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris AND subgroup_label = 'Medical');
SET @idrisFighterBay = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @idris AND subgroup_label = 'Fighter Bay');
SET @idrisMarine = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @idris AND subgroup_label = 'Marine Detachment');


INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES ( @idris,@idrisBridge, @shipCpt, 1),
       (@idris, @idrisBridge, @pilot, 2),
       (@idris, @idrisBridge, @Copilot, 3),
       (@idris, @idrisBridge, @turretOp, 4),
       (@idris,@idrisBridge, @turretOp, 5),
       (@idris,@idrisGun, @turretOp, 1),
       (@idris,@idrisGun, @turretOp, 2),
       (@idris,@idrisGun, @turretOp, 3),
       (@idris,@idrisGun, @turretOp, 4),
       (@idris,@idrisGun, @turretOp, 5),
       (@idris,@idrisGun, @turretOp, 6),
       (@idris,@idrisEng, @engTech, 7),
       (@idris,@idrisEng, @engTech, 8),
       (@idris,@idrisEng, @engTech, 9),
       (@idris,@idrisMed, @medic, 10),
       (@idris,@idrisMed, @medic, 11),
       (@idris,@idrisFighterBay, @pilot, 1),
       (@idris,@idrisFighterBay, @pilot, 2),
       (@idris,@idrisFighterBay, @pilot, 3),
       (@idris,@idrisFighterBay, @pilot, 4),
       (@idris,@idrisFighterBay, @pilot, 5),
       (@idris,@idrisFighterBay, @utilOp, 6),
       (@idris,@idrisMarine, @marine, 1),
       (@idris,@idrisMarine, @marine, 2),
       (@idris,@idrisMarine, @marine, 3),
       (@idris,@idrisMarine, @marine, 4),
       (@idris,@idrisMarine, @marine, 5),
       (@idris,@idrisMarine, @marine, 6);

#Polaris positions and roles------------------------------------------------------------------------------
# (@polaris, NULL, 'Polaris', 14),
# (@polaris, NULL, 'Bridge', 5),
# (@polaris, NULL, 'Gunnery', 4),
# (@polaris, NULL, 'Engineering', 2),
# (@polaris, NULL, 'Weapons Station', 1),
# (@polaris, NULL, 'Medical', 1),
# (@polaris, NULL, 'Fighter Bay', 1),

# @polaris is id_template from crew template OR template_id from crew_subgroup_template

SET @polarisBridge = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @polaris AND subgroup_label = 'Bridge');
SET @polarisGun = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @polaris AND subgroup_label = 'Gunnery');
SET @polarisEng = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @polaris AND subgroup_label = 'Engineering');
SET @polarisWeapSt = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @polaris AND subgroup_label = 'Weapons Station');
SET @polarisMedical = (SELECT id_template_subgroup
                       FROM crew_subgroup_template
                       WHERE template_id = @polaris AND subgroup_label = 'Medical');
SET @polarisFighterBay = (SELECT id_template_subgroup
                          FROM crew_subgroup_template
                          WHERE template_id = @polaris AND subgroup_label = 'Fighter Bay');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES (@polaris, @polarisBridge, @shipCpt, 1),
       (@polaris,@polarisBridge, @pilot, 2),
       (@polaris,@polarisBridge, @Copilot, 3),
       (@polaris,@polarisBridge, @turretOp, 4),
       (@polaris,@polarisBridge, @turretOp, 5),
       (@polaris,@polarisGun, @turretOp, 1),
       (@polaris,@polarisGun, @turretOp, 2),
       (@polaris,@polarisGun, @turretOp, 3),
       (@polaris,@polarisGun, @turretOp, 4),
       (@polaris,@polarisEng, @engTech, 1),
       (@polaris,@polarisEng, @engTech, 2),
       (@polaris,@polarisWeapSt, @weapsTech, 1),
       (@polaris,@polarisMedical, @medic, 1),
       (@polaris,@polarisFighterBay, @pilot, 1);

#Generic Capital positions and roles ----------------------------------------------------------------------
# (@gencap, NULL, 'Generic Capital', 18),
# (@gencap, NULL, 'Bridge', 2),
# (@gencap, NULL, 'Gunnery', 4),
# (@gencap, NULL, 'Engineering', 2),
# (@gencap, NULL, 'Medical', 1),
# (@gencap, NULL, 'Utility', 1),
# (@gencap, NULL, 'Fighter Bay', 2),
# (@gencap, NULL, 'Marine Detachment', 6),

# @gencap is id_template from crew template OR template_id from crew_subgroup_template

SET @gencapBridge = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @gencap AND subgroup_label = 'Bridge');
SET @gencapGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap AND subgroup_label = 'Gunnery');
SET @gencapEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap AND subgroup_label = 'Engineering');
SET @gencapMed = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap AND subgroup_label = 'Medical');
SET @gencapUtil = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @gencap AND subgroup_label = 'Utility');
SET @gencapFighterBay = (SELECT id_template_subgroup
                         FROM crew_subgroup_template
                         WHERE template_id = @gencap AND subgroup_label = 'Fighter Bay');
SET @gencapMarine = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @gencap AND subgroup_label = 'Marine Detachment');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@gencap, @gencapBridge, @pilot, 1),
        (@gencap, @gencapBridge, @Copilot, 2),
        (@gencap, @gencapGun, @turretOp, 1),
        (@gencap, @gencapGun, @turretOp, 2),
        (@gencap, @gencapGun, @turretOp, 3),
        (@gencap, @gencapGun, @turretOp, 4),
        (@gencap, @gencapEng, @engTech, 1),
        (@gencap, @gencapEng, @engTech, 2),
        (@gencap, @gencapMed, @medic, 1),
        (@gencap, @gencapUtil, @utilOp, 1),
        (@gencap, @gencapFighterBay, @pilot, 1),
        (@gencap, @gencapFighterBay, @pilot, 2),
        (@gencap, @gencapMarine, @marine, 1),
        (@gencap, @gencapMarine, @marine, 2),
        (@gencap, @gencapMarine, @marine, 3),
        (@gencap, @gencapMarine, @marine, 4),
        (@gencap, @gencapMarine, @marine, 5),
        (@gencap, @gencapMarine, @marine, 6);

# Hammerhead positions and roles -------------------------------------------------------------------------
# (@hh, NULL, 'Hammerhead', 11),
# (@hh, NULL, 'Bridge', 3),
# (@hh, NULL, 'Gunnery', 6),
# (@hh, NULL, 'Engineering', 2),

# @hh is id_template from crew template OR template_id from crew_subgroup_template

SET @hhBridge = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @hh AND subgroup_label = 'Bridge');
SET @hhGun = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @hh AND subgroup_label = 'Gunnery');
SET @hhEng = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @hh AND subgroup_label = 'Engineering');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@hh, @hhBridge, @shipCpt, 1),
        (@hh, @hhBridge, @pilot, 2),
        (@hh, @hhBridge, @Copilot, 3),
        (@hh, @hhGun, @turretOp, 1),
        (@hh, @hhGun, @turretOp, 2),
        (@hh, @hhGun, @turretOp, 3),
        (@hh, @hhGun, @turretOp, 4),
        (@hh, @hhGun, @turretOp, 5),
        (@hh, @hhGun, @turretOp, 6),
        (@hh, @hhEng, @engTech, 1),
        (@hh, @hhEng, @engTech, 2);

#A2 HERCULES positions and roles ------------------------------------------------------------------------
# (@a2, NULL, 'A2 Hercules', 8),
# (@a2, NULL, 'Bridge', 4),
# (@a2, NULL, 'Engineering',1),
# (@a2, NULL, 'Ground Assault', 3),

# @a2 is id_template from crew template OR template_id from crew_subgroup_template

SET @a2Bridge = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @a2 AND subgroup_label = 'Bridge');

SET @a2Eng = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @a2 AND subgroup_label = 'Engineering');
SET @a2Grnd = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @a2 AND subgroup_label = 'Ground Assault');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@a2, @a2Bridge, @pilot, 1),
        (@a2, @a2Bridge, @Copilot, 2),
        (@a2, @a2Bridge, @turretOp, 3),
        (@a2, @a2Bridge, @turretOp, 4),
        (@a2, @a2Eng, @engTech, 1),
        (@a2, @a2Grnd, @Marine, 1),
        (@a2, @a2Grnd, @Marine, 2),
        (@a2, @a2Grnd, @Marine, 3);

#Starfarer/Gemini positions and roles -------------------------------------------------------------------
# (@gemini, NULL, 'Starfarer/Gemini', 8),
# (@gemini, NULL, 'Bridge', 3),
# (@gemini, NULL, 'Gunnery', 2),
# (@gemini, NULL, 'Fuel Operations', 2),
# (@gemini, NULL, 'Engineering', 1),

# @gemini is id_template from crew template OR template_id from crew_subgroup_template

SET @geminiBridge = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @gemini AND subgroup_label = 'Bridge');
SET @geminiGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gemini AND subgroup_label = 'Gunnery');
SET @geminiFuel = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @gemini AND subgroup_label = 'Fuel Operations');
SET @geminiEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gemini AND subgroup_label = 'Engineering');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@gemini, @geminiBridge, @pilot, 1),
        (@gemini, @geminiBridge, @Copilot, 2),
        (@gemini, @geminiBridge, @utilOp, 3),
        (@gemini, @geminiGun, @turretOp, 1),
        (@gemini, @geminiGun, @turretOp, 2),
        (@gemini, @geminiFuel, @cargoTech, 1),
        (@gemini, @geminiFuel, @cargoTech, 2),
        (@gemini, @geminiEng, @engTech, 1);

#Starlancer TAC positions and roles ---------------------------------------------------------------------
# (@tac, NULL, 'Starlancer TAC', 8),
# (@tac, NULL, 'Bridge', 2),
# (@tac, NULL, 'Gunnery', 4),
# (@tac, NULL, 'Medical', 1),
# (@tac, NULL, 'Engineering', 1),

# @tac is id_template from crew template OR template_id from crew_subgroup_template

SET @tacBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @tac AND subgroup_label = 'Bridge');
SET @tacGun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac AND subgroup_label = 'Gunnery');
SET @tacMed = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac AND subgroup_label = 'Medical');
SET @tacEng = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac AND subgroup_label = 'Engineering');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@tac, @tacBridge, @pilot, 1),
        (@tac, @tacBridge, @Copilot, 2),
        (@tac, @tacGun, @turretOp, 1),
        (@tac, @tacGun, @turretOp, 2),
        (@tac, @tacGun, @turretOp, 3),
        (@tac, @tacGun, @turretOp, 4),
        (@tac, @tacMed, @medic, 1),
        (@tac, @tacEng, @engTech, 1);

#890 Jump positions and roles -------------------------------------------------------------------------
# (@890, NULL, '890 Jump', 8),
# (@890, NULL, 'Bridge', 2),
# (@890, NULL, 'Gunnery', 2),
# (@890, NULL, 'Medical', 1),
# (@890, NULL, 'Engineering', 1),
# (@890, NULL, 'Security', 2),

# @890 is id_template from crew template OR template_id from crew_subgroup_template

SET @890Bridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @890 AND subgroup_label = 'Bridge');
SET @890Gun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 AND subgroup_label = 'Gunnery');
SET @890Med = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 AND subgroup_label = 'Medical');
SET @890Eng = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 AND subgroup_label = 'Engineering');
SET @890Sec = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 AND subgroup_label = 'Security');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@890, @890Bridge, @pilot, 1),
        (@890, @890Bridge, @Copilot, 2),
        (@890, @890Gun, @turretOp, 1),
        (@890, @890Gun, @turretOp, 2),
        (@890, @890Med, @medic, 1),
        (@890, @890Eng, @engTech, 1),
        (@890, @890Sec, @marine, 1),
        (@890, @890Sec, @marine, 1);

#Retaliator positions and roles -----------------------------------------------------------------------
# (@ret, NULL, 'Retaliator', 9),
# (@ret, NULL, 'Bridge', 2),
# (@ret, NULL, 'Gunnery', 5),
# (@ret, NULL, 'Engineering', 1),
# (@ret, NULL, 'Weapons Station', 1),

# @ret is id_template from crew template OR template_id from crew_subgroup_template

SET @retBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret AND subgroup_label = 'Bridge');
SET @retGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret AND subgroup_label = 'Gunnery');
SET @retEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret AND subgroup_label = 'Engineering');
SET @retWeapSt = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret AND subgroup_label = 'Weapons Station');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@ret, @retBridge, @pilot, 1),
        (@ret, @retBridge, @Copilot, 2),
        (@ret, @retGun, @turretOp, 1),
        (@ret, @retGun, @turretOp, 2),
        (@ret, @retGun, @turretOp, 3),
        (@ret, @retGun, @turretOp, 4),
        (@ret, @retGun, @turretOp, 5),
        (@ret, @retEng, @engTech, 1),
        (@ret, @retWeapSt, @weapsTech, 1);

#Perseus positions and roles --------------------------------------------------------------------------
# (@seus, NULL, 'Perseus', 7),
# (@seus, NULL, 'Bridge', 3),
# (@seus, NULL, 'Gunnery', 2),
# (@seus, NULL, 'Engineering', 1),
# (@seus, NULL, 'Weapons Station', 1),

# @seus is id_template from crew template OR template_id from crew_subgroup_template

SET @perseusBridge = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @seus AND subgroup_label = 'Bridge');
SET @perseusGun = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @seus AND subgroup_label = 'Gunnery');
SET @perseusEng = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @seus AND subgroup_label = 'Engineering');
SET @perseusWeapSt = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @seus AND subgroup_label = 'Weapons Station');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@seus, @perseusBridge, @pilot, 1),
        (@seus, @perseusBridge, @Copilot, 2),
        (@seus, @perseusBridge, @turretOp, 3),
        (@seus, @perseusGun, @turretOp, 1),
        (@seus, @perseusGun, @turretOp, 2),
        (@seus, @perseusEng, @engTech, 1),
        (@seus, @perseusWeapSt, @weapsTech, 1);

#Carrack positions and roles --------------------------------------------------------------------------
# (@carr, NULL, 'Carrack', 6),
# (@carr, NULL, 'Bridge', 3),
# (@carr, NULL, 'Gunnery', 3),
# (@carr, NULL, 'Science & Exploration', 1),
# (@carr, NULL, 'Engineering', 1),

# @carr is id_template from crew template OR template_id from crew_subgroup_template

SET @carrBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @carr AND subgroup_label = 'Bridge');
SET @carrGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr AND subgroup_label = 'Gunnery');
SET @carrSci = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr AND subgroup_label = 'Science & Exploration');
SET @carrEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr AND subgroup_label = 'Engineering');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@carr, @carrBridge, @pilot, 1),
        (@carr, @carrBridge, @Copilot, 2),
        (@carr, @carrBridge, @Copilot, 3),
        (@carr, @carrGun, @turretOp, 1),
        (@carr, @carrGun, @turretOp, 2),
        (@carr, @carrGun, @turretOp, 3),
        (@carr, @carrSci, @senseOp, 1),
        (@carr, @carrEng, @engTech, 1);

#Reclaimer positions and roles ------------------------------------------------------------------------
# (@claimer, NULL, 'Reclaimer', 5),
# (@claimer, NULL, 'Bridge', 1),
# (@claimer, NULL, 'Claw Operator', 1),
# (@claimer, NULL, 'Salvage Operator', 2),
# (@claimer, NULL, 'Cargo', 1),

# @claimer is id_template from crew template OR template_id from crew_subgroup_template

SET @claimerBridge = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @claimer AND subgroup_label = 'Bridge');
SET @claimerClaw = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @claimer AND subgroup_label = 'Claw Operator');
SET @claimerSalv = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @claimer AND subgroup_label = 'Salvage Operator');
SET @claimerCargo = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @claimer AND subgroup_label = 'Cargo');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@claimer, @claimerBridge, @pilot, 1),
        (@claimer, @claimerClaw, @utilOp, 1),
        (@claimer, @claimerSalv, @utilOp, 1),
        (@claimer, @claimerSalv, @utilOp, 2),
        (@claimer, @claimerCargo, @cargoTech, 1);

#Generic Large positions and roles --------------------------------------------------------------------
# (@genl, NULL, 'Generic Large', 8),
# (@genl, NULL, 'Bridge', 3),
# (@genl, NULL, 'Gunnery', 2),
# (@genl, NULL, 'Engineering', 1),
# (@genl, NULL, 'Utility', 2),

# @genl is id_template from crew template OR template_id from crew_subgroup_template

SET @genlBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @genl AND subgroup_label = 'Bridge');
SET @genlGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genl AND subgroup_label = 'Gunnery');
SET @genlEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genl AND subgroup_label = 'Engineering');
SET @genlUtil = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @genl AND subgroup_label = 'Utility');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@genl, @genlBridge, @pilot, 1),
        (@genl, @genlBridge, @Copilot, 2),
        (@genl, @genlBridge, @turretOp, 3),
        (@genl, @genlGun, @turretOp, 1),
        (@genl, @genlGun, @turretOp, 2),
        (@genl, @genlEng, @engTech, 1),
        (@genl, @genlUtil, @utilOp, 1),
        (@genl, @genlUtil, @utilOp, 2);

#Valkyrie positions and roles -------------------------------------------------------------------------
# (@valk, NULL, 'Valkyrie', 15),
# (@valk, NULL, 'Bridge', 1),
# (@valk, NULL, 'Gunnery', 2),
# (@valk, NULL, 'Engineering', 1),
# (@valk, NULL,'Ground Assault', 11),
# (@valk, NULL, 'Medical', 1),

# @valk is id_template from crew template OR template_id from crew_subgroup_template

SET @valkBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @valk AND subgroup_label = 'Bridge');
SET @valkGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk AND subgroup_label = 'Gunnery');
SET @valkEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk AND subgroup_label = 'Engineering');
SET @valkGrnd = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @valk AND subgroup_label = 'Ground Assault');
SET @valkMed = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk AND subgroup_label = 'Medical');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@valk, @valkBridge, @pilot, 1),
        (@valk, @valkGun, @turretOp, 1),
        (@valk, @valkGun, @turretOp, 2),
        (@valk, @valkEng, @engTech, 1),
        (@valk, @valkGrnd, @Marine, 1),
        (@valk, @valkGrnd, @Marine, 2),
        (@valk, @valkGrnd, @Marine, 3),
        (@valk, @valkGrnd, @Marine, 4),
        (@valk, @valkGrnd, @Marine, 5),
        (@valk, @valkGrnd, @Marine, 6),
        (@valk, @valkGrnd, @Marine, 7),
        (@valk, @valkGrnd, @Marine, 8),
        (@valk, @valkGrnd, @Marine, 9),
        (@valk, @valkGrnd, @Marine, 10),
        (@valk, @valkMed, @Medic, 1);


#600i positions and roles -----------------------------------------------------------------------------
# (@600, NULL, '600i', 5),
# (@600, NULL, 'Bridge', 2),
# (@600, NULL, 'Scanning/Navigation', 2),
# (@600, NULL, 'Security', 1),

# @600 is id_template from crew template OR template_id from crew_subgroup_template

SET @600Bridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @600 AND subgroup_label = 'Bridge');
SET @600Nav = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @600 AND subgroup_label = 'Scanning/Navigation');
SET @600Sec = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @600 AND subgroup_label = 'Security');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@600, @600Bridge, @pilot, 1),
        (@600, @600Bridge, @Copilot, 2),
        (@600, @600Nav, @nav, 1),
        (@600, @600Nav, @senseOp, 2),
        (@600, @600Sec, @turretOp, 1);

#Generic Medium positions and roles ------------------------------------------------------------------
# (@genm, NULL, 'Generic Medium', 5),
# (@genm, NULL, 'Bridge', 2),
# (@genm, NULL, 'Gunnery', 2),
# (@genm, NULL, 'Utility', 1),

# @genm is id_template from crew template OR template_id from crew_subgroup_template

SET @genmBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @genm AND subgroup_label = 'Bridge');
SET @genmGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genm AND subgroup_label = 'Gunnery');
SET @genmUtil = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @genm AND subgroup_label = 'Utility');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@genm, @genmBridge, @pilot, 1),
        (@genm, @genmBridge, @Copilot, 2),
        (@genm, @genmGun, @turretOp, 1),
        (@genm, @genmGun, @turretOp, 2),
        (@genm, @genmUtil, @utilOp, 1);

#MSR positions and roles -----------------------------------------------------------------------------
# (@msr, NULL, 'Mercury Star Runner', 5),
# (@msr, NULL, 'Bridge', 2),
# (@msr, NULL, 'Scanning/Data', 1),
# (@msr, NULL, 'Gunnery', 2),

# @msr is id_template from crew template OR template_id from crew_subgroup_template

SET @msrBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @msr AND subgroup_label = 'Bridge');
SET @msrScan = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @msr AND subgroup_label = 'Scanning/Data');
SET @msrGun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @msr AND subgroup_label = 'Gunnery');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@msr, @msrBridge, @pilot, 1),
        (@msr, @msrBridge, @Copilot, 2),
        (@msr, @msrScan, @senseOp, 1),
        (@msr, @msrGun, @turretOp, 1),
        (@msr, @msrGun, @turretOp, 2);

#Redeemer postions and roles -------------------------------------------------------------------------
# (@redeemer, NULL, 'Redeemer', 4),
# (@redeemer, NULL, 'Bridge', 2),
# (@redeemer, NULL, 'Gunnery', 2),

# @redeemer is id_template from crew template OR template_id from crew_subgroup_template

SET @redeemBridge = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @redeemer AND subgroup_label = 'Bridge');
SET @redeemGun = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @redeemer AND subgroup_label = 'Gunnery');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@redeemer, @redeemBridge, @pilot, 1),
        (@redeemer, @redeemBridge, @Copilot, 2),
        (@redeemer, @redeemGun, @turretOp, 1),
        (@redeemer, @redeemGun, @turretOp, 2);

#MOLE positions and roles ----------------------------------------------------------------------------
# (@mole, NULL, 'MOLE', 5),
# (@mole, NULL, 'Bridge', 1),
# (@mole, NULL, 'Mining Operators', 3),
# (@mole, NULL, 'Utility', 1),

# @mole is id_template from crew template OR template_id from crew_subgroup_template

SET @moleBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole AND subgroup_label = 'Bridge');
SET @moleMine = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole AND subgroup_label = 'Mining Operators');
SET @moleUtil = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole AND subgroup_label = 'Utility');

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES  (@mole, @moleBridge, @pilot, 1),
        (@mole, @moleMine, @utilOp, 1),
        (@mole, @moleMine, @utilOp, 2),
        (@mole, @moleMine, @utilOp, 3),
        (@mole, @moleUtil, @utilOp, 1);
#GENERIC SMALL positions and roles -------------------------------------------------------------------
# (@gens, NULL, 'Generic Small', 1);

# @gens is id_template from crew template OR template_id from crew_subgroup_template

INSERT INTO crew_position_template (root_template_id, subgroup_template_id, position_role_id, sort_order)
VALUES (@gens, @sParent, @pilot, 1);