package bgit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;

public class GitUtils {

    public static Git open(File dir) {

        try {
            return Git.open(dir);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static IndexDiff newIndexDiff(Repository repository, String revstr,
            WorkingTreeIterator workingTreeIterator) {

        try {
            return new IndexDiff(repository, revstr, workingTreeIterator);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static void diff(IndexDiff indexDiff) {

        try {
            indexDiff.diff();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static Ref call(ResetCommand resetCommand) {

        try {
            return resetCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static DirCache call(AddCommand addCommand) {

        try {
            return addCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static RevCommit call(CommitCommand commitCommand) {

        try {
            return commitCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static CanonicalTreeParser newCanonicalTreeParser(byte[] prefix,
            ObjectReader reader, AnyObjectId treeId) {

        try {
            return new CanonicalTreeParser(prefix, reader, treeId);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static List<DiffEntry> call(DiffCommand diffCommand) {

        try {
            return diffCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static void save(StoredConfig config) {

        try {
            config.save();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static void load(StoredConfig config) {

        try {
            config.load();

        } catch (IOException e) {
            throw new ApplicationException(e);

        } catch (ConfigInvalidException e) {
            throw new ApplicationException(e);
        }
    }

    public static Git call(InitCommand initCommand) {

        try {
            return initCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static Git call(CloneCommand cloneCommand) {

        try {
            return cloneCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static BranchTrackingStatus of(Repository repository,
            String branchName) {

        try {
            return BranchTrackingStatus.of(repository, branchName);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static String getBranch(Repository repository) {

        try {
            return repository.getBranch();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static Iterable<PushResult> call(PushCommand pushCommand) {

        try {
            return pushCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static PullResult call(PullCommand pullCommand) {

        try {
            return pullCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static LogCommand add(LogCommand logCommand, ObjectId objectId) {

        try {
            return logCommand.add(objectId);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static Ref getRef(Repository repository, String name) {

        try {
            return repository.getRef(name);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static Iterable<RevCommit> call(LogCommand logCommand) {

        try {
            return logCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static Set<String> call(CleanCommand cleanCommand) {

        try {
            return cleanCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static List<Ref> call(ListTagCommand listTagCommand) {

        try {
            return listTagCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static ObjectLoader open(Repository repository, AnyObjectId objectId) {

        try {
            return repository.open(objectId);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static List<String> call(DeleteTagCommand deleteTagCommand) {

        try {
            return deleteTagCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static Ref call(TagCommand tagCommand) {

        try {
            return tagCommand.call();

        } catch (GitAPIException e) {
            throw new ApplicationException(e);
        }
    }

    public static void parseHeaders(RevWalk revWalk, RevObject revObject) {

        try {
            revWalk.parseHeaders(revObject);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static RevObject parseAny(RevWalk revWalk, ObjectId id) {

        try {
            return revWalk.parseAny(id);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static ObjectStream openStream(ObjectLoader objectLoader) {

        try {
            return objectLoader.openStream();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static int addTree(TreeWalk treeWalk, AnyObjectId id) {

        try {
            return treeWalk.addTree(id);

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static boolean next(TreeWalk treeWalk) {

        try {
            return treeWalk.next();

        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
