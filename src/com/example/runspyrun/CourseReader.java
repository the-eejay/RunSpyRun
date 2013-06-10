package com.example.runspyrun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class CourseReader {
	/** This class reads courses from the user's external storage
	 *  and stores the courses in its own list.
	 */
	private List<Course> courses = new ArrayList<Course>();
	private File file;
	
	
	public CourseReader(Context context) throws IOException {
		if (isExternalStorageReadable()) {
			file = new File(context.getExternalFilesDir(null), "courses.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			
			Course course = new Course();
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens[0].startsWith("END")) {
					// End of course
					courses.add(course);
					course = new Course();
				} else if (tokens[0].equals("mine")) {
					// Add a mine to the course
					PoiBean mine = new PoiBean(
							"3",
							"Detection Plate",
							"Detection Plate",
							3,
							Double.parseDouble(tokens[1]),
							Double.parseDouble(tokens[2]),
							22.0
					);
					course.addMine(mine);
				} else if (tokens[0].equals("hackin")) {
					// Add a Hack In Point
					PoiBean hackIn = new PoiBean(
							"1",
							"Hack In Point",
							"Hack In Point",
							1,
							Double.parseDouble(tokens[1]),
							Double.parseDouble(tokens[2]),
							22.0
					);
					course.setHackInPoint(hackIn);
				} else if (tokens[0].equals("hackout")) {
					// Add a Hack Out Point
					PoiBean hackOut = new PoiBean(
							"2",
							"Hack Out Point",
							"Hack Out Point",
							2,
							Double.parseDouble(tokens[1]),
							Double.parseDouble(tokens[2]),
							22.0
					);
					course.setHackOutPoint(hackOut);
				} else if (tokens[0].startsWith("Name:")) {
					course.setName(tokens[0].split(":")[1]);
				}
			}
		}
			
		
	}


    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    public ArrayList<Course> getCourses() {
    	return new ArrayList<Course>(courses);
    }
}
