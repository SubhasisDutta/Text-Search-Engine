#!/bin/bash

while :
do

psql -At -F $'\t' $DATABASE < $next_script | tail -n +4 | db_tool queue | crawl download --host --workers=25 2>>dldebug.dat | tee -a out.dat >(db_tool pages >/dev/null) | crawl extract | urlfilter | tee alinks.dat | db_tool links | crawl resolve --workers=10 | db_tool resolve

[ ! -f ./stop ] || break

done
