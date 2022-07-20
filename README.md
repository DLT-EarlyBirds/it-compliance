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

#### Run Network on Docker Compose

To run the network based on docker compose you need to first start the docker deamon by starting Docker Desktop Community Edition.
Then you can simply run the command `docker compose up` in the directory `it-compliance/build/nodes`to deploy the network.

#### Start the middleware application:

...

#### Start the web application:

#### Prerequisite:

https://nodejs.org/en/

#### Change the directory

The webapp resides under the: `it-compliance/clients/src/main/webapp` directory

So, one must change the directory to the webapp directory

#### Install dependencies

`npm i`

### Run the app

`npm run start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

### Connecting to different nodes

Inside the `services/axiosInstance.js` all the nodes that need to be connected are defined. These addresses are retrieved from the gradle file of the client.

Overall, there are four nodes: Financial Supervisory Authority, Brain Finance (Financial Service Provider), Capital Holding (Financial Service Provider) and Auditor

After running the client app, on the top right of the header there is a button "Change Node" which enables the user to switch to different nodes.

#### Create a test regulation:

...

## More Details:

If you need more details about the individual components please refer to the respective documentation page:

- [CorDapp](docs/cordapp.md)
- [Middleware](docs/middleware.md)
- [Web Application](docs/webapp.md)
