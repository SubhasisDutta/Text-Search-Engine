--all links ready to be crawled
create temp table candidates as
select * from sites t 
where t.queued_at is null and 
	t.visited = 'f' and 
	t.ip is not null and 
	t.ip != '' and 
	t.ip != '<nil>' and
	not (t.ip = any(:blacklisted_ips));

--select distinct ips. 
--can't order by random in a distinct call, 
--hence the temp table.
create temp table distinctips as
select distinct ip from candidates;

--filter the amount of IPs returned
create temp table filteredip as
select t.* from candidates t
join (select ip from distinctips order by random() limit 20) as i on i.ip = t.ip;

--select a certain number of links per IP
select x.id, x.url, x.ip from (
    select *, row_number() over (partition by ip order by random()) as r from filteredip
) as x
where x.r <= 2000
order by x.r;
