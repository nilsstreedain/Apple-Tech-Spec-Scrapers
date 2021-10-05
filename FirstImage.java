package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Scrapes an Apple website with a small number of possible pages for images of
 * Apple products.
 * 
 * @author nilsstreedain
 *
 */
public class SmallAppleProductScraper {

	/**
	 * Main method to loop over possible webpages that might exist, then, if they do
	 * exist, saves the first image on the page.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Loops over range of possible webpage numbers.
		for (int i = 500; i < 1000; i++) {
			try {
				// Find the URL of the first image appearing on the webpage.
				URL imgURL = getImageURL(new URL("https://support.apple.com/kb/SP" + i));

				// If an image exists, save the image.
				if (imgURL != null)
					saveImageFromURL(imgURL, filenameFromURL(imgURL, i));
				else
					System.out.println(i + " exists but there is no image.");

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the URL of the first image on a webpage from a specified URL. If no
	 * image is found, null is returned.
	 * 
	 * @param url - URL of webpage to scrape
	 */
	public static URL getImageURL(URL url) {

		try {
			// Creates a buffered reader to go through each line of HTML on the webpage.
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			// Loops over each line of HTML until an image is found.
			String currentLine;
			while ((currentLine = reader.readLine()) != null)
				if (currentLine.contains("<img")) {
					// Parses the line of HTML containing the image source
					currentLine = currentLine.substring(currentLine.indexOf("src=")).split("\"")[1];
					// If the source begins in https:// or http:// the source is returned directly.
					if (currentLine.startsWith("https://") || currentLine.startsWith("http://"))
						return new URL(currentLine);
					// Otherwise it is added to the end of the website domain.
					else
						return new URL("https://support.apple.com" + currentLine);
				}

		} catch (FileNotFoundException e) {
			System.out.println(url + " was not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// If no image is found, null is returned.
		return null;
	}

	/**
	 * Saves image found at given URL and names it filename.
	 * 
	 * @param url      - URL to find image at
	 * @param filename - Name to save image under
	 */
	public static void saveImageFromURL(URL url, String filename) {
		try {
			// Creates an input stream from a URL to save an image.
			InputStream stream = url.openStream();
			// Copies image from the stream to a filepath on the computer.
			Files.copy(stream, Paths.get("/Users/nilsstreedain/products2/" + filename));
			stream.close();
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
