package org.nwgb.wikibridge.Wiki;

public class ArticleData {
    private boolean exists;
    private String preview;
    private String lastEdit;

    public ArticleData(boolean exists, String content, String lastEdit) {
        this.exists = exists;
        this.preview = content;
        this.lastEdit = lastEdit;
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }
}