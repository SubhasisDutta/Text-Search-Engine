#!/bin/bash

in_file=data/next_tmp

db_tool queue < $in_file |

to_nsq -topic="download" -nsqd-tcp-address=127.0.0.1:4150
