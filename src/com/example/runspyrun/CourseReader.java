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

public class CourseReader {
	private List<Course> courses = new ArrayList<Course>();
	private File file;
	
	
	public CourseReader(Context context) throws IOException {
		if (isExternalStorageReadable()) {
			file = new File(context.getExternalFilesDir(null), "courses.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("File read.");
			reader.close();
		
		} else {
			System.out.println("File not readable.");
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
}
