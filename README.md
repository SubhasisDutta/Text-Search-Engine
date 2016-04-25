# text-search
A domain Specific search engine along with comparison with search results from Google and Bing.

Steps to Run the System:

1.	Generate the Crawler Data - Stephen Pls fill this out...


2.	Generate the Pages break up
	a.	In file \com\irsearch\commercesearch\init\breakUpPage.java
		Change INPUT_DAT_FOLDER and
		OUTPUT_SEPERATE_DATA_FILES
	b.	Run the file breakUpPage.java to generate the pages. This removes the duplicate pages that may be there in the crawled files
3.	Generate the index
	a.	In file \com\irsearch\commercesearch\init\IndexFiles.java
		Change INDEX_DIRECTORY_PATH and 
		inputDocumentDirectoryPath
	b.	Run the file IndexFiles.java to generate the index. 
4.	Generate the Cluster – Wayatt please write this steps


5.	Run the HTTP Web Server  
	a.	Run the com\irsearch\commercesearch\Main.java
	b.	Make sure this points to SearchDAO
6.	Run the UI Server
	a.	Make sure Node.js, bower and Git is installed in the system
	b.	Check id node dependency are installed in text-search\user-interface\node_modules – 
		i.	Run npm install in user-interface folder
		ii.	$npm install
	c.	Check the client dependency present in text-search\user-interface\public\vendor
		i.	Run bower to get all client dependency
		ii.	$bower install
	d.	Run the Server – node server.js

In browser open http://localhost:3030/







mvn install:install-file -Dfile=C:\Workspace\Github\text-search\irsearch\lib\kstem-3.4.jar -DgroupId=org.lemurproject -DartifactId=kstem -Dversion=3.4 -Dpackaging=jar