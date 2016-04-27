package main

import (
	"encoding/json"
	"flag"
	"io/ioutil"
	"log"
	"os"
	"os/exec"
	"strings"
	"time"

	"github.com/jbrady42/crawl/core"
)

var sleepTime time.Duration
var sleepSeconds = flag.Int("n", 60, "Check every N seconds")
var minWorkers = flag.Int("workers", 35, "Minimum workers")
var workerQueue = flag.Int("worker-queue", 200, "Minimum urls per worker")
var minUrls = flag.Int("urls", 500, "Minimum urls")
var watchOnly = flag.Bool("watch", false, "Only display stats")
var addCmd = flag.String("cmd", "script/next_script.sh", "Command to run")
var statsFile = flag.String("file", "/tmp/crawl_stats", "Stats file")

func main() {
	flag.Parse()
	sleepTime = time.Duration(*sleepSeconds) * time.Second
	log.Println("Start watcher")
	log.Println("Min workers: ", *minWorkers)
	log.Println("Min urls: ", *minUrls)
	log.Println("Watch only: ", *watchOnly)
	watchStats()
}

func watchStats() {
	checkStats()
	for {
		time.Sleep(sleepTime)
		checkStats()
	}
}

func checkStats() {
	stats := crawlStats()
	log.Println(stats)
	outList := findWorkers(stats.WorkerCount)
	if !*watchOnly && (stats.Workers < *minWorkers || stats.Urls < *minUrls || len(outList) > 0) {
		addUrls(outList)
	}
}

func findWorkers(countMap map[string]int) []string {
	list := make([]string, 0)
	for key, count := range countMap {
		if count > *workerQueue {
			list = append(list, key)
		}
	}
	return list
}

func addUrls(list []string) {
	hostListStr := "'" + strings.Join(list, "','") + "'"
	log.Println("enqueue urls")
	log.Println("Full workers:", hostListStr)

	cmd := exec.Command(*addCmd, hostListStr)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err := cmd.Run()
	if err != nil {
		log.Fatal(err)
	}
}

func crawlStats() *core.CrawlStats {
	data, err := ioutil.ReadFile(*statsFile)
	if err != nil {
		log.Fatal("Could not read crawl file", err)
	}

	var res core.CrawlStats
	err = json.Unmarshal(data, &res)
	if err != nil {
		log.Println("Error marshaling stats")
		log.Println(err)
		panic("No stats")
	}
	return &res
}
