package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy {
    Animation<TextureRegion> runLeftAnimation,runRightAnimation;
    float X=0, Y=0, DX=0, DY=0, Speed= 100;
    float stateTime;
    Enemy.Direction animationDirection = Enemy.Direction.RIGHT;


    enum Direction{
        LEFT,
        RIGHT
    }

    public Enemy()
    {
        this.InitializeAnimation();
    }

    public void InitializeAnimation()
    {
        Texture run = MyGdxGame.MDrun;

        TextureRegion[] frames = MyGdxGame.CreateAnimationFrames(run, 32, 32, 12, false, true);
        runRightAnimation = new Animation<>(0.05f, frames);

        frames = MyGdxGame.CreateAnimationFrames(run, 32, 32, 12, true, true);
        runLeftAnimation = new Animation<>(0.05f, frames);

        stateTime = 0;
    }

    public void draw(SpriteBatch batch)
    {
        TextureRegion curFrame = new TextureRegion();

        if(animationDirection == Enemy.Direction.LEFT)
            curFrame = runLeftAnimation.getKeyFrame(stateTime, true);
        else if(animationDirection == Enemy.Direction.RIGHT)
            curFrame = runRightAnimation.getKeyFrame(stateTime, true);

        batch.draw(curFrame,getX(),getY());
    }

    public void update()
    {
        float time = Gdx.graphics.getDeltaTime();
        stateTime += time;
        X += (DX * Speed * time);
        Y += (DY * Speed * time);

        if(getX() > MyGdxGame.WORLD_WIDTH-20) {
            X = MyGdxGame.WORLD_WIDTH-20;
            Y -= 15;
            Speed *= -1;
            animationDirection = Direction.LEFT;
            stateTime = 0;
        }
        else if(getX() < 5)
        {
            X = 5;
            Y += 15;
            Speed *= -1;
            animationDirection = Direction.RIGHT;
            stateTime = 0;
        }
        if(Y > MyGdxGame.WORLD_HEIGHT-20)
        {
            Y = MyGdxGame.WORLD_HEIGHT-20;
            Speed *= -1;
        }
        else if(Y < 10)
        {
            Y = 10;
            Speed *= -1;
        }
        setDirection(animationDirection);
    }

    public void setDirection(Direction d)
    {
        if(d == Direction.RIGHT)
        {
            DX = 1;
        }
        else if(d == Direction.LEFT)
        {
            DX = -1;
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
}
