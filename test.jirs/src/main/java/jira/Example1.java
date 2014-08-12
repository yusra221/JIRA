package jira;


import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
  /**
37   * A sample code how to use JRJC library 
38   *
39   * @since v0.1
40   */
  public class Example1 {
	  
	  	static final Logger LOG = LoggerFactory.getLogger(Example1.class);

   public static void main(String[] args) throws URISyntaxException {
        final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        
        
        final java.net.URI jiraServerUri = new  java.net.URI("http://localhost:28080/");
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "test", "test");
        //final NullProgressMonitor pm = new NullProgressMonitor();
        final Issue issue = (Issue) restClient.getIssueClient().getIssue("TST-1").claim();
 
        System.out.println(issue);
 
        // now let's vote for it
        restClient.getIssueClient().vote(issue.getVotesUri()).claim();
 
        // now let's watch it
        restClient.getIssueClient().watch(issue.getWatchers().getSelf()).claim();
 
        // now let's start progress on this issue
        final Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(issue.getTransitionsUri()).claim();
        final Transition startProgressTransition = getTransitionByName(transitions, "Start Progress");
        restClient.getIssueClient().transition(issue.getTransitionsUri(), new TransitionInput(startProgressTransition.getId())).claim();
 
        // and now let's resolve it as Incomplete
        final Transition resolveIssueTransition = getTransitionByName(transitions, "Resolve Issue");
        
        Collection<FieldInput> fieldInputs = Arrays.asList(new FieldInput("resolution", "Incomplete"));
        

        
        final TransitionInput transitionInput = new TransitionInput(resolveIssueTransition.getId(), fieldInputs, Comment.valueOf("My comment"));
        restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput).claim();
 
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


