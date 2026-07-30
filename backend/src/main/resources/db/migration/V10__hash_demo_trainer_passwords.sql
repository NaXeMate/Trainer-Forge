-- ========================================
-- V10 - HASH DEMO TRAINER PASSWORDS
-- ========================================

-- The public demo passwords are documented in START_SEQUENCE.md.
UPDATE "trainers"
SET "password_hash" = '$2a$10$kO0tWRdgYZutcuKotxJJpegr86HUZr1ResMuDEkkf6vOQPMqTd50a'
WHERE "id" = 1
  AND "username" = 'red'
  AND "password_hash" = 'hash_red_demo';

UPDATE "trainers"
SET "password_hash" = '$2a$10$6NnIYQxORDNd/qYB9FN/XupBJhmL0fUYGhy6kjhhZCulCDhKSkmVm'
WHERE "id" = 2
  AND "username" = 'leaf'
  AND "password_hash" = 'hash_leaf_demo';

UPDATE "trainers"
SET "password_hash" = '$2a$10$OtRIod2LK68K7VKbD/t3f.bz.W2A.rnsCPB4KSypZChSE7mv2txJ2'
WHERE "id" = 3
  AND "username" = 'liko'
  AND "password_hash" = 'hash_liko_demo';

UPDATE "trainers"
SET "password_hash" = '$2a$10$rEI13WaWsYGeO5MRxxJqCuf06opoC6bL9oFZB0eJhDJ87TQfmZEDm'
WHERE "id" = 4
  AND "username" = 'may'
  AND "password_hash" = 'hash_may_demo';

UPDATE "trainers"
SET "password_hash" = '$2a$10$R9kFDT22acwlz40nA8gj0.fI59fQbtaVSeAGUFfw./6F7aGMNnNFe'
WHERE "id" = 5
  AND "username" = 'rod'
  AND "password_hash" = 'hash_rod_demo';
