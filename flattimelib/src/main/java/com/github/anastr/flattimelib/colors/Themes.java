package com.github.anastr.flattimelib.colors;

public enum Themes {
    LightTheme(new LightColors()),
    DarkTheme(new DarkColors()),
    DefaultTheme(new DefaultTheme());

    public Colors colors;
    Themes (Colors colors){
        this.colors = colors;
    }
}
