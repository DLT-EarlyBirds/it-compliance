package com.compliance.auditor.controllers;

import com.compliance.auditor.NodeRPCConnection;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;


/**
 * This controller provides a REST API for the network map cache
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/network") // The paths for HTTP requests are relative to this base path.
public class NetworkController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public NetworkController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    /**
     * Helpers for filtering the network map cache.
     */
    public String toDisplayString(X500Name name) {
        return BCStyle.INSTANCE.toString(name);
    }

    /**
     * If the node is not a notary, return true, otherwise return false.
     *
     * @param nodeInfo The node that we are checking to see if it is a notary.
     * @return A boolean value.
     */
    private boolean isNotary(NodeInfo nodeInfo) {
        return !(proxy.notaryIdentities()
                .stream().noneMatch(nodeInfo::isLegalIdentity));
    }

    /**
     *  This function returns true if the node is the node that we are currently running on
     *
     * @param nodeInfo The node that we are checking to see if it is the node we're connected to.
     * @return The name of the node.
     */
    private boolean isMe(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    /**
     * Check if the node is a network map, return true, otherwise return false.
     *
     * @param nodeInfo The node info of the node that we are trying to connect to.
     * @return The nodeInfo object contains the identity of the node.
     */
    private boolean isNetworkMap(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }


    /**
     * Endpoint that returns the current time of the node that the proxy is connected to
     *
     * @return The current time on the server.
     */
    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    /**
     * Endpoint that returns a string representation of the list of addresses of the node
     *
     * @return The addresses of the node.
     */
    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }


    /**
     * Endpoint to get all legal identities in the network
     *
     * @return The identities of the nodes in the network.
     */
    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    /**
     * Endpoint that returns the platform version of the node
     *
     * @return The platform version of the node.
     */
    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    /**
     * Get all nodes that are not notaries, ourself, or the network map, and return their names as a list
     *
     * @return A list of peers.
     */
    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    /**
     * Endpoint that returns a list of all the notaries on the network
     *
     * @return A list of notaries.
     */
    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    /**
     * Endpoint that returns a string representation of the registered flows
     *
     * @return A list of all the flows that have been registered with the proxy.
     */
    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }


    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    private HashMap<String, String> whoami() {
        HashMap<String, String> myMap = new HashMap<>();
        myMap.put("me", me.toString());
        return myMap;
    }
}