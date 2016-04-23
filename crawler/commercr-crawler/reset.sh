#!/bin/bash

psql -At -F $'\t' $DATABASE < deleteall && db_tool links < mainlinks && db_tool resolve < mainresolved 
