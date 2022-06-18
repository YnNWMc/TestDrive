package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    enum Direction{
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
    enum State
    {
        IDLE,
        RUN
    }
    Animation<TextureRegion> idleLeftAnimation, runLeftAnimation, idleRightAnimation, runRightAnimation;
    float stateTime;
    float X=0, Y=0, DX=0, DY=0, Speed=200;
    State state = State.IDLE;
    Direction animationDirection = Direction.RIGHT;
    Direction direction = Direction.RIGHT;

    public Player()
    {
        this.InitializeAnimation();
    }

    public void InitializeAnimation()
    {
        Texture idle = MyGdxGame.VGidle;
        Texture run = MyGdxGame.VGrun;

        TextureRegion[] frames = MyGdxGame.CreateAnimationFrames(idle, 32, 32, 11, false, true);
        idleRightAnimation = new Animation<TextureRegion>(0.05f, frames);

        frames = MyGdxGame.CreateAnimationFrames(idle, 32, 32, 11, true, true);
        idleLeftAnimation = new Animation<TextureRegion>(0.05f, frames);

        frames = MyGdxGame.CreateAnimationFrames(run, 32, 32, 11, false, true);
        runRightAnimation = new Animation<TextureRegion>(0.05f, frames);

        frames = MyGdxGame.CreateAnimationFrames(run, 32, 32, 11, true, true);
        runLeftAnimation = new Animation<TextureRegion>(0.05f, frames);
        stateTime = 0;
    }

    public void draw(SpriteBatch batch)
    {
        TextureRegion currentFrame = null;
        if(state == State.RUN && animationDirection == Direction.LEFT)
            currentFrame = runLeftAnimation.getKeyFrame(stateTime, true);
        else if(state == State.RUN && animationDirection == Direction.RIGHT)
            currentFrame = runRightAnimation.getKeyFrame(stateTime, true);
        else if(state == State.IDLE && animationDirection == Direction.LEFT)
            currentFrame = idleLeftAnimation.getKeyFrame(stateTime, true);
        else if(state == State.IDLE && animationDirection == Direction.RIGHT)
            currentFrame = idleRightAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, X-16, Y-16);
    }

    public void update()
    {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        X += DX * Speed * delta;
        if(X > MyGdxGame.WORLD_WIDTH-20)
        {
            X = MyGdxGame.WORLD_WIDTH-20;
            this.Stop();
        }
        else if(X < 20)
        {
            X = 20;
            this.Stop();
        }

        Y += DY * Speed * delta;
        if(Y > MyGdxGame.WORLD_HEIGHT-20)
        {
            Y = MyGdxGame.WORLD_HEIGHT-20;
            this.Stop();
        }
        else if(Y < 20)
        {
            Y = 20;
            this.Stop();
        }
    }

    void Stop()
    {
        if(state != State.IDLE) {
            DX = 0;
            DY = 0;
            state = State.IDLE;
        }
    }

    public void setMove(Direction d)
    {
        direction = d;
        state = State.RUN;
        if(animationDirection == Direction.LEFT && d == Direction.RIGHT)
        {
            animationDirection = Direction.RIGHT;
            stateTime = 0;
        }
        else if(animationDirection == Direction.RIGHT && d == Direction.LEFT)
        {
            animationDirection = Direction.LEFT;
            stateTime = 0;
        }

        if(d == Direction.RIGHT)
        {
            DX = 1;
            DY = 0;
        }
        else if(d == Direction.LEFT)
        {
            DX = -1;
            DY = 0;
        }
        else if(d == Direction.UP)
        {
            DX = 0;
            DY = -1;
        }
        else if(d == Direction.DOWN)
        {
            DX = 0;
            DY = 1;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public State getState() {
        return state;
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

    public boolean Collect(Apple a)
    {
        if(a.getState() != Apple.State.IDLE)
            return false;
        float dx = X - a.getX();
        float dy = Y - a.getY();
        float d = dx*dx + dy*dy;
        return (d <= 256);
    }

    public boolean Collision(Enemy ene)
    {
        return X >= ene.getX() && X <= (ene.getX() + 32) && Y >= ene.getY()
                && (Y <= ene.getY() + 32);
    }
}