var path = require('path');
var rootPath = path.normalize(__dirname + '/../../');

module.exports = {
  development: {    
    rootPath: rootPath,
    port: process.env.PORT || 3030,
    google_search_key: 'AIzaSyCq1hkDNgcwG2YLo1gtmBJgUROL94b5L1I',
    google_search_cx:'017341476643384181453:sh3_hjw1xma',
    google_query_URL: 'https://www.googleapis.com/customsearch/v1?q=',
    query_result_max: 100,
    bing_account_key: 'hi5LIvAVghLrU2pq7b3VYYwcXt0H09uAcX49y8Ag1mo',
    bing_query_URL: 'https://api.datamarket.azure.com/Bing/Search/v1/Web',
    search_query_ws : 'http://localhost:8989/commercesearch/api/search?query=',
    query_expansion_ws: 'http://localhost:8989/commercesearch/api/queryexpansion?query=',
    query_cluster_ws: 'http://localhost:8989/commercesearch/api/clustering?query='
  },
  production: {
    rootPath: rootPath,    
    port: process.env.PORT || 80    
  }
}