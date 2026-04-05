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

INSERT INTO crew_subgroup_template (template_id, parent_subgroup_id, subgroup_label, intended_subgroup_size)
VALUES
    #IDRIS
    (@idris, NULL, 'Idris', 28),
    (@idris, NULL, 'Bridge', 5),
    (@idris, NULL, 'Gunnery', 6),
    (@idris, NULL, 'Engineering', 3),
    (@idris, NULL, 'Medical', 2),
    (@idris, NULL, 'Fighter Bay', 6),
    (@idris, NULL, 'Marine Detachment', 6),
    #POLARIS
    (@polaris, NULL, 'Polaris', 14),
    (@polaris, NULL, 'Bridge', 5),
    (@polaris, NULL, 'Gunnery', 4),
    (@polaris, NULL, 'Engineering', 2),
    (@polaris, NULL, 'Weapons Station', 1),
    (@polaris, NULL, 'Medical', 1),
    (@polaris, NULL, 'Fighter Bay', 1),
    #Generic Capital
    (@gencap, NULL, 'Generic Capital', 18),
    (@gencap, NULL, 'Bridge', 2),
    (@gencap, NULL, 'Gunnery', 4),
    (@gencap, NULL, 'Engineering', 2),
    (@gencap, NULL, 'Medical', 1),
    (@gencap, NULL, 'Utility', 1),
    (@gencap, NULL, 'Fighter Bay', 2),
    (@gencap, NULL, 'Marine Detachment', 6),
    #HAMMERHEAD
    (@hh, NULL, 'Hammerhead', 11),
    (@hh, NULL, 'Bridge', 3),
    (@hh, NULL, 'Gunnery', 6),
    (@hh, NULL, 'Engineering', 2),
    #A2 HERCULES
    (@a2, NULL, 'A2 Hercules', 8),
    (@a2, NULL, 'Bridge', 4),
    (@a2, NULL, 'Engineering',1),
    (@a2, NULL, 'Ground Assault', 3),
    #Starfarer/Gemini
    (@gemini, NULL, 'Starfarer/Gemini', 8),
    (@gemini, NULL, 'Bridge', 3),
    (@gemini, NULL, 'Gunnery', 2),
    (@gemini, NULL, 'Fuel Operations', 2),
    (@gemini, NULL, 'Engineering', 1),
    #Starlancer TAC
    (@tac, NULL, 'Starlancer TAC', 8),
    (@tac, NULL, 'Bridge', 2),
    (@tac, NULL, 'Gunnery', 4),
    (@tac, NULL, 'Medical', 1),
    (@tac, NULL, 'Engineering', 1),
    #890 Jump
    (@890, NULL, '890 Jump', 8),
    (@890, NULL, 'Bridge', 2),
    (@890, NULL, 'Gunnery', 2),
    (@890, NULL, 'Medical', 1),
    (@890, NULL, 'Engineering', 1),
    (@890, NULL, 'Security', 2),
    #Retaliator
    (@ret, NULL, 'Retaliator', 9),
    (@ret, NULL, 'Bridge', 2),
    (@ret, NULL, 'Gunnery', 5),
    (@ret, NULL, 'Engineering', 1),
    (@ret, NULL, 'Weapons Station', 1),
    #Perseus
    (@seus, NULL, 'Perseus', 7),
    (@seus, NULL, 'Bridge', 3),
    (@seus, NULL, 'Gunnery', 2),
    (@seus, NULL, 'Engineering', 1),
    (@seus, NULL, 'Weapons Station', 1),
    #Carrack
    (@carr, NULL, 'Carrack', 8),
    (@carr, NULL, 'Bridge', 3),
    (@carr, NULL, 'Gunnery', 3),
    (@carr, NULL, 'Science & Exploration', 1),
    (@carr, NULL, 'Engineering', 1),
    #Reclaimer
    (@claimer, NULL, 'Reclaimer', 5),
    (@claimer, NULL, 'Bridge', 1),
    (@claimer, NULL, 'Claw Operator', 1),
    (@claimer, NULL, 'Salvage Operator', 2),
    (@claimer, NULL, 'Cargo', 1),
    #Generic Large
    (@genl, NULL, 'Generic Large', 8),
    (@genl, NULL, 'Bridge', 3),
    (@genl, NULL, 'Gunnery', 2),
    (@genl, NULL, 'Engineering', 1),
    (@genl, NULL, 'Utility', 2),
    #Valkyrie
    (@valk, NULL, 'Valkyrie', 15),
    (@valk, NULL, 'Bridge', 1),
    (@valk, NULL, 'Gunnery', 2),
    (@valk, NULL, 'Engineering', 1),
    (@valk, NULL,'Ground Assault', 11),
    (@valk, NULL, 'Medical', 1),
    #600i
    (@600, NULL, '600i', 5),
    (@600, NULL, 'Bridge', 2),
    (@600, NULL, 'Scanning/Navigation', 2),
    (@600, NULL, 'Security', 1),
    #Generic Medium
    (@genm, NULL, 'Generic Medium', 5),
    (@genm, NULL, 'Bridge', 2),
    (@genm, NULL, 'Gunnery', 2),
    (@genm, NULL, 'Utility', 1),
    #MSR
    (@msr, NULL, 'Mercury Star Runner', 5),
    (@msr, NULL, 'Bridge', 2),
    (@msr, NULL, 'Scanning/Data', 1),
    (@msr, NULL, 'Gunnery', 2),
    #Redeemer
    (@redeemer, NULL, 'Redeemer', 4),
    (@redeemer, NULL, 'Bridge', 2),
    (@redeemer, NULL, 'Gunnery', 2),
    #MOLE
    (@mole, NULL, 'MOLE', 5),
    (@mole, NULL, 'Bridge', 1),
    (@mole, NULL, 'Mining Operators', 3),
    (@mole, NULL, 'Utility', 1),
    #GENERIC SMALL
    (@gens, NULL, 'Generic Small', 1);

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
WHERE template_id = @idris && subgroup_label <> 'Idris';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @polarisParent
WHERE template_id = @polaris && subgroup_label <> 'Polaris';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @gencapParent
WHERE template_id = @gencap && subgroup_label <> 'Generic Capital';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @hhParent
WHERE template_id = @hh && subgroup_label <> 'Hammerhead';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @a2Parent
WHERE template_id = @a2 && subgroup_label <> 'A2 Hercules';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @geminiParent
WHERE template_id = @gemini && subgroup_label <> 'Starfarer/Gemini';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @tacParent
WHERE template_id = @tac && subgroup_label <> 'Starlancer TAC';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @890Parent
WHERE template_id = @890 && subgroup_label <> '890 Jump';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @retParent
WHERE template_id = @ret && subgroup_label <> 'Retaliator';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @persParent
WHERE template_id = @seus && subgroup_label <> 'Perseus';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @carrParent
WHERE template_id = @carr && subgroup_label <> 'Carrack';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @recParent
WHERE template_id = @claimer && subgroup_label <> 'Reclaimer';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @lParent
WHERE template_id = @genl && subgroup_label <> 'Generic Large';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @valkParent
WHERE template_id = @valk && subgroup_label <> 'Valkyrie';

SET @ga = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Ground Assault' && template_id = @valk);
UPDATE crew_subgroup_template
SET parent_subgroup_id = @ga
WHERE template_id = @valk && subgroup_label = 'Medical';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @600Parent
WHERE template_id = @600 && subgroup_label <> '600i';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @mParent
WHERE template_id = @genm && subgroup_label <> 'Generic Medium';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @msrParent
WHERE template_id = @msr && subgroup_label <> 'Mercury Star Runner';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @redeemerParent
WHERE template_id = @redeemer && subgroup_label <> 'Redeemer';

UPDATE crew_subgroup_template
SET parent_subgroup_id = @moleParent
WHERE template_id = @mole && subgroup_label <> 'MOLE';

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
                    WHERE template_id = @idris && subgroup_label = 'Bridge');
SET @idrisGun = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris && subgroup_label = 'Gunnery');
SET @idrisEng = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris && subgroup_label = 'Engineering');
SET @idrisMed = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @idris && subgroup_label = 'Medical');
SET @idrisFighterBay = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @idris && subgroup_label = 'Fighter Bay');
SET @idrisMarine = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @idris && subgroup_label = 'Marine Detachment');


INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES (@idrisBridge, @shipCpt),
       (@idrisBridge, @pilot),
       (@idrisBridge, @Copilot),
       (@idrisBridge, @turretOp),
       (@idrisBridge, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisGun, @turretOp),
       (@idrisEng, @engTech),
       (@idrisEng, @engTech),
       (@idrisEng, @engTech),
       (@idrisMed, @medic),
       (@idrisMed, @medic),
       (@idrisFighterBay, @pilot),
       (@idrisFighterBay, @pilot),
       (@idrisFighterBay, @pilot),
       (@idrisFighterBay, @pilot),
       (@idrisFighterBay, @pilot),
       (@idrisFighterBay, @utilOp),
       (@idrisMarine, @marine),
       (@idrisMarine, @marine),
       (@idrisMarine, @marine),
       (@idrisMarine, @marine),
       (@idrisMarine, @marine),
       (@idrisMarine, @marine);

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
                      WHERE template_id = @polaris && subgroup_label = 'Bridge');
SET @polarisGun = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @polaris && subgroup_label = 'Gunnery');
SET @polarisEng = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @polaris && subgroup_label = 'Engineering');
SET @polarisWeapSt = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @polaris && subgroup_label = 'Weapons Station');
SET @polarisMedical = (SELECT id_template_subgroup
                       FROM crew_subgroup_template
                       WHERE template_id = @polaris && subgroup_label = 'Medical');
SET @polarisFighterBay = (SELECT id_template_subgroup
                          FROM crew_subgroup_template
                          WHERE template_id = @polaris && subgroup_label = 'Fighter Bay');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES (@polarisBridge, @shipCpt),
       (@polarisBridge, @pilot),
       (@polarisBridge, @Copilot),
       (@polarisBridge, @turretOp),
       (@polarisBridge, @turretOp),
       (@polarisGun, @turretOp),
       (@polarisGun, @turretOp),
       (@polarisGun, @turretOp),
       (@polarisGun, @turretOp),
       (@polarisEng, @engTech),
       (@polarisEng, @engTech),
       (@polarisWeapSt, @weapsTech),
       (@polarisMedical, @medic),
       (@polarisFighterBay, @pilot);

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
                     WHERE template_id = @gencap && subgroup_label = 'Bridge');
SET @gencapGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap && subgroup_label = 'Gunnery');
SET @gencapEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap && subgroup_label = 'Engineering');
SET @gencapMed = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gencap && subgroup_label = 'Medical');
SET @gencapUtil = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @gencap && subgroup_label = 'Utility');
SET @gencapFighterBay = (SELECT id_template_subgroup
                         FROM crew_subgroup_template
                         WHERE template_id = @gencap && subgroup_label = 'Fighter Bay');
SET @gencapMarine = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @gencap && subgroup_label = 'Marine Detachment');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@gencapBridge, @pilot),
        (@gencapBridge, @Copilot),
        (@gencapGun, @turretOp),
        (@gencapGun, @turretOp),
        (@gencapGun, @turretOp),
        (@gencapGun, @turretOp),
        (@gencapEng, @engTech),
        (@gencapEng, @engTech),
        (@gencapMed, @medic),
        (@gencapUtil, @utilOp),
        (@gencapFighterBay, @pilot),
        (@gencapFighterBay, @pilot),
        (@gencapMarine, @marine),
        (@gencapMarine, @marine),
        (@gencapMarine, @marine),
        (@gencapMarine, @marine),
        (@gencapMarine, @marine),
        (@gencapMarine, @marine);

# Hammerhead positions and roles -------------------------------------------------------------------------
# (@hh, NULL, 'Hammerhead', 11),
# (@hh, NULL, 'Bridge', 3),
# (@hh, NULL, 'Gunnery', 6),
# (@hh, NULL, 'Engineering', 2),

# @hh is id_template from crew template OR template_id from crew_subgroup_template

SET @hhBridge = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @hh && subgroup_label = 'Bridge');
SET @hhGun = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @hh && subgroup_label = 'Gunnery');
SET @hhEng = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @hh && subgroup_label = 'Engineering');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@hhBridge, @shipCpt),
        (@hhBridge, @pilot),
        (@hhBridge, @Copilot),
        (@hhGun, @turretOp),
        (@hhGun, @turretOp),
        (@hhGun, @turretOp),
        (@hhGun, @turretOp),
        (@hhGun, @turretOp),
        (@hhGun, @turretOp),
        (@hhEng, @engTech),
        (@hhEng, @engTech);

#A2 HERCULES positions and roles ------------------------------------------------------------------------
# (@a2, NULL, 'A2 Hercules', 8),
# (@a2, NULL, 'Bridge', 4),
# (@a2, NULL, 'Engineering',1),
# (@a2, NULL, 'Ground Assault', 3),

# @a2 is id_template from crew template OR template_id from crew_subgroup_template

SET @a2Bridge = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @a2 && subgroup_label = 'Bridge');

SET @a2Eng = (SELECT id_template_subgroup
              FROM crew_subgroup_template
              WHERE template_id = @a2 && subgroup_label = 'Engineering');
SET @a2Grnd = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @a2 && subgroup_label = 'Ground Assault');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@a2Bridge, @pilot),
        (@a2Bridge, @Copilot),
        (@a2Bridge, @turretOp),
        (@a2Bridge, @turretOp),
        (@a2Eng, @engTech),
        (@a2Grnd, @Marine),
        (@a2Grnd, @Marine),
        (@a2Grnd, @Marine);

#Starfarer/Gemini positions and roles -------------------------------------------------------------------
# (@gemini, NULL, 'Starfarer/Gemini', 8),
# (@gemini, NULL, 'Bridge', 3),
# (@gemini, NULL, 'Gunnery', 2),
# (@gemini, NULL, 'Fuel Operations', 2),
# (@gemini, NULL, 'Engineering', 1),

# @gemini is id_template from crew template OR template_id from crew_subgroup_template

SET @geminiBridge = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @gemini && subgroup_label = 'Bridge');
SET @geminiGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gemini && subgroup_label = 'Gunnery');
SET @geminiFuel = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @gemini && subgroup_label = 'Fuel Operations');
SET @geminiEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @gemini && subgroup_label = 'Engineering');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@geminiBridge, @pilot),
        (@geminiBridge, @Copilot),
        (@geminiBridge, @utilOp),
        (@geminiGun, @turretOp),
        (@geminiGun, @turretOp),
        (@geminiFuel, @cargoTech),
        (@geminiFuel, @cargoTech),
        (@geminiEng, @engTech);

#Starlancer TAC positions and roles ---------------------------------------------------------------------
# (@tac, NULL, 'Starlancer TAC', 8),
# (@tac, NULL, 'Bridge', 2),
# (@tac, NULL, 'Gunnery', 4),
# (@tac, NULL, 'Medical', 1),
# (@tac, NULL, 'Engineering', 1),

# @tac is id_template from crew template OR template_id from crew_subgroup_template

SET @tacBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @tac && subgroup_label = 'Bridge');
SET @tacGun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac && subgroup_label = 'Gunnery');
SET @tacMed = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac && subgroup_label = 'Medical');
SET @tacEng = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @tac && subgroup_label = 'Engineering');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@tacBridge, @pilot),
        (@tacBridge, @Copilot),
        (@tacGun, @turretOp),
        (@tacGun, @turretOp),
        (@tacGun, @turretOp),
        (@tacGun, @turretOp),
        (@tacMed, @medic),
        (@tacEng, @engTech);

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
                  WHERE template_id = @890 && subgroup_label = 'Bridge');
SET @890Gun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 && subgroup_label = 'Gunnery');
SET @890Med = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 && subgroup_label = 'Medical');
SET @890Eng = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 && subgroup_label = 'Engineering');
SET @890Sec = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @890 && subgroup_label = 'Security');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@890Bridge, @pilot),
        (@890Bridge, @Copilot),
        (@890Gun, @turretOp),
        (@890Gun, @turretOp),
        (@890Med, @medic),
        (@890Eng, @engTech),
        (@890Sec, @marine),
        (@890Sec, @marine);

#Retaliator positions and roles -----------------------------------------------------------------------
# (@ret, NULL, 'Retaliator', 9),
# (@ret, NULL, 'Bridge', 2),
# (@ret, NULL, 'Gunnery', 5),
# (@ret, NULL, 'Engineering', 1),
# (@ret, NULL, 'Weapons Station', 1),

# @ret is id_template from crew template OR template_id from crew_subgroup_template

SET @retBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret && subgroup_label = 'Bridge');
SET @retGun = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret && subgroup_label = 'Gunnery');
SET @retEng = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret && subgroup_label = 'Engineering');
SET @retWeapSt = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @ret && subgroup_label = 'Weapons Station');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@retBridge, @pilot),
        (@retBridge, @Copilot),
        (@retGun, @turretOp),
        (@retGun, @turretOp),
        (@retGun, @turretOp),
        (@retGun, @turretOp),
        (@retGun, @turretOp),
        (@retEng, @engTech),
        (@retWeapSt, @weapsTech);

#Perseus positions and roles --------------------------------------------------------------------------
# (@seus, NULL, 'Perseus', 7),
# (@seus, NULL, 'Bridge', 3),
# (@seus, NULL, 'Gunnery', 2),
# (@seus, NULL, 'Engineering', 1),
# (@seus, NULL, 'Weapons Station', 1),

# @seus is id_template from crew template OR template_id from crew_subgroup_template

SET @perseusBridge = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @seus && subgroup_label = 'Bridge');
SET @perseusGun = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @seus && subgroup_label = 'Gunnery');
SET @perseusEng = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @seus && subgroup_label = 'Engineering');
SET @perseusWeapSt = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @seus && subgroup_label = 'Weapons Station');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@perseusBridge, @pilot),
        (@perseusBridge, @Copilot),
        (@perseusBridge, @turretOp),
        (@perseusGun, @turretOp),
        (@perseusGun, @turretOp),
        (@perseusEng, @engTech),
        (@perseusWeapSt, @weapsTech);

#Carrack positions and roles --------------------------------------------------------------------------
# (@carr, NULL, 'Carrack', 6),
# (@carr, NULL, 'Bridge', 3),
# (@carr, NULL, 'Gunnery', 3),
# (@carr, NULL, 'Science & Exploration', 1),
# (@carr, NULL, 'Engineering', 1),

# @carr is id_template from crew template OR template_id from crew_subgroup_template

SET @carrBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @carr && subgroup_label = 'Bridge');
SET @carrGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr && subgroup_label = 'Gunnery');
SET @carrSci = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr && subgroup_label = 'Science & Exploration');
SET @carrEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @carr && subgroup_label = 'Engineering');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@carrBridge, @pilot),
        (@carrBridge, @Copilot),
        (@carrBridge, @Copilot),
        (@carrGun, @turretOp),
        (@carrGun, @turretOp),
        (@carrGun, @turretOp),
        (@carrSci, @senseOp),
        (@carrEng, @engTech);

#Reclaimer positions and roles ------------------------------------------------------------------------
# (@claimer, NULL, 'Reclaimer', 5),
# (@claimer, NULL, 'Bridge', 1),
# (@claimer, NULL, 'Claw Operator', 1),
# (@claimer, NULL, 'Salvage Operator', 2),
# (@claimer, NULL, 'Cargo', 1),

# @claimer is id_template from crew template OR template_id from crew_subgroup_template

SET @claimerBridge = (SELECT id_template_subgroup
                      FROM crew_subgroup_template
                      WHERE template_id = @claimer && subgroup_label = 'Bridge');
SET @claimerClaw = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @claimer && subgroup_label = 'Claw Operator');
SET @claimerSalv = (SELECT id_template_subgroup
                    FROM crew_subgroup_template
                    WHERE template_id = @claimer && subgroup_label = 'Salvage Operator');
SET @claimerCargo = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @claimer && subgroup_label = 'Cargo');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@claimerBridge, @pilot),
        (@claimerClaw, @utilOp),
        (@claimerSalv, @utilOp),
        (@claimerSalv, @utilOp),
        (@claimerCargo, @cargoTech);

#Generic Large positions and roles --------------------------------------------------------------------
# (@genl, NULL, 'Generic Large', 8),
# (@genl, NULL, 'Bridge', 3),
# (@genl, NULL, 'Gunnery', 2),
# (@genl, NULL, 'Engineering', 1),
# (@genl, NULL, 'Utility', 2),

# @genl is id_template from crew template OR template_id from crew_subgroup_template

SET @genlBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @genl && subgroup_label = 'Bridge');
SET @genlGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genl && subgroup_label = 'Gunnery');
SET @genlEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genl && subgroup_label = 'Engineering');
SET @genlUtil = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @genl && subgroup_label = 'Utility');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@genlBridge, @pilot),
        (@genlBridge, @Copilot),
        (@genlBridge, @turretOp),
        (@genlGun, @turretOp),
        (@genlGun, @turretOp),
        (@genlEng, @engTech),
        (@genlUtil, @utilOp),
        (@genlUtil, @utilOp);

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
                   WHERE template_id = @valk && subgroup_label = 'Bridge');
SET @valkGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk && subgroup_label = 'Gunnery');
SET @valkEng = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk && subgroup_label = 'Engineering');
SET @valkGrnd = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @valk && subgroup_label = 'Ground Assault');
SET @valkMed = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @valk && subgroup_label = 'Medical');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@valkBridge, @pilot),
        (@valkGun, @turretOp),
        (@valkGun, @turretOp),
        (@valkEng, @engTech),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkGrnd, @Marine),
        (@valkMed, @Medic);


#600i positions and roles -----------------------------------------------------------------------------
# (@600, NULL, '600i', 5),
# (@600, NULL, 'Bridge', 2),
# (@600, NULL, 'Scanning/Navigation', 2),
# (@600, NULL, 'Security', 1),

# @600 is id_template from crew template OR template_id from crew_subgroup_template

SET @600Bridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @600 && subgroup_label = 'Bridge');
SET @600Nav = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @600 && subgroup_label = 'Scanning/Navigation');
SET @600Sec = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @600 && subgroup_label = 'Security');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@600Bridge, @pilot),
        (@600Bridge, @Copilot),
        (@600Nav, @nav),
        (@600Nav, @senseOp),
        (@600Sec, @turretOp);

#Generic Medium positions and roles ------------------------------------------------------------------
# (@genm, NULL, 'Generic Medium', 5),
# (@genm, NULL, 'Bridge', 2),
# (@genm, NULL, 'Gunnery', 2),
# (@genm, NULL, 'Utility', 1),

# @genm is id_template from crew template OR template_id from crew_subgroup_template

SET @genmBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @genm && subgroup_label = 'Bridge');
SET @genmGun = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @genm && subgroup_label = 'Gunnery');
SET @genmUtil = (SELECT id_template_subgroup
                 FROM crew_subgroup_template
                 WHERE template_id = @genm && subgroup_label = 'Utility');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@genmBridge, @pilot),
        (@genmBridge, @Copilot),
        (@genmGun, @turretOp),
        (@genmGun, @turretOp),
        (@genmUtil, @utilOp);

#MSR positions and roles -----------------------------------------------------------------------------
# (@msr, NULL, 'Mercury Star Runner', 5),
# (@msr, NULL, 'Bridge', 2),
# (@msr, NULL, 'Scanning/Data', 1),
# (@msr, NULL, 'Gunnery', 2),

# @msr is id_template from crew template OR template_id from crew_subgroup_template

SET @msrBridge = (SELECT id_template_subgroup
                  FROM crew_subgroup_template
                  WHERE template_id = @msr && subgroup_label = 'Bridge');
SET @msrScan = (SELECT id_template_subgroup
                FROM crew_subgroup_template
                WHERE template_id = @msr && subgroup_label = 'Scanning/Data');
SET @msrGun = (SELECT id_template_subgroup
               FROM crew_subgroup_template
               WHERE template_id = @msr && subgroup_label = 'Gunnery');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@msrBridge, @pilot),
        (@msrBridge, @Copilot),
        (@msrScan, @senseOp),
        (@msrGun, @turretOp),
        (@msrGun, @turretOp);

#Redeemer postions and roles -------------------------------------------------------------------------
# (@redeemer, NULL, 'Redeemer', 4),
# (@redeemer, NULL, 'Bridge', 2),
# (@redeemer, NULL, 'Gunnery', 2),

# @redeemer is id_template from crew template OR template_id from crew_subgroup_template

SET @redeemBridge = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @redeemer && subgroup_label = 'Bridge');
SET @redeemGun = (SELECT id_template_subgroup
                     FROM crew_subgroup_template
                     WHERE template_id = @redeemer && subgroup_label = 'Gunnery');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@redeemBridge, @pilot),
        (@redeemBridge, @Copilot),
        (@redeemGun, @turretOp),
        (@redeemGun, @turretOp);

#MOLE positions and roles ----------------------------------------------------------------------------
# (@mole, NULL, 'MOLE', 5),
# (@mole, NULL, 'Bridge', 1),
# (@mole, NULL, 'Mining Operators', 3),
# (@mole, NULL, 'Utility', 1),

# @mole is id_template from crew template OR template_id from crew_subgroup_template

SET @moleBridge = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole && subgroup_label = 'Bridge');
SET @moleMine = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole && subgroup_label = 'Mining Operators');
SET @moleUtil = (SELECT id_template_subgroup
                   FROM crew_subgroup_template
                   WHERE template_id = @mole && subgroup_label = 'Utility');

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES  (@moleBridge, @pilot),
        (@moleMine, @utilOp),
        (@moleMine, @utilOp),
        (@moleMine, @utilOp),
        (@moleUtil, @utilOp);
#GENERIC SMALL positions and roles -------------------------------------------------------------------
# (@gens, NULL, 'Generic Small', 1);

# @gens is id_template from crew template OR template_id from crew_subgroup_template

INSERT INTO crew_position_template (subgroup_template_id, position_role_id)
VALUES (@sParent, @pilot);