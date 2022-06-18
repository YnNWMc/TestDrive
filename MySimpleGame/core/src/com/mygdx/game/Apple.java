package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Apple {
    enum State {
        IDLE,
        COLLECTED,
        INACTIVE
    }

    Animation<TextureRegion> idleAnimation, collectedAnimation;
    float stateTime = 0, X = 0, Y = 0;
    State state = State.IDLE;
    Sound sound;

    public Apple() {
        Game parentGame = (Game) Gdx.app.getApplicationListener();
        sound = MyGdxGame.collectSound;

        this.CreateSpriteSheet();
    }

    public void CreateSpriteSheet()
    {
        Game parentGame = (Game) Gdx.app.getApplicationListener();

        Texture apple = MyGdxGame.apple;
        Texture collected = MyGdxGame.collected;

        TextureRegion[] frames  = MyGdxGame.CreateAnimationFrames(apple, 32, 32, 17, false, true);
        idleAnimation = new Animation<TextureRegion>(0.05f, frames);

        frames  = MyGdxGame.CreateAnimationFrames(collected, 32, 32, 6, false, true);
        collectedAnimation = new Animation<TextureRegion>(0.05f, frames);

        stateTime = 0;
    }

    void draw(SpriteBatch batch)
    {
        TextureRegion currentFrame = null;
        if(state == State.IDLE)
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        else if(state == State.COLLECTED)
            currentFrame = collectedAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, X-16, Y-16);
    }

    void update() {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        if(state == State.COLLECTED && stateTime > 0.6f)
        {
            state = State.INACTIVE;
        }
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public State getState() {
        return state;
    }

    public void Collected()
    {
        if(state != State.COLLECTED) {
            state = State.COLLECTED;
            stateTime = 0;
            sound.play();
        }
    }
}
