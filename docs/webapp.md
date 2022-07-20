# Start the web application:
The web application is a showcase on how to use the client APIs: 
#### Change the directory:

The webapp resides in the: `it-compliance/clients/src/main/webapp` directory.
You must change the directory to the webapp directory to run the application.

#### Install dependencies
Run the following command in the root directory of the webapp
`npm i`

#### Run the app
Start the webapp local development server:
`npm run start`

Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

#### Connecting to different nodes

Inside the `services/axiosInstance.js` all the clients that need to be connected are defined. These addresses are set in the clients `build.gradle`.

Overall, there are four nodes: Financial Supervisory Authority, Brain Finance (Financial Service Provider), Capital Holding (Financial Service Provider) and the Auditor.

After running the client app, on the top right of the header there is a button "Change Node" which enables the user to switch to different nodes.

#### Create a test regulation:
To automatically fill the network with content you can, press the bootstrap graph endpoint on the Regulation View. To run this bootstraper, please select the Supervisory Authority node.