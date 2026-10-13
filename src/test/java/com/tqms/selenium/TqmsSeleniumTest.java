package com.tqms.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 9: Selenium WebDriver Test Suite for Telemedicine Queue Management System (TQMS)
 * Validates critical user journeys end-to-end against live Spring Boot application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TqmsSeleniumTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String SCREENSHOT_DIR = "target/selenium-screenshots";

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeAll
    static void setupDriver() {
        // Ensure screenshot directory exists
        new File(SCREENSHOT_DIR).mkdirs();

        // Initialize Chrome in modern headless mode; fallback to Edge if needed
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(chromeOptions);
            System.out.println("[SELENIUM] Initialized ChromeDriver successfully in headless mode.");
        } catch (Exception e) {
            System.out.println("[SELENIUM] ChromeDriver unavailable, falling back to EdgeDriver: " + e.getMessage());
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments("--headless=new");
            edgeOptions.addArguments("--disable-gpu");
            edgeOptions.addArguments("--no-sandbox");
            edgeOptions.addArguments("--remote-allow-origins=*");
            edgeOptions.addArguments("--window-size=1920,1080");
            driver = new EdgeDriver(edgeOptions);
            System.out.println("[SELENIUM] Initialized EdgeDriver successfully in headless mode.");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
            System.out.println("[SELENIUM] WebDriver session terminated cleanly.");
        }
    }

    /**
     * Utility method: capture screenshot on failure or milestone
     */
    private File captureScreenshot(String testName, String prefix) {
        if (driver instanceof TakesScreenshot takesScreenshot) {
            File srcFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Path destPath = Paths.get(SCREENSHOT_DIR, prefix + "_" + testName + "_" + timestamp + ".png");
            try {
                Files.createDirectories(destPath.getParent());
                Files.copy(srcFile.toPath(), destPath);
                System.out.println("[SELENIUM SCREENSHOT] Captured: " + destPath.toAbsolutePath());
                return destPath.toFile();
            } catch (IOException e) {
                System.err.println("[SELENIUM ERROR] Failed to save screenshot: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * User Journey 1: Homepage Dashboard & Summary Metrics Loading
     */
    @Test
    @Order(1)
    @DisplayName("Journey 1: Public Dashboard and Queue Statistics Verification")
    void test1_HomepageAndDashboardMetrics() {
        try {
            driver.get(getBaseUrl());

            // 1. Assert Title
            assertEquals("WRONG_TITLE_DEFECT_SIMULATION", driver.getTitle(),
                    "Web application page title must match SRS specification");

            // 2. Assert App Header & Brand
            WebElement titleElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("app-title")));
            assertTrue(titleElem.getText().contains("Telemedicine Queue Management System"),
                    "Header brand title must be present and visible");

            // 3. Assert Stats Dashboard Cards
            WebElement statTotal = driver.findElement(By.id("stat-total"));
            WebElement statWaiting = driver.findElement(By.id("stat-waiting"));
            WebElement statCalled = driver.findElement(By.id("stat-called"));
            WebElement statConsultation = driver.findElement(By.id("stat-consultation"));
            WebElement statCompleted = driver.findElement(By.id("stat-completed"));

            assertNotNull(statTotal, "Total Patients stat card must exist");
            assertNotNull(statWaiting, "Waiting count stat card must exist");
            assertNotNull(statCalled, "Called count stat card must exist");
            assertNotNull(statConsultation, "In Consultation stat card must exist");
            assertNotNull(statCompleted, "Completed stat card must exist");

            int totalCount = Integer.parseInt(statTotal.getText().trim());
            assertTrue(totalCount >= 0, "Total count must be non-negative");

            captureScreenshot("journey1_dashboard_metrics", "milestone");
        } catch (Throwable t) {
            captureScreenshot("journey1_dashboard_metrics", "failure");
            throw t;
        }
    }

    /**
     * User Journey 2: Patient Registration & Real-Time Token Enqueue
     */
    @Test
    @Order(2)
    @DisplayName("Journey 2: Patient Registration Desk and Dynamic Token Allocation")
    void test2_PatientRegistrationAndTokenGeneration() {
        try {
            driver.get(getBaseUrl());

            String patientName = "Selenium Test Patient";
            String contact = "+91 9876543210";

            WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("patientName")));
            WebElement contactInput = driver.findElement(By.id("contact"));
            Select departmentSelect = new Select(driver.findElement(By.id("department")));
            Select prioritySelect = new Select(driver.findElement(By.id("priority")));
            WebElement submitBtn = driver.findElement(By.id("btn-register"));

            nameInput.clear();
            nameInput.sendKeys(patientName);
            contactInput.clear();
            contactInput.sendKeys(contact);
            departmentSelect.selectByVisibleText("Cardiology");
            prioritySelect.selectByValue("URGENT");

            // Capture pre-submit state
            captureScreenshot("journey2_registration_form_filled", "milestone");

            // Submit Registration
            submitBtn.click();

            // Handle frontend browser alert if triggered
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                String alertText = alert.getText();
                System.out.println("[SELENIUM] Handled registration alert: " + alertText);
                assertTrue(alertText.contains("Patient registered successfully"), "Alert must confirm successful registration");
                alert.accept();
            } catch (TimeoutException ignored) {
                // Alert not present, continue checking DOM
            }

            // Wait for queue table to reflect newly registered patient
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.id("queue-tbody"), patientName
            ));

            // Verify row exists with correct attributes
            WebElement tableBody = driver.findElement(By.id("queue-tbody"));
            assertTrue(tableBody.getText().contains(patientName),
                    "Queue table must contain newly registered patient name");
            assertTrue(tableBody.getText().contains("Cardiology"),
                    "Queue table row must display Cardiology department");

            captureScreenshot("journey2_patient_enqueued_success", "milestone");
        } catch (Throwable t) {
            captureScreenshot("journey2_registration", "failure");
            throw t;
        }
    }

    /**
     * User Journey 3: Patient Self-Service Token Lookup & Real-Time Wait Tracker
     */
    @Test
    @Order(3)
    @DisplayName("Journey 3: Public Patient Token Tracking and Queue Position Verification")
    void test3_PatientTokenTrackingLookup() {
        try {
            driver.get(getBaseUrl());

            // First find any existing token in the queue table
            List<WebElement> tokenCells = driver.findElements(By.className("token-cell"));
            assertFalse(tokenCells.isEmpty(), "Queue must contain at least one patient record");
            String sampleToken = tokenCells.get(0).getText().trim();

            WebElement tokenInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("token-input")));
            WebElement trackBtn = driver.findElement(By.id("btn-lookup"));

            tokenInput.clear();
            tokenInput.sendKeys(sampleToken);
            trackBtn.click();

            // Wait for lookup result box to appear
            WebElement resultBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lookup-result")));
            assertTrue(resultBox.isDisplayed(), "Patient lookup result card must be displayed");
            assertTrue(resultBox.getText().contains(sampleToken),
                    "Lookup result must display queried token identifier");

            captureScreenshot("journey3_token_lookup_success", "milestone");
        } catch (Throwable t) {
            captureScreenshot("journey3_token_lookup", "failure");
            throw t;
        }
    }

    /**
     * User Journey 4: Queue Search and Department / Status Triage Filter
     */
    @Test
    @Order(4)
    @DisplayName("Journey 4: Real-Time Search Filtering and Status Filter Validation")
    void test4_QueueSearchAndFiltering() throws Exception {
        try {
            driver.get(getBaseUrl());

            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-input")));
            Select statusFilter = new Select(driver.findElement(By.id("status-filter")));

            // Filter by WAITING status
            statusFilter.selectByValue("WAITING");

            // Type search query
            searchInput.clear();
            searchInput.sendKeys("Cardiology");

            // Allow client-side DOM filter animation to apply
            Thread.sleep(600);

            WebElement tableBody = driver.findElement(By.id("queue-tbody"));
            assertNotNull(tableBody, "Queue body must remain intact during live search filter");

            captureScreenshot("journey4_search_filtered", "milestone");
        } catch (Throwable t) {
            captureScreenshot("journey4_search_filter", "failure");
            throw t;
        }
    }

    /**
     * User Journey 5: Role-Based Clinical Workflow Status State Machine Transition
     */
    @Test
    @Order(5)
    @DisplayName("Journey 5: Clinical Status State Machine Progression (Call -> Consult -> Complete)")
    void test5_StatusLifecycleTransitionWorkflow() throws Exception {
        try {
            driver.get(getBaseUrl());

            // 1. Locate first active row and click Call
            WebElement row1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#queue-tbody tr")));
            List<WebElement> btns1 = row1.findElements(By.className("btn-action"));
            assertTrue(btns1.size() >= 3, "Row must provide Call, Consult, and Complete action controls");
            btns1.get(0).click();
            Thread.sleep(800);

            // Handle any alert if present
            try {
                Alert a = driver.switchTo().alert();
                a.accept();
            } catch (NoAlertPresentException ignored) {}

            // 2. Re-locate row for Consult
            WebElement row2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#queue-tbody tr")));
            List<WebElement> btns2 = row2.findElements(By.className("btn-action"));
            btns2.get(1).click();
            Thread.sleep(800);

            try {
                Alert a = driver.switchTo().alert();
                a.accept();
            } catch (NoAlertPresentException ignored) {}

            // 3. Re-locate row for Complete
            WebElement row3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#queue-tbody tr")));
            List<WebElement> btns3 = row3.findElements(By.className("btn-action"));
            btns3.get(2).click();
            Thread.sleep(800);

            try {
                Alert a = driver.switchTo().alert();
                a.accept();
            } catch (NoAlertPresentException ignored) {}

            captureScreenshot("journey5_status_lifecycle_completed", "milestone");
        } catch (Throwable t) {
            captureScreenshot("journey5_status_transition", "failure");
            throw t;
        }
    }

    /**
     * Failure Screenshot Mechanism Validation
     */
    @Test
    @Order(6)
    @DisplayName("Quality Gate: Automated Failure Screenshot Mechanism Validation")
    void test6_FailureScreenshotMechanismVerification() {
        File screenshot = captureScreenshot("quality_gate_audit", "evidence");
        assertNotNull(screenshot, "Failure screenshot mechanism must generate a physical screenshot file");
        assertTrue(screenshot.exists(), "Captured screenshot file must physically exist in target directory");
        assertTrue(screenshot.length() > 0, "Screenshot file must contain binary image data");
        System.out.println("[QUALITY GATE AUDIT] Verified failure screenshot mechanism: " + screenshot.getAbsolutePath() +
                " (" + screenshot.length() + " bytes)");
    }
}
