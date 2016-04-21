package signatureanalysis;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;
import java.io.File;

/**
 * SignatureAnalysisSuite - a tool for creating simple signature models, that
 * provides some limited analysis functionality by counting matching pixels.
 * 
 * Provided for Dr. Bourbakis - Professor of CEG-4110: Software Engineering
 * 
 * @author William Hatfield
 * @author Wiehan Boshoff
 * @author Muath Alhendi
 * @author Brandon Head
 * @author Carly Rolfes
 */
public class SignatureAnalysis {
    /**************************************************************************/
    /*
        the working directories used for storing signatures models and images
    */
    private static final String USER_TEMPLATES = "signatures/templates/";
    private static final String USER_FORGERIES = "signatures/forgeries/";
    private static final String MODEL_DATABASE = "signatures/models/";
    /**************************************************************************/
    /*
        change this number to allow for more (or less) system login attempts
    */
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    /**************************************************************************/
    /*
        the message that is displayed to the user at the startup of this program
    */
    private static final String SYSTEM_LOGIN_PROMPT = ""
    + "This is the signature analysis tool developed for CEG-4110: Software Engineering\n"
    + "For testing purposes their exist only four users within the simulated database:\n"
    + "\n"
    + "User: Head, Brandon \t\tLogin: brandon\n"
    + "User: Rolfes, Carly \t\tLogin: carly\n"
    + "User: Boshoff, Wiehan \t\tLogin: wiehan\n"
    + "User: Alhendi, Muath \t\tLogin: muath\n"
    + "\n"
    + "When prompted enter a valid login to review signature comparison results,\n"
    + "each user has each provided 2-3 valid signatures and the 2-3 forged user\n"
    + "signatures were provided by the other team members. The output displayed\n"
    + "will included the filename and path to the signature image being compared\n"
    + "compared to that users 'model signature', which is a composite image that\n"
    + "produced by layering the (valid) user provided signature images over top\n"
    + "one another. With that in mind, as more valid signatures are provided the\n"
    + "percentage of matching pixels will decrease for both the valid and forged\n"
    + "images being tested. After some testing it has come to be known that the\n"
    + "75% matching pixel ratio was very ambitous given this type of comparison.\n"
    + "So the thershold at which a signature is considered valid has been moved\n"
    + "to 2%, which may sound low. But relatively compared to the forgery scores\n"
    + "within this system that are around ~0.0001-5%, the 2% mark is very high.\n";
    /**************************************************************************/

    /**
     * The Main Method - used as a simple CLI for the Signature Analysis Suite.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(SYSTEM_LOGIN_PROMPT);
        Scanner kbin = new Scanner(System.in);
        int selection = -1;
        while (true) {
            switch (selection) {
                case -1: {
                    // get the user selection and break from switch loop again
                    selection = getUserSelection(kbin);
                    break;
                }
                case 0: {
                    System.out.println("Exiting Signature Analysis Program...");
                    System.exit(0);
                }
                case 1: {
                    viewComparisonResultsOfValidUser(
                            MAX_LOGIN_ATTEMPTS, kbin, MODEL_DATABASE,
                            USER_TEMPLATES, USER_FORGERIES);
                    selection = getUserSelection(kbin);
                    break;
                }
                default: {
                    System.err.println("ERROR: Default Case Reached In Switch");
                    System.err.println("Exiting Signature Analysis Program...");
                    System.exit(1);
                }
            }
        }
    }
    
    /**
     * Prompts the user for input, reads in from the terminal and error checks
     * the users input to ensure selection validity.
     * 
     * @param keyboard  to get the user input from
     * @return          the users selection
     */
    @SuppressWarnings("empty-statement")
    private static int getUserSelection(Scanner keyboard) {
        //
        int numberOfSelections = 2; // change this as selection options change
        //
        String userSelectionPrompt = ""
        + "Enter the index number of your desired action:\n"
        + "0: Exit the Signature Analysis Program\n"
        + "1: View Comparison Results of a User\n";
        //
        System.out.println(userSelectionPrompt);
        System.out.print("Enter Index: ");
        //
        String line = keyboard.nextLine();
        try {
            int sel = Integer.parseInt(line);
            if (sel < 0 || sel > numberOfSelections) {
                throw new IndexOutOfBoundsException();
            } else {
                return sel;
            }
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("ERROR: Must Enter Listed Index, Try Again ...");
            System.err.println("");
            for (int i = 1000000; i > 0; i--);  // busy wait on printing
            return -1;
        } catch (NumberFormatException ex) {
            System.err.println("ERROR: Must Enter An Integer, Try Again ...");
            System.err.println("");
            for (int i = 1000000; i > 0; i--);  // busy wait on printing
            return -1;
        } 
    }
    
    /**
     * Method that is used to access signature models and then display results
     * of the valid and forged signature images compared to the model image.
     * 
     * @param loginAttempts
     * @param keyboard
     * @param models 
     */
    @SuppressWarnings("empty-statement")
    private static void viewComparisonResultsOfValidUser(int loginAttempts, 
            Scanner keyboard, String models, String templates, String forgeries) {
        //
        String userLoginPrompt = ""
        + "Please enter a valid username to view comparison results. This may\n"
        + "be one that was listed in the initial program prompt, or a new user\n"
        + "that has recently been created and has an associated model file.\n"
        + "\n"
        + "For your convenience the default login options are as follows:\n"
        + "User: Head, Brandon \t\tLogin: brandon\n"
        + "User: Rolfes, Carly \t\tLogin: carly\n"
        + "User: Boshoff, Wiehan \t\tLogin: wiehan\n"
        + "User: Alhendi, Muath \t\tLogin: muath\n";
        //
        System.out.println(userLoginPrompt);
        //
        String[] logins = new File(models).list();
        //
        for (int i = 0; i <= loginAttempts; i++) {
            //
            if (i == loginAttempts) {
                System.err.println("ERROR: You Have Reached The Maximum Login Attempts");
                System.err.println("Exiting The Program ...");
                System.exit(0);
            }
            //
            System.out.print("Enter Login: ");
            String line = keyboard.nextLine();
            int space = line.indexOf(" ");
            //
            String user = (space != -1) ? line.substring(0, space) : line;
            for (String login : logins) {
                String loginLo = login.toLowerCase().replace(".png", "");
                String userLo = user.toLowerCase().replace(".png", "");
                if (loginLo.equals(userLo)) {
                    //
                    BufferedImage model = getUserModelFromDir(userLo, models);
                    BufferedImage[] validSignatures = getSignaturesFromDir(userLo, templates);
                    BufferedImage[] forgedSignatures = getSignaturesFromDir(userLo, forgeries);
                    //
                    System.out.println("VALID SIGNATURE RESULTS FOR USER: " + userLo);
                    compareAndDisplayResults(model, validSignatures);
                    for (int j = 1000000; j > 0; j--);  // busy wait on printing
                    System.out.println("");
                    //
                    System.out.println("FORGED SIGNATURE RESULTS FOR USER: " + userLo);
                    compareAndDisplayResults(model, forgedSignatures);
                    for (int j = 1000000; j > 0; j--);  // busy wait on printing
                    System.out.println("");
                    //
                    return;
                }
            }
        }
    }
    
    /**
     * Method used to access the models directory and retrieve the model image.
     * 
     * @param user
     * @param dir
     * @return 
     */
    private static BufferedImage getUserModelFromDir(String user, String dir) {
        try {
            String[] model_signatures = new File(dir).list();
            for (String model_file : model_signatures) {
                if (model_file.contains(user)) {
                    return ImageIO.read(new File(dir + model_file));
                }
            }
        } catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * Method used to retrieve a users signature images from a directory, either
     * the valid or forged signatures are returned depending on passed argument.
     * 
     * @param user
     * @param dir
     * @return 
     */
    private static BufferedImage[] getSignaturesFromDir(String user, String dir) {
        String[] templateSubDirectories = new File(dir).list();
        String[] userTemplateImages = null;
        //
        for (String userTemplateDir : templateSubDirectories) {
            if (userTemplateDir.toLowerCase().contains(user.toLowerCase())) {
                userTemplateImages = new File(dir + userTemplateDir).list();
                for (int i = 0; i < userTemplateImages.length; i++) {
                    String file = userTemplateImages[i];
                    userTemplateImages[i] = dir + userTemplateDir + '/' + file;
                }
                break;
            }
        }
        //
        if (userTemplateImages == null) {
            System.err.println("ERROR: Failed To Access User Signature Data");
            System.err.println("Exiting The Program ...");
            System.exit(0);
        }
        //
        BufferedImage[] templates = new BufferedImage[userTemplateImages.length];
        for (int i = 0; i < templates.length; i++) {
            try {
                templates[i] = ImageIO.read(new File(userTemplateImages[i]));
            } catch (IOException ex) {
                System.err.println("IOException: " + ex.getMessage());
                System.err.println("ERROR: When Accessing Templates");
                System.err.println("Exiting The Program ...");
                System.exit(0);
            }
        }
        //
        return templates;
    }
    
    /**
     * Method that calls the computing methods and then displays the results.
     * 
     * @param model
     * @param templates 
     */
    @SuppressWarnings("empty-statement")
    private static void compareAndDisplayResults(BufferedImage model, BufferedImage[] templates) {
        for (BufferedImage template : templates) {
            System.out.println("Template Comparison Result: " + compareSigs(template, model));
        }
    }
    
    /**
     * Completes the signature comparison by getting the number of matching 
     * pixels between the images and telling whether there is a min 75% match.
     * 
     * @param sig
     * @param model
     * @return 
     */
    private static double compareSigs(BufferedImage sig, BufferedImage model) {
        int total = model.getWidth() * model.getHeight();
        int matches = countMatches(sig, model);
        //return ((double)matches / (double)total) >= 0.75;
        return (double)matches / (double)total;
    }
    
    /**
     * Counts the number of matching pixels by iterating through both images and
     * comparing the integer value of the pixels in each image, if they are the
     * same the counter is incremented, finally the counter is returned.
     * 
     * @param sig
     * @param model
     * @return 
     */
    private static int countMatches(BufferedImage sig, BufferedImage model) {
        int matching_pixels = 0;
        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                try {
                    int mod_rgb = model.getRGB(x, y);
                    int sig_rgb = sig.getRGB(x, y);
                    if (mod_rgb == sig_rgb && mod_rgb <= Color.GRAY.getRGB()) {
                        matching_pixels++;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {}
            }
        }
        return matching_pixels;
    }
        
}
