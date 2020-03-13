package code;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import visual.WorkoutTimerVisual;

public class QuickTest {

	
	public static void main(String[] args) {
		CodeSource src = WorkoutTimerVisual.class.getProtectionDomain().getCodeSource();
		URL jar = src.getLocation();
		ZipInputStream zip;
		try {
			zip = new ZipInputStream(jar.openStream());
			while(true) {
			    ZipEntry e = zip.getNextEntry();
			    if (e == null)
			      break;
			    String name = e.getName();
			    System.out.println(name);
			    if (name.startsWith("path/to/your/dir/")) {
			      /* Do something with this entry. */
			    }
			}
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
