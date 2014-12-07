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
        createFile(folder1, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(folder1, "myFile1.xml", "<xml>", "</xml>");

        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line3", "line2", "line4", "toto");
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
        createFile(folder1, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(folder1, "myFile1.xml", "<xml>", "</xml>");
        File subFolder1 = createFolder(folder1, "subFolder");
        createFile(subFolder1, "myFile1.xml", "<xml>", "</xml>");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line3", "line2", "line4", "toto");
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
    public void create_and_apply_patch() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(folder1, "myFile1.xml", "<xml>", "</xml>");
        createFile(folder1, "myFile2.xml", "<xml>", "</xml>");
        createFile(folder1, "myFile3.xml", "<xml>", "</xml>");
        createFile(folder1, "myFile4.xml", "<xml>", "</xml>");
        createFile(folder1, "myFile5.xml", "<xml>", "</xml>");
        createFile(folder1, "myFile6.xml", "<xml>", "</xml>");
        File subFolder1 = createFolder(folder1, "subFolder1");
        createFile(subFolder1, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(subFolder1, "myFile1.xml", "<xml>", "</xml>");
        File subFolder2 = createFolder(folder1, "subFolder2");
        createFile(subFolder2, "myFile1.txt", "line1", "line2", "toto", "tata");
        File subFolder3 = createFolder(folder1, "subFolder3");
        createFile(subFolder3, "myFile1.txt", "line1", "line2", "toto", "tata");
        File subFolder4 = createFolder(folder1, "subFolder4");
        createFile(subFolder4, "myFile1.txt", "line1", "line2", "toto", "tata");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(folder2, "myFile1.xml", "<xml>", "plop", "</xmaql>");
        createFile(folder2, "myFile2.xml", "<xml>", "</xmzl>");
        createFile(folder2, "myFile3.xml", "<xml>", "</xml>");
        createFile(folder2, "myFile4.xml", "<xml>", "</xmdsl>");
        createFile(folder2, "myFile5.xml", "<xml>", "</dsqxml>");
        createFile(folder2, "myFile6.xml", "<xdsqml>", "</xml>");
        File subFolder11 = createFolder(folder2, "subFolder");
        createFile(subFolder11, "myFile1.txt", "line1", "line2", "toto", "tata");
        createFile(subFolder11, "myFile11.xml", "<xml>", "</xml>");
        File subFolder12 = createFolder(folder2, "subFolder1");
        createFile(subFolder12, "myFile1.txt", "line1", "line2","line3", "toto", "tata");
        File subFolder13 = createFolder(folder2, "subFolder2");
        createFile(subFolder13, "myFile1.txt", "line1", "line2", "toto", "tata");
        File subFolder14 = createFolder(folder2, "subFolder4");
        createFile(subFolder14, "myFile11.txt", "line1", "line2", "toto", "tata");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);


        DirectoryDiff.apply(folder2,patch);

        assertThat(DirectoryDiff.createPatch(folder1,folder2,true)).isEmpty();

    }


    @Test
    public void create_patch_with_missing_file_in_2() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2", "toto", "tata");


        File folder2 = temporaryFolder.newFolder("folder2");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("--- a/myFile1.txt\n" +
                "+++ b/myFile1.txt\n" +
                "@@ -1,4 +0,0 @@\n" +
                "-line1\n" +
                "-line2\n" +
                "-toto\n" +
                "-tata\n");
    }

    @Test
    public void no_diff() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");
        createFile(folder1, "myFile1.txt", "line1", "line2", "toto", "tata");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line2", "toto", "tata");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("");
    }


    @Test
    public void create_patch_with_missing_file_in_1() throws Exception {
        File folder1 = temporaryFolder.newFolder("folder1");


        File folder2 = temporaryFolder.newFolder("folder2");
        createFile(folder2, "myFile1.txt", "line1", "line2", "toto", "tata");


        String patch = DirectoryDiff.createPatch(folder1, folder2, true);

        assertThat(patch).isEqualTo("--- a/myFile1.txt\n" +
                "+++ b/myFile1.txt\n" +
                "@@ -0,0 +1,4 @@\n" +
                "+line1\n" +
                "+line2\n" +
                "+toto\n" +
                "+tata\n");
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