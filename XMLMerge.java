

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class XMLMerge {
  
	public static void main(String args [])
	{
		try {
			 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         
	         Document one = dBuilder.parse(new File("License1.xml"));
	         Document two = dBuilder.parse(new File("License2.xml"));

	         NodeList node1 = one.getElementsByTagName("License");
	         NodeList node2 = two.getElementsByTagName("License");
	         

	         BufferedWriter validLicenseWriter = new BufferedWriter(new FileWriter("ValidLicenses.txt"));
	         BufferedWriter invalidLicenseWriter = new BufferedWriter(new FileWriter("InvalidLicenses.txt"));
	         BufferedWriter MergedFileWriter = new BufferedWriter(new FileWriter("Merged.txt"));
	         

	            for (int i = 0; i < node1.getLength(); i++) {
	                Element license1 = (Element) node1.item(i);
	                String key1 = obtainKey(license1);
	                for (int j = 0; j < node2.getLength(); j++) {
	                    Element license2 = (Element) node2.item(j);
	                    String key2 = obtainKey(license2);
	                    if (key1.equals(key2)) {
	                        if (isValidLicense(license1)) {
	                            MergedFileWriter.write("\n \n Valid License");
	                            writeLicenseToFile(license1, validLicenseWriter);
	                            writeLicenseToFile(license1, MergedFileWriter);

	                        } else {
	                            MergedFileWriter.write("\n \n InValid License");
	                            writeLicenseToFile(license1, invalidLicenseWriter);
	                            writeLicenseToFile(license1, MergedFileWriter);

	                        }
	                        break;
	                    }
	                }
	            }
	            validLicenseWriter.close();
	            invalidLicenseWriter.close();
	            MergedFileWriter.close();
		}
		
		catch (Exception e) {
            e.printStackTrace();
        }
	}

	private static String obtainKey(Element license) {
        return license.getAttribute("NIPR_Number") + ","
                + license.getAttribute("State_Code") + ","
                + license.getAttribute("License_Number") + ","
                + license.getAttribute("License_Expiration_Date");
    }

    private static boolean isValidLicense(Element license) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String expirationDateString = license.getAttribute("License_Expiration_Date");
        LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
        return expirationDate.isAfter(currentDate) || expirationDate.isEqual(currentDate);
    }

    private static void writeLicenseToFile(Element license, BufferedWriter writer) throws IOException {
        String line = license.getAttribute("NIPR_Number") + ","
                + license.getAttribute("License_Number") + ","
                + license.getAttribute("State_Code") + ","
                + license.getAttribute("Resident_Indicator") + ","
                + license.getAttribute("License_Class") + ","
                + license.getAttribute("License_Issue_Date") + ","
                + license.getAttribute("License_Expiration_Date") + ","
                + license.getAttribute("License_Status");
        NodeList loaList = license.getElementsByTagName("LOA");
        for (int k = 0; k < loaList.getLength(); k++) {
            Element loa = (Element) loaList.item(k);
            String loaLine = line + ","
                    + loa.getAttribute("LOA_Name") + ","
                    + loa.getAttribute("LOA_Issue_Date") + ","
                    + loa.getAttribute("LOA_Expiration_Date") + ","
                    + loa.getAttribute("LOA_Status");
            writer.newLine();
            writer.write(loaLine);
        }
    }
}
