#include <string.h>
#include <fstream>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <vector>

const char * http = "http://";
const char * www = "www.";

const char * allowed_domains[] = {
    "target.com",
    "walmart.com",
    "dillards.com",
    "barnesandnoble.com",
    "homedepot.com"
};

std::vector<std::vector<std::string>> product_filter_whitelist;

void init_filters()
{
	std::vector<std::string> target; target.push_back("/p/"); target.push_back("/c/");
	std::vector<std::string> walmart; walmart.push_back("/ip/"); walmart.push_back("/browse/"); walmart.push_back("/cp/");
	std::vector<std::string> dillards; dillards.push_back("/p/"); dillards.push_back("/c/");
	std::vector<std::string> barnesandnoble; barnesandnoble.push_back("/w/");
	std::vector<std::string> homedepot; homedepot.push_back("/p/");

	product_filter_whitelist.push_back(target);
	product_filter_whitelist.push_back(walmart);
	product_filter_whitelist.push_back(dillards);
	product_filter_whitelist.push_back(barnesandnoble);
	product_filter_whitelist.push_back(homedepot);
}

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
	init_filters();

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
				for (const std::string & filter : product_filter_whitelist[tld_index])
				{
                	if (line.find(filter) != std::string::npos)
                	{
                   	 	printline(line);
						break;
                	}
				}
            }
        }
    }

    return 0;
}
