Adding this as an additional file to prevent overwriting someone else's changes to the docx file.

Questions:

What you learned?

    The overall flow of a search engine as well as the reason why Google employs so many people.  I also didn't realize the amount of irrelevant data you have to find a way to work around.  On pretty much every stage of the process, data was introduced that was just not needed for the search engine.  We had to find ways to slim down the amount of data to a manageable amount.  In addition, even weka, which is widely seen as a standard way to do data analysis and clustering, struggled with the 100k+ pages we had, causing me to dive into the weka documentation and find the settings that allowed us to trim the data down more.  By the end of this trial and error process, I had found ways to trim down the clustering data set to just what was needed to provide additional relevant results.

What was your experience?

    Lots of data flow experience.  Figuring out what was needed by whom and the best and most sensible way to get it to them.  In addition, we had to find ways to see everything whole picture because information not necessarily needed by one part may be important for others lated down the pipeline.

What were difficulties you faced?

    Run time efficiencies.  Any time data was needed quickly, we were either unable or unwilling to trust the weka core modules to deliver the results in the time frame I needed.

How did you resolve them?

    By building custom data structures that only contained the needed data in an easy to parse format, we were able to get our results as quickly as possible.



5. Clustering

    Using weka, I prepared the data by removing stopwords and normalizing the data results.  In addition, word counts were transformed into IDF values.  A custom stop words list was used containing a large amount of stop words as well as commerce specific words that brought no relevance to the clusters.

    a. I used a basic k-means model using Euclidean Distance with a max of 500 iterations.  Data was prepared as above.  I found with 100 clusters, I was able to use those as broad categories in order to introduce additional results to the final results as well as re-order the new results.

        i. 100

    b. A relevance model was created to be used at runtime with the received search results.  We took the rank score (result set size / rank) and added to that the cluster score (distance to centroid / closest distance to centroid).  This allowed the top results to be similar with new results added near the end that may be more relevant than the lower ranked results.  In addition, additional results from each cluster were added.  Every time a cluster appeared, the top results were either added or the rank score added to the new result's current score.

    c. See 5.b.

    d. Both the new result rankings and k-means clusters used in the results were given to the front end.

    e. The hierarchical models were mostly used in a trial and error fashion to curate the data and choose the important attributes we used in the final search engine.  After building each cluster, I looked at what each level was being split on and determining attributes.  For the first few iterations, the words used for splitting weren't stop words exactly, but unimportant words for a commerce site (like "free shipping").  After adding to the stop words list with these terms, each clustering model (k-means and hierarchy) using this data set provided better results and more relevant clusters.

    f. ~100 per agglomerative clustering method.

    g. The improved cluster titles were generated in part by the hierarchical clusters and they were provided to the front end.

    h. 500 randomly generated query were used along with 25 manual queries.

    i. See 5.e.

    j. The random queries were used to test and 25 manual queries were based on what I think people would search for online.

    k. TODO