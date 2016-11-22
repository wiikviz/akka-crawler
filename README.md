configuration
=============
Edit application.conf file for set properties:
* crawler.nrOfInstances - the number of running parallel crawlers instance
* crawler.download_folder - the downloads folder
* crawler.retry_count - the number of failed attempts 

run
===
```
sbt ~run
```

set supported domain names
==========================
Only these domains will be used in the crawl
```
curl -H "Content-Type: application/json" -X POST -d '["wikipedia.org", "en.wikipedia.org"]' http://localhost:9000/domains
```

run crawl
============
```
curl -H "Content-Type: application/json" -X POST -d '"http://wikipedia.org"' http://localhost:9000/page

or 

curl -H "Content-Type: text/plain" -X POST -d 'http://wikipedia.org' http://localhost:9000/page
```

get statistics
=================
```
curl http://localhost:9000/status
```

pause crawl
==============
```
curl -X POST http://localhost:9000/pause
```

resume crawl
===============
```
curl -X POST http://localhost:9000/resume
```