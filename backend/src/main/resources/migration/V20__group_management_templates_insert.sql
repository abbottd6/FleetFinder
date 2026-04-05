SET @cap = 'Capital';
SET @lrg = 'Large';
SET @md = 'Medium';
SET @sml = 'Small';

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
SET @mole = (SELECT id_template FROM crew_template WHERE template_label = 'MOLE');
SET @gens = (SELECT id_template FROM crew_template WHERE template_label = 'Generic Small');

INSERT INTO crew_subgroup_template (template_id, parent_subgroup_id, subgroup_label, intended_subgroup_size)
VALUES
    #IDRIS
    (@idris, NULL, 'Idris', 28),
    (@idris, NULL, 'Bridge', 5),
    (@idris, NULL, 'Gunnery', 6),
    (@idris, NULL, 'Engineering', 3),
    (@idris, NULL, 'Medical', 2),
    (@idris, NULL, 'Fighter Wing', 6),
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
    (@gencap, NULL, 'Fighter Wing', 2),
    (@gencap, NULL, 'Marine Detachment', 6),
    #HAMMERHEAD
    (@hh, NULL, 'Hammerhead', 11),
    (@hh, NULL, 'Bridge', 3),
    (@hh, NULL, 'Gunnery', 6),
    (@hh, NULL, 'Engineering', 2),
    #A2 HERCULES
    (@a2, NULL, 'A2 Hercules', 8),
    (@a2, NULL, 'Bridge', 3),
    (@a2, NULL, 'Engineering',1),
    (@a2, NULL, 'Ground Assault', 4),
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
    (@carr, NULL, 'Carrack', 6),
    (@carr, NULL, 'Bridge', 3),
    (@carr, NULL, 'Science & Exploration', 2),
    (@carr, NULL, 'Engineering', 1),
    #Reclaimer
    (@claimer, NULL, 'Reclaimer', 5),
    (@claimer, NULL, 'Bridge', 1),
    (@claimer, NULL, 'Claw Operator', 1),
    (@claimer, NULL, 'Salvage Operator', 2),
    (@claimer, NULL, 'Cargo', 1),
    #Generic Large
    (@seus, NULL, 'Generic Large', 8),
    (@seus, NULL, 'Bridge', 3),
    (@seus, NULL, 'Gunnery', 2),
    (@seus, NULL, 'Engineering', 1),
    (@seus, NULL, 'Utility', 2),
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
    #MOLE
    (@mole, NULL, 'MOLE', 5),
    (@mole, NULL, 'Bridge', 1),
    (@mole, NULL, 'Mining Operators', 3),
    (@mole, NULL, 'Utility', 1),
    #GENERIC SMALL
    (@gens, NULL, 'Generic Small', 2);

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
SET @moleParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'MOLE');
SET @sParent = (SELECT id_template_subgroup FROM crew_subgroup_template WHERE subgroup_label = 'Generic Small');

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
SET parent_subgroup_id = @moleParent
WHERE template_id = @mole && subgroup_label <> 'MOLE';
