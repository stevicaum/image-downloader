# HOW TO RUN
1. Use java corretto 17 and run maven(v3) command ``mvn clean install``
3. Run main class ``com.dmi.imagedownloader.ImageDownloaderApplication``
6. Swagger documentation: http://localhost:8080/swagger-ui/index.html

# HOW TO RUN TESTS
1. Integration and Unit Tests: Run Maven build with the command ``mvn clean install``

# CALL EXAMPLES
1. POST
   ``curl -X 'POST' \
   'http://localhost:8080/images-download' \
   -H 'accept: */*' \
   -H 'Content-Type: application/json' \
   -d '{
   "date": "2024-05-28",
   "targetFolder": "/Users/test/Documents/images-download",
   "archiveType": "NATURAL",
   "imageType": "PNG"
   }'``

2. GET
   ``GET' \
   'http://localhost:8080/images-download/states' \
   -H 'accept: */*``

3. GET
   ``curl -X 'GET' \
   'http://localhost:8080/images-download/states/e6c49347-a2b0-41e3-8dde-eb29b7071231' \
   -H 'accept: */*'``