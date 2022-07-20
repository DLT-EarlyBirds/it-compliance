# Aud-IT
A repository containing the code implementation for DLT4PI practical course for IT Compliance

The goal of _Aud-IT_ is to make IT compliance easier for financial service providers. It optimizes the auditing processes for auditors and supervisory authorities.

The project consists of three main modules:

![modules](docs/media/modules.png "Title")

## Requirements

_Aud-IT_ was tested on Windows 10 and Mac OS with 64-Bit CPU. To be able to run it your system needs to have these requirements installed:

- Java 8
- Docker Desktop >= 4.8.1
- Docker Engine Version >= 20.10.14
- ...



## Quick Start

#### Clone this repository:

```console
$ git clone https://github.com/DLT-EarlyBirds/it-compliance.git
```

#### Run the CorDapp on Docker Compose
To run the network based on docker compose you need to first start the docker deamon by starting Docker Desktop Community Edition.
Then you can simply run the command `docker compose up` in the directory `it-compliance/build/nodes`to deploy the network.


#### Start the middleware application:
 To start the middleware spring boot application you have to find out the RPC ports for each node's docker container and copy it to the variable `--config.rpc.port` in `clients/build.gradle`. 
 
After that start the gradle tasks in `clients/build.gradle` for the servers you want to run. 

#### Start the web application:
...

#### Create a test regulation:
...

## More Details:
If you need more details about the individual components please refer to the respective documentation page:

-   [CorDapp](docs/cordapp.md)
- [Middleware](docs/middleware.md)
- [Web Application](docs/webapp.md)
