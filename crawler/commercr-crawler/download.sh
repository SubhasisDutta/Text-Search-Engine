#!/bin/bash

q_addr=localhost:4150

nsq_tail -topic="download" --channel="url-feed" -nsqd-tcp-address=$q_addr |

crawl download --bad-robot --host --workers 150 --max-bytes=524288 2> log/download.log |

to_nsq -topic="data" -nsqd-tcp-address=$q_addr 
