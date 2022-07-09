package com.compliance.supervisoryauthority.controllers;

import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/rule") // The paths for HTTP requests are relative to this base path.
public class RuleController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);

    public RuleController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GetMapping(value = "/rules", produces = APPLICATION_JSON_VALUE)
    private List<StateAndRef<Rule>> rules() {
        return proxy.vaultQuery(Rule.class).getStates();
    }

}