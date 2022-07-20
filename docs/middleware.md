# Spring Boot Middleware
The middleware consists of three spring boot applications that define REST APIs to interact with the network from the perspective of each of the three parties, financial service provider, auditor and financial supervisory authority.

The code contains a detailed [API specification](/clients/src/main/java/com/compliance/) with comments on each endpoint of the controllers. When you run on of the client applications, you can invoke these API endpoints via HTTP requests. 

For testing and interacting with the middleware layer, we provide [Postman](https://www.postman.com/) API collections that can be found [here](/postman).

## Running the middleware
To run the middleware you first need build the test network and the cordapp, and deploy them. For this follow the guide in the [Readme](/README.md).

When the network is up and running, you need to copy the rpc host ports mapped by docker to the [clients build.gradle](/clients/build.gradle). As Dockerform is not supporting fixed port mapping for rpc ports yet, we need to do this. 

Then simply run the individual gradle tasks for the clients. 

## Know bugs
When starting multiple clients simultaneously, sometimes one of the clients crashes, because the class loader is blocked. Simply restart the failed client and you're good. 
