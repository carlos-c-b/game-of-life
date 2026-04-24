package com.carlos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    Texture buttonTexture;
    boolean isAlive[][];
    boolean next[][];
    int cellSize = 1;
    int worldWidth = 8;
    int worldHeight = 8;
    boolean preparing = true;
    float step = 0.5f;
    float timer = 0f;
    int aliveCounter;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(worldWidth, worldHeight);
        cellTexture = new Texture("cell.png");
        deadcellTexture = new Texture("deadcell.png");
        isAlive = new boolean[worldHeight][worldWidth];
        next = new boolean[worldHeight][worldWidth];


        for(int i = 0; i < worldWidth; i++) {
            for(int j = 0; j < worldHeight; j++) {
                isAlive[i][j] = false;
            }
        }
        aliveCounter = 0;
        
    }

    @Override
    public void render() {
        input();
        if(!preparing)
            logic();
        draw();
    }

    public void input() {
        if (Gdx.input.justTouched() && preparing) {  // justTouched fires once per click
            // Convert screen coords to world coords
            Vector2 worldCoords = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            
            int row = (int) worldCoords.x / cellSize;
            int col = (int) worldCoords.y / cellSize;

            if(row <= worldHeight && col <= worldWidth) {
                isAlive[row][col] = !isAlive[row][col];
                if(isAlive[row][col]) aliveCounter++;
                else aliveCounter--;
            }

        } else if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if(preparing) {
                preparing = false;
            } else {
                restart();
            }
        }
    }

    public void restart() {
        for(int i = 0; i < worldHeight; i++)
            for(int j = 0; j < worldWidth; j++) isAlive[i][j] = false;
        aliveCounter = 0;
        preparing = true;
    }

    public void logic() {
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= step) {
            timer -= step;
            nextGeneration();
            if(aliveCounter == 0) preparing = true;
        }
    }


    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        for(int i = 0; i < worldHeight; i++) {
            for(int j = 0; j < worldWidth; j++) {
                Texture tex = isAlive[i][j] ? cellTexture : deadcellTexture;
                spriteBatch.draw(tex, i, j, cellSize, cellSize);
            }
        }
    

        spriteBatch.end();
    }

    public int countNeighbours(int i, int j) {
        int count = 0;
        if(i > 0 && isAlive[i-1][j]) count++;
        if(i < worldHeight-1 && isAlive[i+1][j]) count++;
        if(j > 0 && isAlive[i][j-1]) count++;
        if(j < worldWidth-1 && isAlive[i][j+1]) count++;
        return count;
    }

    /*
    *   1. Any live cell with fewer than two live neighbours dies, as if by underpopulation.
    *   2. Any live cell with two or three live neighbours lives on to the next generation.
    *   3. Any live cell with more than three live neighbours dies, as if by overpopulation.
    *   4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
    */
    public void nextGeneration() {
        for(int i = 0; i < worldHeight; i++) {
            for(int j = 0; j < worldWidth; j++) {
                int neighbours = countNeighbours(i,j);
                if(isAlive[i][j])
                    next[i][j] = neighbours == 2 || neighbours == 3;
                else
                    next[i][j] = neighbours == 3;
                
                // Update aliveCounter
                if(isAlive[i][j] && !next[i][j]) aliveCounter--;
                else if(!isAlive[i][j] && next[i][j]) aliveCounter++;
            }
        }
        boolean[][] tmp = isAlive;
        isAlive = next;
        next = tmp;     // We bypass the garbage collector by storing the previous state array in next
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

}
