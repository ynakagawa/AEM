package com.adobe.acs.sample.workflow;

/**
 * Created with IntelliJ IDEA.
 * User: ynaka
 * Date: 8/19/14
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.*;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Agent Filter Replication workflow process that sets an <code>agent</code> property to the payload based on the process argument value.
 */
@Component
@Service
public class MyProcess implements WorkflowProcess {

    @Property(value = "An replication workflow process where you can pass the agent filter as an argument.")
    static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
    @Property(value = "Adobe")
    static final String VENDOR = Constants.SERVICE_VENDOR;
    @Property(value = "PREVIEW - Replication by Agent name Process")
    static final String LABEL="process.label";

    /**
     * the logger
     */
    private static final Logger log = LoggerFactory.getLogger(MyProcess.class);

    public static final String TYPE_JCR_PATH = "JCR_PATH";
    @Reference
    protected Replicator replicator;

    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {

        WorkflowData workflowData = item.getWorkflowData();
        final String agentName = args.get("PROCESS_ARGS", "false");
        log.info("Starting MyProcess " + agentName);
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            String path = workflowData.getPayload().toString() + "/jcr:content";
            try {
                ReplicationOptions opts = new ReplicationOptions();
                opts.setFilter(new AgentFilter(){
                    public boolean isIncluded(final Agent agent) {
                        log.info(agent.getId());
                        return agentName.equals(agent.getId());
                    }
                });
                Session jcrSession = workflowSession.adaptTo(Session.class);
                replicator.replicate(jcrSession,ReplicationActionType.ACTIVATE,path,opts);
                log.info("Replicated " + path + " to agent " + agentName);
                Node node = (Node) jcrSession.getItem(path);
                if (node != null) {
                    node.setProperty( agentName, true );
                    jcrSession.save();
                }
                log.info("Ending MyProcess");
            } catch (ReplicationException re) {
                log.error(re.getMessage(), re);
            } catch (RepositoryException e) {
                log.error(e.getMessage(), e);
                throw new WorkflowException(e.getMessage(), e);
            }
        }
    }

}

