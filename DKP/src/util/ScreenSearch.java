package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import static util.SQL.*;

/**
 * @author Aaron
 */
public class ScreenSearch {
    public static BufferedImage CLOSE_ITEM_IMAGE;
    public static BufferedImage MINIMISE_ITEM_IMAGE;
    
    public static BufferedImage CLASS_BARD;
    public static BufferedImage CLASS_BEASTLORD;
    public static BufferedImage CLASS_BERSERKER;
    public static BufferedImage CLASS_CLERIC;
    public static BufferedImage CLASS_DRUID;
    public static BufferedImage CLASS_ENCHANTER;
    public static BufferedImage CLASS_MAGICIAN;
    public static BufferedImage CLASS_MONK;
    public static BufferedImage CLASS_NECROMANCER;
    public static BufferedImage CLASS_PALADIN;
    public static BufferedImage CLASS_RANGER;
    public static BufferedImage CLASS_ROGUE;
    public static BufferedImage CLASS_SHADOW_KNIGHT;
    public static BufferedImage CLASS_SHAMAN;
    public static BufferedImage CLASS_WARRIOR;
    public static BufferedImage CLASS_WIZARD;
    
    public static BufferedImage letterA, letterB, letterC, letterD, letterE, letterF;
    public static BufferedImage letterG, letterH, letterI, letterJ, letterK, letterL;
    public static BufferedImage letterM, letterN, letterO, letterP, letterQ, letterR;
    public static BufferedImage letterS, letterT, letterU, letterV, letterW, letterX;
    public static BufferedImage letterY, letterZ;
    
    public static BufferedImage capitalA, capitalB, capitalC, capitalD, capitalE, capitalF;
    public static BufferedImage capitalG, capitalH, capitalI, capitalJ, capitalK, capitalL;
    public static BufferedImage capitalM, capitalN, capitalO, capitalP, capitalQ, capitalR;
    public static BufferedImage capitalS, capitalT, capitalU, capitalV, capitalW, capitalX;
    public static BufferedImage capitalY, capitalZ;
    
    public static BufferedImage[] letters;
    public static BufferedImage[] capitals;
    public static BufferedImage[] numbers;
    
    public static BufferedImage number0, number1, number2, number3, number4;
    public static BufferedImage number5, number6, number7, number8, number9;
    
    public static BufferedImage tradeWindow, tradeAccept;
    public static BufferedImage platinumCoin;
    
    private static BufferedImage screen;
    private static long[][] sumData;
    private static int[] rgbArray;
    
    static {
        reset();
    }
    
    public static void reset() {
        try {
            CLOSE_ITEM_IMAGE = read("closeItemSegment.bmp");
            MINIMISE_ITEM_IMAGE = read("minimiseItemSegment.bmp");
            
            CLASS_BARD = read("sampleClassBRD2.bmp");
            CLASS_BEASTLORD = read("sampleClassBST2.bmp");
            CLASS_BERSERKER = read("sampleClassBER2.bmp");
            CLASS_CLERIC = read("sampleClassCLR2.bmp");
            CLASS_DRUID = read("sampleClassDRU2.bmp");
            CLASS_ENCHANTER = read("sampleClassENC2.bmp");
            CLASS_MAGICIAN = read("sampleClassMAG2.bmp");
            CLASS_MONK = read("sampleClassMNK2.bmp");
            CLASS_NECROMANCER = read("sampleClassNEC2.bmp");
            CLASS_PALADIN = read("sampleClassPAL2.bmp");
            CLASS_RANGER = read("sampleClassRNG2.bmp");
            CLASS_ROGUE = read("sampleClassROG2.bmp");
            CLASS_SHADOW_KNIGHT = read("sampleClassSHD2.bmp");
            CLASS_SHAMAN = read("sampleClassSHM2.bmp");
            CLASS_WARRIOR = read("sampleClassWAR2.bmp");
            CLASS_WIZARD = read("sampleClassWIZ2.bmp");
            
            // Letters not available will be null
            letterA = read("samples/a.bmp");
            letterB = read("samples/b.bmp");
            letterC = read("samples/c.bmp");
            letterD = read("samples/d.bmp");
            letterE = read("samples/e.bmp");
            letterF = read("samples/f.bmp");
            letterG = read("samples/g.bmp");
            letterH = read("samples/h.bmp");
            letterI = read("samples/i.bmp");
            letterJ = read("samples/j.bmp");
            letterK = read("samples/k.bmp");
            letterL = read("samples/l.bmp");
            letterM = read("samples/m.bmp");
            letterN = read("samples/n.bmp");
            letterO = read("samples/o.bmp");
            letterP = read("samples/p.bmp");
            letterQ = read("samples/q.bmp");
            letterR = read("samples/r.bmp");
            letterS = read("samples/s.bmp");
            letterT = read("samples/t.bmp");
            letterU = read("samples/u.bmp");
            letterV = read("samples/v.bmp");
            letterW = read("samples/w.bmp");
            letterX = read("samples/x.bmp");
            letterY = read("samples/y.bmp");
            letterZ = read("samples/z.bmp");
            
            capitalA = read("samples/capA.bmp");
            capitalB = read("samples/capB.bmp");
            capitalC = read("samples/capC.bmp");
            capitalD = read("samples/capD.bmp");
            capitalE = read("samples/capE.bmp");
            capitalF = read("samples/capF.bmp");
            capitalG = read("samples/capG.bmp");
            capitalH = read("samples/capH.bmp");
            capitalI = read("samples/capI.bmp");
            capitalJ = read("samples/capJ.bmp");
            capitalK = read("samples/capK.bmp");
            capitalL = read("samples/capL.bmp");
            capitalM = read("samples/capM.bmp");
            capitalN = read("samples/capN.bmp");
            capitalO = read("samples/capO.bmp");
            capitalP = read("samples/capP.bmp");
            capitalQ = read("samples/capQ.bmp");
            capitalR = read("samples/capR.bmp");
            capitalS = read("samples/capS.bmp");
            capitalT = read("samples/capT.bmp");
            capitalU = read("samples/capU.bmp");
            capitalV = read("samples/capV.bmp");
            capitalW = read("samples/capW.bmp");
            capitalX = read("samples/capX.bmp");
            capitalY = read("samples/capY.bmp");
            capitalZ = read("samples/capZ.bmp");
            
            number0 = read("samples/0.bmp");
            number1 = read("samples/1.bmp");
            number2 = read("samples/2.bmp");
            number3 = read("samples/3.bmp");
            number4 = read("samples/4.bmp");
            number5 = read("samples/5.bmp");
            number6 = read("samples/6.bmp");
            number7 = read("samples/7.bmp");
            number8 = read("samples/8.bmp");
            number9 = read("samples/9.bmp");
            
            letters = new BufferedImage[] {
                letterA, letterB, letterC, letterD, letterE, letterF,
                letterG, letterH, letterI, letterJ, letterK, letterL,
                letterM, letterN, letterO, letterP, letterQ, letterR,
                letterS, letterT, letterU, letterV, letterW, letterX,
                letterY, letterZ
            };
            capitals = new BufferedImage[] {
                capitalA, capitalB, capitalC, capitalD, capitalE, capitalF,
                capitalG, capitalH, capitalI, capitalJ, capitalK, capitalL,
                capitalM, capitalN, capitalO, capitalP, capitalQ, capitalR,
                capitalS, capitalT, capitalU, capitalV, capitalW, capitalX,
                capitalY, capitalZ
            };
            numbers = new BufferedImage[] {
                number0, number1, number2, number3, number4, 
                number5, number6, number7, number8, number9
            };
            
            tradeWindow = read("samples/sampleTradeWindow.bmp");
            platinumCoin = read("samples/samplePlatinum.bmp");
            tradeAccept = read("samples/sampleTradeButton.bmp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static BufferedImage read(String s) {
        try {
            return ImageIO.read(new File(s));
        } catch (Exception e) {
            System.out.println("Failed to load: " + s);
        }
        return null;
    }
    
    public static ArrayList<String> findClasses() {
        ArrayList<String> classList = new ArrayList<>();
        
        buildTextScreenshot();
        
        if (findText(CLASS_BARD) != null) classList.add(BARD);
        if (findText(CLASS_BEASTLORD) != null) classList.add(BEASTLORD);
        if (findText(CLASS_BERSERKER) != null) classList.add(BERZERKER);
        if (findText(CLASS_CLERIC) != null) classList.add(CLERIC);
        if (findText(CLASS_DRUID) != null) classList.add(DRUID);
        if (findText(CLASS_ENCHANTER) != null) classList.add(ENCHANTER);
        if (findText(CLASS_MAGICIAN) != null) classList.add(MAGICIAN);
        if (findText(CLASS_MONK) != null) classList.add(MONK);
        if (findText(CLASS_NECROMANCER) != null) classList.add(NECROMANCER);
        if (findText(CLASS_PALADIN) != null) classList.add(PALADIN);
        if (findText(CLASS_RANGER) != null) classList.add(RANGER);
        if (findText(CLASS_ROGUE) != null) classList.add(ROGUE);
        if (findText(CLASS_SHADOW_KNIGHT) != null) classList.add(SHADOWKNIGHT);
        if (findText(CLASS_SHAMAN) != null) classList.add(SHAMAN);
        if (findText(CLASS_WARRIOR) != null) classList.add(WARRIOR);
        if (findText(CLASS_WIZARD) != null) classList.add(WIZARD);
        
        return classList;
    }
    
    public static Point findText(BufferedImage image) {
        ArrayList<Point> ps = find(filterWhite(image), null);
        
        if (ps.isEmpty()) return null;
        return ps.get(0);
    }
    
    public static Point findRedText(BufferedImage image) {
        ArrayList<Point> ps = find(filterRed(image), null);
        
        if (ps.isEmpty()) return null;
        return ps.get(0);
    }
    
    public static BufferedImage scale(BufferedImage b, double xScale, double yScale) {
        Image img = b.getScaledInstance((int)(b.getWidth()*xScale), (int)(b.getHeight()*yScale), BufferedImage.SCALE_DEFAULT);
        
        BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bImage.getGraphics().drawImage(img, 0, 0, null);
        
        return bImage;
    }
    
    public static void buildTextScreenshot() {
        try {
            Robot robot = new Robot();
            BufferedImage screenshot = robot.createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            
            // Eliminate everything but bold text
            for (int x = 0; x < screenshot.getWidth(); x++) {
                for (int y = 0; y < screenshot.getHeight(); y++) {
                    int rgb = screenshot.getRGB(x, y);
                    if (((rgb >> 16) & 0xFF) < 150 && ((rgb >> 8) & 0xFF) < 150 && (rgb & 0xFF) < 150) {
                        screenshot.setRGB(x, y, 0);
                    } else {
                        screenshot.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            
            screen = screenshot;
            buildSumArray(screenshot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void buildRedTextScreenshot() {
        try {
            Robot robot = new Robot();
            BufferedImage screenshot = robot.createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            
            // Eliminate everything but bold text
            for (int x = 0; x < screenshot.getWidth(); x++) {
                for (int y = 0; y < screenshot.getHeight(); y++) {
                    int rgb = screenshot.getRGB(x, y);
                    if (((rgb >> 16) & 0xFF) < 150) {
                        screenshot.setRGB(x, y, 0);
                    } else {
                        screenshot.setRGB(x, y, Color.RED.getRGB());
                    }
                }
            }
            
            screen = screenshot;
            buildSumArray(screenshot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static long getDifference(BufferedImage a, BufferedImage b) {
        long dif = 0;
        
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                dif += Math.abs(getLuminosity(a.getRGB(x, y)) - getLuminosity(b.getRGB(x, y)));
            }
        }
        
        return dif;
    }
    
    public static void buildNewScreenshot() {
        try {
            Robot robot = new Robot();
            BufferedImage screenshot = robot.createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            
            screen = screenshot;
            buildSumArray(screenshot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void buildSumArray(BufferedImage b) {
        int width = b.getWidth();
        int height = b.getHeight();
        
        // Initialize the structure if it doesn't exist or doesn't match the previous format
        if ((sumData == null) || (sumData.length != width) || (sumData[0].length != height)) {
            sumData = new long[width][height];
            rgbArray = new int[width*height];
        }
            
        // Don't bother clearing the data, it will all get overriden anyway.
        rgbArray = b.getRGB(0, 0, width, height, rgbArray, 0, width);
        
        // For each pixel sum the rectangle it makes with the origin
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                long pixel = getLuminosity(rgbArray[y*width + x]);
                
                // Special case: First number
                if (x == 0 && y == 0) {
                    sumData[x][y] = pixel;
                    continue;
                }

                // Special case: On the top
                if (y == 0) {
                    sumData[x][y] = sumData[x-1][y] + pixel;
                    continue;
                }

                // Special case: On the left
                if (x == 0) {
                    sumData[x][y] = sumData[x][y-1] + pixel;
                    continue;
                }

                sumData[x][y] = sumData[x-1][y] + sumData[x][y-1] - sumData[x-1][y-1] + pixel;
            }
        }
    }
    
    public static long getSum(BufferedImage b) {
        long sum = 0;
        
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                sum += getLuminosity(b.getRGB(x, y));
            }
        }
        
        return sum;
    }
    
    public static long getSumFromScreen(int x, int y, int w, int h) {
        int x2 = x + (w - 1);
        int y2 = y + (h - 1);
        
        return sumData[x2][y2] + sumData[x-1][y-1] - sumData[x2][y-1] - sumData[x-1][y2];
    }
    
    public static long getLuminosity(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        
        return (r * 3 + g * 6 + b) / 10;
    }
    
    public static String findPattern(Rectangle area) {
        ScreenSearch ss = new ScreenSearch();
        ArrayList<Entry> entries = new ArrayList<>();
        Graphics graphics = screen.getGraphics();
        graphics.setColor(Color.BLACK);
        
        for (int i = 0; i < capitals.length; i++) {
            BufferedImage letter = capitals[i];
            
            ArrayList<Point> ps = find(filterRed(letter), area);
            for (Point p : ps) {
                graphics.fillRect(p.x, p.y, letter.getWidth(), letter.getHeight());
                entries.add(ss.new Entry(p, ""+(char)('A' + i)));
            }
        }
        for (int i = 0; i < letters.length; i++) {
            BufferedImage letter = letters[i];

            ArrayList<Point> ps = find(filterRed(letter), area);
            for (Point p : ps) {
                graphics.fillRect(p.x, p.y, letter.getWidth(), letter.getHeight());
                entries.add(ss.new Entry(p, ""+(char)('a' + i)));
            }
        }
        
        String pattern = buildPattern(entries);
        
        
        // Because I and lower L looks the same in Arial, replace them depending on their position
        /*if (!pattern.isEmpty()) {
            if (pattern.charAt(0) == 'l') pattern = "I" + pattern.substring(1);
            if (pattern.substring(1).contains("I"))
                pattern = pattern.charAt(0) + pattern.substring(1).replaceAll("I", "l");
        }*/
        
        return pattern;
    }
    
    public static  String findNumber(Rectangle area) {
        ScreenSearch ss = new ScreenSearch();
        ArrayList<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < numbers.length; i++) {
            BufferedImage number = numbers[i];
            
            ArrayList<Point> ps = find(filterWhite(number), area);
            for (Point p : ps) {
                entries.add(ss.new Entry(p, ""+i));
            }
        }
        
        return buildPattern(entries);
    }
    
    public class Entry {
        public Point p;
        public String letter;
        
        public Entry(Point a, String c) {
            p = a;
            letter = c;
        }
    }
    
    public static String buildPattern(ArrayList<Entry> matches) {
        String pattern = "";
        int min = 999999;
        int index = -1;
        while (matches.size() > 0) {
            min = 999999;
            
            // Count backwards to avoid on-delete errors
            for (int i = matches.size()-1; i >= 0; i--) {
                Point q = matches.get(i).p;
                
                if (q.x < min) {
                    min = q.x;
                    index = i;
                }
            }
            
            pattern += matches.remove(index).letter;
        }
        
        return pattern;
    }
    
    public static ArrayList<Point> find(BufferedImage sample, Rectangle rect) { 
        if (rect == null) rect = new Rectangle(0, 0, screen.getWidth(), screen.getHeight());
        
        ArrayList<Point> points = new ArrayList<>();
        if (sample == null) return points;
        
        // Calculate the sum from the sample
        long sampleSum = getSum(sample);
        
        // Match the sum against the samples and compare each close match
        int width = sample.getWidth();
        int height = sample.getHeight();
        
        for (int x = Math.max(1, rect.x); x < Math.min(sumData.length - width - 1, rect.x + rect.width); x++) {
            for (int y = Math.max(1, rect.y); y < Math.min(sumData[0].length - height - 1, rect.y + rect.height); y++) {
                long sum = getSumFromScreen(x, y, width, height);
                
                if (Math.abs(sum-sampleSum) < 1) {
                    // Perform close check
                    long difference = getDifference(sample, screen.getSubimage(x, y, width, height));
                    
                    if (difference < 1) {
                        points.add(new Point(x, y));
                        x += width;
                        y += height;
                    }
                }
            }
        }
        
        return points;
    }
    
    public static BufferedImage filterRed(BufferedImage sample) {
        if (sample == null) return null;
        try {
            for (int x = 0; x < sample.getWidth(); x++) {
                for (int y = 0; y < sample.getHeight(); y++) {
                    int rgb = sample.getRGB(x, y);
                    if (((rgb >> 16) & 0xFF) < 150) {
                        sample.setRGB(x, y, 0);
                    } else {
                        sample.setRGB(x, y, Color.RED.getRGB());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sample;
    }
    
    public static BufferedImage filterWhite(BufferedImage sample) {
        if (sample == null) return null;
        try {
            for (int x = 0; x < sample.getWidth(); x++) {
                for (int y = 0; y < sample.getHeight(); y++) {
                    int rgb = sample.getRGB(x, y);
                    if (((rgb >> 16) & 0xFF) < 150 && ((rgb >> 8) & 0xFF) < 150 && (rgb & 0xFF) < 150) {
                        sample.setRGB(x, y, 0);
                    } else {
                        sample.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sample;
    }
}
