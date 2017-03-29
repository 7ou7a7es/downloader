package extracteur.balado;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PodcastXmlExtractor {

	public static void main(String[] args) {

		try {

			URL website = new URL("http://radiofrance-podcast.net/podcast09/rss_10076.xml");

			ReadableByteChannel channel = Channels.newChannel(website.openStream());
			File fXmlFile = new File("src/main/resources/rss_10076.xml");
			FileOutputStream stream = new FileOutputStream(fXmlFile);

			stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("item");

			for (int index = 0; index < nList.getLength(); index++) {
				Node nNode = nList.item(index);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Element guidNode = (Element) eElement.getElementsByTagName("guid").item(0);

					Element titleNode = (Element) eElement.getElementsByTagName("title").item(0);

					Element dateNode = (Element) eElement.getElementsByTagName("pubDate").item(0);

					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

					String url = guidNode.getTextContent();

					String title = titleNode.getTextContent();

					title = dateFormat.format(format.parse(dateNode.getTextContent())) + "--" + title.replace('/', '|')
							+ ".mp3";

					File file = new File(title);

					String[] splitUrl = url.split("/");
					String fileName = splitUrl[splitUrl.length - 1];

					File fileDownload = new File(fileName);

					if (url != null && !file.exists()) {

						System.out.println("URL : " + url);
						System.out.println("Title : " + title);

						// Proxy connection
						// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
						// InetSocketAddress("fr-proxy.groupinfra.com", 3128));
						// URLConnection conn = new
						// URL(url).openConnection(proxy);

						URLConnection conn = new URL(url).openConnection();

						InputStream is = conn.getInputStream();

						OutputStream outstream = new FileOutputStream(fileDownload);
						byte[] buffer = new byte[4096];
						int len;
						while ((len = is.read(buffer)) > 0) {
							outstream.write(buffer, 0, len);
						}
						outstream.close();

						if (!fileDownload.renameTo(file)) {
							System.err.println("Can't rename : " + fileName + " to : " + title);
						}
					} else {
						System.err.println("File : " + title + " exists or URL : " + url + " is null.");
					}

				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
