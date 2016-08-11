package com.mps.cmis.client.wrapper.operations;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;




public class DownloadDocument {

	private Session session;

	public DownloadDocument(Session session) {
		this.session = session;
	}

	public String downloadDoc(String folderPath, String fileName, String version) throws IOException {
		String path = folderPath + "/"+ fileName;
		try {
			Document doc = (Document) session.getObjectByPath(path);
			String ObjectID = doc.getId();
			ObjectID=createObjectId(ObjectID, version);
			String document = download(ObjectID);
			return document;
		} catch (CmisObjectNotFoundException onfe) {
			System.out.println("Document does not exist:" + onfe);
			return null;
		}
	}


	public String downloadDoc(String version) throws IOException {
		String finalObjectID = null;
		if (version != null) {
			finalObjectID =version.trim();
		}
		String document = download(finalObjectID);
		return document;
	}

	public String download(String objectID) throws IOException {
	
		Document doc = (Document) session.getObject(objectID);
		List<Document> docList=doc.getAllVersions();
		System.out.println(docList);
		List<Property<?>> props = doc.getProperties();
		for (Property<?> p : props) {
			System.out.println(p.getDefinition().getDisplayName() + "=" + p.getValuesAsString());
		}
		ContentStream contentStream = doc.getContentStream();
		//InputStream io = contentStream.getStream();
		String content = null;// returns null if the document has no content
		if (contentStream != null) {
			content = getContentAsString(contentStream);
		} else {
			System.out.println("No content.");
		}
		return content;
	}	

	private String getContentAsString(ContentStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();
		Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");

		try {
			final char[] buffer = new char[4 * 1024];
			int b;
			while (true) {
				b = reader.read(buffer, 0, buffer.length);
				if (b > 0) {
					sb.append(buffer, 0, b);
				} else if (b == -1) {
					break;
				}
			}
		} finally {
			reader.close();
		}

		return sb.toString();
	}
	
	public File getFileFromString(String content, String fileName)
			throws Exception {
		File file = null;
		BufferedWriter bufferedWriter = null;
		try {
			file = new File(fileName);
			Writer writer = new FileWriter(file);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			bufferedWriter.close();
		}
		return file;
	}

	/*public File getFile(String path,String fileName) throws Exception
	{
		String filePath=path+"/"+fileName;
		File file =new File(filePath);
		return file;
	}*/
	
	public String getVersion(String name)
	{
		String version="1.0";
		String regexPattern="[A-Za-z]+_v+[0-9]+.+[A-Za-z]";
		Pattern pattern=Pattern.compile(regexPattern);
		Matcher patternMatcher=pattern.matcher(name);
		if(patternMatcher.find())
		{
			String versionNumber=name.substring(name.lastIndexOf(".")-1,name.lastIndexOf("."));
			 version=versionNumber+".0";
			return version;
		}
		return version;
	}
	
	
	public String[] getDocumentSpecifications(String name) {
		String[] DocumentSpecifications = new String[10];
		String version = "1.0";
		String fileName = name;
		String regexPattern = "[A-Za-z]+_v+[0-9]+.+[A-Za-z]";
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher patternMatcher = pattern.matcher(name);
		if (patternMatcher.find()) {
			fileName = name.substring(0, name.lastIndexOf(".") - 3);
			String extension = name.substring(name.lastIndexOf("."));
			fileName = fileName + extension;

			String versionNumber = name.substring(name.lastIndexOf(".") - 1,name.lastIndexOf("."));
			version = versionNumber + ".0";

		}
		DocumentSpecifications[0] = fileName;
		DocumentSpecifications[1] = version;
		return DocumentSpecifications;
	}
	
	
/*	public List<String> getAllDocuments() throws Exception
	{
		List<String> fileName = new ArrayList<String>();
		String folderPath = "/New Folder/TGL/tgl-content";
		Folder folder = (Folder) session.getObjectByPath(folderPath);
		ItemIterable<CmisObject> list = folder.getChildren();
		for (CmisObject cmisObject : list) {
			Document document = (Document) session.getObjectByPath(folderPath
					+ "/" + cmisObject.getName());
			List<Document> documentList = document.getAllVersions();
			for (Document list1 : documentList) {
				String newFile = createTglFileName(list1.getName(),
						list1.getVersionLabel());
				fileName.add(newFile);
			}

		}
		return fileName;
		}
	
	public String createTglFileName(String fileName,String version)
	{
		String name = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		char versionNumber = version.charAt(0);

		String newFileName = name + "_v" + versionNumber + "." + extension;

		System.out.println(newFileName);
		return newFileName;
		}
	*/
	
	public String getFileName(String name) {
		String fileName = null;
		String regexPattern = "[A-Za-z]+_v+[0-9]+.+[A-Za-z]";
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher patternMatcher = pattern.matcher(name);
		if (patternMatcher.find()) {
			fileName = name.substring(0, name.lastIndexOf(".") - 3);
			String extension = name.substring(name.lastIndexOf("."));
			fileName = fileName + extension;
			return fileName;
		} else {
			fileName = name;
			return fileName;
		}
	}

	public String createObjectId(String previousId, String version) {
		String newObjectID = null;
		String[] splittedObjectID = previousId.split(";");
		newObjectID = splittedObjectID[0] + ";" + version;
		return newObjectID;
	}
}


