package bgit.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.StoredConfig;

import bgit.GitUtils;

public class GitConfig {

    private final StoredConfig config;

    GitConfig(StoredConfig config) {
        this.config = config;
    }

    public String getConnectionRemoteName(String branchName) {
        String remoteName = config.getString(
                ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
                ConfigConstants.CONFIG_KEY_REMOTE);

        if (remoteName == null) {
            return "origin";
        }

        return remoteName;
    }

    public String getConnectionUrlString(String remoteName) {
        return config.getString(ConfigConstants.CONFIG_REMOTE_SECTION,
                remoteName, "url");
    }

    public void setConnection(String remoteName, String repositoryUrlString,
            String branchName) {
        config.setString(ConfigConstants.CONFIG_REMOTE_SECTION, remoteName,
                "url", repositoryUrlString);
        config.setString(ConfigConstants.CONFIG_REMOTE_SECTION, remoteName,
                "fetch",
                String.format("+refs/heads/*:refs/remotes/%s/*", remoteName));
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
                ConfigConstants.CONFIG_KEY_REMOTE, remoteName);
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
                ConfigConstants.CONFIG_KEY_MERGE, "refs/heads/" + branchName);
    }

    public void unsetConnection(String remoteName, String branchName) {
        config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
                ConfigConstants.CONFIG_KEY_REMOTE);
        config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, branchName,
                ConfigConstants.CONFIG_KEY_MERGE);

        for (String subsection : config
                .getSubsections(ConfigConstants.CONFIG_BRANCH_SECTION)) {

            if (remoteName.equals(config.getString(
                    ConfigConstants.CONFIG_BRANCH_SECTION, subsection,
                    ConfigConstants.CONFIG_KEY_REMOTE))) {
                return;
            }
        }

        config.unset(ConfigConstants.CONFIG_REMOTE_SECTION, remoteName, "url");
        config.unset(ConfigConstants.CONFIG_REMOTE_SECTION, remoteName, "fetch");
    }

    public String getUserName() {
        return config.getString(ConfigConstants.CONFIG_USER_SECTION, null,
                ConfigConstants.CONFIG_KEY_NAME);
    }

    public String getUserEmail() {
        return config.getString(ConfigConstants.CONFIG_USER_SECTION, null,
                ConfigConstants.CONFIG_KEY_EMAIL);
    }

    public void setUser(String name, String email) {
        config.setString(ConfigConstants.CONFIG_USER_SECTION, null,
                ConfigConstants.CONFIG_KEY_NAME, name);
        config.setString(ConfigConstants.CONFIG_USER_SECTION, null,
                ConfigConstants.CONFIG_KEY_EMAIL, email);
    }

    public Map<String, String> getValueMap() {
        Map<String, String> valueMap = new LinkedHashMap<String, String>();

        for (String section : config.getSections()) {

            for (String subsection : config.getSubsections(section)) {

                for (String name : config.getNames(section, subsection)) {
                    String key = convertToKey(section, subsection, name);
                    String value = config.getString(section, subsection, name);
                    valueMap.put(key, value);
                }
            }

            for (String name : config.getNames(section)) {
                String key = convertToKey(section, null, name);
                String value = config.getString(section, null, name);
                valueMap.put(key, value);
            }
        }

        return valueMap;
    }

    public static boolean isKeyValid(String key) {

        if (key == null || key.isEmpty()) {
            return false;
        }

        return key.indexOf(".") >= 0;
    }

    public void set(String key, String value) {
        String[] names = convertFromKey(key);
        config.setString(names[0], names[1], names[2], value);
    }

    public void unset(String key) {
        String[] names = convertFromKey(key);
        config.unset(names[0], names[1], names[2]);
    }

    public void save() {
        GitUtils.save(config);
    }

    private String convertToKey(String section, String subsection, String name) {

        if (subsection == null) {
            return String.format("%s.%s", section, name);
        }

        return String.format("%s.%s.%s", section, subsection, name);
    }

    private String[] convertFromKey(String key) {
        String[] names = new String[3];
        int pos1 = key.indexOf(".");
        int pos2 = key.indexOf(".", pos1 + 1);

        if (pos2 < 0) {
            names[0] = key.substring(0, pos1);
            names[2] = key.substring(pos1 + 1);
            return names;
        }

        names[0] = key.substring(0, pos1);
        names[1] = key.substring(pos1 + 1, pos2);
        names[2] = key.substring(pos2 + 1);
        return names;
    }
}
