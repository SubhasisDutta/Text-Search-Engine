#include <string.h>
#include <fstream>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>

const char * http = "http://";
const char * www = "www.";

const char * allowed_domains[] = {
    "target.com",
    "walmart.com",
    "dillards.com",
    "barnesandnoble.com",
    "homedepot.com"
};

const char * product_filter[] = {
    "/p",
    "/ip/",
    "/p/",
    "/w/",
    "/p/"
};

void printline(std::string const & line)
{
    printf("%s\n", line.c_str());
}

int valid_tld_idx(std::string const & line)
{
    size_t start_pos = 0;
    if (line.find(http) == 0)
    {
        start_pos = strlen(http);
    }

    //didn't need all of this.
    //set out to block ?referrer= stuff
    //but ended up blocking www.*walmart links
    //in the process (when browsing www.walmart)
    for (int i = 0; i < sizeof(allowed_domains) / sizeof(allowed_domains[0]); ++i)
    {
        size_t domain_pos = line.find(allowed_domains[i], start_pos);

        if (domain_pos != std::string::npos)
        {
            if (domain_pos == start_pos)
            {
                return i;
            }

            size_t www_pos = line.find(www);

            if (www_pos != std::string::npos && www_pos + strlen(www) == domain_pos)
            {
                return i;
            }
        }
    }

    return -1;
}

int main()
{
    std::string line;

    while (std::getline(std::cin, line))
    {
        if (line.size() == 0)
        {
            printline(line);
        }
        else 
        {
            int tld_index = valid_tld_idx(line);
            if (tld_index >= 0)
            {
                if (line.find(product_filter[tld_index]) != std::string::npos)
                {
                    printline(line);
                }
            }
        }
    }

    return 0;
}
