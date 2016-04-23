#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>
#include <ctime>

#define SCRIPT "test.sh" //runner.sh
#define STAT_FILE "/tmp/crawlstats"
#define SLEEP_TIME 5 //5 * 60, seconds
#define MAX_IDLE_TIME 10 //5 * 60, seconds

void spawn();

int main()
{
    spawn();
    return 0;
}

void spawn()
{
    pid_t pid = fork();

    if (pid == 0)
    {
        //child
        int execerr = execl("/bin/bash", SCRIPT, (char *)NULL);
        printf("Error execing %s: %s\n", SCRIPT, strerror(errno));
    }
    else if (pid > 0)
    {
        //parent
        printf("child pid: %ld\n", (long)pid);
        while (true)
        {
            sleep(SLEEP_TIME);
     
            struct stat st;
            int err = stat(STAT_FILE, &st);
            if (err)
            {
                printf("Error stat-ing /tmp/crawlstats. Maybe it's angry. %d\n", err);
                continue;
            }
         
            time_t current_time = time(nullptr);

            printf("Running\n");

            if (current_time > st.st_mtime && difftime(current_time, st.st_mtime) > MAX_IDLE_TIME)
            { 
                //the crawler is deadlocked. 
                kill(pid, 9); //9 for kill
                spawn(); //eventually a stackoverflow. oh well.
                return; //wondering if this is getting back here
            }
        }
    }
    else
    {
        //wtf
        printf("Fork failed because I got up on the wrong side of the bed.\n");
    }
}
