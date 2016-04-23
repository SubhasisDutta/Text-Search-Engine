SELECT
 x.id, x.url, x.ip
FROM (
 SELECT
   ROW_NUMBER() OVER (PARTITION BY ip) AS r, t.*
 FROM
   sites t WHERE t.queued_at IS NULL AND t.visited = 'f' AND t.ip IS NOT NULL AND t.ip != '' AND t.ip != '<nil>'
   ) x
JOIN
   (SELECT ip, row_number() OVER(ORDER BY RANDOM()) FROM (SELECT DISTINCT ip FROM sites) as a) as b
ON x.ip = b.ip
--JOIN
--   (SELECT ROW_NUMBER() OVER (PARTITION BY tld) AS r, d.* FROM sites d) tlds
--ON tlds.id = x.id
WHERE x.r <= 100 --pages per ip
--AND tlds.r <= 25 --max tlds
ORDER BY b.row_number;
