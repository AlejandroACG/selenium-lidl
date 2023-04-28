import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LidlTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Actions actions;

    @BeforeAll
    public static void setUp() {
        // Cargamos el driver, arrancamos el navegador, maximizamos la ventana y aceptamos las cookies.
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);

        driver.manage().window().maximize();
        driver.get("https://www.lidl.es/");
        driver.findElement(By.className("cookie-alert-extended-button")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @AfterEach
    public void reset() {
        driver.get("https://www.lidl.es/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

    @Test
    public void test1() {
        // Navegamos hasta la categoría "Cubertería".
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.className("pt21-category-ribbon-body"), By.linkText("Cocina")));
        driver.findElement(By.className("pt21-category-ribbon-body")).findElement(By.linkText("Cocina")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cubertería")));
        driver.findElement(By.linkText("Cubertería")).click();

        // Comprobamos que carga los 9 resultados que muestra por pantalla.
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("product-grid-box-tile")));
        Assertions.assertEquals(9, driver.findElements(By.className("product-grid-box-tile")).size());
    }

    @Test
    public void test2() {
        // Localizamos y empleamos la barra de búsqueda.
        driver.findElement(By.id("mainsearch-input")).sendKeys("Muebles");
        driver.findElement(By.className("search-bar-container-button")).click();

        // Comprobamos el título del resultado.
        Assertions.assertEquals("Resultado de búsqueda | Lidl", driver.getTitle());
    }

    @Test
    public void test3() {
        // Usamos la barra de búsqueda para buscar productos.
        driver.findElement(By.id("mainsearch-input")).sendKeys("gaming");
        driver.findElement(By.className("search-bar-container-button")).click();

        // Añadimos un producto al azar al carrito.
        wait.until(ExpectedConditions.elementToBeClickable(By.className("frontpage-product-teaser__addtocart")));
        driver.findElement(By.className("frontpage-product-teaser__addtocart")).click();

        // Imagino que aquí lo suyo es testear si el producto se ha añadido correctamente al carrito.
        // La manera más fácil sería obtener el dato del numerito pequeño que aparece sobre el carrito:
        int numberOnCartIcon = Integer.parseInt(driver.findElement(By.className("ua-basket")).findElement(By.className("status-area")).getText());
        Assertions.assertEquals(1, numberOnCartIcon);

        // Pero he preferido escribir también un método alternativo más complicado pero, en mi opinión, quizás más fiable.
        // La siguiente parte del test abre la página del carrito y suma la cantidad de productos que hay en él.
        // El assert comprueba que hay 1 producto en el carrito (la cantidad que hemos añadido antes),
        // pero con este código podríamos comprobar también otras cantidades solo cambiando el valor expected del assert.

        wait.until(ExpectedConditions.elementToBeClickable(By.className("overlay-closer")));
        driver.findElement(By.className("overlay-closer")).click();
        int totalAmount = 0;
        List<WebElement> selectElements = driver.findElements(By.cssSelector("select.submit-on-change"));
        List<WebElement> selectedOptions = new ArrayList<>();

        for (WebElement selectElement : selectElements) {
            List<WebElement> options = selectElement.findElements(By.tagName("option"));
            for (WebElement option : options) {
                if (option.isSelected()) {
                    selectedOptions.add(option);
                }
            }
        }

        for (WebElement selectedOption : selectedOptions) {
            int amount = Integer.parseInt(selectedOption.getText());
            totalAmount += amount;
        }

        Assertions.assertEquals(1, totalAmount);
    }
}