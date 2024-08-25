package com.antz.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

/** First screen of the application. Displayed after the application is created. */
public class PrimeNumberScreen implements Screen {

    public static final int MAX_NUMBER = 10000000;
    private int NUMBER_OF_PRIME_NUMBERS = 0;
    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont bitmapFont = new BitmapFont();
    private float iteration, timer, time, totalTime, average;

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        bitmapFont.getData().scale(1f);
        bitmapFont.setColor(Color.FIREBRICK);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            ModelCacheTest.game.setScreen(new ModelCacheTestScreen());
        }

        ScreenUtils.clear(Color.SKY, true);
        // Reset
        long startTime = TimeUtils.millis();
        NUMBER_OF_PRIME_NUMBERS = 0;
        timer += delta;

        // Test
        sieveOfEratosthenes(MAX_NUMBER);
        iteration++;
        time = TimeUtils.timeSinceMillis(startTime);
        totalTime += time;

        if (timer > 2) {
            timer = 0;
            average = totalTime/iteration;
        }

        //display
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, "libGDX - App Type: " + Gdx.app.getType() +
            ": " + NUMBER_OF_PRIME_NUMBERS + " prime numbers found between 0 and " + MAX_NUMBER, 10, 135);
        bitmapFont.draw(spriteBatch, "Actual time: " + time +"ms.   Average time : " + average + "ms", 10, 100);
        spriteBatch.end();

    }

    public void sieveOfEratosthenes(int n) {
        boolean[] prime = new boolean[n + 1];
        for (int i = 0; i <= n; i++)
            prime[i] = true;

        for (int p = 2; p * p <= n; p++) {
            if (prime[p]) {
                for (int i = p * p; i <= n; i += p)
                    prime[i] = false;
            }
        }

        for (int i = 2; i <= n; i++) {
            if (prime[i])
                NUMBER_OF_PRIME_NUMBERS++;
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        Gdx.app.log("DISPOSE","Screen disposed");
    }
}
