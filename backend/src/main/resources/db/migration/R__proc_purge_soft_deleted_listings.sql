DROP PROCEDURE IF EXISTS proc_purge_soft_deleted_listings;
CREATE PROCEDURE proc_purge_soft_deleted_listings(IN cutoff_days INT)
BEGIN
    DELETE FROM group_listings
    WHERE is_deleted = 1
      AND deleted_at < (NOW() - INTERVAL cutoff_days DAY);
END;