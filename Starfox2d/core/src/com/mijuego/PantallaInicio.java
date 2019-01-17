/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mijuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

/**
 *
 * @author Jose A
 */
public class PantallaInicio implements Screen{
    final Starfox game;
    private Texture img;
    OrthographicCamera camera;
    public PantallaInicio(final Starfox game) {
        this.game = game;
        img = new Texture("fondoMain.jpg");
	camera = new OrthographicCamera();
	camera.setToOrtho(false, 800, 600); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float f) {
                Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
                game.batch.draw(img, 0, 0);
		game.font.draw(game.batch, "STARFOX", 100, 150);
		game.font.draw(game.batch, "Pulsa ESPACIO para continuar!", 100, 100);
		game.batch.end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
			game.setScreen(new JuegoNuevo(game));
			dispose();
		}
    }

    @Override
    public void resize(int i, int i1) {
        
    }

    @Override
    public void pause() {
       
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        img.dispose();
    }

    
    
    
}
