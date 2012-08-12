package bgit.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import bgit.CommonsUtils;
import bgit.GitUtils;
import bgit.JdkUtils;

public class Project implements Comparable<Project> {

    private static final Log log = LogFactory.getLog(Project.class);

    private final Application application;

    private final Long id;

    private final File absolutePath;

    private String name;

    private DefaultFileMonitor fileMonitor;

    private final Set<WorkNodeListener> workNodeListeners = new HashSet<WorkNodeListener>();

    private final Git git;

    public final Repository repository;

    private StatusResult statusResult;

    Project(Application application, Long id, File absolutePath, String name) {
        this.application = application;
        this.id = id;
        this.absolutePath = absolutePath;
        this.name = name;
        this.git = GitUtils.open(absolutePath);
        this.repository = git.getRepository();
        this.statusResult = new StatusResult(absolutePath, null);
    }

    public GitTag createTag(String tagName, String message) {
        TagCommand tagCommand = git.tag();
        tagCommand.setName(tagName);
        tagCommand.setMessage(message);
        Ref ref = GitUtils.call(tagCommand);
        return new GitTag(git, ref);
    }

    public Iterable<GitTag> findGitTags() {
        ListTagCommand listTagCommand = git.tagList();
        List<Ref> refs = GitUtils.call(listTagCommand);
        List<GitTag> gitTags = new ArrayList<GitTag>();

        for (Ref ref : refs) {
            GitTag gitTag = new GitTag(git, ref);
            gitTags.add(gitTag);
        }

        return gitTags;
    }

    public GitBranchTrackingStatus findGitBranchTrackingStatus(String branchName) {
        BranchTrackingStatus branchTrackingStatus = GitUtils.of(repository,
                branchName);

        if (branchTrackingStatus == null) {
            return null;
        }

        return new GitBranchTrackingStatus(branchTrackingStatus);
    }

    public String getRemoteTrackingBranchName() {
        BranchConfig branchConfig = new BranchConfig(repository.getConfig(),
                getCurrentBranchName());
        return branchConfig.getRemoteTrackingBranch();
    }

    public String getCurrentBranchName() {
        return GitUtils.getBranch(repository);
    }

    public List<GitPushResult> push() {
        PushCommand pushCommand = git.push();
        Iterable<PushResult> pushResults = GitUtils.call(pushCommand);
        List<GitPushResult> gitPushResults = new ArrayList<GitPushResult>();

        for (PushResult pushResult : pushResults) {
            gitPushResults.add(new GitPushResult(pushResult));
        }

        return gitPushResults;
    }

    public GitPullResult pull() {
        PullCommand pullCommand = git.pull();
        PullResult pullResult = GitUtils.call(pullCommand);
        return new GitPullResult(pullResult);
    }

    public GitConfig findGitConfig() {
        StoredConfig config = git.getRepository().getConfig();
        return new GitConfig(config);
    }

    public void bare(File repositoryPath) {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(absolutePath.toURI().toString());
        cloneCommand.setDirectory(repositoryPath);
        cloneCommand.setBare(true);
        GitUtils.call(cloneCommand);
    }

    // TODO Cannot delete the first commit.
    public void discardLastCommit() {
        ResetCommand resetCommand = git.reset();
        resetCommand.setMode(ResetType.SOFT);
        resetCommand.setRef("HEAD^");
        GitUtils.call(resetCommand);
    }

    public GitDiffResult diff(String relativePathString,
            GitCommit oldGitCommit, GitCommit newGitCommit) {
        AbstractTreeIterator oldTreeIterator = null;

        if (oldGitCommit == null) {
            oldTreeIterator = new EmptyTreeIterator();

        } else {
            oldTreeIterator = GitUtils.newCanonicalTreeParser(null, git
                    .getRepository().newObjectReader(), oldGitCommit
                    .getTreeId());
        }

        AbstractTreeIterator newTreeIterator = null;

        if (newGitCommit == null) {
            newTreeIterator = new FileTreeIterator(git.getRepository());

        } else {
            newTreeIterator = GitUtils.newCanonicalTreeParser(null, git
                    .getRepository().newObjectReader(), newGitCommit
                    .getTreeId());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DiffCommand diffCommand = git.diff();
        diffCommand.setPathFilter(PathFilter.create(relativePathString));
        diffCommand.setOldTree(oldTreeIterator);
        diffCommand.setNewTree(newTreeIterator);
        diffCommand.setOutputStream(outputStream);
        List<GitDiffEntry> gitDiffEntries = new ArrayList<GitDiffEntry>();

        for (DiffEntry diffEntry : GitUtils.call(diffCommand)) {
            GitDiffEntry gitDiffEntry = new GitDiffEntry(this, diffEntry);
            gitDiffEntries.add(gitDiffEntry);
        }

        return new GitDiffResult(gitDiffEntries, outputStream.toString());
    }

    public GitCommit findNewestGitCommit(String relativePathString) {
        LogCommand logCommand = git.log();
        logCommand.addPath(relativePathString);
        Iterable<RevCommit> revCommits = GitUtils.call(logCommand);
        Iterator<RevCommit> it = revCommits.iterator();

        if (!it.hasNext()) {
            return null;
        }

        return new GitCommit(this, it.next());
    }

    public Iterable<GitCommit> log() {
        Ref currentBranchRef = GitUtils.getRef(repository,
                getCurrentBranchName());
        Ref remoteTrackingBranchRef = null;
        String remoteTrackingBranchName = getRemoteTrackingBranchName();

        if (remoteTrackingBranchName != null) {
            remoteTrackingBranchRef = GitUtils.getRef(repository,
                    remoteTrackingBranchName);
        }

        LogCommand logCommand = git.log();
        GitUtils.add(logCommand, currentBranchRef.getObjectId());

        if (remoteTrackingBranchRef != null) {
            GitUtils.add(logCommand, remoteTrackingBranchRef.getObjectId());
        }

        Iterable<RevCommit> revCommits = GitUtils.call(logCommand);
        List<GitCommit> gitCommits = new ArrayList<GitCommit>();

        for (RevCommit revCommit : revCommits) {
            GitCommit gitCommit = new GitCommit(this, revCommit);
            gitCommits.add(gitCommit);
        }

        return gitCommits;
    }

    public void commit(String message, List<String> relativePathStrings) {
        AddCommand addCommand = git.add();

        for (String relativePathString : relativePathStrings) {
            addCommand.addFilepattern(relativePathString);
        }

        GitUtils.call(addCommand);

        CommitCommand commitCommand = git.commit();
        commitCommand.setMessage(message);

        for (String relativePathString : relativePathStrings) {
            commitCommand.setOnly(relativePathString);
        }

        GitUtils.call(commitCommand);
    }

    public void rollback() {
        ResetCommand resetCommand = git.reset();
        resetCommand.setMode(ResetType.HARD);
        resetCommand.setRef("HEAD");
        GitUtils.call(resetCommand);

        CleanCommand cleanCommand = git.clean();
        GitUtils.call(cleanCommand);
    }

    public StatusResult status() {
        statusResult = status(null);
        return statusResult;
    }

    public StatusResult status(File workFilePath) {
        FileTreeIterator fileTreeIterator = new FileTreeIterator(repository);
        IndexDiff indexDiff = GitUtils.newIndexDiff(repository, Constants.HEAD,
                fileTreeIterator);

        if (workFilePath != null) {
            String relativePathString = convertToRelativePathString(
                    absolutePath.toString(), workFilePath.toString());
            indexDiff.setFilter(PathFilter.create(relativePathString));
        }

        GitUtils.diff(indexDiff);
        return new StatusResult(absolutePath, indexDiff);
    }

    public void startMonitor() {

        if (fileMonitor != null) {
            stopMonitor();
        }

        fileMonitor = new DefaultFileMonitor(new FileListener() {

            // TODO Refactor duplicated codes
            @Override
            public void fileCreated(FileChangeEvent event) throws Exception {
                log.info(event.getFile());
                File workNodePath = new File(CommonsUtils.getURL(
                        event.getFile()).getPath());

                if (!isInProject(absolutePath, workNodePath)) {
                    return;
                }

                boolean updated = updateStatusSet(workNodePath);

                for (WorkNodeListener workNodeListener : workNodeListeners) {
                    workNodeListener.workNodeCreated(workNodePath);
                }

                if (!updated) {
                    return;
                }

                fireAncestorNodeChange(workNodePath);
            }

            // TODO Refactor duplicated codes
            @Override
            public void fileDeleted(FileChangeEvent event) throws Exception {
                log.info(event.getFile());
                File workNodePath = new File(CommonsUtils.getURL(
                        event.getFile()).getPath());

                if (!isInProject(absolutePath, workNodePath)) {
                    return;
                }

                boolean updated = updateStatusSet(workNodePath);

                for (WorkNodeListener workNodeListener : workNodeListeners) {
                    workNodeListener.workNodeDeleted(workNodePath);
                }

                if (!updated) {
                    return;
                }

                fireAncestorNodeChange(workNodePath);
            }

            // TODO Refactor duplicated codes
            @Override
            public void fileChanged(FileChangeEvent event) throws Exception {
                log.info(event.getFile());
                File workNodePath = new File(CommonsUtils.getURL(
                        event.getFile()).getPath());

                if (!isInProject(absolutePath, workNodePath)) {
                    return;
                }

                boolean updated = updateStatusSet(workNodePath);
                fireNodeChange(workNodePath);

                if (!updated) {
                    return;
                }

                fireAncestorNodeChange(workNodePath);
            }

            private boolean updateStatusSet(File workNodePath) {

                if (workNodePath.isDirectory()) {
                    return false;
                }

                EnumSet<WorkNodeStatus> oldStatusSet = statusResult
                        .removeStatusSet(workNodePath.getAbsolutePath());
                StatusResult workFileStatusResult = status(workNodePath);
                EnumSet<WorkNodeStatus> newStatusSet = workFileStatusResult
                        .getStatusSet(workNodePath.toString());

                if (newStatusSet != null) {
                    statusResult.putStatusSet(workNodePath.toString(),
                            newStatusSet);
                }

                if (newStatusSet == null && oldStatusSet == null) {
                    return false;
                }

                if (newStatusSet != null && newStatusSet.equals(oldStatusSet)) {
                    return false;
                }

                return true;
            }

            private void fireAncestorNodeChange(File workNodePath) {
                File ancestorPath = workNodePath.getParentFile();

                while (ancestorPath != null) {

                    if (ancestorPath.toString().length() < absolutePath
                            .toString().length()) {
                        break;
                    }

                    fireNodeChange(ancestorPath);
                    ancestorPath = ancestorPath.getParentFile();
                }
            }

            private void fireNodeChange(File workNodePath) {

                for (WorkNodeListener workNodeListener : workNodeListeners) {
                    workNodeListener.workNodeChanged(workNodePath);
                }
            }
        });
        fileMonitor.setRecursive(true);
        // TODO Don't montior ".git" directory using removeFile.
        fileMonitor.addFile(CommonsUtils.resolveFile(CommonsUtils.getManager(),
                absolutePath.toString()));
        fileMonitor.start();
    }

    public void stopMonitor() {

        if (fileMonitor == null) {
            return;
        }

        fileMonitor.stop();
        fileMonitor = null;
    }

    public void addWorkNodeListener(WorkNodeListener workNodeListener) {
        workNodeListeners.add(workNodeListener);
    }

    public void removeWorkNodeListener(WorkNodeListener workNodeListener) {
        workNodeListeners.remove(workNodeListener);
    }

    @Override
    public int compareTo(Project anotherProject) {
        int cmp = name.compareTo(anotherProject.getName());

        if (cmp != 0) {
            return cmp;
        }

        return id.compareTo(anotherProject.getId());
    }

    public void rename(String newName) {
        Preferences projectPreferences = application.getProjectPreferences(id);
        projectPreferences.put("name", newName);
        JdkUtils.flush(projectPreferences);
        name = newName;
    }

    public void delete() {
        Preferences projectPreferences = application.getProjectPreferences(id);
        JdkUtils.removeNode(projectPreferences);
    }

    public WorkFolder findRootWorkFolder() {
        return findWorkFolder(absolutePath);
    }

    public WorkFolder findWorkFolder(File workFolderPath) {

        if (!isInProject(absolutePath, workFolderPath)) {
            return null;
        }

        boolean ignored = statusResult.isInIgnored(workFolderPath);
        boolean altered = statusResult.isAltered(workFolderPath);
        return new WorkFolder(this, workFolderPath, ignored, altered);
    }

    public WorkFile findWorkFile(File workFilePath) {

        if (!isInProject(absolutePath, workFilePath)) {
            return null;
        }

        boolean ignored = statusResult.isInIgnored(workFilePath);
        EnumSet<WorkNodeStatus> statusSet = statusResult
                .getStatusSet(workFilePath.toString());

        if (statusSet == null) {
            statusSet = EnumSet.noneOf(WorkNodeStatus.class);
        }

        return new WorkFile(this, workFilePath, ignored, statusSet);
    }

    public static boolean isInProject(File projectPath, File workNodePath) {
        String workNodePathString = workNodePath.toString();
        String projectPathString = projectPath.toString();

        if (!workNodePathString.startsWith(projectPathString)) {
            return false;
        }

        File path = new File(convertToRelativePathString(projectPathString,
                workNodePathString));

        while (path != null) {

            if (path.getName().endsWith(".git")) {
                return false;
            }

            path = path.getParentFile();
        }

        return true;
    }

    public static String convertToRelativePathString(String projectPathString,
            String workNodePathString) {

        if (workNodePathString.length() == projectPathString.length()) {
            return "";
        }

        return workNodePathString.substring(projectPathString.length()
                + File.separator.length());
    }

    public Long getId() {
        return id;
    }

    public File getAbsolutePath() {
        return absolutePath;
    }

    public String getName() {
        return name;
    }

    public Git getGit() {
        return git;
    }
}
