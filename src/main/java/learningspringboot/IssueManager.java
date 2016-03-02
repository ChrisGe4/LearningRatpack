package learningspringboot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.github.api.GitHubIssue;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Service;

/**
 * @author chris_ge
 */
@Service
public class IssueManager implements InitializingBean {
    //    String githubToken = "0197dd1e0145f1b5a5281818da8168b3e1022d32";
//    String org = "ChrisGe4";
//
//    String[] repos = {"SpringBoot", "Target"};
//
//    GitHubTemplate gitHubTemplate = new GitHubTemplate(githubToken);
    @Value("${github.token}")
//can have default value, prop file in jar < external prop file < env variables(command line,.bashrc, wins env)
            String githubToken;
    @Value("${org}")
    String org = "ChrisGe4";
    @Value("${repos}")
    String[] repos;

    GitHubTemplate gitHubTemplate;

    public List<Issue> findOpenIssues () {

        List<Issue> openIssues = new ArrayList<>();

        for ( String repo : repos ) {
            final List<GitHubIssue> issues = gitHubTemplate.repoOperations()
                                                           .getIssues(org, repo);

            openIssues.addAll(issues.stream()
                                    .filter(issue -> issue.getState()
                                                          .equals("open"))
                                    .map(issue -> new Issue(repo, issue))
                                    .collect(Collectors.toList()));

        }
        return openIssues;

    }

    @Override
    public void afterPropertiesSet () throws Exception {
        gitHubTemplate = new GitHubTemplate(githubToken);

    }
}
