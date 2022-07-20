# Run the client app

## Download node.js

https://nodejs.org/en/

## Install dependencies

`npm i`

### Run the app

`npm run start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

### Connecting to different nodes

Inside the `services/axiosInstance.js` all the nodes that need to be connected are defined. These addresses are retrieved from the gradle file of the client.

Overall, there are four nodes: Financial Supervisory Authority, Brain Finance (Financial Service Provider), Capital Holding (Financial Service Provider) and Auditor

After running the client app, on the top right of the header there is a button "Change Node" which enables the user to switch to different nodes.
