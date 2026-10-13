package com.test.automation.dataproviders;

import org.testng.annotations.DataProvider;

import com.test.automation.utils.ExcelUtils;

/**
 * Fontes de dados (DataProviders) dos testes de login.
 *
 * Ficam separadas das classes de teste para que qualquer teste possa reutilizá-las.
 * Uso na classe de teste: dataProvider = "loginExcel", dataProviderClass = LoginDataProvider.class
 *
 * Todas devolvem as mesmas 6 colunas, na mesma ordem:
 * testId | scenario | username | password | expectedResult | expectedMessage
 */
public class LoginDataProvider {

    private static final String EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata/LoginData.xlsx";
    private static final String SHEET_NAME = "Login";

    // Mensagens exibidas pelo Sauce Demo
    private static final String MSG_USERNAME_REQUIRED = "Epic sadface: Username is required";
    private static final String MSG_PASSWORD_REQUIRED = "Epic sadface: Password is required";
    private static final String MSG_INVALID_CREDENTIALS = "Epic sadface: Username and password do not match any user in this service";
    private static final String MSG_LOCKED_OUT = "Epic sadface: Sorry, this user has been locked out.";

    private LoginDataProvider() {
    }

    /** Dados lidos do Excel (LoginData.xlsx, aba "Login"). */
    @DataProvider(name = "loginExcel")
    public static Object[][] loginExcel() {
        return ExcelUtils.getSheetData(EXCEL_PATH, SHEET_NAME);
    }

    /** Mesmos cenários, mas fixos no código. Útil para testar sem depender do arquivo. */
    @DataProvider(name = "loginFixo")
    public static Object[][] loginFixo() {
        return new Object[][] {
                { "CT01", "Login com usuário padrão", "standard_user", "secret_sauce", "SUCCESS", "" },
                { "CT02", "Login com problem_user", "problem_user", "secret_sauce", "SUCCESS", "" },
                { "CT03", "Login com performance_glitch_user", "performance_glitch_user", "secret_sauce", "SUCCESS", "" },
                { "CT04", "Login com error_user", "error_user", "secret_sauce", "SUCCESS", "" },
                { "CT05", "Login com visual_user", "visual_user", "secret_sauce", "SUCCESS", "" },
                { "CT06", "Login com usuário bloqueado", "locked_out_user", "secret_sauce", "ERROR", MSG_LOCKED_OUT },
                { "CT07", "Login com usuário e senha em branco", "", "", "ERROR", MSG_USERNAME_REQUIRED },
                { "CT08", "Login informando apenas a senha", "", "secret_sauce", "ERROR", MSG_USERNAME_REQUIRED },
                { "CT09", "Login informando apenas o usuário", "standard_user", "", "ERROR", MSG_PASSWORD_REQUIRED },
                { "CT10", "Login com senha incorreta", "standard_user", "senha_errada", "ERROR", MSG_INVALID_CREDENTIALS },
                { "CT11", "Login com usuário inexistente", "user1", "pass1", "ERROR", MSG_INVALID_CREDENTIALS },
                { "CT12", "Login com usuário em maiúsculas", "STANDARD_USER", "secret_sauce", "ERROR", MSG_INVALID_CREDENTIALS },
        };
    }
}
