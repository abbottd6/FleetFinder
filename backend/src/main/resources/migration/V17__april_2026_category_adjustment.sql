SET @commerce_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Commerce/Trade');
SET @ship_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat');
SET @fps_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat');
SET @piracy_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy');
SET @hauling_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Hauling/Freight');
SET @mining_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining');
SET @salvage_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage');
SET @exploration_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Exploration');
SET @medical_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Medical');
SET @racing_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Racing');
SET @event_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Event');
SET @other_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Other');

# UPDATE LISTINGS/ARCHIVES/TEMPLATES TO REMOVE BEFORE DELETE

UPDATE group_listing
SET subcategory_id = NULL,
    category_id = @other_id
WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id);

UPDATE listing_template
SET subcategory_id = NULL,
    category_id = @other_id
WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id);

UPDATE listing_archive
SET subcategory_id = NULL,
    category_id = @other_id
WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id);

UPDATE user_custom_notification
SET subcategory_id = NULL,
    category_id = NULL
WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id);


# Remove 'for hire' from medical
# drop event category entirely
# move org event into play_style
# remove fps combat
# Remove ship combat

DELETE FROM gameplay_subcategory
    WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id)
        OR subcategory_name = 'For Hire';

DELETE FROM gameplay_category
    WHERE category_id IN (@event_id, @fps_combat_id, @ship_combat_id);

INSERT INTO play_style (play_style) VALUE ('Org Event');

# add 'Bounty' category

# add category 'Open Sandbox'

# add category 'Instanced/Dungeon'

# add 'General Combat' category

# add 'LF Service' category

# add 'For Hire Service' category

INSERT INTO gameplay_category (category_name)
VALUES
    ('Bounty'),
    ('Open Sandbox'),
    ('Instanced/Dungeon'),
    ('General Combat'),
    ('Looking for Service'),
    ('Service for Hire');

SET @bounty_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Bounty');
SET @sandbox_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Open Sandbox');
SET @instanced_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Instanced/Dungeon');
SET @combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'General Combat');
SET @lf_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Looking for Service');
SET @hire_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Service for Hire');

# add Trade Contract to 'Commerce/Trade'
# add 'Investigation Contract' to 'Exploration'
# add 'Mining contract' to mining
# Add salvage contract to salvage

# add 'Bounty' subcategories:
    # FPS bounty
    # Ship Bounty
    # PvP Bounty

# add 'Open Sandbox' subcategories:
    # bunker raid
    # Contested zone
    # persistent sandbox
    # global event

# add 'Instanced/Dungeon' subcategory:
    # rock breaker

# add 'General Combat' subcategories:
    # General FPS
    # Ship Dueling/Skirmish
    # Arena Commander FPS
    # Arena Commander Ship

# add 'LF Service' subcategories
    # Bounty Hire
    # Medical/Rescue
    # Escort/Security
    # Crafting
    # Refining
    # Transport

# add 'For Hire Service' subcategories:
    # Bounty Hunter
    # Medical/Rescue
    # Escort/Security
    # Crafting
    # Refining
    # Transport

INSERT INTO gameplay_subcategory (subcategory_name, category_id)
VALUES
    ('Trade Contract', @commerce_id),
    ('Investigation Contract', @exploration_id),
    ('Mining Contract', @mining_id),
    ('Salvage Contract', @salvage_id),
    ('FPS Bounty', @bounty_id),
    ('Ship Bounty', @bounty_id),
    ('PvP Bounty', @bounty_id),
    ('Bunker/Outpost Raid', @sandbox_id),
    ('Contested Zone', @sandbox_id),
    ('Persistent Sandbox', @sandbox_id),
    ('Global Event', @sandbox_id),
    ('Rock Breaker', @instanced_id),
    ('General FPS', @combat_id),
    ('Ship Dueling/Skirmish', @combat_id),
    ('Arena Commander FPS', @combat_id),
    ('Arena Commander Ship', @combat_id),
    ('PvP Bounty Hunter', @lf_id),
    ('Medical/Rescue', @lf_id),
    ('Escort/Security', @lf_id),
    ('Crafting', @lf_id),
    ('Refining', @lf_id),
    ('Transport', @lf_id),
    ('Bounty Hunter', @hire_id),
    ('Medical/Rescue', @hire_id),
    ('Escort/Security', @hire_id),
    ('Crafting', @hire_id),
    ('Refining', @hire_id),
    ('Transport', @hire_id);