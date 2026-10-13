package com.test.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Leitura de dados de teste a partir de arquivos Excel (.xlsx) usando Apache POI.
 */
public class ExcelUtils {

    // DataFormatter devolve o valor da célula exatamente como aparece no Excel
    // (ex.: 123 continua "123", e não "123.0" como acontece com getNumericCellValue).
    private static final DataFormatter FORMATTER = new DataFormatter();

    // Classe utilitária: só tem métodos estáticos, não precisa ser instanciada
    private ExcelUtils() {
    }

    /**
     * Lê uma aba inteira e devolve no formato que o @DataProvider do TestNG espera.
     *
     * - A linha 0 (cabeçalho) é ignorada.
     * - Cada linha seguinte vira um "caso de teste" (uma execução do @Test).
     * - Cada coluna vira um parâmetro do método de teste, na mesma ordem.
     * - Célula vazia vira "" (string vazia), o que permite testar campos em branco.
     * - Linhas totalmente vazias são ignoradas.
     *
     * O arquivo é aberto e fechado aqui dentro (try-with-resources), então quem chama
     * não precisa se preocupar em fechar nada.
     */
    public static Object[][] getSheetData(String filePath, String sheetName) {
        Log.info("Lendo dados de teste do Excel: " + filePath + " (aba: " + sheetName + ")");

        try (FileInputStream file = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Aba '" + sheetName + "' não encontrada no arquivo " + filePath);
            }

            int columnCount = sheet.getRow(0).getLastCellNum(); // quantidade de colunas definida pelo cabeçalho
            List<Object[]> data = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row, columnCount)) {
                    continue;
                }

                Object[] values = new Object[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    values[j] = getCellValue(row.getCell(j));
                }
                data.add(values);
            }

            Log.info(data.size() + " linha(s) de dados carregada(s) da aba " + sheetName + ".");
            return data.toArray(new Object[0][]);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo Excel: " + filePath, e);
        }
    }

    // Célula inexistente (null) vira string vazia em vez de causar NullPointerException
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return FORMATTER.formatCellValue(cell);
    }

    private static boolean isRowEmpty(Row row, int columnCount) {
        if (row == null) {
            return true;
        }
        for (int j = 0; j < columnCount; j++) {
            if (!getCellValue(row.getCell(j)).isBlank()) {
                return false;
            }
        }
        return true;
    }
}
