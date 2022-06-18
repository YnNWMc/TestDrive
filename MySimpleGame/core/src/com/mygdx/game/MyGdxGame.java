package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MyGdxGame extends Game implements InputProcessor {
	public static final int WORLD_WIDTH = 640;
	public static final int WORLD_HEIGHT = 480;
	enum State{
		PLAYING,
		GAME_OVER
	}
	State state ;

	private Viewport viewport;
	private OrthographicCamera camera;

	SpriteBatch batch;
	static Texture background;
	static Texture VGidle;
	static Texture VGrun;
	static Texture MDrun;
	static Texture apple;
	static Texture collected;
	static Texture gameOver;
	static Sound collectSound;
	static Sound gameOverS;
	Music music;

	Random randomizer = new Random();
	Player player;
	ArrayList<Apple> appleList;
	ArrayList<Enemy> MDude;

	int score;
	BitmapFont font;
	BitmapFontCache fontCache;
	float applecountdowntimer = 1.25f;

	@Override
	public void create () {
		state = State.PLAYING;
		camera = new OrthographicCamera(MyGdxGame.WORLD_WIDTH, MyGdxGame.WORLD_HEIGHT);
		camera.setToOrtho(true, MyGdxGame.WORLD_WIDTH, MyGdxGame.WORLD_HEIGHT);
		viewport = new FitViewport(MyGdxGame.WORLD_WIDTH, MyGdxGame.WORLD_HEIGHT, camera);
		batch = new SpriteBatch();

		gameOver = new Texture("gameover22.png");
		gameOverS = Gdx.audio.newSound(Gdx.files.internal("gameover.wav"));

		background = new Texture("Green.png");
		VGidle = new Texture("VGIdle.png");
		VGrun = new Texture("VGRun.png");
		MDrun = new Texture("MDRun.png");

		apple = new Texture("Apple.png");
		collected = new Texture("Collected.png");
		collectSound = Gdx.audio.newSound(Gdx.files.internal("Sfx1.wav"));

		music = Gdx.audio.newMusic(Gdx.files.internal("BgMusic.mp3"));
		music.setLooping(true);
		music.play();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parameter.size = 32;
		parameter.color = Color.BLACK;
		parameter.flip = true;

		font = generator.generateFont(parameter); // font size 12 pixels

		fontCache = new BitmapFontCache(font);
		fontCache.setText("Score: 0", 5, 5);
		parameter.flip=true;
		player = new Player();

		MDude = new ArrayList<>();
		int eneY = 100;
		for (int i = 0; i < 5 ; i++) {
			Enemy md = new Enemy();
			if(randomizer.nextBoolean())
				md.setX(0);
			else
				md.setX(MyGdxGame.WORLD_WIDTH-20);
			md.setY(eneY);
			MDude.add(md);
			eneY+=80;
		}

		appleList = new ArrayList<>();
		for(int i=0; i < 20; i++)
		{
			Apple a = new Apple();
			a.setX((float)randomizer.nextInt(MyGdxGame.WORLD_WIDTH));
			a.setY((float)randomizer.nextInt(MyGdxGame.WORLD_HEIGHT));
			appleList.add(a);
		}

		Gdx.input.setInputProcessor(this);
		score = 0;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		if (state == State.PLAYING){
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 10; j++)
					batch.draw(background, j * 64, i * 64);
			}

			player.draw(batch);

			for (Apple a: appleList) {
				a.draw(batch);
			}

			for (Enemy enemy : MDude) {
				enemy.draw(batch);
			}

			fontCache.draw(batch);

			this.processInput();
			this.update();
		}
		else {
			ClearField();
			batch.draw(gameOver, 0, 0, MyGdxGame.WORLD_WIDTH, MyGdxGame.WORLD_HEIGHT);
		}
		batch.end();
	}


	public void update()
	{
		player.update();

		for (Enemy md : MDude) {
			md.update();
			if (player.Collision(md)) {
				gameOverS.play();
				state = State.GAME_OVER;
				break;
			}
		}
		if (state == State.PLAYING) {
			applecountdowntimer -= Gdx.graphics.getDeltaTime();
			if (applecountdowntimer <= 0) {
				Apple a = new Apple();
				a.setX((float) randomizer.nextInt(MyGdxGame.WORLD_WIDTH-30)+10);
				a.setY((float) randomizer.nextInt(MyGdxGame.WORLD_HEIGHT-20)+10);
				appleList.add(a);
				applecountdowntimer =  1.25f;
			}
			Iterator<Apple> iterApple = appleList.iterator();
			while (iterApple.hasNext()) {
				Apple a = iterApple.next();
				a.update();
				if (player.Collect(a)) {
					a.Collected();
					this.addScore(10);
				}
				if (a.getState() == Apple.State.INACTIVE)
					iterApple.remove();
			}
		}

	}

	public void addScore(int scr)
	{
		score += scr;
		fontCache.setText(String.format("Score: %d", score), 5, 5);
	}

	public void processInput()
	{
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			player.setMove(Player.Direction.LEFT);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			player.setMove(Player.Direction.RIGHT);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.UP))
		{
			player.setMove(Player.Direction.UP);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			player.setMove(Player.Direction.DOWN);
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		collectSound.dispose();
	}

	public void ClearField() {
		music.dispose();
		background.dispose();
		VGidle.dispose();
		VGrun.dispose();
		MDrun.dispose();
		apple.dispose();
	}
	public static TextureRegion[] CreateAnimationFrames(Texture tex, int frameWidth, int frameHeight, int frameCount, boolean flipx, boolean flipy)
	{
		TextureRegion[][] tmp = TextureRegion.split(tex,frameWidth, frameHeight);
		TextureRegion[] frames = new TextureRegion[frameCount];
		int index = 0;
		int row = tex.getHeight() / frameHeight;
		int col = tex.getWidth() / frameWidth;
		for (int i = 0; i < row && index < frameCount; i++) {
			for (int j = 0; j < col && index < frameCount; j++) {
				frames[index] = tmp[i][j];
				frames[index].flip(flipx, flipy);
				index++;
			}
		}
		return frames;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.LEFT)
		{
			player.setMove(Player.Direction.LEFT);
		}
		else if(keycode == Input.Keys.RIGHT)
		{
			player.setMove(Player.Direction.RIGHT);
		}
		else if(keycode == Input.Keys.UP)
		{
			player.setMove(Player.Direction.UP);
		}
		else if(keycode == Input.Keys.DOWN)
		{
			player.setMove(Player.Direction.DOWN);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT && player.getDirection() == Player.Direction.LEFT)
			player.Stop();
		else if(keycode == Input.Keys.RIGHT && player.getDirection() == Player.Direction.RIGHT)
			player.Stop();
		else if(keycode == Input.Keys.UP && player.getDirection() == Player.Direction.UP)
			player.Stop();
		else if(keycode == Input.Keys.DOWN && player.getDirection() == Player.Direction.DOWN)
			player.Stop();
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
