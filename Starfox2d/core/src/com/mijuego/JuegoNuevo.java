package com.mijuego;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class JuegoNuevo implements Screen {
        final Starfox game;
        private Texture img;
        private Texture imgAsteroide;
        private Texture imgDisparo;
        private Texture imgDispEnmy;
        private Texture imgNavEnmy;
        private Texture imagenMiNave;
        private Texture imgBonus;
        private Music musica;
        private Sound tonoDJ;
        private Sound tonoDE;
        private Sound tonoColision;
        private Sound tonoDestruccion;
        private OrthographicCamera camara;
        private Rectangle miNave;
        private Rectangle naveEnmy;
        private Rectangle dispActual;
        private Rectangle bonus;
        private Array<Rectangle> disparos;
        private Array<Rectangle> asteroides;
        private Array<Rectangle> dispEnmys;
        private long tiempoUltimoMeteor;
        private long tiempoDisparoJug;
        private long tiempoDisparoEnmy;
        private int vidaJug;
        private BitmapFont fuente;
        private int vidaEnem;
        private int xRes, yRes;
        private BitmapFont fuenteCont;
        private int derrotados;
        private int disparosBonus;
        


        
        public  JuegoNuevo (final Starfox game) {
            this.game=game;
            xRes=800;
            yRes=600;
            img = new Texture("fondo.jpg");

            // carga las imÃ¡genes 
            imgAsteroide = new Texture(Gdx.files.internal("asteroid.png"));
            imgDisparo = new Texture(Gdx.files.internal("shoot.png"));
            imagenMiNave = new Texture(Gdx.files.internal("sprite1.png"));
            imgNavEnmy = new Texture(Gdx.files.internal("sprite2.png"));
            imgDispEnmy = new Texture(Gdx.files.internal("shoot2.png"));
            imgBonus = new Texture(Gdx.files.internal("bonus.png"));
            fuente = new BitmapFont();
            fuenteCont = new BitmapFont();
            tonoDJ= Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
            tonoDE= Gdx.audio.newSound(Gdx.files.internal("shoote.wav"));
            tonoColision= Gdx.audio.newSound(Gdx.files.internal("colision.wav"));
            tonoDestruccion= Gdx.audio.newSound(Gdx.files.internal("colision.wav"));
            musica = Gdx.audio.newMusic(Gdx.files.internal("musica.mp3"));
            musica.setLooping(true);
            //musica.play();
            
            // crea la cÃ¡mara ortogrÃ¡fica y el lote de sprites
            camara = new OrthographicCamera();
            camara.setToOrtho(false, xRes, yRes);

            // crea un rectÃ¡ngulo (clase Rectangle) para representar lÃ³gicamente nave
            miNave = new Rectangle();
            miNave.x = xRes / 2 - 64 / 2; // centra 
            miNave.y = 20; // esquina inferior izquierda  a 20 pÃ­xeles del lÃ­mite inferior
            miNave.width = 64;
            miNave.height = 64;
            
            //Creo un bonus fuera de pantalla
            bonus = new Rectangle();
            bonus.width=32;
            bonus.height=32;
            bonus.x=xRes-1000;
            bonus.y=yRes-1000;
            
            crearNaveEnemiga();    
            // crea el vector de asteroides y crea el primer
            asteroides = new Array<Rectangle>();
            crearAsteroide();
            disparos = new Array<Rectangle>();
            dispActual = new Rectangle();
            dispEnmys = new Array<Rectangle>();
            vidaJug=500;
            vidaEnem=500;
            derrotados=0;
            disparosBonus=0;
            
            
        }

        @Override
        public void render (float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
         
            // ordenada a la cÃ¡mara actualizar sus matrices
            camara.update();

            // indica al lote de sprites que se represente en las coordenadas especÃ­ficas de la cÃ¡mara
            game.batch.setProjectionMatrix(camara.combined);
            
            // comienza un nuevo proceso y dibuja 
            game.batch.begin();
            game.batch.draw(img, 0, 0);
            game.batch.draw(imagenMiNave, miNave.x, miNave.y);
            game.batch.draw(imgNavEnmy, naveEnmy.x, naveEnmy.y);
            game.batch.draw(imgBonus, bonus.x, bonus.y);
            fuente.draw(game.batch, ""+vidaJug, miNave.x-5, miNave.y+69);
            fuente.draw(game.batch, ""+vidaEnem, naveEnmy.x-5, naveEnmy.y);
            fuenteCont.draw(game.batch, "Derrotados: "+derrotados, 40, 40);
            for(Rectangle aster: asteroides) {
               game.batch.draw(imgAsteroide, aster.x-8, aster.y-8);
            }
            
            for(Rectangle disp: disparos) {
               game.batch.draw(imgDisparo, disp.x, disp.y);
            }
            
            for(Rectangle disp: dispEnmys) {
               game.batch.draw(imgDispEnmy, disp.x, disp.y);
            }
            
            game.batch.end();

            // lectura de entrada
            if(Gdx.input.isTouched()) {
               Vector3 posicionTocada = new Vector3();
               posicionTocada.set(Gdx.input.getX(), Gdx.input.getY(), 0);
               camara.unproject(posicionTocada);
               miNave.x = posicionTocada.x - 64 / 2;
               miNave.y=posicionTocada.y - 64/2;
            }
            if(Gdx.input.isKeyPressed(Keys.LEFT)) miNave.x -= 400 * Gdx.graphics.getDeltaTime();
            if(Gdx.input.isKeyPressed(Keys.RIGHT)) miNave.x += 400 * Gdx.graphics.getDeltaTime();
            if(Gdx.input.isKeyPressed(Keys.DOWN)) miNave.y -= 400 * Gdx.graphics.getDeltaTime();
            if(Gdx.input.isKeyPressed(Keys.UP)) miNave.y += 400 * Gdx.graphics.getDeltaTime();
            if(Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isTouched()){
                if(disparosBonus>0){
                    if(TimeUtils.nanoTime() - tiempoDisparoJug > 50000000){
                        disparar();
                        disparosBonus--;
                    }
                }else
                    if(TimeUtils.nanoTime() - tiempoDisparoJug > 500000000)
                        disparar();
                
            }    
            // nos aseguramos de que el cubo permanezca entre los lÃ­mites de la pantalla
            if(miNave.x < 0) miNave.x = 0;
            if(miNave.x  > xRes - 96) miNave.x = xRes - 96;

            if(miNave.y < 0) miNave.y = 0;
            if(miNave.y > yRes - 96) miNave.y = yRes - 96;
            
            
            
            //igual para la nave enemiga
            if(naveEnmy.x < 0) naveEnmy.x = 0;
            if(naveEnmy.x  > xRes - 96) naveEnmy.x = xRes - 96;

            if(naveEnmy.y < yRes/2) naveEnmy.y = yRes/2;
            if(naveEnmy.y > yRes - 96) naveEnmy.y = yRes - 96;
            
            
            
            // comprueba si ha pasado un segundo desde la Ãºltima asteroide, para crear una nueva
            if(TimeUtils.nanoTime() - tiempoUltimoMeteor > 1200000000) {
                if(MathUtils.randomBoolean()==false) 
                    crearAsteroide();
            }
            if(TimeUtils.nanoTime() - tiempoDisparoEnmy > 1000000000*10){ 
                if(MathUtils.randomBoolean())enmyDispara();
            }
            
            //disparos mios: 
             Iterator<Rectangle> dIter = disparos.iterator();
            while(dIter.hasNext()){
                Rectangle disp = dIter.next();
                disp.y+=400*Gdx.graphics.getDeltaTime();
                if(disp.y==yRes+20){
                    dIter.remove();
                }
                if(disp.overlaps(naveEnmy)){
                    tonoColision.play();
                    dIter.remove();
                    vidaEnem-=50;
                }
            }
            
            // recorre las asteroides y borra aquellas que hayan llegado al suelo (lÃ­mite inferior de la pantalla) o toquen la nave.
            Iterator<Rectangle> iter = asteroides.iterator();
            while(iter.hasNext()) {
               Rectangle astActual = iter.next();
               
               if(MathUtils.randomBoolean()) 
                   astActual.y -= 50 * Gdx.graphics.getDeltaTime();
               else
                    astActual.y -= 100 * Gdx.graphics.getDeltaTime();
               
               if(astActual.y + 64 < 0) iter.remove();
               if(astActual.overlaps(miNave)) {
                  tonoColision.play();
                  iter.remove();
                  vidaJug= vidaJug-20;
               }
               if(astActual.overlaps(dispActual)){
                   iter.remove();
                   dispActual.x=-1000;
               }
            }
            

            //disparos enemigos
            Iterator<Rectangle> edIter = dispEnmys.iterator();
            while(edIter.hasNext()){
                Rectangle disp = edIter.next();
                disp.y-=350*Gdx.graphics.getDeltaTime();
                movEnemigo();
                if(disp.overlaps(miNave)){
                    tonoColision.play();
                    edIter.remove();
                    vidaJug=vidaJug-100;
                }
            }
            bonus.y-=50*Gdx.graphics.getDeltaTime();
            
            if(miNave.overlaps(bonus)){
                disparosBonus=50;
                bonus.x=xRes-1000;
                bonus.y=yRes-1000;
            }
            if(miNave.overlaps(naveEnmy)){
                tonoColision.play();
                vidaJug=vidaJug-200;
                vidaEnem-=50;
            }
            
            if(bonus.y==0){
                bonus.x=xRes-1000;
                bonus.y=yRes-1000;
            }
           
            
            if(vidaEnem<=0){
                tonoDestruccion.play();
                creaBonus();
                crearNaveEnemiga();
                derrotados++;
            }
            
            if(vidaJug<=0){
                tonoDestruccion.play();
                game.setScreen(new GameOver(game));
                dispose();
            }

        }
        


        private void movEnemigo(){
                if(MathUtils.randomBoolean()){
                     naveEnmy.x -= 80 * Gdx.graphics.getDeltaTime();
                }else{
                    naveEnmy.x += 80 * Gdx.graphics.getDeltaTime();
                }
           
                if(MathUtils.randomBoolean()){
                    naveEnmy.y -= 20 * Gdx.graphics.getDeltaTime();
                }else{
                    naveEnmy.y += 20 * Gdx.graphics.getDeltaTime();
                }
            
            
        }
        
        private void crearAsteroide() {
            Rectangle aster = new Rectangle();
            aster.x = MathUtils.random(0, xRes-64);
            aster.y = yRes;
            aster.width = 32;
            aster.height = 32;
            asteroides.add(aster);
            tiempoUltimoMeteor = TimeUtils.nanoTime();
            
       }
        private void crearNaveEnemiga(){
           naveEnmy = new Rectangle();
           naveEnmy.x = xRes/2 - 64 /2;
           naveEnmy.y = yRes-64;
           naveEnmy.width=64;
           naveEnmy.height=64;
           vidaEnem=1000;
           movEnemigo();
        }
        
        private void creaBonus(){
            float posX, posY;
            posX=naveEnmy.x;
            posY=naveEnmy.y;
            bonus.x=posX;
            bonus.y=posY;
                
        }
        
       private void disparar(){
           Rectangle disparo = new Rectangle();
           float pos;
           pos = miNave.x;
           disparo.x = pos+16;
           disparo.y = miNave.y;
           disparo.width = 32;
           disparo.height = 128;
           disparos.add(disparo);
           tiempoDisparoJug = TimeUtils.nanoTime();
           this.dispActual = disparo;
           tonoDJ.play();
           
       } 
       
       private void enmyDispara(){
           Rectangle disparo = new Rectangle();
           float pos;
           pos = naveEnmy.x;
           disparo.x = pos+16;
           disparo.y = naveEnmy.y;
           disparo.width = 32;
           disparo.height = 64;
           dispEnmys.add(disparo);
           tiempoDisparoEnmy = TimeUtils.nanoTime();
           tonoDE.play();
           
       } 
       
       
       
        @Override
        public void show() {
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
        public void dispose () {
            img.dispose();
            imgAsteroide.dispose();
            imgDisparo.dispose();
            imgDispEnmy.dispose();
            imgNavEnmy.dispose();
            imagenMiNave.dispose();
            imgBonus.dispose();
            musica.dispose();
            tonoDJ.dispose();
            tonoDE.dispose();
            fuente.dispose();
            fuenteCont.dispose();
        }
}





