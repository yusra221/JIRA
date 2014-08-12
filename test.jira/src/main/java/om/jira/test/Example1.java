package om.jira.test;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class Example1 {

    public static void main(String[] args) throws URISyntaxException {
        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        
        
        final java.net.URI jiraServerUri = new  java.net.URI("http://localhost:8090/jira");
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "yourusername", "yourpassword");
        final NullProgressMonitor pm = new NullProgressMonitor();
        final Issue issue = restClient.getIssueClient().getIssue("TST-1", pm);
 
        System.out.println(issue);
 
        // now let's vote for it
        restClient.getIssueClient().vote(issue.getVotesUri(), pm);
 
        // now let's watch it
        restClient.getIssueClient().watch(issue.getWatchers().getSelf(), pm);
 
        // now let's start progress on this issue
        final Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(issue.getTransitionsUri(), pm);
        final Transition startProgressTransition = getTransitionByName(transitions, "Start Progress");
        restClient.getIssueClient().transition(issue.getTransitionsUri(), new TransitionInput(startProgressTransition.getId()), pm);
 
        // and now let's resolve it as Incomplete
        final Transition resolveIssueTransition = getTransitionByName(transitions, "Resolve Issue");
        
        String[] array = { "foo", "bar" };
        @SuppressWarnings("rawtypes")
		List list = Arrays.asList( array );
         
        
        Collection<FieldInput> fieldInputs = Arrays.asList(new FieldInput("resolution", "Incomplete"));
       
        
        final TransitionInput transitionInput = new TransitionInput(resolveIssueTransition.getId(), fieldInputs, Comment.valueOf("My comment"));
        restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput, pm);
 
    }
    private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
        for (Transition transition : transitions) {
            if (transition.getName().equals(transitionName)) {
                return transition;
            }
        }
        return null;
    }
}
