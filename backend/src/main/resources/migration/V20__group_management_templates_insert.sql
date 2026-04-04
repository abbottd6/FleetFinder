SET @cap = 'Capital';
SET @lrg = 'Large';
SET @md = 'Medium';
SET @sml = 'Small';

INSERT INTO crew_template (template_label, template_category)
VALUES('Idris-M', @cap),
      ('Idris-P', @cap),
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
      ('Constellation', @md),
      ('MOLE', @md),
      ('Generic Medium Combat', @md),
      ('Generic Medium Utility', @md),
      ('Generic Small', @sml);

SET @idrism = (SELECT id_template FROM crew_template WHERE template_label = 'Idris-M');
SET @idrisp = (SELECT id_template FROM crew_template WHERE template_label = 'Idris-P');
SET @polaris = (SELECT id_template FROM crew_template WHERE template_label = 'Polaris');
SET @hh = (SELECT id_template FROM crew_template WHERE template_label = 'Hammerhead');



INSERT INTO crew_subgroup_template (template_id, parent_subgroup_id, subgroup_label, intended_subgroup_size)
VALUES
    #IDRIS-M
    (@idrism, NULL, 'Idris-M', 28),
    (@idrism, NULL, 'Bridge', 4),
    (@idrism, NULL, 'Gunnery', 6),
    (@idrism, NULL, 'Engineering', 4),
    (@idrism, NULL, 'Fighter Wing', 6),
    (@idrism, NULL, 'Marine Detachment', 8),
    #IDRIS-P
    (@idrisp, NULL, 'Idris-P', 28),
    (@idrisp, NULL, 'Bridge', 4),
    (@idrisp, NULL, 'Gunnery', 8),
    (@idrisp, NULL, 'Engineering', 4),
    (@idrisp, NULL, 'Fighter Wing', 6),
    (@idrisp, NULL, 'Marine Detachment', 6),
    #HAMMERHEAD
    (@hh, NULL, 'Hammerhead', 9),
    (@hh, NULL, 'Gunnery', 6),
    #POLARIS
    (@polaris, NULL, 'Polaris', 12),
    (@polaris, NULL, 'Bridge', 3),
    (@polaris, NULL, 'Gunnery', 4),
    (@polaris, NULL, 'Engineering', 3),
    (@polaris, NULL, 'Fighter Wing', 2),




