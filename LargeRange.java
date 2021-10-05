package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Scrapes an Apple website with a large number of possible pages for images of
 * Apple products.
 * 
 * @author nilsstreedain
 *
 */
public class AppleProductScraper {
	
	/**
	 * Main method to loop over possible image URL that might exist, then, if they do
	 * exist, saves the image.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 1000000; i++) {
			try {
				// Find the URL of a possible image on the webpage and open a connection.
				URLConnection connection = new URL("https://km.support.apple.com/kb/image.jsp?productid=" + i).openConnection();
				
				// Create an input stream so the redirected URL can be found.
				InputStream stream = connection.getInputStream();
				
				// Save the redirected URL.
				URL redirectedURL = connection.getURL();
				
				if (!redirectedURL.equals(new URL("https://support.apple.com/kb/resources/images/image_notfound_72x72.png")))
					saveImageFromInputStream(stream, filenameFromURL(redirectedURL, i));
				else
					System.out.println(i + " exists but there is no image.");
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves image found at given URL and names it filename.
	 * 
	 * @param url      - URL to find image at
	 * @param filename - Name to save image under
	 */
	public static void saveImageFromInputStream(InputStream stream, String filename) {
		try {
			// Copies image from the stream to a filepath on the computer.
			Files.copy(stream, Paths.get("/Users/nilsstreedain/products/" + filename));
			System.out.println(filename + " successfully saved.");
		} catch (FileAlreadyExistsException e) {
			System.out.println(filename + " already exists.");
		} catch (FileSystemException e) {
			System.out.println(filename.substring(0, 100) + " is an invalid image.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the filename of an image given the images URL.
	 * 
	 * @param url - URL to parse for a filename
	 * @return filename - The parsed filename
	 */
	public static String filenameFromURL(URL url, int productNumber) {
		// Turns the URL into a string
		String filename = url.toString();
		// Parses the filename out of the URL string.
		filename = filename.substring(filename.lastIndexOf("/") + 1);
		return productNumber + " " + filename;
	}
}