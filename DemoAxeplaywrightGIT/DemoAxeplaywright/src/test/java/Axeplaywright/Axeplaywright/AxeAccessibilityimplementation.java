package Axeplaywright.Axeplaywright;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.deque.html.axecore.playwright.*;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.Rule;
import com.microsoft.playwright.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AxeAccessibilityimplementation {
    private static ExtentReports extent;
    private static ExtentTest test;

    static {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("AxeAccessibilityReport.html");
        sparkReporter.config().setReportName("Accessibility Report");
        sparkReporter.config().setDocumentTitle("Axe Accessibility Report");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    protected String GetCurrentDateTime() {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        return date.format(LocalDateTime.now());
    }

    @Test
    public void shouldNotHaveAutomaticallyDetectableAccessibilityIssues() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        String selectorToExcludeUsername = "div[class='orangehrm-login-error'] p:nth-child(1)";
        String selectorToExcludePass = "div[class='orangehrm-login-form'] p:nth-child(2)";
        
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");  
        page.waitForTimeout(5000);
       // page.locator("section").filter(new Locator.FilterOptions().setHasText("Personal AccountMyBiz Account")).locator("span").first().click();
        page.waitForTimeout(5000);
        
		List<String> wcagTags = Arrays.asList("wcag2a", "wcag2aa");
		AxeResults accessibilityScanResults = new AxeBuilder(page).withTags(wcagTags).exclude(selectorToExcludeUsername)
				.exclude(selectorToExcludePass).analyze();

		List<Rule> violations = accessibilityScanResults.getViolations();
        test = extent.createTest("Accessibility Test - I Accept page");
        System.out.println("Total Violations Found: " + violations.size());
        if (violations.isEmpty()) {
            test.fail("No accessibility violations found.");
        } else {
            test.pass("Accessibility violations found.").info("<div>" + generateViolationsTable(violations) + "</div>");
            for (Rule violation : violations) {
                System.out.println("\nViolation: " + violation.getDescription());
                System.out.println("WCAG Tags: " + String.join(", ", violation.getTags()));
                System.out.println("Rule ID: " + violation.getId());
                System.out.println("Help URL: " + violation.getHelpUrl());
                System.out.println("Impact: " + violation.getImpact());

                test.info("Violation: " + violation.getDescription());
                test.info("WCAG Tags: " + String.join(", ", violation.getTags()));
                test.info("Rule ID: " + violation.getId());
                test.info("Help URL: <a href='" + violation.getHelpUrl() + "'>" + violation.getHelpUrl() + "</a>");
                test.info("Impact: " + violation.getImpact());

                StringBuilder failedElements = new StringBuilder();
                violation.getNodes().forEach(node -> {
                    @SuppressWarnings("unchecked")
                    List<String> targets = (List<String>) node.getTarget();

                    if (!targets.isEmpty()) {
                        String selector = targets.get(0);
                        // Highlight the element on the page
                        page.locator(selector).evaluate("element => element.style.border = '3px solid red'");
                        failedElements.append(node.getHtml()).append("<br>");
                        
                        System.out.println("Element: " + node.getHtml());
                    }
                });
                test.info("<b>Failed Elements:</b><br>" + failedElements.toString());
            }
        captureScreenshot(page);
        
        
        
        
		/*
		 * page.waitForTimeout(2000); page.locator("css=img[alt='Log In']").click();
		 * page.locator("#accountNo").click(); //------> Access Info Page Accessibility
		 * Issues AxeResults accessibilityScanResults1 = new AxeBuilder(page)
		 * .withTags(wcagTags) .analyze();
		 * 
		 * List<Rule> violations1 = accessibilityScanResults1.getViolations(); test =
		 * extent.createTest("Accessibility Test - I Accept page");
		 * System.out.println("Total Violations Found: " + violations1.size()); if
		 * (violations1.isEmpty()) { test.fail("No accessibility violations found."); }
		 * else { test.pass("Accessibility violations found.").info("<div>" +
		 * generateViolationsTable1(violations1) + "</div>"); for (Rule violation :
		 * violations1) { System.out.println("\nViolation: " +
		 * violation.getDescription()); System.out.println("WCAG Tags: " +
		 * String.join(", ", violation.getTags())); System.out.println("Rule ID: " +
		 * violation.getId()); System.out.println("Help URL: " +
		 * violation.getHelpUrl()); System.out.println("Impact: " +
		 * violation.getImpact());
		 * 
		 * test.info("Violation: " + violation.getDescription());
		 * test.info("WCAG Tags: " + String.join(", ", violation.getTags()));
		 * test.info("Rule ID: " + violation.getId()); test.info("Help URL: <a href='" +
		 * violation.getHelpUrl() + "'>" + violation.getHelpUrl() + "</a>");
		 * test.info("Impact: " + violation.getImpact());
		 * 
		 * StringBuilder failedElements = new StringBuilder();
		 * violation.getNodes().forEach(node -> {
		 * 
		 * @SuppressWarnings("unchecked") List<String> targets = (List<String>)
		 * node.getTarget();
		 * 
		 * if (!targets.isEmpty()) { String selector = targets.get(0); // Highlight the
		 * element on the page page.locator(selector).
		 * evaluate("element => element.style.border = '3px solid red'");
		 * failedElements.append(node.getHtml()).append("<br>");
		 * 
		 * System.out.println("Element: " + node.getHtml()); } });
		 * test.info("<b>Failed Elements:</b><br>" + failedElements.toString()); } }
		 * captureScreenshot(page);
		 * page.locator("xpath=//input[@id='new_smsuser_loginname']").fill("gayup"+
		 * time);
		 * page.locator("xpath=//input[@id='createLoginPassword']").fill("Password@1");
		 * page.locator("xpath=//input[@id='createLoginPasswordRetype']").fill(
		 * "Password@1");
		 * page.locator("xpath=//span[contains(text(),'Switch to')]").click();
		 * page.waitForTimeout(5000);
		 * page.locator("xpath=//input[@name='accesscode_accesscode']").fill(
		 * "HEAIRC-GRSSS-OSMIC-VARNA-FLASH-CANES"); //------> Account Info Page
		 * Accessibility Issue AxeResults accessibilityScanResults2 = new
		 * AxeBuilder(page) .withTags(wcagTags) .analyze();
		 * 
		 * List<Rule> violations2 = accessibilityScanResults2.getViolations(); test =
		 * extent.createTest("Accessibility Test - I Accept page");
		 * System.out.println("Total Violations Found: " + violations2.size()); if
		 * (violations2.isEmpty()) { test.fail("No accessibility violations found."); }
		 * else { test.pass("Accessibility violations found.").info("<div>" +
		 * generateViolationsTable2(violations2) + "</div>"); for (Rule violation :
		 * violations2) { System.out.println("\nViolation: " +
		 * violation.getDescription()); System.out.println("WCAG Tags: " +
		 * String.join(", ", violation.getTags())); System.out.println("Rule ID: " +
		 * violation.getId()); System.out.println("Help URL: " +
		 * violation.getHelpUrl()); System.out.println("Impact: " +
		 * violation.getImpact());
		 * 
		 * test.info("Violation: " + violation.getDescription());
		 * test.info("WCAG Tags: " + String.join(", ", violation.getTags()));
		 * test.info("Rule ID: " + violation.getId()); test.info("Help URL: <a href='" +
		 * violation.getHelpUrl() + "'>" + violation.getHelpUrl() + "</a>");
		 * test.info("Impact: " + violation.getImpact());
		 * 
		 * StringBuilder failedElements = new StringBuilder();
		 * violation.getNodes().forEach(node -> {
		 * 
		 * @SuppressWarnings("unchecked") List<String> targets = (List<String>)
		 * node.getTarget();
		 * 
		 * if (!targets.isEmpty()) { String selector = targets.get(0); // Highlight the
		 * element on the page page.locator(selector).
		 * evaluate("element => element.style.border = '3px solid red'");
		 * failedElements.append(node.getHtml()).append("<br>");
		 * 
		 * System.out.println("Element: " + node.getHtml()); } });
		 * test.info("<b>Failed Elements:</b><br>" + failedElements.toString()); } }
		 * captureScreenshot(page); page.waitForTimeout(5000);
		 * page.locator("#next").click();
		 * page.locator("#firstName").fill("Chandrashekhar");
		 * page.locator("#lastName").fill("MN"); page.locator("#emailAddy").fill(
		 * "chandrashekar.mellahalliningegowda@pearson.com\r\n");
		 * page.locator("#emailAddyConfirm").fill(
		 * "chandrashekar.mellahalliningegowda@pearson.com\r\n");
		 * page.locator("css=select[name=person_verifyquestionid]").selectOption("3");
		 * page.locator("#password3").fill("aaa"); page.locator("#next").click();
		 * page.waitForTimeout(5000); captureScreenshot(page);
		 * page.waitForTimeout(5000);
		 */
        browser.close();
        playwright.close();
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private String generateViolationsTable(List<Rule> violations) { 
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border='1' style='border-collapse:collapse;width:100%;'>")
            .append("<tr>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Violation</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>WCAG Tags</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Impact</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Rule ID</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Help URL</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Failed Elements</th>")
            .append("</tr>");

        for (Rule violation : violations) {
            String tags = violation.getTags().stream().collect(Collectors.joining(", "));
            tableBuilder.append("<tr>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation.getDescription())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(tags)).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation.getImpact())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation.getId())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'><a href='").append(escapeHtml(violation.getHelpUrl())).append("'>")
                .append(escapeHtml(violation.getHelpUrl())).append("</a></td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>");

            // Only adding the element without raw HTML formatting
            StringBuilder failedElements = new StringBuilder();
            violation.getNodes().forEach(node -> {
                String nodeHtml = node.getHtml();
                failedElements.append(escapeHtml(nodeHtml)).append("<br>");
            });

            tableBuilder.append(failedElements.toString())
                .append("</td>")
                .append("</tr>");
        }

        tableBuilder.append("</table>");
        return tableBuilder.toString();
    } 
    private String generateViolationsTable1(List<Rule> violations) { 
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border='1' style='border-collapse:collapse;width:100%;'>")
            .append("<tr>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Violation</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>WCAG Tags</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Impact</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Rule ID</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Help URL</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Failed Elements</th>")
            .append("</tr>");

        for (Rule violation1 : violations) {
            String tags = violation1.getTags().stream().collect(Collectors.joining(", "));
            tableBuilder.append("<tr>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation1.getDescription())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(tags)).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation1.getImpact())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation1.getId())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'><a href='").append(escapeHtml(violation1.getHelpUrl())).append("'>")
                .append(escapeHtml(violation1.getHelpUrl())).append("</a></td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>");

            // Only adding the element without raw HTML formatting
            StringBuilder failedElements = new StringBuilder();
            violation1.getNodes().forEach(node -> {
                String nodeHtml = node.getHtml();
                failedElements.append(escapeHtml(nodeHtml)).append("<br>");
            });

            tableBuilder.append(failedElements.toString())
                .append("</td>")
                .append("</tr>");
        }

        tableBuilder.append("</table>");
        return tableBuilder.toString();
    }
    private String generateViolationsTable2(List<Rule> violations) { 
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border='1' style='border-collapse:collapse;width:100%;'>")
            .append("<tr>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Violation</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>WCAG Tags</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Impact</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Rule ID</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Help URL</th>")
            .append("<th style='padding:5px;text-align:center;white-space:nowrap;'>Failed Elements</th>")
            .append("</tr>");

        for (Rule violation2 : violations) {
            String tags = violation2.getTags().stream().collect(Collectors.joining(", "));
            tableBuilder.append("<tr>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation2.getDescription())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(tags)).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation2.getImpact())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>").append(escapeHtml(violation2.getId())).append("</td>")
                .append("<td style='padding:5px;word-wrap:break-word;'><a href='").append(escapeHtml(violation2.getHelpUrl())).append("'>")
                .append(escapeHtml(violation2.getHelpUrl())).append("</a></td>")
                .append("<td style='padding:5px;word-wrap:break-word;'>");

            // Only adding the element without raw HTML formatting
            StringBuilder failedElements = new StringBuilder();
            violation2.getNodes().forEach(node -> {
                String nodeHtml = node.getHtml();
                failedElements.append(escapeHtml(nodeHtml)).append("<br>");
            });

            tableBuilder.append(failedElements.toString())
                .append("</td>")
                .append("</tr>");
        }
        tableBuilder.append("</table>");
        return tableBuilder.toString();
    }

    private String captureScreenshot(Page page) {
        // Capture a screenshot and add it to the report
        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

        // Generate a screenshot file name based on the test name and timestamp
        String screenshotFileName = "screenshots/" + "_" + GetCurrentDateTime() + ".png";
        try {
            // Save the screenshot
            java.nio.file.Files.write(java.nio.file.Paths.get(screenshotFileName), screenshotBytes);
            // Attach the screenshot to the ExtentReport
            test.addScreenCaptureFromPath(screenshotFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return screenshotFileName;
    }

    @AfterSuite
    public void tearDown() {
        extent.flush();
    }
}
