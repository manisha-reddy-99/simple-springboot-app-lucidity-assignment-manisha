# Prerequisities
JDK 11
Docker

# How bring the mockservice up
cd mockserver  
docker compose up  
the mocke server will start at port 1080

# How bring the service up
./mvnw clean install -DskipTests  
java -jar target/simple-springboot-app-0.0.1-SNAPSHOT.jar  
The server will start at port 9001

# How to run the tests
./mvnw test  

# Lucidity Assignment - sample-cart-offer
Created new util for adding offer, getting user segment by hitting the mock api, add offer without authentication, get segments methods.
CartOfferTestUtils.java (Test Util)

Created four test case files which will map to test cases designed in excel sheet.
Funtional tests, security tests, performance tests, validation and misc tests.

CartOfferFunctionalTests.java,CartOfferMiscIntegrationTests.java, CartOfferSecurityPerformanceTests.java, CartOfferValidationTests.java

Updated the mock json file for adding user segments.

initializer.json

Added excel sheet in test/resources directory
cart_offer_testcases.xlsx


