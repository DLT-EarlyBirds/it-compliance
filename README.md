# it-compliance
A repository containing the code implementation for DLT4PI practical course

## Running the network
The compliance network for the prototype is based on five dockerized nodes that are communicating via a shared network.
We are using docker compose to run the network and the client applications, as well as the CorDapps within. 

### Deploying the network
To deploy the network you need to run the gradle task called `deployNodes`. The task creates the directory `/it-compliance/build`.
Located inside this directory is a directory called `/it-compliance/build/nodes` containing all of the peer nodes of the prototype network, as well as the file `/it-compliance/build/nodes/docker-compose.yaml`.

#### Run Network on Docker Compose
To run the network based on docker compose you need to first start the docker deamon by starting Docker Desktop Community Edition. 
Then you can simply run the command `docker compose up` in the directory `it-compliance/build/nodes`to deploy the network. 

#### Interacting with the nodes
You can interact with the nodes via a client or via the commandline. As we have not yet developed a client you can use the [Node Expolorer](https://docs.r3.com/en/platform/corda/4.6/open-source/node-explorer.html) as a client, or log into the node via ssh.

##### Logging in via ssh
To log in via ssh you need to run the following command in your preferred shell (assuming you have ssh installed):
```
ssh -p <NODE_PORT> localhost -l user1
```
Replace `<NODE_PORT>` with the port of the node you want to connect to. You can find the respective nodes in the `build.gradle` file located at `it-compliance/build.gradle`. 
The username is already provided with `user1`, the respective password is `test`.
