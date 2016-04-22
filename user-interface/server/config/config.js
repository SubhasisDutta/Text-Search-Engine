var path = require('path');
var rootPath = path.normalize(__dirname + '/../../');

module.exports = {
  development: {    
    rootPath: rootPath,
    port: process.env.PORT || 3030,
    google_search_key: 'AIzaSyDjsz5jJZTOj0jI5sL4UyJ462X0B0vQEk0',
    google_search_cx:'015316648851022853627:d-2vcb0f5l4',
    google_query_URL: 'https://www.googleapis.com/customsearch/v1?q=',
    query_result_max: 100,
    bing_account_key: 'hi5LIvAVghLrU2pq7b3VYYwcXt0H09uAcX49y8Ag1mo',
    bing_query_URL: 'https://api.datamarket.azure.com/Bing/Search/v1/Web'
  },
  production: {
    rootPath: rootPath,    
    port: process.env.PORT || 80    
  }
}