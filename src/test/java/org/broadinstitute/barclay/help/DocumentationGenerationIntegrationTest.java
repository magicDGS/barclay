package org.broadinstitute.barclay.help;

import org.broadinstitute.barclay.help.testdoclets.TestDoclet;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Integration test for documentation generation.
 */
public class DocumentationGenerationIntegrationTest {

    private static String inputResourcesDir = "src/main/resources/org/broadinstitute/barclay/";
    private static String testResourcesDir = "src/test/resources/org/broadinstitute/barclay/";

    private static final String indexFileName = "index";
    private static final String jsonFileExtension = ".json";

    // common arguments not changed for tests
    private static final List<String> COMMON_DOC_ARG_LIST = Arrays.asList(
            "-build-timestamp", "2016/01/01 01:01:01",      // dummy, constant timestamp
            "-absolute-version", "11.1",                    // dummy version
            "-docletpath", "build/libs",
            "-sourcepath", "src/test/java",
            "org.broadinstitute.barclay.help.testinputs",
            "org.broadinstitute.barclay.argparser",
            "-verbose",
            "-cp", System.getProperty("java.class.path")
    );

    private static final List<String> EXPECTED_OUTPUT_FILE_NAME_PREFIXES = Arrays.asList(
            "org_broadinstitute_barclay_help_testinputs_TestArgumentContainer",
            "org_broadinstitute_barclay_help_testinputs_TestExtraDocs"
    );

    private static List<String> docArgList(final Class<?> docletClass, final File templatesFolder, final File outputDir,
            final String indexFileExtension, final String outputFileExtension) {

        // set the common arguments
        final List<String> docArgList = new ArrayList<>(COMMON_DOC_ARG_LIST);

        // set the templates
        if ( templatesFolder != null ) {
            docArgList.add("-settings-dir");
            docArgList.add(templatesFolder.getAbsolutePath());
        }

        // set the output directory
        docArgList.add("-d");
        docArgList.add(outputDir.getAbsolutePath());

        // set the doclet class
        docArgList.add("-doclet");
        docArgList.add(docletClass.getName());

        // set the index and output extension
        docArgList.add("-index-file-extension");
        docArgList.add(indexFileExtension);
        docArgList.add("-output-file-extension");
        docArgList.add(outputFileExtension);

        return docArgList;
    }

    @DataProvider
    public Object[][] getDocGenTestParams() {
        return new Object[][] {
                // default doclet and templates
                {HelpDoclet.class,
                        new File(inputResourcesDir + "helpTemplates/"),
                        new File(testResourcesDir + "help/expected/HelpDoclet"),
                        indexFileName,
                        "html", // testIndexFileExtension
                        "html", // testOutputFileExtension
                        "html", // requestedIndexFileExtension
                        "html", // requestedOutputFileExtension
                        new String[] {}, // customDocletArgs
                        false    // onlyTestIndex
                },
                // defaut doclet and templates using alternate index extension
                {HelpDoclet.class,
                        new File(inputResourcesDir + "helpTemplates/"),
                        new File(testResourcesDir + "help/expected/HelpDoclet"),
                        indexFileName,
                        "html",  // testIndexFileExtension
                        "html",  // testOutputFileExtension
                        "xhtml", // requestedIndexFileExtension
                        "html",  // requestedOutputFileExtension
                        new String[] {}, // customDocletArgs
                        false     // onlyTestIndex
                },
                // custom doclet and templates
                {TestDoclet.class,
                        new File(testResourcesDir + "help/templates/TestDoclet/"),
                        new File(testResourcesDir + "help/expected/TestDoclet"),
                        indexFileName,
                        "html", // testIndexFileExtension
                        "html", // testOutputFileExtension
                        "html", // requestedIndexFileExtension
                        "html", // requestedOutputFileExtension
                        new String[] {}, // customDocletArgs
                        false    // onlyTestIndex
                },
                // custom bash doclet and templates
                {BashTabCompletionDoclet.class,
                        new File(inputResourcesDir + "helpTemplates/"),
                        new File(testResourcesDir + "help/expected/BashTabCompletionDoclet"),
                        "bashTabCompletionDocletTestLaunch-completion",
                        "sh", // testIndexFileExtension
                        "sh", // testOutputFileExtension
                        "sh", // requestedIndexFileExtension
                        "sh", // requestedOutputFileExtension
                        new String[] {    // customDocletArgs
                                "-caller-script-name",         "bashTabCompletionDocletTestLaunch.sh",

                                // Test with these off for now:
                                "-caller-pre-legal-args",      "--pre-help --pre-info --pre-inputFile",
                                "-caller-pre-arg-val-types",   "null null File",
                                "-caller-pre-mutex-args",      "--pre-help;pre-info,pre-inputFile --pre-info;pre-help,pre-inputFile",
                                "-caller-pre-alias-args",      "--pre-help;-prh --pre-inputFile;-prif",
                                "-caller-pre-arg-min-occurs",  "0 0 1",
                                "-caller-pre-arg-max-occurs",  "1 1 1",

                                "-caller-post-legal-args",     "--post-help --post-info --post-inputFile",
                                "-caller-post-arg-val-types",  "null null File",
                                "-caller-post-mutex-args",     "--post-help;post-info,post-inputFile --post-info;post-help,post-inputFile",
                                "-caller-post-alias-args",     "--post-help;-poh --post-inputFile;-poif",
                                "-caller-post-arg-min-occurs", "0 0 1",
                                "-caller-post-arg-max-occurs", "1 1 1",
                        },
                        true  // onlyTestIndex
                },

                //==============================================================================================================

                // default doclet and templates from classpath
                {HelpDoclet.class,
                        null,
                        new File(testResourcesDir + "help/expected/HelpDoclet"),
                        indexFileName,
                        "html", // testIndexFileExtension
                        "html", // testOutputFileExtension
                        "html", // requestedIndexFileExtension
                        "html", // requestedOutputFileExtension
                        new String[] {"-use-default-templates"}, // customDocletArgs
                        false    // onlyTestIndex
                },
                // defaut doclet and templates from classpath using alternate index extension
                {HelpDoclet.class,
                        null,
                        new File(testResourcesDir + "help/expected/HelpDoclet"),
                        indexFileName,
                        "html",  // testIndexFileExtension
                        "html",  // testOutputFileExtension
                        "xhtml", // requestedIndexFileExtension
                        "html",  // requestedOutputFileExtension
                        new String[] { "-use-default-templates" }, // customDocletArgs
                        false     // onlyTestIndex
                },
                // custom bash doclet pulling templates from classpath
                {BashTabCompletionDoclet.class,
                        null,
                        new File(testResourcesDir + "help/expected/BashTabCompletionDoclet"),
                        "bashTabCompletionDocletTestLaunch-completion",
                        "sh", // testIndexFileExtension
                        "sh", // testOutputFileExtension
                        "sh", // requestedIndexFileExtension
                        "sh", // requestedOutputFileExtension
                        new String[] {    // customDocletArgs
                                "-caller-script-name",         "bashTabCompletionDocletTestLaunch.sh",

                                "-use-default-templates",

                                // Test with these off for now:
                                "-caller-pre-legal-args",      "--pre-help --pre-info --pre-inputFile",
                                "-caller-pre-arg-val-types",   "null null File",
                                "-caller-pre-mutex-args",      "--pre-help;pre-info,pre-inputFile --pre-info;pre-help,pre-inputFile",
                                "-caller-pre-alias-args",      "--pre-help;-prh --pre-inputFile;-prif",
                                "-caller-pre-arg-min-occurs",  "0 0 1",
                                "-caller-pre-arg-max-occurs",  "1 1 1",

                                "-caller-post-legal-args",     "--post-help --post-info --post-inputFile",
                                "-caller-post-arg-val-types",  "null null File",
                                "-caller-post-mutex-args",     "--post-help;post-info,post-inputFile --post-info;post-help,post-inputFile",
                                "-caller-post-alias-args",     "--post-help;-poh --post-inputFile;-poif",
                                "-caller-post-arg-min-occurs", "0 0 1",
                                "-caller-post-arg-max-occurs", "1 1 1",
                        },
                        true  // onlyTestIndex
                },
                // custom bash doclet pulling templates from classpath, Mostly defaults
                {BashTabCompletionDoclet.class,
                        null,
                        new File(testResourcesDir + "help/expected/BashTabCompletionDoclet"),
                        "bashTabCompletionDocletTestLaunchWithDefaults-completion",
                        "sh", // testIndexFileExtension
                        "sh", // testOutputFileExtension
                        "sh", // requestedIndexFileExtension
                        "sh", // requestedOutputFileExtension
                        new String[] {  // customDocletArgs
                                "-caller-script-name",         "bashTabCompletionDocletTestLaunchWithDefaults.sh",
                                "-use-default-templates"
                        },
                        true  // onlyTestIndex
                },
        };
    }

    @Test(dataProvider = "getDocGenTestParams")
    public void testDocGenRoundTrip(
            final Class<?> docletClass,
            final File inputTemplatesFolder,
            final File expectedDir,
            final String indexFileBaseName,
            final String testIndexFileExtension,
            final String testOutputFileExtension,
            final String requestedIndexFileExtension,
            final String requestedOutputFileExtension,
            final String[] customDocletArgs,
            final boolean onlyTestIndex
    ) throws IOException
    {
        // creates a temp output directory
        final File outputDir = Files.createTempDirectory(docletClass.getName()).toAbsolutePath().toFile();
        outputDir.deleteOnExit();

        // pull all our arguments together:
        List<String> javadocArgs = docArgList(docletClass, inputTemplatesFolder, outputDir, requestedIndexFileExtension, requestedOutputFileExtension);
        for (int i = 0 ; i < customDocletArgs.length; ++i) {
            javadocArgs.add(customDocletArgs[i]);
        }

        // run javadoc with the custom doclet
        com.sun.tools.javadoc.Main.execute(
                javadocArgs.toArray(new String[] {})
        );

        // Compare index files
        assertFileContentsIdentical(
                new File(outputDir, indexFileBaseName + "." + requestedIndexFileExtension),
                new File(expectedDir, indexFileBaseName + "." + testIndexFileExtension));

        // Only compare other output files if we should have them:
        if ( !onlyTestIndex ) {
            // Compare output files (json and workunit)
            for (final String workUnitFileNamePrefix : EXPECTED_OUTPUT_FILE_NAME_PREFIXES) {
                //check json file
                assertFileContentsIdentical(
                        new File(outputDir, workUnitFileNamePrefix + jsonFileExtension),
                        new File(expectedDir, workUnitFileNamePrefix + jsonFileExtension));
                // check workunit output file
                assertFileContentsIdentical(
                        new File(outputDir, workUnitFileNamePrefix + "." + requestedOutputFileExtension),
                        new File(expectedDir, workUnitFileNamePrefix + "." + testOutputFileExtension));
            }
        }
    }

    private void assertFileContentsIdentical(
            final File actualFile,
            final File expectedFile) throws IOException {
        byte[] actualBytes = Files.readAllBytes(actualFile.toPath());
        byte[] expectedBytes = Files.readAllBytes(expectedFile.toPath());
        Assert.assertEquals(actualBytes, expectedBytes);
    }
}
