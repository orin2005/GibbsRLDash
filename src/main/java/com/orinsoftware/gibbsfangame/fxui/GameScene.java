package com.orinsoftware.gibbsfangame.fxui;

import java.util.Objects;

import com.orinsoftware.gibbsfangame.BoostPeanut;
import com.orinsoftware.gibbsfangame.GameManager;
import com.orinsoftware.gibbsfangame.Gibbs;
import com.orinsoftware.gibbsfangame.Platform;
import com.orinsoftware.gibbsfangame.RLDSprite;

import javafx.scene.canvas.Canvas;

public class GameScene extends Canvas {
	
	private GameManager manager;
	
	public static final double WIDTH = 1200;
	public static final double HEIGHT = 800;
	
	private long lastNanoTime;
	
	public GameScene() {
		super(WIDTH,600);
		
		this.setVisible(true);
		manager = GameManager.getInstance();
		generateFirstTwoPlatforms();
		lastNanoTime = System.nanoTime();
	}
	
	public void update( double delta )
	{
		
		double t = (delta - lastNanoTime) / 1000000000.0;
		checkCollisions();
		manager.updateAll( t );
		generatePlatforms();
		
		//manager.getObjects().stream().filter(Objects::nonNull).forEach( obj -> obj.update(t));
		
		
		
		
		manager.renderAll( this.getGraphicsContext2D() );
		
		lastNanoTime = System.nanoTime();
	}
	
	private void generateFirstTwoPlatforms()
	{
		manager.addObject( new Platform(GameScene.WIDTH/2, 300) );
		manager.generateNewPlatform();
	}
	
	private void generatePlatforms()
	{	
		if( manager.getObjects().isEmpty() )
		{
			generateFirstTwoPlatforms();
		}
		if( manager.getObjects().stream().mapToDouble( platform-> platform.getPositionX() ).max().getAsDouble() < manager.getPlayer().getPositionX()+ 2000)
			manager.generateNewPlatform();
	}
	
	private void checkCollisions()
	{
		
		Gibbs gibbs = manager.getPlayer();
	
		boolean shouldBeFalling = true;
		for( RLDSprite object : manager.getObjects() )
		{
			
			if( gibbs.intersects(object) && gibbs != object )
			{
				if( object instanceof Platform)
				{
					Platform platform = (Platform)object;
					if( gibbs.getPositionY() + gibbs.getHeight() >= platform.getPositionY() 
							&& (gibbs.getPositionX()+gibbs.getWidth() >= platform.getPositionX()
									&& gibbs.getPositionX() < platform.getPositionX() + platform.getWidth()))
					{
						shouldBeFalling = false;
						gibbs.setDoubleJump(true);
						gibbs.setPositionY( platform.getPositionY() - gibbs.getHeight()+1 );
					}
				}
				else
				{
					BoostPeanut peanut = (BoostPeanut)object;
					peanut.setPickedUp( true );
					gibbs.consume(peanut);
				}
			}
		}
		gibbs.setFalling(shouldBeFalling);
	}
	


}
