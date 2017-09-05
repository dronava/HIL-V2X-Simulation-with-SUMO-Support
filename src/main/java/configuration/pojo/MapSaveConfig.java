package configuration.pojo;

import com.google.common.base.MoreObjects;

public class MapSaveConfig {
    private String saveDir;
    private String saveFile;
    private String namePattern;

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    public String getNamePattern() {
        return namePattern;
    }

    public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("saveDir", saveDir + "\n")
                .add("saveFile", saveFile + "\n").add("namePattern", namePattern + "\n")
                .toString();
    }

}
