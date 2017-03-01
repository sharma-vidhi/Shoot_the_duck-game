package shoot_the_duck;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;



public class Game {
    
   
    private Random random;
    
    
    private Font font;
    
    
    private ArrayList<Duck> ducks;
    
    
    private int runawayDucks;
    private int killedDucks;
    private int score;
    private int shoots;
    
    private long lastTimeShoot;    
    
    private long timeBetweenShots;

    private BufferedImage backgroundImg;
    
 //   private BufferedImage grassImg;
    
   
    private BufferedImage duckImg;
    
    
    private BufferedImage sightImg;
    
    private int sightImgMiddleWidth;
    
    private int sightImgMiddleHeight;
    

    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                
                Initialize();
               
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
  
    private void Initialize()
    {
        random = new Random();        
        font = new Font("monospaced", Font.BOLD, 18);
        
        ducks = new ArrayList<Duck>();
       
        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;
        lastTimeShoot = 0;
        timeBetweenShots = Framework.secInNanosec / 3;
    }
    
   
    private void LoadContent()
    {
        try
        {
            URL backgroundImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/background.jpg");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
           // URL grassImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/grass.png");
            //grassImg = ImageIO.read(grassImgUrl);
            
            URL duckImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/duck.png");
            duckImg = ImageIO.read(duckImgUrl);
            
            URL sightImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;
        }
        catch (IOException ex) {
           
        }
    }
    
    
    
    public void RestartGame()
    {
        
        ducks.clear();
        
        
        Duck.lastDuckTime = 0;
        killedDucks=0;
        score=0;
        shoots=0;
        runawayDucks = 0;
       
        
        lastTimeShoot = 0;
    }
    
   
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        
        if(System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks)
        {
            
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200), Duck.duckLines[Duck.nextDuckLines][1], Duck.duckLines[Duck.nextDuckLines][2], killedDucks, duckImg));
            
            
            Duck.nextDuckLines++;
            if(Duck.nextDuckLines >= Duck.duckLines.length)
                Duck.nextDuckLines = 0;
            
            Duck.lastDuckTime = System.nanoTime();
        }
        
       
        for(int i = 0; i < ducks.size(); i++)
        {
            
            ducks.get(i).Update();
            
           
            if(ducks.get(i).x < 0 - duckImg.getWidth())
            {
                ducks.remove(i);
                runawayDucks++;
            }
        }
        
        
     
        
        
        
        if(Canvas.mouseButtonState(MouseEvent.BUTTON1))
        {
           
            if(System.nanoTime() - lastTimeShoot >= timeBetweenShots)
            {
            	shoots++;
                for(int i = 0; i < ducks.size(); i++)
                {
                   
                    if(new Rectangle(ducks.get(i).x + 18, ducks.get(i).y     , 27, 30).contains(mousePosition) ||
                       new Rectangle(ducks.get(i).x + 30, ducks.get(i).y + 30, 88, 25).contains(mousePosition))
                    {
                    	
                    	killedDucks++;
                        score += ducks.get(i).score;
                        
                        ducks.remove(i);
                        
                        
                        break;
                    }
                }
                
                lastTimeShoot = System.nanoTime();
            }
        }
        if(runawayDucks >= 70)
            Framework.gameState = Framework.GameState.GAMEOVER;
             
    }
    
    
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        
        for(int i = 0; i < ducks.size(); i++)
        {
            ducks.get(i).Draw(g2d);
        }
        
       // g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);
        
        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);
        
        g2d.setFont(font);
        g2d.setColor(Color.black);
        
        g2d.drawString("RUNAWAY: " + runawayDucks, 10, 21);
        g2d.drawString("KILLS: " + killedDucks, 160, 21);
        g2d.drawString("SHOOTS: " + shoots, 299, 21);
        g2d.drawString("SCORE: " + score, 440, 21);
        
        
    }
    
    
    
    public void DrawGameOver(Graphics2D g2d, Point mousePosition)
    {
        Draw(g2d, mousePosition);
        
        
        g2d.setColor(Color.black);
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 39, (int)(Framework.frameHeight * 0.65) + 1);
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 149, (int)(Framework.frameHeight * 0.70) + 1);
        g2d.setColor(Color.red);
        g2d.drawString("Game Over", Framework.frameWidth / 2 - 40, (int)(Framework.frameHeight * 0.65));
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.70));
    }
}
