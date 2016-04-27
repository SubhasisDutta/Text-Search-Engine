#!/bin/bash

psql -At -F $'\t' $DATABASE -v blacklisted_ips=array[$1] < $next_script | tail -n +4 > data/next_tmp
