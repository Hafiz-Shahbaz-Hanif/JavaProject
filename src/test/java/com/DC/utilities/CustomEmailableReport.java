package com.DC.utilities;

import com.DC.testcases.BaseClass;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.DC.utilities.DateUtility.getCurrentDateTime;


/**
 * Reporter that generates a single-page HTML report of the test results.
 */
public class CustomEmailableReport implements IReporter {

    private BaseClass baseclass = new BaseClass();

    private static final Logger LOG = Logger.getLogger(CustomEmailableReport.class);

    protected PrintWriter writer;

    protected final List<SuiteResult> suiteResults = Lists.newArrayList();

    // Reusable buffer
    private final StringBuilder buffer = new StringBuilder();

    private String dReportTitle = "Digital Commerce Summary Report";

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            writer = createWriter(outputDirectory);
        } catch (IOException e) {
            LOG.error("Unable to create output file", e);
            return;
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();

        writer.close();
    }

    protected PrintWriter createWriter(String outdir) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());//time stamp
        String dReportFileName = "DC_Summary_Report" + "-" + timeStamp + ".html";
        new File(outdir).mkdirs();
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, dReportFileName))));
    }

    protected void writeReportTitle(String title) {
        writer.println("<center><h1>" + title + " - " + getCurrentDateTime() + "</h1></center>");
    }

    protected void writeDocumentStart() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writer.println("<head>");
        writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writer.println("<title>TestNG Report</title>");
        writeStylesheet();
        writer.println("</head>");
    }

    protected void writeStylesheet() {
        writer.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("#summary {margin-top:30px}");
        writer.print("h1 {font-size:30px}");
        writer.print("body {width:100%;}");
        writer.print("th,td {padding: 8px}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold;color:#0D1EB6;}");
        writer.print(".easy-overview {margin-left: auto; margin-right: auto;} ");
        writer.print(".easy-test-overview tr:first-child {background-color:#D3D3D3}");
        writer.print(".stripe td {background-color: #E6EBF9}");
        writer.print(".num {text-align:right}");
        writer.print(".passedodd td {background-color: #3F3}");
        writer.print(".passedeven td {background-color: #0A0}");
        writer.print(".skippedodd td {background-color: #DDD}");
        writer.print(".skippedeven td {background-color: #CCC}");
        writer.print(".failedodd td,.attn {background-color: #F33}");
        writer.print(".failedeven td,.stripe .attn {background-color: #D00}");
        writer.print(".stacktrace {font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print(".invisible {display:none}");
        writer.println("</style>");
    }

    protected void writeBody() {
        writer.println("<body>");
        writeReportTitle(dReportTitle);
        writeSuiteSummary();
        writeScenarioSummary();
        writeScenarioDetails();
        writer.println("</body>");
    }

    protected void writeDocumentEnd() {
        writer.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;

        writer.println("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"table-bordered easy-overview\">");
        writer.print("<tr>");
        writer.print("<th>Pod</th>");
        writer.print("<th># Total Test Cases</th>");
        writer.print("<th># Test Cases Passed</th>");
        writer.print("<th># Test Cases Skipped</th>");
        writer.print("<th># Test Cases Failed</th>");
        writer.print("<th># Browser</th>");
        writer.print("<th># Start Time</th>");
        writer.print("<th># End Time</th>");
        writer.print("<th># Total Time<br/>(hh:mm:ss)</th>");
        writer.println("</tr>");

        int testIndex = 0;
        Date testSuiteStart = Date.from(Instant.now());
        Date testSuiteEnd = Date.from(Instant.now());

        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
                int totalTests = passedTests + failedTests;


                Date startTime = testResult.getTestStartTime();
                if (testSuiteStart.after(startTime)) {
                    testSuiteStart = startTime;
                }
                Date endTime = testResult.getTestEndTime();
                if (testSuiteEnd.before(endTime)) {
                    testSuiteEnd = endTime;
                }
                long duration = testResult.getDuration();

                if (passedTests + skippedTests + failedTests > 0) {
                    writer.print("<tr");
                    if ((testIndex % 2) == 1) {
                        writer.print(" class=\"stripe\"");
                    }
                    writer.print(">");

                    buffer.setLength(0);
                    writeTableData(buffer.append("<a href=\"#t").append(testIndex)
                            .append("\">")
                            .append(Utils.escapeHtml(testResult.getTestName()))
                            .append("</a>").toString());
                    writeTableData(integerFormat.format(totalTests), "num");
                    writeTableData(integerFormat.format(passedTests), "num");
                    writeTableData(integerFormat.format(skippedTests),
                            (skippedTests > 0 ? "num attn" : "num"));
                    writeTableData(integerFormat.format(failedTests),
                            (failedTests > 0 ? "num attn" : "num"));
                    writeTableData(BaseClass.Browser, "num");
                    writeTableData(dateFormat.format(startTime), "num");
                    writeTableData(dateFormat.format(endTime), "num");
                    writeTableData(convertTimeToString(duration), "num");
                    writer.println("</tr>");
                }
                totalTestsCount += totalTests;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                testIndex++;
            }
        }


        // Print totals if there was more than one test suite


        writer.print("<tr>");
        writer.print("<th>Total</th>");
        writeTableHeader(integerFormat.format(totalTestsCount), "num");
        writeTableHeader(integerFormat.format(totalPassedTests), "num");
        writeTableHeader(integerFormat.format(totalSkippedTests),
                (totalSkippedTests > 0 ? "num attn" : "num"));
        writeTableHeader(integerFormat.format(totalFailedTests),
                (totalFailedTests > 0 ? "num attn" : "num"));
        writeTableHeader(BaseClass.Browser, "num");
        writeTableHeader(dateFormat.format(testSuiteStart), "num");
        writeTableHeader(dateFormat.format(testSuiteEnd), "num");
        writeTableHeader(convertTimeToString(testSuiteEnd.getTime() - testSuiteStart.getTime()), "num");
        writer.print("<th colspan=\"9\"></th>");
        writer.println("</tr>");

        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
        writer.print("<div class=\"easy-test-summary\">");
        writer.print("<table class=\"table-bordered\" id='summary'>");
        writer.print("<thead>");
        writer.print("<tr>");
        writer.print("<th>Scenario</th>");
        writer.print("<th>Test case ID</th>");
        writer.print("<th>Business Unit</th>");
        writer.print("<th>Short Exception</th>");
        writer.print("<th>Screenshot</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("<th>Automation Time (hh:mm:ss)</th>");
        writer.print("<th>Manual Estimate Time <br/>(Minutes)</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                if (testResult.getPassedTestCount() + testResult.getFailedTestCount() + testResult.getSkippedTestCount() > 0) {
                    writer.printf("<tbody id=\"t%d\">", testIndex);

                    String testName = Utils.escapeHtml(testResult.getTestName());

                    int startIndex = scenarioIndex;

                    scenarioIndex += writeScenarioSummary(testName
                                    + " &#8212; failed (configuration methods)",
                            testResult.getFailedConfigurationResults(), "failed",
                            scenarioIndex);
                    scenarioIndex += writeScenarioSummary(testName
                                    + " &#8212; failed", testResult.getFailedTestResults(),
                            "failed", scenarioIndex);
                    scenarioIndex += writeScenarioSummary(testName
                                    + " &#8212; skipped (configuration methods)",
                            testResult.getSkippedConfigurationResults(), "skipped",
                            scenarioIndex);
                    scenarioIndex += writeScenarioSummary(testName
                                    + " &#8212; skipped",
                            testResult.getSkippedTestResults(), "skipped",
                            scenarioIndex);
                    scenarioIndex += writeScenarioSummary(testName
                                    + " &#8212; passed", testResult.getPassedTestResults(),
                            "passed", scenarioIndex);

                    if (scenarioIndex == startIndex) {
                        writer.print("<tr><th colspan=\"4\" class=\"invisible\"/></tr>");
                    }

                    writer.println("</tbody>");
                }
                testIndex++;
            }
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description,
                                     List<ClassResult> classResults, String cssClassPrefix,
                                     int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            writer.print("<tr><th colspan=\"8\">");
            writer.print(description);
            writer.print("</th></tr>");

            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            for (ClassResult classResult : classResults) {
                String cssClass = cssClassPrefix
                        + ((classIndex % 2) == 0 ? "even" : "odd");

                buffer.setLength(0);

                int scenariosPerClass = 0;
                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    // Write the scenarios for the method
                    for (int i = 0; i < resultsCount; i++) {
                        ITestResult result = results.get(i);
                        Method method = result.getMethod().getConstructorOrMethod().getMethod();
                        String testCaseId = baseclass.getTestCaseIdValue(method);

                        long start = result.getStartMillis();
                        long end = result.getEndMillis();
                        long totalTime = end - start;

                        int estimate = 0;
                        if (!testCaseId.isEmpty()) {
                            try {
                                estimate = baseclass.getTestCaseData(testCaseId, "estimate");
                            } catch (Exception e) {
                                LOG.error(e.getMessage());
                            }
                        }

                        String methodName = Utils.escapeHtml(result.getName());
                        String BusinessUnit = "";
                        if (result.getParameters().length > 0) {
                            methodName = Utils.escapeHtml(result.getParameters()[0].toString());
                            try {
                                BusinessUnit = Utils.escapeHtml(result.getParameters()[1].toString());
                            } catch (IndexOutOfBoundsException exception) {
                                LOG.info("Result only had one parameter");
                            }
                        }


                        String shortException = "";
                        String failureScreenShot = "";

                        Throwable exception = result.getThrowable();
                        boolean hasThrowable = exception != null;
                        if (hasThrowable) {
                            String str = Utils.shortStackTrace(exception, true);
                            Scanner scanner = new Scanner(str);
                            shortException = scanner.nextLine();
                            scanner.close();
                            List<String> msgs = Reporter.getOutput(result);
                            boolean hasReporterOutput = msgs.size() > 0;
                            if (hasReporterOutput) {
                                for (String info : msgs) {
                                    failureScreenShot += info + "<br/>";
                                }
                            }
                        }


                        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(start);

                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(end);


                        // Write the timing information with the first scenario per
                        // method
                        buffer.append("<tr class=\"").append(cssClass)
                                .append("\">");
                        buffer.append("<td><a href=\"#m").append(scenarioIndex)
                                .append("\">").append(methodName)
                                .append("</a></td>").append("<td rowspan=\"")
                                .append("\">").append(BusinessUnit)
                                .append("</td>").append("<td rowspan=\"")
                                .append("\">").append(shortException)
                                .append("</td>").append("<td rowspan=\"")
                                .append("\">").append(failureScreenShot)
                                .append("</td>").append("<td rowspan=\"")
                                .append("\">").append(formatter.format(startTime.getTime()))
                                .append("</td>").append("<td rowspan=\"")
                                .append("\">").append(formatter.format(endTime.getTime()))
                                .append("</td>").append("<td rowspan=\"")
                                .append("\">").append(convertTimeToString(totalTime)).append("</td>")
                                .append("<td rowspan=\"")
                                .append("\">").append(estimate).append("</td></tr>");
                        scenarioIndex++;
                    }

                    scenariosPerClass += resultsCount;
                }

                // Write the test results for the class
                String className = Utils.escapeHtml(classResult.getClassName());
                className = className.substring(className.lastIndexOf('.') + 1);
                writer.print("<tr class=\"");
                writer.print(cssClass);
                writer.print("\">");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass + 1);
                writer.print("\">");
                writer.print(className);
                writer.print("</td>");
                writer.print(buffer);

                classIndex++;
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes the details for all test scenarios.
     */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                if (testResult.getPassedTestCount() + testResult.getFailedTestCount() + testResult.getSkippedTestCount() > 0) {
                    writer.print("<h2>");
                    writer.print(Utils.escapeHtml(testResult.getTestName()));
                    writer.print("</h2>");

                    scenarioIndex += writeScenarioDetails(
                            testResult.getFailedConfigurationResults(),
                            scenarioIndex);
                    scenarioIndex += writeScenarioDetails(
                            testResult.getFailedTestResults(), scenarioIndex);
                    scenarioIndex += writeScenarioDetails(
                            testResult.getSkippedConfigurationResults(),
                            scenarioIndex);
                    scenarioIndex += writeScenarioDetails(
                            testResult.getSkippedTestResults(), scenarioIndex);
                    scenarioIndex += writeScenarioDetails(
                            testResult.getPassedTestResults(), scenarioIndex);
                }
            }
        }
    }

    /**
     * Writes the scenario details for the results of a given state for a single
     * test.
     */
    private int writeScenarioDetails(List<ClassResult> classResults,
                                     int startingScenarioIndex) {
        int scenarioIndex = startingScenarioIndex;
        for (ClassResult classResult : classResults) {
            String className = classResult.getClassName();
            for (MethodResult methodResult : classResult.getMethodResults()) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();


                for (ITestResult result : results) {
                    String testCasename = result.getName();
                    if (result.getParameters().length > 0)
                        testCasename = result.getParameters()[0].toString();
                    String label = Utils
                            .escapeHtml(className
                                    + "#"
                                    + testCasename);
                    writeScenario(scenarioIndex, label, result);
                    scenarioIndex++;
                }
            }
        }

        return scenarioIndex - startingScenarioIndex;
    }

    /**
     * Writes the details for an individual test scenario.
     */
    private void writeScenario(int scenarioIndex, String label,
                               ITestResult result) {
        writer.print("<h3 id=\"m");
        writer.print(scenarioIndex);
        writer.print("\">");
        writer.print(label);
        writer.print("</h3>");

        writer.print("<table class=\"table-bordered result\">");

        boolean hasRows = false;

        // Write test parameters (if any)
        Object[] parameters = result.getParameters();
        int parameterCount = (parameters == null ? 0 : parameters.length);
        if (parameterCount > 0) {
            writer.print("<tr class=\"param\">");
            for (int i = 1; i <= parameterCount; i++) {
                writer.print("<th>Parameter #");
                writer.print(i);
                writer.print("</th>");
            }
            writer.print("</tr><tr class=\"param stripe\">");
            for (Object parameter : parameters) {
                writer.print("<td>");
                writer.print(Utils.escapeHtml(Utils.toString(parameter)));
                writer.print("</td>");
            }
            writer.print("</tr>");
            hasRows = true;
        }

        // Write reporter messages (if any)
        List<String> reporterMessages = Reporter.getOutput(result);
        if (!reporterMessages.isEmpty()) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">Messages</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeReporterMessages(reporterMessages);
            writer.print("</td></tr>");
            hasRows = true;
        }

        // Write exception (if any)
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writer.print((result.getStatus() == ITestResult.SUCCESS ? "Expected Exception"
                    : "Exception"));
            writer.print("</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeStackTrace(throwable);
            writer.print("</td></tr>");
            hasRows = true;
        }

        if (!hasRows) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(" class=\"invisible\"/></tr>");
        }

        writer.print("</table>");
        writer.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
            writer.print(Utils.escapeHtml(iterator.next()));
        } else {
            writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
                writer.print(Utils.escapeHtml(iterator.next()));
            } else {
                writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName()
                        .compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName()
                            .compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context
                    .getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests()
                    .getAllResults();
            Set<ITestResult> skippedConfigurations = context
                    .getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests()
                    .getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests()
                    .getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);

            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;

            duration = context.getEndDate().getTime() - context.getStartDate().getTime();

            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,
                                resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName,
                        resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }

        public Date getTestStartTime() {
            return testStartTime;
        }

        public Date getTestEndTime() {
            return testEndTime;
        }


        /**
         * @return the results for failed configurations (possibly empty)
         */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /**
         * @return the results for failed tests (possibly empty)
         */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /**
         * @return the results for skipped configurations (possibly empty)
         */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /**
         * @return the results for skipped tests (possibly empty)
         */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

        /**
         * @return the results for passed tests (possibly empty)
         */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }

        public int getTestCount() {
            return testCount;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className     the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }


    /* Convert long type milliseconds to format hh:mm:ss */
    public String convertTimeToString(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }
}