/*
 * Copyright (C) 2014 Baptiste Mesta
 */

package com.cloudbees.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Baptiste Mesta
 */
public class DirectoryDiff {


    public static List<Diff> diff(File directory1, File directory2, boolean ignoreWitheSpaces) throws IOException {
        //non recursive
        File[] files = directory1.listFiles();
        List<Diff> diffs = new ArrayList<Diff>();
        for (File file1 : files) {
            File file2 = new File(directory2, file1.getName());
            Diff diff = Diff.diff(file1, file2, ignoreWitheSpaces);
            diffs.add(diff);
        }
        return diffs;
    }

    public static String createPatch(File directory1, File directory2, boolean ignoreWitheSpaces) throws IOException {
        return createPatch(directory1, directory2, ignoreWitheSpaces, "a", "b");
    }

    private static String createPatch(File directory1, File directory2, boolean ignoreWitheSpaces, String path1, String path2) throws IOException {
        //non recursive
        List<String> files = getFiles(directory1, directory2);

        StringBuilder stb = new StringBuilder();
        for (String fileName : files) {
            File file1 = new File(directory1, fileName);
            File file2 = new File(directory2, fileName);
            if (file1.exists() && file1.isDirectory() || file2.exists() && file2.isDirectory()) {
                stb.append(createPatch(file1, file2, ignoreWitheSpaces, path1 + "/" + file1.getName(), path2 + "/" + file2.getName()));
            }
            if((file1.exists() && file1.isFile()) || (file2.exists() && file2.isFile())){


                Diff diff = Diff.diff(getReader(file1), getReader(file2), ignoreWitheSpaces);
                if(!diff.isEmpty()){
                    stb.append(diff.toUnifiedDiff(path1 + "/" + file1.getName(), path2 + "/" + file2.getName(), getReader(file1), getReader(file2), 3));
                }
            }

        }
        return stb.toString();
    }

    private static Reader getReader(File file) throws FileNotFoundException {
        if(file.exists() && file.isFile()){
            return new FileReader(file);
        }else{
            return new StringReader("");
        }
    }

    private static List<String> getFiles(File directory1, File directory2) {
        HashSet<String> names = new HashSet<String>();
        getFilesFromDirectory(directory1, names);
        getFilesFromDirectory(directory2, names);
        ArrayList<String> strings = new ArrayList<String>(names);
        Collections.sort(strings);
        return strings;
    }

    private static void getFilesFromDirectory(File directory1, HashSet<String> names) {
        if( directory1.exists() && directory1.isDirectory()){
            for (File file : directory1.listFiles()) {
                names.add(file.getName());
            }
        }
    }

    public static void apply(File directory, String patch) throws IOException, PatchException {
        ContextualPatch contextualPatch = ContextualPatch.create(patch, directory);
        contextualPatch.patch(false);
    }
}
