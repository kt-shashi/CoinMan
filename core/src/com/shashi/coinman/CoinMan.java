package com.shashi.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import sun.rmi.runtime.Log;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;

    //To create background, we use Texture
    Texture backgroud;
    Texture[] man;

    int manState = 0;
    int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;
    Rectangle manRectangle;
    BitmapFont font;

    Texture dizzy;

    int score = 0;
    int gameState = 0;

    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();


    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();

    Texture bomb;
    int bombCount;

    Random random;

    @Override
    public void create() {
        batch = new SpriteBatch();
        backgroud = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;

        //Setup Coin
        coin = new Texture("coin.png");
        //Setup Bomb
        bomb = new Texture("bomb.png");

        dizzy = new Texture("dizzy-1.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        random = new Random();

    }

    public void makeCoin() {
        float heigth = random.nextFloat() * Gdx.graphics.getHeight();

        coinYs.add((int) heigth);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void makeBomb() {
        float heigth = random.nextFloat() * Gdx.graphics.getHeight();

        bombYs.add((int) heigth);
        bombXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {

        batch.begin();
        //tO DRAW on screen: Reuired: Texture,start_pos,end_pos,width,height
        //background
        batch.draw(backgroud, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        if (gameState == 1) {
            //GAME if LIVE

            //Display Bomb
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }
            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //Display Coins
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 5);

                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }

            //Jump
            if (Gdx.input.justTouched()) {
                velocity = -10;
            }

            //SlowDownRunning
            if (pause < 8) {
                pause++;
            } else {

                pause = 0;
                //Running
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            velocity += gravity;
            manY -= velocity;


            if (manY <= 0) {
                manY = 0;
            } else if (manY > 1800) {
                manY = 1750;
            }


        } else if (gameState == 0) {
            //Waiting to Start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }

        } else if (gameState == 2) {
            //GAME OVER
            if (Gdx.input.justTouched()) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 0;

                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();
                bombCount = 0;
            }
        }


        if (gameState == 2) {
            //man
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        } else {
            //man
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        }


        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        //Check if man has collided with Coin
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                score++;

                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        //Check if man has collided with Coin
        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                Gdx.app.log("Bomb!", "Collision");
                gameState = 2;
            }
        }

        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();

    }

    @Override
    public void dispose() {


        batch.dispose();
    }
}
