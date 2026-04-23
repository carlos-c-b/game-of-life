package com.carlos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture cellTexture;
    Texture deadcellTexture;
    boolean isAlive[][];
    int cellSize = 1;
    int worldWidth = 8;
    int worldHeight = 8;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(worldWidth, worldHeight);
        cellTexture = new Texture("cell.png");
        deadcellTexture = new Texture("deadcell.png");
        isAlive = new boolean[worldHeight][worldWidth];


        for(int i = 0; i < worldWidth; i++) {
            for(int j = 0; j < worldHeight; j++) {
                isAlive[i][j] = true;
            }
        }
        
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void input() {
        if (Gdx.input.justTouched()) {  // justTouched fires once per click
            // Convert screen coords to world coords
            Vector2 worldCoords = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            
            int row = (int) worldCoords.x / cellSize;
            int col = (int) worldCoords.y / cellSize;

            if(row <= worldHeight && col <= worldWidth)
                isAlive[row][col] = !isAlive[row][col];

        }
    }

    public void logic() {

    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        for(int i = 0; i < worldWidth; i++) {
            for(int j = 0; j < worldHeight; j++) {
                Texture tex = isAlive[i][j] ? cellTexture : deadcellTexture;
                spriteBatch.draw(tex, i, j, cellSize, cellSize);
            }
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

}
