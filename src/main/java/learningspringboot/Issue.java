package learningspringboot;

import org.springframework.social.github.api.GitHubIssue;

/**
 * @author chris_ge
 */
public class Issue {

    private String repo;
    private GitHubIssue gitHubIssue;

    public Issue (String repo, GitHubIssue gitHubIssue) {
        this.repo = repo;
        this.gitHubIssue = gitHubIssue;
    }

    public String getRepo () {
        return repo;
    }

    public GitHubIssue getGitHubIssue () {
        return gitHubIssue;
    }
}
