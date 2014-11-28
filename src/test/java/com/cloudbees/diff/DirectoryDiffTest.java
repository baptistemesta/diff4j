/*
 * Copyright (C) 2014 Baptiste Mesta
 */

package com.cloudbees.diff;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DirectoryDiffTest {


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void create_patch_of_a_folder() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2","toto","tata");
        createFile(folder1, "myFile1.xml", "<xml>", "</xml>");

        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line3","line2","line4","toto");
        createFile(folder2, "myFile1.xml", "<xml>", "\t<plop>kikoo</plop>", "</xml>");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("--- a/myFile1.txt\n" +
                "+++ b/myFile1.txt\n" +
                "@@ -1,4 +1,5 @@\n" +
                " line1\n" +
                "+line3\n" +
                " line2\n" +
                "+line4\n" +
                " toto\n" +
                "-tata\n" +
                "--- a/myFile1.xml\n" +
                "+++ b/myFile1.xml\n" +
                "@@ -1,2 +1,3 @@\n" +
                " <xml>\n" +
                "+\t<plop>kikoo</plop>\n" +
                " </xml>\n");
    }


    @Test
    public void create_patch_recursive() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2","toto","tata");
        createFile(folder1, "myFile1.xml", "<xml>", "</xml>");
        File subFolder1 = createFolder(folder1, "subFolder");
        createFile(subFolder1, "myFile1.xml", "<xml>", "</xml>");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line3","line2","line4","toto");
        createFile(folder2, "myFile1.xml", "<xml>", "\t<plop>kikoo</plop>", "</xml>");
        File subFolder2 = createFolder(folder2, "subFolder");
        createFile(subFolder2, "myFile1.xml", "<xml>", "\t<plop>kikoo</plop>", "</xml>");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("--- a/myFile1.txt\n" +
                "+++ b/myFile1.txt\n" +
                "@@ -1,4 +1,5 @@\n" +
                " line1\n" +
                "+line3\n" +
                " line2\n" +
                "+line4\n" +
                " toto\n" +
                "-tata\n" +
                "--- a/myFile1.xml\n" +
                "+++ b/myFile1.xml\n" +
                "@@ -1,2 +1,3 @@\n" +
                " <xml>\n" +
                "+\t<plop>kikoo</plop>\n" +
                " </xml>\n" +
                "--- a/subFolder/myFile1.xml\n" +
                "+++ b/subFolder/myFile1.xml\n" +
                "@@ -1,2 +1,3 @@\n" +
                " <xml>\n" +
                "+\t<plop>kikoo</plop>\n" +
                " </xml>\n");
    }


    @Test
    public void create_patch_with_missing_file_in_2() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2","toto","tata");


        File folder2 = temporaryFolder.newFolder("folder2");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("");
    }


    @Test
    public void create_patch_with_missing_file_in_1() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line2","toto","tata");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("");
    }

    private File createFolder(File folder1, String subFolder1) {
        File file = new File(folder1, subFolder1);
        file.mkdir();
        return file;
    }

    private void createFile(File folder1, String child, String... lines) throws IOException {
        File file = new File(folder1, child);
        FileUtils.writeLines(file, Arrays.asList(lines));
    }
}