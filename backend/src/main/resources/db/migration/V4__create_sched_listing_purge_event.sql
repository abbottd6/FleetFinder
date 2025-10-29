CREATE EVENT IF NOT EXISTS ev_purge_listings
ON SCHEDULE EVERY 1 DAY
DO CALL proc_purge_soft_deleted_listings(30);