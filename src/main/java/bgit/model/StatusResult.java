package bgit.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jgit.lib.IndexDiff;

public class StatusResult {

    private final File projectPath;

    private final Map<String, Boolean> ignoredMap = new HashMap<String, Boolean>();

    private final Map<String, EnumSet<WorkNodeStatus>> statusSetMap = new TreeMap<String, EnumSet<WorkNodeStatus>>();

    StatusResult(File projectPath, IndexDiff indexDiff) {
        this.projectPath = projectPath;
        ignoredMap.put(projectPath.toString(), Boolean.FALSE);

        if (indexDiff == null) {
            return;
        }

        for (String relativePathString : indexDiff.getIgnoredNotInIndex()) {
            String absolutePathString = new File(projectPath,
                    relativePathString).toString();
            ignoredMap.put(absolutePathString, Boolean.TRUE);
        }

        addToStatusSetMap(indexDiff.getAdded(), WorkNodeStatus.ADDED);
        addToStatusSetMap(indexDiff.getChanged(), WorkNodeStatus.CHANGED);
        addToStatusSetMap(indexDiff.getConflicting(),
                WorkNodeStatus.CONFLICTING);
        addToStatusSetMap(indexDiff.getMissing(), WorkNodeStatus.MISSING);
        addToStatusSetMap(indexDiff.getModified(), WorkNodeStatus.MODIFIED);
        addToStatusSetMap(indexDiff.getRemoved(), WorkNodeStatus.REMOVED);
        addToStatusSetMap(indexDiff.getUntracked(), WorkNodeStatus.UNTRACKED);
    }

    private void addToStatusSetMap(Set<String> relativePathStrings,
            WorkNodeStatus status) {

        for (String relativePathString : relativePathStrings) {
            String workFilePathString = new File(projectPath,
                    relativePathString).toString();
            EnumSet<WorkNodeStatus> statusSet = statusSetMap
                    .get(workFilePathString);

            if (statusSet == null) {
                statusSet = EnumSet.noneOf(WorkNodeStatus.class);
                statusSetMap.put(workFilePathString, statusSet);
            }

            statusSet.add(status);
        }
    }

    EnumSet<WorkNodeStatus> getStatusSet(String workFilePathString) {
        return statusSetMap.get(workFilePathString);
    }

    void putStatusSet(String workFilePathString,
            EnumSet<WorkNodeStatus> statusSet) {
        statusSetMap.put(workFilePathString, statusSet);
    }

    EnumSet<WorkNodeStatus> removeStatusSet(String workFilePathString) {
        return statusSetMap.remove(workFilePathString);
    }

    public boolean isUntracked(File workFilePath) {
        EnumSet<WorkNodeStatus> statusSet = statusSetMap.get(workFilePath
                .toString());

        if (statusSet == null) {
            return false;
        }

        return statusSet.contains(WorkNodeStatus.UNTRACKED);
    }

    public boolean isInIgnored(File workNodePath) {
        Boolean ignored = ignoredMap.get(workNodePath.toString());

        if (ignored == null) {
            File parentPath = workNodePath.getParentFile();
            ignored = isInIgnored(parentPath);
            ignoredMap.put(parentPath.toString(), ignored);
        }

        return ignored;
    }

    public boolean isAltered(File workFolderPath) {
        List<String> alteredPathStrings = new ArrayList<String>(
                statusSetMap.keySet());
        String workFolderPathString = workFolderPath.toString()
                + File.separator;
        int pos = Collections.binarySearch(alteredPathStrings,
                workFolderPathString);

        if (pos >= 0) {
            return true;
        }

        pos = -pos - 1;

        if (pos == alteredPathStrings.size()) {
            return false;
        }

        return alteredPathStrings.get(pos).startsWith(workFolderPathString);
    }

    public File getProjectPath() {
        return projectPath;
    }

    public Map<String, EnumSet<WorkNodeStatus>> getStatusSetMap() {
        return Collections.unmodifiableMap(statusSetMap);
    }

    public boolean isClean() {
        return statusSetMap.isEmpty();
    }
}
