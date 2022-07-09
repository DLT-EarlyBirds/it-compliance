package com.compliance.supervisoryauthority.controllers;

import com.compliance.flows.CreateClaimTemplate;
import com.compliance.flows.CreateRegulation;
import com.compliance.flows.CreateRule;
import com.compliance.states.ClaimTemplate;
import com.compliance.states.Regulation;
import com.compliance.states.Rule;
import com.compliance.supervisoryauthority.NodeRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/claims") // The paths for HTTP requests are relative to this base path.
public class SpecificClaimController {
    private final CordaRPCOps proxy;
    private final CordaX500Name me;
    private final static Logger logger = LoggerFactory.getLogger(SpecificClaimController.class);

    public SpecificClaimController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

}