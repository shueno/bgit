package bgit.model;

public class GitDiffResult {

    private final Iterable<GitDiffEntry> gitDiffEntries;

    private final String text;

    GitDiffResult(Iterable<GitDiffEntry> gitDiffEntries, String text) {
        this.gitDiffEntries = gitDiffEntries;
        this.text = text;
    }

    public Iterable<GitDiffEntry> getGitDiffEntries() {
        return gitDiffEntries;
    }

    public String getText() {
        return text;
    }
}
