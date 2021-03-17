package dev.donhk.utils;

import java.io.IOException;

public class LayoutManager {

    private static LayoutManager layoutManager = null;
    private String layout;

    private LayoutManager() {
        try {
            layout = Utils.resource2txt("layout.html");
        } catch (IOException e) {
            layout = "";
        }
    }

    public static LayoutManager getInstance() {
        if (layoutManager == null) {
            layoutManager = new LayoutManager();
        }
        return layoutManager;
    }

    public String getLayout() {
        return layout;
    }
}
