package com.antz.tests;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ModelCacheTest extends Game {
    public static Game game;
    @Override
    public void create() {
        game = this;
        setScreen(new ModelCacheTestScreen());
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }
}
